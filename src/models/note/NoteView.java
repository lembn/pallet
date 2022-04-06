package models.note;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import helpers.GUI;
import helpers.IO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class NoteView extends VBox {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");
    private static final Image deleteImage = new Image(IO.res("img/trash.png").toString());
    public static final int WIDTH = 170;
    public static final int HEIGHT = 230;

    private final Note note;
    private final String path;
    private final Consumer<Node> onDelete;

    public NoteView(Note note, String path, Consumer<Node> onDelete) throws IOException {
        this.note = note;
        this.path = path;
        this.onDelete = onDelete;
        IO.writeJSON(note, this.path);
        populate();
    }

    public NoteView(String path, Consumer<Node> onDelete) throws IOException {
        this(IO.readJSON(path, Note.class), path, onDelete);
    }

    private void populate() throws IOException {
        setPrefSize(WIDTH, HEIGHT);
        getStylesheets().add(IO.res("css/note.css").toString());
        getStyleClass().add("note-container");

        Label title = new Label(note.title());
        title.getStyleClass().addAll("title", "white-txt");

        Label para = new Label(note.content());
        para.getStyleClass().addAll("para", "white-txt");
        para.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);

        VBox main = new VBox(title, para);
        main.getStyleClass().add("note-content");
        setVgrow(main, Priority.ALWAYS);
        main.setPrefHeight(200);

        Label timeLabel = new Label(SDF.format(note.lastEdited()));
        timeLabel.getStyleClass().add("time");

        Pane timeContainer = new Pane(timeLabel);
        timeContainer.getStyleClass().add("note-options");
        HBox.setHgrow(timeContainer, Priority.ALWAYS);

        ImageView deleteBtn = new ImageView(deleteImage);
        deleteBtn.setFitWidth(16);
        deleteBtn.setFitHeight(16);
        GUI.decorateBtn(deleteBtn, (event) -> delete());

        Pane deleteContainer = new Pane(deleteBtn);

        HBox bottom = new HBox(timeContainer, deleteContainer);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(0, 7, 3, 7));

        getChildren().addAll(main, bottom);
    }

    private void delete() {
        File outfile = new File(path);
        if (outfile.exists())
            outfile.delete();
        onDelete.accept(this);
    }
}
