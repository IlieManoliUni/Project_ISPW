// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/GraphicControllerCli.java
package ispw.project.project_ispw.controller.graphic.cli;

import ispw.project.project_ispw.controller.application.ApplicationController;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.controller.graphic.cli.command.*;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton controller for the JavaFX CLI window's backend logic,
 * refactored to use the Command design pattern.
 */
public class GraphicControllerCli {

    private static final Logger LOGGER = Logger.getLogger(GraphicControllerCli.class.getName());
    private static GraphicControllerCli instance; // Singleton instance

    private final ApplicationController applicationController;
    private UserBean currentUserBean = null; // Store current user here, as this is the logic layer
    private final Map<String, CliCommand> commands; // Map to hold command instances

    // Private constructor for Singleton pattern
    private GraphicControllerCli(PersistenceModeState persistenceState) {
        this.applicationController = new ApplicationController(persistenceState);
        this.commands = new HashMap<>();
        initializeCommands(); // Populate the command map
        LOGGER.log(Level.INFO, "GraphicControllerCli initialized with {0}", persistenceState.getClass().getSimpleName());
    }

    // --- Singleton Access Methods ---
    public static synchronized GraphicControllerCli getInstance(PersistenceModeState persistenceState) {
        if (instance == null) {
            instance = new GraphicControllerCli(persistenceState);
        } else {
            LOGGER.log(Level.WARNING, "GraphicControllerCli.getInstance() called with PersistenceModeState, but already initialized.");
        }
        return instance;
    }

    public static synchronized GraphicControllerCli getInstance() {
        if (instance == null) {
            LOGGER.log(Level.SEVERE, "GraphicControllerCli accessed before proper initialization with PersistenceModeState.");
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
        commands.put("createlist", new CreateListCommand()); // You'd create this class
        commands.put("deletelist", new DeleteListCommand()); // You'd create this class
        commands.put("getalllists", new GetAllListsCommand()); // You'd create this class
        commands.put("searchanime", new SearchAnimeCommand()); // You'd create this class
        commands.put("searchmovie", new SearchMovieCommand()); // You'd create this class
        commands.put("searchtvseries", new SearchTvSeriesCommand()); // You'd create this class
        commands.put("saveanimetolist", new SaveAnimeToListCommand()); // You'd create this class
        commands.put("deleteanimefromlist", new DeleteAnimeFromListCommand()); // You'd create this class
        commands.put("savemovietolist", new SaveMovieToListCommand()); // You'd create this class
        commands.put("deletemoviefromlist", new DeleteMovieFromListCommand()); // You'd create this class
        commands.put("savetvseriestolist", new SaveTvSeriesToListCommand()); // You'd create this class
        commands.put("deletetvseriesfromlist", new DeleteTvSeriesFromListCommand()); // You'd create this class
        // Add all other concrete command implementations here
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
                LOGGER.log(Level.WARNING, "Number format error in command '{0}': {1}", new Object[]{cmdName, e.getMessage()});
                return "Error: Invalid number format for ID. Please provide a valid number.";
            } catch (ExceptionUser e) {
                LOGGER.log(Level.WARNING, "User error executing command '{0}': {1}", new Object[]{cmdName, e.getMessage()});
                return "User error: " + e.getMessage();
            } catch (ExceptionApplicationController e) {
                LOGGER.log(Level.SEVERE, "ApplicationController error executing command '{0}': {1}", new Object[]{cmdName, e.getMessage()});
                return "Application error: " + e.getMessage();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An unexpected error occurred while executing command '{0}'", e); // Pass exception object for full stack trace
                return "An unexpected error occurred: " + e.getMessage();
            }
        } else {
            return "Unknown command: '" + cmdName + "'. Type 'help' for a list of commands.";
        }
    }

    // The individual handle*Command methods are now replaced by separate CliCommand classes.
    // E.g., handleLoginCommand is now LoginCommand.execute()
    // You would remove all the handle*Command methods from this class.
}