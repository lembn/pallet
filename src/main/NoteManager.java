package main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import models.note.Note;
import models.settings.Settings;

public class NoteManager {
    private static final int SHORT_ID_LENGTH = 5;

    private final Map<String, String> noteIds = new HashMap<String, String>();
    private final Settings settings;

    public NoteManager(Settings settings) {
        this.settings = settings;
    }

    public void register(Note note) {
        noteIds.put(note.id.substring(0, SHORT_ID_LENGTH), note.id);
    }

    public void unregister(Note note) {
        noteIds.remove(note.id.substring(0, SHORT_ID_LENGTH), note.id);
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
}
