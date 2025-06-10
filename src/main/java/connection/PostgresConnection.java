package connection;

import util.PropertyUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        URL = PropertyUtil.getProperty("db.postgresql.url");
        USER = PropertyUtil.getProperty("db.postgresql.user");
        PASSWORD = PropertyUtil.getProperty("db.postgresql.password");
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}