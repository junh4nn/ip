package jimbo;

import java.io.IOException;

import jimbo.gui.MainWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX application class for Jimbo's GUI. Loads the main chat window from
 * FXML and injects a {@link Jimbo} instance for it to send user input to.
 */
public class JimboApp extends Application {
    private final Jimbo jimbo = new Jimbo(Jimbo.FILE_PATH);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JimboApp.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();

        fxmlLoader.<MainWindow>getController().setJimbo(jimbo);

        stage.setScene(new Scene(root));
        stage.show();
    }
}