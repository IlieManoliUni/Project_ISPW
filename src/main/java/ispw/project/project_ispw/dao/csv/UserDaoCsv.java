package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoCsv implements UserDao {

    private static final Logger LOGGER = Logger.getLogger(UserDaoCsv.class.getName());
    private static final String CSV_FILE_NAME;

    // Use a robust, non-static cache for thread safety and consistency.
    // For simplicity, I'll keep it as instance cache as it's typically managed.
    private final HashMap<String, UserBean> localCache;

    static {
        // This static block runs once when the class is loaded
        Properties properties = new Properties();
        String fileName = "users.csv"; // Default

        try (InputStream input = UserDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("user.csv.filename", fileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default CSV filename for users: {0}", fileName);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading csv.properties for UserDaoCsv. Using default filename: {0}");
        }
        CSV_FILE_NAME = fileName;

        // Ensure the file exists right at the start
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
                LOGGER.log(Level.INFO, "User CSV file created: {0}", CSV_FILE_NAME);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Initialization failed: Could not create user CSV file: " + CSV_FILE_NAME, e);
            throw new RuntimeException("Initialization failed: Could not create CSV file for users.", e);
        }
    }

    public UserDaoCsv() {
        this.localCache = new HashMap<>();
        // Optionally load all data into cache on startup if file is small.
        // For this example, retrieveByUsernameFromFile will handle loading into cache on demand.
    }

    @Override
    public UserBean retrieveByUsername(String username) throws ExceptionDao {
        // First, check cache
        synchronized (localCache) {
            if (localCache.containsKey(username)) {
                return localCache.get(username);
            }
        }

        // If not in cache, read from file
        UserBean user = null;
        try {
            user = retrieveByUsernameFromFile(username);
        } catch (IOException | CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "Error reading CSV file or parsing data for username: " + username, e);
            throw new ExceptionDao("Failed to retrieve user from CSV for username: " + username + ". Data corruption or I/O error.", e);
        }

        if (user != null) {
            // Add to cache
            synchronized (localCache) {
                localCache.put(username, user);
            }
        } else {
            // If not found in file either
            throw new ExceptionDao("No User Found with username: " + username);
        }
        return user;
    }

    private UserBean retrieveByUsernameFromFile(String username) throws IOException, CsvValidationException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    LOGGER.log(Level.WARNING, "Skipping malformed CSV record for retrieveByUsernameFromFile: {0}", String.join(",", record));
                    continue; // Skip invalid records
                }
                if (record[0].equals(username)) {
                    return new UserBean(record[0], record[1]); // username, password
                }
            }
        }
        return null;
    }

    @Override
    public void saveUser(UserBean user) throws ExceptionDao {
        String username = user.getUsername();

        // Check if username already exists in cache or file
        synchronized (localCache) {
            if (localCache.containsKey(username)) {
                throw new ExceptionDao("User with username '" + username + "' already in cache.");
            }
        }

        UserBean existingUser = null;
        try {
            existingUser = retrieveByUsernameFromFile(username);
        } catch (IOException | CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "Error checking existing user in CSV for username: " + username, e);
            throw new ExceptionDao("Failed to check existing user for username: " + username + ". Data corruption or I/O error.", e);
        }

        if (existingUser != null) {
            throw new ExceptionDao("Duplicated Username: " + username + ". User already exists in CSV file.");
        }

        // If it doesn't exist, save to file and add to cache
        try {
            saveUserToFile(user);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving user '" + username + "' to CSV file.", e);
            throw new ExceptionDao("Failed to save user to CSV for username: " + username + ". I/O error.", e);
        } catch (CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "CSV validation error saving user '" + username + "' to CSV file.", e);
            throw new ExceptionDao("CSV validation error while saving user.", e);
        }

        synchronized (localCache) {
            localCache.put(username, user);
        }
    }

    private void saveUserToFile(UserBean user) throws IOException, CsvValidationException {
        // Using StandardOpenOption.APPEND for appending and newBufferedWriter for efficiency
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
            String[] record = {user.getUsername(), user.getPassword()};
            csvWriter.writeNext(record);
        }
    }
}