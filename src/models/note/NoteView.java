package models.note;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import helpers.GUI;
import helpers.IO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class NoteView extends VBox {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");
    private static final Image cancelImg = new Image(IO.res("img/cancel.png").toString());
    public static final int WIDTH = 170;
    public static final int HEIGHT = 230;

    private static Consumer<String> onError;

    private final String path;
    private final Consumer<NoteView> onDelete;
    private boolean watching = true;
    private Label timeLabel;
    private Label content;

    public final Note note;

    public NoteView(Note note, String path, Consumer<NoteView> onDelete) throws IOException {
        this.note = note;
        this.path = path;
        this.onDelete = onDelete;
        IO.writeJSON(note, this.path);
        populate();
        Thread watcher = new Thread(() -> update());
        watcher.setDaemon(true);
        watcher.start();
    }

    public NoteView(String path, Consumer<NoteView> onDelete) throws IOException {
        this(IO.readJSON(path, Note.class), path, onDelete);
    }

    private void populate() throws IOException {
        setPrefSize(WIDTH, HEIGHT);
        getStylesheets().add(IO.res("css/note.css").toString());
        getStyleClass().add("note-container");

        Label title = new Label(note.title());
        title.setTooltip(new Tooltip(note.file.getAbsolutePath()));
        title.getStyleClass().addAll("title", "white-txt");

        content = new Label(note.content());
        content.getStyleClass().addAll("para", "white-txt");
        content.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);

        VBox main = new VBox(title, content);
        main.getStyleClass().add("note-content");
        setVgrow(main, Priority.ALWAYS);
        main.setPrefHeight(200);

        timeLabel = new Label(SDF.format(note.lastEdited()));
        timeLabel.getStyleClass().add("time");

        Pane timeContainer = new Pane(timeLabel);
        timeContainer.getStyleClass().add("note-options");
        HBox.setHgrow(timeContainer, Priority.ALWAYS);

        ImageView cancel = new ImageView(cancelImg);
        Label cancelContainer = new Label();
        cancelContainer.setGraphic(cancel);
        cancelContainer.setTooltip(new Tooltip("Stop streaming this file"));
        GUI.decorateBtn(cancelContainer, (event) -> delete());

        HBox bottom = new HBox(timeContainer, cancelContainer);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(0, 7, 7, 7));

        getChildren().addAll(main, bottom);
    }

    private void delete() {
        watching = false;
        File outfile = new File(path);
        if (outfile.exists())
            outfile.delete();
        onDelete.accept(this);
    }

    private void update() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = note.file.toPath().getParent();
            WatchKey watchKey = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (watching) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    if (dir.resolve((Path) event.context()).equals(note.file.toPath())) {
                        Platform.runLater(() -> {
                            try {
                                timeLabel.setText(SDF.format(note.lastEdited()));
                                content.setText(note.content());
                            } catch (IOException e) {
                                onError.accept(e.getMessage());
                            }
                        });
                    }
                }
                Thread.sleep(5000);
            }
        } catch (IOException | InterruptedException e) {
            onError.accept(e.getMessage());
        }
    }

    public static void setOnError(Consumer<String> onError) {
        NoteView.onError = onError;
    }
}
