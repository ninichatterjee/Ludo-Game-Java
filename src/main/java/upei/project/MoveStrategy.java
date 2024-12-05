package upei.project;

import java.util.List;

/**
 * Strategy interface for evaluating and selecting moves in the Ludo game.
 */
@FunctionalInterface
public interface MoveStrategy {
    /**
     * Evaluates a potential move and returns a score indicating its desirability.
     * Higher scores indicate more desirable moves according to the strategy.
     *
     * @param piece The piece to be moved
     * @param steps Number of steps to move
     * @param currentPlayer The player making the move
     * @param allPlayers List of all players in the game
     * @return A score indicating how desirable this move is (higher is better)
     */
    double evaluateMove(Piece piece, int steps, Player currentPlayer, List<Player> allPlayers);
}
