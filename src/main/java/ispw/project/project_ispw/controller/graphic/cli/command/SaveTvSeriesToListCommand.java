// File: src/main/java/ispw/project/project_ispw/controller/graphic/cli/SaveTvSeriesToListCommand.java
package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.bean.ListBean;
import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public class SaveTvSeriesToListCommand implements CliCommand {
    @Override
    public String execute(GraphicControllerCli context, String args) throws ExceptionApplicationController, ExceptionUser, NumberFormatException {
        if (!context.isUserLoggedIn()) { throw new ExceptionUser("You must be logged in to add items to a list."); }
        String[] parts = args.split(" ");
        if (parts.length < 2) { return "Usage: savetvseriestolist <listId> <tvSeriesId>"; }
        int listId = Integer.parseInt(parts[0]);
        int tvSeriesId = Integer.parseInt(parts[1]);
        ListBean listBean = context.getApplicationController().getListByIdForCurrentUser(listId, context.getCurrentUserBean());
        if (listBean == null) { throw new ExceptionUser("List with ID " + listId + " not found or does not belong to you."); }
        context.getApplicationController().addTvSeriesToList(listBean, tvSeriesId);
        return "TV Series with ID " + tvSeriesId + " added to list '" + listBean.getName() + "'.";
    }
}