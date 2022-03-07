package controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import helpers.GUI;
import helpers.IO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import notes.Note;

public class NoteController {
    private static Consumer<String> onError;

    @FXML
    private Tooltip info;
    @FXML
    private Label title;
    @FXML
    private Label para;
    @FXML
    private Label time;
    @FXML
    private ImageView network;
    @FXML
    private ImageView deleteBtn;

    public void setNote(Note note) {
        info.setText(String.format("ID: %s\nAddress: %s", note.toString(), note.address()));
        title.setText(note.title());
        para.setText(note.para());
        time.setText(new SimpleDateFormat("HH:mm").format(note.lastEdited()));
        if (note.isOwned()) {
            GUI.decorateBtn(network, (event) -> {
                try {
                    note.togglePrivacy();
                } catch (IOException e) {
                    onError.accept(e.getMessage());
                }
                network.setImage(note.isPrivate() ? new Image(IO.res("img/offline.png").toString())
                        : new Image(IO.res("img/online.png").toString()));
            });
            GUI.decorateBtn(deleteBtn, (event) -> note.delete());
        } else
            deleteBtn.setVisible(false);
    }

    public static void setOnError(Consumer<String> onError) {
        NoteController.onError = onError;
    }
}
