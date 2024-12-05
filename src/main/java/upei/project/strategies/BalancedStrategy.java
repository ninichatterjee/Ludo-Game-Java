package upei.project.strategies;

import upei.project.MoveStrategy;
import upei.project.Node;
import upei.project.Piece;
import upei.project.Player;
import java.util.List;

/**
 * Implements a balanced strategy for AI players in the Ludo game.
 * This strategy combines both offensive and defensive elements:
 * - Opportunistic captures when safe
 * - Strategic use of safe spots
 * - Careful piece advancement
 * - Risk assessment for each move
 *
 * The strategy assigns balanced scores to moves that:
 * 1. Get pieces out of base with a roll of 6
 * 2. Land on safe spots (moderate priority)
 * 3. Capture opponents when advantageous
 * 4. Advance pieces while maintaining safety
 *
 * This strategy aims to provide a well-rounded playing style
 * that adapts to different game situations.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see MoveStrategy
 * @see Player
 * @see AggressiveStrategy
 * @see DefensiveStrategy
 */
public class BalancedStrategy implements MoveStrategy {
    /**
     * Evaluates a potential move using a balanced scoring system.
     * Higher scores indicate more desirable moves. The scoring system is:
     * - Base score is inverse of distance to home
     * - +150 points for moving out of base with a 6
     * - +100 points for captures
     * - +50 points for reaching safe spots
     * - -1 for pieces that have reached home
     *
     * @param piece The piece to be moved
     * @param dieRoll The number rolled on the die
     * @param currentPlayer The player making the move
     * @param allPlayers List of all players in the game
     * @return Score for the move, higher is better, -1.0 for invalid moves
     */
    @Override
    public double evaluateMove(Piece piece, int dieRoll, Player currentPlayer, List<Player> allPlayers) {
        if (piece.hasReachedHome()) return -1.0;
        
        // Base score from distance to home
        double score = piece.getDistanceFromHome();
        
        // Balanced priority for getting pieces out of home
        if (piece.isAtHome() && dieRoll == 6) {
            score += 150.0;
        }
        
        // Points for potential captures, but not as high as aggressive
        Node targetNode = piece.simulateMove(dieRoll);
        if (targetNode != null) {
            for (Piece otherPiece : targetNode.getPieces()) {
                if (otherPiece.getColor() != piece.getColor()) {
                    score += 100.0;  // Moderate bonus for captures
                }
            }
            
            // Extra points for safe spots
            if (targetNode.isSafe()) {
                score += 50.0;
            }
        }
        
        return score;
    }
}
