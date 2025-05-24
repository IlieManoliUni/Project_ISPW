// File: ispw.project.project_ispw.controller.application.state.PersistenceModeState.java
package ispw.project.project_ispw.controller.application.state;

import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.ListAnime; // Added
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.ListMovie; // Added
import ispw.project.project_ispw.dao.ListTvSeries; // Added
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.UserDao;

/**
 * Interface for the Persistence Mode State.
 * Defines methods to retrieve the appropriate DAO implementations
 * based on the current application mode (demo or full).
 */
public interface PersistenceModeState {
    UserDao getUserDao();
    ListDao getListDao();
    MovieDao getMovieDao();
    TvSeriesDao getTvSeriesDao();
    AnimeDao getAnimeDao();

    // New methods to get the specialized list DAOs
    ListMovie getListMovieDao();
    ListTvSeries getListTvSeriesDao();
    ListAnime getListAnimeDao();
}