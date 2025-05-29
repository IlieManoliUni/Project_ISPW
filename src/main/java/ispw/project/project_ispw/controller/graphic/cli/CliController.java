package ispw.project.project_ispw.controller.graphic.cli;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class CliController {

    @FXML
    private TextArea cliTextArea;

    @FXML
    private Button submitButton;

    private GraphicControllerCli graphicControllerCli;
    private StringBuilder currentInputLine = new StringBuilder();
    private int inputLineStart = 0;

    public CliController() {
        // Initialization logic goes in initialize()
    }

    @FXML
    public void initialize() {
        this.graphicControllerCli = GraphicControllerCli.getInstance();

        cliTextArea.setWrapText(true);
        cliTextArea.setEditable(false);

        displayOutput("Welcome to Media Hub CLI (JavaFX)! Type 'help' for commands.");
        promptForInput();

        submitButton.setOnAction(event -> processCurrentInput());
        cliTextArea.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);

        cliTextArea.textProperty().addListener((obs, oldText, newText) ->
            cliTextArea.positionCaret(newText.length()));
    }

    private void displayOutput(String text) {
        cliTextArea.setEditable(true);
        cliTextArea.appendText(text + "\n");
        cliTextArea.setEditable(false);
    }

    private void promptForInput() {
        displayOutput("\n> ");
        inputLineStart = cliTextArea.getText().length();
        currentInputLine.setLength(0);
        cliTextArea.setEditable(true);
        cliTextArea.positionCaret(cliTextArea.getText().length());
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            processCurrentInput();
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            if (cliTextArea.getCaretPosition() <= inputLineStart) {
                event.consume();
            } else {
                if (currentInputLine.length() > 0) {
                    currentInputLine.deleteCharAt(currentInputLine.length() - 1);
                }
            }
        } else if (event.getCode().isArrowKey()) {
            if (cliTextArea.getCaretPosition() < inputLineStart && event.getCode() != KeyCode.RIGHT) {
                cliTextArea.positionCaret(inputLineStart);
            }
        } else if (event.getText().isEmpty() && cliTextArea.isEditable() && cliTextArea.getCaretPosition() >= inputLineStart) {
                currentInputLine.append(event.getText());
            }

    }

    private void processCurrentInput() {
        cliTextArea.setEditable(false);

        String command = currentInputLine.toString().trim();

        if (command.isEmpty()) {
            promptForInput();
            return;
        }

        displayOutput(">>> " + command);

        if (command.equalsIgnoreCase("clear")) {
            cliTextArea.clear();
            displayOutput("Welcome to Media Hub CLI (JavaFX)! Type 'help' for commands.");
            promptForInput();
            return;
        }

        if (command.equalsIgnoreCase("exit")) {
            displayOutput("Exiting application. Goodbye!");
            javafx.application.Platform.exit();
            return;
        }

        try {
            String response = graphicControllerCli.processCliCommand(command);
            displayOutput(response);
        } catch (Exception e) {
            displayOutput("An internal error occurred: " + e.getMessage());
        }

        promptForInput();
    }
}