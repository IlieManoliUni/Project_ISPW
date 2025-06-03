package ispw.project.project_ispw.controller.application;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.controller.application.state.DemoModeState;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.controller.application.util.*;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.model.AnimeModel;
import ispw.project.project_ispw.model.MovieModel;
import ispw.project.project_ispw.model.TvSeriesModel;

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

    public AuthService getAuthService() {
        return authService;
    }

    public UserBean getCurrentUserBean() {
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

    public boolean login(String username, String password) throws ExceptionApplication {
        return authService.login(username, password);
    }

    public boolean registerUser(UserBean userBean) throws ExceptionApplication {
        return authService.registerUser(userBean);
    }

    public List<MovieModel> searchMovies(String query) throws ExceptionApplication {
        return contentService.searchAndMapMovies(query);
    }

    public List<TvSeriesModel> searchTvSeries(String query) throws ExceptionApplication {
        return contentService.searchAndMapTvSeries(query);
    }

    public List<AnimeModel> searchAnime(String query) throws ExceptionApplication {
        return contentService.searchAndMapAnime(query);
    }

    public MovieModel retrieveMovieById(int id) throws ExceptionApplication {
        return contentService.retrieveMovieById(id);
    }

    public TvSeriesModel retrieveTvSeriesById(int id) throws ExceptionApplication {
        return contentService.retrieveTvSeriesById(id);
    }

    public AnimeModel retrieveAnimeById(int id) throws ExceptionApplication {
        return contentService.retrieveAnimeById(id);
    }

    public ListBean findListForUserByName(UserBean user, String listName) throws ExceptionApplication {
        return listManagementService.findListForUserByName(user, listName);
    }

    public ListBean getListByIdForCurrentUser(int listId, UserBean userBean) throws ExceptionApplication {
        return listManagementService.getListByIdForUser(listId, userBean);
    }

    public boolean addMovieToList(ListBean targetList, int movieId) throws ExceptionApplication {
        return listManagementService.addMovieToList(targetList, movieId);
    }

    public boolean removeMovieFromList(ListBean targetList, int movieId) throws ExceptionApplication {
        return listManagementService.removeMovieFromList(targetList, movieId);
    }

    public boolean addTvSeriesToList(ListBean targetList, int tvSeriesId) throws ExceptionApplication {
        return listManagementService.addTvSeriesToList(targetList, tvSeriesId);
    }

    public boolean removeTvSeriesFromList(ListBean targetList, int tvSeriesId) throws ExceptionApplication {
        return listManagementService.removeTvSeriesFromList(targetList, tvSeriesId);
    }

    public boolean addAnimeToList(ListBean targetList, int animeId) throws ExceptionApplication {
        return listManagementService.addAnimeToList(targetList, animeId);
    }

    public boolean removeAnimeFromList(ListBean targetList, int animeId) throws ExceptionApplication {
        return listManagementService.removeAnimeFromList(targetList, animeId);
    }

    public List<ListBean> getListsForUser(UserBean userBean) throws ExceptionApplication {
        return listManagementService.getListsForUser(userBean);
    }

    public boolean createList(ListBean newListBean, UserBean userBean) throws ExceptionApplication {
        return listManagementService.createList(newListBean, userBean);
    }

    public boolean deleteList(ListBean listBean) throws ExceptionApplication {
        return listManagementService.deleteList(listBean);
    }

    public List<MovieBean> getMoviesInList(ListBean listBean) throws ExceptionApplication {
        return listManagementService.getMoviesInList(listBean);
    }

    public List<TvSeriesBean> getTvSeriesInList(ListBean listBean) throws ExceptionApplication {
        return listManagementService.getTvSeriesInList(listBean);
    }

    public List<AnimeBean> getAnimeInList(ListBean listBean) throws ExceptionApplication {
        return listManagementService.getAnimeInList(listBean);
    }

    public void logout() throws ExceptionApplication {
        try {
            authService.logout();

            this.selectedList = null;
            this.selectedSearchCategory = null;
            this.searchQuery = null;
            this.selectedItemCategory = null;
            this.selectedItemId = 0;
        } catch (Exception e) {
            throw new ExceptionApplication("An unexpected error occurred during logout.", e);
        }
    }
}