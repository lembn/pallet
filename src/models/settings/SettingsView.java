package models.settings;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import helpers.GUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

public class SettingsView implements Initializable {
    private static Consumer<String> onError;

    private Settings settings;
    private DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private Label dataPathLbl;
    @FXML
    private Label usernameInput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataPathLbl.setText(settings.getDataPath());
        GUI.decorateBtn(dataPathLbl, event -> choosePath());
        usernameInput.setText(settings.getUsername());
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void choosePath() {
        directoryChooser.setInitialDirectory(new File(settings.getDataPath()));
        directoryChooser.setTitle("Select Pallet Folder");
        File dirFile = directoryChooser.showDialog(null);
        if (dirFile != null) {
            if (!dirFile.canWrite())
                onError.accept("This is not a writeable directory.");
            else {
                String path = dirFile.getAbsolutePath();
                settings.setDataPath(path);
                dataPathLbl.setText(settings.getDataPath());
            }
        }
    }

    public static void setOnError(Consumer<String> onError) {
        SettingsView.onError = onError;
    }
}
