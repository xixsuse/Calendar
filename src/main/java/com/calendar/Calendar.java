package com.calendar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.calendar.dao.DatabaseQueryExecutor;
import com.calendar.dao.NoteDao;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Aleksandra Borejko
 *
 */

public class Calendar extends Application {

    private static final String DB_NAME = "notes.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;

    private static Connection conn;
    private static DatabaseQueryExecutor databaseQueryExecutor;
    private static NoteDao noteDao;

    public static void main(String[] args) throws SQLException {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        conn = DriverManager.getConnection(CONNECTION_STRING);
        databaseQueryExecutor = new DatabaseQueryExecutor(conn);
        noteDao = new NoteDao(databaseQueryExecutor);

        noteDao.createNoteTable();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("Calendar.fxml"));
        BorderPane pane = loader.load();
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.show();
        primaryStage.setTitle("Calendar");
        primaryStage.setResizable(false);
        primaryStage.setOnHidden(e -> Platform.exit());

    }

    @Override
    public void stop() throws Exception {
        conn.close();
    }

    public static NoteDao getNoteDao() {
        return noteDao;
    }
}
