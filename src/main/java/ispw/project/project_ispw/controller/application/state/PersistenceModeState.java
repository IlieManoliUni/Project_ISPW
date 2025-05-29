package ispw.project.project_ispw.controller.application.state;

import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.dao.UserDao;

public interface PersistenceModeState {
    UserDao getUserDao();
    ListDao getListDao();
    MovieDao getMovieDao();
    TvSeriesDao getTvSeriesDao();
    AnimeDao getAnimeDao();
    ListMovie getListMovieDao();
    ListTvSeries getListTvSeriesDao();
    ListAnime getListAnimeDao();
}