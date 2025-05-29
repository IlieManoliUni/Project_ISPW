package ispw.project.project_ispw.controller.application.util;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.connection.AnimeAniList;
import ispw.project.project_ispw.connection.MovieTmdb;
import ispw.project.project_ispw.connection.TvSeriesTmdb;
import ispw.project.project_ispw.exception.ExceptionAniListApi;
import ispw.project.project_ispw.exception.ExceptionTmdbApi;
import ispw.project.project_ispw.model.AnimeModel;
import ispw.project.project_ispw.model.MovieModel;
import ispw.project.project_ispw.model.TvSeriesModel;


import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class ContentService {

    private static final String DATE_FORMAT_PATTERN = "%d-%02d-%02d";

    public ContentService() {
        // No logging needed in constructor
    }

    public List<MovieBean> searchAndMapMovies(String query) throws ExceptionApplicationController {
        try {
            List<MovieModel> movieModels = MovieTmdb.searchMovies(query);
            List<MovieBean> movieBeans = new java.util.ArrayList<>();
            for (MovieModel model : movieModels) {
                List<String> genres = new java.util.ArrayList<>();
                if (model.getGenres() != null) {
                    for (MovieModel.Genre genre : model.getGenres()) {
                        genres.add(genre.getName());
                    }
                }
                List<String> productionCompanies = new java.util.ArrayList<>();
                if (model.getProductionCompanies() != null) {
                    for (MovieModel.ProductionCompany company : model.getProductionCompanies()) {
                        productionCompanies.add(company.getName());
                    }
                }

                movieBeans.add(new MovieBean(
                        model.getId(),
                        model.getTitle(),
                        model.getOverview(),
                        model.getOriginalTitle(),
                        model.getOriginalLanguage(),
                        model.getReleaseDate(),
                        model.getRuntime(),
                        genres,
                        model.getVoteAverage(),
                        model.getBudget(),
                        model.getRevenue(),
                        productionCompanies,
                        model.getPosterPath()
                ));
            }
            return movieBeans;
        } catch (ExceptionTmdbApi tmdbE) {
            throw new ExceptionApplicationController("Failed to search movies from TMDb API: " + tmdbE.getMessage(), tmdbE);
        }
    }

    public List<TvSeriesBean> searchAndMapTvSeries(String query) throws ExceptionApplicationController {
        try {
            List<TvSeriesModel> tvSeriesModels = TvSeriesTmdb.searchTvSeries(query);
            List<TvSeriesBean> tvSeriesBeans = new java.util.ArrayList<>();

            for (TvSeriesModel model : tvSeriesModels) {
                List<String> productionCompanies = extractProductionCompanyNames(model.getProductionCompanies());
                List<String> createdBy = extractCreatorNames(model.getCreatedBy());

                int episodeRuntime = calculateEpisodeRuntime(model.getEpisodeRunTime());

                tvSeriesBeans.add(new TvSeriesBean(
                        model.getId(),
                        episodeRuntime,
                        model.getNumberOfEpisodes(),
                        model.getName(),
                        model.getOverview(),
                        model.getOriginalName(),
                        model.getOriginalLanguage(),
                        model.getFirstAirDate(),
                        model.getLastAirDate(),
                        model.getNumberOfSeasons(),
                        model.getInProduction(),
                        model.getStatus(),
                        model.getVoteAverage(),
                        createdBy,
                        productionCompanies,
                        model.getPosterPath()
                ));
            }
            return tvSeriesBeans;
        } catch (ExceptionTmdbApi tmdbE) {
            throw new ExceptionApplicationController("Failed to search TV Series from TMDb API: " + tmdbE.getMessage(), tmdbE);
        }
    }

    private List<String> extractProductionCompanyNames(List<TvSeriesModel.ProductionCompany> companiesList) {
        List<String> names = new java.util.ArrayList<>();
        if (companiesList != null) {
            for (TvSeriesModel.ProductionCompany company : companiesList) {
                names.add(company.getName());
            }
        }
        return names;
    }

    private List<String> extractCreatorNames(List<TvSeriesModel.Creator> creatorsList) {
        List<String> names = new java.util.ArrayList<>();
        if (creatorsList != null) {
            for (TvSeriesModel.Creator creator : creatorsList) {
                names.add(creator.getName());
            }
        }
        return names;
    }

    private int calculateEpisodeRuntime(List<Integer> runTimeList) {
        return (runTimeList != null && !runTimeList.isEmpty()) ? runTimeList.get(0) : 0;
    }

    public List<AnimeBean> searchAndMapAnime(String query) throws ExceptionApplicationController {
        try {
            List<AnimeModel> animeModels = AnimeAniList.searchAnime(query);
            List<AnimeBean> animeBeans = new java.util.ArrayList<>();
            for (AnimeModel model : animeModels) {
                animeBeans.add(new AnimeBean(
                        model.getId(),
                        model.getTitle() != null ? model.getTitle().getRomaji() : null,
                        model.getDescription(),
                        model.getCoverImage() != null ? model.getCoverImage().getMedium() : null,
                        model.getEpisodes(),
                        model.getDuration(),
                        model.getCountryOfOrigin(),
                        model.getStartDate() != null ? String.format(DATE_FORMAT_PATTERN, model.getStartDate().getYear(), model.getStartDate().getMonth(), model.getStartDate().getDay()) : null,
                        model.getEndDate() != null ? String.format(DATE_FORMAT_PATTERN, model.getEndDate().getYear(), model.getEndDate().getMonth(), model.getEndDate().getDay()) : null,
                        model.getAverageScore(),
                        model.getMeanScore(),
                        model.getStatus(),
                        model.getNextAiringEpisode() != null ? "Episode " + model.getNextAiringEpisode().getEpisode() + " airing at " + model.getNextAiringEpisode().getAiringAt() : null,
                        model.getGenres() != null ? Arrays.asList(model.getGenres()) : Collections.emptyList()
                ));
            }
            return animeBeans;
        } catch (ExceptionAniListApi aniListE) {
            throw new ExceptionApplicationController("Failed to search Anime from AniList API: " + aniListE.getMessage(), aniListE);
        }
    }

    public MovieBean retrieveMovieById(int id) throws ExceptionApplicationController {
        try {
            return fetchAndMapMovieModel(id);
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to retrieve Movie details: " + e.getMessage(), e);
        }
    }

    private MovieBean fetchAndMapMovieModel(int id) throws ExceptionApplicationController {
        try {
            MovieModel model = MovieTmdb.getMovieById(id);

            if (model == null) {
                throw new ExceptionApplicationController("Movie with ID " + id + " not found or returned null model from TMDb API.");
            }

            List<String> genres = new java.util.ArrayList<>();
            if (model.getGenres() != null) {
                for (MovieModel.Genre genre : model.getGenres()) {
                    genres.add(genre.getName());
                }
            }

            List<String> productionCompanies = new java.util.ArrayList<>();
            if (model.getProductionCompanies() != null) {
                for (MovieModel.ProductionCompany company : model.getProductionCompanies()) {
                    productionCompanies.add(company.getName());
                }
            }

            return new MovieBean(
                    model.getId(),
                    model.getTitle(),
                    model.getOverview(),
                    model.getOriginalTitle(),
                    model.getOriginalLanguage(),
                    model.getReleaseDate(),
                    model.getRuntime(),
                    genres,
                    model.getVoteAverage(),
                    model.getBudget(),
                    model.getRevenue(),
                    productionCompanies,
                    model.getPosterPath()
            );
        } catch (ExceptionTmdbApi tmdbE) {
            throw new ExceptionApplicationController("Movie with ID " + id + " not found from TMDb API: " + tmdbE.getMessage(), tmdbE);
        }
    }

    public TvSeriesBean retrieveTvSeriesById(int id) throws ExceptionApplicationController {
        try {
            return fetchAndMapTvSeriesModel(id);
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to retrieve TV Series details: " + e.getMessage(), e);
        }
    }

    private TvSeriesBean fetchAndMapTvSeriesModel(int id) throws ExceptionApplicationController {
        try {
            TvSeriesModel model = TvSeriesTmdb.getTvSeriesById(id);

            if (model == null) {
                throw new ExceptionApplicationController("TV Series with ID " + id + " not found or returned null model from TMDb API.");
            }

            List<String> genres = new java.util.ArrayList<>();
            if (model.getGenres() != null) {
                for (TvSeriesModel.Genre genre : model.getGenres()) {
                    genres.add(genre.getName());
                }
            }

            List<String> productionCompanies = new java.util.ArrayList<>();
            if (model.getProductionCompanies() != null) {
                for (TvSeriesModel.ProductionCompany company : model.getProductionCompanies()) {
                    productionCompanies.add(company.getName());
                }
            }

            List<String> createdBy = new java.util.ArrayList<>();
            if (model.getCreatedBy() != null) {
                for (TvSeriesModel.Creator creator : model.getCreatedBy()) {
                    createdBy.add(creator.getName());
                }
            }

            int episodeRuntime = (model.getEpisodeRunTime() != null && !model.getEpisodeRunTime().isEmpty()) ? model.getEpisodeRunTime().get(0) : 0;

            return new TvSeriesBean(
                    model.getId(),
                    episodeRuntime,
                    model.getNumberOfEpisodes(),
                    model.getName(),
                    model.getOverview(),
                    model.getOriginalName(),
                    model.getOriginalLanguage(),
                    model.getFirstAirDate(),
                    model.getLastAirDate(),
                    model.getNumberOfSeasons(),
                    model.getInProduction(),
                    model.getStatus(),
                    model.getVoteAverage(),
                    createdBy,
                    productionCompanies,
                    model.getPosterPath()
            );
        } catch (ExceptionTmdbApi tmdbE) {
            throw new ExceptionApplicationController("TV Series with ID " + id + " not found from TMDb API: " + tmdbE.getMessage(), tmdbE);
        }
    }


    public AnimeBean retrieveAnimeById(int id) throws ExceptionApplicationController {
        try {
            AnimeModel model = AnimeAniList.getAnimeById(id);

            return new AnimeBean(
                    model.getId(),
                    model.getTitle() != null ? model.getTitle().getRomaji() : null,
                    model.getDescription(),
                    model.getCoverImage() != null ? model.getCoverImage().getMedium() : null,
                    model.getEpisodes(),
                    model.getDuration(),
                    model.getCountryOfOrigin(),
                    model.getStartDate() != null ? String.format(DATE_FORMAT_PATTERN, model.getStartDate().getYear(), model.getStartDate().getMonth(), model.getStartDate().getDay()) : null,
                    model.getEndDate() != null ? String.format(DATE_FORMAT_PATTERN, model.getEndDate().getYear(), model.getEndDate().getMonth(), model.getEndDate().getDay()) : null,
                    model.getAverageScore(),
                    model.getMeanScore(),
                    model.getStatus(),
                    model.getNextAiringEpisode() != null ? "Episode " + model.getNextAiringEpisode().getEpisode() + " airing at " + model.getNextAiringEpisode().getAiringAt() : null,
                    model.getGenres() != null ? Arrays.asList(model.getGenres()) : Collections.emptyList()
            );
        } catch (ExceptionAniListApi aniListE) {
            throw new ExceptionApplicationController("Failed to retrieve Anime details: " + aniListE.getMessage(), aniListE);
        } catch (Exception e) {
            throw new ExceptionApplicationController("An unexpected error occurred while retrieving Anime details: " + e.getMessage(), e);
        }
    }
}