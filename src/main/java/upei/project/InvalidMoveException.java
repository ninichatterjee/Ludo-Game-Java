package upei.project;

/**
 * Exception thrown when a player attempts an invalid move in the Ludo game.
 * This exception is used to handle various invalid move scenarios such as:
 * - Moving a piece that is still in base with an invalid roll
 * - Attempting to move beyond the final home position
 * - Moving to an occupied safe spot
 * - Any other move that violates game rules
 *
 * @author UPEI Project Team
 * @version 1.0
 */
public class InvalidMoveException extends Exception {
    /**
     * Creates a new InvalidMoveException with a detailed message.
     *
     * @param message Description of why the move was invalid
     */
    public InvalidMoveException(String message) {
        super(message);
    }
}
