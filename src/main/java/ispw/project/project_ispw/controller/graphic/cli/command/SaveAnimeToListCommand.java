package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;
import ispw.project.project_ispw.exception.ExceptionUser;

public class SaveAnimeToListCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication, ExceptionUser, NumberFormatException {
        String[] parts = args.split(" ");
        if (parts.length < 2) {
            return "Usage: saveanimetolist <listId> <animeId>";
        }
        int listId = Integer.parseInt(parts[0]);
        int animeId = Integer.parseInt(parts[1]);

        context.addAnimeToList(listId, animeId);

        return "Anime with ID " + animeId + " added to list with ID '" + listId + "'.";
    }
}
