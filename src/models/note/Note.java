package models.note;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import helpers.IO;

public class Note {
    private final String id;
    public final File file;

    @JsonCreator
    public Note(@JsonProperty("id") String id, @JsonProperty("file") String file) {
        this.id = id;
        this.file = new File(file);
    }

    public String title() {
        return "#" + this;
    }

    public String content() throws IOException {
        return IO.readLines(file, 10);
    }

    public Date lastEdited() {
        return new Date(file.lastModified());
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
