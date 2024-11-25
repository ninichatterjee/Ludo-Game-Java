package upei.project;

import java.util.Random;

/**
 * Abstract Player class representing a generic player in the game.
 */
public abstract class Player {
    protected String name;
    protected String color;

    /**
     * Constructs a Player with a name and color.
     *
     * @param name  Name of the player.
     * @param color Color of the player.
     */
    public Player(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Returns the player's next move.
     *
     * @return Move object representing the move.
     */
    public abstract int getNextMove();

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
