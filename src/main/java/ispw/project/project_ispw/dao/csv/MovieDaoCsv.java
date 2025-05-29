package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.dao.MovieDao;
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

public class MovieDaoCsv implements MovieDao {

    private static final String CSV_FILE_NAME;

    // Use a robust, non-static cache for thread safety and consistency.
    // For simplicity, I'll keep it as instance cache as it's typically managed.
    private final HashMap<Integer, MovieBean> localCache;

    static {
        // This static block runs once when the class is loaded
        Properties properties = new Properties();
        String fileName = "movie.csv"; // Default

        try (InputStream input = MovieDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("movie.csv.filename", fileName);
            } else {
                // csv.properties file not found, using default CSV filename.
            }
        } catch (IOException e) {
            throw new RuntimeException("Initialization failed: Error loading csv.properties.", e); // Critical startup error
        }
        CSV_FILE_NAME = fileName;

        // Ensure the file exists right at the start
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
        } catch (IOException e) {
            // If file creation fails, this is a critical error for the DAO
            throw new RuntimeException("Initialization failed: Could not create CSV file for movies.", e);
        }
    }

    public MovieDaoCsv() {
        this.localCache = new HashMap<>();
        // Optionally load all data into cache on startup if file is small.
        // For this example, retrieveAllMovies will handle loading into cache on first call if not already there.
    }

    @Override
    public MovieBean retrieveById(int id) throws ExceptionDao {
        // First, check cache
        synchronized (localCache) {
            if (localCache.containsKey(id)) {
                return localCache.get(id);
            }
        }

        // If not in cache, read from file
        MovieBean movie = null;
        try {
            movie = retrieveByIdFromFile(id);
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve movie from CSV for ID: " + id + ". Data corruption or I/O error.", e);
        }

        if (movie != null) {
            // Add to cache
            synchronized (localCache) {
                localCache.put(id, movie);
            }
        } else {
            // If not found in file either
            throw new ExceptionDao("No Movie Found with ID: " + id);
        }
        return movie;
    }

    private MovieBean retrieveByIdFromFile(int id) throws IOException, CsvValidationException, NumberFormatException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 3) {
                    continue; // Skip malformed records
                }
                int currentId = Integer.parseInt(record[0]);
                if (currentId == id) {
                    return new MovieBean(currentId, Integer.parseInt(record[1]), record[2]); // id, runtime, title
                }
            }
        }
        return null;
    }

    @Override
    public void saveMovie(MovieBean movie) throws ExceptionDao {
        // Corrected: Use getIdMovieTmdb()
        int movieId = movie.getIdMovieTmdb();

        // Check if ID already exists in cache or file (to prevent duplicates)
        synchronized (localCache) {
            if (localCache.containsKey(movieId)) {
                throw new ExceptionDao("Duplicated Movie ID already in cache: " + movieId);
            }
        }

        MovieBean existingMovie = null;
        try {
            existingMovie = retrieveByIdFromFile(movieId);
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            throw new ExceptionDao("Failed to check existing movie for ID: " + movieId + ". Data corruption or I/O error.", e);
        }

        if (existingMovie != null) {
            throw new ExceptionDao("Duplicated Movie ID already exists in CSV file: " + movieId);
        }

        // If it doesn't exist, save to file and add to cache
        try {
            saveMovieToFile(movie);
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to save movie to CSV for ID: " + movieId + ". I/O or data error.", e);
        }

        synchronized (localCache) {
            localCache.put(movieId, movie);
        }
    }

    private void saveMovieToFile(MovieBean movie) throws IOException, CsvValidationException {
        // Using StandardOpenOption.APPEND for appending and newBufferedWriter for efficiency
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
            // Corrected: Use getIdMovieTmdb()
            String[] record = {String.valueOf(movie.getIdMovieTmdb()), String.valueOf(movie.getRuntime()), movie.getTitle()};
            csvWriter.writeNext(record);
        }
    }

    @Override
    public List<MovieBean> retrieveAllMovies() throws ExceptionDao {
        List<MovieBean> movieList = null;
        try {
            movieList = retrieveAllMoviesFromFile();
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve all movies from CSV. Data corruption or I/O error.", e);
        }

        // Load into cache if successful
        synchronized (localCache) {
            // Clear existing cache to ensure it's fresh from the file
            localCache.clear();
            for (MovieBean movie : movieList) {
                // Corrected: Use getIdMovieTmdb() for the cache key
                localCache.put(movie.getIdMovieTmdb(), movie);
            }
        }

        if (movieList.isEmpty()) {
            // It might be valid for the file to be empty, depending on your business rules.
            // Throwing an exception here might not always be desired.
            // Consider returning an empty list instead or a specific "no data" exception if critical.
            throw new ExceptionDao("No Movies Found in CSV file.");
        }

        return Collections.unmodifiableList(movieList); // Return an unmodifiable list
    }

    private List<MovieBean> retrieveAllMoviesFromFile() throws IOException, CsvValidationException, NumberFormatException {
        List<MovieBean> movieList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 3) {
                    continue; // Skip malformed records
                }
                movieList.add(new MovieBean(Integer.parseInt(record[0]), Integer.parseInt(record[1]), record[2])); // id, runtime, title
            }
        }
        return movieList;
    }
}