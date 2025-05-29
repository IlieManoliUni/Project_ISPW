package ispw.project.project_ispw.controller.application.state;

import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.UserDao;

public class PersistenceModeContext {

    private PersistenceModeState currentState;

    public PersistenceModeContext() {
        this.currentState = new DemoModeState();
    }

    public void changeState(PersistenceModeState newState) {
        this.currentState = newState;
    }

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