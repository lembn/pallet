package main;

import java.io.IOException;
import helpers.GUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // TODO: unfocus search bar
        GUI.decorateStage(stage);
        Scene scene = new Scene(GUI.loadFXML("Main"));
        stage.setScene(scene);
        stage.show();
    }
}
