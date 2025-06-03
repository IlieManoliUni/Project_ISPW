package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionUser;

public class DeleteListCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication, ExceptionUser, NumberFormatException {
        int listId = Integer.parseInt(args.trim());

        context.deleteList(listId);

        return "List with ID '" + listId + "' deleted successfully.";
    }
}
