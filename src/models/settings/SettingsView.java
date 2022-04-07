package models.settings;

import java.io.File;
import java.util.function.Consumer;
import helpers.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

public class SettingsView {
    private static Consumer<String> onError;

    private DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private Label dataPathLbl;
    @FXML
    private Label downloadPathLbl;

    public void set(Settings settings) {
        System.out.println(dataPathLbl);
        System.out.println(downloadPathLbl);
        dataPathLbl.setText(settings.getDataPath());
        GUI.decorateBtn(dataPathLbl, event -> {
            String path = choosePath(settings.getDataPath(), "Set Pallet Data Path");
            if (path != null) {
                settings.setDataPath(path);
                dataPathLbl.setText(path);
            }
        });

        downloadPathLbl.setText(settings.getDownloadPath());
        GUI.decorateBtn(downloadPathLbl, event -> {
            String path = choosePath(settings.getDownloadPath(), "Set Pallet Download Path");
            if (path != null) {
                settings.setDownloadPath(path);
                downloadPathLbl.setText(path);
            }
        });
    }

    public String choosePath(String initialPath, String prompt) {
        directoryChooser.setInitialDirectory(new File(initialPath));
        directoryChooser.setTitle(prompt);
        File dirFile = directoryChooser.showDialog(null);
        if (dirFile != null) {
            if (!dirFile.canWrite())
                onError.accept("This is not a writeable directory.");
            else
                return dirFile.getAbsolutePath();
        }

        return null;
    }

    public static void setOnError(Consumer<String> onError) {
        SettingsView.onError = onError;
    }
}
