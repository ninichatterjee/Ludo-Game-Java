package upei.project;

import java.util.List;

/**
 * Strategy interface for evaluating and selecting moves
 */
@FunctionalInterface
public interface MoveStrategy {
    /**
     * Evaluates a potential move and returns a score
     * @param piece The piece to evaluate
     * @param dieRoll The die roll value
     * @param currentPlayer The player making the move
     * @param allPlayers All players in the game
     * @return Score for this move (higher is better)
     */
    int evaluateMove(Piece piece, int dieRoll, Player currentPlayer, List<Player> allPlayers);
}
