// File: src/main/java/ispw/project/project_ispw/controller/application/state/FullModeState.java
package ispw.project.project_ispw.controller.application.state;

// Import all DAO interfaces
import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.UserDao;

// Import the DaoType enum
import ispw.project.project_ispw.dao.DaoType;

// Import your concrete JDBC DAO implementations
import ispw.project.project_ispw.dao.jdbc.UserDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.ListDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.MovieDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.TvSeriesDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.AnimeDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.ListMovieDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.ListTvSeriesDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.ListAnimeDaoJdbc;

// Import your concrete CSV DAO implementations
import ispw.project.project_ispw.dao.csv.UserDaoCsv;
import ispw.project.project_ispw.dao.csv.ListDaoCsv;
import ispw.project.project_ispw.dao.csv.MovieDaoCsv;
import ispw.project.project_ispw.dao.csv.TvSeriesDaoCsv;
import ispw.project.project_ispw.dao.csv.AnimeDaoCsv;
import ispw.project.project_ispw.dao.csv.ListMovieDaoCsv;
import ispw.project.project_ispw.dao.csv.ListTvSeriesDaoCsv;
import ispw.project.project_ispw.dao.csv.ListAnimeDaoCsv;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete State for the Full Version (persistent storage).
 * Provides instances of persistent DAOs (JDBC or CSV) based on the configured DaoType.
 * This state *does not* use in-memory DAOs.
 */
public class FullModeState implements PersistenceModeState {

    private static final Logger LOGGER = Logger.getLogger(FullModeState.class.getName());

    private final UserDao userDao;
    private final ListDao listDao;
    private final MovieDao movieDao;
    private final TvSeriesDao tvSeriesDao;
    private final AnimeDao animeDao;
    private final ListMovie listMovieDao;
    private final ListTvSeries listTvSeriesDao;
    private final ListAnime listAnimeDao;

    /**
     * Constructor for FullModeState, requiring selection of a persistent DAO implementation type.
     * It initializes all DAOs based on the provided {@code daoType}.
     *
     * @param daoType The type of persistent DAO implementation to use (JDBC or CSV).
     * @throws IllegalStateException if the specified DaoType is not supported by FullModeState
     * or if an error occurs during the instantiation of concrete DAO implementations.
     */
    public FullModeState(DaoType daoType) {
        LOGGER.log(Level.INFO, "FullModeState initialized with DaoType: {0}", daoType);

        switch (daoType) {
            case JDBC:
                try {
                    this.userDao = new UserDaoJdbc();
                    this.listDao = new ListDaoJdbc();
                    this.movieDao = new MovieDaoJdbc();
                    this.tvSeriesDao = new TvSeriesDaoJdbc();
                    this.animeDao = new AnimeDaoJdbc();
                    this.listMovieDao = new ListMovieDaoJdbc();
                    this.listTvSeriesDao = new ListTvSeriesDaoJdbc();
                    this.listAnimeDao = new ListAnimeDaoJdbc();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to initialize JDBC DAOs for FullModeState: {0}", e.getMessage());
                    throw new IllegalStateException("Error initializing JDBC DAOs. Ensure all JDBC DAO classes are implemented and available.", e);
                }
                break;

            case CSV:
                try {
                    // IMPORTANT: Replace these with your actual CSV DAO instantiations once they are ready.
                    this.userDao = new UserDaoCsv();
                    this.listDao = new ListDaoCsv();
                    this.movieDao = new MovieDaoCsv();
                    this.tvSeriesDao = new TvSeriesDaoCsv();
                    this.animeDao = new AnimeDaoCsv();
                    this.listMovieDao = new ListMovieDaoCsv();
                    this.listTvSeriesDao = new ListTvSeriesDaoCsv();
                    this.listAnimeDao = new ListAnimeDaoCsv();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to initialize CSV DAOs for FullModeState: {0}", e.getMessage());
                    throw new IllegalStateException("Error initializing CSV DAOs. Ensure all CSV DAO classes are implemented and available.", e);
                }
                break;

            // The 'default' case now handles any DaoType that is NOT JDBC or CSV,
            // which correctly catches IN_MEMORY if it were accidentally passed here.
            default:
                LOGGER.log(Level.SEVERE, "Unsupported DaoType for FullModeState: {0}. FullModeState only supports JDBC or CSV.", daoType);
                throw new IllegalStateException("Invalid DaoType for FullModeState. Must be JDBC or CSV.");
        }
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

    @Override
    public ListMovie getListMovieDao() {
        return listMovieDao;
    }

    @Override
    public ListTvSeries getListTvSeriesDao() {
        return listTvSeriesDao;
    }

    @Override
    public ListAnime getListAnimeDao() {
        return listAnimeDao;
    }
}