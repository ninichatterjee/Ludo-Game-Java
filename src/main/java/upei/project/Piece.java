package upei.project;

import java.awt.*;
import java.util.List;

/**
 * Represents a game piece in the Ludo board game.
 * Each piece belongs to a player and is identified by its color.
 * Pieces can move around the board, capture other pieces, and try to reach home.
 * The piece keeps track of its current position, whether it's in base, on the board,
 * or has reached home.
 *
 * @author UPEI Project Team
 * @version 1.0
 */
public class Piece {
    /** Current node where the piece is located (null means in base) */
    private Node currentNode;
    
    /** Color of the piece, determines its path and ownership */
    private final Color color;
    
    /** Flag indicating if the piece has reached home */
    private boolean isHome;
    
    /** Total number of nodes in the main track */
    private static final int BOARD_SIZE = 52;
    
    /** Reference to the game board for position calculations */
    private BoardPanel board;

    /**
     * Constructs a new Piece with specified color and board reference.
     * Initially, the piece starts in its base (currentNode = null).
     *
     * @param color The color of the piece (BLUE, GREEN, YELLOW, or RED)
     * @param board Reference to the game board
     */
    public Piece(Color color, BoardPanel board) {
        this.color = color;
        this.board = board;
        this.currentNode = null;
        this.isHome = false;
    }

    /**
     * Calculates the distance from this piece to its home position.
     * This is used for move evaluation and AI strategy.
     *
     * @return int representing distance:
     *         -1 if piece is already home
     *         Integer.MAX_VALUE if piece is in base
     *         Otherwise, returns the number of steps needed to reach home
     */
    public int getDistanceFromHome() {
        // If piece is marked as home or is at the end of home stretch, return -1
        if (isHome) {
            return -1;
        }
        
        // If piece is in base, return MAX_VALUE
        if (currentNode == null) {
            return Integer.MAX_VALUE;
        }
        
        // If piece is in home stretch, count remaining steps to home
        if (currentNode.getPosition() >= 300) {
            int steps = 0;
            Node current = currentNode;
            while (current.getNext(color) != null) {
                steps++;
                current = current.getNext(color);
            }
            return steps;
        }
        
        // Otherwise calculate distance to home entry point
        int homePosition;
        if (color == Color.BLUE) homePosition = 50;
        else if (color == Color.GREEN) homePosition = 11;
        else if (color == Color.YELLOW) homePosition = 24;
        else if (color == Color.RED) homePosition = 37;
        else return Integer.MAX_VALUE;

        int currentPos = currentNode.getPosition();
        if (currentPos <= homePosition) {
            return homePosition - currentPos;
        } else {
            return BOARD_SIZE - currentPos + homePosition;
        }
    }

    /**
     * Validates if a proposed move is legal according to Ludo rules.
     * Checks various conditions including:
     * - Valid number of steps
     * - Requirements for moving out of base (needs a 6)
     * - Prevention of moving backwards on main track
     * - Valid movement within home stretch
     *
     * @param steps Number of steps to move
     * @throws InvalidMoveException if the move violates any game rules
     */
    void validateMove(int steps) throws InvalidMoveException {
        if (steps < 1) {
            throw new InvalidMoveException("Invalid number of steps");
        }

        // If in base, can only move out with a 6
        if (currentNode == null && steps != 6) {
            throw new InvalidMoveException("Need a 6 to move out of base");
        }

        // Simulate the move to check if it's valid
        Node targetNode = simulateMove(steps);
        
        // A null target is valid if we're in the home stretch
        if (targetNode == null && (currentNode == null || currentNode.getPosition() < 300)) {
            throw new InvalidMoveException("Invalid move: would go beyond board");
        }

        // Check if move would go backwards (only on main track)
        if (targetNode != null && currentNode != null && 
            targetNode.getPosition() < currentNode.getPosition() && 
            !currentNode.isHomeEntry(color) && 
            currentNode.getPosition() < 52) {
            throw new InvalidMoveException("Cannot move backwards");
        }
    }

    /**
     * Simulates a move without actually moving the piece.
     * Used for move validation and AI move evaluation.
     * Handles special cases like:
     * - Moving out of base
     * - Board wraparound
     * - Home stretch entry
     * - Backwards movement prevention
     *
     * @param steps Number of steps to simulate
     * @return Node that would be reached, or null if move is invalid
     */
    public Node simulateMove(int steps) {
        if (currentNode == null) {
            if (steps == 6) {
                // Get starting node based on color
                int startPosition = 
                    color == Color.BLUE ? 0 :
                    color == Color.GREEN ? 13 :
                    color == Color.YELLOW ? 26 :
                    color == Color.RED ? 39 : -1;
                
                if (startPosition != -1) {
                    return board.getNodeAtPosition(startPosition);
                }
                return null;
            }
            return null;
        }

        // Move forward the required steps - pieces can jump over others
        Node current = currentNode;
        for (int i = 0; i < steps; i++) {
            Node next = current.getNext(color);
            if (next == null) {
                return null;
            }
            
            // Handle board wraparound for main track
            // If we're on main track (position < 52) and next node is a home stretch node (position >= 300)
            // and we're not at our home entry point, wrap around to position 0
            if (current.getPosition() < 52 && next.getPosition() >= 300 && !current.isHomeEntry(color)) {
                next = board.getNodeAtPosition(0);
            }
            
            // If we're at our home entry point, take the home path
            if (current.isHomeEntry(color)) {
                next = current.getNext(color);  // This will automatically use home path if appropriate
                if (next == null) {
                    return null;  // Can't move further
                }
            }
            
            // Prevent moving backwards on main track (not in home path)
            if (current.getPosition() < 52 && next.getPosition() < current.getPosition() && !current.isHomeEntry(color)) {
                return null;
            }
            
            current = next;
        }
        return current;
    }

    /**
     * Executes a move on the board, updating the piece's position.
     * Handles all special cases including:
     * - Moving out of base
     * - Capturing opponent pieces
     * - Entering home stretch
     * - Reaching home
     *
     * @param steps Number of steps to move
     * @throws InvalidMoveException if the move is not valid
     */
    public void move(int steps) throws InvalidMoveException {
        validateMove(steps);
        
        // Remove from current node if on board
        if (currentNode != null) {
            currentNode.removePiece(this);
        }

        if (currentNode == null && steps == 6) {
            // Moving out of base
            int startPosition = 
                color == Color.BLUE ? 0 :
                color == Color.GREEN ? 13 :
                color == Color.YELLOW ? 26 :
                color == Color.RED ? 39 : -1;
            
            if (startPosition != -1) {
                Node startNode = board.getNodeAtPosition(startPosition);
                if (startNode != null) {
                    // Handle any opponent pieces at the start position
                    handleOpponentPieces(startNode);
                    currentNode = startNode;
                    currentNode.addPiece(this);
                } else {
                    throw new InvalidMoveException("Invalid start position");
                }
            }
            return;
        }

        // Regular move
        Node targetNode = simulateMove(steps);
        if (targetNode == null) {
            // If we're in the home stretch and can't move further, we've reached home
            if (currentNode != null && currentNode.getPosition() >= 300) {
                isHome = true;
                currentNode = null;  // Remove from board
            } else {
                throw new InvalidMoveException("Invalid move: target position is null");
            }
            return;
        }

        // If we're at the last node in home stretch and have exact steps to reach home
        if (targetNode.getPosition() >= 300 && targetNode.getNext(color) == null) {
            isHome = true;
            currentNode = null;  // Remove from board
            return;
        }

        // Handle any opponent pieces at the target position
        handleOpponentPieces(targetNode);
        currentNode = targetNode;
        currentNode.addPiece(this);
    }

    /**
     * Handles interaction with opponent pieces at a target node.
     * If the target is not a safe spot, all opponent pieces are sent back to their base.
     *
     * @param node The target node where opponent pieces might be present
     */
    private void handleOpponentPieces(Node node) {
        if (!node.isSafeSpot()) {  // Only capture on non-safe spots
            List<Piece> opponentPieces = node.getPieces().stream()
                .filter(p -> p.getColor() != this.color)
                .toList();
            
            // Send all opponent pieces back to base
            for (Piece opponent : opponentPieces) {
                opponent.sendToBase();
            }
        }
    }

    /**
     * Returns the piece to its starting base position.
     * This happens when the piece is captured by an opponent.
     * Removes the piece from its current node and resets home status.
     */
    public void sendToBase() {
        if (currentNode != null) {
            currentNode.removePiece(this);
            currentNode = null;
        }
        isHome = false;
    }

    /**
     * Gets the color of this piece.
     *
     * @return Color of the piece (BLUE, GREEN, YELLOW, or RED)
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the current node where the piece is located.
     *
     * @return Current node, or null if piece is in base or home
     */
    public Node getCurrentNode() {
        return currentNode;
    }

    /**
     * Checks if the piece is currently in its starting base.
     *
     * @return true if piece is in base (not on board and not home), false otherwise
     */
    public boolean isAtHome() {
        return currentNode == null && !isHome;
    }

    /**
     * Checks if the piece has reached its final home position.
     *
     * @return true if piece has reached home, false otherwise
     */
    public boolean hasReachedHome() {
        return isHome;
    }

    /**
     * Simulates the distance from home after a potential move.
     * Used for AI move evaluation to determine the best moves.
     *
     * @param steps Number of steps to simulate moving
     * @return int representing simulated distance:
     *         -1 if piece would reach home
     *         Current distance if move is invalid
     *         Otherwise, returns the simulated distance to home
     */
    public int simulateDistanceFromHome(int steps) {
        if (isHome) return -1;
        
        Node targetNode = simulateMove(steps);
        if (targetNode == null) return getDistanceFromHome();

        // Check if piece would reach home
        int homePosition = 
            color == Color.BLUE ? 50 :
            color == Color.GREEN ? 11 :
            color == Color.YELLOW ? 24 :
            color == Color.RED ? 37 : -1;

        if (targetNode.getPosition() == homePosition) {
            return -1;
        }

        // Calculate distance from simulated position
        if (targetNode.getPosition() <= homePosition) {
            return homePosition - targetNode.getPosition();
        } else {
            return BOARD_SIZE - targetNode.getPosition() + homePosition;
        }
    }
}
