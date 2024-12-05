package upei.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * A concrete implementation of the Player class for testing purposes.
 * Provides a simple implementation of abstract methods to facilitate
 * testing of the base Player class functionality.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see Player
 */
class TestPlayer extends Player {
    /**
     * Creates a new test player with specified name, color, and pieces.
     *
     * @param name The player's name
     * @param color The player's color
     * @param pieces List of pieces assigned to this player
     */
    public TestPlayer(String name, Color color, List<Piece> pieces) {
        super(name, color, new ArrayList<>(pieces)); // Create a copy of the pieces list
    }

    /**
     * Implements a simple move strategy for testing.
     * Attempts to move the first valid piece by the die roll amount.
     *
     * @param dieRoll The number rolled on the die
     * @param allPlayers List of all players in the game
     */
    @Override
    public void makeMove(int dieRoll, List<Player> allPlayers) {
        // Simple implementation for testing
        if (hasValidMoves(dieRoll)) {
            Piece piece = getPieces().getFirst();
            try {
                piece.validateMove(dieRoll);
                piece.move(dieRoll);
            } catch (InvalidMoveException e) {
                // Ignore for testing
            }
        }
    }

    /**
     * Indicates whether this player is human-controlled.
     *
     * @return false as TestPlayer is not human-controlled
     */
    @Override
    public boolean isHuman() {
        return false; // Test player is not human
    }
}

/**
 * Test suite for the Player class functionality.
 * Tests the core player features including:
 * - Player initialization and setup
 * - Move validation and execution
 * - Piece management
 * - Game state tracking
 *
 * Uses TestPlayer implementation to test abstract Player class.
 * Each test method includes a timeout to ensure performance.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see Player
 * @see TestPlayer
 */
public class PlayerTest {
    /** The test player instance */
    private TestPlayer player;
    
    /** Game board for testing player movements */
    private BoardPanel board;
    
    /** List of all players in the test game */
    private List<Player> players;
    
    /** List of pieces owned by the test player */
    private List<Piece> pieces;

    /**
     * Sets up the test environment before each test.
     * Creates a new board, player, and piece configuration.
     * The setup includes:
     * - Creating a board with an empty player list
     * - Creating 4 blue pieces
     * - Creating a test player with the pieces
     * - Adding the player to the game's player list
     */
    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        board = new BoardPanel(players);
        pieces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pieces.add(new Piece(Color.BLUE, board));
        }
        player = new TestPlayer("Test Player", Color.BLUE, new ArrayList<>(pieces));
        players.add(player);
    }

    /**
     * Tests the initial state of a newly created player.
     * Verifies that:
     * - Player name matches the constructor argument
     * - Player color is correctly assigned
     * - Player has exactly 4 pieces
     */
    @Test
    @Timeout(100)
    void testPlayerInitialization() {
        assertEquals("Test Player", player.getName(), "Player name should match constructor argument");
        assertEquals(Color.BLUE, player.getColor(), "Player color should match constructor argument");
        assertEquals(4, player.getPieces().size(), "Player should have 4 pieces");
    }

    /**
     * Tests move validation for pieces.
     * Verifies that:
     * - Invalid moves throw appropriate exceptions
     * - Pieces in base cannot move with non-6 rolls
     */
    @Test
    @Timeout(100)
    void testMoveValidation() {
        Piece piece = pieces.getFirst();
        assertThrows(InvalidMoveException.class, () -> {
            piece.validateMove(1);  // Should throw exception as piece is in base
        });
    }

    /**
     * Tests the valid moves checker.
     * Verifies that:
     * - No valid moves exist with roll of 1 when all pieces are in base
     * - Valid moves exist with roll of 6 when pieces are in base
     */
    @Test
    @Timeout(100)
    void testValidMoves() {
        assertFalse(player.hasValidMoves(1), 
            "Should not have valid moves with roll of 1 when all pieces in base");
        assertTrue(player.hasValidMoves(6), 
            "Should have valid moves with roll of 6 when pieces in base");
    }

    /**
     * Tests the move execution functionality.
     * Verifies that:
     * - Pieces can move out of base with a roll of 6
     * - At least one piece moves when a valid move is available
     */
    @Test
    @Timeout(100)
    void testMakeMove() {
        // Test move with roll of 6 (should be valid for pieces in base)
        player.makeMove(6, players);
        boolean anyPieceMoved = pieces.stream()
            .anyMatch(p -> p.getCurrentNode() != null);
        assertTrue(anyPieceMoved, "At least one piece should move with roll of 6");
    }

    /**
     * Tests player equality.
     * Verifies that:
     * - Players with different properties are not equal
     * - Player is not equal to null
     */
    @Test
    @Timeout(100)
    void testPlayerEquality() {
        Player player1 = new TestPlayer("Player1", Color.BLUE, new ArrayList<>());
        Player player3 = new TestPlayer("Player2", Color.RED, new ArrayList<>());

        assertNotEquals(player1, player3, "Players with different properties should not be equal");
        assertNotEquals(player1, null, "Player should not equal null");
    }

    /**
     * Tests player type check.
     * Verifies that:
     * - TestPlayer is not human-controlled
     * - HumanPlayer is human-controlled
     */
    @Test
    @Timeout(100)
    void testPlayerType() {
        assertFalse(player.isHuman(), "TestPlayer should not be human");
        
        // Create a human player for comparison
        Player humanPlayer = new HumanPlayer("Human", Color.RED, board, new javax.swing.JFrame());
        assertTrue(humanPlayer.isHuman(), "HumanPlayer should be human");
    }
}
