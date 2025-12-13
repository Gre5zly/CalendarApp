package com.calendar.model;

import java.util.UUID;

/**
 * Сущность "Заметка".
 * Хранит уникальный идентификатор (UUID) и текстовое содержимое.
 */
public class Note {
    private final UUID id;
    private final String content;

    public Note(String content) {
        this.id = UUID.randomUUID();
        this.content = content;
    }

    /** @return Уникальный идентификатор заметки. */
    public UUID getId() {
        return id;
    }

    /** @return Текст заметки. */
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content;
    }
}