package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
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
    private File dataDir;

    @FXML
    private ScrollPane scrollPane;
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

            dataDir = new File(settings.getDataPath());
            dataDir.mkdirs();

            SettingsView.setOnError(msg -> error(msg));
            NoteView.setOnError(msg -> error(msg));
        } catch (IOException e) {
            error(e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File[] notes = dataDir.listFiles();
        Arrays.sort(notes,
                (f1, f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));

        try {
            for (File file : notes) {
                FXMLLoader loader = GUI.getFXMLLoader("Note");
                Parent root = loader.load();
                NoteView controller = (NoteView) loader.getController();
                controller.setNote(file.getAbsolutePath(), this::removeNote);
                notesContainer.getChildren().add(root);
            }
            setScrollHeight();
        } catch (IOException e) {
            error("Failed to load notes: " + e.getMessage());
        }


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

        GUI.decorateBtn(newBtn, event -> newNote());
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
    }

    private void newNote() {
        int id;
        File file;
        // TODO: this will get stuck if we never find a free id
        while (true) {
            id = RNG.nextInt();
            file = new File(settings.getDataPath(), Integer.toHexString(id) + ".json");
            if (!file.exists())
                break;
        }

        Note note = new Note(id, "...", new Date());
        try {
            FXMLLoader loader = GUI.getFXMLLoader("Note");
            Parent root = loader.load();
            NoteView controller = (NoteView) loader.getController();
            controller.setNote(note, settings.getDataPath(), this::removeNote);
            notesContainer.getChildren().add(root);
            setScrollHeight();
        } catch (IOException e) {
            error(e.getMessage());
        }
    }

    private void removeNote(int id) {
        // TODO: implement
        setScrollHeight();
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

    private void setScrollHeight() {
        if (notesPerRow() == 0)
            return;
        scrollPane.setVmax(
                ((int) Math.max(dataDir.list().length - 1, 0) / notesPerRow()) * noteHeight());
    }

    private double noteHeight() {
        return NoteView.HEIGHT + notesContainer.getVgap();
    }

    private int notesPerRow() {
        int noteWidth = NoteView.WIDTH + (int) notesContainer.getHgap();
        int value = (int) scrollPane.getWidth() / (int) noteWidth;
        while (value * noteWidth - notesContainer.getHgap() > scrollPane.getWidth())
            value--;
        return value;
    }

    private void error(String msg) {
        alert.setAlertType(AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}
