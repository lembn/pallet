package main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import helpers.Encoding;
import helpers.IO;
import models.note.Note;
import models.settings.Settings;

public class NoteManager {
    private static final int SHORT_ID_LENGTH = 5;

    private final Map<String, String> noteIds = new HashMap<String, String>();
    private final Settings settings;

    public NoteManager(Settings settings, Consumer<File> noteConsumer) throws IOException {
        this.settings = settings;

        File[] notes = new File(settings.getDataPath()).listFiles();
        Arrays.sort(notes,
                (f1, f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));
        for (File file : notes)
            noteConsumer.accept(file);
    }

    public void remove(Note note) {
        noteIds.remove(note.id.substring(0, SHORT_ID_LENGTH), note.id);
        File noteFile = new File(settings.getDataPath(), note.id);
        if (noteFile.exists())
            noteFile.delete();
    }

    public File getNoteFile(String shortId) {
        File file = new File(settings.getDataPath(), noteIds.get(shortId));
        if (!file.exists())
            file = null;
        return file;
    }

    public File getDownloadDir() {
        return new File(settings.getDownloadPath());
    }

    public Note makeNote(File file) throws IOException {
        String filePath = file.getAbsolutePath();
        String id = Encoding.base62Encode(filePath);
        String notePath = String.format("%s/%s", settings.getDataPath(), id);

        Note note = null;
        if (!new File(notePath).exists()) {
            note = new Note(id, filePath);
            noteIds.put(note.id.substring(0, SHORT_ID_LENGTH), note.id);
            IO.writeJSON(note, notePath);
        }

        return note;
    }
}
