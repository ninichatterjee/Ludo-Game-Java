package upei.project;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an AI player in the Ludo game that uses different strategies to make moves.
 * This class extends the base Player class and implements AI-specific behavior for:
 * - Move evaluation and selection
 * - Strategy-based decision making
 * - Game state tracking
 *
 * The AI player can use different strategies (Aggressive, Defensive, Balanced) to:
 * - Evaluate potential moves
 * - Prioritize piece movement
 * - React to game situations
 *
 * Performance statistics are tracked including:
 * - Number of moves made
 * - Captures achieved
 * - Strategy effectiveness
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see Player
 * @see MoveStrategy
 */
public class AIPlayer extends Player {
    private final Random random = new Random();
    private int capturesMade = 0;
    private int movesCount = 0;
    private List<Player> allPlayers;
    private String strategy;
    private static final int MOVE_DELAY = 500;

    /**
     * Constructs an AI player with the given name, color, pieces, and strategy.
     *
     * @param name     the player's name
     * @param color    the player's color
     * @param pieces   the player's pieces
     * @param strategy the player's strategy (Aggressive, Defensive, Balanced)
     */
    public AIPlayer(String name, Color color, List<Piece> pieces, String strategy) {
        super(name, color);
        this.pieces = pieces;
        this.strategy = strategy;
    }

    /**
     * Constructs an AI player with the given name, color, and pieces, using the default Balanced strategy.
     *
     * @param name   the player's name
     * @param color  the player's color
     * @param pieces the player's pieces
     */
    public AIPlayer(String name, Color color, List<Piece> pieces) {
        this(name, color, pieces, "Balanced");
    }

    /**
     * Constructs an AI player with the given name, color, and board, using the default Balanced strategy.
     *
     * @param name  the player's name
     * @param color the player's color
     * @param board the game board
     */
    public AIPlayer(String name, Color color, BoardPanel board) {
        super(name, color);

        // Initialize pieces
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pieces.add(new Piece(color, board));
        }
        this.pieces = pieces;
        this.strategy = "Balanced"; // Default strategy
    }

    /**
     * Makes a move based on the given die roll and game state.
     *
     * @param dieRoll    the result of the die roll
     * @param allPlayers the list of all players in the game
     */
    @Override
    public void makeMove(int dieRoll, List<Player> allPlayers) {
        this.allPlayers = allPlayers;
        MoveResult result = attemptMove(dieRoll);
        if (result.wasSuccessful()) {
            movesCount++;
            if (result.didCapture()) {
                capturesMade++;
            }
        }
    }

    /**
     * Selects the best piece to move based on the given die roll and game state.
     *
     * @param dieRoll    the result of the die roll
     * @param allPlayers the list of all players in the game
     * @return the best piece to move, or null if no move is possible
     */
    private Piece selectBestMove(int dieRoll, List<Player> allPlayers) {
        Piece bestPiece = null;
        int bestScore = Integer.MIN_VALUE;

        for (Piece piece : pieces) {
            try {
                piece.validateMove(dieRoll);
                int score = evaluateMove(piece, dieRoll);
                if (score > bestScore) {
                    bestScore = score;
                    bestPiece = piece;
                }
            } catch (InvalidMoveException ignored) {
                // Skip invalid moves
            }
        }

        return bestPiece;
    }

    /**
     * Evaluates the potential move of a piece based on the given die roll and game state.
     *
     * @param piece     the piece to evaluate
     * @param dieRoll   the result of the die roll
     * @return the score of the potential move
     */
    @Override
    protected int evaluateMove(Piece piece, int dieRoll) {
        // Base case: always try to get pieces out with a 6
        if (piece.isAtHome() && dieRoll == 6) return 2000;

        if (piece.hasReachedHome()) return -1;

        Node targetNode = piece.simulateMove(dieRoll);
        if (targetNode == null) return -1;

        int score = 0;
        int strategyMultiplier = 1;

        // Count pieces already home
        int piecesHome = (int) pieces.stream().filter(Piece::hasReachedHome).count();

        // Apply strategy-specific multipliers
        switch (strategy) {
            case "Aggressive":
                strategyMultiplier = 2;
                break;
            case "Defensive":
                strategyMultiplier = piecesHome >= 2 ? 2 : 1;
                break;
            case "Balanced":
                strategyMultiplier = piecesHome >= 1 ? 2 : 1;
                break;
        }

        // Count pieces out of home
        int piecesOut = (int) pieces.stream()
                .filter(p -> !p.isAtHome() && !p.hasReachedHome())
                .count();

        // Strongly encourage getting pieces out early game
        if (piecesOut < 2 && !piece.isAtHome()) {
            score += 500;
        }

        // Prioritize moves that get closer to home
        score += (52 - piece.getDistanceFromHome()) * 20;

        // Extra points for getting very close to home
        if (piece.getDistanceFromHome() < 10) {
            score += 200;
        }

        // Bonus for landing on safe spots
        if (targetNode.isSafeSpot()) {
            score += 100;
        }

        // Strategy-specific scoring for captures
        if (!targetNode.isEmpty() && !targetNode.isSafeSpot()) {
            for (Piece otherPiece : targetNode.getPieces()) {
                if (otherPiece.getColor() != piece.getColor()) {
                    score += 200 * strategyMultiplier;
                }
            }
        }

        // Reduced penalty for moving into danger
        for (Player player : allPlayers) {
            if (player.getColor() != piece.getColor()) {
                for (Piece otherPiece : player.getPieces()) {
                    if (!otherPiece.isAtHome() && !otherPiece.hasReachedHome()) {
                        Node otherNode = otherPiece.getCurrentNode();
                        if (otherNode != null) {
                            int distance = getNodeDistance(otherNode, targetNode);
                            if (distance >= 1 && distance <= 6) {
                                score -= (7 - distance) * 10;
                            }
                        }
                    }
                }
            }
        }

        // Extra bonus for moves that would win the game
        if (piece.getDistanceFromHome() <= dieRoll) {
            score += 1000;
        }

        return score;
    }

    /**
     * Calculates the distance between two nodes on the board.
     *
     * @param start the starting node
     * @param end   the ending node
     * @return the distance between the nodes, or -1 if the nodes are not connected
     */
    private int getNodeDistance(Node start, Node end) {
        int distance = 0;
        Node current = start;
        while (current != null && current != end && distance < 52) {
            current = current.getNext(color);
            distance++;
        }
        return current == end ? distance : -1;
    }

    /**
     * Attempts to make a move based on the given die roll and game state.
     *
     * @param dieRoll the result of the die roll
     * @return the result of the move attempt
     */
    private MoveResult attemptMove(int dieRoll) {
        Piece bestPiece = selectBestMove(dieRoll, allPlayers);
        if (bestPiece != null) {
            try {
                final Piece finalBestPiece = bestPiece;
                Node targetNode = finalBestPiece.simulateMove(dieRoll);
                boolean willCapture = !targetNode.isEmpty() &&
                        targetNode.getPieces().stream()
                                .anyMatch(p -> p.getColor() != finalBestPiece.getColor());
                finalBestPiece.move(dieRoll);
                return new MoveResult(true, willCapture);
            } catch (InvalidMoveException e) {
                return new MoveResult(false, false);
            }
        }

        return new MoveResult(false, false);
    }

    /**
     * Adds a delay between moves to simulate human-like gameplay.
     */
    private void addMoveDelay() {
        try {
            Thread.sleep(MOVE_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns whether this player is human or not.
     *
     * @return false, since this is an AI player
     */
    @Override
    public boolean isHuman() {
        return false;
    }

    /**
     * Returns the number of captures made by this player.
     *
     * @return the number of captures made
     */
    public int getCapturesMade() {
        return capturesMade;
    }

    /**
     * Returns the number of moves made by this player.
     *
     * @return the number of moves made
     */
    public int getMovesCount() {
        return movesCount;
    }

    /**
     * Represents the result of a move attempt.
     */
    private static class MoveResult {
        private final boolean success;
        private final boolean captured;

        MoveResult(boolean success, boolean captured) {
            this.success = success;
            this.captured = captured;
        }

        /**
         * Returns whether the move was successful.
         *
         * @return true if the move was successful, false otherwise
         */
        boolean wasSuccessful() {
            return success;
        }

        /**
         * Returns whether a capture was made during the move.
         *
         * @return true if a capture was made, false otherwise
         */
        boolean didCapture() {
            return captured;
        }
    }
}
