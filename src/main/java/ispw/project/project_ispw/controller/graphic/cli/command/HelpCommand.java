package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;

public class HelpCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) {
        String loggedInCommands = "logout, searchmovie, searchtvseries, searchanime, createlist, deletelist, getalllists, saveanimetolist, deleteanimefromlist, savemovietolist, deletemoviefromlist, savetvseriestolist, deletetvseriesfromlist";
        String loggedOutCommands = "login, signup, searchmovie, searchtvseries, searchanime";

        StringBuilder helpText = new StringBuilder("--- Help ---\n");
        if (context.isUserLoggedIn()) {
            helpText.append("You are logged in as: ").append(context.getCurrentUserBean().getUsername()).append("\n");
            helpText.append("Commands: ").append(loggedInCommands).append("\n");
        } else {
            helpText.append("You are logged out.\n");
            helpText.append("Commands: ").append(loggedOutCommands).append("\n");
        }
        helpText.append("Global Commands: help, clear, exit (processed by UI)\n");
        helpText.append("--- End Help ---");
        return helpText.toString();
    }
}