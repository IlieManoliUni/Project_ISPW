package ispw.project.project_ispw.dao.jdbc;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.connection.SingletonDatabase;
import ispw.project.project_ispw.dao.queries.CrudMovie;
import ispw.project.project_ispw.exception.ExceptionDao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; // For MockitoExtension

import org.mockito.Mock;
import org.mockito.MockedStatic; // For static mocking
import org.mockito.Mockito;      // For mockStatic
import org.mockito.junit.jupiter.MockitoExtension; // For @Mock annotations

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections; // For emptyList
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*; // For when, verify, any

@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
@DisplayName("MovieDaoJdbc Test Suite")
class TestMovieDaoJdbc {

    private MovieDaoJdbc movieDaoJdbc; // The class under test

    @Mock
    private SingletonDatabase mockSingletonDatabase; // Mock the singleton
    @Mock
    private Connection mockConnection; // Mock the JDBC Connection

    // No need for @BeforeEach to initialize movieDaoJdbc if it's done in the constructor
    // or we can initialize it here. Let's initialize it here for clarity.
    @BeforeEach
    void setUp() {
        // Mock the SingletonDatabase to return our mocked Connection
        // We use Mockito.mockStatic for SingletonDatabase.getInstance()
        // This setup is a bit tricky for singletons, a better design for SingletonDatabase
        // would be to have an interface for it and pass it to DAO constructor.
        // For current code, we'll need to mock its static getInstance().

        // This is a common pattern to ensure SingletonDatabase returns our mock
        // You might need to adjust SingletonDatabase to allow this if it's very strict
        // For simplicity, we'll assume SingletonDatabase.getInstance() can be mocked.
        // A more robust way to test would be to inject the connection provider
        // into MovieDaoJdbc instead of relying on SingletonDatabase.getInstance()

        // As SingletonDatabase.getInstance() is static, we'll try to mock it directly if possible
        // This is where mockito-inline comes in handy.
        // For this scenario, we primarily need to mock SingletonDatabase.getInstance().getConnection()
        // which can be achieved by first mocking SingletonDatabase.getInstance()

        // Simpler approach: Assume MovieDaoJdbc constructor implicitly calls SingletonDatabase.getInstance()
        // and we can intercept its getConnection call. This is still tricky with strict singletons.

        // Given current MovieDaoJdbc, the best way to unit test it with Mockito
        // is to mock the static behavior of SingletonDatabase and CrudMovie.
        // We'll primarily mock SingletonDatabase.getInstance() behavior for simplicity
        // related to connection pooling/management.

        // A truly isolated test requires that MovieDaoJdbc depends on interfaces, not statics/singletons.
        // For the provided code, the standard practice for unit testing static dependencies is mockStatic.

        // Initialize the DAO with dependencies that will return our mocks
        movieDaoJdbc = new MovieDaoJdbc(); // Assuming a default constructor
    }

    // --- Test for retrieveById ---
    @Test
    @DisplayName("retrieveById - Should return MovieBean for existing ID")
    void testRetrieveById_ExistingMovie() throws ExceptionDao, SQLException {
        MovieBean expectedMovie = new MovieBean(1, 120, "Test Movie 1");

        // Use try-with-resources for MockedStatic to ensure it's closed
        try (MockedStatic<SingletonDatabase> mockedSingletonDb = Mockito.mockStatic(SingletonDatabase.class);
             MockedStatic<CrudMovie> mockedCrudMovie = Mockito.mockStatic(CrudMovie.class)) {

            // When SingletonDatabase.getInstance() is called, return our mockSingletonDatabase
            mockedSingletonDb.when(SingletonDatabase::getInstance).thenReturn(mockSingletonDatabase);

            // When mockSingletonDatabase.getConnection() is called, return our mockConnection
            when(mockSingletonDatabase.getConnection()).thenReturn(mockConnection);

            // When CrudMovie.getMovieById is called, return our expected movie
            mockedCrudMovie.when(() -> CrudMovie.getMovieById(mockConnection, 1)).thenReturn(expectedMovie);

            // Act
            MovieBean result = movieDaoJdbc.retrieveById(1);

            // Assert
            assertNotNull(result, "Movie should be retrieved");
            assertEquals(expectedMovie, result, "Retrieved movie should match the expected one");

            // Verify interactions
            verify(mockSingletonDatabase, times(1)).getConnection();
            mockedCrudMovie.verify(() -> CrudMovie.getMovieById(mockConnection, 1), times(1));
            verify(mockConnection, times(1)).close(); // Ensure connection is closed
        }
    }

    @Test
    @DisplayName("retrieveById - Should return null for non-existing ID")
    void testRetrieveById_NonExistingMovie() throws ExceptionDao, SQLException {
        // Use try-with-resources for MockedStatic
        try (MockedStatic<SingletonDatabase> mockedSingletonDb = Mockito.mockStatic(SingletonDatabase.class);
             MockedStatic<CrudMovie> mockedCrudMovie = Mockito.mockStatic(CrudMovie.class)) {

            mockedSingletonDb.when(SingletonDatabase::getInstance).thenReturn(mockSingletonDatabase);
            when(mockSingletonDatabase.getConnection()).thenReturn(mockConnection);

            // Mock CrudMovie.getMovieById to return null for a non-existent ID
            mockedCrudMovie.when(() -> CrudMovie.getMovieById(mockConnection, 999)).thenReturn(null);

            // Act
            MovieBean result = movieDaoJdbc.retrieveById(999);

            // Assert
            assertNull(result, "Should return null for non-existing movie");

            // Verify interactions
            verify(mockSingletonDatabase, times(1)).getConnection();
            mockedCrudMovie.verify(() -> CrudMovie.getMovieById(mockConnection, 999), times(1));
            verify(mockConnection, times(1)).close();
        }
    }

    @Test
    @DisplayName("retrieveById - Should throw ExceptionDao on SQLException during retrieval")
    void testRetrieveById_SQLException() throws SQLException {
        // Use try-with-resources for MockedStatic
        try (MockedStatic<SingletonDatabase> mockedSingletonDb = Mockito.mockStatic(SingletonDatabase.class);
             MockedStatic<CrudMovie> mockedCrudMovie = Mockito.mockStatic(CrudMovie.class)) {

            mockedSingletonDb.when(SingletonDatabase::getInstance).thenReturn(mockSingletonDatabase);
            when(mockSingletonDatabase.getConnection()).thenReturn(mockConnection);

            // Mock CrudMovie.getMovieById to throw SQLException
            SQLException sqlException = new SQLException("Database error during getMovieById");
            mockedCrudMovie.when(() -> CrudMovie.getMovieById(mockConnection, anyInt())).thenThrow(sqlException);

            // Act & Assert
            ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> movieDaoJdbc.retrieveById(1),
                    "Should throw ExceptionDao on SQLException");
            assertTrue(thrown.getMessage().contains("Database error during getMovieById"), "Exception message should contain SQL error detail");

            // Verify interactions
            verify(mockSingletonDatabase, times(1)).getConnection();
            mockedCrudMovie.verify(() -> CrudMovie.getMovieById(mockConnection, 1), times(1));
            verify(mockConnection, times(1)).close(); // Connection should still be closed in finally block
        }
    }

    // --- Test for saveMovie ---
    @Test
    @DisplayName("saveMovie - Should successfully save a new MovieBean")
    void testSaveMovie_Success() throws ExceptionDao, SQLException {
        MovieBean movieToSave = new MovieBean(2, 100, "New Movie");

        try (MockedStatic<SingletonDatabase> mockedSingletonDb = Mockito.mockStatic(SingletonDatabase.class);
             MockedStatic<CrudMovie> mockedCrudMovie = Mockito.mockStatic(CrudMovie.class)) {

            mockedSingletonDb.when(SingletonDatabase::getInstance).thenReturn(mockSingletonDatabase);
            when(mockSingletonDatabase.getConnection()).thenReturn(mockConnection);

            // Mock CrudMovie.addMovie to do nothing (success)
            mockedCrudMovie.when(() -> CrudMovie.addMovie(mockConnection, movieToSave)).thenAnswer(invocation -> null); // Use thenAnswer for void methods

            // Act
            movieDaoJdbc.saveMovie(movieToSave);

            // Assert
            // Verify that addMovie was called exactly once with the correct arguments
            mockedCrudMovie.verify(() -> CrudMovie.addMovie(mockConnection, movieToSave), times(1));
            verify(mockConnection, times(1)).close(); // Ensure connection is closed
        }
    }

    @Test
    @DisplayName("saveMovie - Should throw ExceptionDao on SQLException during save")
    void testSaveMovie_SQLException() throws SQLException {
        MovieBean movieToSave = new MovieBean(3, 110, "Movie with Error");

        try (MockedStatic<SingletonDatabase> mockedSingletonDb = Mockito.mockStatic(SingletonDatabase.class);
             MockedStatic<CrudMovie> mockedCrudMovie = Mockito.mockStatic(CrudMovie.class)) {

            mockedSingletonDb.when(SingletonDatabase::getInstance).thenReturn(mockSingletonDatabase);
            when(mockSingletonDatabase.getConnection()).thenReturn(mockConnection);

            // Mock CrudMovie.addMovie to throw SQLException
            SQLException sqlException = new SQLException("Database error during addMovie");
            mockedCrudMovie.when(() -> CrudMovie.addMovie(mockConnection, movieToSave)).thenThrow(sqlException);

            // Act & Assert
            ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> movieDaoJdbc.saveMovie(movieToSave),
                    "Should throw ExceptionDao on SQLException");
            assertTrue(thrown.getMessage().contains("Database error during addMovie"), "Exception message should contain SQL error detail");

            // Verify
            mockedCrudMovie.verify(() -> CrudMovie.addMovie(mockConnection, movieToSave), times(1));
            verify(mockConnection, times(1)).close(); // Connection should still be closed
        }
    }

    // --- Test for retrieveAllMovies ---
    @Test
    @DisplayName("retrieveAllMovies - Should return all saved movies")
    void testRetrieveAllMovies_Success() throws ExceptionDao, SQLException {
        MovieBean movie1 = new MovieBean(10, 90, "Movie A");
        MovieBean movie2 = new MovieBean(11, 130, "Movie B");
        List<MovieBean> expectedMovies = Arrays.asList(movie1, movie2);

        try (MockedStatic<SingletonDatabase> mockedSingletonDb = Mockito.mockStatic(SingletonDatabase.class);
             MockedStatic<CrudMovie> mockedCrudMovie = Mockito.mockStatic(CrudMovie.class)) {

            mockedSingletonDb.when(SingletonDatabase::getInstance).thenReturn(mockSingletonDatabase);
            when(mockSingletonDatabase.getConnection()).thenReturn(mockConnection);

            // Mock CrudMovie.getAllMovies to return our list
            mockedCrudMovie.when(() -> CrudMovie.getAllMovies(mockConnection)).thenReturn(expectedMovies);

            // Act
            List<MovieBean> result = movieDaoJdbc.retrieveAllMovies();

            // Assert
            assertNotNull(result, "Result list should not be null");
            assertEquals(2, result.size(), "Result list should have 2 movies");
            assertTrue(result.containsAll(expectedMovies), "Result list should contain all expected movies");

            // Verify
            mockedCrudMovie.verify(() -> CrudMovie.getAllMovies(mockConnection), times(1));
            verify(mockConnection, times(1)).close();
        }
    }

    @Test
    @DisplayName("retrieveAllMovies - Should return an empty list if no movies found")
    void testRetrieveAllMovies_EmptyResult() throws ExceptionDao, SQLException {
        try (MockedStatic<SingletonDatabase> mockedSingletonDb = Mockito.mockStatic(SingletonDatabase.class);
             MockedStatic<CrudMovie> mockedCrudMovie = Mockito.mockStatic(CrudMovie.class)) {

            mockedSingletonDb.when(SingletonDatabase::getInstance).thenReturn(mockSingletonDatabase);
            when(mockSingletonDatabase.getConnection()).thenReturn(mockConnection);

            // Mock CrudMovie.getAllMovies to return an empty list
            mockedCrudMovie.when(() -> CrudMovie.getAllMovies(mockConnection)).thenReturn(Collections.emptyList());

            // Act
            List<MovieBean> result = movieDaoJdbc.retrieveAllMovies();

            // Assert
            assertNotNull(result, "Result list should not be null");
            assertTrue(result.isEmpty(), "Result list should be empty");

            // Verify
            mockedCrudMovie.verify(() -> CrudMovie.getAllMovies(mockConnection), times(1));
            verify(mockConnection, times(1)).close();
        }
    }

    @Test
    @DisplayName("retrieveAllMovies - Should throw ExceptionDao on SQLException during retrieval")
    void testRetrieveAllMovies_SQLException() throws SQLException {
        try (MockedStatic<SingletonDatabase> mockedSingletonDb = Mockito.mockStatic(SingletonDatabase.class);
             MockedStatic<CrudMovie> mockedCrudMovie = Mockito.mockStatic(CrudMovie.class)) {

            mockedSingletonDb.when(SingletonDatabase::getInstance).thenReturn(mockSingletonDatabase);
            when(mockSingletonDatabase.getConnection()).thenReturn(mockConnection);

            // Mock CrudMovie.getAllMovies to throw SQLException
            SQLException sqlException = new SQLException("Database error during getAllMovies");
            mockedCrudMovie.when(() -> CrudMovie.getAllMovies(mockConnection)).thenThrow(sqlException);

            // Act & Assert
            ExceptionDao thrown = assertThrows(ExceptionDao.class, () -> movieDaoJdbc.retrieveAllMovies(),
                    "Should throw ExceptionDao on SQLException");
            assertTrue(thrown.getMessage().contains("Database error during getAllMovies"), "Exception message should contain SQL error detail");

            // Verify
            mockedCrudMovie.verify(() -> CrudMovie.getAllMovies(mockConnection), times(1));
            verify(mockConnection, times(1)).close();
        }
    }
}