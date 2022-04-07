package communication;

public class P2PCommunicationException extends Exception {
    public P2PCommunicationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
