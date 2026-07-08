package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_PATH =
            System.getProperty("user.dir")
            + File.separator + "src"
            + File.separator + "database"
            + File.separator + "autocomplete.db";

    private static final String DB_URL =
            "jdbc:sqlite:" + DB_PATH;

    public static Connection getConnection() throws SQLException {

        try {

            File dbFile = new File(DB_PATH);

            if (!dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }

            Class.forName("org.sqlite.JDBC");

            Connection conn =
                    DriverManager.getConnection(DB_URL);

            return conn;

        } catch (ClassNotFoundException e) {

            throw new SQLException(
                    "SQLite JDBC Driver not found!"
            );
        }
    }
}