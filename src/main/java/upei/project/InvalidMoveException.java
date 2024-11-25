package upei.project;

/**
 * Exception thrown when a player makes an invalid move.
 */
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}
