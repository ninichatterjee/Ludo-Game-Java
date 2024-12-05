package upei.project;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node (space) on the Ludo game board.
 * Each node maintains information about:
 * - Its position on the board
 * - Coordinates for visual representation
 * - Connected nodes (next in main path and home path)
 * - Pieces currently occupying the space
 * - Special properties (safe spots, home entries)
 *
 * The node system forms the backbone of the game board by:
 * - Creating the main circular path
 * - Connecting to home paths for each color
 * - Managing piece movement and captures
 * - Enforcing game rules
 *
 * @author UPEI Project Team
 * @version 1.0
 */
public class Node {
    private final int position;
    private final int x;
    private final int y;
    private Node next;
    private Node homePathNext;
    private final List<Piece> pieces;
    private final boolean safe;

    /** Positions where pieces enter their home paths */
    private static final int[] HOME_ENTRIES = {50, 11, 24, 37};

    /**
     * Constructs a node with the given properties.
     *
     * @param position the node's position on the board
     * @param x        the x-coordinate for visual representation
     * @param y        the y-coordinate for visual representation
     * @param safe     whether this is a safe spot where pieces cannot be captured
     */
    public Node(int position, int x, int y, boolean safe) {
        this.position = position;
        this.x = x;
        this.y = y;
        this.safe = safe;
        this.pieces = new ArrayList<>();
    }

    /**
     * Returns the next node in the main path.
     *
     * @return the next node
     */
    public Node getNext() {
        return next;
    }

    /**
     * Returns the next node in the path for the given color.
     * If the node is a home entry for the given color, returns the next node in the home path.
     *
     * @param color the color of the piece
     * @return the next node in the path
     */
    public Node getNext(Color color) {
        if (isHomeEntry(color)) {
            return homePathNext;
        }
        return next;
    }

    /**
     * Sets the next node in the main path.
     *
     * @param next the next node
     */
    public void setNext(Node next) {
        this.next = next;
    }

    /**
     * Returns the next node in the home path.
     *
     * @return the next node in the home path
     */
    public Node getHomePathNext() {
        return homePathNext;
    }

    /**
     * Sets the next node in the home path.
     *
     * @param homePathNext the next node in the home path
     */
    public void setHomePathNext(Node homePathNext) {
        this.homePathNext = homePathNext;
    }

    /**
     * Returns the node's position on the board.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the x-coordinate for visual representation.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate for visual representation.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns whether this is a safe spot where pieces cannot be captured.
     *
     * @return whether the node is safe
     */
    public boolean isSafe() {
        return safe;
    }

    /**
     * Returns a copy of the list of pieces currently occupying the space.
     *
     * @return the list of pieces
     */
    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }

    /**
     * Returns whether the node is empty (i.e., no pieces are occupying the space).
     *
     * @return whether the node is empty
     */
    public boolean isEmpty() {
        return pieces.isEmpty();
    }

    /**
     * Returns whether this is a safe spot where pieces cannot be captured.
     *
     * @return whether the node is a safe spot
     */
    public boolean isSafeSpot() {
        return safe;
    }

    /**
     * Adds a piece to the node and returns a list of captured pieces.
     * If the node is not a safe spot, pieces of different colors are captured and sent to their base.
     *
     * @param piece the piece to add
     * @return the list of captured pieces
     */
    public List<Piece> addPiece(Piece piece) {
        List<Piece> captured = new ArrayList<>();
        if (!safe) {
            pieces.stream()
                .filter(p -> p.getColor() != piece.getColor())
                .forEach(p -> {
                    captured.add(p);
                    p.sendToBase();
                });
            pieces.removeAll(captured);
        }
        pieces.add(piece);
        return captured;
    }

    /**
     * Removes a piece from the node.
     *
     * @param piece the piece to remove
     */
    public void removePiece(Piece piece) {
        pieces.remove(piece);
    }

    /**
     * Returns whether the node is a home entry for the given color.
     *
     * @param color the color of the piece
     * @return whether the node is a home entry
     */
    public boolean isHomeEntry(Color color) {
        int colorIndex = getColorIndex(color);
        return position == HOME_ENTRIES[colorIndex];
    }

    /**
     * Returns the distance from this node to the given node.
     * If the nodes are not connected, returns Integer.MAX_VALUE.
     *
     * @param other the other node
     * @return the distance
     */
    public int getDistanceTo(Node other) {
        if (other == null) return Integer.MAX_VALUE;
        
        Node current = this;
        int distance = 0;
        while (current != other && current != null && distance < 52) {
            current = current.next;
            distance++;
        }
        return current == other ? distance : Integer.MAX_VALUE;
    }

    /**
     * Returns the index of the given color in the HOME_ENTRIES array.
     *
     * @param color the color of the piece
     * @return the index
     */
    private int getColorIndex(Color color) {
        if (color.equals(Color.BLUE)) return 0;
        if (color.equals(Color.GREEN)) return 1;
        if (color.equals(Color.YELLOW)) return 2;
        if (color.equals(Color.RED)) return 3;
        throw new IllegalArgumentException("Invalid color");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node other = (Node) obj;
        return position == other.position && 
               x == other.x && 
               y == other.y && 
               safe == other.safe;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + position;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + (safe ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Node[position=" + position + ", x=" + x + ", y=" + y + ", safe=" + safe + "]";
    }
}