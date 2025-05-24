// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/GetAllListsCommand.java
package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

import java.util.List;

public class GetAllListsCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser {
        if (!context.isUserLoggedIn()) {
            throw new ExceptionUser("You must be logged in to view your lists.");
        }
        List<ListBean> lists = context.getApplicationController().getListsForUser(context.getCurrentUserBean());
        if (lists.isEmpty()) {
            return "No lists found for user '" + context.getCurrentUserBean().getUsername() + "'.";
        } else {
            StringBuilder sb = new StringBuilder("Your Lists for '" + context.getCurrentUserBean().getUsername() + "':\n");
            for (ListBean list : lists) {
                sb.append("  ID: ").append(list.getId()).append(", Name: '").append(list.getName()).append("'\n");
            }
            return sb.toString().trim();
        }
    }
}