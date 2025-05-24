package ispw.project.project_ispw.controller.graphic.gui;

/**
 * An interface to be implemented by all specific view controllers (e.g., LoginController, HomeController, SearchController)
 * that need a reference to the central GraphicControllerGui (Screen Manager) for navigation or
 * to initiate other application-level UI flow actions.
 */
public interface NavigableController {

    /**
     * Sets the reference to the GraphicControllerGui (Screen Manager) for this view controller.
     * This method is typically called by the GraphicControllerGui itself when loading the FXML and its associated controller.
     *
     * @param graphicController The instance of the GraphicControllerGui.
     */
    void setGraphicController(GraphicControllerGui graphicController);
}