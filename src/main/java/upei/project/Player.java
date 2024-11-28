package upei.project;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import upei.project.MoveValidator; // Add MoveValidator import

/**
 * Abstract Player class representing a generic player in the game.
 */
public abstract class Player {
    protected String name;
    protected Color color;
    protected List<Piece> pieces;
    protected MoveValidator moveValidator; // Change access modifier to protected
    protected BiFunction<Piece, Integer, Integer> moveStrategy;
    private List<Player> allPlayers;

    /**
     * Constructs a Player with a name and color.
     */
    public Player(String name, Color color, List<Piece> pieces) {
        this.name = name;
        this.color = color;
        this.pieces = pieces;
        this.moveValidator = (piece, steps) -> piece.validateMove(steps);
        this.moveStrategy = (piece, dieRoll) -> evaluateMove(piece, dieRoll); // Default strategy
    }

    /**
     * Default move evaluation strategy
     */
    protected int evaluateMove(Piece piece, int dieRoll) {
        if (piece.isAtHome() && dieRoll == 6) return 100;  // Prioritize getting pieces out
        if (piece.hasReachedHome()) return -1;  // Don't move pieces that reached home
        return piece.getDistanceFromHome();  // Prefer pieces closer to home
    }

    /**
     * Sets the move validation strategy
     */
    protected void setMoveValidator(MoveValidator validator) {
        this.moveValidator = validator;
    }

    /**
     * Sets the list of all players in the game
     */
    public void setAllPlayers(List<Player> players) {
        this.allPlayers = players;
    }

    /**
     * Gets the list of all players
     */
    protected List<Player> getAllPlayers() {
        return allPlayers;
    }

    /**
     * Determines the next move for the player.
     */
    public abstract void makeMove(int dieRoll, List<Player> allPlayers);

    /**
     * Checks if a move is valid for a given piece and die roll.
     */
    protected boolean isValidMove(Piece piece, int dieRoll) {
        try {
            moveValidator.validate(piece, dieRoll);
            return true;
        } catch (InvalidMoveException e) {
            return false;
        }
    }

    /**
     * Gets the reason why a move is invalid
     */
    protected String getInvalidMoveReason(Piece piece, int dieRoll) {
        try {
            moveValidator.validate(piece, dieRoll);
            return null;
        } catch (InvalidMoveException e) {
            return e.getMessage();
        }
    }

    /**
     * Finds the best piece to move based on the current strategy
     */
    protected Optional<Piece> findBestPiece(int dieRoll, List<Player> allPlayers) {
        setAllPlayers(allPlayers);
        return pieces.stream()
            .filter(piece -> isValidMove(piece, dieRoll))
            .max((p1, p2) -> Integer.compare(
                evaluateMove(p1, dieRoll),
                evaluateMove(p2, dieRoll)
            ));
    }

    /**
     * Handles piece capture at a specific node
     */
    protected void handleCapture(Node targetNode, List<Player> allPlayers) {
        if (targetNode == null || targetNode.isSafe()) return;

        allPlayers.stream()
            .filter(player -> player != this)
            .forEach(player -> player.getPieces().stream()
                .filter(piece -> !piece.isAtHome() && !piece.hasReachedHome() && 
                               piece.getCurrentNode() == targetNode)
                .forEach(Piece::sendToBase));
    }

    /**
     * Checks if any valid moves are available
     */
    public boolean hasValidMoves(int dieRoll) {
        return pieces.stream().anyMatch(piece -> isValidMove(piece, dieRoll));
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    /**
     * Returns true if the player is human.
     */
    public abstract boolean isHuman();

    /**
     * Checks if the player has won
     */
    public boolean hasWon() {
        return pieces.stream().allMatch(Piece::hasReachedHome);
    }
}
