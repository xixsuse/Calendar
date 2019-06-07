package com.calendar.dao;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import com.calendar.entity.Note;

import javafx.scene.paint.Color;

/**
 *
 * @author Aleksandra Borejko
 *
 **/
public class NoteDao {
    private final DatabaseQueryExecutor databaseQueryExecutor;

    public NoteDao(DatabaseQueryExecutor databaseQueryExecutor) {
        this.databaseQueryExecutor = databaseQueryExecutor;
    }

    /**
     * Creates table in the database
     *
     * @throws IOException  In case something goes wrong while accessing the SQL file
     * @throws SQLException In case something goes wrong while executing the SQL statement
     */
    public void createNoteTable() throws IOException, SQLException {
        databaseQueryExecutor.executeSqlFile("sql/create_table.sql");
    }

    /**
     * Adds note to the database
     *
     * @param note Note object
     */
    public boolean insertNote(Note note) throws IOException, SQLException {
        final Date date = Date.valueOf(note.getDate());
        final String color = String.valueOf(note.getColorOrDefault());
        final Time time = getTime(note);

        return databaseQueryExecutor.executeUpdateSqlFile(
                "sql/insert_note.sql",
                prepStmt -> insertNoteStatement(prepStmt, note, date, color, time)
        ) == 1;
    }

    /**
     * Updates note
     *
     * @param note Note that's updated
     * @param id   Note's id
     * @return
     */
    public boolean updateNote(Note note, long id) throws IOException, SQLException {
        final Date date = Date.valueOf(note.getDate());
        final String color = String.valueOf(note.getColorOrDefault());
        final Time time = getTime(note);

        return databaseQueryExecutor.executeUpdateSqlFile(
                "sql/update_note.sql",
                prepStmt -> {
                    insertNoteStatement(prepStmt, note, date, color, time);
                    prepStmt.setLong(7, id);
                }
        ) == 1;
    }

    /**
     * Remove selected note
     *
     * @param id Note's id
     * @return
     */

    public boolean deleteNote(long id) throws IOException, SQLException {
        return databaseQueryExecutor.executeUpdateSqlFile(
                "sql/delete_note.sql",
                prepStmt -> prepStmt.setLong(1, id)
        ) == 1;
    }

    /**
     * Retrieves all notes from the database
     *
     * @return List of notes
     */
    public List<Note> getNotes() throws SQLException {
        return databaseQueryExecutor.selectAll("notes", this::resultSetToNoteList);
    }

    private List<Note> resultSetToNoteList(ResultSet rSet) throws SQLException {
        List<Note> notes = new LinkedList<>();

        while (rSet.next()) {
            Long id = rSet.getLong("id");
            Date date = rSet.getDate("date");
            Time time = rSet.getTime("time");
            String location = rSet.getString("location");
            String title = rSet.getString("title");
            String details = rSet.getString("details");
            String color = rSet.getString("color");

            LocalDate localDate = date.toLocalDate();

            Color pickedColor = Color.web(color, 0.5);

            LocalTime localTime;
            if (time != null) {
                localTime = time.toLocalTime();
            } else {
                localTime = null;
            }

            Note note = new Note();
            note.setId(id);
            note.setDate(localDate);
            note.setTime(localTime);
            note.setLocation(location);
            note.setTitle(title);
            note.setDetails(details);
            note.setColor(pickedColor);

            notes.add(note);
        }

        return notes;
    }

    private Time getTime(Note note) {
        Time time;
        if (note.getTime() != null) {
            time = Time.valueOf(note.getTime());
        } else {
            time = null;
        }
        return time;
    }

    private void insertNoteStatement(
            PreparedStatement prepStmt,
            Note note,
            Date date,
            String color,
            Time time) throws SQLException {
        prepStmt.setDate(1, date);
        prepStmt.setTime(2, time);
        prepStmt.setString(3, note.getLocation());
        prepStmt.setString(4, note.getTitle());
        prepStmt.setString(5, note.getDetails());
        prepStmt.setString(6, color);
    }
}
