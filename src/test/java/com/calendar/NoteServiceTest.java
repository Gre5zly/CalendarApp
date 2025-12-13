package com.calendar;

import com.calendar.model.Note;
import com.calendar.service.NoteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class NoteServiceTest {
    private NoteService service;
    private final LocalDate testDate = LocalDate.of(2025, 1, 1);

    @BeforeEach
    void setUp() {
        service = new NoteService();
    }

    @Test
    @DisplayName("Добавление заметки увеличивает список")
    void testAddNote() {
        service.addNote(testDate, "Test Note");
        List<Note> notes = service.getNotesForDate(testDate);

        Assertions.assertEquals(1, notes.size());
        Assertions.assertEquals("Test Note", notes.get(0).getContent());
    }

    @Test
    @DisplayName("Удаление заметки убирает её из списка")
    void testDeleteNote() {
        service.addNote(testDate, "To Delete");
        Note note = service.getNotesForDate(testDate).get(0);

        boolean deleted = service.deleteNoteById(note.getId());

        Assertions.assertTrue(deleted);
        Assertions.assertTrue(service.getNotesForDate(testDate).isEmpty());
    }
}