package zeta.foods.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "5432");
    // Check for new database from migrations, otherwise use default
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "restaurant_management_new");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "restaurant_user");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "restaurant_password");

    private static final String JDBC_URL = String.format("jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load PostgreSQL JDBC driver
                Class.forName("org.postgresql.Driver");

                logger.info("Connecting to database {} at {}:{}", DB_NAME, DB_HOST, DB_PORT);

                // Create connection
                connection = DriverManager.getConnection(
                    JDBC_URL,
                    DB_USER,
                    DB_PASSWORD
                );

                logger.info("Database connection established");
            } catch (ClassNotFoundException e) {
                logger.error("PostgreSQL JDBC driver not found", e);
                throw new SQLException("Database driver not found", e);
            } catch (SQLException e) {
                logger.error("Failed to connect to database: {}", e.getMessage(), e);
                throw e;
            }
        }

        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }
}
