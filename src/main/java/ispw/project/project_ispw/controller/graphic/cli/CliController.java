package ispw.project.project_ispw.controller.graphic.cli;

// Necessary imports (Bean classes are not directly used in CliController now, as GraphicControllerCli handles them)
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

    private GraphicControllerCli graphicControllerCli; // Reference to the graphic controller backend
    private StringBuilder currentInputLine = new StringBuilder(); // Buffer for the user's current command
    private int inputLineStart = 0; // Marks the beginning of the user's editable input area

    // Constructor should be empty for FXML controllers
    public CliController() {
        // Initialization logic goes in initialize()
    }

    @FXML
    public void initialize() {
        // Get the singleton instance of GraphicControllerCli.
        // It's guaranteed to be initialized by MainApp.start() before this FXML is loaded.
        this.graphicControllerCli = GraphicControllerCli.getInstance();

        // Configure TextArea properties
        cliTextArea.setWrapText(true);
        cliTextArea.setEditable(false); // Make it read-only by default, user only types after the prompt

        // Initial welcome message and prompt
        displayOutput("Welcome to Media Hub CLI (JavaFX)! Type 'help' for commands.");
        promptForInput();

        // Add listeners for UI events
        submitButton.setOnAction(event -> processCurrentInput());
        cliTextArea.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);

        // Ensure caret is always at the end when text changes (important for a terminal)
        cliTextArea.textProperty().addListener((obs, oldText, newText) -> {
            cliTextArea.positionCaret(newText.length());
        });
    }

    /**
     * Appends text to the TextArea and ensures it's followed by a newline.
     * This is for displaying application output.
     * @param text The text to display.
     */
    private void displayOutput(String text) {
        // Make textarea temporarily editable to append text
        cliTextArea.setEditable(true);
        cliTextArea.appendText(text + "\n");
        // Immediately make it non-editable again until promptForInput
        cliTextArea.setEditable(false);
    }

    /**
     * Displays the input prompt and sets up the TextArea for user input.
     */
    private void promptForInput() {
        displayOutput("\n> "); // Display the prompt
        inputLineStart = cliTextArea.getText().length(); // Record the start of the new input line
        currentInputLine.setLength(0); // Clear the internal buffer for the new command
        cliTextArea.setEditable(true); // Allow user to type
        cliTextArea.positionCaret(cliTextArea.getText().length()); // Place cursor at the end
    }

    /**
     * Handles keyboard events, especially ENTER for command submission.
     * Also controls backspace and cursor movement to protect output.
     * @param event The KeyEvent.
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume(); // Prevent default new line behavior
            processCurrentInput();
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            // Prevent backspace from deleting characters before the current input line
            if (cliTextArea.getCaretPosition() <= inputLineStart) {
                event.consume(); // Block the backspace
            } else {
                // Allow normal backspace within the user's input
                if (currentInputLine.length() > 0) {
                    currentInputLine.deleteCharAt(currentInputLine.length() - 1);
                }
            }
        } else if (event.getCode().isArrowKey()) {
            // Prevent arrow keys from moving cursor into the previous output area
            if (cliTextArea.getCaretPosition() < inputLineStart && event.getCode() != KeyCode.RIGHT) {
                cliTextArea.positionCaret(inputLineStart);
            }
        } else if (event.getText().length() > 0) { // Check if a character was actually typed
            // Append typed character to the internal buffer, only if it's within the editable area
            if (cliTextArea.isEditable() && cliTextArea.getCaretPosition() >= inputLineStart) {
                currentInputLine.append(event.getText());
            }
        }
    }

    /**
     * Processes the command entered by the user.
     * Extracts the command, sends it to GraphicControllerCli, and displays the response.
     */
    private void processCurrentInput() {
        // Temporarily make TextArea read-only while processing
        cliTextArea.setEditable(false);

        // Get the command entered by the user (only the part after the last prompt)
        String command = currentInputLine.toString().trim(); // Use the internal buffer

        if (command.isEmpty()) {
            promptForInput(); // Just re-prompt if nothing was typed
            return;
        }

        // Echo the command (optional, but good for terminal feel)
        displayOutput(">>> " + command);

        // Special handling for 'clear' and 'exit' commands directly in the UI controller
        if (command.equalsIgnoreCase("clear")) {
            cliTextArea.clear();
            displayOutput("Welcome to Media Hub CLI (JavaFX)! Type 'help' for commands.");
            promptForInput();
            return;
        }

        if (command.equalsIgnoreCase("exit")) {
            displayOutput("Exiting application. Goodbye!");
            javafx.application.Platform.exit(); // Properly shut down JavaFX
            // System.exit(0); // Optional: if you want to force exit immediately
            return;
        }

        // Delegate command processing to GraphicControllerCli
        try {
            String response = graphicControllerCli.processCliCommand(command);
            displayOutput(response);
        } catch (Exception e) {
            displayOutput("An internal error occurred: " + e.getMessage());
        }

        // Prepare for the next command
        promptForInput();
    }
}