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
 * Сервис проверки праздников.
 * Использует API isdayoff.ru для проверки статуса дня (рабочий/выходной)
 * и внутренний словарь для отображения названий официальных праздников РФ.
 */
public class HolidayService {
    private static final Logger logger = LogManager.getLogger(HolidayService.class);
    private final HttpClient httpClient;

    // Словарь официальных государственных праздников РФ (ст. 112 ТК РФ)
    private static final Map<MonthDay, String> HOLIDAYS_MAP = new HashMap<>();

    static {
        // --- ЯНВАРЬ ---
        // Новогодние каникулы: 1, 2, 3, 4, 5, 6 и 8 января
        HOLIDAYS_MAP.put(MonthDay.of(1, 1), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 2), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 3), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 4), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 5), "Новогодние каникулы");
        HOLIDAYS_MAP.put(MonthDay.of(1, 6), "Новогодние каникулы");

        // Рождество Христово: 7 января
        HOLIDAYS_MAP.put(MonthDay.of(1, 7), "Рождество Христово");

        HOLIDAYS_MAP.put(MonthDay.of(1, 8), "Новогодние каникулы");

        // --- ФЕВРАЛЬ ---
        // День защитника Отечества
        HOLIDAYS_MAP.put(MonthDay.of(2, 23), "День защитника Отечества");

        // --- МАРТ ---
        // Международный женский день
        HOLIDAYS_MAP.put(MonthDay.of(3, 8), "Международный женский день");

        // --- МАЙ ---
        // Праздник Весны и Труда
        HOLIDAYS_MAP.put(MonthDay.of(5, 1), "Праздник Весны и Труда");

        // День Победы
        HOLIDAYS_MAP.put(MonthDay.of(5, 9), "День Победы");

        // --- ИЮНЬ ---
        // День России
        HOLIDAYS_MAP.put(MonthDay.of(6, 12), "День России");

        // --- НОЯБРЬ ---
        // День народного единства
        HOLIDAYS_MAP.put(MonthDay.of(11, 4), "День народного единства");
    }

    public HolidayService() {
        // Используем стандартный HttpClient (встроен в Java 11+)
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Возвращает текстовое описание статуса дня.
     */
    public String getDayStatus(LocalDate date) {
        // 1. Запрос к API: "Нужно ли сегодня работать?"
        String apiStatus = checkApiStatus(date);

        // Обработка ошибок сети
        if (apiStatus.startsWith("Ошибка") || apiStatus.startsWith("Нет связи")) {
            return apiStatus;
        }

        // 2. Поиск названия праздника в нашем словаре
        String holidayName = getHolidayName(date);

        // 3. Формирование ответа
        switch (apiStatus) {
            case "1": // Выходной день (код API - 1)
                if (holidayName != null) {
                    return "ПРАЗДНИК: " + holidayName;
                } else {
                    return "Выходной день (Суббота/Воскресенье/Перенос)";
                }

            case "2": // Сокращенный день (код API - 2)
                return "Сокращенный рабочий день" + (holidayName != null ? " (" + holidayName + ")" : "");

            case "4": // Особые дни (COVID и т.д., редкость)
                return "Нерабочий день (Спец. указ)";

            case "0": // Рабочий день
                return "Рабочий день";

            default:
                return "Статус неизвестен";
        }
    }

    private String getHolidayName(LocalDate date) {
        // MonthDay игнорирует год (подходит для повторяющихся праздников)
        MonthDay currentMonthDay = MonthDay.from(date);
        return HOLIDAYS_MAP.get(currentMonthDay);
    }

    private String checkApiStatus(LocalDate date) {
        String dateStr = date.toString().replace("-", ""); // Формат YYYYMMDD
        String url = "https://isdayoff.ru/" + dateStr;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body(); // Вернет: 0, 1, 2 или 4
            } else {
                logger.warn("API isdayoff вернул код: {}", response.statusCode());
                return "Ошибка API";
            }
        } catch (Exception e) {
            logger.error("Ошибка при запросе к API", e);
            return "Нет связи с сервером";
        }
    }
}