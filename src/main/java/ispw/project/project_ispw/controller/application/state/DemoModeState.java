package ispw.project.project_ispw.controller.application.state;

import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.UserDao;
import ispw.project.project_ispw.dao.inMemory.*;
import ispw.project.project_ispw.dao.inMemory.ListMovieDaoInMemory;
import ispw.project.project_ispw.dao.inMemory.ListTvSeriesDaoInMemory;
import ispw.project.project_ispw.dao.inMemory.ListAnimeDaoInMemory;

public class DemoModeState implements PersistenceModeState {

    private final UserDao userDao;
    private final ListDao listDao;
    private final MovieDao movieDao;
    private final TvSeriesDao tvSeriesDao;
    private final AnimeDao animeDao;
    private final ListMovie listMovieDao;
    private final ListTvSeries listTvSeriesDao;
    private final ListAnime listAnimeDao;

    public DemoModeState() {
        this.userDao = new UserDaoInMemory();
        this.listDao = new ListDaoInMemory();
        this.movieDao = new MovieDaoInMemory();
        this.tvSeriesDao = new TvSeriesDaoInMemory();
        this.animeDao = new AnimeDaoInMemory();
        this.listMovieDao = new ListMovieDaoInMemory();
        this.listTvSeriesDao = new ListTvSeriesDaoInMemory();
        this.listAnimeDao = new ListAnimeDaoInMemory();
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