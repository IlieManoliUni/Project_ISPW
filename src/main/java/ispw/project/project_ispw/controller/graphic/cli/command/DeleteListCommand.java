// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/DeleteListCommand.java
package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class DeleteListCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        if (!context.isUserLoggedIn()) {
            throw new ExceptionUser("You must be logged in to delete a list.");
        }
        int listId = Integer.parseInt(args.trim());
        ListBean listToDelete = context.getApplicationController().getListByIdForCurrentUser(listId, context.getCurrentUserBean());
        if (listToDelete != null) {
            context.getApplicationController().deleteList(listToDelete);
            return "List '" + listToDelete.getName() + "' (ID: " + listId + ") deleted successfully.";
        } else {
            throw new ExceptionUser("List with ID " + listId + " not found or you do not own it.");
        }
    }
}