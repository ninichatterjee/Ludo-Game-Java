package upei.project;

import java.awt.Color;
import java.util.List;
import java.util.Optional;

/**
 * Abstract base class representing a player in the Ludo game.
 * This class provides the core functionality for both human and AI players,
 * including move validation, piece movement, and game state tracking.
 *
 * Players have:
 * - A name and color for identification
 * - A set of game pieces
 * - Move validation and strategy logic
 * - Game state tracking (pieces at home, on board, reached final home)
 *
 * The class uses the Strategy pattern for move validation and evaluation,
 * allowing different implementations for different player types.
 *
 * @author UPEI Project Team
 * @version 1.0
 */
public abstract class Player {
    /** Player's display name */
    protected String name;
    
    /** Player's color, determines their pieces and path */
    protected Color color;
    
    /** List of pieces owned by this player */
    protected List<Piece> pieces;
    
    /** Strategy for validating moves */
    protected MoveValidator moveValidator;
    
    /** Strategy for evaluating and selecting moves */
    protected MoveStrategy moveStrategy;
    
    /** Reference to all players in the game for interaction */
    private List<Player> allPlayers;

    /**
     * Constructs a player with basic attributes and default strategies.
     * Sets up the default move validator and strategy.
     *
     * @param name Player's display name
     * @param color Player's color (determines piece paths)
     */
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.moveValidator = Piece::validateMove;
        this.moveStrategy = (piece, dieRoll, player, players) -> evaluateMove(piece, dieRoll);
    }

    /**
     * Constructs a player with pieces and initializes strategies.
     *
     * @param name Player's display name
     * @param color Player's color
     * @param pieces List of pieces owned by this player
     */
    public Player(String name, Color color, List<Piece> pieces) {
        this(name, color);
        this.pieces = pieces;
    }

    /**
     * Default strategy for evaluating potential moves.
     * Prioritizes:
     * 1. Getting pieces out of base (with a roll of 6)
     * 2. Moving pieces closer to home
     * Ignores pieces that have already reached home.
     *
     * @param piece The piece to evaluate
     * @param dieRoll The current die roll
     * @return Score for the move (higher is better)
     */
    protected int evaluateMove(Piece piece, int dieRoll) {
        if (piece.isAtHome() && dieRoll == 6) return 100;  // Prioritize getting pieces out
        if (piece.hasReachedHome()) return -1;  // Don't move pieces that reached home
        return piece.getDistanceFromHome();  // Prefer pieces closer to home
    }

    /**
     * Sets a custom move validation strategy.
     * Allows for different validation rules in different game variants.
     *
     * @param validator The move validation strategy to use
     */
    protected void setMoveValidator(MoveValidator validator) {
        this.moveValidator = validator;
    }

    /**
     * Updates the reference to all players in the game.
     * Required for handling piece captures and player interaction.
     *
     * @param players List of all players in the game
     */
    public void setAllPlayers(List<Player> players) {
        this.allPlayers = players;
    }

    /**
     * Gets the list of all players in the game.
     *
     * @return List of all players
     */
    protected List<Player> getAllPlayers() {
        return allPlayers;
    }

    /**
     * Abstract method for making a move.
     * Must be implemented by concrete player classes (Human, AI).
     * This is the main entry point for player turns.
     *
     * @param dieRoll The result of the die roll
     * @param allPlayers List of all players for interaction
     */
    public abstract void makeMove(int dieRoll, List<Player> allPlayers);

    /**
     * Validates if a move is legal for a given piece and die roll.
     * Uses the current move validation strategy.
     *
     * @param piece The piece to move
     * @param dieRoll The number of spaces to move
     * @return true if the move is valid
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
     * Gets the reason why a move is invalid.
     * Useful for providing feedback in the UI.
     *
     * @param piece The piece attempting to move
     * @param dieRoll The number of spaces to move
     * @return Error message if move is invalid, null if valid
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
     * Finds the best piece to move based on the current strategy.
     * Evaluates all valid moves and selects the highest scoring one.
     *
     * @param dieRoll The current die roll
     * @param allPlayers List of all players for move evaluation
     * @return Optional containing the best piece to move, empty if no valid moves
     */
    protected Optional<Piece> findBestPiece(int dieRoll, List<Player> allPlayers) {
        setAllPlayers(allPlayers);
        return pieces.stream()
            .filter(piece -> isValidMove(piece, dieRoll))
            .max((p1, p2) -> Double.compare(
                moveStrategy.evaluateMove(p1, dieRoll, this, allPlayers),
                moveStrategy.evaluateMove(p2, dieRoll, this, allPlayers)
            ));
    }

    /**
     * Handles capturing opponent pieces at a target node.
     * Sends captured pieces back to their bases if the node isn't safe.
     *
     * @param targetNode The node where capture might occur
     * @param allPlayers List of all players to check for capturable pieces
     */
    protected void handleCapture(Node targetNode, List<Player> allPlayers) {
        if (targetNode == null || targetNode.isSafe()) return;

        // Find all pieces that can be captured at the target node
        allPlayers.stream()
            .filter(player -> player != this)
            .forEach(player -> player.getPieces().stream()
                .filter(piece -> !piece.isAtHome() && !piece.hasReachedHome() && 
                               piece.getCurrentNode() == targetNode)
                .forEach(Piece::sendToBase));
    }

    /**
     * Checks if the player has any valid moves available.
     *
     * @param dieRoll The current die roll
     * @return true if at least one valid move exists
     */
    public boolean hasValidMoves(int dieRoll) {
        return !pieces.isEmpty() && pieces.stream().anyMatch(piece -> isValidMove(piece, dieRoll));
    }

    /**
     * Gets the player's display name.
     *
     * @return Player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's color.
     *
     * @return Player's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the list of pieces owned by this player.
     *
     * @return List of player's pieces
     */
    public List<Piece> getPieces() {
        return pieces;
    }

    /**
     * Indicates if this is a human-controlled player.
     * Used to determine interaction mode (UI vs AI).
     *
     * @return true if player is human-controlled
     */
    public abstract boolean isHuman();

    /**
     * Checks if the player has won the game.
     * Victory condition: all pieces have reached home.
     *
     * @return true if all pieces have reached home
     */
    public boolean hasWon() {
        return !pieces.isEmpty() && pieces.stream().allMatch(Piece::hasReachedHome);
    }

    /**
     * Represents the result of a move attempt.
     * Contains success/failure status, capture information, and error messages.
     *
     * @param success Whether the move was successful
     * @param capture Whether the move resulted in capturing an opponent's piece
     * @param message Error message if move failed, null if successful
     */
    public record MoveResult(
        boolean success,
        boolean capture,
        String message
    ) {
        /**
         * Creates a successful move result.
         *
         * @param capture Whether a capture occurred
         * @return Successful move result
         */
        public static MoveResult success(boolean capture) {
            return new MoveResult(true, capture, null);
        }

        /**
         * Creates a failed move result with an error message.
         *
         * @param reason The reason for failure
         * @return Failed move result
         */
        public static MoveResult failure(String reason) {
            return new MoveResult(false, false, reason);
        }

        /**
         * Checks if the move was successful.
         *
         * @return true if move succeeded
         */
        public boolean wasSuccessful() {
            return success;
        }

        /**
         * Checks if the move resulted in a capture.
         *
         * @return true if an opponent's piece was captured
         */
        public boolean didCapture() {
            return capture;
        }

        /**
         * Gets the error message if move failed.
         *
         * @return Error message or null if move succeeded
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Attempts to make a move with the current die roll.
     * Handles the complete move process:
     * 1. Finding the best piece to move
     * 2. Validating the move
     * 3. Executing the move
     * 4. Handling any captures
     *
     * @param dieRoll The current die roll
     * @param allPlayers List of all players for interaction
     * @return MoveResult indicating success/failure and capture status
     */
    public MoveResult attemptMove(int dieRoll, List<Player> allPlayers) {
        Optional<Piece> bestPiece = findBestPiece(dieRoll, allPlayers);
        if (bestPiece.isEmpty()) {
            return MoveResult.failure("No valid moves available");
        }

        Piece piece = bestPiece.get();
        try {
            Node targetNode = piece.simulateMove(dieRoll);
            if (targetNode == null) {
                return MoveResult.failure("Invalid move: target position is null");
            }

            // Check if there are pieces that can be captured at the target node
            boolean canCapture = !targetNode.isSafe() && allPlayers.stream()
                .filter(p -> p != this)
                .flatMap(p -> p.getPieces().stream())
                .anyMatch(p -> !p.isAtHome() && !p.hasReachedHome() && 
                             p.getCurrentNode() == targetNode);

            piece.move(dieRoll);
            
            if (canCapture) {
                handleCapture(piece.getCurrentNode(), allPlayers);
            }
            
            return MoveResult.success(canCapture);
        } catch (InvalidMoveException e) {
            return MoveResult.failure(e.getMessage());
        }
    }

    /**
     * Record containing statistics about a player's game state.
     * Tracks the distribution of pieces across different states.
     *
     * @param piecesAtHome Number of pieces in starting base
     * @param piecesReachedHome Number of pieces that reached final home
     * @param piecesOnBoard Number of pieces actively on the board
     */
    public record GameStats(int piecesAtHome, int piecesReachedHome, int piecesOnBoard) {
    }

    /**
     * Gets current statistics about the player's game state.
     * Useful for AI decision making and UI display.
     *
     * @return GameStats containing current piece distribution
     */
    public GameStats getStats() {
        int piecesAtHome = (int) pieces.stream().filter(Piece::isAtHome).count();
        int piecesReachedHome = (int) pieces.stream().filter(Piece::hasReachedHome).count();
        int piecesOnBoard = pieces.size() - piecesAtHome - piecesReachedHome;
        
        return new GameStats(piecesAtHome, piecesReachedHome, piecesOnBoard);
    }
}
