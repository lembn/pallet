package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.note.Note;
import models.note.NoteView;
import models.settings.Settings;
import models.settings.SettingsView;

public class MainController implements Initializable {
    private static final String SETTINGS_PATH = "pallet.json";
    private static final Random RNG = new Random();

    private Settings settings;
    private Alert alert = new Alert(AlertType.NONE);
    private Map<Integer, Integer> notes = new HashMap<Integer, Integer>();

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
            File settingsFile = new File(SETTINGS_PATH);
            if (settingsFile.exists())
                settings = IO.readJSON(SETTINGS_PATH, Settings.class);
            else {
                settings = new Settings(new File("pallet").getAbsolutePath());
                IO.writeJSON(settings, SETTINGS_PATH);
            }
            File dataDir = new File(settings.getDataPath());
            dataDir.mkdirs();

            SettingsView.setOnError(msg -> error(msg));
            NoteView.setOnError(msg -> error(msg));
        } catch (IOException e) {
            error(e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearSearch.setOpacity(0);
        clearSearch.setOnMouseClicked((event -> search.clear()));
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

        // TODO: load saved notes

        GUI.decorateBtn(newBtn, event -> newNote());
        GUI.decorateBtn(settingsBtn, event -> openSettings());
        settingsBtn.hoverProperty().addListener(new ChangeListener<Boolean>() {
            RotateTransition rt = new RotateTransition(
                    Duration.millis(GUI.BUTTON_ANIMATION_DURATION), settingsBtn);

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
                    Boolean newPropertyValue) {
                if (newPropertyValue) {
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
    }

    private void newNote() {
        try {
            int id = createNoteId();
            Note note =
                    new Note(id, "local", "#" + Integer.toHexString(id), "...", new Date(), true);

            FXMLLoader loader = GUI.getFXMLLoader("Note");
            Parent root = loader.load();
            NoteView controller = (NoteView) loader.getController();
            controller.setNote(note, settings.getDataPath(), this::removeNote);

            List<Node> displayedNotes = notesContainer.getChildren();
            int index = Math.max(displayedNotes.size() - 1, 0);
            displayedNotes.add(index, root);

            notes.put(id, index);
        } catch (IOException e) {
            error(e.getMessage());
        }
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

    private void removeNote(int id) {
        int index = notes.get(id);
        notesContainer.getChildren().remove(index);
        notes.remove(id);
    }

    // TODO: this will go on forever if it never find a free id
    private int createNoteId() {
        int id;
        File file;
        while (true) {
            id = RNG.nextInt();
            file = new File(settings.getDataPath(), Integer.toHexString(id) + ".json");
            if (!file.exists())
                return id;
        }
    }
}
