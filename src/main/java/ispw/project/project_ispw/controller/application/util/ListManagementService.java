package ispw.project.project_ispw.controller.application.util;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.dao.ListDao;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionDao;
import ispw.project.project_ispw.model.AnimeModel;
import ispw.project.project_ispw.model.MovieModel;
import ispw.project.project_ispw.model.TvSeriesModel;

import java.util.List;

public class ListManagementService {

    private final ListDao listDao;
    private final ListContentDaoProvider listContentDaoProvider;
    private final ContentDetailDaoProvider contentDetailDaoProvider;
    private final ContentService contentService;

    public ListManagementService(ListDao listDao,
                                 ListContentDaoProvider listContentDaoProvider,
                                 ContentDetailDaoProvider contentDetailDaoProvider,
                                 ContentService contentService) {
        this.listDao = listDao;
        this.listContentDaoProvider = listContentDaoProvider;
        this.contentDetailDaoProvider = contentDetailDaoProvider;
        this.contentService = contentService;
    }

    public ListBean findListForUserByName(UserBean user, String listName) throws ExceptionApplication {
        try {
            if (listDao == null) {
                throw new ExceptionApplication("Functionality not available (List DAO missing).");
            }
            List<ListBean> userLists = listDao.retrieveAllListsOfUsername(user.getUsername());
            for (ListBean list : userLists) {
                if (list.getName() != null && list.getName().equals(listName)) {
                    return list;
                }
            }
            return null;
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to find list: " + e.getMessage(), e);
        }
    }

    public ListBean getListByIdForUser(int listId, UserBean userBean) throws ExceptionApplication {
        try {
            if (listDao == null) {
                throw new ExceptionApplication("Functionality not available (List DAO missing).");
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
            throw new ExceptionApplication("Failed to retrieve list by ID: " + e.getMessage(), e);
        }
    }

    public boolean addMovieToList(ListBean targetList, int movieId) throws ExceptionApplication {
        MovieModel movie = contentService.retrieveMovieById(movieId);
        if (movie == null) {
            throw new ExceptionApplication("Movie with ID " + movieId + " not found.");
        }
            MovieBean movieBean = new MovieBean(
                    movie.getId(),
                    movie.getRuntime(),
                    movie.getTitle());

        return addMovieToList(targetList, movieBean);
    }

    public boolean addMovieToList(ListBean targetList, MovieBean movie) throws ExceptionApplication {
        try {
            if (listContentDaoProvider.getListMovieDao() == null) {
                throw new ExceptionApplication("Functionality not available (List Movie DAO missing).");
            }
            if (contentDetailDaoProvider.getMovieDao() == null) {
                throw new ExceptionApplication("Functionality not available (Movie DAO missing).");
            }

            getOrCreateMovieInDatabase(movie);

            listContentDaoProvider.getListMovieDao().addMovieToList(targetList, movie);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to add movie to list: " + e.getMessage(), e);
        }
    }

    private void getOrCreateMovieInDatabase(MovieBean movie) throws ExceptionApplication {
        MovieBean existingMovieInDb = null;
        try {
            existingMovieInDb = contentDetailDaoProvider.getMovieDao().retrieveById(movie.getIdMovieTmdb());
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to find movie in the database: " + e.getMessage(), e);
        }

        if (existingMovieInDb == null) {
            try {
                contentDetailDaoProvider.getMovieDao().saveMovie(movie);
            } catch (Exception e) {
                throw new ExceptionApplication("Failed to save movie details to database: " + e.getMessage(), e);
            }
        }
    }

    public boolean removeMovieFromList(ListBean targetList, int movieId) throws ExceptionApplication {
        MovieModel movie = contentService.retrieveMovieById(movieId);
        if (movie == null) {
            throw new ExceptionApplication("Movie with ID " + movieId + " not found for removal.");
        }
        MovieBean movieBean = new MovieBean(
                movie.getId(),
                movie.getRuntime(),
                movie.getTitle());
        return removeMovieFromList(targetList, movieBean);
    }

    public boolean removeMovieFromList(ListBean targetList, MovieBean movie) throws ExceptionApplication {
        try {
            if (listContentDaoProvider.getListMovieDao() == null) {
                throw new ExceptionApplication("Functionality not available (List Movie DAO missing).");
            }
            listContentDaoProvider.getListMovieDao().removeMovieFromList(targetList, movie);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to remove movie from list: " + e.getMessage(), e);
        }
    }
    private int calculateEpisodeRuntime(List<Integer> runTimeList) {
        return (runTimeList != null && !runTimeList.isEmpty()) ? runTimeList.get(0) : 0;
    }

    public boolean addTvSeriesToList(ListBean targetList, int tvSeriesId) throws ExceptionApplication {
        TvSeriesModel tvSeries = contentService.retrieveTvSeriesById(tvSeriesId);
        if (tvSeries == null) {
            throw new ExceptionApplication("TV Series with ID " + tvSeriesId + " not found.");
        }
        TvSeriesBean tvSeriesBean = new TvSeriesBean(
                calculateEpisodeRuntime(tvSeries.getEpisodeRunTime()),
                tvSeries.getId(),
                tvSeries.getNumberOfEpisodes(),
                tvSeries.getName()
        );
        return addTvSeriesToList(targetList, tvSeriesBean);
    }

    public boolean addTvSeriesToList(ListBean targetList, TvSeriesBean tvSeries) throws ExceptionApplication {
        try {
            if (listContentDaoProvider.getListTvSeriesDao() == null) {
                throw new ExceptionApplication("Functionality not available (List TV Series DAO missing).");
            }
            if (contentDetailDaoProvider.getTvSeriesDao() == null) {
                throw new ExceptionApplication("Functionality not available (TvSeries DAO missing).");
            }

            getOrCreateTvSeriesInDatabase(tvSeries);

            listContentDaoProvider.getListTvSeriesDao().addTvSeriesToList(targetList, tvSeries);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to add TV Series to list: " + e.getMessage(), e);
        }
    }

    private void getOrCreateTvSeriesInDatabase(TvSeriesBean tvSeries) throws ExceptionApplication {
        TvSeriesBean existingTvSeriesInDb = null;
        try {
            existingTvSeriesInDb = contentDetailDaoProvider.getTvSeriesDao().retrieveById(tvSeries.getIdTvSeriesTmdb());
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to find TV Series in the database: " + e.getMessage(), e);
        }

        if (existingTvSeriesInDb == null) {
            try {
                contentDetailDaoProvider.getTvSeriesDao().saveTvSeries(tvSeries);
            } catch (ExceptionDao e) {
                throw new ExceptionApplication("Failed to save TV Series details to database: " + e.getMessage(), e);
            }
        }
    }

    public boolean removeTvSeriesFromList(ListBean targetList, int tvSeriesId) throws ExceptionApplication {
        TvSeriesModel tvSeries = contentService.retrieveTvSeriesById(tvSeriesId);
        if (tvSeries == null) {
            throw new ExceptionApplication("TV Series with ID " + tvSeriesId + " not found for removal.");
        }
        TvSeriesBean tvSeriesBean = new TvSeriesBean(
                calculateEpisodeRuntime(tvSeries.getEpisodeRunTime()),
                tvSeries.getId(),
                tvSeries.getNumberOfEpisodes(),
                tvSeries.getName()
        );
        return removeTvSeriesFromList(targetList, tvSeriesBean);
    }

    public boolean removeTvSeriesFromList(ListBean targetList, TvSeriesBean tvSeries) throws ExceptionApplication {
        try {
            if (listContentDaoProvider.getListTvSeriesDao() == null) {
                throw new ExceptionApplication("Functionality not available (List TV Series DAO missing).");
            }
            listContentDaoProvider.getListTvSeriesDao().removeTvSeriesFromList(targetList, tvSeries);
            return true;
        } catch (ExceptionDao e) {
            throw new ExceptionApplication("Failed to remove TV Series from list: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to remove TV Series to list: " + e.getMessage(), e);
        }
    }

    public boolean addAnimeToList(ListBean targetList, int animeId) throws ExceptionApplication {
        AnimeModel anime = contentService.retrieveAnimeById(animeId);
        if (anime == null) {
            throw new ExceptionApplication("Anime with ID " + animeId + " not found via ContentService.");
        }
        AnimeBean animeBean = new AnimeBean(
                anime.getId(),
                anime.getDuration(),
                anime.getEpisodes(),
                anime.getTitle() != null ? anime.getTitle().getRomaji() : null // Use romaji title
        );
        return addAnimeToList(targetList, animeBean);
    }

    public boolean addAnimeToList(ListBean targetList, AnimeBean anime) throws ExceptionApplication {
        try {
            if (listContentDaoProvider.getListAnimeDao() == null) {
                throw new ExceptionApplication("Functionality not available (List Anime DAO missing).");
            }
            if (contentDetailDaoProvider.getAnimeDao() == null) {
                throw new ExceptionApplication("Functionality not available (Anime DAO missing).");
            }

            getOrCreateAnimeInDatabase(anime);

            listContentDaoProvider.getListAnimeDao().addAnimeToList(targetList, anime);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to add Anime to list: " + e.getMessage(), e);
        }
    }

    private void getOrCreateAnimeInDatabase(AnimeBean anime) throws ExceptionApplication {
        AnimeBean existingAnimeInDb = null;
        try {
            existingAnimeInDb = contentDetailDaoProvider.getAnimeDao().retrieveById(anime.getIdAnimeTmdb());
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to find anime in the database: " + e.getMessage(), e);
        }

        if (existingAnimeInDb == null) {
            try {
                contentDetailDaoProvider.getAnimeDao().saveAnime(anime);
            } catch (Exception e) {
                throw new ExceptionApplication("Failed to save anime details to database: " + e.getMessage(), e);
            }
        }
    }

    public boolean removeAnimeFromList(ListBean targetList, int animeId) throws ExceptionApplication {
        AnimeModel anime = contentService.retrieveAnimeById(animeId);
        if (anime == null) {
            throw new ExceptionApplication("Anime with ID " + animeId + " not found for removal.");
        }
        AnimeBean animeBean = new AnimeBean(
                anime.getId(),
                anime.getDuration(),
                anime.getEpisodes(),
                anime.getTitle() != null ? anime.getTitle().getRomaji() : null // Use romaji title
        );
        return removeAnimeFromList(targetList, animeBean);
    }

    public boolean removeAnimeFromList(ListBean targetList, AnimeBean anime) throws ExceptionApplication {
        try {
            if (listContentDaoProvider.getListAnimeDao() == null) {
                throw new ExceptionApplication("Functionality not available (List Anime DAO missing).");
            }
            listContentDaoProvider.getListAnimeDao().removeAnimeFromList(targetList, anime);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to remove Anime from list: " + e.getMessage(), e);
        }
    }

    public List<ListBean> getListsForUser(UserBean userBean) throws ExceptionApplication {
        try {
            if (listDao == null) {
                throw new ExceptionApplication("Functionality not available (List DAO missing).");
            }
            return listDao.retrieveAllListsOfUsername(userBean.getUsername());
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to retrieve user lists: " + e.getMessage(), e);
        }
    }

    public boolean createList(ListBean newListBean, UserBean userBean) throws ExceptionApplication {
        try {
            if (listDao == null) {
                throw new ExceptionApplication("Functionality not available (List DAO missing).");
            }
            if (userBean == null) {
                throw new ExceptionApplication("Cannot create list without a logged-in user context.");
            }
            newListBean.setUsername(userBean.getUsername());
            listDao.saveList(newListBean, userBean);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to create list: " + e.getMessage(), e);
        }
    }

    public boolean deleteList(ListBean listBean) throws ExceptionApplication {
        try {
            if (listDao == null ||
                    listContentDaoProvider.getListMovieDao() == null ||
                    listContentDaoProvider.getListTvSeriesDao() == null ||
                    listContentDaoProvider.getListAnimeDao() == null) {
                throw new ExceptionApplication("Functionality not available (Missing DAOs).");
            }

            listContentDaoProvider.getListMovieDao().removeAllMoviesFromList(listBean);

            listContentDaoProvider.getListTvSeriesDao().removeAllTvSeriesFromList(listBean);

            listContentDaoProvider.getListAnimeDao().removeAllAnimesFromList(listBean);

            listDao.deleteList(listBean);
            return true;
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to delete list: " + e.getMessage(), e);
        }
    }

    public List<MovieBean> getMoviesInList(ListBean listBean) throws ExceptionApplication {
        try {
            if (listContentDaoProvider.getListMovieDao() == null) {
                throw new ExceptionApplication("Functionality not available (List Movie DAO missing).");
            }
            return listContentDaoProvider.getListMovieDao().getAllMoviesInList(listBean);
        }
        catch (Exception e) {
            throw new ExceptionApplication("Failed to retrieve movies in list: " + e.getMessage(), e);
        }
    }

    public List<TvSeriesBean> getTvSeriesInList(ListBean listBean) throws ExceptionApplication {
        try {
            if (listContentDaoProvider.getListTvSeriesDao() == null) {
                throw new ExceptionApplication("Functionality not available (List TV Series DAO missing).");
            }
            return listContentDaoProvider.getListTvSeriesDao().getAllTvSeriesInList(listBean);
        } catch (ExceptionDao e) {
            throw new ExceptionApplication("Failed to retrieve TV series in list: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to retrieve TV Series in list: " + e.getMessage(), e);
        }
    }

    public List<AnimeBean> getAnimeInList(ListBean listBean) throws ExceptionApplication {
        try {
            if (listContentDaoProvider.getListAnimeDao() == null) {
                throw new ExceptionApplication("Functionality not available (List Anime DAO missing).");
            }
            return listContentDaoProvider.getListAnimeDao().getAllAnimeInList(listBean);
        } catch (Exception e) {
            throw new ExceptionApplication("Failed to retrieve anime in list: " + e.getMessage(), e);
        }
    }
}