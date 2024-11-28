package upei.project;

/**
 * Interface for validating moves in the Ludo game.
 * Implementations should check if a move is valid and throw InvalidMoveException if not.
 */
@FunctionalInterface
public interface MoveValidator {
    /**
     * Validates if a move is valid for a given piece and number of steps
     * @param piece The piece to move
     * @param steps Number of steps to move
     * @throws InvalidMoveException if the move is invalid
     */
    void validate(Piece piece, int steps) throws InvalidMoveException;
}
