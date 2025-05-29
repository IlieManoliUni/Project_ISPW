package ispw.project.project_ispw.controller.graphic.cli.command;

import ispw.project.project_ispw.controller.graphic.cli.GraphicControllerCli;

import ispw.project.project_ispw.exception.CliCommandException;
import ispw.project.project_ispw.exception.ExceptionApplicationController;
import ispw.project.project_ispw.exception.ExceptionUser;

public interface CliCommand {
    String execute(GraphicControllerCli context, String args) throws CliCommandException, ExceptionApplicationController, ExceptionUser;
}