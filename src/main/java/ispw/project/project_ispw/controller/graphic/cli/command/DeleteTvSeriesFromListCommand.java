package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class DeleteTvSeriesFromListCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        String[] parts = args.split(" ");
        if (parts.length < 2) {
            return "Usage: deletetvseriesfromlist <listId> <tvSeriesId>";
        }
        int listId = Integer.parseInt(parts[0]);
        int tvSeriesId = Integer.parseInt(parts[1]);

        context.deleteTvSeriesFromList(listId, tvSeriesId);

        return "TV Series with ID " + tvSeriesId + " removed from list with ID '" + listId + "'.";
    }
}
