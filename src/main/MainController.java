package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.ResourceBundle;
import helpers.GUI;
import helpers.IO;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.note.NoteView;
import models.note.Note;
import models.settings.Settings;
import models.settings.SettingsView;

public class MainController implements Initializable {
    private static final String SETTINGS_PATH = "pallet.json";

    private Settings settings;
    private Alert alert = new Alert(AlertType.NONE);
    private File dataDir;
    private FileChooser fileChooser = new FileChooser();
    private MessageDigest md5;

    @FXML
    private ImageView clearSearch;
    @FXML
    private TextField search;
    @FXML
    private ImageView newBtn;
    @FXML
    private FlowPane notesContainer;
    @FXML
    private ImageView settingsBtn;

    public MainController() {
        try {
            md5 = MessageDigest.getInstance("MD5");

            File settingsFile = new File(SETTINGS_PATH);
            if (settingsFile.exists())
                settings = IO.readJSON(SETTINGS_PATH, Settings.class);
            else {
                settings = new Settings(new File("pallet").getAbsolutePath());
                IO.writeJSON(settings, SETTINGS_PATH);
            }

            dataDir = new File(settings.getDataPath());
            dataDir.mkdirs();

            SettingsView.setOnError(msg -> error(msg));
        } catch (IOException | NoSuchAlgorithmException e) {
            error(e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GUI.decorateBtn(settingsBtn, event -> openSettings());
        settingsBtn.hoverProperty().addListener(new ChangeListener<Boolean>() {
            RotateTransition rt = new RotateTransition(
                    Duration.millis(GUI.BUTTON_ANIMATION_DURATION), settingsBtn);

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue) {
                if (newValue) {
                    rt.setFromAngle(0);
                    rt.setToAngle(360);
                    rt.play();
                } else {
                    rt.setFromAngle(360);
                    rt.setToAngle(0);
                    rt.play();
                }
            }
        });

        File[] notes = dataDir.listFiles();
        Arrays.sort(notes,
                (f1, f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));

        try {
            for (File file : notes) {
                NoteView noteView = new NoteView(file.getAbsolutePath(), this::removeNote);
                notesContainer.getChildren().add(noteView);
            }
        } catch (IOException e) {
            error("Failed to load notes: " + e.getMessage());
        }

        notesContainer.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles())
                event.acceptTransferModes(TransferMode.ANY);
        });
        notesContainer.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                for (File file : db.getFiles())
                    newNote(file);
                success = true;
            }
            event.setDropCompleted(success);
        });

        clearSearch.setOpacity(0);
        clearSearch.setOnMouseClicked(event -> search.clear());
        clearSearch.setCursor(Cursor.HAND);
        search.focusedProperty().addListener(new ChangeListener<Boolean>() {
            FadeTransition ft = new FadeTransition(Duration.millis(400), clearSearch);

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
                    Boolean newPropertyValue) {
                if (newPropertyValue) {
                    ft.setFromValue(0.0);
                    ft.setToValue(0.7);
                    ft.play();
                } else {
                    ft.setFromValue(0.7);
                    ft.setToValue(0.0);
                    ft.play();
                }
            }
        });

        GUI.decorateBtn(newBtn, event -> {
            fileChooser.setTitle("Add Note");
            File file = fileChooser.showOpenDialog(null);
            if (file != null)
                newNote(file);
        });
    }

    private void newNote(File file) {
        if (file.isDirectory()) {
            error(String.format("File [%s] is a directory.", file.getAbsolutePath()));
            return;
        }

        String filePath = file.getAbsolutePath();
        md5.reset();
        md5.update(filePath.getBytes());
        String id = Base64.getEncoder().encodeToString(md5.digest()).replace("==", "");
        String notePath = String.format("%s/%s", settings.getDataPath(), id);

        try {
            if (new File(notePath).exists())
                return;
            Note note = new Note(id, filePath, new Date());
            NoteView view = new NoteView(note, notePath, this::removeNote);
            notesContainer.getChildren().add(view);
        } catch (IOException e) {
            error("Failed to load note content: " + e.getMessage());
        }
    }

    private void removeNote(Node noteView) {
        notesContainer.getChildren().remove(noteView);
    }

    private void openSettings() {
        String name = "Settings";
        try {
            Stage stage = new Stage();
            GUI.decorateStage(stage, name);

            FXMLLoader loader = GUI.getFXMLLoader(name);
            Parent root = loader.load();
            SettingsView controller = (SettingsView) loader.getController();
            controller.setSettings(settings);

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
        }
    }

    private void error(String msg) {
        alert.setAlertType(AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}
