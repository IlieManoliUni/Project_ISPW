package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplication;

public class SearchTvSeriesCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplication {
        if (args.isEmpty()) {
            return "Usage: searchtvseries <query>";
        }

        // Delegate the search and display formatting to GraphicControllerCli
        return context.performCliSearchAndDisplay("tvseries", args);
    }
}