package models.settings;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import helpers.GUI;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

public class SettingsView implements Initializable {
    private static Consumer<String> onError;

    private Settings settings;
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private StringProperty dataPath = new SimpleStringProperty();

    @FXML
    private Label dataPathLbl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataPathLbl.textProperty().bind(dataPath);
        GUI.decorateBtn(dataPathLbl, event -> choosePath());
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
        dataPath.set(settings.getDataPath());
    }

    public void choosePath() {
        directoryChooser.setInitialDirectory(new File(dataPath.get()));
        directoryChooser.setTitle("Select Pallet Folder");
        File dirFile = directoryChooser.showDialog(null);
        if (dirFile != null) {
            if (!dirFile.canWrite())
                onError.accept("This is not a writeable directory.");
            else {
                String path = dirFile.getAbsolutePath();
                dataPath.set(path);
                settings.setDataPath(path);
            }
        }
    }

    public static void setOnError(Consumer<String> onError) {
        SettingsView.onError = onError;
    }
}
