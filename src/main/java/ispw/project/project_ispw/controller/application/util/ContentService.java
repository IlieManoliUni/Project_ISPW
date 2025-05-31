package ispw.project.project_ispw.controller.application.util;

import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.connection.AnimeAniList;
import ispw.project.project_ispw.connection.MovieTmdb;
import ispw.project.project_ispw.connection.TvSeriesTmdb;
import ispw.project.project_ispw.exception.ExceptionAniListApi;
import ispw.project.project_ispw.exception.ExceptionTmdbApi;
import ispw.project.project_ispw.model.AnimeModel;
import ispw.project.project_ispw.model.MovieModel;
import ispw.project.project_ispw.model.TvSeriesModel;


import java.util.List;

public class ContentService {

    public ContentService() {
        // Empty constructor
    }

    public List<MovieModel> searchAndMapMovies(String query) throws ExceptionApplicationController {
        try {
            return MovieTmdb.searchMovies(query);
        } catch (ExceptionTmdbApi tmdbE) {
            throw new ExceptionApplicationController("Failed to search movies from TMDb API: " + tmdbE.getMessage(), tmdbE);
        }
    }

    public List<TvSeriesModel> searchAndMapTvSeries(String query) throws ExceptionApplicationController {
        try {
            return TvSeriesTmdb.searchTvSeries(query);
        } catch (ExceptionTmdbApi tmdbE) {
            throw new ExceptionApplicationController("Failed to search TV Series from TMDb API: " + tmdbE.getMessage(), tmdbE);
        }
    }

    public List<AnimeModel> searchAndMapAnime(String query) throws ExceptionApplicationController {
        try {
            return AnimeAniList.searchAnime(query);
        } catch (ExceptionAniListApi aniListE) {
            throw new ExceptionApplicationController("Failed to search Anime from AniList API: " + aniListE.getMessage(), aniListE);
        }
    }

    public MovieModel retrieveMovieById(int id) throws ExceptionApplicationController {
        try {
            return fetchAndMapMovieModel(id);
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to retrieve Movie details: " + e.getMessage(), e);
        }
    }

    private MovieModel fetchAndMapMovieModel(int id) throws ExceptionApplicationController {
        try {
            MovieModel modelMovie = MovieTmdb.getMovieById(id);

            if (modelMovie == null) {
                throw new ExceptionApplicationController("Movie with ID " + id + " not found or returned null model from TMDb API.");
            }

            return modelMovie;
        } catch (ExceptionTmdbApi tmdbE) {
            throw new ExceptionApplicationController("Movie with ID " + id + " not found from TMDb API: " + tmdbE.getMessage(), tmdbE);
        }
    }

    public TvSeriesModel retrieveTvSeriesById(int id) throws ExceptionApplicationController {
        try {
            return fetchAndMapTvSeriesModel(id);
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to retrieve TV Series details: " + e.getMessage(), e);
        }
    }

    private TvSeriesModel fetchAndMapTvSeriesModel(int id) throws ExceptionApplicationController {
        try {
            TvSeriesModel modelTvSeries = TvSeriesTmdb.getTvSeriesById(id);

            if (modelTvSeries == null) {
                throw new ExceptionApplicationController("TV Series with ID " + id + " not found or returned null model from TMDb API.");
            }

            return modelTvSeries;
        } catch (ExceptionTmdbApi tmdbE) {
            throw new ExceptionApplicationController("TV Series with ID " + id + " not found from TMDb API: " + tmdbE.getMessage(), tmdbE);
        }
    }


    public AnimeModel retrieveAnimeById(int id) throws ExceptionApplicationController {
        try {
            return AnimeAniList.getAnimeById(id);
        } catch (ExceptionAniListApi aniListE) {
            throw new ExceptionApplicationController("Failed to retrieve Anime details: " + aniListE.getMessage(), aniListE);
        } catch (Exception e) {
            throw new ExceptionApplicationController("An unexpected error occurred while retrieving Anime details: " + e.getMessage(), e);
        }
    }
}