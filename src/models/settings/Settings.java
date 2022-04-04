package models.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Settings {
    private String dataPath;

    @JsonCreator
    public Settings(@JsonProperty("dataPath") String dataPath) {
        this.dataPath = dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getDataPath() {
        return dataPath;
    }
}
