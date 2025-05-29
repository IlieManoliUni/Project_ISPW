package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.exception.CsvDaoException;
import ispw.project.project_ispw.exception.ExceptionDao;

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

    private final HashMap<String, UserBean> localCache;

    static {
        Properties properties = new Properties();
        String fileName = "users.csv";

        try (InputStream input = UserDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("user.csv.filename", fileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default CSV filename for users: ''{0}''", fileName);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading csv.properties. Using default CSV filename for users: ''{0}''. Error: {1}",
                    new Object[]{fileName, e.getMessage()});
        }
        CSV_FILE_NAME = fileName;

        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Could not create CSV file for users.", e);
        }
    }

    public UserDaoCsv() {
        this.localCache = new HashMap<>();
    }

    @Override
    public UserBean retrieveByUsername(String username) throws ExceptionDao {
        synchronized (localCache) {
            if (localCache.containsKey(username)) {
                return localCache.get(username);
            }
        }

        UserBean user = null;
        try {
            user = retrieveByUsernameFromFile(username);
        } catch (IOException e) {
            throw new ExceptionDao("Failed to retrieve user from CSV for username: " + username + ". I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error while retrieving user for username: " + username, e);
        }

        if (user != null) {
            synchronized (localCache) {
                localCache.put(username, user);
            }
        }
        return user;
    }

    private UserBean retrieveByUsernameFromFile(String username) throws IOException, CsvValidationException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordUser;
            while ((recordUser = csvReader.readNext()) != null) {
                if (recordUser.length < 2) {
                    continue;
                }
                if (recordUser[0].equals(username)) {
                    return new UserBean(recordUser[0], recordUser[1]);
                }
            }
        }
        return null;
    }

    @Override
    public void saveUser(UserBean user) throws ExceptionDao {
        String username = user.getUsername();

        synchronized (localCache) {
            if (localCache.containsKey(username)) {
                throw new ExceptionDao("User with username '" + username + "' already in cache.");
            }
        }

        UserBean existingUser = null;
        try {
            existingUser = retrieveByUsernameFromFile(username);
        } catch (IOException e) {
            throw new ExceptionDao("Failed to check existing user for username: " + username + ". I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error while checking existing user for username: " + username, e);
        }

        if (existingUser != null) {
            throw new ExceptionDao("Duplicated Username: " + username + ". User already exists in CSV file.");
        }

        try {
            saveUserToFile(user);
        } catch (IOException e) {
            throw new ExceptionDao("Failed to save user to CSV for username: " + username + ". I/O error.", e);
        }

        synchronized (localCache) {
            localCache.put(username, user);
        }
    }

    private void saveUserToFile(UserBean user) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
            String[] recordUser = {user.getUsername(), user.getPassword()};
            csvWriter.writeNext(recordUser);
        }
    }
}