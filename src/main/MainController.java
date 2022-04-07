package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import helpers.GUI;
import helpers.IO;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
    private FileChooser fileChooser = new FileChooser();
    private NoteManager noteManager;

    @FXML
    private ImageView clearSearch;
    @FXML
    private TextField search;
    @FXML
    private ImageView newBtn;
    @FXML
    private FlowPane notesContainer;
    @FXML
    private Node download;
    @FXML
    private ImageView settingsBtn;

    public MainController() {
        try {
            File settingsFile = new File(SETTINGS_PATH);
            if (settingsFile.exists())
                settings = IO.readJSON(SETTINGS_PATH, Settings.class);
            else {
                settings = new Settings(new File("notes").getAbsolutePath(),
                        new File("downloads").getAbsolutePath());
                IO.writeJSON(settings, SETTINGS_PATH);
            }


            SettingsView.setOnError(msg -> error(msg));
            NoteView.setOnError(msg -> error(msg));
        } catch (IOException e) {
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
        download.setOpacity(0);
        search.focusedProperty().addListener(new ChangeListener<Boolean>() {
            FadeTransition clearSearchFT = new FadeTransition(Duration.millis(400), clearSearch);
            FadeTransition downloadFT = new FadeTransition(Duration.millis(400), download);
            FadeTransition[] transitions = {clearSearchFT, downloadFT};
            ParallelTransition pt = new ParallelTransition(transitions);

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
                    Boolean newPropertyValue) {
                if (newPropertyValue) {
                    for (FadeTransition transition : transitions) {
                        transition.setFromValue(0.0);
                        transition.setToValue(GUI.INITIAL_BUTTON_OPACITY);
                    }
                    pt.play();
                } else {
                    for (FadeTransition transition : transitions) {
                        transition.setFromValue(GUI.INITIAL_BUTTON_OPACITY);
                        transition.setToValue(0.0);
                    }
                    pt.play();
                }
            }
        });

        GUI.decorateBtn(clearSearch, event -> search.clear());
        GUI.decorateBtn(download, event -> download());

        GUI.decorateBtn(newBtn, event -> {
            fileChooser.setTitle("Add Note");
            File file = fileChooser.showOpenDialog(null);
            if (file != null)
                newNote(file);
        });

        try {
            noteManager = new NoteManager(settings, note -> {
                try {
                    NoteView view = new NoteView(note, this::removeNote);
                    notesContainer.getChildren().add(view);
                } catch (IOException e) {
                    error(String.format("Failed to load file [%s]: %s", note.file, e.getMessage()));
                }

            });
        } catch (IOException e) {
            error("Failed to load files: " + e.getMessage());
        }
    }

    private void newNote(File file) {
        if (file.isDirectory()) {
            error(String.format("File [%s] is a directory.", file.getAbsolutePath()));
            return;
        }

        try {
            Note note = noteManager.makeNote(file);
            NoteView view = new NoteView(note, this::removeNote);
            notesContainer.getChildren().add(view);
        } catch (IOException e) {
            error(String.format("Failed to upload file [%s]: %s", file, e.getMessage()));
        }
    }

    private void removeNote(NoteView noteView) {
        notesContainer.getChildren().remove(noteView);
        noteManager.remove(noteView.note);
    }

    private void download() {
        System.out.println(search.getText());
    }

    private void openSettings() {
        String name = "Settings";
        try {
            Stage stage = new Stage();
            GUI.decorateStage(stage, name);

            FXMLLoader loader = GUI.getFXMLLoader(name);
            Parent root = loader.load();
            SettingsView controller = (SettingsView) loader.getController();
            controller.set(settings);

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            error(e.getMessage());
        }
    }

    private void error(String msg) {
        alert.setAlertType(AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}
