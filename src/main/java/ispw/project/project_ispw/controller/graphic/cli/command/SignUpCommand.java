package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class SignUpCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionUser, ExceptionApplicationController {
        String[] signupArgs = args.split(" ", 2);
        if (signupArgs.length < 2) {
            return "Usage: signup <username> <password>";
        }
        UserBean newUserBean = new UserBean();
        newUserBean.setUsername(signupArgs[0]);
        newUserBean.setPassword(signupArgs[1]);
        boolean success = context.getApplicationController().registerUser(newUserBean);
        if (success) {
            return "User '" + newUserBean.getUsername() + "' registered successfully. Please log in.";
        } else {
            return "Registration failed. User might already exist or internal error.";
        }
    }
}