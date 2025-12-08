package com.calendar.service;

import com.calendar.model.Note;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.*;

/**
 * Сервис управления заметками.
 */
public class NoteService {
    private static final Logger logger = LogManager.getLogger(NoteService.class);
    private final Map<LocalDate, List<Note>> storage = new HashMap<>();

    /**
     * Добавить заметку.
     */
    public void addNote(LocalDate date, String content) {
        storage.computeIfAbsent(date, k -> new ArrayList<>()).add(new Note(content));
        logger.info("Добавлена заметка на {}: {}", date, content);
    }

    /**
     * Получить заметки на дату.
     */
    public List<Note> getNotesForDate(LocalDate date) {
        return storage.getOrDefault(date, Collections.emptyList());
    }

    /**
     * Удалить заметку по ID (используя Stream API).
     */
    public boolean deleteNoteById(UUID noteId) {
        for (Map.Entry<LocalDate, List<Note>> entry : storage.entrySet()) {
            List<Note> notes = entry.getValue();

            // Stream API: поиск заметки
            Optional<Note> target = notes.stream()
                    .filter(n -> n.getId().equals(noteId))
                    .findFirst();

            if (target.isPresent()) {
                notes.remove(target.get());
                // Удаляем пустой список, чтобы не хранить мусор
                if (notes.isEmpty()) {
                    storage.remove(entry.getKey());
                }
                logger.info("Удалена заметка ID: {}", noteId);
                return true;
            }
        }
        logger.warn("Заметка ID {} не найдена", noteId);
        return false;
    }

    /**
     * Очистить весь день.
     */
    public void clearDay(LocalDate date) {
        if (storage.containsKey(date)) {
            storage.remove(date);
            logger.info("Очищен день: {}", date);
        }
    }
}