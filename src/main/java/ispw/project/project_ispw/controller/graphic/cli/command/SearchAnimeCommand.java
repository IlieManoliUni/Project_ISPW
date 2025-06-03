package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;

public class SearchAnimeCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication {
        if (args.isEmpty()) {
            return "Usage: searchanime <query>";
        }

        return context.performCliSearchAndDisplay("anime", args);
    }
}