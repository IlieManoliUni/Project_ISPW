package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionUser;

public class LoginCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionUser, ExceptionApplication {
        String[] loginArgs = args.split(" ", 2);
        if (loginArgs.length < 2) {
            return "Usage: login <username> <password>";
        }

        boolean success = context.getApplicationController().login(loginArgs[0], loginArgs[1]);

        if (success) {
            return "User '" + context.getApplicationController().getCurrentUserBean().getUsername() + "' logged in successfully.";
        } else {
            return "Login failed. Invalid credentials.";
        }
    }
}