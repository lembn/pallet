package models.settings;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;
import helpers.GUI;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

public class SettingsView {
    private static Consumer<String> onError;

    private DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private Label dataPathLbl;
    @FXML
    private Label downloadPathLbl;
    @FXML
    private Label ipLbl;

    public void set(Settings settings) throws UnknownHostException {
        dataPathLbl.setText(settings.getDataPath());
        GUI.decorateBtn(dataPathLbl, event -> {
            String path = choosePath(settings.getDataPath(), "Set Pallet Data Path");
            if (path != null) {
                settings.setDataPath(path);
                dataPathLbl.setText(path);
            }
        });
        dataPathLbl.setCursor(Cursor.HAND);

        downloadPathLbl.setText(settings.getDownloadPath());
        GUI.decorateBtn(downloadPathLbl, event -> {
            String path = choosePath(settings.getDownloadPath(), "Set Pallet Download Path");
            if (path != null) {
                settings.setDownloadPath(path);
                downloadPathLbl.setText(path);
            }
        });
        downloadPathLbl.setCursor(Cursor.HAND);

        ipLbl.setText(InetAddress.getLocalHost().getHostAddress());
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
