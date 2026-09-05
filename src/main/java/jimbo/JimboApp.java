package jimbo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * JavaFX application class for Jimbo's GUI. Currently just a "Hello World!"
 * placeholder used to verify the JavaFX setup works end to end; the real
 * chat window (wired to {@link Jimbo#getResponse(String)}) replaces this
 * later.
 */
public class JimboApp extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello World!");
        Scene scene = new Scene(label);

        stage.setScene(scene);
        stage.show();
    }
}