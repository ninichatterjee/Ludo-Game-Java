package upei.project;

/**
 * Interface for validating moves in the Ludo game.
 * Implementations validate moves according to Ludo rules, including:
 * - Basic movement rules
 * - Piece starting conditions
 * - Safe spot rules
 * - Home stretch rules
 * - Capture rules
 *
 * The validator throws InvalidMoveException with a descriptive message
 * when a move violates any game rules.
 *
 * This interface is designed to be implemented using lambda expressions
 * for flexible validation strategies.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see InvalidMoveException
 */
@FunctionalInterface
public interface MoveValidator {
    /**
     * Validates if a move is legal according to Ludo rules.
     * 
     * @param piece The piece to be moved
     * @param steps Number of steps to move
     * @throws InvalidMoveException if the move is not valid, with a message explaining why
     */
    void validate(Piece piece, int steps) throws InvalidMoveException;
}
