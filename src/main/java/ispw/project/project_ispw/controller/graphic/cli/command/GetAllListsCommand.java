package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionUser;

import java.util.List;

public class GetAllListsCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication, ExceptionUser {
        List<ListBean> lists = context.getListsForUser();
        if (lists.isEmpty()) {
            return "No lists found for the current user.";
        } else {
            StringBuilder sb = new StringBuilder("Your Lists:\n");
            for (ListBean list : lists) {
                sb.append("  ID: ").append(list.getId()).append(", Name: '").append(list.getName()).append("'\n");
            }
            return sb.toString().trim();
        }
    }
}
