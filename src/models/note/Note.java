package models.note;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import helpers.IO;

public class Note {
    public final String id;
    public final File file;

    private String shortId;

    @JsonCreator
    public Note(@JsonProperty("id") String id, @JsonProperty("file") String file) {
        this.id = id;
        this.file = new File(file);
    }

    public String title() {
        return "#" + shortId;
    }

    public String content() throws IOException {
        return IO.readLines(file.toString(), 10);
    }

    public Date lastEdited() {
        return new Date(file.lastModified());
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }
}
