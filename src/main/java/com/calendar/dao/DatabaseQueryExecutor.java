package com.calendar.dao;

import com.calendar.util.ExceptionThrowingConsumer;

import com.calendar.util.ExceptionThrowingFunction;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Aleksandra Borejko
 *
 */
public class DatabaseQueryExecutor {
    private final Connection conn;

    public DatabaseQueryExecutor(Connection conn) {
        this.conn = conn;
    }

    /**
     * Executes the SQL contained in the specified file.
     * To be used when we don't care about any return values (such as the number of affected rows).
     * Useful for statements like CREATE TABLE.
     *
     * @param relativeFilePath The path to the file (e.x. sql/insert_note.sql)
     * @throws SQLException In case something goes wrong while executing the SQL statement
     * @throws IOException  In case something goes wrong while accessing the SQL file
     */
    public void executeSqlFile(String relativeFilePath) throws SQLException, IOException {
        try (Statement stmt = conn.createStatement()) {
            String sqlCode = readSqlFile(relativeFilePath);
            stmt.execute(sqlCode);
        }
    }

    /**
     * Executes the SQL contained in the specified file.
     * It returns the number of affected rows. This method is useful for executing
     * statements such as INSERT or UPDATE.
     *
     * @param relativeFilePath    The path to the file (e.x. sql/insert_note.sql)
     * @param statementOperations Operations to perform on the PreparedStatement
     *                            before calling {@link PreparedStatement#executeUpdate()} on it
     * @return Number of affected rows
     * @throws SQLException In case something goes wrong while executing the SQL statement
     * @throws IOException  In case something goes wrong while accessing the SQL file
     */
    public long executeUpdateSqlFile(
            String relativeFilePath,
            ExceptionThrowingConsumer<PreparedStatement, SQLException> statementOperations)
            throws SQLException, IOException {
        String sqlCode = readSqlFile(relativeFilePath);
        try (PreparedStatement stmt = conn.prepareStatement(sqlCode)) {
            statementOperations.consume(stmt);
            return stmt.executeUpdate();
        }
    }

    /**
     * Selects all rows from a table.
     * It does not directly return the {@link ResultSet}, but it transforms it
     * as specified by the toResult argument.
     *
     * @param tableName The name of the table.
     * @param toResult  How to transform the table rows into the desired result.
     * @param <T>       The type of the desired result
     * @return The result of transforming the rows using the toResult function
     * @throws SQLException In case something goes wrong while executing the SQL query
     */
    public <T> T selectAll(
            String tableName,
            ExceptionThrowingFunction<ResultSet, T, SQLException> toResult)
            throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + tableName)) {
            return toResult.apply(resultSet);
        }
    }

    private String readSqlFile(String relativeFilePath) throws IOException {
        InputStream createTable = this.getClass().getClassLoader().getResourceAsStream(relativeFilePath);
        return IOUtils.toString(createTable, Charset.forName("UTF-8"));
    }
}