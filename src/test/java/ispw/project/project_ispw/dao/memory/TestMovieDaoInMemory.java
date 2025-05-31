package ispw.project.project_ispw.dao.memory;

import ispw.project.project_ispw.bean.MovieBean; // Import your MovieBean
import ispw.project.project_ispw.exception.ExceptionDao; // Import your custom ExceptionDao
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MovieDaoInMemory Test Suite")
class TestMovieDaoInMemory {

    private MovieDaoInMemory movieDao; // The actual instance of your DAO to test

    @BeforeEach
    void setUp() {
        // Initialize a new instance before each test.
        // This ensures a clean state (empty internal map) for every test method,
        // preventing test interference.
        movieDao = new MovieDaoInMemory();
    }

    @Test
    @DisplayName("retrieveById - Should return MovieBean for existing ID")
    void testRetrieveById_ExistingMovie() throws ExceptionDao {
        // Arrange (Given)
        MovieBean movie1 = new MovieBean(101, 150, "The Grand Adventure");
        movieDao.saveMovie(movie1); // Save the movie first

        // Act (When)
        MovieBean retrievedMovie = movieDao.retrieveById(101);

        // Assert (Then)
        assertNotNull(retrievedMovie, "Retrieved movie should not be null");
        assertEquals(movie1.getIdMovieTmdb(), retrievedMovie.getIdMovieTmdb(), "Movie IDs should match");
        assertEquals(movie1.getTitle(), retrievedMovie.getTitle(), "Movie titles should match");
        assertEquals(movie1.getRuntime(), retrievedMovie.getRuntime(), "Movie runtimes should match");
        assertEquals(movie1, retrievedMovie, "Retrieved movie object should be equal to the saved one (if equals/hashCode are implemented)");
    }

    @Test
    @DisplayName("retrieveById - Should return null for non-existing ID")
    void testRetrieveById_NonExistingMovie() throws ExceptionDao {
        // Arrange (Given) - The DAO is clean due to @BeforeEach, so no movie with this ID exists.

        // Act (When)
        MovieBean retrievedMovie = movieDao.retrieveById(999); // Use an ID that was never saved

        // Assert (Then)
        assertNull(retrievedMovie, "Should return null when no movie with the given ID exists");
    }

    @Test
    @DisplayName("saveMovie - Should successfully save a new MovieBean")
    void testSaveMovie_Success() throws ExceptionDao {
        // Arrange (Given)
        MovieBean newMovie = new MovieBean(201, 120, "Journey to the Stars");

        // Act (When)
        movieDao.saveMovie(newMovie);

        // Assert (Then)
        MovieBean retrieved = movieDao.retrieveById(201);
        assertNotNull(retrieved, "Movie should be retrievable after successful save");
        assertEquals(newMovie, retrieved, "Saved movie object should match the retrieved one");
    }

    @Test
    @DisplayName("saveMovie - Should throw IllegalArgumentException for null MovieBean")
    void testSaveMovie_NullMovieThrowsException() {
        // Arrange (Given)
        MovieBean nullMovie = null;

        // Act & Assert (When & Then)
        assertThrows(IllegalArgumentException.class, () -> movieDao.saveMovie(nullMovie),
                "Saving a null MovieBean should throw an IllegalArgumentException");
    }

    @Test
    @DisplayName("saveMovie - Should throw ExceptionDao for existing movie ID")
    void testSaveMovie_ExistingIdThrowsExceptionDao() throws ExceptionDao {
        // Arrange (Given)
        MovieBean movieA = new MovieBean(301, 180, "Epic Saga Part 1");
        MovieBean movieB = new MovieBean(301, 90, "Epic Saga Part 2 - Same ID"); // Different movie, same ID

        movieDao.saveMovie(movieA); // Save the first movie with ID 301

        // Act & Assert (When & Then)
        ExceptionDao thrownException = assertThrows(ExceptionDao.class, () -> movieDao.saveMovie(movieB),
                "Saving a movie with an ID that already exists should throw ExceptionDao");
        // Verify the exception message to be more specific
        assertTrue(thrownException.getMessage().contains("already exists"),
                "Exception message should indicate that the movie ID already exists");
    }

    @Test
    @DisplayName("retrieveAllMovies - Should return an empty list if no movies are saved")
    void testRetrieveAllMovies_EmptyDao() throws ExceptionDao {
        // Arrange (Given) - The DAO is empty due to @BeforeEach

        // Act (When)
        List<MovieBean> allMovies = movieDao.retrieveAllMovies();

        // Assert (Then)
        assertNotNull(allMovies, "List of all movies should not be null, even if empty");
        assertTrue(allMovies.isEmpty(), "List should be empty when no movies are saved");
        assertEquals(0, allMovies.size(), "Size of list should be 0 when no movies are saved");
    }

    @Test
    @DisplayName("retrieveAllMovies - Should return all saved movies")
    void testRetrieveAllMovies_ContainsAllSavedMovies() throws ExceptionDao {
        // Arrange (Given)
        MovieBean movie1 = new MovieBean(401, 100, "Comedy Fun");
        MovieBean movie2 = new MovieBean(402, 130, "Action Thriller");
        movieDao.saveMovie(movie1);
        movieDao.saveMovie(movie2);

        // Act (When)
        List<MovieBean> allMovies = movieDao.retrieveAllMovies();

        // Assert (Then)
        assertNotNull(allMovies, "List of all movies should not be null");
        assertEquals(2, allMovies.size(), "List should contain exactly 2 movies");
        assertTrue(allMovies.contains(movie1), "List should contain the first saved movie");
        assertTrue(allMovies.contains(movie2), "List should contain the second saved movie");
    }

    @Test
    @DisplayName("retrieveAllMovies - Returned list should be unmodifiable")
    void testRetrieveAllMovies_IsUnmodifiable() throws ExceptionDao {
        // Arrange (Given)
        MovieBean movie1 = new MovieBean(501, 90, "Documentary");
        movieDao.saveMovie(movie1);

        // Act (When)
        List<MovieBean> allMovies = movieDao.retrieveAllMovies();

        MovieBean newMovie = new MovieBean(502, 100, "New Doc");

        // Assert (Then)
        // Attempt to modify the list to verify it's unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> allMovies.add(newMovie),
                "Attempting to add to the returned list should throw UnsupportedOperationException");
        assertThrows(UnsupportedOperationException.class, () -> allMovies.remove(0),
                "Attempting to remove from the returned list should throw UnsupportedOperationException");
    }
}