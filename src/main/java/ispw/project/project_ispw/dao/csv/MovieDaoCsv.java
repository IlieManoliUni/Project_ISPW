package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.exception.CsvDaoException;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieDaoCsv implements MovieDao {

    private static final Logger LOGGER = Logger.getLogger(MovieDaoCsv.class.getName());

    private static final String CSV_FILE_NAME;

    private final HashMap<Integer, MovieBean> localCache;

    static {
        Properties properties = new Properties();
        String fileName = "movie.csv";

        try (InputStream input = MovieDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("movie.csv.filename", fileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default filename: {0}", fileName);
            }
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Error loading csv.properties.", e);
        }
        CSV_FILE_NAME = fileName;

        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Could not create CSV file for movies.", e);
        }
    }

    public MovieDaoCsv() {
        this.localCache = new HashMap<>();
    }

    @Override
    public MovieBean retrieveById(int id) throws ExceptionDao {
        synchronized (localCache) {
            if (localCache.containsKey(id)) {
                return localCache.get(id);
            }
        }

        MovieBean movie = null;
        try {
            movie = retrieveByIdFromFile(id);
        } catch (IOException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve movie from CSV for ID: " + id + ". Data corruption or I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error while retrieving movie for ID: " + id, e);
        }

        if (movie != null) {
            synchronized (localCache) {
                localCache.put(id, movie);
            }
        }
        return movie;
    }

    private MovieBean retrieveByIdFromFile(int id) throws IOException, CsvValidationException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordMovie;
            while ((recordMovie = csvReader.readNext()) != null) {
                if (recordMovie.length < 3) {
                    continue;
                }
                try {
                    int currentId = Integer.parseInt(recordMovie[0]);
                    if (currentId == id) {
                        return new MovieBean(currentId, Integer.parseInt(recordMovie[1]), recordMovie[2]);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Skipping malformed movie record during retrieveByIdFromFile due to invalid ID/runtime format. Record: {0}, Error: {1}",
                            new Object[]{java.util.Arrays.toString(recordMovie), e.getMessage()});
                }
            }
        }
        return null;
    }

    @Override
    public void saveMovie(MovieBean movie) throws ExceptionDao {
        int movieId = movie.getIdMovieTmdb();

        synchronized (localCache) {
            if (localCache.containsKey(movieId)) {
                throw new ExceptionDao("Duplicated Movie ID already in cache: " + movieId);
            }
        }

        MovieBean existingMovie = null;
        try {
            existingMovie = retrieveByIdFromFile(movieId);
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to check existing movie for ID: " + movieId + ". I/O error.", e);
        } catch (NumberFormatException e) {
            throw new ExceptionDao("Data corruption while checking existing movie for ID: " + movieId + ". Invalid number format.", e);
        }

        if (existingMovie != null) {
            throw new ExceptionDao("Duplicated Movie ID already exists in CSV file: " + movieId);
        }

        try {
            saveMovieToFile(movie);
        } catch (IOException e) {
            throw new ExceptionDao("Failed to save movie to CSV for ID: " + movieId + ". I/O error.", e);
        }

        synchronized (localCache) {
            localCache.put(movieId, movie);
        }
    }

    private void saveMovieToFile(MovieBean movie) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
            String[] recordMovie = {String.valueOf(movie.getIdMovieTmdb()), String.valueOf(movie.getRuntime()), movie.getTitle()};
            csvWriter.writeNext(recordMovie);
        }
    }

    @Override
    public List<MovieBean> retrieveAllMovies() throws ExceptionDao {
        List<MovieBean> movieList = new ArrayList<>();
        try {
            movieList = retrieveAllMoviesFromFile();
        } catch (IOException e) {
            throw new ExceptionDao("Failed to retrieve all movies from CSV. I/O error.", e);
        } catch (CsvValidationException e) {
            throw new CsvDaoException("CSV data validation error while retrieving all movies.", e);
        } catch (NumberFormatException e) {
            throw new ExceptionDao("Data corruption while retrieving all movies. Invalid number format.", e);
        }

        synchronized (localCache) {
            localCache.clear();
            for (MovieBean movie : movieList) {
                localCache.put(movie.getIdMovieTmdb(), movie);
            }
        }

        return Collections.unmodifiableList(movieList);
    }

    private List<MovieBean> retrieveAllMoviesFromFile() throws IOException, CsvValidationException {
        List<MovieBean> movieList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordMovie;
            while ((recordMovie = csvReader.readNext()) != null) {
                if (recordMovie.length < 3) {
                    continue;
                }
                try {
                    movieList.add(new MovieBean(Integer.parseInt(recordMovie[0]), Integer.parseInt(recordMovie[1]), recordMovie[2]));
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Skipping malformed movie record during retrieveAllMoviesFromFile due to invalid ID/runtime format. Record: {0}, Error: {1}",
                            new Object[]{java.util.Arrays.toString(recordMovie), e.getMessage()});
                }
            }
        }
        return movieList;
    }
}