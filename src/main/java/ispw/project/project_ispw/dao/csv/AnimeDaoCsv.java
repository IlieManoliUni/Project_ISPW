package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom DAO exception

import java.io.*;
import java.nio.file.Files; // For new way to handle file existence
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections; // For unmodifiable list
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnimeDaoCsv implements AnimeDao {

    private static final Logger LOGGER = Logger.getLogger(AnimeDaoCsv.class.getName());
    private static final String CSV_FILE_NAME;

    // Use a robust, non-static cache if multiple instances of AnimeDaoCsv are possible
    // For a simple single instance scenario, static is fine.
    // However, if the file changes externally, the cache will be stale.
    // For simplicity, I'll keep it as instance cache as it's typically managed.
    private final HashMap<Integer, AnimeBean> localCache;

    static {
        // This static block runs once when the class is loaded
        CSV_FILE_NAME = loadCsvFileName();
        // Ensure the file exists right at the start
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
                LOGGER.log(Level.INFO, "CSV file created: {0}", CSV_FILE_NAME);
            }
        } catch (IOException e) {
            // If file creation fails, this is a critical error for the DAO
            LOGGER.log(Level.SEVERE, "Failed to create CSV file: " + CSV_FILE_NAME, e);
            throw new RuntimeException("Initialization failed: Could not create CSV file.", e);
        }
    }

    // Constructor no longer needs to create the file, static block handles it
    public AnimeDaoCsv() {
        this.localCache = new HashMap<>();
        // Optionally load all data into cache on startup if file is small
        // For large files, load on demand or partially.
        // For this example, retrieveAllAnime will handle loading into cache on first call if not already there.
    }

    private static String loadCsvFileName() {
        Properties properties = new Properties();
        try (InputStream input = AnimeDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input == null) {
                // Better to throw a specific runtime exception here, as the DAO can't function without config
                throw new IllegalStateException("csv.properties file not found in resources folder. Cannot initialize CSV DAO.");
            }
            properties.load(input);
            // Provide a sensible default if property is missing, but log it.
            String filename = properties.getProperty("anime.csv.filename");
            if (filename == null || filename.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Property 'anime.csv.filename' not found or is empty in csv.properties. Using default: anime.csv");
                filename = "anime.csv";
            }
            return filename;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading csv.properties", e);
            throw new RuntimeException("Initialization failed: Error loading csv.properties.", e);
        }
    }

    @Override
    public AnimeBean retrieveById(int id) throws ExceptionDao {
        // First, check cache
        synchronized (localCache) {
            if (localCache.containsKey(id)) {
                return localCache.get(id);
            }
        }

        // If not in cache, read from file
        AnimeBean anime = null;
        try {
            anime = retrieveByIdFromFile(id);
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error reading CSV file or parsing data for ID: " + id, e);
            throw new ExceptionDao("Failed to retrieve anime from CSV for ID: " + id + ". Data corruption or I/O error.", e);
        }

        if (anime != null) {
            // Add to cache
            synchronized (localCache) {
                localCache.put(id, anime);
            }
        } else {
            // If not found in file either
            throw new ExceptionDao("No Anime Found with ID: " + id);
        }

        return anime;
    }

    // This method handles low-level file reading and parsing
    private static AnimeBean retrieveByIdFromFile(int id) throws IOException, NumberFormatException {
        // Using Files.newBufferedReader provides more options for encoding if needed
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 4) {
                    LOGGER.log(Level.WARNING, "Skipping malformed CSV record: {0}", String.join(",", record));
                    continue; // Skip invalid records
                }
                int currentId = Integer.parseInt(record[0]); // Throws NumberFormatException
                if (currentId == id) {
                    // id, duration, episodes, title -> record[0], record[2], record[1], record[3] (based on your current mapping)
                    return new AnimeBean(currentId, Integer.parseInt(record[2]), Integer.parseInt(record[1]), record[3]);
                }
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void saveAnime(AnimeBean anime) throws ExceptionDao {
        int animeId = anime.getIdAnimeTmdb();

        // Check if ID already exists in cache or file (to prevent duplicates)
        synchronized (localCache) {
            if (localCache.containsKey(animeId)) {
                throw new ExceptionDao("Duplicated Anime ID already in cache: " + animeId);
            }
        }

        // Must check file as well, as cache might not be fully loaded
        AnimeBean existingAnime = null;
        try {
            existingAnime = retrieveByIdFromFile(animeId);
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error checking existing anime in CSV for ID: " + animeId, e);
            throw new ExceptionDao("Failed to check existing anime for ID: " + animeId + ". Data corruption or I/O error.", e);
        }

        if (existingAnime != null) {
            throw new ExceptionDao("Duplicated Anime ID already exists in CSV file: " + animeId);
        }

        // If it doesn't exist, save to file and add to cache
        try {
            saveAnimeToFile(anime);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving anime to CSV file: " + animeId, e);
            throw new ExceptionDao("Failed to save anime to CSV for ID: " + animeId + ". I/O error.", e);
        }

        synchronized (localCache) {
            localCache.put(animeId, anime);
        }
    }

    // This method handles low-level file writing
    private static void saveAnimeToFile(AnimeBean anime) throws IOException {
        // FileWriter with 'true' appends to the file
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), java.nio.file.StandardOpenOption.APPEND))) {
            String[] record = {
                    String.valueOf(anime.getIdAnimeTmdb()),
                    String.valueOf(anime.getEpisodes()), // Note: Your original mapping was duration then episodes
                    String.valueOf(anime.getDuration()), // Corrected order based on your bean constructor mapping below
                    anime.getTitle()
            };
            csvWriter.writeNext(record);
            // You might need to flush or close the writer to ensure data is written immediately
            // try-with-resources handles close(), which usually flushes.
        }
    }

    @Override
    public List<AnimeBean> retrieveAllAnime() throws ExceptionDao {
        List<AnimeBean> animeList = null;
        try {
            animeList = retrieveAllAnimeFromFile();
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error reading all animes from CSV file", e);
            throw new ExceptionDao("Failed to retrieve all animes from CSV. Data corruption or I/O error.", e);
        }


        // Load into cache if successful
        synchronized (localCache) {
            // Clear existing cache to ensure it's fresh from the file
            localCache.clear();
            for (AnimeBean anime : animeList) {
                localCache.put(anime.getIdAnimeTmdb(), anime);
            }
        }

        if (animeList.isEmpty()) {
            // It might be valid for the file to be empty, depending on your business rules.
            // Throwing an exception here might not always be desired.
            // Consider returning an empty list instead or a specific "no data" exception if critical.
            throw new ExceptionDao("No Anime Found in CSV file.");
        }

        return Collections.unmodifiableList(animeList); // Return an unmodifiable list
    }

    // This method handles low-level file reading for all records
    private static List<AnimeBean> retrieveAllAnimeFromFile() throws IOException, NumberFormatException {
        List<AnimeBean> animeList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 4) {
                    LOGGER.log(Level.WARNING, "Skipping malformed CSV record: {0}", String.join(",", record));
                    continue; // Skip invalid records
                }
                // id, duration, episodes, title -> record[0], record[2], record[1], record[3] (based on your original mapping)
                animeList.add(new AnimeBean(Integer.parseInt(record[0]), Integer.parseInt(record[2]), Integer.parseInt(record[1]), record[3]));
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return animeList;
    }
}