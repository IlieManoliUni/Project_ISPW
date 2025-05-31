package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.model.TvSeriesModel;

import java.util.List;

public class SearchTvSeriesCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController {
        if (args.isEmpty()) {
            return "Usage: searchtvseries <query>";
        }
        List<TvSeriesModel> results = context.getApplicationController().searchTvSeries(args);
        if (results.isEmpty()) {
            return "No TV Series found for query: '" + args + "'";
        } else {
            StringBuilder sb = new StringBuilder("TV Series Search Results for '" + args + "':\n");
            for (TvSeriesModel tvSeries : results) {
                sb.append("  ID: ").append(tvSeries.getId())
                        .append(", Name: '").append(tvSeries.getName())
                        .append("', Episodes: ").append(tvSeries.getNumberOfEpisodes())
                        .append(", AvgEpLen: ").append(tvSeries.getEpisodeRunTime()).append("\n");
            }
            return sb.toString().trim();
        }
    }
}