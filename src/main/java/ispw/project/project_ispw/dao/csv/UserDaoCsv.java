package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Properties;

public class UserDaoCsv implements UserDao {

    private static final String CSV_FILE_NAME;

    private final HashMap<String, UserBean> localCache;

    static {
        Properties properties = new Properties();
        String fileName = "users.csv"; // Default

        try (InputStream input = UserDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("user.csv.filename", fileName);
            } else {
                // csv.properties file not found, using default CSV filename for users.
            }
        } catch (IOException e) {
            // Error loading properties, using default filename.
        }
        CSV_FILE_NAME = fileName;

        // Ensure the file exists right at the start
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new RuntimeException("Initialization failed: Could not create CSV file for users.", e);
        }
    }

    public UserDaoCsv() {
        this.localCache = new HashMap<>();
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
            throw new ExceptionDao("Failed to retrieve user from CSV for username: " + username + ". Data corruption or I/O error.", e);
        }

        if (user != null) {
            // Add to cache
            synchronized (localCache) {
                localCache.put(username, user);
            }
        }
        // If user is null (not found in file), simply return null.
        return user;
    }

    private UserBean retrieveByUsernameFromFile(String username) throws IOException, CsvValidationException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    continue; // Skip malformed records
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
            // Use the corrected retrieveByUsernameFromFile, which returns null if not found
            existingUser = retrieveByUsernameFromFile(username);
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to check existing user for username: " + username + ". Data corruption or I/O error.", e);
        }

        if (existingUser != null) {
            throw new ExceptionDao("Duplicated Username: " + username + ". User already exists in CSV file.");
        }

        // If it doesn't exist, save to file and add to cache
        try {
            saveUserToFile(user);
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to save user to CSV for username: " + username + ". I/O or data error.", e);
        }

        synchronized (localCache) {
            localCache.put(username, user);
        }
    }

    private void saveUserToFile(UserBean user) throws IOException, CsvValidationException {
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
            String[] record = {user.getUsername(), user.getPassword()};
            csvWriter.writeNext(record);
        }
    }
}