package upei.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Test suite for the Node class in the Ludo game.
 * Tests the functionality of board nodes including:
 * - Node creation and initialization
 * - Piece management (adding/removing)
 * - Node linking and navigation
 * - Home path logic for different colors
 * - Safe spot behavior
 * - Piece capture rules
 *
 * Each test method includes a timeout to ensure performance.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see Node
 * @see Piece
 */
public class NodeTest {
    /** Test node instance */
    private Node node;
    
    /** Test piece for node operations */
    private Piece piece;
    
    /** Game board for testing */
    private BoardPanel board;
    
    /** List of players for board initialization */
    private List<Player> players;

    /**
     * Sets up the test environment before each test.
     * Creates:
     * - A board with an empty player list
     * - A test node at position 0
     * - A blue test piece
     */
    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        board = new BoardPanel(players);
        node = new Node(0, 100, 100, false);
        piece = new Piece(Color.BLUE, board);
    }

    /**
     * Tests node initialization with various parameters.
     * Verifies:
     * - Position assignment
     * - Coordinate assignment
     * - Safe status
     */
    @Test
    @Timeout(100)
    void testNodeInitialization() {
        Node testNode = new Node(5, 200, 300, true);
        assertEquals(5, testNode.getPosition(), "Node position should match constructor argument");
        assertEquals(200, testNode.getX(), "Node X coordinate should match constructor argument");
        assertEquals(300, testNode.getY(), "Node Y coordinate should match constructor argument");
        assertTrue(testNode.isSafe(), "Node safe status should match constructor argument");
    }

    /**
     * Tests piece management operations.
     * Verifies:
     * - Initial empty state
     * - Adding pieces
     * - Removing pieces
     * - List size maintenance
     */
    @Test
    @Timeout(100)
    void testPieceManagement() {
        assertTrue(node.getPieces().isEmpty(), "New node should have no pieces");
        node.addPiece(piece);
        assertEquals(1, node.getPieces().size(), "Node should have one piece after adding");
        node.removePiece(piece);
        assertTrue(node.getPieces().isEmpty(), "Node should have no pieces after removing");
    }

    /**
     * Tests node linking and navigation.
     * Verifies that nodes can be properly connected
     * and traversed in the game board.
     */
    @Test
    @Timeout(100)
    void testNodeLinking() {
        Node nextNode = new Node(1, 150, 150, false);
        node.setNext(nextNode);
        assertEquals(nextNode, node.getNext(Color.BLUE), "Next node should be correctly set");
    }

    /**
     * Tests home path logic for different colored pieces.
     * Verifies:
     * - Correct home path entry for each color
     * - Home path bypass for other colors
     * - Home path connections
     */
    @Test
    @Timeout(100)
    void testHomePathLogic() {
        // Test blue home path entry
        Node blueEntryNode = new Node(50, 0, 0, false);
        Node blueHomePath = new Node(52, 0, 0, false);
        blueEntryNode.setHomePathNext(blueHomePath);
        assertEquals(blueHomePath, blueEntryNode.getNext(Color.BLUE), 
            "Blue piece should enter home path at position 50");
        
        // Test other colors don't enter blue home path
        Node regularNext = new Node(51, 0, 0, false);
        blueEntryNode.setNext(regularNext);
        assertEquals(regularNext, blueEntryNode.getNext(Color.RED),
            "Red piece should not enter blue home path");
    }

    /**
     * Tests safe spot functionality.
     * Verifies that nodes can be properly marked as safe
     * and that this status affects piece capture rules.
     */
    @Test
    @Timeout(100)
    void testSafeSpot() {
        Node safeNode = new Node(8, 200, 200, true);
        assertTrue(safeNode.isSafe(), "Safe node should be marked as safe");
        assertFalse(node.isSafe(), "Regular node should not be marked as safe");
    }

    /**
     * Tests piece capture logic on nodes.
     * Verifies:
     * - Capture conditions
     * - Safe spot protection
     * - Multiple piece interactions
     */
    @Test
    @Timeout(100)
    void testPieceCapture() {
        // Test capture on a non-safe spot
        Node normalNode = new Node(0, 100, 100, false);
        Piece piece1 = new Piece(Color.BLUE, board);
        Piece piece2 = new Piece(Color.RED, board);
        
        normalNode.addPiece(piece1);
        List<Piece> piecesBeforeCapture = new ArrayList<>(normalNode.getPieces());
        assertEquals(1, piecesBeforeCapture.size(), "Should have one piece before adding second piece");
        
        List<Piece> captured = normalNode.addPiece(piece2);
        assertEquals(1, captured.size(), "Should capture one piece");
        assertTrue(captured.contains(piece1), "Blue piece should be captured");
        
        List<Piece> finalPieces = normalNode.getPieces();
        assertEquals(1, finalPieces.size(), "Should have one piece after capture");
        assertTrue(finalPieces.contains(piece2), "Red piece should remain");
        assertFalse(finalPieces.contains(piece1), "Blue piece should be removed");
        
        // Test no capture on a safe spot
        Node safeNode = new Node(0, 100, 100, true);
        safeNode.addPiece(piece1);
        captured = safeNode.addPiece(piece2);
        
        assertTrue(captured.isEmpty(), "No pieces should be captured on safe spot");
        List<Piece> safePieces = safeNode.getPieces();
        assertEquals(2, safePieces.size(), "Should have both pieces on safe spot");
        assertTrue(safePieces.contains(piece1), "Blue piece should remain on safe spot");
        assertTrue(safePieces.contains(piece2), "Red piece should be added to safe spot");
    }

    /**
     * Tests multiple pieces on safe spot.
     * Verifies:
     * - Initial empty state
     * - Adding pieces
     * - List size maintenance
     */
    @Test
    @Timeout(100)
    void testMultiplePiecesOnSafeSpot() {
        Node safeNode = new Node(8, 200, 200, true);
        Piece piece1 = new Piece(Color.BLUE, board);
        Piece piece2 = new Piece(Color.RED, board);
        
        safeNode.addPiece(piece1);
        List<Piece> piecesBeforeSecond = new ArrayList<>(safeNode.getPieces());
        assertEquals(1, piecesBeforeSecond.size(), "Should have one piece initially");
        
        safeNode.addPiece(piece2);
        List<Piece> piecesAfterSecond = safeNode.getPieces();
        assertEquals(2, piecesAfterSecond.size(), "Safe spot should allow multiple pieces");
        assertTrue(piecesAfterSecond.contains(piece1), "First piece should remain");
        assertTrue(piecesAfterSecond.contains(piece2), "Second piece should be added");
    }
}
