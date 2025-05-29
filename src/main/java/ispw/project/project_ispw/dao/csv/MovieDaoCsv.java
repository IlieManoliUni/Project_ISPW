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

public class MovieDaoCsv implements MovieDao {

    private static final String CSV_FILE_NAME;

    private final HashMap<Integer, MovieBean> localCache;

    static {
        Properties properties = new Properties();
        String fileName = "movie.csv";

        try (InputStream input = MovieDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                fileName = properties.getProperty("movie.csv.filename", fileName);
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
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            throw new ExceptionDao("Failed to retrieve movie from CSV for ID: " + id + ". Data corruption or I/O error.", e);
        }

        if (movie != null) {
            synchronized (localCache) {
                localCache.put(id, movie);
            }
        } else {
            throw new ExceptionDao("No Movie Found with ID: " + id);
        }
        return movie;
    }

    private MovieBean retrieveByIdFromFile(int id) throws IOException, CsvValidationException, NumberFormatException {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordMovie;
            while ((recordMovie = csvReader.readNext()) != null) {
                if (recordMovie.length < 3) {
                    continue;
                }
                int currentId = Integer.parseInt(recordMovie[0]);
                if (currentId == id) {
                    return new MovieBean(currentId, Integer.parseInt(recordMovie[1]), recordMovie[2]);
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
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            throw new ExceptionDao("Failed to check existing movie for ID: " + movieId + ". Data corruption or I/O error.", e);
        }

        if (existingMovie != null) {
            throw new ExceptionDao("Duplicated Movie ID already exists in CSV file: " + movieId);
        }

        try {
            saveMovieToFile(movie);
        } catch (IOException e) {
            throw new ExceptionDao("Failed to save movie to CSV for ID: " + movieId + ". I/O or data error.", e);
        }

        synchronized (localCache) {
            localCache.put(movieId, movie);
        }
    }

    private void saveMovieToFile(MovieBean movie) throws IOException{
        try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
            String[] recordMovie = {String.valueOf(movie.getIdMovieTmdb()), String.valueOf(movie.getRuntime()), movie.getTitle()};
            csvWriter.writeNext(recordMovie);
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

        synchronized (localCache) {
            localCache.clear();
            for (MovieBean movie : movieList) {
                localCache.put(movie.getIdMovieTmdb(), movie);
            }
        }

        if (movieList.isEmpty()) {
            throw new ExceptionDao("No Movies Found in CSV file.");
        }

        return Collections.unmodifiableList(movieList);
    }

    private List<MovieBean> retrieveAllMoviesFromFile() throws IOException, CsvValidationException, NumberFormatException {
        List<MovieBean> movieList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordMovie;
            while ((recordMovie = csvReader.readNext()) != null) {
                if (recordMovie.length < 3) {
                    continue;
                }
                movieList.add(new MovieBean(Integer.parseInt(recordMovie[0]), Integer.parseInt(recordMovie[1]), recordMovie[2]));
            }
        }
        return movieList;
    }
}