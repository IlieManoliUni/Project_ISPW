package ispw.project.project_ispw;

import ispw.project.project_ispw.controller.application.state.DemoModeState;
import ispw.project.project_ispw.controller.application.state.FullModeState;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.controller.graphic.gui.GraphicControllerGui;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.dao.DaoType;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    private static final LaunchType DEFAULT_LAUNCH_TYPE = LaunchType.GUI;

    // Merged default settings for both GUI and CLI
    private static final boolean DEFAULT_DEMO_MODE = true; // You can adjust this to false if "full" is preferred default
    private static final DaoType DEFAULT_DAO_TYPE = DaoType.JDBC; // You can adjust this to another DaoType if preferred default

    private static LaunchType currentLaunchType;
    private static PersistenceModeState currentPersistenceModeState;

    public static void main(String[] args) {
        LaunchType launchType = DEFAULT_LAUNCH_TYPE;
        boolean runInDemoMode;
        DaoType daoType;
        String persistenceModeArg = null;
        String daoTypeArg = null;

        for (String arg : args) {
            if (arg.startsWith("--launch=")) {
                try {
                    launchType = LaunchType.valueOf(arg.substring("--launch=".length()).toUpperCase());
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Invalid launch type '{0}' specified. Falling back to default GUI.", arg.substring("--launch=".length()));
                    launchType = DEFAULT_LAUNCH_TYPE;
                }
            } else if (arg.startsWith("--persistence=")) {
                persistenceModeArg = arg.substring("--persistence=".length()).toLowerCase();
            } else if (arg.startsWith("--daotype=")) {
                daoTypeArg = arg.substring("--daotype=".length()).toLowerCase();
            }
        }

        currentLaunchType = launchType;

        // Apply merged default settings
        runInDemoMode = DEFAULT_DEMO_MODE;
        daoType = DEFAULT_DAO_TYPE;

        if (persistenceModeArg != null) {
            if ("demo".equals(persistenceModeArg)) {
                runInDemoMode = true;
            } else if ("full".equals(persistenceModeArg)) {
                runInDemoMode = false;
                if (daoTypeArg != null) {
                    try {
                        daoType = DaoType.valueOf(daoTypeArg.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        LOGGER.log(Level.WARNING, "Invalid DaoType '{0}' specified. Using default '{1}' for Full Mode.", new Object[]{daoTypeArg, daoType});
                    }
                } else {
                    LOGGER.log(Level.INFO, "No DaoType specified for Full Mode. Using default '{0}'.", daoType);
                }
            } else {
                LOGGER.log(Level.WARNING, "Invalid persistence mode '{0}' specified. Using default settings.", persistenceModeArg);
            }
        }

        if (runInDemoMode) {
            currentPersistenceModeState = new DemoModeState();
            LOGGER.log(Level.INFO, "Application starting in DEMO MODE (in-memory persistence).");
        } else {
            currentPersistenceModeState = new FullModeState(daoType);
            LOGGER.log(Level.INFO, "Application starting in FULL MODE ({0} persistence).", daoType);
        }

        Application.launch(MainApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root;
            String title;
            String fxmlPath;

            if (currentLaunchType == LaunchType.GUI) {
                GraphicControllerGui.getInstance(currentPersistenceModeState);
                // FIX: Changed fxmlPath from "main.fxml" to "home.fxml"
                fxmlPath = "/ispw/project/project_ispw/view/gui/home.fxml"; // Corrected FXML path
                title = "Media Hub GUI";
                LOGGER.log(Level.INFO, "Loading GUI FXML: {0}", fxmlPath);
            } else {
                GraphicControllerCli.getInstance(currentPersistenceModeState);
                fxmlPath = "/ispw/project/project_ispw/view/cli/cli.fxml";
                title = "Media Hub CLI Console";
                LOGGER.log(Level.INFO, "Loading CLI FXML: {0}", fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.setWidth(1000);
            primaryStage.setHeight(700);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML: " + e.getMessage(), e);
            System.err.println("Fatal error loading FXML: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during application startup.", e);
            System.err.println("Fatal error during application startup: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}