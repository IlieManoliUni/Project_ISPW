package ispw.project.project_ispw;

import ispw.project.project_ispw.controller.application.state.DemoModeState;
import ispw.project.project_ispw.controller.application.state.FullModeState;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.controller.graphic.gui.GraphicControllerGui;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.dao.DaoType;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    private static final LaunchType DESIRED_LAUNCH_TYPE = LaunchType.GUI;
    private static final boolean RUN_IN_DEMO_MODE = false;
    private static final DaoType DESIRED_DAO_TYPE = DaoType.CSV;

    private static LaunchType currentLaunchType;
    private static PersistenceModeState currentPersistenceModeState;

    public static void main(String[] args) {
        currentLaunchType = DESIRED_LAUNCH_TYPE;

        if (RUN_IN_DEMO_MODE) {
            currentPersistenceModeState = new DemoModeState();
        } else {
            currentPersistenceModeState = new FullModeState(DESIRED_DAO_TYPE);
        }

        Application.launch(MainApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            if (currentLaunchType == LaunchType.GUI) {
                GraphicControllerGui guiController = GraphicControllerGui.getInstance(currentPersistenceModeState);
                guiController.setPrimaryStage(primaryStage);
                guiController.startView();
            } else {
                GraphicControllerCli cliController = GraphicControllerCli.getInstance(currentPersistenceModeState);
                cliController.setPrimaryStage(primaryStage);
                cliController.startView();
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading FXML file during application startup.", e);
            System.exit(1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fatal error during application startup.", e);
            System.exit(1);
        }
    }
}