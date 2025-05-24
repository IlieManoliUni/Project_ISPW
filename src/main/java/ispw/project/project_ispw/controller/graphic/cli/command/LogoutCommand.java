// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/LogoutCommand.java
package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser; // Potentially thrown by isUserLoggedIn() if you put it in AppController

public class LogoutCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser {
        if (context.isUserLoggedIn()) {
            String username = context.getCurrentUserBean().getUsername();
            context.getApplicationController().logout();
            context.setCurrentUserBean(null); // Clear the current user in the GraphicControllerCli
            return "User '" + username + "' logged out successfully.";
        } else {
            return "No user is currently logged in.";
        }
    }
}