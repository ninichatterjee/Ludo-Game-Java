package upei.project;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Abstract Player class representing a generic player in the game.
 */
public abstract class Player {
    protected String name;
    protected Color color;
    protected List<Piece> pieces;

    /**
     * Constructs a Player with a name and color.
     *
     * @param name  Name of the player.
     * @param color Color of the player.
     */
    public Player(String name, Color color, List<Piece> pieces) {
        this.name = name;
        this.color = color;
        this.pieces = pieces;
    }

    /**
     * Determines the next move for the player.
     * @param dieRoll The result of the die roll.
     * @param allPlayers All players in the game for strategic decision-making.
     */
    public abstract void makeMove(int dieRoll, List<Player> allPlayers);

    public List<Piece> getPieces() { return pieces; }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Returns true if the player is human.
     */

    public abstract boolean isHuman();
}
