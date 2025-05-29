package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class SaveMovieToListCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        if (!context.isUserLoggedIn()) { throw new ExceptionUser("You must be logged in to add items to a list."); }
        String[] parts = args.split(" ");
        if (parts.length < 2) { return "Usage: savemovietolist <listId> <movieId>"; }
        int listId = Integer.parseInt(parts[0]);
        int movieId = Integer.parseInt(parts[1]);
        ListBean listBean = context.getApplicationController().getListByIdForCurrentUser(listId, context.getCurrentUserBean());
        if (listBean == null) { throw new ExceptionUser("List with ID " + listId + " not found or does not belong to you."); }
        context.getApplicationController().addMovieToList(listBean, movieId);
        return "Movie with ID " + movieId + " added to list '" + listBean.getName() + "'.";
    }
}