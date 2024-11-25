package upei.project;

import java.util.ArrayList;
import java.util.List;

/**
 * SimulationExperiment runs multiple games with varied strategies.
 */
public class SimulationExperiment {
    public static void main(String[] args) {
        System.out.println("Running Ludo Simulation...");

        int trials = 60;
        List<Player> players = new ArrayList<>();
        players.add(new HumanPlayer("Alice", "Blue"));
        players.add(new AIPlayer("Bot1", "Red"));
        players.add(new AIPlayer("Bot2", "Green"));
        players.add(new AIPlayer("Bot3", "Yellow"));

        for (int i = 1; i <= trials; i++) {
            System.out.println("Trial " + i + " begins...");
            // Implement the game simulation logic
            // Track results
        }

        System.out.println("Simulation complete.");
    }
}
