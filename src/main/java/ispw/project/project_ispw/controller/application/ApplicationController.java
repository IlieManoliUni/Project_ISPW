package ispw.project.project_ispw.controller.application;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.controller.application.state.DemoModeState;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.exception.ExceptionApplicationController;

import java.util.List;

import ispw.project.project_ispw.controller.application.util.AuthService;
import ispw.project.project_ispw.controller.application.util.ContentService;
import ispw.project.project_ispw.controller.application.util.ListManagementService;

public class ApplicationController {

    private UserBean currentUser;
    private String selectedItemCategory;
    private int selectedItemId;
    private String selectedSearchCategory;
    private String searchQuery;
    private ListBean selectedList;

    private final PersistenceModeState persistenceState;

    private final AuthService authService;
    private final ContentService contentService;
    private final ListManagementService listManagementService;


    public ApplicationController(PersistenceModeState persistenceState) {
        this.persistenceState = persistenceState;

        this.authService = new AuthService(persistenceState.getUserDao());

        this.contentService = new ContentService();

        this.listManagementService = new ListManagementService(
                persistenceState.getListDao(),
                persistenceState.getListMovieDao(),
                persistenceState.getListTvSeriesDao(),
                persistenceState.getListAnimeDao(),
                this.contentService,
                persistenceState.getAnimeDao(),
                persistenceState.getMovieDao(),
                persistenceState.getTvSeriesDao()
        );
    }

    public ApplicationController() {
        this(new DemoModeState());
    }

    public UserBean getCurrentUserBean() {
        return currentUser;
    }

    public void setCurrentUserBean(UserBean currentUser) {
        this.currentUser = currentUser;
    }

    public String getSelectedItemCategory() {
        return selectedItemCategory;
    }

    public void setSelectedItemCategory(String selectedItemCategory) {
        this.selectedItemCategory = selectedItemCategory;
    }

    public int getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(int selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public String getSelectedSearchCategory() {
        return selectedSearchCategory;
    }

    public void setSelectedSearchCategory(String selectedSearchCategory) {
        this.selectedSearchCategory = selectedSearchCategory;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public ListBean getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(ListBean selectedList) {
        this.selectedList = selectedList;
    }

    public boolean login(String username, String password) throws ExceptionApplicationController {
        boolean success = authService.login(username, password);
        if (success) {
            setCurrentUserBean(authService.getCurrentUser());
        }
        return success;
    }

    public boolean registerUser(UserBean userBean) throws ExceptionApplicationController {
        return authService.registerUser(userBean);
    }

    public List<?> searchContent(String category, String query) throws ExceptionApplicationController {
        try {
            switch (category) {
                case "Movie":
                    return contentService.searchAndMapMovies(query);
                case "TvSeries":
                    return contentService.searchAndMapTvSeries(query);
                case "Anime":
                    return contentService.searchAndMapAnime(query);
                default:
                    throw new ExceptionApplicationController("Invalid content category: " + category);
            }
        } catch (ExceptionApplicationController e) {
            // Re-throw specific application exceptions already wrapped by helper methods
            throw e;
        } catch (Exception e) {
            // Catch any other unexpected generic exceptions
            throw new ExceptionApplicationController("An unexpected error occurred during content search.", e);
        }
    }

    public MovieBean retrieveMovieById(int id) throws ExceptionApplicationController {
        return contentService.retrieveMovieById(id);
    }

    public TvSeriesBean retrieveTvSeriesById(int id) throws ExceptionApplicationController {
        return contentService.retrieveTvSeriesById(id);
    }

    public AnimeBean retrieveAnimeById(int id) throws ExceptionApplicationController {
        return contentService.retrieveAnimeById(id);
    }

    public ListBean findListForUserByName(UserBean user, String listName) throws ExceptionApplicationController {
        return listManagementService.findListForUserByName(user, listName);
    }

    public ListBean getListByIdForCurrentUser(int listId, UserBean userBean) throws ExceptionApplicationController {
        return listManagementService.getListByIdForUser(listId, userBean);
    }

    public boolean addMovieToList(ListBean targetList, int movieId) throws ExceptionApplicationController {
        return listManagementService.addMovieToList(targetList, movieId);
    }

    public boolean removeMovieFromList(ListBean targetList, int movieId) throws ExceptionApplicationController {
        return listManagementService.removeMovieFromList(targetList, movieId);
    }

    public boolean addTvSeriesToList(ListBean targetList, int tvSeriesId) throws ExceptionApplicationController {
        return listManagementService.addTvSeriesToList(targetList, tvSeriesId);
    }

    public boolean removeTvSeriesFromList(ListBean targetList, int tvSeriesId) throws ExceptionApplicationController {
        return listManagementService.removeTvSeriesFromList(targetList, tvSeriesId);
    }

    public boolean addAnimeToList(ListBean targetList, int animeId) throws ExceptionApplicationController {
        return listManagementService.addAnimeToList(targetList, animeId);
    }

    public boolean removeAnimeFromList(ListBean targetList, int animeId) throws ExceptionApplicationController {
        return listManagementService.removeAnimeFromList(targetList, animeId);
    }

    public List<ListBean> getListsForUser(UserBean userBean) throws ExceptionApplicationController {
        return listManagementService.getListsForUser(userBean);
    }

    public boolean createList(ListBean newListBean, UserBean userBean) throws ExceptionApplicationController {
        return listManagementService.createList(newListBean, userBean);
    }

    public boolean deleteList(ListBean listBean) throws ExceptionApplicationController {
        return listManagementService.deleteList(listBean);
    }

    public List<MovieBean> getMoviesInList(ListBean listBean) throws ExceptionApplicationController {
        return listManagementService.getMoviesInList(listBean);
    }

    public List<TvSeriesBean> getTvSeriesInList(ListBean listBean) throws ExceptionApplicationController {
        return listManagementService.getTvSeriesInList(listBean);
    }

    public List<AnimeBean> getAnimeInList(ListBean listBean) throws ExceptionApplicationController {
        return listManagementService.getAnimeInList(listBean);
    }

    public void logout() throws ExceptionApplicationController {
        try {
            this.currentUser = null;
            this.selectedList = null;
            this.selectedSearchCategory = null;
            this.searchQuery = null;
            this.selectedItemCategory = null;
            this.selectedItemId = 0;
        } catch (Exception e) {
            throw new ExceptionApplicationController("An unexpected error occurred during logout.", e);
        }
    }
}