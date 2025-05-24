package ispw.project.project_ispw.controller.application;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.bean.MovieBean;
import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.controller.application.state.DemoModeState;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.dao.UserDao; // Still needed for internal state init if passed directly

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Import the new services
import ispw.project.project_ispw.controller.application.util.AuthService;
import ispw.project.project_ispw.controller.application.util.ContentService;
import ispw.project.project_ispw.controller.application.util.ListManagementService;


public class ApplicationController {

    private static final Logger LOGGER = Logger.getLogger(ApplicationController.class.getName());

    // --- Application State ---
    private UserBean currentUser;
    private String selectedItemCategory;
    private int selectedItemId;
    private String selectedSearchCategory;
    private String searchQuery;
    private ListBean selectedList;

    // --- Persistence Mode State (the Context in State Pattern) ---
    private final PersistenceModeState persistenceState; // Still holds the DAOs

    // --- New Service Instances ---
    private final AuthService authService;
    private final ContentService contentService;
    private final ListManagementService listManagementService;


    public ApplicationController(PersistenceModeState persistenceState) {
        this.persistenceState = persistenceState;

        // Initialize DAOs indirectly via persistenceState for services
        this.authService = new AuthService(persistenceState.getUserDao());
        this.contentService = new ContentService(
                persistenceState.getMovieDao(),
                persistenceState.getTvSeriesDao(),
                persistenceState.getAnimeDao()
        );
        this.listManagementService = new ListManagementService(
                persistenceState.getListDao(),
                persistenceState.getListMovieDao(),
                persistenceState.getListTvSeriesDao(),
                persistenceState.getListAnimeDao(),
                this.contentService // List service might need to retrieve content details
        );

        LOGGER.log(Level.INFO, "ApplicationController initialized with persistence mode: {0}", persistenceState.getClass().getSimpleName());
    }

    /**
     * Default constructor, uses DemoModeState (in-memory persistence).
     */
    public ApplicationController() {
        this(new DemoModeState());
    }

    // --- Current User Session Management (remains in ApplicationController) ---
    public UserBean getCurrentUserBean() {
        return currentUser;
    }

    public void setCurrentUserBean(UserBean currentUser) {
        this.currentUser = currentUser;
        LOGGER.log(Level.INFO, "Current user set to: {0}", (currentUser != null ? currentUser.getUsername() : "null"));
    }

    // --- Selected Item for Display (for ShowController) ---
    public String getSelectedItemCategory() {
        return selectedItemCategory;
    }

    public void setSelectedItemCategory(String selectedItemCategory) {
        this.selectedItemCategory = selectedItemCategory;
        LOGGER.log(Level.FINE, "Selected item category set to: {0}", selectedItemCategory);
    }

    public int getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(int selectedItemId) {
        this.selectedItemId = selectedItemId;
        LOGGER.log(Level.FINE, "Selected item ID set to: {0}", selectedItemId);
    }

    // --- Search Parameters ---
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

    // --- Selected List ---
    public ListBean getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(ListBean selectedList) {
        this.selectedList = selectedList;
        LOGGER.log(Level.INFO, "Selected list set to: {0}", (selectedList != null ? selectedList.getName() : "null"));
    }

    // --- Business Logic Methods (now delegated to services) ---

    public boolean login(String username, String password) throws ExceptionApplicationController {
        boolean success = authService.login(username, password);
        if (success) {
            setCurrentUserBean(authService.getCurrentUser()); // Auth service sets its internal user
        }
        return success;
    }

    public boolean registerUser(UserBean userBean) throws ExceptionApplicationController {
        return authService.registerUser(userBean);
    }

    public List<?> searchContent(String category, String query) throws ExceptionApplicationController {
        return contentService.searchContent(category, query);
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
            LOGGER.log(Level.INFO, "User logged out successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during logout.", e);
            throw new ExceptionApplicationController("An unexpected error occurred during logout.", e);
        }
    }
}