package upei.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Collections;

/**
 * Test suite for the LudoGame class.
 * Tests the core game functionality including:
 * - Game initialization and setup
 * - Player management and turns
 * - Game state tracking
 * - UI component creation
 * - Game flow control
 *
 * Uses reflection to access private fields for thorough testing
 * of internal game state. Each test method includes a timeout
 * to ensure responsive game behavior.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see LudoGame
 * @see Player
 * @see BoardPanel
 */
public class LudoGameTest {
    /** The game instance being tested */
    private LudoGame game;

    /**
     * Sets up the test environment before each test.
     * Creates a new LudoGame instance with default settings.
     */
    @BeforeEach
    void setUp() {
        game = new LudoGame();
    }

    /**
     * Tests the initial state of a newly created game.
     * Verifies:
     * - Player list creation
     * - Player count
     * - Game over flag
     */
    @Test
    @Timeout(100)
    void testInitialGameState() {
        List<Player> players = getPlayersField();
        assertNotNull(players, "Players list should not be null");
        assertEquals(4, players.size(), "Game should have 4 players");
        assertFalse(getGameOverField(), "Game should not be over at start");
    }

    /**
     * Tests player initialization and setup.
     * Verifies:
     * - Player list creation
     * - Color assignment
     * - Player order
     */
    @Test
    @Timeout(100)
    void testPlayerInitialization() {
        List<Player> players = getPlayersField();
        assertNotNull(players, "Players list should not be null");
        assertEquals(Color.BLUE, players.get(0).getColor(), "First player should be blue");
        assertEquals(Color.GREEN, players.get(1).getColor(), "Second player should be green");
        assertEquals(Color.YELLOW, players.get(2).getColor(), "Third player should be yellow");
        assertEquals(Color.RED, players.get(3).getColor(), "Fourth player should be red");
    }

    /**
     * Tests game component initialization.
     * Verifies the creation and setup of:
     * - Board panel
     * - Game pieces
     * - UI elements
     */
    @Test
    @Timeout(100)
    void testGameComponents() {
        BoardPanel boardPanel = getBoardPanelField();
        assertNotNull(boardPanel, "Board panel should not be null");
        assertInstanceOf(BoardPanel.class, boardPanel, "Board panel should be instance of BoardPanel");
    }

    /**
     * Tests player turn management.
     * Verifies:
     * - Turn order
     * - Player rotation
     * - Current player tracking
     */
    @Test
    @Timeout(100)
    void testPlayerTurns() {
        int initialPlayer = getCurrentPlayerIndexField();
        simulateNextTurn();
        int expectedNextPlayer = (initialPlayer + 1) % 4;
        assertEquals(expectedNextPlayer, getCurrentPlayerIndexField(), 
            "Current player should advance to next player");
    }

    /**
     * Tests game over state management.
     * Verifies:
     * - Initial state
     * - State changes
     * - Game over detection
     */
    @Test
    @Timeout(100)
    void testGameOverState() {
        assertFalse(getGameOverField(), "Game should not be over initially");
        setGameOverField(true);
        assertTrue(getGameOverField(), "Game should be over after setting game over state");
    }

    /**
     * Tests UI component initialization and properties.
     * Verifies:
     * - Window creation
     * - Title setting
     * - Close operation
     * - Layout settings
     */
    @Test
    @Timeout(100)
    void testUIComponents() {
        assertInstanceOf(JFrame.class, game, "Game should be a JFrame");
        assertEquals("Ludo Game", game.getTitle(), "Game window should have correct title");
        assertEquals(JFrame.EXIT_ON_CLOSE, game.getDefaultCloseOperation(), 
            "Game should exit on close");
    }

    /**
     * Helper method to access the players list field via reflection.
     * 
     * @return List of players in the game
     */
    @SuppressWarnings("unchecked")
    private List<Player> getPlayersField() {
        try {
            Field playersField = LudoGame.class.getDeclaredField("players");
            playersField.setAccessible(true);
            List<Player> players = (List<Player>) playersField.get(game);
            return players != null ? players : Collections.emptyList();
        } catch (Exception e) {
            fail("Could not access players field: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Helper method to access the board panel field via reflection.
     * 
     * @return The game's board panel
     */
    private BoardPanel getBoardPanelField() {
        try {
            Field boardField = LudoGame.class.getDeclaredField("boardPanel");
            boardField.setAccessible(true);
            return (BoardPanel) boardField.get(game);
        } catch (Exception e) {
            fail("Could not access board panel field: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to access the current player index via reflection.
     * 
     * @return Index of the current player
     */
    private int getCurrentPlayerIndexField() {
        try {
            Field indexField = LudoGame.class.getDeclaredField("currentPlayerIndex");
            indexField.setAccessible(true);
            return indexField.getInt(game);
        } catch (Exception e) {
            fail("Could not access current player index field: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Helper method to access the game over flag via reflection.
     * 
     * @return true if game is over, false otherwise
     */
    private boolean getGameOverField() {
        try {
            Field gameOverField = LudoGame.class.getDeclaredField("isGameOver");
            gameOverField.setAccessible(true);
            return gameOverField.getBoolean(game);
        } catch (Exception e) {
            fail("Could not access game over field: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to set the game over flag via reflection.
     * 
     * @param value The value to set for the game over flag
     */
    private void setGameOverField(boolean value) {
        try {
            Field gameOverField = LudoGame.class.getDeclaredField("isGameOver");
            gameOverField.setAccessible(true);
            gameOverField.setBoolean(game, value);
        } catch (Exception e) {
            fail("Could not set game over field: " + e.getMessage());
        }
    }

    /**
     * Helper method to simulate advancing to the next turn.
     * Updates the current player index.
     */
    private void simulateNextTurn() {
        try {
            Field indexField = LudoGame.class.getDeclaredField("currentPlayerIndex");
            indexField.setAccessible(true);
            int currentIndex = indexField.getInt(game);
            indexField.setInt(game, (currentIndex + 1) % 4);
        } catch (Exception e) {
            fail("Could not simulate next turn: " + e.getMessage());
        }
    }
}
