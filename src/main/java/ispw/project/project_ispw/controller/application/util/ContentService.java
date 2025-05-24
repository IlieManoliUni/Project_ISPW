package ispw.project.project_ispw.controller.application.util;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContentService {

    private static final Logger LOGGER = Logger.getLogger(ContentService.class.getName());

    private final MovieDao movieDao;
    private final TvSeriesDao tvSeriesDao;
    private final AnimeDao animeDao;

    public ContentService(MovieDao movieDao, TvSeriesDao tvSeriesDao, AnimeDao animeDao) {
        this.movieDao = movieDao;
        this.tvSeriesDao = tvSeriesDao;
        this.animeDao = animeDao;
    }

    public List<?> searchContent(String category, String query) throws ExceptionApplicationController {
        try {
            LOGGER.log(Level.INFO, "Performing search for category: {0}, query: {1}", new Object[]{category, query});
            switch (category) {
                case "Movie":
                    // This would ideally interact with movieDao or an external API client
                    return Collections.emptyList(); // Replace with actual results
                case "TV Series":
                    // This would ideally interact with tvSeriesDao or an external API client
                    return Collections.emptyList(); // Replace with actual results
                case "Anime":
                    // This would ideally interact with animeDao or an external API client
                    return Collections.emptyList(); // Replace with actual results
                default:
                    LOGGER.log(Level.WARNING, "Invalid search category: {0}", category);
                    return Collections.emptyList();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during content search for category {0}, query {1}: {2}", new Object[]{category, query, e.getMessage()});
            throw new ExceptionApplicationController("Failed to perform search: " + e.getMessage(), e);
        }
    }

    public MovieBean retrieveMovieById(int id) throws ExceptionApplicationController {
        try {
            if (movieDao == null) {
                LOGGER.log(Level.SEVERE, "MovieDao is not initialized. Cannot retrieve movie by ID.");
                throw new ExceptionApplicationController("Functionality not available (Movie DAO missing).");
            }
            MovieBean movie = movieDao.retrieveById(id);
            if (movie != null) {
                LOGGER.log(Level.INFO, "Movie '{0}' (ID: {1}) retrieved successfully.", new Object[]{movie.getTitle(), id});
                return movie;
            }

            // --- TEMPORARY MOCK MOVIE (fallback if not found in DAO) ---
            if (id == 100) {
                LOGGER.log(Level.INFO, "Returning mock movie for ID: {0}", id);
                return new MovieBean(id, "Mock Movie Title", "A very exciting mock movie overview.",
                        "Original Mock Title", "en", "2023-01-01", 120,
                        List.of("Action", "Adventure"), 7.5, 100000000, 500000000,
                        List.of("Mock Productions"), "/path/to/mock/poster.jpg");
            }
            // --- END TEMPORARY MOCK MOVIE ---

            throw new ExceptionApplicationController("Movie with ID " + id + " not found.");

        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error retrieving movie by ID {0}: {1}", new Object[]{id, e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve movie details: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error retrieving movie by ID {0}: {1}", new Object[]{id, e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve movie details: " + e.getMessage(), e);
        }
    }

    public TvSeriesBean retrieveTvSeriesById(int id) throws ExceptionApplicationController {
        try {
            if (tvSeriesDao == null) {
                LOGGER.log(Level.SEVERE, "TvSeriesDao is not initialized. Cannot retrieve TV series by ID.");
                throw new ExceptionApplicationController("Functionality not available (TV Series DAO missing).");
            }
            TvSeriesBean tvSeries = tvSeriesDao.retrieveById(id);
            if (tvSeries != null) {
                LOGGER.log(Level.INFO, "TV Series '{0}' (ID: {1}) retrieved successfully.", new Object[]{tvSeries.getName(), id});
                return tvSeries;
            }

            // --- TEMPORARY MOCK TV SERIES ---
            if (id == 200) {
                LOGGER.log(Level.INFO, "Returning mock TV Series for ID: {0}", id);
                return new TvSeriesBean(id, 45, 10, "Mock Series Name",
                        "An engaging mock TV series overview.", "Original Mock Series", "en",
                        "2022-03-01", "2022-05-10", 1, false, "Ended",
                        8.2, List.of("Creator A", "Creator B"), List.of("Mock TV Inc."), "/path/to/mock/tv_poster.jpg");
            }
            // --- END TEMPORARY MOCK TV SERIES ---
            throw new ExceptionApplicationController("TV Series with ID " + id + " not found.");
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error retrieving TV Series by ID {0}: {1}", new Object[]{id, e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve TV Series details: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error retrieving TV Series by ID {0}: {1}", new Object[]{id, e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve TV Series details: " + e.getMessage(), e);
        }
    }

    public AnimeBean retrieveAnimeById(int id) throws ExceptionApplicationController {
        try {
            if (animeDao == null) {
                LOGGER.log(Level.SEVERE, "AnimeDao is not initialized. Cannot retrieve anime by ID.");
                throw new ExceptionApplicationController("Functionality not available (Anime DAO missing).");
            }
            AnimeBean anime = animeDao.retrieveById(id);
            if (anime != null) {
                LOGGER.log(Level.INFO, "Anime '{0}' (ID: {1}) retrieved successfully.", new Object[]{anime.getTitle(), id});
                return anime;
            }

            // --- TEMPORARY MOCK ANIME ---
            if (id == 300) {
                LOGGER.log(Level.INFO, "Returning mock Anime for ID: {0}", id);
                return new AnimeBean(id, "Mock Anime Title", "A fantastical journey in a mock anime world.",
                        "https://example.com/mock_anime_cover.jpg", 24, 23, "JP",
                        "2021-07-01", "2021-12-16", 85, 80, "Finished",
                        "Episode 25 airing on 2022-01-01", List.of("Fantasy", "Action"));
            }
            // --- END TEMPORARY MOCK ANIME ---
            throw new ExceptionApplicationController("Anime with ID " + id + " not found.");
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error retrieving Anime by ID {0}: {1}", new Object[]{id, e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve Anime details: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error retrieving Anime by ID {0}: {1}", new Object[]{id, e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve Anime details: " + e.getMessage(), e);
        }
    }
}