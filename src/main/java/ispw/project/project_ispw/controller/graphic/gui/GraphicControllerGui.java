package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.controller.application.ApplicationController;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState; // New import
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GraphicControllerGui acts as the central Screen Manager and Flow Controller for the GUI.
 * It manages navigation between different FXML screens, handles dependencies between
 * GUI controllers and the ApplicationController, and orchestrates the overall UI flow.
 * It follows the Singleton pattern to ensure a single point of control for GUI management.
 */
public class GraphicControllerGui {

    private static final Logger LOGGER = Logger.getLogger(GraphicControllerGui.class.getName());
    private static GraphicControllerGui instance; // Singleton instance

    // Stores the FXML file paths for different screens
    private final Map<String, String> screenPaths = new HashMap<>();
    // Maintains a history of visited screens for back navigation
    private final Stack<String> screenHistory = new Stack<>();
    private Stage primaryStage; // The primary stage of the JavaFX application

    // The ApplicationController is the business logic layer, this GUI controller delegates to it
    private final ApplicationController applicationController; // This is now initialized via constructor

    // A map to store controllers that are currently loaded, if needed for direct access
    // This is generally discouraged in favor of passing data via ApplicationController
    // but can be used for specific cases like dynamic setup.
    private final Map<String, NavigableController> loadedControllers = new HashMap<>();


    /**
     * Private constructor for the Singleton pattern.
     * Initializes the ApplicationController based on the provided persistence state.
     * @param persistenceState The concrete state object (DemoModeState or FullModeState)
     * that dictates how DAOs are instantiated.
     */
    // !!! CHANGE START !!!
    private GraphicControllerGui(PersistenceModeState persistenceState) { // Constructor now takes PersistenceModeState
        // Initialize the ApplicationController using the provided state
        this.applicationController = new ApplicationController(persistenceState); // Pass the state to ApplicationController

        // Register default screens here.
        addScreen("logIn", "/ispw/project/project_ispw/view/gui/logIn.fxml");
        addScreen("home", "/ispw/project/project_ispw/view/gui/home.fxml");
        addScreen("list", "/ispw/project/project_ispw/view/gui/list.fxml");
        addScreen("search", "/ispw/project/project_ispw/view/gui/search.fxml");
        addScreen("show", "/ispw/project/project_ispw/view/gui/show.fxml"); // For displaying item details
        addScreen("signIn", "/ispw/project/project_ispw/view/gui/signIn.fxml");
        addScreen("stats", "/ispw/project/project_ispw/view/gui/stats.fxml");
    }

    /**
     * Returns the singleton instance of GraphicControllerGui, ensuring it's initialized
     * with the correct persistence mode. This method should be called once from MainApp.
     * Subsequent calls without the parameter will return the already created instance.
     *
     * @param persistenceState The desired persistence mode state (e.g., new DemoModeState(), new FullModeState(DaoType.JDBC)).
     * This parameter is only used for the *first* call to initialize the singleton.
     * @return The single instance of GraphicControllerGui.
     */
    public static synchronized GraphicControllerGui getInstance(PersistenceModeState persistenceState) {
        if (instance == null) {
            instance = new GraphicControllerGui(persistenceState); // Pass the state to the private constructor
        } else {
            // Optional: Log a warning if getInstance is called with a state after it's already initialized.
            // This suggests inconsistent initialization logic.
            LOGGER.log(Level.WARNING, "GraphicControllerGui.getInstance() called with a PersistenceModeState, but it's already initialized. The new state will be ignored.");
        }
        return instance;
    }

    // You might still want a parameterless getInstance() for convenience in some cases,
    // but it would only return the already initialized instance and cannot set the mode.
    // public static synchronized GraphicControllerGui getInstance() {
    //     if (instance == null) {
    //         // This scenario should ideally be avoided if the mode is critical for startup
    //         // Or, it could create a default instance (e.g., DemoMode) if no state was provided.
    //         LOGGER.log(Level.SEVERE, "GraphicControllerGui accessed before proper initialization with a PersistenceModeState. Defaulting to Demo Mode.");
    //         instance = new GraphicControllerGui(new ispw.project.project_ispw.controller.application.state.DemoModeState());
    //     }
    //     return instance;
    // }
    // !!! CHANGE END !!!


    /**
     * Sets the primary stage for the application. This must be called early in the application lifecycle.
     * @param primaryStage The main JavaFX Stage.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Adds a screen name and its corresponding FXML file path to the manager.
     * @param name The logical name of the screen (e.g., "home", "logIn").
     * @param fxmlFile The path to the FXML file (e.g., "/view/gui/home.fxml").
     */
    public void addScreen(String name, String fxmlFile) {
        screenPaths.put(name, fxmlFile);
        LOGGER.log(Level.INFO, "Screen ''{0}'' mapped to {1}", new Object[]{name, fxmlFile});
    }

    /**
     * Loads and displays the specified screen.
     * This method pushes the new screen onto the history stack.
     * It also injects the GraphicControllerGui instance into the new screen's controller
     * if it implements NavigableController.
     *
     * @param name The logical name of the screen to display.
     */
    public void setScreen(String name) {
        if (!screenPaths.containsKey(name)) {
            LOGGER.log(Level.WARNING, "Attempted to set non-existent screen: {0}", name);
            // Optionally, show a critical error to the user
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screenPaths.get(name)));
            Parent root = loader.load();

            // Get the specific view controller associated with the loaded FXML
            Object specificViewController = loader.getController();

            // Inject this GraphicControllerGui instance into the specific view controller
            if (specificViewController instanceof NavigableController navigableController) {
                navigableController.setGraphicController(this);
                // Store the loaded controller if needed (use with caution)
                loadedControllers.put(name, navigableController);
            } else {
                LOGGER.log(Level.WARNING, "Controller for ''{0}'' does not implement NavigableController.", name);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Only push to history if it's a new screen, not a refresh of the current one
            if (screenHistory.isEmpty() || !screenHistory.peek().equals(name)) {
                screenHistory.push(name);
                LOGGER.log(Level.INFO, "Navigated to screen: {0}", name);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load screen: " + name, e);
            // In a real application, you might show a fatal error dialog here.
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load screen: " + name + ".\n" + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred while setting screen: " + name, e);
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred during navigation: " + e.getMessage());
        }
    }

    /**
     * Retrieves a loaded controller by its screen name.
     * Use with caution. Prefer passing data via ApplicationController.
     * @param screenName The logical name of the screen.
     * @return The controller instance, or null if not found or not loaded.
     */
    public NavigableController getController(String screenName) {
        return loadedControllers.get(screenName);
    }

    /**
     * Navigates back to the previous screen in the history.
     * If there's no previous screen (history size <= 1), no action is taken.
     */
    public void goBack() {
        if (screenHistory.size() > 1) { // Need at least two screens to go back (current + previous)
            String poppedScreen = screenHistory.pop(); // Remove the current screen from history
            String previousScreen = screenHistory.peek(); // Get the screen to navigate to

            LOGGER.log(Level.INFO, "Going back from {0} to {1}", new Object[]{poppedScreen, previousScreen});

            // Call setScreen to reload the previous screen.
            // The logic in setScreen will correctly handle not re-pushing it to history if it's already the peek.
            setScreen(previousScreen);
        } else {
            LOGGER.log(Level.INFO, "No previous screen to go back to. Current history size: {0}", screenHistory.size());
            // Optionally, disable back button or show message. For now, no action.
        }
    }

    // --- Delegation to ApplicationController for Business Logic ---

    /**
     * Provides access to the application controller for business logic operations.
     * This allows specific GUI controllers to interact with the core application logic.
     * @return The ApplicationController instance.
     */
    public ApplicationController getApplicationController() {
        return applicationController;
    }

    /**
     * Delegates the user login process to the ApplicationController and manages navigation.
     * This method is typically called by the LogInController.
     *
     * @param username The username for login.
     * @param password The password for login.
     * @return true if login is successful, false otherwise.
     * @throws ExceptionApplicationController if an application-level error occurs during login.
     */
    public boolean processLogin(String username, String password) throws ExceptionApplicationController {
        try {
            boolean success = applicationController.login(username, password);
            if (success) {
                setScreen("home"); // If login successful, navigate to the home screen
            }
            return success;
        } catch (ExceptionApplicationController e) {
            throw e; // Re-throw application exceptions for GUI controller to display
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during processLogin for user: " + username, e);
            throw new ExceptionApplicationController("An unexpected error occurred during login.", e);
        }
    }

    /**
     * Delegates the user registration process to the ApplicationController and manages navigation.
     * This method is typically called by the SignInController.
     *
     * @param userBean The UserBean containing new user details.
     * @return true if registration is successful.
     * @throws ExceptionApplicationController if registration fails.
     */
    public boolean registerUser(UserBean userBean) throws ExceptionApplicationController {
        try {
            boolean success = applicationController.registerUser(userBean);
            if (success) {
                setScreen("logIn"); // If registration successful, navigate to the login screen
            }
            return success;
        } catch (ExceptionApplicationController e) {
            throw e; // Re-throw application exceptions for GUI controller to display
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during registerUser for user: " + userBean.getUsername(), e);
            throw new ExceptionApplicationController("An unexpected error occurred during registration.", e);
        }
    }

    /**
     * Delegates a search request to the ApplicationController and navigates to the search screen.
     * This method is called from the HomeController (or any search bar controller).
     * @param category The selected category (Anime, Movie, TvSeries).
     * @param searchText The text to search for.
     * @throws ExceptionApplicationController if an error occurs.
     */
    public void performSearchAndNavigate(String category, String searchText) throws ExceptionApplicationController {
        try {
            // Set the search parameters in the ApplicationController
            applicationController.setSelectedSearchCategory(category);
            applicationController.setSearchQuery(searchText);
            setScreen("search"); // Navigate to the search results screen
            LOGGER.log(Level.INFO, "Initiated search and navigated to search screen for category: {0}, text: {1}", new Object[]{category, searchText});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error performing search and navigating: " + category + " " + searchText, e);
            throw new ExceptionApplicationController("An unexpected error occurred during search navigation.", e);
        }
    }

    /**
     * Navigates to a specific list detail screen (e.g., "list" for viewing items, "stats" for statistics).
     * This method sets the active list in the ApplicationController and then navigates.
     *
     * @param listBean   The ListBean representing the list to display.
     * @param screenName The name of the target screen ("list" or "stats").
     * @throws ExceptionApplicationController If there's an issue setting the selected list or navigating.
     */
    public void navigateToListDetail(ListBean listBean, String screenName) throws ExceptionApplicationController {
        try {
            applicationController.setSelectedList(listBean); // Set the selected list
            setScreen(screenName); // Navigate to the target screen
            LOGGER.log(Level.INFO, "Navigated to ''{0}'' screen for list: {1}", new Object[]{screenName, listBean.getName()});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error navigating to list detail screen for ''{0}'' ('{1}'): {2}", new Object[]{listBean.getName(), screenName, e.getMessage()});
            throw new ExceptionApplicationController("An unexpected error occurred while navigating to list details.", e);
        }
    }

    /**
     * Navigates to the details screen for a specific content item (Movie, TV Series, Anime).
     * This method sets the item's category and ID in the ApplicationController.
     *
     * @param category The type of content (e.g., "Movie", "TV Series", "Anime").
     * @param id       The ID of the content item.
     * @throws ExceptionApplicationController If there's an issue navigating or passing details.
     */
    public void navigateToItemDetails(String category, int id) throws ExceptionApplicationController {
        try {
            // Set the selected item details in ApplicationController for the ShowController to retrieve
            applicationController.setSelectedItemCategory(category);
            applicationController.setSelectedItemId(id);

            setScreen("show"); // Assuming "show" is the screen that displays item details

            LOGGER.log(Level.INFO, "Navigated to 'show' screen for {0} with ID: {1}", new Object[]{category, id});

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error navigating to item details for {0} (ID: {1}).");
            throw new ExceptionApplicationController("An unexpected error occurred while navigating to item details.", e);
        }
    }

    // Helper method to show alert messages for GraphicControllerGui specific errors
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}