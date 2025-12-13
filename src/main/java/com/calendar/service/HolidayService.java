package com.calendar.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для работы с календарем праздников.
 * Реализует гибридный алгоритм: запрос к API isdayoff.ru + локальный справочник праздников РФ.
 */
public class HolidayService {
    private static final Logger logger = LogManager.getLogger(HolidayService.class);
    private final HttpClient httpClient;

    // Словарь государственных праздников РФ (ст. 112 ТК РФ)
    private static final Map<MonthDay, String> HOLIDAYS_MAP = new HashMap<>();

    static {
        HOLIDAYS_MAP.put(MonthDay.of(1, 1), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 2), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 3), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 4), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 5), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 6), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 7), "Рождество Христово");
        HOLIDAYS_MAP.put(MonthDay.of(1, 8), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(2, 23), "День защитника Отечества");
        HOLIDAYS_MAP.put(MonthDay.of(3, 8), "Международный женский день");
        HOLIDAYS_MAP.put(MonthDay.of(5, 1), "Праздник Весны и Труда");
        HOLIDAYS_MAP.put(MonthDay.of(5, 9), "День Победы");
        HOLIDAYS_MAP.put(MonthDay.of(6, 12), "День России");
        HOLIDAYS_MAP.put(MonthDay.of(11, 4), "День народного единства");
    }

    public HolidayService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Определяет статус дня, комбинируя данные API и справочника.
     *
     * @param date проверяемая дата.
     * @return строка со статусом (например: "ПРАЗДНИК: День Победы" или "Рабочий день").
     */
    public String getDayStatus(LocalDate date) {
        String apiStatus = checkApiStatus(date);

        // Если сеть недоступна, возвращаем ошибку сразу
        if (apiStatus.startsWith("Ошибка") || apiStatus.startsWith("Нет связи")) {
            return apiStatus;
        }

        String holidayName = HOLIDAYS_MAP.get(MonthDay.from(date));

        switch (apiStatus) {
            case "1": // API: Выходной
                return holidayName != null ? "ПРАЗДНИК: " + holidayName : "Выходной день";
            case "2": // API: Сокращенный
                return "Сокращенный рабочий день" + (holidayName != null ? " (" + holidayName + ")" : "");
            case "0": // API: Рабочий
                return "Рабочий день";
            default:
                return "Статус неизвестен";
        }
    }

    /**
     * Выполняет GET-запрос к сервису isdayoff.ru.
     * @return Код ответа (0, 1, 2) или сообщение об ошибке.
     */
    private String checkApiStatus(LocalDate date) {
        String url = "https://isdayoff.ru/" + date.toString().replace("-", "");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            }
            logger.warn("API isdayoff вернул код: {}", response.statusCode());
            return "Ошибка API";
        } catch (Exception e) {
            logger.error("Ошибка соединения с API", e);
            return "Нет связи с сервером";
        }
    }
}