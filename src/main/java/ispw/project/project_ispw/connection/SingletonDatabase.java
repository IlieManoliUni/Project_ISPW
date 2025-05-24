package ispw.project.project_ispw.connection;

import ispw.project.project_ispw.exception.ExceptionDatabase;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger; // Using java.util.logging for simplicity, consider SLF4J

public class SingletonDatabase {

    private static final Logger LOGGER = Logger.getLogger(SingletonDatabase.class.getName());

    private Connection connection;

    private SingletonDatabase() {
        initializeConnection();
    }

    private void initializeConnection() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new ExceptionDatabase("Unable to find database.properties file on the classpath.");
            }
            properties.load(input);

            String url = properties.getProperty("CONNECTION_URL");
            String user = properties.getProperty("USER");
            String password = properties.getProperty("PASSWORD");

            if (url == null || user == null || password == null) {
                throw new ExceptionDatabase("One or more database properties (URL, USER, PASSWORD) are missing in database.properties.");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, password);
            LOGGER.info("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            throw new ExceptionDatabase("MySQL JDBC Driver not found! Please ensure it's in your classpath.", e);
        } catch (SQLException e) {
            throw new ExceptionDatabase("Failed to establish database connection. Check database server status, credentials, and URL.", e);
        } catch (Exception e) { // Catch-all for other issues like IOException during properties loading
            throw new ExceptionDatabase("An unexpected error occurred during database connection setup.", e);
        }
    }

    private static class SingletonHolder {
        private static final SingletonDatabase INSTANCE = new SingletonDatabase();
    }

    public static SingletonDatabase getInstance() throws ExceptionDatabase {
        return SingletonHolder.INSTANCE;
    }

    public Connection getConnection() throws ExceptionDatabase {
        try {
            // Check if connection is null, closed, or invalid.
            // isValid(timeout) checks if the connection is still open and valid within 'timeout' seconds.
            if (connection == null || connection.isClosed() || !connection.isValid(5)) { // 5-second timeout for validation
                LOGGER.warning("Database connection is stale or closed. Attempting to re-establish connection.");
                closeConnection(); // Ensure any old invalid connection is truly closed
                initializeConnection(); // Re-initialize the connection
            }
        } catch (SQLException e) {
            throw new ExceptionDatabase("Error checking database connection validity or re-establishing it.", e);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed successfully.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to close the database connection!", e);
            } finally {
                connection = null; // Important: set to null after closing
            }
        }
    }
}