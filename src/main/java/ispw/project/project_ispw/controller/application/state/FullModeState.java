package ispw.project.project_ispw.controller.application.state;

import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.UserDao;

import ispw.project.project_ispw.dao.DaoType;

import ispw.project.project_ispw.dao.jdbc.UserDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.ListDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.MovieDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.TvSeriesDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.AnimeDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.ListMovieDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.ListTvSeriesDaoJdbc;
import ispw.project.project_ispw.dao.jdbc.ListAnimeDaoJdbc;

import ispw.project.project_ispw.dao.csv.UserDaoCsv;
import ispw.project.project_ispw.dao.csv.ListDaoCsv;
import ispw.project.project_ispw.dao.csv.MovieDaoCsv;
import ispw.project.project_ispw.dao.csv.TvSeriesDaoCsv;
import ispw.project.project_ispw.dao.csv.AnimeDaoCsv;
import ispw.project.project_ispw.dao.csv.ListMovieDaoCsv;
import ispw.project.project_ispw.dao.csv.ListTvSeriesDaoCsv;
import ispw.project.project_ispw.dao.csv.ListAnimeDaoCsv;

public class FullModeState implements PersistenceModeState {

    private final UserDao userDao;
    private final ListDao listDao;
    private final MovieDao movieDao;
    private final TvSeriesDao tvSeriesDao;
    private final AnimeDao animeDao;
    private final ListMovie listMovieDao;
    private final ListTvSeries listTvSeriesDao;
    private final ListAnime listAnimeDao;

    public FullModeState(DaoType daoType) {

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
                    throw new IllegalStateException("Error initializing JDBC DAOs. Ensure all JDBC DAO classes are implemented and available.", e);
                }
                break;

            case CSV:
                try {
                    this.userDao = new UserDaoCsv();
                    this.listDao = new ListDaoCsv();
                    this.movieDao = new MovieDaoCsv();
                    this.tvSeriesDao = new TvSeriesDaoCsv();
                    this.animeDao = new AnimeDaoCsv();
                    this.listMovieDao = new ListMovieDaoCsv();
                    this.listTvSeriesDao = new ListTvSeriesDaoCsv();
                    this.listAnimeDao = new ListAnimeDaoCsv();
                } catch (Exception e) {
                    throw new IllegalStateException("Error initializing CSV DAOs. Ensure all CSV DAO classes are implemented and available.", e);
                }
                break;

            default:
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