package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.exception.CsvDaoException;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListDaoCsv implements ListDao {

    private static final Logger LOGGER = Logger.getLogger(ListDaoCsv.class.getName());

    private static final String CSV_FILE_NAME;

    private final HashMap<Integer, ListBean> localCache;

    static {
        Properties properties = new Properties();
        String fileName = "list.csv";

        try (InputStream input = ListDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("list.csv.filename", fileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default filename: {0}", fileName);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load csv.properties. Using default filename: {0}. Error: {1}", new Object[]{fileName, e.getMessage()});
        }
        CSV_FILE_NAME = fileName;

        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Could not create CSV file for lists.", e);
        }
    }

    public ListDaoCsv() {
        this.localCache = new HashMap<>();
    }

    @Override
    public ListBean retrieveById(int id) throws ExceptionDao {
        synchronized (localCache) {
            if (localCache.containsKey(id)) {
                return localCache.get(id);
            }
        }

        ListBean list = null;
        try {
            list = retrieveByIdFromFile(id);
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve list from CSV for ID: " + id + ". Data corruption or I/O error.", e);
        } catch (CsvDaoException e) {
            throw new ExceptionDao("CSV data validation error while retrieving list for ID: " + id, e);
        }

        if (list != null) {
            synchronized (localCache) {
                localCache.put(id, list);
            }
        }
        return list;
    }

    private ListBean retrieveByIdFromFile(int id) throws IOException, NumberFormatException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordList;
            while ((recordList = csvReader.readNext()) != null) {
                if (recordList.length < 3) {
                    continue;
                }
                int currentId = Integer.parseInt(recordList[0]);
                if (currentId == id) {
                    return new ListBean(currentId, recordList[1], recordList[2]);
                }
            }
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV validation error.", e);
        }
        return null;
    }

    @Override
    public void saveList(ListBean list, UserBean user) throws ExceptionDao {
        int listId = list.getId();

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
        } catch (CsvDaoException e) {
            throw new ExceptionDao("CSV data validation error while checking existing list for ID: " + listId, e);
        }

        if (existingList != null) {
            throw new ExceptionDao("List with ID " + listId + " already exists in CSV file.");
        }

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
            String[] recordList = {
                    String.valueOf(list.getId()),
                    list.getName(),
                    list.getUsername()
            };
            csvWriter.writeNext(recordList);
        }
    }

    @Override
    public void deleteList(ListBean list) throws ExceptionDao {
        synchronized (localCache) {
            localCache.remove(list.getId());
        }

        try {
            deleteListFromFile(list);
        } catch (IOException | CsvDaoException e) {
            throw new ExceptionDao("Failed to delete list from CSV. I/O or data error.", e);
        }
    }

    private void deleteListFromFile(ListBean list) throws IOException {
        java.nio.file.Path originalPath = Paths.get(CSV_FILE_NAME);
        java.nio.file.Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");

        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(originalPath));
             CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(tempPath))) {

            String[] recordList;
            while ((recordList = csvReader.readNext()) != null) {
                if (recordList.length < 3) {
                    csvWriter.writeNext(recordList);
                    continue;
                }
                processListRecordForDeletion(recordList, list.getId(), csvWriter);
            }
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV validation error during deleteListFromFile.", e);
        }

        Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void processListRecordForDeletion(String[] recordList, int listIdToDelete, CSVWriter csvWriter) {
        try {
            if (Integer.parseInt(recordList[0]) != listIdToDelete) {
                csvWriter.writeNext(recordList);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping removal for malformed list record in CSV due to invalid ID format. Record will be preserved. Record: {0}, Error: {1}",
                    new Object[]{java.util.Arrays.toString(recordList), e.getMessage()});
            csvWriter.writeNext(recordList);
        }
    }

    @Override
    public List<ListBean> retrieveAllListsOfUsername(String username) throws ExceptionDao {
        List<ListBean> allLists = null;
        try {
            allLists = retrieveAllListsFromFile();
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve all lists from CSV. Data corruption or I/O error.", e);
        } catch (CsvDaoException e) {
            throw new ExceptionDao("CSV data validation error while retrieving all lists.", e);
        }

        List<ListBean> userLists = new ArrayList<>();
        for (ListBean list : allLists) {
            if (list.getUsername().equals(username)) {
                userLists.add(list);
            }
        }

        synchronized (localCache) {
            localCache.clear();
            for (ListBean list : allLists) {
                localCache.put(list.getId(), list);
            }
        }

        return Collections.unmodifiableList(userLists);
    }

    private List<ListBean> retrieveAllListsFromFile() throws IOException, NumberFormatException {
        List<ListBean> listModels = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordList;
            while ((recordList = csvReader.readNext()) != null) {
                if (recordList.length < 3) {
                    continue;
                }
                ListBean parsedList = parseListRecord(recordList);
                if (parsedList != null) {
                    listModels.add(parsedList);
                }
            }
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV validation error during retrieveAllListsFromFile.", e);
        }
        return listModels;
    }

    private ListBean parseListRecord(String[] recordList) {
        try {
            return new ListBean(Integer.parseInt(recordList[0]), recordList[1], recordList[2]);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping malformed list record in CSV. Expected numeric ID, but found invalid data. Record: {0}, Error: {1}",
                    new Object[]{java.util.Arrays.toString(recordList), e.getMessage()});
            return null;
        }
    }
}