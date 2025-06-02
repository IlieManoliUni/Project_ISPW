package ispw.project.project_ispw;

import ispw.project.project_ispw.controller.application.state.DemoModeState;
import ispw.project.project_ispw.controller.application.state.FullModeState;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.controller.graphic.gui.GraphicControllerGui;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.controller.graphic.GraphicController;
import ispw.project.project_ispw.dao.DaoType;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());


    private static final LaunchType DESIRED_LAUNCH_TYPE = LaunchType.CLI;
    private static final boolean RUN_IN_DEMO_MODE = false;
    private static final DaoType DESIRED_DAO_TYPE = DaoType.JDBC;

    private static LaunchType currentLaunchType;
    private static PersistenceModeState currentPersistenceModeState;

    private static ApplicationConfig appConfig;

    public static void main(String[] args) {
        appConfig = new ApplicationConfig();

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
            GraphicController controller;

            if (currentLaunchType == LaunchType.GUI) {
                String guiFxmlPath = appConfig.getProperty("gui.fxml.path.prefix", "/ispw/project/project_ispw/view/gui/");

                controller = GraphicControllerGui.getInstance(currentPersistenceModeState, guiFxmlPath);
            } else {
                controller = GraphicControllerCli.getInstance(currentPersistenceModeState);
            }

            controller.setPrimaryStage(primaryStage);
            controller.startView();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading FXML file during application startup.", e);
            System.exit(1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fatal error during application startup.", e);
            System.exit(1);
        }
    }
}