// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/GraphicControllerCli.java
package ispw.project.project_ispw.controller.graphic.cli;

import ispw.project.project_ispw.controller.application.ApplicationController;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.controller.graphic.GraphicController;
import ispw.project.project_ispw.controller.graphic.cli.command.*;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;
import javafx.fxml.FXMLLoader; // Import for FXMLLoader
import javafx.scene.Parent;   // Import for Parent
import javafx.scene.Scene;    // Import for Scene
import javafx.stage.Stage;    // Import for Stage

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton controller for the JavaFX CLI window's backend logic,
 * refactored to use the Command design pattern.
 */
public class GraphicControllerCli implements GraphicController {

    private static GraphicControllerCli instance; // Singleton instance

    private final ApplicationController applicationController;
    private UserBean currentUserBean = null; // Store current user here, as this is the logic layer
    private final Map<String, CliCommand> commands; // Map to hold command instances
    private Stage primaryStage; // Add a field to hold the primary stage

    // Private constructor for Singleton pattern
    private GraphicControllerCli(PersistenceModeState persistenceState) {
        this.applicationController = new ApplicationController(persistenceState);
        this.commands = new HashMap<>();
        initializeCommands(); // Populate the command map
    }

    // --- Singleton Access Methods ---
    public static synchronized GraphicControllerCli getInstance(PersistenceModeState persistenceState) {
        if (instance == null) {
            instance = new GraphicControllerCli(persistenceState);
        } else {
            // "GraphicControllerCli.getInstance() called with PersistenceModeState, but already initialized."
        }
        return instance;
    }

    public static synchronized GraphicControllerCli getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GraphicControllerCli not initialized. Call getInstance(PersistenceModeState) first.");
        }
        return instance;
    }

    /**
     * Initializes the map of available CLI commands.
     * Each command is instantiated and put into the map with its associated command string.
     */
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
        // Add all other concrete command implementations here
    }

    /**
     * Sets the primary stage for the application. This must be called early in the application lifecycle.
     * @param primaryStage The main JavaFX Stage.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Provides access to the ApplicationController for the CLI-like GUI.
     * @return The ApplicationController instance.
     */
    public ApplicationController getApplicationController() {
        return this.applicationController;
    }

    /**
     * Sets the current logged-in user bean. Used by LoginCommand/SignUpCommand.
     * @param user The UserBean representing the currently logged-in user.
     */
    public void setCurrentUserBean(UserBean user) {
        this.currentUserBean = user;
    }

    /**
     * Gets the current logged-in user bean.
     * @return The UserBean of the current user, or null if no one is logged in.
     */
    public UserBean getCurrentUserBean() {
        return currentUserBean;
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isUserLoggedIn() {
        return currentUserBean != null;
    }


    /**
     * Processes a single command string received from CliController (the FXML UI).
     * This method parses the command, delegates to the appropriate Command object,
     * and returns a formatted string response for display.
     *
     * @param fullCommand The complete command string entered by the user.
     * @return A string containing the response to be displayed in the TextArea.
     */
    public String processCliCommand(String fullCommand) {
        String[] commandParts = fullCommand.split(" ", 2);
        String cmdName = commandParts[0].toLowerCase();
        String args = commandParts.length > 1 ? commandParts[1] : "";

        CliCommand command = commands.get(cmdName); // Get the command object from the map

        if (command != null) {
            try {
                // Execute the command, passing this GraphicControllerCli instance as context
                return command.execute(this, args);
            } catch (NumberFormatException e) {
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
        // This method now takes responsibility for loading the CLI FXML and setting up the stage.
        if (primaryStage == null) {
            // In a real application, you might throw a RuntimeException or log a severe error
            // as the primaryStage should always be set before calling startView.
            System.err.println("Error: Primary Stage not set for GraphicControllerCli.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ispw/project/project_ispw/view/cli/cli.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Media Hub CLI");
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("CLI Graphic Controller initialized and ready to receive commands.");
    }
}