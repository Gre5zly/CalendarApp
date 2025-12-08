package com.calendar;

import com.calendar.model.Note;
import com.calendar.service.HolidayService;
import com.calendar.service.NoteService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;

/**
 * Главный класс приложения. Запускает JavaFX UI.
 */
public class App extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);

    private final NoteService noteService = new NoteService();
    private final HolidayService holidayService = new HolidayService();

    private DatePicker datePicker;
    private ListView<String> noteListView;
    private Label holidayInfoLabel;
    private TextArea inputArea;

    // Временное хранение объектов для сопоставления с ListView
    private List<Note> currentNotesList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        logger.info("Приложение запускается...");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // --- ВЕРХНЯЯ ПАНЕЛЬ ---
        VBox topBox = new VBox(10);
        datePicker = new DatePicker(LocalDate.now());
        holidayInfoLabel = new Label("Выберите дату для проверки праздников");
        holidayInfoLabel.setStyle("-fx-text-fill: #2a66c4; -fx-font-weight: bold;");

        datePicker.setOnAction(e -> refreshData());

        topBox.getChildren().addAll(new Label("Дата:"), datePicker, holidayInfoLabel);
        root.setTop(topBox);

        // --- ЦЕНТР (СПИСОК) ---
        noteListView = new ListView<>();
        root.setCenter(noteListView);
        BorderPane.setMargin(noteListView, new Insets(10, 0, 10, 0));

        // --- ПРАВАЯ ПАНЕЛЬ (КНОПКИ) ---
        VBox rightBox = new VBox(10);
        rightBox.setPrefWidth(200);

        inputArea = new TextArea();
        inputArea.setPromptText("Текст заметки...");
        inputArea.setPrefRowCount(3);
        inputArea.setWrapText(true);

        Button btnAdd = new Button("Добавить заметку");
        Button btnDelete = new Button("Удалить выбранную");
        Button btnClear = new Button("Очистить день");

        // Растягиваем кнопки
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnClear.setMaxWidth(Double.MAX_VALUE);

        // События кнопок
        btnAdd.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            String text = inputArea.getText();
            if (date != null && !text.isBlank()) {
                noteService.addNote(date, text);
                inputArea.clear();
                refreshData();
            }
        });

        btnDelete.setOnAction(e -> {
            int idx = noteListView.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && currentNotesList != null && idx < currentNotesList.size()) {
                Note toDelete = currentNotesList.get(idx);
                noteService.deleteNoteById(toDelete.getId());
                refreshData();
            }
        });

        btnClear.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            if (date != null) {
                noteService.clearDay(date);
                refreshData();
            }
        });

        rightBox.getChildren().addAll(new Label("Новая запись:"), inputArea, btnAdd, new Separator(), btnDelete, btnClear);
        root.setRight(rightBox);

        // Первая отрисовка
        refreshData();

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Календарь Заметок (JavaFX)");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshData() {
        LocalDate date = datePicker.getValue();
        if (date == null) return;

        // 1. Обновляем статус праздника
        String status = holidayService.getDayStatus(date);
        holidayInfoLabel.setText("Статус дня: " + status);

        // 2. Обновляем список заметок
        noteListView.getItems().clear();
        currentNotesList = noteService.getNotesForDate(date);

        int counter = 1;
        for (Note note : currentNotesList) {
            String shortId = note.getId().toString().substring(0, 8);
            String row = String.format("#%d | ID: ...%s | %s", counter++, shortId, note.getContent());
            noteListView.getItems().add(row);
        }
    }
}