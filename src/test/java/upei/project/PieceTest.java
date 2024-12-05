package upei.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Test suite for the Piece class in the Ludo game.
 * Tests the core functionality of game pieces including:
 * - Piece creation and initialization
 * - Movement validation
 * - Color assignment
 * - Distance calculations
 * - State tracking (base, board, home)
 * - Piece equality
 *
 * Each test method includes a timeout to ensure performance.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see Piece
 */
public class PieceTest {
    /** Test piece instance */
    private Piece piece;
    
    /** Game board for testing piece movements */
    private BoardPanel board;
    
    /** List of players for board initialization */
    private final List<Player> players = new ArrayList<>();

    /**
     * Sets up the test environment before each test.
     * Creates a new board, piece, and player configuration.
     * The setup includes:
     * - Creating a board with an empty player list
     * - Creating a blue test piece
     * - Adding an AI player that owns the test piece
     */
    @BeforeEach
    void setUp() {
        board = new BoardPanel(players);
        piece = new Piece(Color.BLUE, board);
        // Add a player with this piece
        List<Piece> pieces = new ArrayList<>();
        pieces.add(piece);
        Player player = new AIPlayer("Test Player", Color.BLUE, pieces);
        players.add(player);
    }

    /**
     * Tests the initial state of a newly created piece.
     * Verifies that:
     * - The piece has not reached home
     * - The piece is not on any node (in base)
     * - The piece color matches the constructor argument
     */
    @Test
    @Timeout(100)
    void testInitialState() {
        assertFalse(piece.hasReachedHome(), "New piece should not be home");
        assertNull(piece.getCurrentNode(), "New piece should not be on any node");
        assertEquals(Color.BLUE, piece.getColor(), "Piece color should match constructor argument");
    }

    /**
     * Tests the distance calculation for a piece in its starting base.
     * Verifies that a piece in base returns Integer.MAX_VALUE as its
     * distance from home, indicating it must first move onto the board.
     */
    @Test
    @Timeout(100)
    void testDistanceFromHomeInBase() {
        assertEquals(Integer.MAX_VALUE, piece.getDistanceFromHome(), 
            "Piece in base should return MAX_VALUE as distance");
    }

    /**
     * Tests piece color assignment for all valid colors in the game.
     * Creates pieces with each allowed color (Blue, Green, Yellow, Red)
     * and verifies that their colors are correctly assigned.
     */
    @Test
    @Timeout(100)
    void testPieceColors() {
        Piece bluePiece = new Piece(Color.BLUE, board);
        Piece greenPiece = new Piece(Color.GREEN, board);
        Piece yellowPiece = new Piece(Color.YELLOW, board);
        Piece redPiece = new Piece(Color.RED, board);

        assertEquals(Color.BLUE, bluePiece.getColor(), "Blue piece should have blue color");
        assertEquals(Color.GREEN, greenPiece.getColor(), "Green piece should have green color");
        assertEquals(Color.YELLOW, yellowPiece.getColor(), "Yellow piece should have yellow color");
        assertEquals(Color.RED, redPiece.getColor(), "Red piece should have red color");
    }

    /**
     * Tests move validation when attempting to move a piece from base.
     * Verifies that:
     * - Moving with any number other than 6 throws InvalidMoveException
     * This enforces the Ludo rule that pieces can only leave base with a roll of 6.
     */
    @Test
    @Timeout(100)
    void testInvalidMoveFromBase() {
        assertThrows(InvalidMoveException.class, () -> {
            piece.validateMove(2); // Should throw exception as pieces can only move out with a 6
        }, "Should throw InvalidMoveException for non-6 move from base");
    }

    /**
     * Tests piece equality and identity comparison.
     * Verifies that:
     * - A piece is not equal to null
     * - A piece is not equal to pieces of different colors
     * - A piece is equal to itself
     * - Different pieces of the same color are not equal
     */
    @Test
    @Timeout(100)
    void testPieceEquality() {
        Piece redPiece = new Piece(Color.RED, board);
        Piece otherPiece = new Piece(Color.BLUE, board);

        assertNotEquals(null, piece, "Piece should not equal null");
        assertNotEquals(redPiece, piece, "Pieces of different colors should not be equal");
        assertEquals(piece, piece, "Piece should equal itself");
        assertNotEquals(otherPiece, piece, "Different pieces should not be equal even with same color");
    }
}
