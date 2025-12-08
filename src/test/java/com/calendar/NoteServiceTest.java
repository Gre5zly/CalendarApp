package com.calendar;

import com.calendar.model.Note;
import com.calendar.service.NoteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class NoteServiceTest {
    private NoteService service;
    private final LocalDate testDate = LocalDate.of(2025, 1, 1);

    @BeforeEach
    void init() {
        service = new NoteService();
    }

    @Test
    void testAddAndGetNote() {
        service.addNote(testDate, "Hello World");
        List<Note> notes = service.getNotesForDate(testDate);

        Assertions.assertEquals(1, notes.size());
        Assertions.assertEquals("Hello World", notes.get(0).getContent());
    }

    @Test
    void testDeleteNote() {
        service.addNote(testDate, "To Delete");
        Note note = service.getNotesForDate(testDate).get(0);

        boolean isDeleted = service.deleteNoteById(note.getId());

        Assertions.assertTrue(isDeleted);
        Assertions.assertTrue(service.getNotesForDate(testDate).isEmpty());
    }
}