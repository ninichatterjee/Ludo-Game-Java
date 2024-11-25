package upei.project;

import java.util.Random;

/**
 * AIPlayer class simulates AI strategy.
 */
public class AIPlayer extends Player {
    private Random random;

    public AIPlayer(String name, String color) {
        super(name, color);
        this.random = new Random();
    }

    @Override
    public int getNextMove() {
        int move = random.nextInt(6) + 1;
        System.out.println(name + " (" + color + ") chooses: " + move);
        return move;
    }
}
