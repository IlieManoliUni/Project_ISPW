package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom DAO exception

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections; // For unmodifiable list
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TvSeriesDaoCsv implements TvSeriesDao {

    private static final Logger LOGGER = Logger.getLogger(TvSeriesDaoCsv.class.getName());
    private static final String CSV_FILE_NAME;

    // Using an instance cache for thread safety and consistency.
    private final HashMap<Integer, TvSeriesBean> localCache;

    static {
        // This static block runs once when the class is loaded
        Properties properties = new Properties();
        String fileName = "tvseries.csv"; // Default

        try (InputStream input = TvSeriesDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("tvseries.csv.filename", fileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default CSV filename for TV series: {0}", fileName);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading csv.properties for TvSeriesDaoCsv. Using default filename: {0}");
            throw new RuntimeException("Initialization failed: Error loading csv.properties.", e); // Critical startup error
        }
        CSV_FILE_NAME = fileName;

        // Ensure the file exists right at the start
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
                LOGGER.log(Level.INFO, "TV Series CSV file created: {0}", CSV_FILE_NAME);
            }
        } catch (IOException e) {
            // If file creation fails, this is a critical error for the DAO
            LOGGER.log(Level.SEVERE, "Initialization failed: Could not create TV series CSV file: " + CSV_FILE_NAME, e);
            throw new RuntimeException("Initialization failed: Could not create CSV file for TV series.", e);
        }
    }

    public TvSeriesDaoCsv() {
        this.localCache = new HashMap<>();
        // Optionally load all data into cache on startup if file is small.
        // For this example, `retrieveAllTvSeries` will handle loading into cache on first call if not already there.
    }

    @Override
    public TvSeriesBean retrieveById(int id) throws ExceptionDao { // Changed to throw ExceptionDao
        // First, check cache
        synchronized (localCache) {
            if (localCache.containsKey(id)) {
                return localCache.get(id);
            }
        }

        // If not in cache, read from file
        TvSeriesBean tvSeries = null;
        try {
            tvSeries = retrieveByIdFromFile(id);
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error reading CSV file or parsing data for TV Series ID: " + id, e);
            throw new ExceptionDao("Failed to retrieve TV Series from CSV for ID: " + id + ". Data corruption or I/O error.", e);
        }

        if (tvSeries != null) {
            // Add to cache
            synchronized (localCache) {
                localCache.put(id, tvSeries);
            }
        } else {
            // If not found in file either
            throw new ExceptionDao("No TV Series Found with ID: " + id);
        }
        return tvSeries;
    }

    private TvSeriesBean retrieveByIdFromFile(int id) throws IOException, CsvValidationException, NumberFormatException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 4) {
                    LOGGER.log(Level.WARNING, "Skipping malformed CSV record for retrieveByIdFromFile: {0}", String.join(",", record));
                    continue; // Skip invalid records
                }
                int currentId = Integer.parseInt(record[0]);
                if (currentId == id) {
                    // Assuming TvSeriesBean constructor: public TvSeriesBean(int id, int episodeRuntime, int numberOfEpisodes, String name)
                    return new TvSeriesBean(currentId, Integer.parseInt(record[1]), Integer.parseInt(record[2]), record[3]);
                }
            }
        }
        return null;
    }

    @Override
    public boolean saveTvSeries(TvSeriesBean tvSeries) throws ExceptionDao { // Changed to throw ExceptionDao
        int tvSeriesId = tvSeries.getIdTvSeriesTmdb();

        // Check if ID already exists in cache or file (to prevent duplicates)
        synchronized (localCache) {
            if (localCache.containsKey(tvSeriesId)) {
                throw new ExceptionDao("Duplicated TV Series ID already in cache: " + tvSeriesId);
            }
        }

        TvSeriesBean existingTvSeries = null;
        try {
            existingTvSeries = retrieveByIdFromFile(tvSeriesId);
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error checking existing TV Series in CSV for ID: " + tvSeriesId, e);
            throw new ExceptionDao("Failed to check existing TV Series for ID: " + tvSeriesId + ". Data corruption or I/O error.", e);
        }

        if (existingTvSeries != null) {
            throw new ExceptionDao("Duplicated TV Series ID already exists in CSV file: " + tvSeriesId);
        }

        // If it doesn't exist, save to file and add to cache
        try {
            saveTvSeriesToFile(tvSeries);
        } catch (IOException | CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "Error saving TV Series to CSV file: " + tvSeriesId, e);
            throw new ExceptionDao("Failed to save TV Series to CSV for ID: " + tvSeriesId + ". I/O or data error.", e);
        }

        synchronized (localCache) {
            localCache.put(tvSeriesId, tvSeries);
        }

        return true;
    }

    private void saveTvSeriesToFile(TvSeriesBean tvSeries) throws IOException, CsvValidationException {
        // Using StandardOpenOption.APPEND for appending and newBufferedWriter for efficiency
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
            String[] record = {
                    String.valueOf(tvSeries.getIdTvSeriesTmdb()),
                    String.valueOf(tvSeries.getEpisodeRuntime()),
                    String.valueOf(tvSeries.getNumberOfEpisodes()),
                    tvSeries.getName()
            };
            csvWriter.writeNext(record);
        }
    }

    @Override
    public List<TvSeriesBean> retrieveAllTvSeries() throws ExceptionDao { // Changed to throw ExceptionDao
        List<TvSeriesBean> tvSeriesList = null;
        try {
            tvSeriesList = retrieveAllTvSeriesFromFile();
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error reading all TV Series from CSV file", e);
            throw new ExceptionDao("Failed to retrieve all TV Series from CSV. Data corruption or I/O error.", e);
        }

        // Load into cache if successful
        synchronized (localCache) {
            // Clear existing cache to ensure it's fresh from the file
            localCache.clear();
            for (TvSeriesBean tvSeries : tvSeriesList) {
                localCache.put(tvSeries.getIdTvSeriesTmdb(), tvSeries);
            }
        }

        if (tvSeriesList.isEmpty()) {
            // It might be valid for the file to be empty, depending on your business rules.
            // Throwing an exception here might not always be desired.
            // Consider returning an empty list instead or a specific "no data" exception if critical.
            throw new ExceptionDao("No TV Series Found in CSV file.");
        }

        return Collections.unmodifiableList(tvSeriesList); // Return an unmodifiable list
    }

    private List<TvSeriesBean> retrieveAllTvSeriesFromFile() throws IOException, CsvValidationException, NumberFormatException {
        List<TvSeriesBean> tvSeriesList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 4) {
                    LOGGER.log(Level.WARNING, "Skipping malformed CSV record for retrieveAllTvSeriesFromFile: {0}", String.join(",", record));
                    continue; // Skip invalid records
                }
                // Assuming TvSeriesBean constructor: public TvSeriesBean(int id, int episodeRuntime, int numberOfEpisodes, String name)
                tvSeriesList.add(new TvSeriesBean(Integer.parseInt(record[0]), Integer.parseInt(record[1]), Integer.parseInt(record[2]), record[3]));
            }
        }
        return tvSeriesList;
    }
}