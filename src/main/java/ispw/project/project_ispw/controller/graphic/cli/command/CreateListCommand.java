package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionUser;

public class CreateListCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication, ExceptionUser {

        String listName = args.trim();
        if (listName.isEmpty()) {
            return "Usage: createlist <name>";
        }

        context.createList(listName);

        return "List '" + listName + "' created successfully.";
    }
}
