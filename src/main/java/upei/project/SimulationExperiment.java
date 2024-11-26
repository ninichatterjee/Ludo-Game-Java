package upei.project;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationExperiment {
    public static void main(String[] args) {
        System.out.println("Running Ludo Simulation...");

        int trials = 60;
        List<Player> players = new ArrayList<>();
        players.add(new HumanPlayer("Alice", Color.BLUE, createPieces(Color.BLUE)));
        players.add(new AIPlayer("Bot1", Color.RED, createPieces(Color.RED)));
        players.add(new AIPlayer("Bot2", Color.GREEN, createPieces(Color.GREEN)));
        players.add(new AIPlayer("Bot3", Color.YELLOW, createPieces(Color.YELLOW)));

        for (int i = 1; i <= trials; i++) {
            System.out.println("Trial " + i + " begins...");
            // Set up the game for simulation
            LudoGame game = new LudoGame();

            // Simulate game programmatically
            while (!game.isGameOver()) {
                game.rollDice(); // Trigger dice roll
            }

            System.out.println("Trial " + i + " completed.");
        }

        System.out.println("Simulation complete.");
    }

    private static List<Piece> createPieces(Color color) {
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pieces.add(new Piece(color));
        }
        return pieces;
    }
}