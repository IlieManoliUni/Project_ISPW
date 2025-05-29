package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.controller.application.ApplicationController;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.controller.graphic.GraphicController;
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

/**
 * GraphicControllerGui acts as the central Screen Manager and Flow Controller for the GUI.
 * It manages navigation between different FXML screens, handles dependencies between
 * GUI controllers and the ApplicationController, and orchestrates the overall UI flow.
 * It follows the Singleton pattern to ensure a single point of control for GUI management.
 */
public class GraphicControllerGui implements GraphicController {

    private static GraphicControllerGui instance; // Singleton instance

    // Stores the FXML file paths for different screens
    private final Map<String, String> screenPaths = new HashMap<>();
    // Maintains a history of visited screens for back navigation
    private final Stack<String> screenHistory = new Stack<>();
    private Stage primaryStage; // The primary stage of the JavaFX application

    // The ApplicationController is the business logic layer, this GUI controller delegates to it
    private final ApplicationController applicationController;

    // A map to store controllers that are currently loaded, if needed for direct access
    private final Map<String, NavigableController> loadedControllers = new HashMap<>();


    /**
     * Private constructor for the Singleton pattern.
     * Initializes the ApplicationController based on the provided persistence state.
     * @param persistenceState The concrete state object (DemoModeState or FullModeState)
     * that dictates how DAOs are instantiated.
     */
    private GraphicControllerGui(PersistenceModeState persistenceState) {
        this.applicationController = new ApplicationController(persistenceState);

        // Register default screens here.
        addScreen("logIn", "/ispw/project/project_ispw/view/gui/logIn.fxml");
        addScreen("home", "/ispw/project/project_ispw/view/gui/home.fxml");
        addScreen("list", "/ispw/project/project_ispw/view/gui/list.fxml");
        addScreen("search", "/ispw/project/project_ispw/view/gui/search.fxml");
        addScreen("show", "/ispw/project/project_ispw/view/gui/show.fxml");
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
            instance = new GraphicControllerGui(persistenceState);
        } else {
            // Logger removed
        }
        return instance;
    }


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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Screen not registered: " + name);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screenPaths.get(name)));
            Parent root = loader.load();

            Object specificViewController = loader.getController();

            if (specificViewController instanceof NavigableController navigableController) {
                navigableController.setGraphicController(this);
                loadedControllers.put(name, navigableController);
            } else {
                // Logger removed
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            if (screenHistory.isEmpty() || !screenHistory.peek().equals(name)) {
                screenHistory.push(name);
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load screen: " + name + ".\n" + e.getMessage());
        } catch (Exception e) {
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
        if (screenHistory.size() > 1) {
            String poppedScreen = screenHistory.pop();
            String previousScreen = screenHistory.peek();

            setScreen(previousScreen);
        } else {
            // Logger removed
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
            throw e;
        } catch (Exception e) {
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
                // If registration is successful,
                // log in the user immediately instead of going back to the login screen.
                // Logger removed
                // Call processLogin with the newly registered user's credentials
                return processLogin(userBean.getUsername(), userBean.getPassword());
            }
            return success;
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (Exception e) {
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
            applicationController.setSelectedSearchCategory(category);
            applicationController.setSearchQuery(searchText);
            setScreen("search");
        } catch (Exception e) {
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
            applicationController.setSelectedList(listBean);
            setScreen(screenName);
        } catch (Exception e) {
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
            applicationController.setSelectedItemCategory(category);
            applicationController.setSelectedItemId(id);

            setScreen("show");

        } catch (Exception e) {
            throw new ExceptionApplicationController("An unexpected error occurred while navigating to item details.", e);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void startView() {
        // This method is called from MainApp to kick off the GUI.
        // It assumes primaryStage has already been set via setPrimaryStage().
        if (primaryStage == null) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Primary Stage not set for GraphicControllerGui.");
            return;
        }

        // Set the initial screen
        setScreen("home"); // Or "home" depending on your desired starting point

        // Set initial stage properties
        primaryStage.setTitle("Media Hub GUI");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.setResizable(true);
        // primaryStage.show() is handled internally by setScreen now
    }
}