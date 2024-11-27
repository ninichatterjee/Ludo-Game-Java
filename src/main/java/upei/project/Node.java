package upei.project;

import java.awt.Color;

/**
 * Node class representing a position on the board.
 */
public class Node {
    private final int position;      // Position on board (0-51 for main track, 52+ for home stretches)
    private final int x;            // X coordinate for GUI
    private final int y;            // Y coordinate for GUI
    private Node next;              // Next node in the path
    private Node homePathNext;      // Next node in home path (if applicable)
    private final boolean isSafe;    // Whether this is a safe spot

    /**
     * Creates a new node with given position and coordinates
     */
    public Node(int position, int x, int y, boolean isSafe) {
        this.position = position;
        this.x = x;
        this.y = y;
        this.isSafe = isSafe;
    }

    /**
     * Gets the next node based on piece color
     */
    public Node getNext(Color pieceColor) {
        // If there's a home path and it's for this color's piece, use it
        if (homePathNext != null && shouldTakeHomePath(pieceColor)) {
            return homePathNext;
        }
        return next;
    }

    /**
     * Determines if a piece should take the home path
     */
    private boolean shouldTakeHomePath(Color pieceColor) {
        // Each color enters home path at different positions
        if (pieceColor == Color.BLUE && position == 50) return true;
        if (pieceColor == Color.GREEN && position == 11) return true;
        if (pieceColor == Color.YELLOW && position == 24) return true;
        if (pieceColor == Color.RED && position == 37) return true;
        return false;
    }

    // Getters and setters
    public int getPosition() { return position; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isSafe() { return isSafe; }
    public void setNext(Node next) { this.next = next; }
    public void setHomePathNext(Node homePathNext) { this.homePathNext = homePathNext; }

    public boolean isSafeSpot() {
        return isSafe;
    }

    public Node getNextForColor(Color color) {
        // Check if the home path should be taken for the given color
        if (homePathNext != null && isHomeEntry(color)) {
            return homePathNext;
        }
        // Otherwise, return the next node in the main path
        return next;
    }

    public boolean isHomeEntry(Color pieceColor) {
            if (pieceColor == Color.BLUE && position == 50) return true;
            if (pieceColor == Color.GREEN && position == 11) return true;
            if (pieceColor == Color.YELLOW && position == 24) return true;
            if (pieceColor == Color.RED && position == 37) return true;
            return false;
    }
}