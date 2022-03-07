package notes;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Note {
    private static ObjectMapper mapper = new ObjectMapper();

    private int id;
    private String title;
    private String para;
    private Date lastEdited;
    private boolean isOwned;
    private boolean isPrivate;

    @JsonCreator
    public Note(int id, String title, String para, Date lastEdited, boolean isOwned,
            boolean isPrivate) {
        this.id = id;
        this.title = title;
        this.para = para;
        this.lastEdited = lastEdited;
        this.isOwned = isOwned;
        this.isPrivate = isPrivate;
    }

    public Note(String path) throws JsonMappingException, JsonProcessingException {
        Note note = mapper.readValue(path, Note.class);
        this.title = note.title;
        this.para = note.para;
        this.lastEdited = note.lastEdited;
        this.isOwned = note.isOwned;
        this.isPrivate = note.isPrivate;
    }

    public int id() {
        return id;
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

    public void save(String path) throws StreamWriteException, DatabindException, IOException {
        File outfile = new File(String.format("%s/%s.json", path, this));
        if (!outfile.exists())
            outfile.createNewFile();
        mapper.writeValue(outfile, this);
    }

    public void delete(String path) {
        File outfile = new File(String.format("%s/%s.json", path, this));
        if (outfile.exists())
            outfile.delete();
    }

    @Override
    public String toString() {
        return Integer.toHexString(id);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Note) {
            Note other = (Note) obj;
            return id == other.id;
        } else
            return false;
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
}
