package com.calendar.service;

import com.calendar.model.Note;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.*;

/**
 * Сервис управления заметками.
 * Отвечает за CRUD операции с данными в памяти приложения.
 */
public class NoteService {
    private static final Logger logger = LogManager.getLogger(NoteService.class);

    // Хранилище заметок: Дата -> Список заметок
    private final Map<LocalDate, List<Note>> storage = new HashMap<>();

    /**
     * Добавляет заметку. Создает список для новой даты при необходимости.
     * @param date дата заметки.
     * @param content текст заметки.
     */
    public void addNote(LocalDate date, String content) {
        storage.computeIfAbsent(date, k -> new ArrayList<>()).add(new Note(content));
        logger.info("Добавлена заметка на {}: {}", date, content);
    }

    /**
     * Возвращает список заметок.
     * @return список заметок или пустой список, если записей нет.
     */
    public List<Note> getNotesForDate(LocalDate date) {
        return storage.getOrDefault(date, Collections.emptyList());
    }

    /**
     * Удаляет заметку по UUID.
     * Проходит по всем дням и удаляет совпадение.
     *
     * @param noteId уникальный ID заметки.
     * @return true, если удаление прошло успешно.
     */
    public boolean deleteNoteById(UUID noteId) {
        for (Map.Entry<LocalDate, List<Note>> entry : storage.entrySet()) {
            List<Note> notes = entry.getValue();

            // Используем Stream API для поиска заметки
            Optional<Note> target = notes.stream()
                    .filter(n -> n.getId().equals(noteId))
                    .findFirst();

            if (target.isPresent()) {
                notes.remove(target.get());
                if (notes.isEmpty()) {
                    storage.remove(entry.getKey()); // Очистка памяти
                }
                logger.info("Удалена заметка ID: {}", noteId);
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет все заметки за выбранный день.
     */
    public void clearDay(LocalDate date) {
        if (storage.containsKey(date)) {
            storage.remove(date);
            logger.info("Очищен день: {}", date);
        }
    }
}