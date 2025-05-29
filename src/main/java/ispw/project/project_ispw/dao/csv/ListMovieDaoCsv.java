package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.exception.CsvDaoException;
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

public class ListMovieDaoCsv implements ListMovie {

    private static final Logger LOGGER = Logger.getLogger(ListMovieDaoCsv.class.getName());

    private static final String CSV_FILE_NAME;
    private static final String MOVIE_CSV_FILE_NAME;

    static {
        Properties properties = new Properties();
        String listMovieFileName = "list_movie.csv";
        String movieFileName = "movie.csv";

        try (InputStream input = ListMovieDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                listMovieFileName = properties.getProperty("list_movie.csv.filename", listMovieFileName);
                movieFileName = properties.getProperty("movie.csv.filename", movieFileName);
            } else {
                LOGGER.log(Level.WARNING, "csv.properties file not found. Using default CSV filenames: list_movie.csv=''{0}'', movie.csv=''{1}''",
                        new Object[]{listMovieFileName, movieFileName});
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load csv.properties. Using default CSV filenames: list_movie.csv=''{0}'', movie.csv=''{1}''. Error: {2}",
                    new Object[]{listMovieFileName, movieFileName, e.getMessage()});
        }

        CSV_FILE_NAME = listMovieFileName;
        MOVIE_CSV_FILE_NAME = movieFileName;

        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
            if (!Files.exists(Paths.get(MOVIE_CSV_FILE_NAME))) {
                Files.createFile(Paths.get(MOVIE_CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new CsvDaoException("Initialization failed: Could not create CSV files.", e);
        }
    }

    public ListMovieDaoCsv() {
        //Empty constructor
    }

    @Override
    public void addMovieToList(ListBean list, MovieBean movie) throws ExceptionDao {
        try {
            if (movieExistsInList(list, movie)) {
                throw new ExceptionDao("Movie ID " + movie.getIdMovieTmdb() + " already exists in list ID " + list.getId() + ".");
            }

            try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
                String[] recordListMovie = {String.valueOf(list.getId()), String.valueOf(movie.getIdMovieTmdb())};
                csvWriter.writeNext(recordListMovie);
            }
        } catch (IOException e) {
            throw new ExceptionDao("Failed to add movie to list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public void removeMovieFromList(ListBean list, MovieBean movie) throws ExceptionDao {
        java.nio.file.Path originalPath = Paths.get(CSV_FILE_NAME);
        java.nio.file.Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");
        List<String[]> allRecords = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(originalPath));
             CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(tempPath))) {

            if (!movieExistsInList(list, movie)) {
                throw new ExceptionDao("Movie ID " + movie.getIdMovieTmdb() + " not found in list ID " + list.getId() + ".");
            }

            String[] recordListMovie;
            while ((recordListMovie = csvReader.readNext()) != null) {
                if (recordListMovie.length < 2) {
                    continue;
                }
                if (!(recordListMovie[0].equals(String.valueOf(list.getId())) && recordListMovie[1].equals(String.valueOf(movie.getIdMovieTmdb())))) {
                    allRecords.add(recordListMovie);
                }
            }
            csvWriter.writeAll(allRecords);
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to remove movie from list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public List<MovieBean> getAllMoviesInList(ListBean list) throws ExceptionDao {
        List<MovieBean> movieList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordListMovie;
            while ((recordListMovie = csvReader.readNext()) != null) {
                if (recordListMovie.length < 2) {
                    continue;
                }
                if (recordListMovie[0].equals(String.valueOf(list.getId()))) {
                    MovieBean movie = processListMovieRecordAndFetchMovie(recordListMovie, list.getId());
                    if (movie != null) {
                        movieList.add(movie);
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to retrieve all movies for list from CSV. Data corruption or I/O error.", e);
        }

        return movieList;
    }

    private MovieBean processListMovieRecordAndFetchMovie(String[] recordListMovie, int listId) throws ExceptionDao {
        try {
            int movieId = Integer.parseInt(recordListMovie[1]);
            MovieBean movie = fetchMovieById(movieId);
            if (movie != null) {
                return movie;
            } else {
                LOGGER.log(Level.WARNING, "Movie with ID {0} found in list ID {1}, but details not found in movie.csv. Skipping this entry. Record: {2}",
                        new Object[]{movieId, listId, java.util.Arrays.toString(recordListMovie)});
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping malformed list-movie record in CSV due to invalid movie ID format. Record: {0}, Error: {1}",
                    new Object[]{java.util.Arrays.toString(recordListMovie), e.getMessage()});
        }
        return null;
    }

    @Override
    public void removeAllMoviesFromList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        java.nio.file.Path originalPath = Paths.get(CSV_FILE_NAME);
        java.nio.file.Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");
        List<String[]> allRecordsToKeep = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(originalPath));
             CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(tempPath))) {

            String[] recordListMovie;
            while ((recordListMovie = csvReader.readNext()) != null) {
                if (recordListMovie.length < 2) {
                    continue;
                }
                if (!recordListMovie[0].equals(String.valueOf(list.getId()))) {
                    allRecordsToKeep.add(recordListMovie);
                }
            }
            csvWriter.writeAll(allRecordsToKeep);
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to remove all movies from list in CSV. I/O or data error.", e);
        }
    }

    private boolean movieExistsInList(ListBean list, MovieBean movie) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] recordListMovie;
            while ((recordListMovie = csvReader.readNext()) != null) {
                if (recordListMovie.length < 2) {
                    continue;
                }
                if (recordListMovie[0].equals(String.valueOf(list.getId())) && recordListMovie[1].equals(String.valueOf(movie.getIdMovieTmdb()))) {
                    return true;
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to check movie existence in list from CSV. I/O or data error.", e);
        }
        return false;
    }

    private MovieBean fetchMovieById(int id) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(MOVIE_CSV_FILE_NAME)))) {
            String[] recordMovie;
            while ((recordMovie = csvReader.readNext()) != null) {
                if (recordMovie.length < 3) {
                    continue;
                }
                MovieBean movie = parseAndMatchMovieRecord(recordMovie, id);
                if (movie != null) {
                    return movie;
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to fetch movie details from main movie CSV file. I/O or data error.", e);
        }
        return null;
    }

    private MovieBean parseAndMatchMovieRecord(String[] recordMovie, int targetId) {
        try {
            int currentId = Integer.parseInt(recordMovie[0]);
            if (currentId == targetId) {
                int runtime = Integer.parseInt(recordMovie[1]);
                String title = recordMovie[2];
                return new MovieBean(currentId, runtime, title);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Skipping malformed movie record in CSV. Expected numeric ID or runtime, but found invalid data. Record: {0}, Error: {1}",
                    new Object[]{java.util.Arrays.toString(recordMovie), e.getMessage()});
        }
        return null;
    }
}