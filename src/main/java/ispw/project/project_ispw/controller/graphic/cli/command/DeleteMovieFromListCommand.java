package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class DeleteMovieFromListCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        String[] parts = args.split(" ");
        if (parts.length < 2) {
            return "Usage: deletemoviefromlist <listId> <movieId>";
        }
        int listId = Integer.parseInt(parts[0]);
        int movieId = Integer.parseInt(parts[1]);

        context.deleteMovieFromList(listId, movieId);

        return "Movie with ID " + movieId + " removed from list with ID '" + listId + "'.";
    }
}
