// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/SearchAnimeCommand.java
package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.AnimeBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;

import java.util.List;

public class SearchAnimeCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController {
        if (args.isEmpty()) {
            return "Usage: searchanime <query>";
        }
        List<AnimeBean> results = (List<AnimeBean>) context.getApplicationController().searchContent("Anime", args);
        if (results.isEmpty()) {
            return "No anime found for query: '" + args + "'";
        } else {
            StringBuilder sb = new StringBuilder("Anime Search Results for '" + args + "':\n");
            for (AnimeBean anime : results) {
                sb.append("  ID: ").append(anime.getIdAnimeTmdb())
                        .append(", Title: '").append(anime.getTitle())
                        .append("', Episodes: ").append(anime.getEpisodes())
                        .append(", AvgEpLen: ").append(anime.getDuration()).append("\n");
            }
            return sb.toString().trim();
        }
    }
}