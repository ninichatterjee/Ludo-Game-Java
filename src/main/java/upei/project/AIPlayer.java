package upei.project;

import java.awt.*;
import java.util.List;

/**
 * AIPlayer class implements intelligent move selection for the Ludo game.
 */
public class AIPlayer extends Player {

    public AIPlayer(String name, Color color, List<Piece> pieces) {
        super(name, color, pieces);
    }

    @Override
    public void makeMove(int dieRoll, List<Player> allPlayers) {
        if (!hasValidMoves(dieRoll)) {
            System.out.println(name + " has no valid moves.");
            return;
        }

        Piece bestPiece = null;
        int bestScore = Integer.MIN_VALUE;

        // Evaluate each piece's potential move
        for (Piece piece : pieces) {
            if (!isValidMove(piece, dieRoll)) {
                continue;
            }

            int currentScore = evaluateMove(piece, dieRoll, allPlayers);
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestPiece = piece;
            }
        }

        // Execute the best move found
        if (bestPiece != null) {
            int targetPosition = bestPiece.isAtHome() ? 0 : bestPiece.getPosition() + dieRoll;
            bestPiece.move(dieRoll);
            handleCapture(targetPosition, allPlayers);
        }
    }

    /**
     * Evaluates the score for moving a piece based on various factors.
     */
    private int evaluateMove(Piece piece, int dieRoll, List<Player> allPlayers) {
        int score = 0;
        int targetPosition = piece.isAtHome() ? 0 : piece.getPosition() + dieRoll;

        // Prioritize getting pieces out of home when rolling a 6
        if (piece.isAtHome() && dieRoll == 6) {
            score += 80;
        }

        // Bonus for getting closer to finish
        score += targetPosition;

        // Extra points for reaching safe spots
        if (isSafeSpot(targetPosition)) {
            score += 50;
        }

        // Points for capturing opponent pieces
        if (!isSafeSpot(targetPosition)) {
            for (Player opponent : allPlayers) {
                if (opponent != this) {
                    for (Piece opponentPiece : opponent.getPieces()) {
                        if (opponentPiece.getPosition() == targetPosition) {
                            score += 100;  // High priority for capturing
                        }
                    }
                }
            }
        }

        // Penalty for moving to a position where piece could be captured
        if (!isSafeSpot(targetPosition)) {
            for (Player opponent : allPlayers) {
                if (opponent != this) {
                    for (Piece opponentPiece : opponent.getPieces()) {
                        int opponentPos = opponentPiece.getPosition();
                        if (!opponentPiece.isAtHome() &&
                                Math.abs(opponentPos - targetPosition) <= 6) {
                            score -= 30;  // Penalty for being within opponent's reach
                        }
                    }
                }
            }
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
