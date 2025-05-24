// File: ispw.project.project_ispw.controller.application.state.PersistenceModeContext.java
package ispw.project.project_ispw.controller.application.state;

import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.ListAnime; // Added
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.ListMovie; // Added
import ispw.project.project_ispw.dao.ListTvSeries; // Added
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.dao.DaoType;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Context class in the State design pattern.
 * It holds the current persistence mode state (Demo or Full)
 * and delegates requests for DAOs to the current state.
 */
public class PersistenceModeContext {

    private static final Logger LOGGER = Logger.getLogger(PersistenceModeContext.class.getName());

    private PersistenceModeState currentState;

    /**
     * Initializes the context, typically starting in a default state (e.g., DemoModeState).
     */
    public PersistenceModeContext() {
        // Default to DemoModeState on initialization
        this.currentState = new DemoModeState();
        LOGGER.log(Level.INFO, "PersistenceModeContext initialized in Demo Mode (default).");
    }

    /**
     * Changes the current persistence mode state.
     * @param newState The new state to transition to.
     */
    public void changeState(PersistenceModeState newState) {
        this.currentState = newState;
        LOGGER.log(Level.INFO, "Persistence Mode changed to: {0}", newState.getClass().getSimpleName());
    }

    // Delegate methods to the current state to get the appropriate DAOs
    public UserDao getUserDao() {
        return currentState.getUserDao();
    }

    public ListDao getListDao() {
        return currentState.getListDao();
    }

    public MovieDao getMovieDao() {
        return currentState.getMovieDao();
    }

    public TvSeriesDao getTvSeriesDao() {
        return currentState.getTvSeriesDao();
    }

    public AnimeDao getAnimeDao() {
        return currentState.getAnimeDao();
    }

    // New delegating methods for the specialized list DAOs
    public ListMovie getListMovieDao() {
        return currentState.getListMovieDao();
    }

    public ListTvSeries getListTvSeriesDao() {
        return currentState.getListTvSeriesDao();
    }

    public ListAnime getListAnimeDao() {
        return currentState.getListAnimeDao();
    }

    public PersistenceModeState getCurrentState() {
        return currentState;
    }
}