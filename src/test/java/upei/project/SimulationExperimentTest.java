package upei.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Test suite for the SimulationExperiment class.
 * Tests the AI strategy simulation functionality including:
 * - Initialization of simulation statistics
 * - Game simulation with different strategies
 * - Mixed strategy combinations
 * - Game termination conditions
 *
 * Uses reflection to access and test private static fields
 * and methods of the SimulationExperiment class.
 * Each test includes timeouts to ensure simulation
 * performance and prevent infinite loops.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see SimulationExperiment
 * @see AIPlayer
 */
public class SimulationExperimentTest {

    /**
     * Tests the initialization of simulation statistics.
     * Verifies that all required tracking maps are properly
     * initialized and contain entries for each strategy type.
     * 
     * @throws Exception if reflection access fails
     */
    @Test
    @Timeout(100)
    void testSimulationInitialization() throws Exception {
        // Access private static fields using reflection
        Map<String, Integer> wins = getPrivateStaticField("wins");
        Map<String, Integer> totalMoves = getPrivateStaticField("totalMoves");
        Map<String, Integer> totalCaptures = getPrivateStaticField("totalCaptures");
        Map<String, Integer> gamesPlayed = getPrivateStaticField("gamesPlayed");
        
        assertNotNull(wins, "Wins map should be initialized");
        assertNotNull(totalMoves, "Total moves map should be initialized");
        assertNotNull(totalCaptures, "Total captures map should be initialized");
        assertNotNull(gamesPlayed, "Games played map should be initialized");
        
        // Check for all strategy types
        List<String> strategies = Arrays.asList("Aggressive", "Defensive", "Balanced");
        for (String strategy : strategies) {
            assertTrue(wins.containsKey(strategy), "Wins should track " + strategy);
            assertEquals(0, wins.get(strategy), "Initial wins for " + strategy + " should be 0");
            assertTrue(totalMoves.containsKey(strategy), "Moves should track " + strategy);
            assertEquals(0, totalMoves.get(strategy), "Initial moves for " + strategy + " should be 0");
            assertTrue(totalCaptures.containsKey(strategy), "Captures should track " + strategy);
            assertEquals(0, totalCaptures.get(strategy), "Initial captures for " + strategy + " should be 0");
            assertTrue(gamesPlayed.containsKey(strategy), "Games played should track " + strategy);
            assertEquals(0, gamesPlayed.get(strategy), "Initial games for " + strategy + " should be 0");
        }
    }

    /**
     * Tests the core game simulation functionality.
     * Runs a single game simulation with a mix of strategies
     * and verifies that statistics are properly updated.
     * 
     * @throws Exception if reflection access or game simulation fails
     */
    @Test
    @Timeout(1000)
    void testGameSimulation() throws Exception {
        // Create a test game setup
        List<String> strategies = Arrays.asList("Aggressive", "Aggressive", "Defensive", "Balanced");
        Method runGameMethod = SimulationExperiment.class.getDeclaredMethod("runGame", List.class, int.class);
        runGameMethod.setAccessible(true);
        
        // Run the game simulation
        runGameMethod.invoke(null, strategies, 1);
        
        // Verify that statistics were updated
        Map<String, Integer> gamesPlayed = getPrivateStaticField("gamesPlayed");
        for (String strategy : strategies) {
            assertTrue(gamesPlayed.get(strategy) > 0, 
                "Games played should be updated for " + strategy);
        }
    }

    /**
     * Tests combinations of different AI strategies.
     * Verifies that the simulation can handle mixed
     * strategy combinations without errors.
     * 
     * @throws Exception if reflection access fails
     */
    @Test
    @Timeout(1000)
    void testMixedStrategies() throws Exception {
        Method testMixedMethod = SimulationExperiment.class.getDeclaredMethod("testMixedStrategies", List.class);
        testMixedMethod.setAccessible(true);
        
        List<String> strategies = Arrays.asList("Aggressive", "Defensive", "Balanced", "Balanced");
        assertDoesNotThrow(() -> testMixedMethod.invoke(null, strategies));
    }

    /**
     * Tests the maximum moves limit for game termination.
     * Verifies that the MAX_MOVES constant is set to a
     * reasonable value to prevent infinite games.
     * 
     * @throws Exception if reflection access fails
     */
    @Test
    @Timeout(100)
    void testMaxMovesLimit() throws Exception {
        Field maxMovesField = SimulationExperiment.class.getDeclaredField("MAX_MOVES");
        maxMovesField.setAccessible(true);
        int maxMoves = maxMovesField.getInt(null);
        
        assertTrue(maxMoves > 0, "Max moves should be positive");
        assertTrue(maxMoves < 1000, "Max moves should be reasonable");
    }

    /**
     * Helper method to access private static fields of the
     * SimulationExperiment class using reflection.
     * 
     * @param <T> The type of the field to access
     * @param fieldName The name of the field to access
     * @return The value of the requested field
     * @throws Exception if reflection access fails
     */
    @SuppressWarnings("unchecked")
    private <T> T getPrivateStaticField(String fieldName) throws Exception {
        Field field = SimulationExperiment.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(null);
    }
}
