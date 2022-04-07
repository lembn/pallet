package models.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Settings {
    private String dataPath;
    private String downloadPath;

    @JsonCreator
    public Settings(@JsonProperty("dataPath") String dataPath,
            @JsonProperty("downloadPath") String downloadPath) {
        this.dataPath = dataPath;
        this.downloadPath = downloadPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getDownloadPath() {
        return downloadPath;
    }
}
