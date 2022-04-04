package models.note;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonCreator;

public class Note {
    private int id;
    private String address;
    private String title;
    private String para;
    private Date lastEdited;
    private boolean isOwned;
    private boolean isPrivate;

    @JsonCreator
    public Note(int id, String address, String title, String para, Date lastEdited, boolean isOwned,
            boolean isPrivate) {
        this.id = id;
        this.address = address;
        this.title = title;
        this.para = para;
        this.lastEdited = lastEdited;
        this.isOwned = isOwned;
        this.isPrivate = isPrivate;
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

    public void togglePrivacy() {
        isPrivate = !isPrivate;
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
