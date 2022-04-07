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

    public NoteManager(Settings settings, Consumer<Note> noteConsumer) throws IOException {
        this.settings = settings;

        File dataDir = new File(settings.getDataPath());
        File downloadDir = new File(settings.getDownloadPath());
        dataDir.mkdirs();
        downloadDir.mkdirs();

        File[] notes = dataDir.listFiles();
        Arrays.sort(notes,
                (f1, f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));
        for (File file : notes) {
            Note note = IO.readJSON(file.toString(), Note.class);
            note.setShortId(getShortId(note.id));
            noteConsumer.accept(note);
        }
    }

    public void remove(Note note) {
        noteIds.remove(note.id.substring(0, SHORT_ID_LENGTH), note.id);
        File noteFile = new File(settings.getDataPath(), note.id);
        if (noteFile.exists())
            noteFile.delete();
    }

    public File getFileOfNote(String shortId) throws IOException {
        File file = new File(settings.getDataPath(), noteIds.get(shortId));
        if (!file.exists())
            return null;

        Note note = IO.readJSON(file.toString(), Note.class);
        return note.file;
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
            String shortId = getShortId(id);
            note.setShortId(shortId);
            noteIds.put(shortId, note.id);
            IO.writeJSON(note, notePath);
        }

        return note;
    }

    private String getShortId(String longId) {
        return longId.substring(0, SHORT_ID_LENGTH);
    }
}
