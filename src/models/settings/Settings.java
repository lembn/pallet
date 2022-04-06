package models.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Settings {
    private String dataPath;
    private String username;

    @JsonCreator
    public Settings(@JsonProperty("dataPath") String dataPath,
            @JsonProperty("username") String username) {
        this.dataPath = dataPath;
        this.username = username;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
