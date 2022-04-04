package models.note;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.GUI;
import helpers.IO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NoteView {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static Consumer<String> onError;

    private Note note;
    private String path;
    private Consumer<Integer> onDelete;

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

    public void setNote(Note note, String path, Consumer<Integer> onDelete) throws IOException {
        this.path = String.format("%s/%s.json", path, note);
        this.note = note;
        this.onDelete = onDelete;
        save();
        populate();
    }

    public void setNote(String path, Consumer<Integer> onDelete)
            throws JsonMappingException, JsonProcessingException {
        this.path = path;
        note = mapper.readValue(path, Note.class);
        this.onDelete = onDelete;
        populate();
    }

    private void populate() {
        info.setText(String.format("ID: %s\nAddress: %s", note.toString(), note.address()));
        title.setText(note.title());
        para.setText(note.para());
        time.setText(new SimpleDateFormat("HH:mm").format(note.lastEdited()));
        if (note.isOwned()) {
            GUI.decorateBtn(network, (event) -> {
                try {
                    togglePrivacy();
                } catch (IOException e) {
                    onError.accept(e.getMessage());
                }
                network.setImage(note.isPrivate() ? new Image(IO.res("img/offline.png").toString())
                        : new Image(IO.res("img/online.png").toString()));
            });
            GUI.decorateBtn(deleteBtn, (event) -> delete());
        } else
            deleteBtn.setVisible(false);
    }

    private void togglePrivacy() throws IOException {
        note.togglePrivacy();
        save();
    }

    private void save() throws IOException {
        File outfile = new File(path);
        if (!outfile.exists())
            outfile.createNewFile();
        mapper.writeValue(outfile, note);
    }

    private void delete() {
        File outfile = new File(path);
        if (outfile.exists())
            outfile.delete();
        onDelete.accept(note.hashCode());
    }

    public static void setOnError(Consumer<String> onError) {
        NoteView.onError = onError;
    }
}
