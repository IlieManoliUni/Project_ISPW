package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class ListDaoCsv implements ListDao {

    private static final String CSV_FILE_NAME;

    // Use a robust, non-static cache for thread safety and consistency.
    // For simplicity, I'll keep it as instance cache as it's typically managed.
    private final HashMap<Integer, ListBean> localCache;

    static {
        // This static block runs once when the class is loaded
        Properties properties = new Properties();
        String fileName = "list.csv"; // Default

        try (InputStream input = ListDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("list.csv.filename", fileName);
            } else {
                // csv.properties file not found, using default filename.
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
            // If file creation fails, this is a critical error for the DAO
            throw new RuntimeException("Initialization failed: Could not create CSV file for lists.", e);
        }
    }

    public ListDaoCsv() {
        this.localCache = new HashMap<>();
        // Optionally load all data into cache on startup if file is small
        // For this example, retrieveAllListsFromFile will handle loading into cache on first call if not already there.
    }

    @Override
    public ListBean retrieveById(int id) throws ExceptionDao {
        // First, check cache
        synchronized (localCache) {
            if (localCache.containsKey(id)) {
                return localCache.get(id);
            }
        }

        // If not in cache, read from file
        ListBean list = null;
        try {
            list = retrieveByIdFromFile(id);
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve list from CSV for ID: " + id + ". Data corruption or I/O error.", e);
        }

        if (list != null) {
            // Add to cache
            synchronized (localCache) {
                localCache.put(id, list);
            }
        } else {
            // If not found in file either
            throw new ExceptionDao("No List Found with ID: " + id);
        }
        return list;
    }

    private ListBean retrieveByIdFromFile(int id) throws IOException, NumberFormatException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 3) {
                    continue; // Skip malformed records
                }
                int currentId = Integer.parseInt(record[0]);
                if (currentId == id) {
                    return new ListBean(currentId, record[1], record[2]);
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV validation error.", e);
        }
        return null;
    }

    @Override
    public void saveList(ListBean list, UserBean user) throws ExceptionDao {
        int listId = list.getId();

        // Check if ID already exists in cache or file
        synchronized (localCache) {
            if (localCache.containsKey(listId)) {
                throw new ExceptionDao("List with ID " + listId + " already in cache.");
            }
        }

        ListBean existingList = null;
        try {
            existingList = retrieveByIdFromFile(listId);
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to check existing list for ID: " + listId + ". Data corruption or I/O error.", e);
        }

        if (existingList != null) {
            throw new ExceptionDao("List with ID " + listId + " already exists in CSV file.");
        }

        // If it doesn't exist, save to file and add to cache
        try {
            saveListToFile(list);
        } catch (IOException e) {
            throw new ExceptionDao("Failed to save list to CSV for ID: " + listId + ". I/O error.", e);
        }

        synchronized (localCache) {
            localCache.put(listId, list);
        }
    }

    private void saveListToFile(ListBean list) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
            String[] record = {
                    String.valueOf(list.getId()),
                    list.getName(),
                    list.getUsername()
            };
            csvWriter.writeNext(record);
        }
    }

    @Override
    public void deleteList(ListBean list) throws ExceptionDao {
        synchronized (localCache) {
            localCache.remove(list.getId());
        }

        try {
            deleteListFromFile(list);
        } catch (IOException e) {
            throw new ExceptionDao("Failed to delete list from CSV. I/O error.", e);
        }
    }

    private void deleteListFromFile(ListBean list) throws IOException {
        java.nio.file.Path originalPath = Paths.get(CSV_FILE_NAME);
        java.nio.file.Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");

        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(originalPath));
             CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(tempPath))) {

            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 3) { // Ensure record has enough fields
                    csvWriter.writeNext(record); // Write it back if unsure, or skip
                    continue;
                }
                try {
                    if (Integer.parseInt(record[0]) != list.getId()) {
                        csvWriter.writeNext(record);
                    }
                } catch (NumberFormatException e) {
                    csvWriter.writeNext(record); // Write back records that cause parsing errors
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV validation error.", e);
        }

        // Atomically replace the original file with the temporary one
        Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public List<ListBean> retrieveAllListsOfUsername(String username) throws ExceptionDao {
        List<ListBean> allLists = null;
        try {
            allLists = retrieveAllListsFromFile();
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve all lists from CSV. Data corruption or I/O error.", e);
        }

        List<ListBean> userLists = new ArrayList<>();
        for (ListBean list : allLists) {
            if (list.getUsername().equals(username)) {
                userLists.add(list);
            }
        }

        // Optionally, cache all retrieved lists if retrieveAllListsFromFile fully reloads the cache.
        // If retrieveAllListsFromFile populates the cache:
        synchronized (localCache) {
            localCache.clear(); // Clear to ensure fresh state if this is the primary loading method
            for (ListBean list : allLists) {
                localCache.put(list.getId(), list);
            }
        }

        if (userLists.isEmpty()) {
            // Depending on business logic, returning an empty list might be preferred over an exception.
            // For now, mirroring previous behavior:
            throw new ExceptionDao("No Lists Found for username: " + username + " in CSV file.");
        }

        return Collections.unmodifiableList(userLists); // Return an unmodifiable list
    }

    private List<ListBean> retrieveAllListsFromFile() throws IOException, NumberFormatException {
        List<ListBean> listModels = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 3) {
                    continue; // Skip malformed records
                }
                try {
                    listModels.add(new ListBean(Integer.parseInt(record[0]), record[1], record[2]));
                } catch (NumberFormatException e) {
                    // Skipping CSV record with invalid ID format
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV validation error.", e);
        }
        return listModels;
    }
}