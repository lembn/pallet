package communication;

public class CommunicationException extends Exception {
    public CommunicationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
