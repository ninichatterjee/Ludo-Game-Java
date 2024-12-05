package upei.project.strategies;

import upei.project.MoveStrategy;
import upei.project.Node;
import upei.project.Piece;
import upei.project.Player;
import java.util.List;

/**
 * Implements an aggressive strategy for AI players in the Ludo game.
 * This strategy prioritizes:
 * - Capturing opponent pieces
 * - Getting pieces out of base
 * - Moving pieces towards home
 *
 * The strategy assigns higher scores to moves that:
 * 1. Can capture opponent pieces (highest priority)
 * 2. Get pieces out of base with a roll of 6
 * 3. Advance pieces closer to home
 *
 * Pieces that have already reached home are not considered for moves.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see MoveStrategy
 * @see Player
 */
public class AggressiveStrategy implements MoveStrategy {
    /**
     * Evaluates a potential move and returns a score based on aggressive criteria.
     * Higher scores indicate more desirable moves. The scoring system is:
     * - Base score is inverse of distance to home
     * - +100 points for moving out of base with a 6
     * - +200 points for potential captures
     * - -1 for pieces that have reached home
     *
     * @param piece The piece to be moved
     * @param dieRoll The number rolled on the die
     * @param currentPlayer The player making the move
     * @param allPlayers List of all players in the game
     * @return Score for the move, higher is better, -1 for invalid moves
     */
    @Override
    public double evaluateMove(Piece piece, int dieRoll, Player currentPlayer, List<Player> allPlayers) {
        if (piece.hasReachedHome()) return -1.0;
        
        // Base score from distance to home
        double score = piece.getDistanceFromHome();
        
        // High priority for getting pieces out of home
        if (piece.isAtHome() && dieRoll == 6) {
            score += 100.0;
        }
        
        // Extra points for potential captures
        Node targetNode = piece.simulateMove(dieRoll);
        if (targetNode != null) {
            for (Piece otherPiece : targetNode.getPieces()) {
                if (otherPiece.getColor() != piece.getColor()) {
                    score += 200.0;  // Aggressive bonus for captures
                }
            }
        }
        
        return score;
    }
}
