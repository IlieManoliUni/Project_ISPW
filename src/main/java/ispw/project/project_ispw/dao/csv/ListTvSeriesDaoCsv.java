package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListTvSeriesDaoCsv implements ListTvSeries {

    private static final Logger LOGGER = Logger.getLogger(ListTvSeriesDaoCsv.class.getName());
    private static final String CSV_FILE_NAME;
    private static final String TVSERIES_CSV_FILE_NAME; // To get TV series details

    // Static block to load CSV file names and ensure files exist
    static {
        Properties properties = new Properties();
        String listTvSeriesFileName = "list_tvseries.csv"; // Default
        String tvSeriesFileName = "tvseries.csv"; // Default for fetching TV series details

        try (InputStream input = ListTvSeriesDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                listTvSeriesFileName = properties.getProperty("list_tvseries.csv.filename", listTvSeriesFileName);
                tvSeriesFileName = properties.getProperty("tvseries.csv.filename", tvSeriesFileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default CSV filenames.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading csv.properties for ListTvSeriesDaoCsv. Using default filenames.", e);
        }

        CSV_FILE_NAME = listTvSeriesFileName;
        TVSERIES_CSV_FILE_NAME = tvSeriesFileName;

        // Ensure both files exist
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
                LOGGER.log(Level.INFO, "List-TVSeries CSV file created: {0}", CSV_FILE_NAME);
            }
            if (!Files.exists(Paths.get(TVSERIES_CSV_FILE_NAME))) {
                Files.createFile(Paths.get(TVSERIES_CSV_FILE_NAME));
                LOGGER.log(Level.INFO, "TVSeries CSV file created for lookup: {0}", TVSERIES_CSV_FILE_NAME);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Initialization failed: Could not create CSV files for ListTvSeriesDaoCsv.", e);
            throw new RuntimeException("Initialization failed: Could not create CSV files.", e);
        }
    }

    public ListTvSeriesDaoCsv() {
        // Constructor no longer needs to create the file; the static block handles it.
    }

    @Override
    public void addTvSeriesToList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        try {
            // Check if the TV Series already exists in the list
            if (tvSeriesExistsInList(list, tvSeries)) {
                throw new ExceptionDao("TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " already exists in list ID " + list.getId() + ".");
            }

            // Add the TV Series to the CSV file
            try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
                String[] record = {
                        String.valueOf(list.getId()),
                        String.valueOf(tvSeries.getIdTvSeriesTmdb())
                };
                writer.writeNext(record);
            }
        } catch (IOException e) { // Added CsvValidationException
            LOGGER.log(Level.SEVERE, "Error adding TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " to list ID " + list.getId() + " in CSV file.", e);
            throw new ExceptionDao("Failed to add TV Series to list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public void removeTvSeriesFromList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        try {
            // Check if the TV Series exists in the list
            if (!tvSeriesExistsInList(list, tvSeries)) {
                throw new ExceptionDao("TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " not found in list ID " + list.getId() + ".");
            }

            java.nio.file.Path originalPath = Paths.get(CSV_FILE_NAME);
            java.nio.file.Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");
            List<String[]> allRecords = new ArrayList<>();
            String[] record = null; // Declare record outside try-catch to be accessible in catch block

            try (CSVReader reader = new CSVReader(Files.newBufferedReader(originalPath))) {
                while ((record = reader.readNext()) != null) {
                    if (record.length < 2) {
                        LOGGER.log(Level.WARNING, "Skipping malformed CSV record during removal: {0}", String.join(",", record));
                        continue;
                    }
                    try {
                        if (!(Integer.parseInt(record[0]) == list.getId() && Integer.parseInt(record[1]) == tvSeries.getIdTvSeriesTmdb())) {
                            allRecords.add(record);
                        }
                    } catch (NumberFormatException e) {
                        // Log the problematic record if it caused NumberFormatException
                        LOGGER.log(Level.WARNING, "Skipping CSV record with invalid ID format during removal: {0}. Error: {1}",
                                new Object[]{record != null ? String.join(",", record) : "null record", e.getMessage()});
                        // Decide whether to keep or discard malformed records. For removal, safer to keep.
                        allRecords.add(record);
                    }
                }
            } catch (CsvValidationException e) { // Added CsvValidationException
                LOGGER.log(Level.SEVERE, "CSV validation error during removal of TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " from list ID " + list.getId(), e);
                throw new IOException("CSV validation error.", e);
            }

            try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(tempPath))) {
                writer.writeAll(allRecords);
            }

            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) { // Catch IOException for file operations
            LOGGER.log(Level.SEVERE, "Error removing TV Series ID " + tvSeries.getIdTvSeriesTmdb() + " from list ID " + list.getId() + " in CSV file.", e);
            throw new ExceptionDao("Failed to remove TV Series from list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public List<TvSeriesBean> getAllTvSeriesInList(ListBean list) throws ExceptionDao {
        List<TvSeriesBean> tvSeriesList = new ArrayList<>();
        String[] record = null; // Declare record outside try-catch for logging

        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    LOGGER.log(Level.WARNING, "Skipping malformed CSV record while getting all TV Series in list: {0}", String.join(",", record));
                    continue;
                }
                try {
                    if (Integer.parseInt(record[0]) == list.getId()) {
                        int tvSeriesId = Integer.parseInt(record[1]);
                        TvSeriesBean tvSeries = fetchTvSeriesById(tvSeriesId);

                        if (tvSeries != null) {
                            tvSeriesList.add(tvSeries);
                        } else {
                            LOGGER.log(Level.WARNING, "TV Series with ID {0} found in list {1} but not in {2}.",
                                    new Object[]{tvSeriesId, list.getId(), TVSERIES_CSV_FILE_NAME});
                        }
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Skipping CSV record with invalid ID format: {0}. Error: {1}",
                            new Object[]{record != null ? String.join(",", record) : "null record", e.getMessage()});
                }
            }
        } catch (IOException | CsvValidationException e) { // Added CsvValidationException
            LOGGER.log(Level.SEVERE, "Error retrieving all TV Series for list ID " + list.getId() + " from CSV file.", e);
            throw new ExceptionDao("Failed to retrieve all TV Series for list from CSV. Data corruption or I/O error.", e);
        }

        if (tvSeriesList.isEmpty()) {
            LOGGER.log(Level.INFO, "No TV Series Found in list ID: {0}", list.getId());
            throw new ExceptionDao("No TV Series Found in the List (ID: " + list.getId() + ").");
        }

        return tvSeriesList;
    }

    // Helper method to check if a TV Series is already in the List
    private boolean tvSeriesExistsInList(ListBean list, TvSeriesBean tvSeries) throws ExceptionDao {
        String[] record = null; // Declare record outside try-catch for logging
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    LOGGER.log(Level.WARNING, "Skipping malformed CSV record during existence check: {0}", String.join(",", record));
                    continue;
                }
                try {
                    if (Integer.parseInt(record[0]) == list.getId() && Integer.parseInt(record[1]) == tvSeries.getIdTvSeriesTmdb()) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Skipping CSV record with invalid ID format: {0}. Error: {1}",
                            new Object[]{record != null ? String.join(",", record) : "null record", e.getMessage()});
                }
            }
        } catch (IOException | CsvValidationException e) { // Added CsvValidationException
            LOGGER.log(Level.SEVERE, "Error checking TV Series existence in list ID " + list.getId() + " for TV Series ID " + tvSeries.getIdTvSeriesTmdb(), e);
            throw new ExceptionDao("Failed to check TV Series existence in list from CSV. I/O or data error.", e);
        }
        return false;
    }

    // Helper method to fetch a TV series by its ID from the "tvseries.csv" file
    private TvSeriesBean fetchTvSeriesById(int id) throws ExceptionDao {
        String[] record = null; // Declare record outside try-catch for logging
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(TVSERIES_CSV_FILE_NAME)))) {
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 4) { // Assuming ID, episodeRuntime, numberOfEpisodes, name
                    LOGGER.log(Level.WARNING, "Skipping malformed TV Series CSV record: {0}", String.join(",", record));
                    continue;
                }
                try {
                    if (Integer.parseInt(record[0]) == id) {
                        // Assuming TvSeriesBean constructor: public TvSeriesBean(int id, int episodeRuntime, int numberOfEpisodes, String name)
                        // Note: Your TvSeriesBean constructor is (episodeRuntime, idTvSeriesTmdb, numberOfEpisodes, name)
                        // So, the order should be: record[1] (episodeRuntime), record[0] (id), record[2] (numberOfEpisodes), record[3] (name)
                        return new TvSeriesBean(Integer.parseInt(record[1]), Integer.parseInt(record[0]), Integer.parseInt(record[2]), record[3]);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Skipping CSV record with invalid number format: {0}. Error: {1}",
                            new Object[]{record != null ? String.join(",", record) : "null record", e.getMessage()});
                }
            }
        } catch (IOException | CsvValidationException e) { // Added CsvValidationException
            LOGGER.log(Level.SEVERE, "Error fetching TV Series ID " + id + " from " + TVSERIES_CSV_FILE_NAME, e);
            throw new ExceptionDao("Failed to fetch TV Series details from main TV Series CSV file. I/O or data error.", e);
        }
        return null; // TV Series not found
    }
}