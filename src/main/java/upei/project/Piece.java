package upei.project;

import java.awt.*;

public class Piece {
    private int position; // -1 indicates the piece is still in the base
    private final Color color; // Color of the piece

    public Piece(Color color) {
        this.position = -1; // Start in base
        this.color = color;
    }

    /**
     * Moves the piece by specified number of steps
     *
     * @param steps The number of steps to move
     */
    public void move(int steps) {
        if (position == -1 && steps == 6) {
            position = 0; // Move out of base if a 6 is rolled
        } else if (position != -1) {
            position += steps; // Normal move
        }
    }

    /**
     * Returns whether the piece is still at home
     *
     * @return true if the piece is in the base, false otherwise
     */
    public boolean isAtHome() {
        return position == -1;
    }

    /**
     * Gets the current position of the piece
     *
     * @return The position of the piece
     */
    public int getPosition() {
        return position;
    }

    /**
     * Gets the color of the piece
     *
     * @return The color of the piece
     */
    public Color getColor() {
        return color;
    }

    /**
     * Resets the piece back to the base
     */
    public void sendToBase() {
        position = -1;
    }
}
