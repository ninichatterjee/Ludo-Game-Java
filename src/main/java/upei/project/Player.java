package upei.project;

import java.awt.*;
import java.util.List;

/**
 * Abstract Player class representing a generic player in the game.
 */
public abstract class Player {
    protected static final int BOARD_SIZE = 52;  // Standard Ludo board size
    protected static final int[] SAFE_SPOTS = {0, 8, 13, 21, 26, 34, 39, 47};  // Safe spots on board

    protected String name;
    protected Color color;
    protected List<Piece> pieces;

    /**
     * Constructs a Player with a name and color.
     *
     * @param name  Name of the player.
     * @param color Color of the player.
     * @param pieces List of player's pieces
     */
    public Player(String name, Color color, List<Piece> pieces) {
        this.name = name;
        this.color = color;
        this.pieces = pieces;
    }

    /**
     * Determines the next move for the player.
     * @param dieRoll The result of the die roll.
     * @param allPlayers All players in the game for strategic decision-making.
     */
    public abstract void makeMove(int dieRoll, List<Player> allPlayers);

    /**
     * Checks if a move is valid for a given piece and die roll.
     * @param piece The piece to move
     * @param dieRoll The die roll value
     * @return true if the move is valid, false otherwise
     */
    protected boolean isValidMove(Piece piece, int dieRoll) {
        // Can't move if piece is at home and didn't roll 6
        if (piece.isAtHome() && dieRoll != 6) {
            return false;
        }

        // If piece is at base and rolled 6, it's a valid move
        if (piece.isAtHome() && dieRoll == 6) {
            return true;
        }

        // Check if piece has already reached home
        if (piece.hasReachedHome()) {
            return false;
        }

        // Simulate the move to check if it's valid
        Node currentNode = piece.getCurrentNode();
        for (int i = 0; i < dieRoll; i++) {
            if (currentNode == null) {
                return false;
            }
            currentNode = currentNode.getNextForColor(color);
        }

        return currentNode != null;
    }

    /**
     * Handles piece capture at a specific node
     * @param node The node to check for captures
     * @param allPlayers List of all players
     */
    protected void handleCapture(Node node, List<Player> allPlayers) {
        if (node == null || node.isSafeSpot()) return;

        for (Player otherPlayer : allPlayers) {
            if (otherPlayer == this) continue;

            for (Piece otherPiece : otherPlayer.getPieces()) {
                if (!otherPiece.isAtHome() && !otherPiece.hasReachedHome() &&
                        otherPiece.getCurrentNode() == node) {
                    otherPiece.sendToBase();
                }
            }
        }
    }

    /**
     * Gets the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the player's pieces
     */
    public List<Piece> getPieces() {
        return pieces;
    }

    /**
     * Checks if the player has won
     */
    public boolean hasWon() {
        return pieces.stream().allMatch(Piece::hasReachedHome);
    }

    /**
     * Returns true if the player is human.
     */
    public abstract boolean isHuman();
}
