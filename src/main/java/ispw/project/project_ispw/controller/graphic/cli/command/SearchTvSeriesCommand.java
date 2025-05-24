// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/SearchTvSeriesCommand.java
package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.TvSeriesBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;

import java.util.List;

public class SearchTvSeriesCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController {
        if (args.isEmpty()) {
            return "Usage: searchtvseries <query>";
        }
        List<TvSeriesBean> results = (List<TvSeriesBean>) context.getApplicationController().searchContent("TvSeries", args);
        if (results.isEmpty()) {
            return "No TV Series found for query: '" + args + "'";
        } else {
            StringBuilder sb = new StringBuilder("TV Series Search Results for '" + args + "':\n");
            for (TvSeriesBean tvSeries : results) {
                sb.append("  ID: ").append(tvSeries.getIdTvSeriesTmdb())
                        .append(", Name: '").append(tvSeries.getName())
                        .append("', Episodes: ").append(tvSeries.getNumberOfEpisodes())
                        .append(", AvgEpLen: ").append(tvSeries.getEpisodeRuntime()).append("\n");
            }
            return sb.toString().trim();
        }
    }
}