package ispw.project.project_ispw.controller.graphic.cli;

import ispw.project.project_ispw.controller.application.ApplicationController;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.bean.UserBean; // Still needed for method signatures and return types
import ispw.project.project_ispw.controller.graphic.GraphicController;
import ispw.project.project_ispw.controller.graphic.cli.command.*;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Logger;

public class GraphicControllerCli implements GraphicController {

    private static final Logger LOGGER = Logger.getLogger(GraphicControllerCli.class.getName());

    private static GraphicControllerCli instance;

    private final ApplicationController applicationController;
    private final Map<String, CliCommand> commands;
    private Stage primaryStage;

    private GraphicControllerCli(PersistenceModeState persistenceState) {
        this.applicationController = new ApplicationController(persistenceState);
        this.commands = new HashMap<>();
        initializeCommands();
    }

    public static synchronized GraphicControllerCli getInstance(PersistenceModeState persistenceState) {
        if (instance == null) {
            instance = new GraphicControllerCli(persistenceState);
        }
        return instance;
    }

    public static synchronized GraphicControllerCli getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GraphicControllerCli not initialized. Call getInstance(PersistenceModeState) first.");
        }
        return instance;
    }

    private void initializeCommands() {
        commands.put("help", new HelpCommand());
        commands.put("login", new LoginCommand());
        commands.put("signup", new SignUpCommand());
        commands.put("logout", new LogoutCommand());
        commands.put("createlist", new CreateListCommand());
        commands.put("deletelist", new DeleteListCommand());
        commands.put("getalllists", new GetAllListsCommand());
        commands.put("searchanime", new SearchAnimeCommand());
        commands.put("searchmovie", new SearchMovieCommand());
        commands.put("searchtvseries", new SearchTvSeriesCommand());
        commands.put("saveanimetolist", new SaveAnimeToListCommand());
        commands.put("deleteanimefromlist", new DeleteAnimeFromListCommand());
        commands.put("savemovietolist", new SaveMovieToListCommand());
        commands.put("deletemoviefromlist", new DeleteMovieFromListCommand());
        commands.put("savetvseriestolist", new SaveTvSeriesToListCommand());
        commands.put("deletetvseriesfromlist", new DeleteTvSeriesFromListCommand());
        commands.put("seeanimedetails", new SeeAnimeDetailsCommand());
        commands.put("seemoviedetails", new SeeMovieDetailsCommand());
        commands.put("seetvseriesdetails", new SeeTvSeriesDetailsCommand());
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public ApplicationController getApplicationController() {
        return this.applicationController;
    }

    public UserBean getCurrentUserBean() {
        return applicationController.getCurrentUserBean();
    }

    public boolean isUserLoggedIn() {
        return applicationController.getCurrentUserBean() != null;
    }


    public String processCliCommand(String fullCommand) {
        String[] commandParts = fullCommand.split(" ", 2);
        String cmdName = commandParts[0].toLowerCase();
        String args = commandParts.length > 1 ? commandParts[1] : "";

        CliCommand command = commands.get(cmdName);

        if (command != null) {
            try {
                return command.execute(this, args);
            } catch (NumberFormatException _) {
                return "Error: Invalid number format for ID. Please provide a valid number.";
            } catch (ExceptionUser e) {
                return "User error: " + e.getMessage();
            } catch (ExceptionApplicationController e) {
                return "Application error: " + e.getMessage();
            } catch (Exception e) {
                return "An unexpected error occurred: " + e.getMessage();
            }
        } else {
            return "Unknown command: '" + cmdName + "'. Type 'help' for a list of commands.";
        }
    }

    @Override
    public void startView() throws IOException {
        if (primaryStage == null) {
            LOGGER.severe("Primary Stage not set for GraphicControllerCli.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ispw/project/project_ispw/view/cli/cli.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Media Hub CLI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}