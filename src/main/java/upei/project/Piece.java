package upei.project;

import java.awt.*;

public class Piece {
    private Node currentNode;        // Current node (null means in base)
    private final Color color;       // Color of the piece
    private boolean isHome;          // Whether piece has reached home

    public Piece(Color color) {
        this.color = color;
        this.currentNode = null;
        this.isHome = false;
    }

    /**
     * Moves the piece by specified number of steps
     * @return true if move was successful
     */
    public boolean move(int steps) {
        // Handle moving out of base
        if (currentNode == null) {
            if (steps == 6) {
                currentNode = getStartNode();
                return true;
            }
            return false;
        }

        // Follow the path for the given number of steps
        Node targetNode = currentNode;
        for (int i = 0; i < steps; i++) {
            if (targetNode == null) return false;
            targetNode = targetNode.getNext(color);
        }

        if (targetNode == null) return false;

        currentNode = targetNode;
        return true;
    }

    /**
     * Gets the starting node for this piece's color
     */
    private Node getStartNode() {
        // These positions correspond to the start positions for each color
        int position = 0; // Default for blue
        if (color == Color.GREEN) position = 13;
        else if (color == Color.YELLOW) position = 26;
        else if (color == Color.RED) position = 39;

        return BoardPanel.getNodeAtPosition(position);
    }

    public boolean isAtHome() {
        return currentNode == null;
    }

    public boolean hasReachedHome() {
        return isHome;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Color getColor() {
        return color;
    }

    public void sendToBase() {
        currentNode = null;
        isHome = false;
    }

    /**
     * Gets the current position for GUI display
     */
    public Point getDisplayPosition() {
        if (currentNode == null) return null;
        return new Point(currentNode.getX(), currentNode.getY());
    }

    public int getPosition() {
        return currentNode == null ? -1 : currentNode.getPosition();
    }

}
