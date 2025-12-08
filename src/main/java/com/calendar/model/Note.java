package com.calendar.model;

import java.util.UUID;

public class Note {
    private final UUID id;
    private final String content;

    public Note(String content) {
        this.id = UUID.randomUUID();
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content;
    }
}