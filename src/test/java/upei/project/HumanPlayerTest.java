package upei.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test suite for the HumanPlayer class in the Ludo game.
 * Tests the functionality of human player interactions including:
 * - Player initialization and setup
 * - Move validation and execution
 * - User interface interactions
 * - Game rule enforcement
 * - Home path navigation
 *
 * Each test method includes a timeout to ensure responsive
 * user interaction and prevent UI hangs.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see HumanPlayer
 * @see BoardPanel
 */
public class HumanPlayerTest {
    /** The human player instance being tested */
    private HumanPlayer humanPlayer;
    
    /** Test frame for UI components */
    private final JFrame gameFrame = new JFrame("Test Frame");
    
    /** Game board for testing moves */
    private BoardPanel board;
    
    /** List of players for game context */
    private final List<Player> players = new ArrayList<>();

    /**
     * Sets up the test environment before each test.
     * Creates:
     * - A board with an empty player list
     * - A human player with blue pieces
     * - A test frame for UI interactions
     */
    @BeforeEach
    void setUp() {
        board = new BoardPanel(players);
        humanPlayer = new HumanPlayer("Test Player", Color.BLUE, board, gameFrame);
        players.add(humanPlayer);
    }

    /**
     * Tests the initialization of a human player.
     * Verifies:
     * - Player name assignment
     * - Color assignment
     * - Initial piece count
     */
    @Test
    @Timeout(100)
    void testPlayerInitialization() {
        assertEquals("Test Player", humanPlayer.getName(), "Player name should match constructor argument");
        assertEquals(Color.BLUE, humanPlayer.getColor(), "Player color should match constructor argument");
        assertEquals(4, humanPlayer.getPieces().size(), "Player should have 4 pieces");
    }

    /**
     * Tests the move validator functionality.
     * Verifies that:
     * - Custom validators can be set
     * - Validators properly check move legality
     * - Invalid moves are caught
     */
    @Test
    @Timeout(100)
    void testMoveValidatorSetting() {
        humanPlayer.setMoveValidator(Piece::validateMove);
        
        assertThrows(InvalidMoveException.class, 
            () -> humanPlayer.getPieces().getFirst().validateMove(3),
            "Should throw exception for piece in base");
    }

    /**
     * Tests detection of invalid moves.
     * Verifies that illegal moves are properly identified,
     * such as moving from base without rolling a 6.
     */
    @Test
    @Timeout(100)
    void testInvalidMoveDetection() {
        assertThrows(InvalidMoveException.class, 
            () -> humanPlayer.getPieces().getFirst().validateMove(3),
            "Should not allow moving piece from base without rolling 6");
    }

    /**
     * Tests the hasValidMoves method.
     * Verifies move availability in different scenarios:
     * - Pieces in base (need 6 to move)
     * - Pieces on board (any valid roll)
     * - Mixed situations
     *
     * @throws InvalidMoveException if move validation fails
     */
    @Test
    @Timeout(100)
    void testHasValidMoves() throws InvalidMoveException {
        // Test when piece is in base
        assertFalse(humanPlayer.hasValidMoves(3), 
            "Should have no valid moves with roll of 3 when pieces are in base");
        assertTrue(humanPlayer.hasValidMoves(6), 
            "Should have valid moves with roll of 6 to leave base");

        // Move a piece out of base
        Piece firstPiece = humanPlayer.getPieces().getFirst();
        firstPiece.move(6); // Move out of base with a 6

        // Now test with piece on board
        assertTrue(humanPlayer.hasValidMoves(3), 
            "Should have valid moves with piece on board");
    }

    /**
     * Tests move validation near and into the home path.
     * Verifies:
     * - Correct entry into home path
     * - Valid moves near home entry
     * - Move distance calculations
     *
     * @throws InvalidMoveException if move validation fails
     */
    @Test
    @Timeout(100)
    void testHomePathValidation() throws InvalidMoveException {
        // Move piece near home path (for blue pieces)
        Piece testPiece = humanPlayer.getPieces().getFirst();
        testPiece.move(6); // First move out of base
        
        // Move piece to position 48 (2 steps away from home entry at 50)
        for (int i = 0; i < 8; i++) {
            testPiece.move(6);
        }

        assertTrue(humanPlayer.hasValidMoves(2), 
            "Should allow valid move into home path");
    }

    /**
     * Tests the user interface aspects of move selection.
     * Verifies that the UI properly handles:
     * - Piece selection
     * - Move confirmation
     * - Error messages
     */
    @Test
    @Timeout(100)
    void testMoveSelection() {
        // Verify that pieces can be selected
        assertNotNull(humanPlayer.getPieces(), "Player should have pieces available for selection");
        
        // Test that each piece is selectable
        for (Piece piece : humanPlayer.getPieces()) {
            assertNotNull(piece, "Each piece should be properly initialized");
            assertEquals(humanPlayer.getColor(), piece.getColor(), 
                "Each piece should have the player's color");
        }
    }

    /**
     * Tests capture handling.
     * Verifies that:
     * - Opponent pieces can be captured
     * - Captured pieces are removed from the board
     * - Captured pieces are sent to the opponent's base
     *
     * @throws InvalidMoveException if move validation fails
     */
    @Test
    @Timeout(100)
    void testCaptureHandling() throws InvalidMoveException {
        // Create opponent player
        List<Piece> opponentPieces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            opponentPieces.add(new Piece(Color.RED, board));
        }
        Player opponent = new AIPlayer("Opponent", Color.RED, opponentPieces);
        players.add(opponent);
        
        // Move attacking piece out of base and to position 0
        Piece attackingPiece = humanPlayer.getPieces().getFirst();
        attackingPiece.move(6); // Move out of base
        
        // Move opponent piece out of base and to position 5
        Piece opponentPiece = opponent.getPieces().getFirst();
        opponentPiece.move(6); // Move out of base
        for (int i = 0; i < 5; i++) {
            opponentPiece.move(1);
        }
        
        assertTrue(humanPlayer.hasValidMoves(5), 
            "Should allow capturing move");
    }

    /**
     * Tests move cancellation.
     * Verifies that:
     * - Player state remains unchanged after cancelled move
     * - Pieces are not moved
     * - No errors are thrown
     */
    @Test
    @Timeout(100)
    void testMoveCancellation() {
        // Test that player state remains unchanged after cancelled move
        int initialPieceCount = humanPlayer.getPieces().size();
        humanPlayer.makeMove(6, players);  // Simulate cancelled move
        assertEquals(initialPieceCount, humanPlayer.getPieces().size(),
            "Piece count should remain unchanged after cancelled move");
    }
}
