package communication;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;
import helpers.Concurrency;

public class Communicator {
    private static final int PORT = 2003;
    private static final int BUFFER_SIZE = 1024;

    private final Consumer<String> onError;

    public Communicator(Consumer<String> onError) {
        this.onError = onError;
    }

    public void requestDownloadAsync(String host, String noteId, String path) {
        Concurrency.runDaemon(() -> {
            try (Socket socket = new Socket(host, PORT)) {
                send(MessageType.DOWNLOAD, noteId, socket);

                MessageType res = readHeader(socket);
                if (res == MessageType.NOT_FOUND)
                    throw new CommunicationException("Failed to download note [%s] from %s", noteId,
                            host);

                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                byte[] buffer = new byte[BUFFER_SIZE];
                try (FileOutputStream out = new FileOutputStream(new File(path))) {
                    int bytesRead = 0;
                    while ((bytesRead = bis.read(buffer)) != -1)
                        out.write(buffer);
                }
            } catch (ClassNotFoundException | IOException | CommunicationException e) {
                onError.accept(e.getMessage());
            }
        });
    }

    private void send(MessageType header, String s, Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(header);
        DataOutputStream das = new DataOutputStream(socket.getOutputStream());
        das.writeUTF(s);
        das.flush();
    }

    private MessageType readHeader(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        MessageType res = (MessageType) ois.readObject();
        return res;
    }
}
