package upei.project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a human player in the Ludo game.
 * This class handles all user interactions for making moves, including:
 * - Piece selection through dialog boxes
 * - Move validation with user feedback
 * - Error handling and display
 *
 * The human player's moves are validated in real-time with visual feedback,
 * and invalid moves are prevented with appropriate error messages.
 *
 * @author UPEI Project Team
 * @version 1.0
 */
public class HumanPlayer extends Player {
    /** The main game window for displaying dialogs */
    private final JFrame gameFrame;
    
    /** Validator for checking move legality */
    private MoveValidator moveValidator;

    /**
     * Creates a new human player with the specified attributes.
     *
     * @param name The player's name
     * @param color The player's color (affects piece appearance and home location)
     * @param board The game board panel
     * @param gameFrame The main game window for displaying dialogs
     */
    public HumanPlayer(String name, Color color, BoardPanel board, JFrame gameFrame) {
        super(name, color);
        this.gameFrame = gameFrame;
        
        // Initialize pieces
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pieces.add(new Piece(color, board));
        }
        this.pieces = pieces;
        
        // Set custom move validator for human player
        setMoveValidator((piece, steps) -> {
            String reason = getInvalidMoveReason(piece, steps);
            if (reason != null) {
                throw new InvalidMoveException(reason);
            }
        });
    }

    /**
     * Sets a custom move validator for this player.
     * The validator is used to check move legality before execution.
     *
     * @param moveValidator The validator to use for move checking
     */
    public void setMoveValidator(MoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    /**
     * Executes a move for the human player.
     * This method:
     * 1. Checks if any valid moves exist
     * 2. Prompts the user to select a piece
     * 3. Validates and executes the move
     * 4. Handles any resulting piece captures
     *
     * @param dieRoll The result of the dice roll
     * @param allPlayers List of all players in the game
     */
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
     * Validates a move with user feedback.
     * If the move is invalid, displays an error message to the user.
     *
     * @param piece The piece to move
     * @param steps Number of steps to move
     * @return true if the move is valid, false otherwise
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
     * Displays a dialog for the user to select a piece to move.
     * Shows only pieces that can make valid moves with the current roll.
     *
     * @param dieRoll The current dice roll value
     * @return The selected piece, or null if selection was cancelled
     */
    private Piece selectPiece(int dieRoll) {
        // Create array of piece descriptions
        List<String> options = new ArrayList<>();
        List<Piece> validPieces = new ArrayList<>();
        int pieceNumber = 1;
        
        for (Piece piece : pieces) {
            if (isValidMove(piece, dieRoll)) {
                String location;
                if (piece.isAtHome()) {
                    location = "in base";
                } else if (piece.hasReachedHome()) {
                    location = "at home";
                } else {
                    location = "at position " + piece.getCurrentNode().getPosition();
                }
                options.add("Piece " + pieceNumber + " (" + location + ")");
                validPieces.add(piece);
            }
            pieceNumber++;
        }

        if (options.isEmpty()) {
            return null;
        }

        Object selection = JOptionPane.showInputDialog(
            gameFrame,
            "Select a piece to move " + dieRoll + " steps",
            name + "'s Turn",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options.toArray(),
            options.get(0)
        );
        
        if (selection == null) {
            return null;
        }
        
        return validPieces.get(options.indexOf(selection));
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}
