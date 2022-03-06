package main;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static final String RES_PATH = "../res";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().addAll(new Image(resStr("img/icon64.png")),
                new Image(resStr("img/icon32.png")), new Image(resStr("img/icon16.png")));
        stage.setTitle("Pallet");
        Parent root = FXMLLoader.load(res("view/Main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        root.requestFocus();
        stage.show();
    }

    public URL res(String path) {
        return getClass().getResource(String.format("%s/%s", RES_PATH, path));
    }

    public String resStr(String path) {
        return getClass().getResource(String.format("%s/%s", RES_PATH, path)).toString();
    }
}
