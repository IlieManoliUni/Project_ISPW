package ispw.project.project_ispw.controller.application.util;

import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.dao.ListTvSeries;

public class ListContentDaoProvider {
    private final ListMovie listMovieDao;
    private final ListTvSeries listTvSeriesDao;
    private final ListAnime listAnimeDao;

    public ListContentDaoProvider(ListMovie listMovieDao, ListTvSeries listTvSeriesDao, ListAnime listAnimeDao) {
        this.listMovieDao = listMovieDao;
        this.listTvSeriesDao = listTvSeriesDao;
        this.listAnimeDao = listAnimeDao;
    }

    public ListMovie getListMovieDao() { return listMovieDao; }
    public ListTvSeries getListTvSeriesDao() { return listTvSeriesDao; }
    public ListAnime getListAnimeDao() { return listAnimeDao; }
}
