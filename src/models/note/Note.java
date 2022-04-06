package models.note;

import java.io.IOException;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import helpers.IO;

public class Note {
    private final String id;
    private String path;
    private Date lastEdited;

    @JsonCreator
    public Note(@JsonProperty("id") String id, @JsonProperty("path") String path,
            @JsonProperty("lastEdited") Date lastEdited) {
        this.id = id;
        this.path = path;
        this.lastEdited = lastEdited;
    }

    public String title() {
        return "#" + this;
    }

    public String content() throws IOException {
        return IO.readLines(path, 10);
    }

    public String path() {
        return path;
    }

    public Date lastEdited() {
        return lastEdited;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
