package upei.project;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * HumanPlayer class handles user interaction for making moves in the Ludo game.
 */
public class HumanPlayer extends Player {
    private final JFrame gameFrame;
    private MoveValidator moveValidator;

    public HumanPlayer(String name, Color color, List<Piece> pieces, JFrame gameFrame) {
        super(name, color, pieces);
        this.gameFrame = gameFrame;
        
        // Set custom move validator for human player
        setMoveValidator((piece, steps) -> {
            String reason = getInvalidMoveReason(piece, steps);
            if (reason != null) {
                throw new InvalidMoveException(reason);
            }
        });
    }

    public void setMoveValidator(MoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    @Override
    public void makeMove(int dieRoll, List<Player> allPlayers) {
        if (!hasValidMoves(dieRoll)) {
            JOptionPane.showMessageDialog(gameFrame, 
                name + " has no valid moves with roll of " + dieRoll,
                "No Valid Moves",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        while (true) {
            Piece selectedPiece = selectPiece(dieRoll);
            if (selectedPiece == null) {
                // User cancelled move
                return;
            }

            try {
                moveValidator.validate(selectedPiece, dieRoll);
                selectedPiece.move(dieRoll);
                handleCapture(selectedPiece.getCurrentNode(), allPlayers);
                break;
            } catch (InvalidMoveException e) {
                JOptionPane.showMessageDialog(gameFrame,
                    "Invalid move: " + e.getMessage(),
                    "Invalid Move",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Validates a move for human player with user feedback
     */
    private boolean validateHumanMove(Piece piece, int steps) {
        String reason = getInvalidMoveReason(piece, steps);
        if (reason != null) {
            JOptionPane.showMessageDialog(gameFrame,
                "Invalid move: " + reason,
                "Invalid Move",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Prompts the user to select a piece to move
     */
    private Piece selectPiece(int dieRoll) {
        Object[] options = pieces.stream()
            .filter(piece -> isValidMove(piece, dieRoll))
            .toArray();

        if (options.length == 0) {
            return null;
        }

        return (Piece) JOptionPane.showInputDialog(
            gameFrame,
            "Select a piece to move " + dieRoll + " steps",
            name + "'s Turn",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}
