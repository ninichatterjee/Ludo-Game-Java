package upei.project;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SimulationExperiment {
    private static final int NUM_TRIALS = 60;
    private static final Map<String, Integer> strategyWins = new HashMap<>();
    private static final Map<String, Integer> movesToWin = new HashMap<>();
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Running Ludo Simulation with " + NUM_TRIALS + " trials...\n");

        // Initialize strategy tracking
        strategyWins.put("Aggressive", 0);
        strategyWins.put("Defensive", 0);
        strategyWins.put("Balanced", 0);
        
        movesToWin.put("Aggressive", 0);
        movesToWin.put("Defensive", 0);
        movesToWin.put("Balanced", 0);

        for (int trial = 1; trial <= NUM_TRIALS; trial++) {
            System.out.println("Trial " + trial + " begins...");
            runTrial(trial);
        }

        // Print results
        System.out.println("\n=== Simulation Results ===");
        System.out.println("Total trials: " + NUM_TRIALS);
        System.out.println("\nWin Statistics:");
        strategyWins.forEach((strategy, wins) -> {
            double winRate = (wins * 100.0) / NUM_TRIALS;
            double avgMoves = movesToWin.get(strategy) / (double)Math.max(1, wins);
            System.out.printf("%s Strategy: %d wins (%.1f%%) - Avg moves to win: %.1f%n",
                    strategy, wins, winRate, avgMoves);
        });

        // Determine best strategy
        String bestStrategy = strategyWins.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        System.out.println("\nBest Performing Strategy: " + bestStrategy);
    }

    private static void runTrial(int trialNum) {
        JFrame frame = new JFrame();
        BoardPanel boardPanel = new BoardPanel(new java.util.ArrayList<>());
        
        // Create players with different strategies
        java.util.List<Player> players = new java.util.ArrayList<>();
        players.add(createAIPlayer("Aggressive-" + trialNum, Color.RED, boardPanel, "Aggressive"));
        players.add(createAIPlayer("Defensive-" + trialNum, Color.BLUE, boardPanel, "Defensive"));
        players.add(createAIPlayer("Balanced-" + trialNum, Color.GREEN, boardPanel, "Balanced"));
        players.add(createAIPlayer("Aggressive2-" + trialNum, Color.YELLOW, boardPanel, "Aggressive"));

        // Set up the game
        for (Player player : players) {
            player.setAllPlayers(players);
        }

        int moveCount = 0;
        int currentPlayerIndex = 0;
        boolean gameOver = false;

        while (!gameOver) {
            moveCount++;
            Player currentPlayer = players.get(currentPlayerIndex);
            int dieRoll = random.nextInt(6) + 1;
            
            currentPlayer.makeMove(dieRoll, players);
            
            if (currentPlayer.hasWon()) {
                gameOver = true;
                String strategy = determineStrategy(currentPlayer.getName());
                strategyWins.put(strategy, strategyWins.get(strategy) + 1);
                movesToWin.put(strategy, movesToWin.get(strategy) + moveCount);
                System.out.println("Trial " + trialNum + " completed. Winner: " + strategy + " (" + moveCount + " moves)");
                break;
            }
            
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }

    private static AIPlayer createAIPlayer(String name, Color color, BoardPanel boardPanel, String strategy) {
        java.util.List<Piece> pieces = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pieces.add(new Piece(color, boardPanel));
        }

        AIPlayer player = new AIPlayer(name, color, pieces);
        
        // Set strategy-specific move evaluation
        if (strategy.equals("Aggressive")) {
            player.setMoveValidator((piece, steps) -> {
                if (!isValidMove(piece, steps)) return;
                Node targetNode = piece.simulateMove(steps);
                if (targetNode != null && !targetNode.isSafe()) {
                    // More likely to make aggressive moves
                    if (random.nextDouble() < 0.8) piece.move(steps);
                } else {
                    // Less likely to make defensive moves
                    if (random.nextDouble() < 0.3) piece.move(steps);
                }
            });
        } else if (strategy.equals("Defensive")) {
            player.setMoveValidator((piece, steps) -> {
                if (!isValidMove(piece, steps)) return;
                Node targetNode = piece.simulateMove(steps);
                if (targetNode != null && targetNode.isSafe()) {
                    // More likely to make defensive moves
                    if (random.nextDouble() < 0.8) piece.move(steps);
                } else {
                    // Less likely to make aggressive moves
                    if (random.nextDouble() < 0.3) piece.move(steps);
                }
            });
        } else {
            // Balanced strategy - equal probability for all valid moves
            player.setMoveValidator((piece, steps) -> {
                if (!isValidMove(piece, steps)) return;
                if (random.nextDouble() < 0.5) piece.move(steps);
            });
        }
        
        return player;
    }

    private static boolean isValidMove(Piece piece, int steps) {
        if (piece.isAtHome() && steps != 6) return false;
        if (piece.hasReachedHome()) return false;
        Node targetNode = piece.simulateMove(steps);
        return targetNode != null;
    }

    private static String determineStrategy(String playerName) {
        if (playerName.startsWith("Aggressive")) return "Aggressive";
        if (playerName.startsWith("Defensive")) return "Defensive";
        return "Balanced";
    }
}