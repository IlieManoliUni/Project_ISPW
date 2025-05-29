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
        // Empty constructor
    }

    public List<MovieBean> searchAndMapMovies(String query) throws ExceptionApplicationController {
        try {
            List<MovieModel> movieModels = MovieTmdb.searchMovies(query);
            List<MovieBean> movieBeans = new java.util.ArrayList<>();
            for (MovieModel model : movieModels) {
                MovieBean movieBean = new MovieBean(
                        model.getId(),
                        model.getRuntime(),
                        model.getTitle()
                );

                movieBean.setOverview(model.getOverview());
                movieBean.setOriginalTitle(model.getOriginalTitle());
                movieBean.setOriginalLanguage(model.getOriginalLanguage());
                movieBean.setReleaseDate(model.getReleaseDate());
                movieBean.setVoteAverage(model.getVoteAverage());
                movieBean.setBudget(model.getBudget());
                movieBean.setRevenue(model.getRevenue());
                movieBean.setPosterPath(model.getPosterPath());

                List<String> genres = new java.util.ArrayList<>();
                if (model.getGenres() != null) {
                    for (MovieModel.Genre genre : model.getGenres()) {
                        genres.add(genre.getName());
                    }
                }
                movieBean.setGenres(genres);

                List<String> productionCompanies = new java.util.ArrayList<>();
                if (model.getProductionCompanies() != null) {
                    for (MovieModel.ProductionCompany company : model.getProductionCompanies()) {
                        productionCompanies.add(company.getName());
                    }
                }
                movieBean.setProductionCompanies(productionCompanies);

                movieBeans.add(movieBean);
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
                int episodeRuntime = calculateEpisodeRuntime(model.getEpisodeRunTime());

                TvSeriesBean tvSeriesBean = new TvSeriesBean(
                        episodeRuntime,
                        model.getId(),
                        model.getNumberOfEpisodes(),
                        model.getName()
                );

                tvSeriesBean.setOverview(model.getOverview());
                tvSeriesBean.setOriginalName(model.getOriginalName());
                tvSeriesBean.setOriginalLanguage(model.getOriginalLanguage());
                tvSeriesBean.setFirstAirDate(model.getFirstAirDate());
                tvSeriesBean.setLastAirDate(model.getLastAirDate());
                tvSeriesBean.setNumberOfSeasons(model.getNumberOfSeasons());
                tvSeriesBean.setInProduction(model.getInProduction());
                tvSeriesBean.setStatus(model.getStatus());
                tvSeriesBean.setVoteAverage(model.getVoteAverage());
                tvSeriesBean.setPosterPath(model.getPosterPath());

                List<String> createdBy = extractCreatorNames(model.getCreatedBy());
                tvSeriesBean.setCreatedBy(createdBy);

                List<String> productionCompanies = extractProductionCompanyNames(model.getProductionCompanies());
                tvSeriesBean.setProductionCompanies(productionCompanies);

                tvSeriesBeans.add(tvSeriesBean);
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
                AnimeBean animeBean = new AnimeBean(
                        model.getId(),
                        model.getDuration(),
                        model.getEpisodes(),
                        model.getTitle() != null ? model.getTitle().getRomaji() : null // Use romaji title
                );

                animeBean.setDescription(model.getDescription());
                animeBean.setCoverImageUrl(model.getCoverImage() != null ? model.getCoverImage().getMedium() : null);
                animeBean.setCountryOfOrigin(model.getCountryOfOrigin());
                animeBean.setStartDate(model.getStartDate() != null ? String.format(DATE_FORMAT_PATTERN, model.getStartDate().getYear(), model.getStartDate().getMonth(), model.getStartDate().getDay()) : null);
                animeBean.setEndDate(model.getEndDate() != null ? String.format(DATE_FORMAT_PATTERN, model.getEndDate().getYear(), model.getEndDate().getMonth(), model.getEndDate().getDay()) : null);
                animeBean.setAverageScore(model.getAverageScore());
                animeBean.setMeanScore(model.getMeanScore());
                animeBean.setStatus(model.getStatus());
                animeBean.setNextAiringEpisodeDetails(model.getNextAiringEpisode() != null ? "Episode " + model.getNextAiringEpisode().getEpisode() + " airing at " + model.getNextAiringEpisode().getAiringAt() : null);

                animeBean.setGenres(model.getGenres() != null ? Arrays.asList(model.getGenres()) : Collections.emptyList());

                animeBeans.add(animeBean);
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

            MovieBean movieBean = new MovieBean(
                    model.getId(),
                    model.getRuntime(),
                    model.getTitle()
            );

            movieBean.setOverview(model.getOverview());
            movieBean.setOriginalTitle(model.getOriginalTitle());
            movieBean.setOriginalLanguage(model.getOriginalLanguage());
            movieBean.setReleaseDate(model.getReleaseDate());
            movieBean.setVoteAverage(model.getVoteAverage());
            movieBean.setBudget(model.getBudget());
            movieBean.setRevenue(model.getRevenue());
            movieBean.setPosterPath(model.getPosterPath());

            List<String> genres = new java.util.ArrayList<>();
            if (model.getGenres() != null) {
                for (MovieModel.Genre genre : model.getGenres()) {
                    genres.add(genre.getName());
                }
            }
            movieBean.setGenres(genres);

            List<String> productionCompanies = new java.util.ArrayList<>();
            if (model.getProductionCompanies() != null) {
                for (MovieModel.ProductionCompany company : model.getProductionCompanies()) {
                    productionCompanies.add(company.getName());
                }
            }
            movieBean.setProductionCompanies(productionCompanies);

            return movieBean;
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

            int episodeRuntime = calculateEpisodeRuntime(model.getEpisodeRunTime());

            TvSeriesBean tvSeriesBean = new TvSeriesBean(
                    episodeRuntime,
                    model.getId(),
                    model.getNumberOfEpisodes(),
                    model.getName()
            );

            tvSeriesBean.setOverview(model.getOverview());
            tvSeriesBean.setOriginalName(model.getOriginalName());
            tvSeriesBean.setOriginalLanguage(model.getOriginalLanguage());
            tvSeriesBean.setFirstAirDate(model.getFirstAirDate());
            tvSeriesBean.setLastAirDate(model.getLastAirDate());
            tvSeriesBean.setNumberOfSeasons(model.getNumberOfSeasons());
            tvSeriesBean.setInProduction(model.getInProduction());
            tvSeriesBean.setStatus(model.getStatus());
            tvSeriesBean.setVoteAverage(model.getVoteAverage());
            tvSeriesBean.setPosterPath(model.getPosterPath());

            List<String> createdBy = extractCreatorNames(model.getCreatedBy());
            tvSeriesBean.setCreatedBy(createdBy);

            List<String> productionCompanies = extractProductionCompanyNames(model.getProductionCompanies());
            tvSeriesBean.setProductionCompanies(productionCompanies);

            return tvSeriesBean;
        } catch (ExceptionTmdbApi tmdbE) {
            throw new ExceptionApplicationController("TV Series with ID " + id + " not found from TMDb API: " + tmdbE.getMessage(), tmdbE);
        }
    }


    public AnimeBean retrieveAnimeById(int id) throws ExceptionApplicationController {
        try {
            AnimeModel model = AnimeAniList.getAnimeById(id);

            AnimeBean animeBean = new AnimeBean(
                    model.getId(),
                    model.getDuration(),
                    model.getEpisodes(),
                    model.getTitle() != null ? model.getTitle().getRomaji() : null // Use romaji title
            );


            animeBean.setDescription(model.getDescription());
            animeBean.setCoverImageUrl(model.getCoverImage() != null ? model.getCoverImage().getMedium() : null);
            animeBean.setCountryOfOrigin(model.getCountryOfOrigin());
            animeBean.setStartDate(model.getStartDate() != null ? String.format(DATE_FORMAT_PATTERN, model.getStartDate().getYear(), model.getStartDate().getMonth(), model.getStartDate().getDay()) : null);
            animeBean.setEndDate(model.getEndDate() != null ? String.format(DATE_FORMAT_PATTERN, model.getEndDate().getYear(), model.getEndDate().getMonth(), model.getEndDate().getDay()) : null);
            animeBean.setAverageScore(model.getAverageScore());
            animeBean.setMeanScore(model.getMeanScore());
            animeBean.setStatus(model.getStatus());
            animeBean.setNextAiringEpisodeDetails(model.getNextAiringEpisode() != null ? "Episode " + model.getNextAiringEpisode().getEpisode() + " airing at " + model.getNextAiringEpisode().getAiringAt() : null);


            animeBean.setGenres(model.getGenres() != null ? Arrays.asList(model.getGenres()) : Collections.emptyList());

            return animeBean;
        } catch (ExceptionAniListApi aniListE) {
            throw new ExceptionApplicationController("Failed to retrieve Anime details: " + aniListE.getMessage(), aniListE);
        } catch (Exception e) {
            throw new ExceptionApplicationController("An unexpected error occurred while retrieving Anime details: " + e.getMessage(), e);
        }
    }
}