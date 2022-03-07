package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import notes.Note;

public class MainController implements Initializable {
    private static final String PALLETPATH = "palletpath";
    private static final String PALLETDIR = "pallet";
    private String dataPath;
    private Alert alert;
    private Map<Note, Integer> notes = new HashMap<Note, Integer>();

    @FXML
    private ImageView clearSearch;
    @FXML
    private TextField search;
    @FXML
    private ImageView newBtn;
    @FXML
    private FlowPane notesContainer;
    @FXML
    private ImageView settings;

    public MainController() throws IOException {
        File palletPathFile = new File(System.getProperty("java.io.tmpdir"), PALLETPATH);
        if (palletPathFile.exists())
            dataPath = IO.read(palletPathFile.getAbsolutePath());
        else {
            File palletDir = new File(PALLETDIR);
            palletDir.mkdirs();
            Files.createTempFile(PALLETPATH, null).toFile();
            updateDataPath(palletDir.getAbsolutePath());
        }
        SettingsController.setDataPath(dataPath);
        SettingsController.setOnPathChange(this::updateDataPath);
        SettingsController.setOnError(msg -> error(msg));
        NoteController.setDelete(this::removeNote);
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

        GUI.decorateBtn(newBtn, this::newNote);
        GUI.decorateBtn(settings, this::openSettings);
        settings.hoverProperty().addListener(new ChangeListener<Boolean>() {
            RotateTransition rt =
                    new RotateTransition(Duration.millis(GUI.BUTTON_ANIMATION_DURATION), settings);

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

    private void newNote(MouseEvent event) {
        try {
            int id = Note.getId(dataPath);
            Note note = new Note(id, "#" + Integer.toHexString(id), "...", new Date(), true, true);

            FXMLLoader loader = GUI.getFXMLLoader("Note");
            Parent root = loader.load();
            NoteController controller = (NoteController) loader.getController();
            controller.setNote(note);

            List<Node> displayedNotes = notesContainer.getChildren();
            int index = Math.min(displayedNotes.size() - 1, 0);
            displayedNotes.add(index, root);

            notes.put(note, index);
            note.save(dataPath);
        } catch (IOException e) {
        }
    }

    private void removeNote(Note note) {
        note.delete(dataPath);
        int index = notes.get(note);
        notesContainer.getChildren().remove(index);
        notes.remove(note);
    }

    private void openSettings(MouseEvent event) {
        String name = "Settings";
        try {
            Stage stage = new Stage();
            GUI.decorateStage(stage, name);
            stage.setScene(new Scene(GUI.loadFXML(name)));
            stage.show();
        } catch (IOException e) {
        }
    }

    private void updateDataPath(String newDataPath) {
        dataPath = newDataPath;
        File palletPathFile = new File(System.getProperty("java.io.tmpdir"), PALLETPATH);
        IO.write(dataPath, palletPathFile, this::error);
    }

    private void error(String msg) {
        alert.setAlertType(AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}
