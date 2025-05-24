// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/CliCommand.java
package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public interface CliCommand {
    /**
     * Executes the command.
     *
     * @param context The GraphicControllerCli instance providing access to application logic and state.
     * @param args    The raw arguments string for the command (e.g., "username password").
     * @return A formatted string response to be displayed to the user.
     * @throws ExceptionUser             For user-related errors (e.g., invalid input, not logged in).
     * @throws ExceptionApplicationController For errors within the application's core logic.
     * @throws NumberFormatException     If a number argument is invalid.
     * @throws Exception                 For any unexpected errors.
     */
    String execute(GraphicControllerCli context, String args) throws ExceptionUser, ExceptionApplicationController, NumberFormatException, Exception;
}