package controllers;

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
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

public class SettingsController implements Initializable {
    private static Consumer<String> updateDataPath;
    private static Consumer<String> onError;
    private static DirectoryChooser directoryChooser;
    private static StringProperty dataPath = new SimpleStringProperty();

    @FXML
    private Label dataPathLbl;

    public SettingsController() {
        directoryChooser = new DirectoryChooser();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataPathLbl.textProperty().bind(dataPath);
        GUI.decorateBtn(dataPathLbl, this::choosePath);
    }

    public void choosePath(MouseEvent event) {
        directoryChooser.setInitialDirectory(new File(dataPath.get()));
        directoryChooser.setTitle("Select Pallet Folder");
        File dirFile = directoryChooser.showDialog(null);
        if (dirFile != null) {
            if (!dirFile.canWrite())
                onError.accept("This is not a writeable directory.");
            else {
                setDataPath(dirFile.getAbsolutePath());
                updateDataPath.accept(dataPath.get());
            }
        }
    }

    public static void setDataPath(String dataPath) {
        SettingsController.dataPath.set(dataPath);
    }

    public static void setOnPathChange(Consumer<String> updateDataPath) {
        SettingsController.updateDataPath = updateDataPath;
    }

    public static void setOnError(Consumer<String> onError) {
        SettingsController.onError = onError;
    }
}
