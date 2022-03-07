package helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public final class IO {
    private static final String RES_PATH = "../res";

    public static String read(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
                line = reader.readLine();
            }
            return builder.toString().trim();
        }
    }

    public static void write(String data, File file, Consumer<String> onFail) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        } catch (IOException e) {
            onFail.accept(e.getMessage());
        }
    }

    public static URL res(String path) {
        return IO.class.getResource(String.format("%s/%s", RES_PATH, path));
    }
}
