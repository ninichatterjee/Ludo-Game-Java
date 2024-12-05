package upei.project;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A simulation framework for testing and evaluating different Ludo AI strategies.
 * This class runs multiple games with various strategy combinations to:
 * - Compare strategy effectiveness
 * - Gather performance statistics
 * - Analyze game outcomes
 *
 * Statistics tracked include:
 * - Wins per strategy
 * - Average moves per game
 * - Capture rates
 * - Game completion rates
 *
 * The simulation supports:
 * - Single-strategy games (all players use same strategy)
 * - Mixed-strategy games (players use different strategies)
 * - Multiple trials per configuration
 *
 * @author UPEI Project Team
 * @version 1.0
 */
public class SimulationExperiment {
    /** Number of games to run per configuration */
    private static final int NUM_TRIALS = 20;
    
    /** Maximum moves before declaring a game stuck */
    private static final int MAX_MOVES = 500;
    
    /** Random number generator for dice rolls */
    private static final Random random = new Random();
    
    /** Track wins per strategy */
    private static final Map<String, Integer> wins = new HashMap<>();
    
    /** Track total moves per strategy */
    private static final Map<String, Integer> totalMoves = new HashMap<>();
    
    /** Track captures per strategy */
    private static final Map<String, Integer> totalCaptures = new HashMap<>();
    
    /** Track games played per strategy */
    private static final Map<String, Integer> gamesPlayed = new HashMap<>();

    static {
        // Initialize statistics for each strategy
        initializeStats();
    }

    /**
     * Main entry point for running the simulation experiments.
     * Runs multiple trials with different strategy combinations and
     * prints the results.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Ludo Strategy Simulation\n");
        
        // Test each strategy combination
        testStrategy("Aggressive");
        testStrategy("Defensive");
        testStrategy("Balanced");
        
        // Test mixed strategies with multiple combinations
        testMixedStrategies(Arrays.asList("Aggressive", "Aggressive", "Defensive", "Defensive"));
        testMixedStrategies(Arrays.asList("Aggressive", "Defensive", "Balanced", "Balanced"));
        testMixedStrategies(Arrays.asList("Defensive", "Defensive", "Balanced", "Balanced"));
        
        printResults();
    }
    
    /**
     * Initializes statistics tracking for each strategy type.
     * Resets all counters to zero.
     */
    private static void initializeStats() {
        String[] strategies = {"Aggressive", "Defensive", "Balanced"};
        for (String strategy : strategies) {
            wins.put(strategy, 0);
            totalMoves.put(strategy, 0);
            totalCaptures.put(strategy, 0);
            gamesPlayed.put(strategy, 0);
        }
    }
    
    /**
     * Tests a single strategy against itself.
     * All players use the same strategy for these games.
     *
     * @param strategy The strategy to test
     */
    private static void testStrategy(String strategy) {
        System.out.println("\nTesting " + strategy + " vs " + strategy);
        for (int trial = 1; trial <= NUM_TRIALS; trial++) {
            runGame(Arrays.asList(strategy, strategy, strategy, strategy), trial);
        }
    }
    
    /**
     * Tests a combination of different strategies.
     * Each player can use a different strategy.
     *
     * @param strategies List of strategies to use, one per player
     */
    private static void testMixedStrategies(List<String> strategies) {
        System.out.println("\nTesting Mixed Strategies: " + strategies);
        for (int trial = 1; trial <= NUM_TRIALS; trial++) {
            runGame(strategies, trial);
        }
    }
    
    /**
     * Runs a single game simulation with the specified strategies.
     * Tracks statistics and determines the winner.
     *
     * @param strategies List of strategies to use, one per player
     * @param trial Trial number for this game
     */
    private static void runGame(List<String> strategies, int trial) {
        // Create players with their strategies
        List<Player> players = new ArrayList<>();
        BoardPanel board = new BoardPanel(players) {
            @Override
            public void paintComponent(Graphics g) {}
            @Override
            public void repaint() {}
        };
        
        // Create players
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        for (int i = 0; i < 4; i++) {
            String name = strategies.get(i) + (i + 1);
            List<Piece> pieces = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                pieces.add(new Piece(colors[i], board));
            }
            players.add(new AIPlayer(name, colors[i], pieces, strategies.get(i)));
        }
        
        // Set up player relationships
        for (Player player : players) {
            player.setAllPlayers(players);
        }
        
        // Initialize game stats if needed
        for (String strategy : strategies) {
            if (!wins.containsKey(strategy)) {
                wins.put(strategy, 0);
                totalMoves.put(strategy, 0);
                totalCaptures.put(strategy, 0);
                gamesPlayed.put(strategy, 0);
            }
            gamesPlayed.put(strategy, gamesPlayed.get(strategy) + 1);
        }
        
        // Run the game
        int moves = 0;
        int currentPlayer = 0;
        Player winner = null;
        int stuckMoves = 0;
        
        // Update games played count for each strategy
        for (String strategy : strategies) {
            gamesPlayed.put(strategy, gamesPlayed.get(strategy) + 1);
        }
        
        while (moves < MAX_MOVES && winner == null) {
            Player player = players.get(currentPlayer);
            boolean madeMoveThisTurn = false;
            
            // Give multiple roll attempts in late game
            int numAttempts = moves > 200 ? 3 : 1;  
            
            for (int attempt = 0; attempt < numAttempts && !madeMoveThisTurn; attempt++) {
                int roll = random.nextInt(6) + 1;
                
                // Late game bonus: higher chance of useful rolls
                if (moves > 300 && roll < 3) {  
                    roll = random.nextInt(6) + 1;
                }

                // Guarantee 6s more frequently early game to get pieces out
                if (moves < 100 && moves % 10 == 0 && attempt == 0) {
                    roll = 6;
                }
                
                try {
                    // Track pieces before move
                    Set<Piece> piecesBeforeMove = new HashSet<>();
                    for (Piece p : player.getPieces()) {
                        if (!p.isAtHome() && !p.hasReachedHome()) {
                            piecesBeforeMove.add(p);
                        }
                    }
                    
                    player.makeMove(roll, players);
                    
                    // Check if any piece actually moved
                    for (Piece p : player.getPieces()) {
                        if (!p.isAtHome() && !p.hasReachedHome() && !piecesBeforeMove.contains(p)) {
                            madeMoveThisTurn = true;
                            break;
                        }
                    }
                    
                    if (player.hasWon()) {
                        winner = player;
                        break;
                    }
                } catch (Exception e) {
                    // Skip invalid moves
                }
            }
            
            // Track stuck state
            if (!madeMoveThisTurn) {
                stuckMoves++;
                if (stuckMoves >= 10) {  
                    // Help stuck players with extra moves
                    for (Player p : players) {
                        if (!p.hasWon()) {
                            for (int i = 0; i < 2; i++) {  
                                int roll = random.nextInt(6) + 1;
                                if (moves > 200 && i == 0) roll = 6;  
                                try {
                                    p.makeMove(roll, players);
                                    if (p.hasWon()) {
                                        winner = p;
                                        break;
                                    }
                                } catch (Exception e) {
                                    // Skip invalid moves
                                }
                            }
                        }
                    }
                    stuckMoves = 0;
                }
            } else {
                stuckMoves = 0;
            }
            
            currentPlayer = (currentPlayer + 1) % players.size();
            moves++;
        }
        
        // Record results
        if (winner != null) {
            String strategy = strategies.get(players.indexOf(winner));
            wins.put(strategy, wins.get(strategy) + 1);
            totalMoves.put(strategy, totalMoves.get(strategy) + moves);
            totalCaptures.put(strategy, totalCaptures.get(strategy) + 
                ((AIPlayer)winner).getCapturesMade());
            
            System.out.printf("Trial %d: %s won in %d moves with %d captures%n",
                trial, strategy, moves, ((AIPlayer)winner).getCapturesMade());
        } else {
            System.out.printf("Trial %d: No winner after %d moves%n", trial, moves);
        }
    }
    
    /**
     * Prints the final results of the simulation, including:
     * - Total games played
     * - Wins per strategy
     * - Average moves per game
     * - Capture rates
     */
    private static void printResults() {
        System.out.println("\n=== Final Results ===");
        String[] strategies = {"Aggressive", "Defensive", "Balanced"};
        
        int totalGames = 0;
        for (String strategy : strategies) {
            totalGames += gamesPlayed.get(strategy);
        }
        
        System.out.println("Total Games Played: " + totalGames);
        
        for (String strategy : strategies) {
            int strategyWins = wins.get(strategy);
            int strategyGames = gamesPlayed.get(strategy);
            double winRate = strategyGames > 0 ? (strategyWins * 100.0) / strategyGames : 0;
            double avgMoves = strategyWins > 0 ? 
                (double)totalMoves.get(strategy) / strategyWins : 0;
            double avgCaptures = strategyWins > 0 ? 
                (double)totalCaptures.get(strategy) / strategyWins : 0;
            
            System.out.printf("\n%s Strategy:%n", strategy);
            System.out.printf("  Games Played: %d%n", strategyGames);
            System.out.printf("  Wins: %d (%.1f%%)%n", strategyWins, winRate);
            System.out.printf("  Average Moves per Win: %.1f%n", avgMoves);
            System.out.printf("  Average Captures per Win: %.1f%n", avgCaptures);
        }
    }
}