package ispw.project.project_ispw.controller.application.util;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.ListAnime;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.dao.ListMovie;
import ispw.project.project_ispw.dao.ListTvSeries;
import ispw.project.project_ispw.dao.AnimeDao;
import ispw.project.project_ispw.dao.MovieDao;
import ispw.project.project_ispw.dao.TvSeriesDao;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionDao;
import ispw.project.project_ispw.exception.ExceptionDatabase;

import java.util.List;

public class ListManagementService {

    private final ListDao listDao;
    private final ListMovie listMovieDao;
    private final ListTvSeries listTvSeriesDao;
    private final ListAnime listAnimeDao;
    private final ContentService contentService;
    private final AnimeDao animeDao;
    private final MovieDao movieDao;
    private final TvSeriesDao tvSeriesDao;

    public ListManagementService(ListDao listDao, ListMovie listMovieDao, ListTvSeries listTvSeriesDao,
                                 ListAnime listAnimeDao, ContentService contentService,
                                 AnimeDao animeDao, MovieDao movieDao, TvSeriesDao tvSeriesDao) {
        this.listDao = listDao;
        this.listMovieDao = listMovieDao;
        this.listTvSeriesDao = listTvSeriesDao;
        this.listAnimeDao = listAnimeDao;
        this.contentService = contentService;
        this.animeDao = animeDao;
        this.movieDao = movieDao;
        this.tvSeriesDao = tvSeriesDao;
    }

    public ListBean findListForUserByName(UserBean user, String listName) throws ExceptionApplicationController {
        try {
            if (listDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List DAO missing).");
            }
            List<ListBean> userLists = listDao.retrieveAllListsOfUsername(user.getUsername());
            for (ListBean list : userLists) {
                if (list.getName() != null && list.getName().equals(listName)) {
                    return list;
                }
            }
            return null;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to find list: " + e.getMessage(), e);
        }
    }

    public ListBean getListByIdForUser(int listId, UserBean userBean) throws ExceptionApplicationController {
        try {
            if (listDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List DAO missing).");
            }
            if (userBean == null) {
                return null;
            }

            ListBean list = listDao.retrieveById(listId);
            if (list != null && list.getUsername().equals(userBean.getUsername())) {
                return list;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to retrieve list by ID: " + e.getMessage(), e);
        }
    }

    public boolean addMovieToList(ListBean targetList, int movieId) throws ExceptionApplicationController {
        MovieBean movie = contentService.retrieveMovieById(movieId);
        if (movie == null) {
            throw new ExceptionApplicationController("Movie with ID " + movieId + " not found.");
        }
        return addMovieToList(targetList, movie);
    }

    public boolean addMovieToList(ListBean targetList, MovieBean movie) throws ExceptionApplicationController {
        try {
            if (listMovieDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List Movie DAO missing).");
            }
            if (movieDao == null) {
                throw new ExceptionApplicationController("Functionality not available (Movie DAO missing).");
            }

            MovieBean existingMovieInDb = null;
            try {
                existingMovieInDb = movieDao.retrieveById(movie.getIdMovieTmdb());
            } catch (Exception e) {
                throw new ExceptionDatabase("Failed to find movie.", e);
            }

            if (existingMovieInDb == null) {
                try {
                    movieDao.saveMovie(movie);
                } catch (ExceptionDao e) {
                    throw new ExceptionApplicationController("Failed to save movie details to database: " + e.getMessage(), e);
                }
            }

            listMovieDao.addMovieToList(targetList, movie);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to add movie to list: " + e.getMessage(), e);
        }
    }

    public boolean removeMovieFromList(ListBean targetList, int movieId) throws ExceptionApplicationController {
        MovieBean movie = contentService.retrieveMovieById(movieId);
        if (movie == null) {
            throw new ExceptionApplicationController("Movie with ID " + movieId + " not found for removal.");
        }
        return removeMovieFromList(targetList, movie);
    }

    public boolean removeMovieFromList(ListBean targetList, MovieBean movie) throws ExceptionApplicationController {
        try {
            if (listMovieDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List Movie DAO missing).");
            }
            listMovieDao.removeMovieFromList(targetList, movie);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to remove movie from list: " + e.getMessage(), e);
        }
    }

    public boolean addTvSeriesToList(ListBean targetList, int tvSeriesId) throws ExceptionApplicationController {
        TvSeriesBean tvSeries = contentService.retrieveTvSeriesById(tvSeriesId);
        if (tvSeries == null) {
            throw new ExceptionApplicationController("TV Series with ID " + tvSeriesId + " not found.");
        }
        return addTvSeriesToList(targetList, tvSeries);
    }

    public boolean addTvSeriesToList(ListBean targetList, TvSeriesBean tvSeries) throws ExceptionApplicationController {
        try {
            if (listTvSeriesDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List TV Series DAO missing).");
            }
            if (tvSeriesDao == null) {
                throw new ExceptionApplicationController("Functionality not available (TvSeries DAO missing).");
            }

            TvSeriesBean existingTvSeriesInDb = null;
            try {
                existingTvSeriesInDb = tvSeriesDao.retrieveById(tvSeries.getIdTvSeriesTmdb());
            } catch (Exception e) {
                throw new ExceptionDatabase("Failed to find Tv Series.", e);
            }

            if (existingTvSeriesInDb == null) {
                try {
                    tvSeriesDao.saveTvSeries(tvSeries);
                } catch (ExceptionDao e) {
                    throw new ExceptionApplicationController("Failed to save TV Series details to database: " + e.getMessage(), e);
                }
            }

            listTvSeriesDao.addTvSeriesToList(targetList, tvSeries);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to add TV Series to list: " + e.getMessage(), e);
        }
    }

    public boolean removeTvSeriesFromList(ListBean targetList, int tvSeriesId) throws ExceptionApplicationController {
        TvSeriesBean tvSeries = contentService.retrieveTvSeriesById(tvSeriesId);
        if (tvSeries == null) {
            throw new ExceptionApplicationController("TV Series with ID " + tvSeriesId + " not found for removal.");
        }
        return removeTvSeriesFromList(targetList, tvSeries);
    }

    public boolean removeTvSeriesFromList(ListBean targetList, TvSeriesBean tvSeries) throws ExceptionApplicationController {
        try {
            if (listTvSeriesDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List TV Series DAO missing).");
            }
            listTvSeriesDao.removeTvSeriesFromList(targetList, tvSeries);
            return true;
        } catch (ExceptionDao e) {
            throw new ExceptionApplicationController("Failed to remove TV Series from list: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to remove TV Series to list: " + e.getMessage(), e);
        }
    }

    public boolean addAnimeToList(ListBean targetList, int animeId) throws ExceptionApplicationController {
        AnimeBean anime = contentService.retrieveAnimeById(animeId);
        if (anime == null) {
            throw new ExceptionApplicationController("Anime with ID " + animeId + " not found via ContentService.");
        }
        return addAnimeToList(targetList, anime);
    }

    public boolean addAnimeToList(ListBean targetList, AnimeBean anime) throws ExceptionApplicationController {
        try {
            if (listAnimeDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List Anime DAO missing).");
            }
            if (animeDao == null) {
                throw new ExceptionApplicationController("Functionality not available (Anime DAO missing).");
            }

            AnimeBean existingAnimeInDb = null;
            try {
                existingAnimeInDb = animeDao.retrieveById(anime.getIdAnimeTmdb());
            } catch (Exception e) {
                throw new ExceptionDatabase("Failed to find anime.", e);
            }

            if (existingAnimeInDb == null) {
                try {
                    animeDao.saveAnime(anime);
                } catch (ExceptionDao e) {
                    throw new ExceptionApplicationController("Failed to save anime details to database: " + e.getMessage(), e);
                }
            }

            listAnimeDao.addAnimeToList(targetList, anime);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to add Anime to list: " + e.getMessage(), e);
        }
    }

    public boolean removeAnimeFromList(ListBean targetList, int animeId) throws ExceptionApplicationController {
        AnimeBean anime = contentService.retrieveAnimeById(animeId);
        if (anime == null) {
            throw new ExceptionApplicationController("Anime with ID " + animeId + " not found for removal.");
        }
        return removeAnimeFromList(targetList, anime);
    }

    public boolean removeAnimeFromList(ListBean targetList, AnimeBean anime) throws ExceptionApplicationController {
        try {
            if (listAnimeDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List Anime DAO missing).");
            }
            listAnimeDao.removeAnimeFromList(targetList, anime);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to remove Anime from list: " + e.getMessage(), e);
        }
    }

    public List<ListBean> getListsForUser(UserBean userBean) throws ExceptionApplicationController {
        try {
            if (listDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List DAO missing).");
            }
            return listDao.retrieveAllListsOfUsername(userBean.getUsername());
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to retrieve user lists: " + e.getMessage(), e);
        }
    }

    public boolean createList(ListBean newListBean, UserBean userBean) throws ExceptionApplicationController {
        try {
            if (listDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List DAO missing).");
            }
            if (userBean == null) {
                throw new ExceptionApplicationController("Cannot create list without a logged-in user context.");
            }
            newListBean.setUsername(userBean.getUsername());
            listDao.saveList(newListBean, userBean);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to create list: " + e.getMessage(), e);
        }
    }

    public boolean deleteList(ListBean listBean) throws ExceptionApplicationController {
        try {
            if (listDao == null || listMovieDao == null || listTvSeriesDao == null || listAnimeDao == null) {
                throw new ExceptionApplicationController("Functionality not available (Missing DAOs).");
            }

            listMovieDao.removeAllMoviesFromList(listBean);

            listTvSeriesDao.removeAllTvSeriesFromList(listBean);

            listAnimeDao.removeAllAnimesFromList(listBean);

            listDao.deleteList(listBean);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to delete list: " + e.getMessage(), e);
        }
    }

    public List<MovieBean> getMoviesInList(ListBean listBean) throws ExceptionApplicationController {
        try {
            if (listMovieDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List Movie DAO missing).");
            }
            return listMovieDao.getAllMoviesInList(listBean);
        }
        catch (Exception e) {
            throw new ExceptionApplicationController("Failed to retrieve movies in list: " + e.getMessage(), e);
        }
    }

    public List<TvSeriesBean> getTvSeriesInList(ListBean listBean) throws ExceptionApplicationController {
        try {
            if (listTvSeriesDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List TV Series DAO missing).");
            }
            return listTvSeriesDao.getAllTvSeriesInList(listBean);
        } catch (ExceptionDao e) {
            throw new ExceptionApplicationController("Failed to retrieve TV series in list: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to retrieve TV Series in list: " + e.getMessage(), e);
        }
    }

    public List<AnimeBean> getAnimeInList(ListBean listBean) throws ExceptionApplicationController {
        try {
            if (listAnimeDao == null) {
                throw new ExceptionApplicationController("Functionality not available (List Anime DAO missing).");
            }
            return listAnimeDao.getAllAnimeInList(listBean);
        } catch (Exception e) {
            throw new ExceptionApplicationController("Failed to retrieve anime in list: " + e.getMessage(), e);
        }
    }
}