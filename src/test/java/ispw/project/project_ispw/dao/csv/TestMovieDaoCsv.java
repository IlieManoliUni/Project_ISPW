package ispw.project.project_ispw.dao.csv;

import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.exception.CsvDaoException;
import ispw.project.project_ispw.exception.ExceptionDao;
import org.junit.jupiter.api.*;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MovieDaoCsv Test Suite")
class TestMovieDaoCsv {

    // IMPORTANT: This reflection approach is generally discouraged for production code.
    // It's a workaround for testing legacy code with poor testability (static final fields).
    // The ideal solution is to refactor MovieDaoCsv to accept the file path in its constructor,
    // as suggested in the previous detailed response.
    private static final String TEMP_CSV_FILE_NAME = "test_movie_dao.csv"; // Renamed for clarity
    private static Path tempCsvPath; // Static path to the shared temporary file

    private MovieDaoCsv movieDaoCsv;

    @BeforeAll
    static void setUpClass() throws Exception {
        // Create the Path object for the temporary file
        tempCsvPath = Paths.get(TEMP_CSV_FILE_NAME);

        // 1. Ensure the file doesn't exist from previous runs
        Files.deleteIfExists(tempCsvPath);

        // 2. Modify the static final CSV_FILE_NAME field using reflection
        // This is necessary because MovieDaoCsv uses a static final field which is initialized once.
        try {
            Field csvField = MovieDaoCsv.class.getDeclaredField("CSV_FILE_NAME");
            csvField.setAccessible(true);

            // Remove FINAL modifier to allow modification
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(csvField, csvField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);

            // Set the static field to point to our temporary test file
            csvField.set(null, TEMP_CSV_FILE_NAME);

            // Re-add FINAL modifier (optional, but good practice if you don't need further modification)
            modifiersField.setInt(csvField, csvField.getModifiers() | java.lang.reflect.Modifier.FINAL);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Log or rethrow as a test setup error
            System.err.println("Failed to modify static final CSV_FILE_NAME via reflection: " + e.getMessage());
            throw e; // Fail the test setup if reflection fails
        }
    }

    @AfterAll
    static void tearDownClass() throws IOException {
        // Clean up the temporary file after all tests are done
        Files.deleteIfExists(tempCsvPath);
    }

    @BeforeEach
    void setUp() throws IOException, CsvDaoException {
        // IMPORTANT: Clear the content of the shared CSV file before EACH test
        // This ensures test isolation, as the DAO operates on a single file across all tests.
        Files.write(tempCsvPath, new byte[0], StandardOpenOption.TRUNCATE_EXISTING); // Truncate to make it empty

        // Re-instantiate the DAO for each test, ensuring its internal cache is also fresh.
        // The DAO constructor will now operate on the `tempCsvPath` due to the @BeforeAll reflection hack.
        movieDaoCsv = new MovieDaoCsv();
    }

    // Helper method to write a movie to the CSV file directly (bypassing DAO for setup if needed)
    private void writeMovieToCsvDirectly(MovieBean movie) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(tempCsvPath, StandardOpenOption.APPEND);
             PrintWriter printWriter = new PrintWriter(writer)) {
            // Using a simple comma-separated format for direct write, matching what MovieDaoCsv expects
            printWriter.printf("%d,%d,%s%n", movie.getIdMovieTmdb(), movie.getRuntime(), movie.getTitle());
        }
    }

    // --- Tests for retrieveById ---

    @Test
    @DisplayName("retrieveById - Should retrieve an existing movie by ID")
    void testRetrieveById_ExistingMovie() throws ExceptionDao{
        // Arrange
        MovieBean movieToSave = new MovieBean(1234, 120, "Inception");
        movieDaoCsv.saveMovie(movieToSave); // Use DAO to save, populates cache and file

        // Act
        MovieBean retrievedMovie = movieDaoCsv.retrieveById(1234);

        // Assert
        assertNotNull(retrievedMovie, "Retrieved movie should not be null");
        assertEquals(movieToSave.getIdMovieTmdb(), retrievedMovie.getIdMovieTmdb());
        assertEquals(movieToSave.getRuntime(), retrievedMovie.getRuntime());
        assertEquals(movieToSave.getTitle(), retrievedMovie.getTitle());
        assertEquals(movieToSave, retrievedMovie, "Retrieved movie object should be equal to the saved one");
    }

    @Test
    @DisplayName("retrieveById - Should return null for non-existent movie ID")
    void testRetrieveById_NonExistentMovie() throws ExceptionDao {
        // Arrange: CSV is empty (due to @BeforeEach cleanup)

        // Act
        MovieBean retrievedMovie = movieDaoCsv.retrieveById(9999);

        // Assert
        assertNull(retrievedMovie, "Should return null for a non-existent movie ID");
    }

    @Test
    @DisplayName("retrieveById - Should retrieve from cache if already loaded")
    void testRetrieveById_FromCache() throws ExceptionDao {
        // Arrange: Save a movie, which puts it in both file and cache
        MovieBean movieInCache = new MovieBean(500, 95, "Cached Movie");
        movieDaoCsv.saveMovie(movieInCache);

        // Act: Retrieve again - should hit cache
        MovieBean retrievedFromCache = movieDaoCsv.retrieveById(500);

        // Assert
        assertNotNull(retrievedFromCache);
        assertEquals(movieInCache, retrievedFromCache, "Movie should be retrieved from cache");
    }

    @Test
    @DisplayName("retrieveById - Malformed ID in CSV should throw ExceptionDao")
    void testRetrieveById_MalformedIdThrowsExceptionDao() throws IOException {
        // Arrange: Write a malformed record (non-numeric ID)
        Files.write(tempCsvPath, "abc,120,Malformed Movie\n".getBytes(), StandardOpenOption.APPEND);

        // Act & Assert
        ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> movieDaoCsv.retrieveById(1),
                "Should throw ExceptionDao for malformed ID");
        assertTrue(thrown.getMessage().contains("Data corruption or I/O error."), "Exception message should indicate data corruption");
    }

    @Test
    @DisplayName("retrieveById - Malformed runtime in CSV should throw ExceptionDao")
    void testRetrieveById_MalformedRuntimeThrowsExceptionDao() throws IOException {
        // Arrange: Write a malformed record (non-numeric runtime)
        Files.write(tempCsvPath, "1,xyz,Movie Title\n".getBytes(), StandardOpenOption.APPEND);

        // Act & Assert
        ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> movieDaoCsv.retrieveById(1),
                "Should throw ExceptionDao for malformed runtime");
        assertTrue(thrown.getMessage().contains("Data corruption or I/O error."), "Exception message should indicate data corruption");
    }


    // --- Tests for saveMovie ---

    @Test
    @DisplayName("saveMovie - Should successfully save a new MovieBean")
    void testSaveMovie_Success() throws ExceptionDao, IOException {
        // Arrange
        MovieBean newMovie = new MovieBean(1000, 110, "New Sci-Fi");

        // Act
        movieDaoCsv.saveMovie(newMovie);

        // Assert: Verify it's in the cache and the file
        MovieBean retrievedFromDao = movieDaoCsv.retrieveById(1000);
        assertNotNull(retrievedFromDao, "Movie should be retrievable after saving");
        assertEquals(newMovie, retrievedFromDao, "Saved movie should match retrieved one");

        // Verify content directly from file
        List<String> fileLines = Files.readAllLines(tempCsvPath);
        assertTrue(fileLines.contains("1000,110,New Sci-Fi"), "CSV file should contain the new movie record");
    }

    @Test
    @DisplayName("saveMovie - Should throw IllegalArgumentException for null MovieBean")
    void testSaveMovie_NullMovieThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> movieDaoCsv.saveMovie(null),
                "Saving a null movie should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("saveMovie - Should throw ExceptionDao if movie ID already exists in cache")
    void testSaveMovie_DuplicateIdInCacheThrowsException() throws ExceptionDao {
        // Arrange: Save a movie to populate cache and file
        MovieBean existingMovie = new MovieBean(2000, 100, "Existing Movie");
        movieDaoCsv.saveMovie(existingMovie);

        // Act & Assert: Attempt to save a new movie with the same ID
        ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> movieDaoCsv.saveMovie(new MovieBean(2000, 150, "Duplicate Movie")),
                "Saving movie with existing ID in cache should throw ExceptionDao");
        assertTrue(thrown.getMessage().contains("Duplicated Movie ID already in cache"), "Error message should indicate cache duplication");
    }

    @Test
    @DisplayName("saveMovie - Should throw ExceptionDao if movie ID already exists in CSV file (not in cache)")
    void testSaveMovie_DuplicateIdInFileThrowsException() throws IOException {
        // Arrange: Write a movie directly to the CSV file, bypassing the DAO's cache
        writeMovieToCsvDirectly(new MovieBean(3000, 120, "File Only Movie"));

        // Act & Assert: Attempt to save a new movie with the same ID. DAO should read file first.
        ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> movieDaoCsv.saveMovie(new MovieBean(3000, 130, "Another Movie")),
                "Saving movie with existing ID in file should throw ExceptionDao");
        assertTrue(thrown.getMessage().contains("Duplicated Movie ID already exists in CSV file"), "Error message should indicate file duplication");
    }


    // --- Tests for retrieveAllMovies ---

    @Test
    @DisplayName("retrieveAllMovies - Should return an empty list for an empty CSV file")
    void testRetrieveAllMovies_EmptyCsv() throws ExceptionDao {
        // Arrange: CSV is empty due to @BeforeEach

        // Act
        List<MovieBean> movies = movieDaoCsv.retrieveAllMovies();

        // Assert
        assertNotNull(movies, "List should not be null");
        assertTrue(movies.isEmpty(), "List should be empty for an empty CSV file");
    }

    @Test
    @DisplayName("retrieveAllMovies - Should return all movies from a populated CSV file")
    void testRetrieveAllMovies_PopulatedCsv() throws ExceptionDao, IOException {
        // Arrange: Write multiple movies directly to CSV
        MovieBean movie1 = new MovieBean(1, 90, "Test Movie 1");
        MovieBean movie2 = new MovieBean(2, 110, "Test Movie 2");
        writeMovieToCsvDirectly(movie1);
        writeMovieToCsvDirectly(movie2);

        // Act
        List<MovieBean> movies = movieDaoCsv.retrieveAllMovies();

        // Assert
        assertNotNull(movies);
        assertEquals(2, movies.size());
        // Verify content. MovieBean.equals() should be based on ID for robustness.
        assertTrue(movies.stream().anyMatch(m -> m.getIdMovieTmdb() == movie1.getIdMovieTmdb() && m.getTitle().equals(movie1.getTitle())), "Should contain movie1");
        assertTrue(movies.stream().anyMatch(m -> m.getIdMovieTmdb() == movie2.getIdMovieTmdb() && m.getTitle().equals(movie2.getTitle())), "Should contain movie2");
        // Or simply:
        assertTrue(movies.contains(movie1), "List should contain movie1 (requires MovieBean.equals)");
        assertTrue(movies.contains(movie2), "List should contain movie2 (requires MovieBean.equals)");
    }

    @Test
    @DisplayName("retrieveAllMovies - Should update local cache with retrieved movies")
    void testRetrieveAllMovies_UpdatesCache() throws ExceptionDao, IOException {
        // Arrange: Put a movie in cache, and different movies in the file
        movieDaoCsv.saveMovie(new MovieBean(99, 80, "Movie Only In Initial Cache")); // Added to cache and file
        Files.write(tempCsvPath, new byte[0], StandardOpenOption.TRUNCATE_EXISTING); // Clear file, but cache still has 99
        writeMovieToCsvDirectly(new MovieBean(101, 70, "Movie From File 1"));
        writeMovieToCsvDirectly(new MovieBean(102, 150, "Movie From File 2"));

        // Assert: Verify cache contents are now reflecting the file
        assertNotNull(movieDaoCsv.retrieveById(101), "Movie 101 should be in cache after retrieveAllMovies");
        assertNotNull(movieDaoCsv.retrieveById(102), "Movie 102 should be in cache after retrieveAllMovies");
        assertNull(movieDaoCsv.retrieveById(99), "Movie 99 (old cache) should be cleared from cache");
    }

    @Test
    @DisplayName("retrieveAllMovies - Returned list should be unmodifiable")
    void testRetrieveAllMovies_UnmodifiableList() throws ExceptionDao, IOException {
        // Arrange
        writeMovieToCsvDirectly(new MovieBean(1, 100, "Film"));
        movieDaoCsv.retrieveAllMovies(); // Populate the internal list and return unmodifiable view

        // Act
        List<MovieBean> retrievedList = movieDaoCsv.retrieveAllMovies();
        MovieBean movieBean = new MovieBean(2, 90, "New Film");
        // Assert
        assertThrows(UnsupportedOperationException.class, () -> retrievedList.add(movieBean),
                "Attempting to add to the list should throw UnsupportedOperationException");
        assertThrows(UnsupportedOperationException.class, () -> retrievedList.remove(0),
                "Attempting to remove from the list should throw UnsupportedOperationException");
    }

    @Test
    @DisplayName("retrieveAllMovies - Should skip malformed lines and retrieve valid ones")
    void testRetrieveAllMovies_MalformedLineIsIgnored() throws ExceptionDao, IOException {
        // Arrange: Write a mix of valid and malformed data
        writeMovieToCsvDirectly(new MovieBean(1, 100, "Valid Movie 1"));
        Files.write(tempCsvPath, "bad,data,line\n".getBytes(), StandardOpenOption.APPEND); // Malformed
        writeMovieToCsvDirectly(new MovieBean(2, 120, "Valid Movie 2"));
        Files.write(tempCsvPath, "3,abc,Movie With Bad Runtime\n".getBytes(), StandardOpenOption.APPEND); // Malformed runtime

        // Act
        List<MovieBean> movies = movieDaoCsv.retrieveAllMovies();

        // Assert
        assertNotNull(movies);
        assertEquals(2, movies.size(), "Only valid movies should be retrieved");
        assertTrue(movies.contains(new MovieBean(1, 100, "Valid Movie 1")), "Should contain Valid Movie 1");
        assertTrue(movies.contains(new MovieBean(2, 120, "Valid Movie 2")), "Should contain Valid Movie 2");
        assertFalse(movies.stream().anyMatch(m -> m.getTitle().contains("bad")), "Should not contain malformed entries");
        assertFalse(movies.stream().anyMatch(m -> m.getTitle().contains("Bad Runtime")), "Should not contain malformed entries");
    }


    @Test
    @DisplayName("retrieveAllMovies - Should throw CsvDaoException for fundamental CSV parsing errors")
    void testRetrieveAllMovies_CsvValidationExceptionHandled() throws IOException {
        // Arrange: Corrupt the file to trigger fundamental CSV parsing errors (e.g., non-text content)
        // This simulates a file that cannot be read as valid CSV by OpenCSV
        Files.write(tempCsvPath, new byte[]{0x00, 0x01, 0x02, 0x03}, StandardOpenOption.TRUNCATE_EXISTING);

        // Act & Assert
        // Expecting CsvDaoException from MovieDaoCsv's internal handling
        CsvDaoException thrown = assertThrows(CsvDaoException.class, () -> movieDaoCsv.retrieveAllMovies(),
                "Should throw CsvDaoException for fundamental CSV parsing errors");
        assertTrue(thrown.getMessage().contains("CSV data validation error"), "Exception message should indicate CSV validation issue");
    }

    @Test
    @DisplayName("retrieveAllMovies - Should throw ExceptionDao for I/O error during read")
    void testRetrieveAllMovies_IoExceptionDuringRead() throws IOException {
        // Arrange: Simulate an I/O error by making the file inaccessible or non-existent after DAO init
        Files.delete(tempCsvPath); // Delete the file to cause IOException on read

        // Act & Assert
        ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> movieDaoCsv.retrieveAllMovies(),
                "Should throw ExceptionDao on IOException during retrieveAllMovies");
        assertTrue(thrown.getMessage().contains("Failed to retrieve all movies from CSV. I/O error."), "Exception message should indicate I/O error");
    }

}