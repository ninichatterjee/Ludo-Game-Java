package upei.project;

import java.awt.*;
import java.util.Random;
import java.util.List;

/**
 * AIPlayer class simulates AI strategy.
 */
public class AIPlayer extends Player {

    public AIPlayer(String name, Color color, List<Piece> pieces) {
        super(name, color, pieces);
    }

    @Override
    public void makeMove(int dieRoll, List<Player> allPlayers) {
        Piece bestPiece = null;

        // Strategy 1: Aggressive - send opponents back to their home
        for (Piece piece : pieces) {
            if (!piece.isAtHome()) {
                int targetPosition = piece.getPosition() + dieRoll;

                for (Player opponent : allPlayers) {
                    if (opponent != this) {
                        for (Piece opponentPiece : opponent.getPieces()) {
                            if (opponentPiece.getPosition() == targetPosition) {
                                bestPiece = piece;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Strategy 2: Defensive - Make safe moves
        if (bestPiece == null) {
            for (Piece piece : pieces) {
                if (!piece.isAtHome()) {
                    int targetPosition = piece.getPosition() + dieRoll;
                    boolean isSafe = true;

                    for (Player opponent : allPlayers) {
                        if (opponent != this) {
                            for (Piece opponentPiece : opponent.getPieces()) {
                                if (opponentPiece.getPosition() == targetPosition) {
                                    isSafe = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (isSafe) {
                        bestPiece = piece;
                        break;
                    }
                }
            }
        }

        // Fallback: Move the first available piece
        if (bestPiece == null) {
            for (Piece piece : pieces) {
                if (!piece.isAtHome()) {
                    bestPiece = piece;
                    break;
                }
            }
        }

        // Execute the move.
        if (bestPiece != null) {
            bestPiece.move(dieRoll);
        }
        else {
            System.out.println(name + " has no valid moves.");
        }
    }

    @Override
    public boolean isHuman() { return false; }
}
