package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;

public class SearchMovieCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController {
        if (args.isEmpty()) {
            return "Usage: searchmovie <query>";
        }

        return context.performCliSearchAndDisplay("movie", args);
    }
}