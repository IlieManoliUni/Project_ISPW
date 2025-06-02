package ispw.project.project_ispw.controller.graphic.cli;

import ispw.project.project_ispw.controller.application.ApplicationController;
import ispw.project.project_ispw.controller.application.state.PersistenceModeState;
import ispw.project.project_ispw.bean.UserBean;
import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.graphic.GraphicController;
import ispw.project.project_ispw.controller.graphic.cli.command.*;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;
import ispw.project.project_ispw.model.AnimeModel;
import ispw.project.project_ispw.model.MovieModel;
import ispw.project.project_ispw.model.TvSeriesModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphicControllerCli implements GraphicController {

    private static final Logger LOGGER = Logger.getLogger(GraphicControllerCli.class.getName());

    private static GraphicControllerCli instance;

    private final ApplicationController applicationController;
    private final Map<String, CliCommand> commands;
    private Stage primaryStage;

    private GraphicControllerCli(PersistenceModeState persistenceState) {
        this.applicationController = new ApplicationController(persistenceState);
        this.commands = new HashMap<>();
        initializeCommands();
    }

    public static synchronized GraphicControllerCli getInstance(PersistenceModeState persistenceState) {
        if (instance == null) {
            instance = new GraphicControllerCli(persistenceState);
        }
        return instance;
    }

    public static synchronized GraphicControllerCli getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GraphicControllerCli not initialized. Call getInstance(PersistenceModeState) first.");
        }
        return instance;
    }

    private void initializeCommands() {
        commands.put("help", new HelpCommand());
        commands.put("login", new LoginCommand());
        commands.put("signup", new SignUpCommand());
        commands.put("logout", new LogoutCommand());
        commands.put("createlist", new CreateListCommand());
        commands.put("deletelist", new DeleteListCommand());
        commands.put("getalllists", new GetAllListsCommand());
        commands.put("searchanime", new SearchAnimeCommand());
        commands.put("searchmovie", new SearchMovieCommand());
        commands.put("searchtvseries", new SearchTvSeriesCommand());
        commands.put("saveanimetolist", new SaveAnimeToListCommand());
        commands.put("deleteanimefromlist", new DeleteAnimeFromListCommand());
        commands.put("savemovietolist", new SaveMovieToListCommand());
        commands.put("deletemoviefromlist", new DeleteMovieFromListCommand());
        commands.put("savetvseriestolist", new SaveTvSeriesToListCommand());
        commands.put("deletetvseriesfromlist", new DeleteTvSeriesFromListCommand());
        commands.put("seeanimedetails", new SeeAnimeDetailsCommand());
        commands.put("seemoviedetails", new SeeMovieDetailsCommand());
        commands.put("seetvseriesdetails", new SeeTvSeriesDetailsCommand());
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public ApplicationController getApplicationController() {
        return this.applicationController;
    }

    public UserBean getCurrentUserBean() {
        return applicationController.getCurrentUserBean();
    }

    public boolean isUserLoggedIn() {
        return applicationController.getCurrentUserBean() != null;
    }

    public String processCliCommand(String fullCommand) {
        String[] commandParts = fullCommand.split(" ", 2);
        String cmdName = commandParts[0].toLowerCase();
        String args = commandParts.length > 1 ? commandParts[1] : "";

        CliCommand command = commands.get(cmdName);

        if (command != null) {
            try {
                return command.execute(this, args);
            } catch (NumberFormatException e) {
                return "Error: Invalid number format for ID. Please provide a valid integer ID. Details: " + e.getMessage();
            } catch (ExceptionUser e) {
                return "User error: " + e.getMessage();
            } catch (ExceptionApplicationController e) {
                return "Application error: " + e.getMessage();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e, () -> "An unexpected error occurred during command execution: " + e.getMessage());
                return "An unexpected error occurred: " + e.getMessage();
            }
        } else {
            return "Unknown command: '" + cmdName + "'. Type 'help' for a list of commands.";
        }
    }

    @Override
    public void startView() throws IOException {
        if (primaryStage == null) {
            LOGGER.severe("Primary Stage not set for GraphicControllerCli.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ispw/project/project_ispw/view/cli/cli.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Media Hub CLI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public String performCliSearchAndDisplay(String category, String searchText) {
        try {
            List<?> results = switch (category.toLowerCase()) {
                case "anime" -> applicationController.searchAnime(searchText);
                case "movie" -> applicationController.searchMovies(searchText);
                case "tvseries" -> applicationController.searchTvSeries(searchText);
                default -> throw new IllegalArgumentException("Unsupported search category: " + category);
            };

            if (results.isEmpty()) {
                return "No " + category + " found for query: '" + searchText + "'.";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("--- Search Results for '").append(searchText).append("' in ").append(category).append(" ---\n");
                for (Object item : results) {
                    switch (item) {
                        case AnimeModel anime -> sb.append("  ID Anime: ").append(anime.getId())
                                .append(", Title Anime: '").append(anime.getTitle()).append("'\n");
                        case MovieModel movie -> sb.append("  ID Movie: ").append(movie.getId())
                                .append(", Title Movie: '").append(movie.getTitle()).append("'\n");
                        case TvSeriesModel tvSeries -> sb.append("  ID TvSeries: ").append(tvSeries.getId())
                                .append(", Title TvSeries: '").append(tvSeries.getName()).append("'\n");
                        default -> sb.append("- ").append(item.toString()).append("\n"); // Fallback for unknown types
                    }
                }
                sb.append("------------------------------------------");
                return sb.toString();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e, () -> "Error during search operation: " + e.getMessage());
            return "Error: An unexpected error occurred during search. Details: " + e.getMessage();
        }
    }

    public Object viewContentDetails(String category, int id) throws ExceptionApplicationController{
        return switch (category.toLowerCase()) {
            case "anime" -> applicationController.retrieveAnimeById(id);
            case "movie" -> applicationController.retrieveMovieById(id);
            case "tvseries" -> applicationController.retrieveTvSeriesById(id);
            default -> throw new IllegalArgumentException("Unsupported content category for details: " + category);
        };
    }

    public void addMovieToList(int listId, int movieId) throws ExceptionApplicationController, ExceptionUser {
        ListBean listBean = getListByIdForCurrentUser(listId);
        if (listBean == null) {
            throw new ExceptionUser("List with ID " + listId + " not found or does not belong to you.");
        }
        applicationController.addMovieToList(listBean, movieId);
    }

    public void deleteMovieFromList(int listId, int movieId) throws ExceptionApplicationController, ExceptionUser {
        ListBean listBean = getListByIdForCurrentUser(listId);
        if (listBean == null) {
            throw new ExceptionUser("List with ID " + listId + " not found or does not belong to you.");
        }
        applicationController.removeMovieFromList(listBean, movieId);
    }

    public void addAnimeToList(int listId, int animeId) throws ExceptionApplicationController, ExceptionUser {
        ListBean listBean = getListByIdForCurrentUser(listId);
        if (listBean == null) {
            throw new ExceptionUser("List with ID " + listId + " not found or does not belong to you.");
        }
        applicationController.addAnimeToList(listBean, animeId);
    }

    public void deleteAnimeFromList(int listId, int animeId) throws ExceptionApplicationController, ExceptionUser {
        ListBean listBean = getListByIdForCurrentUser(listId);
        if (listBean == null) {
            throw new ExceptionUser("List with ID " + listId + " not found or does not belong to you.");
        }
        applicationController.removeAnimeFromList(listBean, animeId);
    }

    public void addTvSeriesToList(int listId, int tvSeriesId) throws ExceptionApplicationController, ExceptionUser {
        ListBean listBean = getListByIdForCurrentUser(listId);
        if (listBean == null) {
            throw new ExceptionUser("List with ID " + listId + " not found or does not belong to you.");
        }
        applicationController.addTvSeriesToList(listBean, tvSeriesId);
    }

    public void deleteTvSeriesFromList(int listId, int tvSeriesId) throws ExceptionApplicationController, ExceptionUser {
        ListBean listBean = getListByIdForCurrentUser(listId);
        if (listBean == null) {
            throw new ExceptionUser("List with ID " + listId + " not found or does not belong to you.");
        }
        applicationController.removeTvSeriesFromList(listBean, tvSeriesId);
    }

    public void createList(String listName) throws ExceptionApplicationController, ExceptionUser {
        if (!isUserLoggedIn()) {
            throw new ExceptionUser("You must be logged in to create a list.");
        }
        if (listName.isEmpty()) {
            throw new ExceptionUser("List name cannot be empty.");
        }
        ListBean newListBean = new ListBean();
        newListBean.setName(listName);
        applicationController.createList(newListBean, getCurrentUserBean());
    }

    public void deleteList(int listId) throws ExceptionApplicationController, ExceptionUser {
        if (!isUserLoggedIn()) {
            throw new ExceptionUser("You must be logged in to delete a list.");
        }
        ListBean listToDelete = applicationController.getListByIdForCurrentUser(listId, getCurrentUserBean());
        if (listToDelete != null) {
            applicationController.deleteList(listToDelete);
        } else {
            throw new ExceptionUser("List with ID " + listId + " not found or you do not own it.");
        }
    }

    public List<ListBean> getListsForUser() throws ExceptionApplicationController, ExceptionUser {
        if (!isUserLoggedIn()) {
            throw new ExceptionUser("You must be logged in to view your lists.");
        }
        return applicationController.getListsForUser(getCurrentUserBean());
    }

    public ListBean getListByIdForCurrentUser(int listId) throws ExceptionApplicationController, ExceptionUser {
        if (!isUserLoggedIn()) {
            throw new ExceptionUser("You must be logged in to access lists.");
        }
        return applicationController.getListByIdForCurrentUser(listId, getCurrentUserBean());
    }
}