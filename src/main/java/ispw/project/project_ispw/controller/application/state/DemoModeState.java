// File: ispw.project.project_ispw.controller.application.state.DemoModeState.java
package ispw.project.project_ispw.controller.application.state;

import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.ListAnime; // Added
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.ListMovie; // Added
import ispw.project.project_ispw.dao.ListTvSeries; // Added
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.dao.inMemory.*;
import ispw.project.project_ispw.dao.inMemory.ListMovieDaoInMemory; // Added
import ispw.project.project_ispw.dao.inMemory.ListTvSeriesDaoInMemory; // Added
import ispw.project.project_ispw.dao.inMemory.ListAnimeDaoInMemory; // Added

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete State for the Demo Version (in-memory persistence).
 * Provides instances of in-memory DAOs.
 */
public class DemoModeState implements PersistenceModeState {

    private static final Logger LOGGER = Logger.getLogger(DemoModeState.class.getName());

    private final UserDao userDao;
    private final ListDao listDao;
    private final MovieDao movieDao;
    private final TvSeriesDao tvSeriesDao;
    private final AnimeDao animeDao;
    private final ListMovie listMovieDao; // Added
    private final ListTvSeries listTvSeriesDao; // Added
    private final ListAnime listAnimeDao; // Added

    public DemoModeState() {
        LOGGER.log(Level.INFO, "DemoModeState initialized (using in-memory DAOs).");
        this.userDao = new UserDaoInMemory();
        this.listDao = new ListDaoInMemory();
        this.movieDao = new MovieDaoInMemory();
        this.tvSeriesDao = new TvSeriesDaoInMemory();
        this.animeDao = new AnimeDaoInMemory();
        // Instantiate your new in-memory DAOs here
        this.listMovieDao = new ListMovieDaoInMemory(); // You need to implement this
        this.listTvSeriesDao = new ListTvSeriesDaoInMemory(); // You need to implement this
        this.listAnimeDao = new ListAnimeDaoInMemory(); // You need to implement this
    }

    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public ListDao getListDao() {
        return listDao;
    }

    @Override
    public MovieDao getMovieDao() {
        return movieDao;
    }

    @Override
    public TvSeriesDao getTvSeriesDao() {
        return tvSeriesDao;
    }

    @Override
    public AnimeDao getAnimeDao() {
        return animeDao;
    }

    @Override // New method
    public ListMovie getListMovieDao() {
        return listMovieDao;
    }

    @Override // New method
    public ListTvSeries getListTvSeriesDao() {
        return listTvSeriesDao;
    }

    @Override // New method
    public ListAnime getListAnimeDao() {
        return listAnimeDao;
    }
}