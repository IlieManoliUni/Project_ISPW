package ispw.project.project_ispw.controller.application.util;

import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;

public class ContentDetailDaoProvider {
    private final AnimeDao animeDao;
    private final MovieDao movieDao;
    private final TvSeriesDao tvSeriesDao;

    public ContentDetailDaoProvider(AnimeDao animeDao, MovieDao movieDao, TvSeriesDao tvSeriesDao) {
        this.animeDao = animeDao;
        this.movieDao = movieDao;
        this.tvSeriesDao = tvSeriesDao;
    }

    public AnimeDao getAnimeDao() { return animeDao; }
    public MovieDao getMovieDao() { return movieDao; }
    public TvSeriesDao getTvSeriesDao() { return tvSeriesDao; }
}
