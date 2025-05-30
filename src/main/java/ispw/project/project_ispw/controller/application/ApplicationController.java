package ispw.project.project_ispw.controller.application;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.controller.application.state.DemoModeState;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.controller.application.util.*;
import ispw.project.project_ispw.exception.ExceptionApplicationController;

import java.util.List;

public class ApplicationController {

    private String selectedItemCategory;
    private int selectedItemId;
    private String selectedSearchCategory;
    private String searchQuery;
    private ListBean selectedList;

    private final AuthService authService;
    private final ContentService contentService;
    private final ListManagementService listManagementService;


    public ApplicationController(PersistenceModeState persistenceState) {

        this.authService = new AuthService(persistenceState.getUserDao());

        this.contentService = new ContentService();

        ListContentDaoProvider listContentDaoProvider = new ListContentDaoProvider(
                persistenceState.getListMovieDao(),
                persistenceState.getListTvSeriesDao(),
                persistenceState.getListAnimeDao()
        );

        ContentDetailDaoProvider contentDetailDaoProvider = new ContentDetailDaoProvider(
                persistenceState.getAnimeDao(),
                persistenceState.getMovieDao(),
                persistenceState.getTvSeriesDao()
        );

        this.listManagementService = new ListManagementService(
                persistenceState.getListDao(),
                listContentDaoProvider,
                contentDetailDaoProvider,
                this.contentService
        );
    }

    public ApplicationController() {
        this(new DemoModeState());
    }

    public UserBean getCurrentUserBean() {
        // Delegate to AuthService to get the current user
        return authService.getCurrentUser();
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
        // The authService handles setting its internal currentUser upon successful login
        return authService.login(username, password);
    }

    public boolean registerUser(UserBean userBean) throws ExceptionApplicationController {
        return authService.registerUser(userBean);
    }

    public List<MovieBean> searchMovies(String query) throws ExceptionApplicationController {
        return contentService.searchAndMapMovies(query);
    }

    public List<TvSeriesBean> searchTvSeries(String query) throws ExceptionApplicationController {
        return contentService.searchAndMapTvSeries(query);
    }

    public List<AnimeBean> searchAnime(String query) throws ExceptionApplicationController {
        return contentService.searchAndMapAnime(query);
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
            // Delegate logout to AuthService
            authService.logout();

            // Clear other session-related state specific to ApplicationController
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