package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionUser;

public class LogoutCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication, ExceptionUser {
        if (context.isUserLoggedIn()) {
            String username = context.getCurrentUserBean().getUsername();

            context.getApplicationController().logout();

            return "User '" + username + "' logged out successfully.";
        } else {
            return "No user is currently logged in.";
        }
    }
}