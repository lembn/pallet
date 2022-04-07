package communication;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import helpers.Concurrency;
import main.NoteManager;

public class P2PCommunicator {
    private static final int PORT = 2003;
    private static final int BUFFER_SIZE = 1024;

    private final NoteManager noteManager;
    private final Consumer<String> onError;

    private boolean running = true;

    public P2PCommunicator(NoteManager noteManager, Consumer<String> onError) {
        this.noteManager = noteManager;
        this.onError = onError;
        Concurrency.runDaemon(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                while (running) {
                    Socket client = server.accept();
                    Concurrency.runDaemon(() -> serve(client));
                }
            } catch (IOException e) {
                onError.accept(e.getMessage());
            }
        });
    }

    public void requestDownloadAsync(String host, String noteId) {
        Concurrency.runDaemon(() -> requestDownload(host, noteId));
    }

    public void stop() {
        running = false;
    }

    private void serve(Socket s) {
        try (Socket socket = s) {
            MessageType res = readHeader(socket);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String content = dis.readUTF();

            switch (res) {
                case DOWNLOAD:
                    File file = noteManager.getNoteFile(content);
                    if (file == null)
                        send(MessageType.NOT_FOUND, "", socket);
                    else
                        sendFile(file, socket);
                    break;
                default:
                    send(MessageType.FAILURE, "", socket);
            }
        } catch (IOException | ClassNotFoundException | P2PCommunicationException e) {
            onError.accept(e.getMessage());
        }
    }

    private void requestDownload(String host, String noteId) {
        try (Socket socket = new Socket(host, PORT)) {
            send(MessageType.DOWNLOAD, noteId, socket);

            MessageType res = readHeader(socket);
            if (res != MessageType.FILE)
                throw new P2PCommunicationException("Failed to download note [%s] from %s", noteId,
                        host);

            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            try (FileOutputStream out = new FileOutputStream(noteManager.getDownloadDir())) {
                int bytesRead;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = bis.read(buffer)) > 0)
                    out.write(buffer);
            }
        } catch (ClassNotFoundException | IOException | P2PCommunicationException e) {
            onError.accept(e.getMessage());
        }
    }

    private void send(MessageType header, String s, Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(header);
        DataOutputStream das = new DataOutputStream(socket.getOutputStream());
        das.writeUTF(s);
        socket.getOutputStream().flush();
    }

    private void sendFile(File file, Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(MessageType.FILE);

        try (FileInputStream in = new FileInputStream(file)) {
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = in.read(buffer)) > 0) {
                socket.getOutputStream().write(buffer, 0, bytesRead);
            }
        }

        socket.getOutputStream().flush();
    }

    private MessageType readHeader(Socket socket)
            throws IOException, ClassNotFoundException, P2PCommunicationException {
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        MessageType res = (MessageType) ois.readObject();
        if (res == MessageType.FAILURE) {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            throw new P2PCommunicationException("FAILURE: " + dis.readUTF());
        }
        return res;
    }
}
