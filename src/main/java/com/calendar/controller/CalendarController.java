package com.calendar.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import com.calendar.Calendar;
import com.calendar.entity.Note;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.BlendMode;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Aleksandra Borejko
 *
 */

public class CalendarController {
    @FXML
    private Label monthLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private Label titleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private TextArea detailsArea;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private AnchorPane note;
    @FXML
    private NoteController noteController;

    private LocalDate localDate, selectedDate;
    private static Note selectedNote;

    public static Note getSelectedNote() {
        return selectedNote;
    }

    public void initialize() {
        if (localDate == null) {
            localDate = LocalDate.now();
        }
        if (selectedDate == null) {
            selectedDate = localDate;
        }
        monthLabel.setText(localDate.getMonth().toString());
        yearLabel.setText(String.valueOf(localDate.getYear()));
        drawCalendarGrid(localDate);

    }

    public void newNote() {
        if (selectedNote != null) {
            selectedNote = null;
            clearNoteDetails();
        }
        showNoteWindow();
    }

    public void editNote() {
        if (selectedNote != null) {
            showNoteWindow();
        }
    }

    public void deleteNote() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete this note?");
        alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
            deleteNoteFromDatabase();
            refreshCalendar(selectedDate);
            clearNoteDetails();
        });
    }

    private void deleteNoteFromDatabase() {
        try {
            Calendar.getNoteDao().deleteNote(selectedNote.getId());
        } catch (IOException | SQLException e) {
            System.out.println("Couldn't delete note: " + e.getMessage());
        }
    }

    private void showNoteWindow() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("Note.fxml"));
        try {
            Stage stage = new Stage();
            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.show();
            stage.setTitle("Note");
            stage.setResizable(false);
        } catch (IOException e) {
            System.out.println("Couldn't load note window: " + e.getMessage());
        }
        noteController = loader.getController();
        noteController.setCalendarController(this);
    }

    private void showNoteDetails() {
        clearNoteDetails();
        titleLabel.setVisible(true);
        titleLabel.textProperty().bind(selectedNote.titleProperty());
        dateLabel.setVisible(true);
        dateLabel.textProperty().bind(selectedNote.dateProperty().asString());
        if (selectedNote.getTime() != null) {
            timeLabel.setVisible(true);
            timeLabel.textProperty().bind(selectedNote.timeProperty().asString());
        }
        if (selectedNote.getLocation() != null) {
            locationLabel.setVisible(true);
            locationLabel.textProperty().bindBidirectional(selectedNote.locationProperty());
        }
        if (selectedNote.getDetails() != null) {
            detailsArea.setVisible(true);
            detailsArea.textProperty().bindBidirectional(selectedNote.detailsProperty());
        }
        editButton.setVisible(true);
        deleteButton.setVisible(true);
    }

    private void clearNoteDetails() {
        titleLabel.setVisible(false);
        dateLabel.setVisible(false);
        timeLabel.setVisible(false);
        locationLabel.setVisible(false);
        detailsArea.setVisible(false);
        editButton.setVisible(false);
        deleteButton.setVisible(false);
    }

    private void drawCalendarGrid(LocalDate date) {
        clearGrid();
        int rows = 6;
        int columns = 7;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                VBox dayVBox = new VBox();

                drawDays(date, i, j, dayVBox);
                if (dayVBox.getChildren().isEmpty()) {
                    dayVBox.setBackground(new Background(new BackgroundFill(Color.rgb(29, 27, 30, 0.5), null, null)));
                }

                GridPane.setVgrow(dayVBox, Priority.ALWAYS);

                GridPane.setFillHeight(dayVBox, true);
                GridPane.setFillWidth(dayVBox, true);
                calendarGrid.add(dayVBox, j, i);
            }
        }
    }

    private void drawDays(LocalDate date, int row, int column, VBox vBox) {
        int lengthOfMonth = date.lengthOfMonth();
        int dayNumber;
        int dayOfWeek;
        int weekNumber = 0;

        for (int i = 0; i < lengthOfMonth; i++) {
            dayNumber = i + 1;
            dayOfWeek = date.withDayOfMonth(dayNumber).getDayOfWeek().getValue();

            // checking Sundays
            if (dayOfWeek == 7) {
                // checking if the month starts from Sunday
                if (dayNumber != 1) {
                    weekNumber++;
                }
                if ((row == weekNumber) && (column == 0)) {
                    addDayLabel(dayNumber, vBox);
                    addNotesLabels(getNotesForDay(date, dayNumber), vBox);
                }
            }

            // checking the rest of the week
            if ((row == weekNumber) && (column == dayOfWeek)) {
                addDayLabel(dayNumber, vBox);
                addNotesLabels(getNotesForDay(date, dayNumber), vBox);
            }
        }
    }

    private void addDayLabel(int day, VBox vBox) {
        Label dayLabel = new Label();
        dayLabel.setMinWidth(calendarGrid.getMinWidth() / 7);
        dayLabel.setAlignment(Pos.CENTER_LEFT);
        dayLabel.setId("day-label");
        dayLabel.setPadding(new Insets(5));
        dayLabel.setText(String.valueOf(day));
        vBox.getChildren().add(dayLabel);
    }

    private void addNotesLabels(ObservableList<Note> notes, VBox vBox) {
        for (Note note : notes) {
            Label noteLabel = new Label();
            noteLabel.setMinWidth(calendarGrid.getMinWidth() / 7);
            noteLabel.setAlignment(Pos.CENTER);
            noteLabel.setId("note-label");
            noteLabel.setText(note.getTitle());
            noteLabel.setBlendMode(BlendMode.SCREEN);
            noteLabel.setBackground(
                    new Background(new BackgroundFill(note.getColorOrDefault(), new CornerRadii(20), Insets.EMPTY)));
            noteLabel.setOnMouseClicked(e -> {
                selectedNote = note;
                showNoteDetails();
            });
            vBox.getChildren().add(noteLabel);
        }
    }

    private ObservableList<Note> getNotesForDay(LocalDate date, int day) {
        int month = date.getMonthValue();
        int year = date.getYear();
        LocalDate checkedDate = LocalDate.of(year, month, day);
        ObservableList<Note> notesForDay = FXCollections.observableArrayList();
        List<Note> allNotes;

        try {
            allNotes = getNotesFromDatabase();
            if (allNotes.isEmpty()) {
                allNotes = Collections.emptyList();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't load notes from the database: " + e.getMessage());
            allNotes = Collections.emptyList();
        }

        for (Note note : allNotes) {
            if (note.getDate().equals(checkedDate)) {
                notesForDay.add(note);
            }
        }
        return notesForDay;
    }

    private List<Note> getNotesFromDatabase() throws SQLException {
        return Calendar.getNoteDao().getNotes();
    }

    private void clearGrid() {
        calendarGrid.getChildren().retainAll(calendarGrid.getChildren().get(0));
    }

    public void refreshCalendar(LocalDate date) {
        drawCalendarGrid(date);
        if (selectedNote != null) {
            showNoteDetails();
        }
    }

    public void nextMonth() {
        selectedDate = selectedDate.plusMonths(1);
        monthLabel.setText(String.valueOf(selectedDate.getMonth()));
        if (selectedDate.getMonth().equals(Month.JANUARY)) {
            yearLabel.setText(String.valueOf(selectedDate.getYear()));
        }
        refreshCalendar(selectedDate);
    }

    public void previousMonth() {
        selectedDate = selectedDate.minusMonths(1);
        monthLabel.setText(String.valueOf(selectedDate.getMonth()));
        if (selectedDate.getMonth().equals(Month.DECEMBER)) {
            yearLabel.setText(String.valueOf(selectedDate.getYear()));
        }
        refreshCalendar(selectedDate);
    }

    public void nextYear() {
        selectedDate = selectedDate.plusYears(1);
        yearLabel.setText(String.valueOf(selectedDate.getYear()));
        refreshCalendar(selectedDate);
    }

    public void previousYear() {
        selectedDate = selectedDate.minusYears(1);
        yearLabel.setText(String.valueOf(selectedDate.getYear()));
        refreshCalendar(selectedDate);
    }

}
