// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/LoginCommand.java
package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class LoginCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionUser, ExceptionApplicationController {
        String[] loginArgs = args.split(" ", 2);
        if (loginArgs.length < 2) {
            return "Usage: login <username> <password>";
        }
        boolean success = context.getApplicationController().login(loginArgs[0], loginArgs[1]);
        if (success) {
            // Update the current user in GraphicControllerCli after successful login
            context.setCurrentUserBean(context.getApplicationController().getCurrentUserBean());
            return "User '" + context.getCurrentUserBean().getUsername() + "' logged in successfully.";
        } else {
            return "Login failed. Invalid credentials.";
        }
    }
}