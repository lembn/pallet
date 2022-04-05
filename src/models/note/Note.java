package models.note;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Note {
    private final int id;
    private String para;
    private Date lastEdited;

    @JsonCreator
    public Note(@JsonProperty("id") int id, @JsonProperty("para") String para,
            @JsonProperty("lastEdited") Date lastEdited) {
        this.id = id;
        this.para = para;
        this.lastEdited = lastEdited;
    }

    public String title() {
        return "#" + this;
    }

    public String para() {
        return para;
    }

    public Date lastEdited() {
        return lastEdited;
    }

    @Override
    public String toString() {
        return Integer.toHexString(id);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
