package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;
import ispw.project.project_ispw.model.AnimeModel;

import java.util.List;

public class SeeAnimeDetailsCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        if (args.isEmpty()) {
            return "Usage: seeanimedetails <anime_id>";
        }

        try {
            int animeId = Integer.parseInt(args.trim());
            AnimeModel anime = context.getApplicationController().retrieveAnimeById(animeId);

            StringBuilder sb = new StringBuilder();
            sb.append("--- Anime Details (ID: ").append(nullSafeString(anime.getId())).append(") ---\n");
            sb.append("Title: ").append(nullSafeString(anime.getTitle())).append("\n");
            sb.append("Description: ").append(nullSafeDescription(anime.getDescription())).append("\n");
            sb.append("Episodes: ").append(nullSafeString(anime.getEpisodes())).append("\n");
            sb.append("Duration Per Episode: ").append(nullSafeDuration(anime.getDuration())).append("\n");
            sb.append("Country of Origin: ").append(nullSafeString(anime.getCountryOfOrigin())).append("\n");
            sb.append("Start Date: ").append(nullSafeString(anime.getStartDate())).append("\n");
            sb.append("End Date: ").append(nullSafeString(anime.getEndDate())).append("\n");
            sb.append("Average Score: ").append(nullSafeString(anime.getAverageScore())).append("\n");
            sb.append("Mean Score: ").append(nullSafeString(anime.getMeanScore())).append("\n");
            sb.append("Status: ").append(nullSafeString(anime.getStatus())).append("\n");
            sb.append("Next Airing Episode: ").append(nullSafeString(anime.getNextAiringEpisode(), "No more airing info")).append("\n");
            sb.append("Genres: ").append(nullSafeGenres(anime.getGenres())).append("\n");
            sb.append("Cover Image URL: ").append(nullSafeString(anime.getCoverImage())).append("\n");
            sb.append("--------------------------------------");

            return sb.toString();

        } catch (NumberFormatException _) {
            throw new NumberFormatException("Invalid Anime ID. Please provide a valid integer ID.");
        } catch (ExceptionApplicationController e) {
            throw e;
        }
    }

    private String nullSafeString(Object value) {
        return value != null ? value.toString() : "N/A";
    }

    private String nullSafeString(Object value, String defaultMessage) {
        return value != null ? value.toString() : defaultMessage;
    }

    private String nullSafeDescription(String description) {
        if (description != null) {
            return description.replace("<br>", "\n").replace("<i>", "").replace("</i>", "");
        }
        return "N/A";
    }

    private String nullSafeDuration(Integer duration) {
        return duration != null ? duration + " minutes" : "N/A";
    }

    private String nullSafeGenres(List<String> genres) {
        return (genres != null && !genres.isEmpty()) ? String.join(", ", genres) : "N/A";
    }
}