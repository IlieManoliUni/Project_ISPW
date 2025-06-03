package ispw.project.project_ispw.controller.graphic.gui;

import ispw.project.project_ispw.controller.application.ApplicationController;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.controller.graphic.GraphicController;
import ispw.project.project_ispw.model.ListModel;
import ispw.project.project_ispw.model.UserModel;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphicControllerGui implements GraphicController {

    private static final Logger LOGGER = Logger.getLogger(GraphicControllerGui.class.getName());
    private static final String SYSTEM_ERROR_TITLE = "System Error";
    private static final String LOGINSCREEN = "logIn";

    private static GraphicControllerGui instance;

    private final Map<String, String> screenPaths = new HashMap<>();
    private final Deque<String> screenHistory = new ArrayDeque<>();
    private Stage primaryStage;

    private final ApplicationController applicationController;
    private final UserModel userModel;
    private final String fxmlPathPrefix;

    private final ChangeListener<Boolean> loggedInGlobalListener;

    private GraphicControllerGui(PersistenceModeState persistenceState, String fxmlPathPrefix) {
        this.applicationController = new ApplicationController(persistenceState);
        this.userModel = new UserModel(this.applicationController.getAuthService());
        this.fxmlPathPrefix = fxmlPathPrefix;

        addScreen(LOGINSCREEN, this.fxmlPathPrefix + "logIn.fxml");
        addScreen("home", this.fxmlPathPrefix + "home.fxml");
        addScreen("list", this.fxmlPathPrefix + "list.fxml");
        addScreen("search", this.fxmlPathPrefix + "search.fxml");
        addScreen("show", this.fxmlPathPrefix + "show.fxml");
        addScreen("signIn", this.fxmlPathPrefix + "signIn.fxml");
        addScreen("stats", this.fxmlPathPrefix + "stats.fxml");

        loggedInGlobalListener = (obs, oldVal, newVal) -> {
            //transition from a logged-in state (oldVal is true) to a logged-out state (newVal is false)
            if (!newVal.booleanValue() && oldVal.booleanValue()) {
                LOGGER.log(Level.INFO, "GraphicControllerGui: User logged out. Clearing history and showing alert.");
                showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been successfully logged out. All data cleared.");
                screenHistory.clear();
                setScreen(LOGINSCREEN);
            }
        };

        this.userModel.loggedInProperty().addListener(loggedInGlobalListener);
    }

    public static synchronized GraphicControllerGui getInstance(PersistenceModeState persistenceState, String fxmlPathPrefix) {
        if (instance == null) {
            instance = new GraphicControllerGui(persistenceState, fxmlPathPrefix);
        }
        return instance;
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void addScreen(String name, String fxmlPath) {
        screenPaths.put(name, fxmlPath);
    }

    public void setScreen(String name) {
        if (!screenPaths.containsKey(name)) {
            showAlert(Alert.AlertType.ERROR, "Navigation screen error", "Screen not registered: " + name);
            LOGGER.log(Level.SEVERE, "Attempted to set unregistered screen: {0}", name);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screenPaths.get(name)));
            Parent root = loader.load();
            NavigableController controller = loader.getController();

            if (controller == null) {
                LOGGER.log(Level.SEVERE, "FXMLLoader did not provide a controller for {0}.fxml", name);
                throw new IOException("Controller not found for FXML: " + name);
            }

            controller.setGraphicController(this);
            if (controller instanceof UserAwareController userAwareController) {
                userAwareController.setUserModel(this.userModel);
            }

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            primaryStage.show();

            if (screenHistory.isEmpty() || !screenHistory.peekLast().equals(name)) {
                screenHistory.offerLast(name);
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation screen error", "Could not load screen: " + name + ".\n" + e.getMessage());
            LOGGER.log(Level.SEVERE, e, () -> "Failed to load FXML for screen: " + name);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during screen transition: " + e.getMessage());
            LOGGER.log(Level.SEVERE, e, () -> "An unexpected error occurred during screen transition to " + name);
        }
    }

    public NavigableController getController(String screenName) {
        LOGGER.log(Level.WARNING, "Attempted to get controller for screen {0}. GraphicControllerGui is configured to load new controllers per screen set, so this will return null.", screenName);
        return null;
    }

    public void goBack() {
        if (screenHistory.size() > 1) {
            screenHistory.pollLast();
            String previousScreen = screenHistory.peekLast();
            setScreen(previousScreen);
        } else {
            LOGGER.warning("Attempted to go back with no screen history (only current screen remaining).");
        }
    }

    public ApplicationController getApplicationController() {
        return applicationController;
    }


    public void performSearchAndNavigate(String category, String searchText) {
        try {
            applicationController.setSelectedSearchCategory(category);
            applicationController.setSearchQuery(searchText);
            setScreen("search");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error navigating to search results", e);
            showAlert(Alert.AlertType.ERROR, "Navigation error", "An unexpected error occurred while navigating to search results. Please try again.");
        }
    }

    public void navigateToListDetail(ListModel listModel, String screenName) {
        try {
            applicationController.setSelectedList(listModel.getListBean());
            setScreen(screenName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error navigating to list details", e);
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An unexpected error occurred while navigating to list details. Please try again.");
        }
    }

    public void navigateToItemDetails(String category, int id) {
        try {
            applicationController.setSelectedItemCategory(category);
            applicationController.setSelectedItemId(id);
            setScreen("show");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error navigating to item details", e);
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "An unexpected error occurred while navigating to item details. Please try again.");
        }
    }

    public String getSelectedItemCategory() {
        return applicationController.getSelectedItemCategory();
    }

    public int getSelectedItemId() {
        return applicationController.getSelectedItemId();
    }

    public String getSearchQuery() {
        return applicationController.getSearchQuery();
    }

    public String getSelectedSearchCategory() {
        return applicationController.getSelectedSearchCategory();
    }

    public ListBean getSelectedList() {
        return applicationController.getSelectedList();
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
        if (primaryStage == null) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Primary Stage not set for GraphicControllerGui.");
            LOGGER.log(Level.SEVERE, "Primary Stage is null during startView.");
            return;
        }

        if (userModel.loggedInProperty().get()) {
            setScreen("home");
        } else {
            setScreen(LOGINSCREEN);
        }

        primaryStage.setTitle("Media Hub GUI");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.setResizable(true);
    }
}