package jimbo.gui;

import jimbo.Jimbo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for the main chat window: holds the conversation history and
 * routes each line of user input to {@link Jimbo#getResponse(String)}.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Jimbo jimbo;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private final Image jimboImage = new Image(this.getClass().getResourceAsStream("/images/Jimbo.jpeg"));

    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the {@link Jimbo} instance this window sends user input to.
     */
    public void setJimbo(Jimbo jimbo) {
        this.jimbo = jimbo;
    }

    /**
     * Reads the text currently in {@code userInput}, gets Jimbo's reply,
     * appends both as dialog boxes to {@code dialogContainer}, then clears
     * the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = jimbo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getJimboDialog(response, jimboImage)
        );
        userInput.clear();
    }
}