package ispw.project.project_ispw.dao.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.dao.ListMovie;
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

public class ListMovieDaoCsv implements ListMovie {

    private static final String CSV_FILE_NAME;
    private static final String MOVIE_CSV_FILE_NAME; // To get movie details

    // Static block to load CSV file names and ensure files exist
    static {
        Properties properties = new Properties();
        String listMovieFileName = "list_movie.csv"; // Default
        String movieFileName = "movie.csv"; // Default for fetching movie details

        try (InputStream input = ListMovieDaoCsv.class.getClassLoader().getResourceAsStream("csv.properties")) {
            if (input != null) {
                properties.load(input);
                listMovieFileName = properties.getProperty("list_movie.csv.filename", listMovieFileName);
                movieFileName = properties.getProperty("movie.csv.filename", movieFileName);
            } else {
                // csv.properties file not found, using default CSV filenames.
            }
        } catch (IOException e) {
            // Error loading properties, using default filenames.
        }

        CSV_FILE_NAME = listMovieFileName;
        MOVIE_CSV_FILE_NAME = movieFileName;

        // Ensure both files exist
        try {
            if (!Files.exists(Paths.get(CSV_FILE_NAME))) {
                Files.createFile(Paths.get(CSV_FILE_NAME));
            }
            if (!Files.exists(Paths.get(MOVIE_CSV_FILE_NAME))) {
                Files.createFile(Paths.get(MOVIE_CSV_FILE_NAME));
            }
        } catch (IOException e) {
            throw new RuntimeException("Initialization failed: Could not create CSV files.", e);
        }
    }

    public ListMovieDaoCsv() {
        // Constructor no longer needs to create the file; the static block handles it.
    }

    @Override
    public void addMovieToList(ListBean list, MovieBean movie) throws ExceptionDao {
        try {
            // Check if the movie already exists in the list
            if (movieExistsInList(list, movie)) {
                throw new ExceptionDao("Movie ID " + movie.getIdMovieTmdb() + " already exists in list ID " + list.getId() + ".");
            }

            // Add the movie to the CSV file
            try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(Paths.get(CSV_FILE_NAME), StandardOpenOption.APPEND))) {
                String[] record = {String.valueOf(list.getId()), String.valueOf(movie.getIdMovieTmdb())};
                csvWriter.writeNext(record);
            }
        } catch (IOException e) {
            throw new ExceptionDao("Failed to add movie to list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public void removeMovieFromList(ListBean list, MovieBean movie) throws ExceptionDao {
        try {
            // Check if the movie exists in the list
            if (!movieExistsInList(list, movie)) {
                throw new ExceptionDao("Movie ID " + movie.getIdMovieTmdb() + " not found in list ID " + list.getId() + ".");
            }

            java.nio.file.Path originalPath = Paths.get(CSV_FILE_NAME);
            java.nio.file.Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");
            List<String[]> allRecords = new ArrayList<>();

            // Read all records, excluding the one to be removed
            try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(originalPath))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    if (record.length < 2) {
                        continue; // Skip malformed records
                    }
                    if (!(record[0].equals(String.valueOf(list.getId())) && record[1].equals(String.valueOf(movie.getIdMovieTmdb())))) {
                        allRecords.add(record);
                    }
                }
            }

            // Write the updated records back to a temporary file
            try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(tempPath))) {
                csvWriter.writeAll(allRecords);
            }

            // Atomically replace the original file with the temporary one
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to remove movie from list in CSV. I/O or data error.", e);
        }
    }

    @Override
    public List<MovieBean> getAllMoviesInList(ListBean list) throws ExceptionDao {
        List<MovieBean> movieList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    continue; // Skip malformed records
                }
                if (record[0].equals(String.valueOf(list.getId()))) {
                    try {
                        int movieId = Integer.parseInt(record[1]);
                        // Fetch Movie details from the movie.csv file
                        MovieBean movie = fetchMovieById(movieId);
                        if (movie != null) {
                            movieList.add(movie);
                        } else {
                            // Movie with ID found in list but not in movie.csv.
                        }
                    } catch (NumberFormatException e) {
                        // Skipping CSV record with invalid movie ID format.
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to retrieve all movies for list from CSV. Data corruption or I/O error.", e);
        }

        if (movieList.isEmpty()) {
            // It might be valid for a list to be empty; throwing an exception here is optional.
            // For now, mirroring previous behavior:
            throw new ExceptionDao("No Movies Found in the List (ID: " + list.getId() + ").");
        }

        return movieList;
    }

    @Override
    public void removeAllMoviesFromList(ListBean list) throws ExceptionDao {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        java.nio.file.Path originalPath = Paths.get(CSV_FILE_NAME);
        java.nio.file.Path tempPath = Paths.get(CSV_FILE_NAME + ".tmp");
        List<String[]> allRecordsToKeep = new ArrayList<>();
        boolean listHadEntries = false; // Flag to check if the list actually contained entries

        try {
            // Read all records, excluding those for the specified list ID
            try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(originalPath))) {
                String[] record;
                while ((record = csvReader.readNext()) != null) {
                    if (record.length < 2) {
                        continue; // Skip malformed records
                    }
                    // Keep records that do NOT match the list ID
                    if (!record[0].equals(String.valueOf(list.getId()))) {
                        allRecordsToKeep.add(record);
                    } else {
                        listHadEntries = true; // Mark that we found entries for this list ID
                    }
                }
            }

            // Write the filtered records back to a temporary file
            try (CSVWriter csvWriter = new CSVWriter(Files.newBufferedWriter(tempPath))) {
                csvWriter.writeAll(allRecordsToKeep);
            }

            // Atomically replace the original file with the temporary one
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);

            if (!listHadEntries) {
                // If the list ID was not found in the CSV, throw an exception
                throw new ExceptionDao("List with ID " + list.getId() + " not found or already empty.");
            }

        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to remove all movies from list in CSV. I/O or data error.", e);
        }
    }


    // Helper method to check if a Movie is already in the List
    private boolean movieExistsInList(ListBean list, MovieBean movie) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 2) {
                    continue; // Skip malformed records
                }
                if (record[0].equals(String.valueOf(list.getId())) && record[1].equals(String.valueOf(movie.getIdMovieTmdb()))) {
                    return true; // Movie already in the list
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to check movie existence in list from CSV. I/O or data error.", e);
        }
        return false; // Movie not in the list
    }

    // Helper method to fetch a Movie by its ID from the "movie.csv" file
    private MovieBean fetchMovieById(int id) throws ExceptionDao {
        try (CSVReader csvReader = new CSVReader(Files.newBufferedReader(Paths.get(MOVIE_CSV_FILE_NAME)))) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 3) { // Assuming ID, runtime, title
                    continue; // Skip malformed Movie CSV records
                }
                try {
                    if (Integer.parseInt(record[0]) == id) {
                        // Assuming MovieBean constructor: public MovieBean(int id, int runtime, String title)
                        return new MovieBean(Integer.parseInt(record[0]), Integer.parseInt(record[1]), record[2]);
                    }
                } catch (NumberFormatException e) {
                    // Skipping CSV record with invalid number format.
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new ExceptionDao("Failed to fetch movie details from main movie CSV file. I/O or data error.", e);
        }
        return null; // Movie not found
    }
}