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
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphicControllerGui implements GraphicController {

    private static final Logger LOGGER = Logger.getLogger(GraphicControllerGui.class.getName());
    private static final String SYSTEM_ERROR_TITLE = "System Error";

    private static GraphicControllerGui instance;

    private final Map<String, String> screenPaths = new HashMap<>();
    private final Deque<String> screenHistory = new ArrayDeque<>();
    private Stage primaryStage;

    private final ApplicationController applicationController;

    private final Map<String, NavigableController> loadedControllers = new HashMap<>();

    private GraphicControllerGui(PersistenceModeState persistenceState) {
        this.applicationController = new ApplicationController(persistenceState);

        addScreen("logIn", "/ispw/project/project_ispw/view/gui/logIn.fxml");
        addScreen("home", "/ispw/project/project_ispw/view/gui/home.fxml");
        addScreen("list", "/ispw/project/project_ispw/view/gui/list.fxml");
        addScreen("search", "/ispw/project/project_ispw/view/gui/search.fxml");
        addScreen("show", "/ispw/project/project_ispw/view/gui/show.fxml");
        addScreen("signIn", "/ispw/project/project_ispw/view/gui/signIn.fxml");
        addScreen("stats", "/ispw/project/project_ispw/view/gui/stats.fxml");
    }

    public static synchronized GraphicControllerGui getInstance(PersistenceModeState persistenceState) {
        if (instance == null) {
            instance = new GraphicControllerGui(persistenceState);
        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void addScreen(String name, String fxmlFile) {
        screenPaths.put(name, fxmlFile);
    }

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
                if (LOGGER.isLoggable(Level.SEVERE)) LOGGER.log(Level.SEVERE, String.format("Controller for screen '%s' does not implement NavigableController.", name));
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            if (screenHistory.isEmpty() || !screenHistory.peekLast().equals(name)) {
                screenHistory.offerLast(name);
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load screen: " + name + ".\n" + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, SYSTEM_ERROR_TITLE, "An unexpected error occurred during navigation: " + e.getMessage());
        }
    }

    public NavigableController getController(String screenName) {
        return loadedControllers.get(screenName);
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

    public boolean processLogin(String username, String password) throws ExceptionApplicationController {
        try {
            boolean success = applicationController.login(username, password);
            if (success) {
                setScreen("home");
            }
            return success;
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (Exception e) {
            throw new ExceptionApplicationController("An unexpected error occurred during login.", e);
        }
    }

    public boolean registerUser(UserBean userBean) throws ExceptionApplicationController {
        try {
            boolean success = applicationController.registerUser(userBean);
            if (success) {
                return processLogin(userBean.getUsername(), userBean.getPassword());
            }
            return success;
        } catch (ExceptionApplicationController e) {
            throw e;
        } catch (Exception e) {
            throw new ExceptionApplicationController("An unexpected error occurred during registration.", e);
        }
    }

    public void performSearchAndNavigate(String category, String searchText) throws ExceptionApplicationController {
        try {
            applicationController.setSelectedSearchCategory(category);
            applicationController.setSearchQuery(searchText);
            setScreen("search");
        } catch (Exception e) {
            throw new ExceptionApplicationController("An unexpected error occurred during search navigation.", e);
        }
    }

    public void navigateToListDetail(ListBean listBean, String screenName) throws ExceptionApplicationController {
        try {
            applicationController.setSelectedList(listBean);
            setScreen(screenName);
        } catch (Exception e) {
            throw new ExceptionApplicationController("An unexpected error occurred while navigating to list details.", e);
        }
    }

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
        if (primaryStage == null) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Primary Stage not set for GraphicControllerGui.");
            return;
        }

        setScreen("home");

        primaryStage.setTitle("Media Hub GUI");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.setResizable(true);
    }
}