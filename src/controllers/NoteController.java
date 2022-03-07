package controllers;

import java.util.function.Consumer;
import helpers.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import notes.Note;

public class NoteController {
    private static Consumer<Note> delete;

    @FXML
    private Tooltip id;
    @FXML
    private Label title;
    @FXML
    private Label para;
    @FXML
    private ImageView network;
    @FXML
    private ImageView deleteBtn;

    public void setNote(Note note) {
        id.setText(note.toString());
        title.setText(note.title());
        para.setText(note.para());
        if (note.isOwned()) {
            GUI.decorateBtn(network, (event) -> System.out.println("network"));
            GUI.decorateBtn(deleteBtn, (event) -> delete.accept(note));
        } else
            deleteBtn.setVisible(false);
    }

    public static void setDelete(Consumer<Note> delete) {
        NoteController.delete = delete;
    }
}
