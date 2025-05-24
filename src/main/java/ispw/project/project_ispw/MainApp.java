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

    // --- Configuration Settings (No longer read from args) ---
    // Set your desired launch type here (GUI or CLI)
    private static final LaunchType DESIRED_LAUNCH_TYPE = LaunchType.GUI; // Set to LaunchType.CLI for CLI

    // Set your desired persistence mode here (true for Demo, false for Full)
    private static final boolean RUN_IN_DEMO_MODE = true; // Set to false for Full Mode

    // Set your desired DAO type here (only relevant if RUN_IN_DEMO_MODE is false)
    private static final DaoType DESIRED_DAO_TYPE = DaoType.JDBC; // Example: DaoType.JDBC or DaoType.CSV
    // --- End Configuration Settings ---


    private static LaunchType currentLaunchType;
    private static PersistenceModeState currentPersistenceModeState;

    public static void main(String[] args) {
        // No command-line argument parsing needed here anymore
        // Values are directly assigned from the constants above

        currentLaunchType = DESIRED_LAUNCH_TYPE;

        if (RUN_IN_DEMO_MODE) {
            currentPersistenceModeState = new DemoModeState();
            LOGGER.log(Level.INFO, "Application starting in DEMO MODE (in-memory persistence).");
        } else {
            currentPersistenceModeState = new FullModeState(DESIRED_DAO_TYPE);
            LOGGER.log(Level.INFO, "Application starting in FULL MODE ({0} persistence).", DESIRED_DAO_TYPE);
        }

        // Call Application.launch with no additional args since they are not used
        Application.launch(MainApp.class);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root;
            String title;
            String fxmlPath;

            if (currentLaunchType == LaunchType.GUI) {
                GraphicControllerGui.getInstance(currentPersistenceModeState);
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