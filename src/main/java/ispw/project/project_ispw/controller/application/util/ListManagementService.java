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
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionDao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListManagementService {

    private static final Logger LOGGER = Logger.getLogger(ListManagementService.class.getName());

    private final ListDao listDao;
    private final ListMovie listMovieDao;
    private final ListTvSeries listTvSeriesDao;
    private final ListAnime listAnimeDao;
    private final ContentService contentService; // To retrieve content details for adding/removing by ID

    public ListManagementService(ListDao listDao, ListMovie listMovieDao, ListTvSeries listTvSeriesDao, ListAnime listAnimeDao, ContentService contentService) {
        this.listDao = listDao;
        this.listMovieDao = listMovieDao;
        this.listTvSeriesDao = listTvSeriesDao;
        this.listAnimeDao = listAnimeDao;
        this.contentService = contentService;
    }

    public ListBean findListForUserByName(UserBean user, String listName) throws ExceptionApplicationController {
        try {
            if (listDao == null) {
                LOGGER.log(Level.SEVERE, "ListDao is not initialized. Cannot find list.");
                throw new ExceptionApplicationController("Functionality not available (List DAO missing).");
            }
            List<ListBean> userLists = listDao.retrieveAllListsOfUsername(user.getUsername());
            for (ListBean list : userLists) {
                if (list.getName() != null && list.getName().equals(listName)) {
                    LOGGER.log(Level.INFO, "List '{0}' found for user '{1}'.", new Object[]{listName, user.getUsername()});
                    return list;
                }
            }
            LOGGER.log(Level.INFO, "List '{0}' not found for user '{1}'.", new Object[]{listName, user.getUsername()});
            return null;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error finding list ''{0}'' for user ''{1}'': {2}", new Object[]{listName, user.getUsername(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to find list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error finding list ''{0}'' for user ''{1}'': {2}", new Object[]{listName, user.getUsername(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to find list: " + e.getMessage(), e);
        }
    }

    public ListBean getListByIdForUser(int listId, UserBean userBean) throws ExceptionApplicationController {
        try {
            if (listDao == null) {
                LOGGER.log(Level.SEVERE, "ListDao is not initialized. Cannot retrieve list by ID.");
                throw new ExceptionApplicationController("Functionality not available (List DAO missing).");
            }
            if (userBean == null) {
                LOGGER.log(Level.WARNING, "No user logged in to check list ownership for ID: {0}", listId);
                return null;
            }

            ListBean list = listDao.retrieveById(listId);
            if (list != null && list.getUsername().equals(userBean.getUsername())) {
                LOGGER.log(Level.INFO, "List '{0}' (ID: {1}) retrieved for user '{2}'.", new Object[]{list.getName(), listId, userBean.getUsername()});
                return list;
            } else {
                LOGGER.log(Level.WARNING, "List with ID {0} not found or does not belong to user {1}.", new Object[]{listId, userBean.getUsername()});
                return null;
            }
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error retrieving list by ID {0} for user {1}: {2}", new Object[]{listId, userBean.getUsername(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve list by ID: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error retrieving list by ID {0} for user {1}: {2}", new Object[]{listId, userBean.getUsername(), e.getMessage()});
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
                LOGGER.log(Level.SEVERE, "ListMovieDao is not initialized. Cannot add movie to list.");
                throw new ExceptionApplicationController("Functionality not available (List Movie DAO missing).");
            }
            LOGGER.log(Level.INFO, "Attempting to add movie '{0}' (ID: {1}) to list '{2}' (ID: {3})",
                    new Object[]{movie.getTitle(), movie.getIdMovieTmdb(), targetList.getName(), targetList.getId()});
            listMovieDao.addMovieToList(targetList, movie);
            LOGGER.log(Level.INFO, "Movie '{0}' added to list '{1}' successfully.", new Object[]{movie.getTitle(), targetList.getName()});
            return true;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error adding movie to list: {0}", e.getMessage());
            throw new ExceptionApplicationController("Failed to add movie to list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error adding movie to list: {0}", e.getMessage());
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
                LOGGER.log(Level.SEVERE, "ListMovieDao is not initialized. Cannot remove movie from list.");
                throw new ExceptionApplicationController("Functionality not available (List Movie DAO missing).");
            }
            LOGGER.log(Level.INFO, "Attempting to remove movie '{0}' (ID: {1}) from list '{2}' (ID: {3})",
                    new Object[]{movie.getTitle(), movie.getIdMovieTmdb(), targetList.getName(), targetList.getId()});
            listMovieDao.removeMovieFromList(targetList, movie);
            LOGGER.log(Level.INFO, "Movie '{0}' removed from list '{1}' successfully.", new Object[]{movie.getTitle(), targetList.getName()});
            return true;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error removing movie from list: {0}", e.getMessage());
            throw new ExceptionApplicationController("Failed to remove movie from list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error removing movie from list: {0}", e.getMessage());
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
                LOGGER.log(Level.SEVERE, "ListTvSeriesDao is not initialized. Cannot add TV series to list.");
                throw new ExceptionApplicationController("Functionality not available (List TV Series DAO missing).");
            }
            LOGGER.log(Level.INFO, "Attempting to add TV Series '{0}' (ID: {1}) to list '{2}' (ID: {3})",
                    new Object[]{tvSeries.getName(), tvSeries.getIdTvSeriesTmdb(), targetList.getName(), targetList.getId()});
            listTvSeriesDao.addTvSeriesToList(targetList, tvSeries);
            LOGGER.log(Level.INFO, "TV Series '{0}' added to list '{1}' successfully.", new Object[]{tvSeries.getName(), targetList.getName()});
            return true;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error adding TV Series to list: {0}", e.getMessage());
            throw new ExceptionApplicationController("Failed to add TV Series to list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error adding TV Series to list: {0}", e.getMessage());
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
                LOGGER.log(Level.SEVERE, "ListTvSeriesDao is not initialized. Cannot remove TV series from list.");
                throw new ExceptionApplicationController("Functionality not available (List TV Series DAO missing).");
            }
            LOGGER.log(Level.INFO, "Attempting to remove TV Series '{0}' (ID: {1}) from list '{2}' (ID: {3})",
                    new Object[]{tvSeries.getName(), tvSeries.getIdTvSeriesTmdb(), targetList.getName(), targetList.getId()});
            listTvSeriesDao.removeTvSeriesFromList(targetList, tvSeries);
            LOGGER.log(Level.INFO, "TV Series '{0}' removed from list '{1}' successfully.", new Object[]{tvSeries.getName(), targetList.getName()});
            return true;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error removing TV Series from list: {0}", e.getMessage());
            throw new ExceptionApplicationController("Failed to remove TV Series from list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error removing TV Series from list: {0}", e.getMessage());
            throw new ExceptionApplicationController("Failed to remove TV Series from list: " + e.getMessage(), e);
        }
    }

    public boolean addAnimeToList(ListBean targetList, int animeId) throws ExceptionApplicationController {
        AnimeBean anime = contentService.retrieveAnimeById(animeId);
        if (anime == null) {
            throw new ExceptionApplicationController("Anime with ID " + animeId + " not found.");
        }
        return addAnimeToList(targetList, anime);
    }

    public boolean addAnimeToList(ListBean targetList, AnimeBean anime) throws ExceptionApplicationController {
        try {
            if (listAnimeDao == null) {
                LOGGER.log(Level.SEVERE, "ListAnimeDao is not initialized. Cannot add anime to list.");
                throw new ExceptionApplicationController("Functionality not available (List Anime DAO missing).");
            }
            LOGGER.log(Level.INFO, "Attempting to add Anime '{0}' (ID: {1}) to list '{2}' (ID: {3})",
                    new Object[]{anime.getTitle(), anime.getIdAnimeTmdb(), targetList.getName(), targetList.getId()});
            listAnimeDao.addAnimeToList(targetList, anime);
            LOGGER.log(Level.INFO, "Anime '{0}' added to list '{1}' successfully.", new Object[]{anime.getTitle(), targetList.getName()});
            return true;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error adding Anime to list: {0}", e.getMessage());
            throw new ExceptionApplicationController("Failed to add Anime to list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error adding Anime to list: {0}", e.getMessage());
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
                LOGGER.log(Level.SEVERE, "ListAnimeDao is not initialized. Cannot remove anime from list.");
                throw new ExceptionApplicationController("Functionality not available (List Anime DAO missing).");
            }
            LOGGER.log(Level.INFO, "Attempting to remove Anime '{0}' (ID: {1}) from list '{2}' (ID: {3})",
                    new Object[]{anime.getTitle(), anime.getIdAnimeTmdb(), targetList.getName(), targetList.getId()});
            listAnimeDao.removeAnimeFromList(targetList, anime);
            LOGGER.log(Level.INFO, "Anime '{0}' removed from list '{1}' successfully.", new Object[]{anime.getTitle(), targetList.getName()});
            return true;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error removing Anime from list: {0}", e.getMessage());
            throw new ExceptionApplicationController("Failed to remove Anime from list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error removing Anime from list: {0}", e.getMessage());
            throw new ExceptionApplicationController("Failed to remove Anime from list: " + e.getMessage(), e);
        }
    }

    public List<ListBean> getListsForUser(UserBean userBean) throws ExceptionApplicationController {
        try {
            if (listDao == null) {
                LOGGER.log(Level.SEVERE, "ListDao is not initialized. Cannot get lists.");
                throw new ExceptionApplicationController("Functionality not available (List DAO missing).");
            }
            LOGGER.log(Level.INFO, "Retrieving lists for user {0}", userBean.getUsername());
            return listDao.retrieveAllListsOfUsername(userBean.getUsername());
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error getting lists for user {0}: {1}", new Object[]{userBean.getUsername(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve user lists: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error getting lists for user {0}: {1}", new Object[]{userBean.getUsername(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve user lists: " + e.getMessage(), e);
        }
    }

    public boolean createList(ListBean newListBean, UserBean userBean) throws ExceptionApplicationController {
        try {
            if (listDao == null) {
                LOGGER.log(Level.SEVERE, "ListDao is not initialized. Cannot create list.");
                throw new ExceptionApplicationController("Functionality not available (List DAO missing).");
            }
            if (userBean == null) {
                LOGGER.log(Level.WARNING, "Cannot create list: No user bean provided for list creation.");
                throw new ExceptionApplicationController("Cannot create list without a logged-in user context.");
            }
            newListBean.setUsername(userBean.getUsername());
            listDao.saveList(newListBean, userBean);
            LOGGER.log(Level.INFO, "List {0} created successfully for user {1}.", new Object[]{newListBean.getName(), newListBean.getUsername()});
            return true;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error creating list {0} for user {1}: {2}", new Object[]{newListBean.getName(), newListBean.getUsername(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to create list: " + e.getMessage(), e);
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error creating list {0} for user {1}: {2}", new Object[]{newListBean.getName(), newListBean.getUsername(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to create list: " + e.getMessage(), e);
        }
    }

    public boolean deleteList(ListBean listBean) throws ExceptionApplicationController {
        try {
            if (listDao == null) {
                LOGGER.log(Level.SEVERE, "ListDao is not initialized. Cannot delete list.");
                throw new ExceptionApplicationController("Functionality not available (List DAO missing).");
            }
            listDao.deleteList(listBean);
            LOGGER.log(Level.INFO, "List {0} (ID: {1}) deleted for user {2}.", new Object[]{listBean.getName(), listBean.getId(), listBean.getUsername()});
            return true;
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error deleting list {0} for user {1}: {2}", new Object[]{listBean.getName(), listBean.getUsername(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to delete list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error deleting list {0} for user {1}: {2}", new Object[]{listBean.getName(), listBean.getUsername(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to delete list: " + e.getMessage(), e);
        }
    }

    public List<MovieBean> getMoviesInList(ListBean listBean) throws ExceptionApplicationController {
        try {
            if (listMovieDao == null) {
                LOGGER.log(Level.SEVERE, "ListMovieDao is not initialized. Cannot retrieve movies in list.");
                throw new ExceptionApplicationController("Functionality not available (List Movie DAO missing).");
            }
            LOGGER.log(Level.INFO, "Retrieving movies for list ID {0}", listBean.getId());
            return listMovieDao.getAllMoviesInList(listBean);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error retrieving movies for list ID {0}: {1}", new Object[]{listBean.getId(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve movies in list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error retrieving movies for list ID {0}: {1}", new Object[]{listBean.getId(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve movies in list: " + e.getMessage(), e);
        }
    }

    public List<TvSeriesBean> getTvSeriesInList(ListBean listBean) throws ExceptionApplicationController {
        try {
            if (listTvSeriesDao == null) {
                LOGGER.log(Level.SEVERE, "ListTvSeriesDao is not initialized. Cannot retrieve TV series in list.");
                throw new ExceptionApplicationController("Functionality not available (List TV Series DAO missing).");
            }
            LOGGER.log(Level.INFO, "Retrieving TV series for list ID {0}", listBean.getId());
            return listTvSeriesDao.getAllTvSeriesInList(listBean);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error retrieving TV series for list ID {0}: {1}", new Object[]{listBean.getId(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve TV series in list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error retrieving TV series for list ID {0}: {1}", new Object[]{listBean.getId(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve TV Series in list: " + e.getMessage(), e);
        }
    }

    public List<AnimeBean> getAnimeInList(ListBean listBean) throws ExceptionApplicationController {
        try {
            if (listAnimeDao == null) {
                LOGGER.log(Level.SEVERE, "ListAnimeDao is not initialized. Cannot retrieve anime in list.");
                throw new ExceptionApplicationController("Functionality not available (List Anime DAO missing).");
            }
            LOGGER.log(Level.INFO, "Retrieving anime for list ID {0}", listBean.getId());
            return listAnimeDao.getAllAnimesInList(listBean);
        } catch (ExceptionDao e) {
            LOGGER.log(Level.SEVERE, "DAO error retrieving anime for list ID {0}: {1}", new Object[]{listBean.getId(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve anime in list: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error retrieving anime for list ID {0}: {1}", new Object[]{listBean.getId(), e.getMessage()});
            throw new ExceptionApplicationController("Failed to retrieve anime in list: " + e.getMessage(), e);
        }
    }
}