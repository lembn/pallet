package notes;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Note {
    private static ObjectMapper mapper = new ObjectMapper();
    private static Consumer<Integer> onDelete;

    private int id;
    private String address;
    private String title;
    private String para;
    private Date lastEdited;
    private boolean isOwned;
    private boolean isPrivate;
    private String path;

    @JsonCreator
    public Note(int id, String address, String title, String para, Date lastEdited, boolean isOwned,
            boolean isPrivate, String path) {
        this.id = id;
        this.address = address;
        this.title = title;
        this.para = para;
        this.lastEdited = lastEdited;
        this.isOwned = isOwned;
        this.isPrivate = isPrivate;
        this.path = String.format("%s/%s.json", path, this);
    }

    public Note(String path) throws JsonMappingException, JsonProcessingException {
        Note note = mapper.readValue(path, Note.class);
        this.id = note.id;
        this.title = note.title;
        this.para = note.para;
        this.lastEdited = note.lastEdited;
        this.isOwned = note.isOwned;
        this.isPrivate = note.isPrivate;
        this.path = path;
    }

    public int id() {
        return id;
    }

    public String address() {
        return address;
    }

    public String title() {
        return title;
    }

    public String para() {
        return para;
    }

    public Date lastEdited() {
        return lastEdited;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void togglePrivacy() throws StreamWriteException, DatabindException, IOException {
        this.isPrivate = !this.isPrivate;
        save();
    }

    public void save() throws StreamWriteException, DatabindException, IOException {
        // TODO: debounce
        File outfile = new File(path);
        if (!outfile.exists())
            outfile.createNewFile();
        mapper.writeValue(outfile, this);
    }

    public void delete() {
        File outfile = new File(path);
        if (outfile.exists())
            outfile.delete();
        onDelete.accept(id);
    }

    @Override
    public String toString() {
        return Integer.toHexString(id);
    }

    // TODO: this will go on forever if it never find a free id
    public static int getId(String path) {
        int id;
        File file;
        while (true) {
            id = (new Random()).nextInt();
            file = new File(path, Integer.toHexString(id) + ".json");
            if (!file.exists())
                return id;
        }
    }

    public static void setOnDelete(Consumer<Integer> onDelete) {
        Note.onDelete = onDelete;
    }
}
