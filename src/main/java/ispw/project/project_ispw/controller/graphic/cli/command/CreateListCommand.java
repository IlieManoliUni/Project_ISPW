// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/CreateListCommand.java
package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class CreateListCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser {
        if (!context.isUserLoggedIn()) {
            throw new ExceptionUser("You must be logged in to create a list.");
        }
        String listName = args.trim();
        if (listName.isEmpty()) {
            return "Usage: createlist <name>";
        }
        ListBean newListBean = new ListBean();
        newListBean.setName(listName);
        context.getApplicationController().createList(newListBean, context.getCurrentUserBean());
        return "List '" + listName + "' created successfully.";
    }
}