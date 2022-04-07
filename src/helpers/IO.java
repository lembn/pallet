package helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class IO {
    private static final ObjectMapper mapper =
            new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    private static final String RES_PATH = "../res";

    public static <T> void writeJSON(T obj, String path) throws IOException {
        File outfile = new File(path);
        mapper.writeValue(outfile, obj);
    }

    public static <T> T readJSON(String path, Class<T> clazz) throws IOException {
        return mapper.readValue(new File(path), clazz);
    }

    public static String readLines(String file, int lines) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();

            int counter = 1;
            while (line != null && counter < lines) {
                builder.append(line);
                builder.append(System.lineSeparator());
                line = reader.readLine();
                counter++;
            }
            return builder.toString().trim();
        }
    }

    public static URL res(String path) {
        return IO.class.getResource(String.format("%s/%s", RES_PATH, path));
    }
}
