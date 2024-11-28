package upei.project;

import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * AIPlayer implements a computer-controlled player with basic strategy
 */
public class AIPlayer extends Player {

    public AIPlayer(String name, Color color, List<Piece> pieces) {
        super(name, color, pieces);
    }

    @Override
    public void makeMove(int dieRoll, List<Player> allPlayers) {
        Optional<Piece> bestPiece = findBestPiece(dieRoll, allPlayers);
        
        if (bestPiece.isPresent()) {
            Piece piece = bestPiece.get();
            try {
                piece.move(dieRoll);
                handleCapture(piece.getCurrentNode(), allPlayers);
            } catch (InvalidMoveException e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        } else {
            System.out.println(name + " has no valid moves.");
        }
    }

    @Override
    protected int evaluateMove(Piece piece, int dieRoll) {
        int score = 0;

        // Base priorities from parent class
        if (piece.isAtHome() && dieRoll == 6) return 100;  // Highest priority: get pieces out
        if (piece.hasReachedHome()) return -1;  // Don't move pieces that reached home

        // Additional AI-specific strategy
        Node targetNode = piece.simulateMove(dieRoll);
        if (targetNode != null) {
            // Prioritize safe spots
            if (targetNode.isSafe()) {
                score += 50;
            }
            
            // Prioritize capturing opponent pieces
            for (Player opponent : getAllPlayers()) {
                if (opponent != this) {
                    for (Piece p : opponent.getPieces()) {
                        if (!p.isAtHome() && !p.hasReachedHome() && 
                            p.getCurrentNode() == targetNode) {
                            score += 75;  // High priority for captures
                            break;
                        }
                    }
                }
            }

            // Consider distance to home
            score += (100 - piece.getDistanceFromHome());  // Higher score for pieces closer to home
        }

        return score;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    public boolean hasValidMoves(int dieRoll) {
        for (Piece piece : pieces) {
            if (isValidMove(piece, dieRoll)) {
                return true;
            }
        }
        return false;
    }
}
