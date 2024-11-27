package upei.project;

import java.awt.*;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * HumanPlayer class allowing human interaction for moves.
 */
public class HumanPlayer extends Player {

    public HumanPlayer(String name, Color color, List<Piece> pieces) {
        super(name, color, pieces);
    }

    @Override
    public void makeMove(int dieRoll, List<Player> allPlayers) {
        // First check if any valid moves are available
        if (!hasValidMoves(dieRoll)) {
            JOptionPane.showMessageDialog(null,
                    "No valid moves available with roll of " + dieRoll);
            return;
        }

        Piece chosenPiece = null;
        while (chosenPiece == null) {
            // Build status message
            StringBuilder message = new StringBuilder();
            message.append("Die roll: ").append(dieRoll).append("\n\n");
            message.append("Current piece positions:\n");
            for (int i = 0; i < pieces.size(); i++) {
                Piece piece = pieces.get(i);
                message.append("Piece ").append(i + 1).append(": ");
                if (piece.isAtHome()) {
                    message.append("At Home");
                } else {
                    message.append("Position ").append(piece.getPosition());
                }
                if (!isValidMove(piece, dieRoll)) {
                    message.append(" (Cannot move)");
                }
                message.append("\n");
            }
            message.append("\nEnter piece number (1-").append(pieces.size()).append("):");

            String input = JOptionPane.showInputDialog(null, message.toString());

            if (input == null) {  // User clicked Cancel
                continue;
            }

            try {
                int pieceIndex = Integer.parseInt(input) - 1;
                if (pieceIndex >= 0 && pieceIndex < pieces.size()) {
                    Piece selectedPiece = pieces.get(pieceIndex);
                    if (isValidMove(selectedPiece, dieRoll)) {
                        chosenPiece = selectedPiece;
                    } else {
                        String reason;
                        if (selectedPiece.isAtHome() && dieRoll != 6) {
                            reason = "You need a 6 to move a piece out of home.";
                        } else {
                            reason = "This move would exceed the board limits.";
                        }
                        JOptionPane.showMessageDialog(null,
                                "Invalid move! " + reason);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Invalid piece number. Please enter a number between 1 and " + pieces.size());
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid input. Please enter a number!");
            }
        }

        // Make the move
        int targetPosition = chosenPiece.isAtHome() ? 0 : chosenPiece.getPosition() + dieRoll;
        chosenPiece.move(dieRoll);

        // Handle captures
        handleCapture(targetPosition, allPlayers);
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}
