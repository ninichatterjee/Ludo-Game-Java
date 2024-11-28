package upei.project;

import java.awt.*;

public class Piece {
    private Node currentNode;        // Current node (null means in base)
    private final Color color;       // Color of the piece
    private boolean isHome;          // Whether piece has reached home
    private static final int BOARD_SIZE = 52;  // Total nodes in main track
    private BoardPanel board;        // Reference to the game board

    public Piece(Color color, BoardPanel board) {
        this.color = color;
        this.board = board;
        this.currentNode = null;
        this.isHome = false;
    }

    /**
     * Gets the distance from home (used for move evaluation)
     * Returns:
     * -1 if piece is already home
     * Integer.MAX_VALUE if piece is in base
     * Otherwise, returns the number of steps needed to reach home
     */
    public int getDistanceFromHome() {
        if (isHome) return -1;
        if (currentNode == null) return Integer.MAX_VALUE;
        
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
     * Validates if a move is possible without actually moving
     * @throws InvalidMoveException if the move is not valid
     */
    public void validateMove(int steps) throws InvalidMoveException {
        // Handle moving out of base
        if (currentNode == null) {
            if (steps != 6) {
                throw new InvalidMoveException("Need a 6 to move piece out of base");
            }
            return;
        }

        // Check if piece has already reached home
        if (isHome) {
            throw new InvalidMoveException("Piece has already reached home");
        }

        // Simulate the move
        Node targetNode = simulateMove(steps);
        if (targetNode == null) {
            throw new InvalidMoveException("Move would exceed board limits");
        }
    }

    /**
     * Simulates a move without actually moving the piece
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

        Node current = currentNode;
        for (int i = 0; i < steps; i++) {
            current = current.getNext(color);
            if (current == null) return null;
        }
        return current;
    }

    /**
     * Moves the piece on the board
     */
    public void move(int steps) throws InvalidMoveException {
        validateMove(steps);
        
        if (currentNode == null && steps == 6) {
            // Moving out of base
            int startPosition = 
                color == Color.BLUE ? 0 :
                color == Color.GREEN ? 13 :
                color == Color.YELLOW ? 26 :
                color == Color.RED ? 39 : -1;
            
            if (startPosition != -1) {
                currentNode = board.getNodeAtPosition(startPosition);
            }
            return;
        }

        // Regular move
        Node targetNode = simulateMove(steps);
        if (targetNode == null) {
            throw new InvalidMoveException("Invalid move: target position is null");
        }
        currentNode = targetNode;
    }

    /**
     * Sends the piece back to its starting base
     */
    public void sendToBase() {
        currentNode = null;
        isHome = false;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public boolean isAtHome() {
        return currentNode == null && !isHome;
    }

    public boolean hasReachedHome() {
        return isHome;
    }
}
