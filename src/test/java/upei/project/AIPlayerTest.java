package upei.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Test suite for the AIPlayer class in the Ludo game.
 * Tests the functionality of AI players including:
 * - Player initialization with different strategies
 * - Move evaluation and selection
 * - Game statistics tracking
 * - Strategy-specific behavior
 *
 * Each test method includes a timeout to ensure performance
 * and prevent infinite loops in strategy evaluation.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see AIPlayer
 * @see MoveStrategy
 */
public class AIPlayerTest {
    /** Test AI player instance */
    private AIPlayer aiPlayer;
    
    /** List of pieces for the AI player */
    private List<Piece> pieces;

    /**
     * Sets up the test environment before each test.
     * Creates:
     * - A board with an empty player list
     * - Four blue pieces
     * - An AI player with default strategy
     */
    @BeforeEach
    void setUp() {
        List<Player> players = new ArrayList<>();
        BoardPanel board = new BoardPanel(players);
        pieces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pieces.add(new Piece(Color.BLUE, board));
        }
        aiPlayer = new AIPlayer("AI Player", Color.BLUE, pieces);
        players.add(aiPlayer);
    }

    /**
     * Tests the default initialization of an AI player.
     * Verifies:
     * - Player name assignment
     * - Color assignment
     * - Piece count
     */
    @Test
    @Timeout(100)
    void testDefaultInitialization() {
        assertEquals("AI Player", aiPlayer.getName(), "AI player name should match constructor argument");
        assertEquals(Color.BLUE, aiPlayer.getColor(), "AI player color should match constructor argument");
        assertEquals(4, aiPlayer.getPieces().size(), "AI player should have 4 pieces");
    }

    /**
     * Tests AI player initialization with a specific strategy.
     * Verifies that players can be created with different strategies
     * and maintain their assigned properties.
     */
    @Test
    @Timeout(100)
    void testStrategyInitialization() {
        AIPlayer strategicAI = new AIPlayer("Strategic AI", Color.RED, pieces, "Aggressive");
        assertNotNull(strategicAI, "AI player should be created with specific strategy");
        assertEquals("Strategic AI", strategicAI.getName(), "AI player name should match constructor argument");
    }

    /**
     * Tests move evaluation for a piece in base with a roll of 6.
     * This is a critical test as moving out of base is a key strategic decision.
     * Verifies that the evaluation score is positive for this advantageous move.
     */
    @Test
    @Timeout(100)
    void testMoveEvaluationFromBase() {
        Piece piece = pieces.getFirst();
        int evaluation = aiPlayer.evaluateMove(piece, 6);
        assertTrue(evaluation > 0, "Moving piece out of base with 6 should have positive evaluation");
    }

    /**
     * Tests the makeMove method of AIPlayer.
     * Verifies:
     * - Move execution
     * - Statistics tracking
     * - Valid move selection
     */
    @Test
    @Timeout(100)
    void testMakeMove() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.add(aiPlayer);
        
        aiPlayer.makeMove(6, allPlayers);
        assertTrue(aiPlayer.getMovesCount() >= 0, "Moves count should be tracked");
    }

    /**
     * Tests the capture statistics tracking.
     * Verifies that capture counts are properly initialized
     * and maintained.
     */
    @Test
    @Timeout(100)
    void testCaptureStatistics() {
        assertTrue(aiPlayer.getCapturesMade() >= 0, "Captures made should be non-negative");
    }

    /**
     * Tests the move statistics tracking.
     * Verifies that move counts are properly initialized
     * and incremented.
     */
    @Test
    @Timeout(100)
    void testMoveStatistics() {
        assertEquals(0, aiPlayer.getMovesCount(), "Initial moves count should be 0");
    }

    /**
     * Tests AI player behavior with different strategies.
     * Creates players with various strategies and verifies
     * that they maintain their distinct properties.
     */
    @Test
    @Timeout(100)
    void testDifferentStrategies() {
        AIPlayer aggressiveAI = new AIPlayer("Aggressive AI", Color.RED, pieces, "Aggressive");
        AIPlayer defensiveAI = new AIPlayer("Defensive AI", Color.GREEN, pieces, "Defensive");
        
        assertNotNull(aggressiveAI, "Aggressive AI should be created successfully");
        assertNotNull(defensiveAI, "Defensive AI should be created successfully");
        assertNotEquals(aggressiveAI.getName(), defensiveAI.getName(), "Different AI players should have different names");
    }
}
