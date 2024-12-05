package upei.project.strategies;

import upei.project.MoveStrategy;
import upei.project.Node;
import upei.project.Piece;
import upei.project.Player;
import java.util.List;

/**
 * Implements a defensive strategy for AI players in the Ludo game.
 * This strategy prioritizes:
 * - Moving pieces to safe spots
 * - Avoiding capture by staying in safe positions
 * - Getting pieces out of base when safe
 * - Moving pieces towards home when protected
 *
 * The strategy assigns higher scores to moves that:
 * 1. Land on safe spots (highest priority)
 * 2. Get pieces out of base with a roll of 6
 * 3. Move pieces away from opponent threats
 * 4. Advance pieces closer to home when safe
 *
 * Pieces that have already reached home are not considered for moves.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see MoveStrategy
 * @see Player
 */
public class DefensiveStrategy implements MoveStrategy {
    /**
     * Evaluates a potential move and returns a score based on defensive criteria.
     * Higher scores indicate more desirable moves. The scoring system is:
     * - Base score is inverse of distance to home
     * - +50 points for moving out of base with a 6
     * - +200 points for reaching safe spots
     * - +50 points for capturing opponent pieces
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
        
        // Lower priority for getting pieces out of home
        if (piece.isAtHome() && dieRoll == 6) {
            score += 50.0;
        }
        
        // Evaluate target position
        var targetNode = piece.simulateMove(dieRoll);
        if (targetNode != null) {
            // High priority for safe spots
            if (targetNode.isSafe()) {
                score += 200.0;
            }
            
            // Lower bonus for captures compared to aggressive
            for (Piece otherPiece : targetNode.getPieces()) {
                if (otherPiece.getColor() != piece.getColor()) {
                    score += 50.0;
                }
            }
        }
        
        return score;
    }
}
