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
 * Класс графического интерфейса (JavaFX).
 * Организует взаимодействие пользователя с сервисами.
 */
public class App extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);

    private final NoteService noteService = new NoteService();
    private final HolidayService holidayService = new HolidayService();

    private DatePicker datePicker;
    private ListView<String> noteListView;
    private Label holidayInfoLabel;
    private TextArea inputArea;

    // Временный список для сопоставления индексов ListView с объектами Note
    private List<Note> currentNotesList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        logger.info("Инициализация интерфейса...");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // Верхняя панель (Дата и Статус)
        VBox topBox = new VBox(10);
        datePicker = new DatePicker(LocalDate.now());
        holidayInfoLabel = new Label("Выберите дату для проверки...");
        holidayInfoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2a66c4;");

        // Обновление при смене даты
        datePicker.setOnAction(e -> refreshData());

        topBox.getChildren().addAll(new Label("Календарь:"), datePicker, holidayInfoLabel);
        root.setTop(topBox);

        // Центральная панель (Список)
        noteListView = new ListView<>();
        root.setCenter(noteListView);
        BorderPane.setMargin(noteListView, new Insets(10, 0, 10, 0));

        // Правая панель (Управление)
        VBox rightBox = new VBox(10);
        rightBox.setPrefWidth(220);

        inputArea = new TextArea();
        inputArea.setPromptText("Текст заметки...");
        inputArea.setPrefRowCount(4);
        inputArea.setWrapText(true);

        Button btnAdd = new Button("Добавить заметку");
        Button btnDelete = new Button("Удалить выбранную");
        Button btnClear = new Button("Очистить день");

        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnClear.setMaxWidth(Double.MAX_VALUE);

        btnAdd.setOnAction(e -> addNoteAction());
        btnDelete.setOnAction(e -> deleteNoteAction());
        btnClear.setOnAction(e -> clearDayAction());

        rightBox.getChildren().addAll(new Label("Новая запись:"), inputArea, btnAdd, new Separator(), btnDelete, btnClear);
        root.setRight(rightBox);

        // Первичная загрузка данных
        refreshData();

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Календарь и Заметки (Курсовая работа)");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Основной метод обновления UI.
     * Запрашивает статус дня у HolidayService и заметки у NoteService.
     */
    private void refreshData() {
        LocalDate date = datePicker.getValue();
        if (date == null) return;

        // 1. Обновляем статус праздника
        holidayInfoLabel.setText("Статус дня: " + holidayService.getDayStatus(date));

        // 2. Обновляем список
        noteListView.getItems().clear();
        currentNotesList = noteService.getNotesForDate(date);

        int i = 1;
        for (Note note : currentNotesList) {
            // Форматируем строку: № | ID... | Текст
            String shortId = note.getId().toString().substring(0, 8);
            noteListView.getItems().add(String.format("#%d | ID:...%s | %s", i++, shortId, note.getContent()));
        }
    }

    private void addNoteAction() {
        LocalDate date = datePicker.getValue();
        String text = inputArea.getText();
        if (date != null && !text.isBlank()) {
            noteService.addNote(date, text);
            inputArea.clear();
            refreshData();
        }
    }

    private void deleteNoteAction() {
        int idx = noteListView.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && currentNotesList != null && idx < currentNotesList.size()) {
            Note toDelete = currentNotesList.get(idx);
            noteService.deleteNoteById(toDelete.getId());
            refreshData();
        }
    }

    private void clearDayAction() {
        LocalDate date = datePicker.getValue();
        if (date != null) {
            noteService.clearDay(date);
            refreshData();
        }
    }
}