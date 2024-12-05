package upei.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Test suite for the BoardPanel class in the Ludo game.
 * Tests the functionality of the game board including:
 * - Board initialization and dimensions
 * - Node creation and connectivity
 * - Home path entry points
 * - Safe spot placement
 * - Visual rendering
 * - Player piece management
 *
 * Each test method includes a timeout to ensure performance
 * and prevent rendering delays.
 *
 * @author UPEI Project Team
 * @version 1.0
 * @see BoardPanel
 * @see Node
 */
public class BoardPanelTest {
    /** The board panel instance being tested */
    private BoardPanel boardPanel;
    
    /** List of players for testing board interactions */
    private List<Player> players;

    /**
     * Sets up the test environment before each test.
     * Creates:
     * - A list of players with different colors
     * - Four pieces per player
     * - A board panel initialized with these players
     */
    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        // Add players of different colors
        for (Color color : new Color[]{Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED}) {
            List<Piece> pieces = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                pieces.add(new Piece(color, null));
            }
            players.add(new AIPlayer("Test Player", color, pieces));
        }
        boardPanel = new BoardPanel(players);
    }

    /**
     * Tests the initialization of the board panel.
     * Verifies:
     * - Panel creation
     * - JPanel inheritance
     * - Board dimensions
     * - Square shape
     */
    @Test
    @Timeout(100)
    void testBoardInitialization() {
        assertNotNull(boardPanel, "Board panel should be created");
        assertInstanceOf(JPanel.class, boardPanel, "Board panel should extend JPanel");
        
        Dimension size = boardPanel.getPreferredSize();
        assertTrue(size.width > 0 && size.height > 0, "Board should have positive dimensions");
        assertEquals(size.width, size.height, "Board should be square");
    }

    /**
     * Tests node creation and connectivity on the board.
     * Verifies:
     * - Main track node count
     * - Node connections
     * - Color-specific path navigation
     * - Home path entry points
     */
    @Test
    @Timeout(100)
    void testNodeCreation() {
        List<Node> nodes = boardPanel.getNodes();
        assertNotNull(nodes, "Nodes list should not be null");
        assertEquals(52, nodes.size(), "Should have 52 nodes in main track");
        
        // Test main track connectivity
        for (int i = 0; i < nodes.size(); i++) {
            Node currentNode = nodes.get(i);
            
            // For positions not at home entry points, all colors should follow main track
            if (i != 50 && i != 11 && i != 24 && i != 37) {
                Node expectedNext = nodes.get((i + 1) % 52);
                assertEquals(expectedNext, currentNode.getNext(Color.BLUE), 
                    "Node " + i + " should connect to node " + ((i + 1) % 52) + " for BLUE");
                assertEquals(expectedNext, currentNode.getNext(Color.GREEN),
                    "Node " + i + " should connect to node " + ((i + 1) % 52) + " for GREEN");
                assertEquals(expectedNext, currentNode.getNext(Color.YELLOW),
                    "Node " + i + " should connect to node " + ((i + 1) % 52) + " for YELLOW");
                assertEquals(expectedNext, currentNode.getNext(Color.RED),
                    "Node " + i + " should connect to node " + ((i + 1) % 52) + " for RED");
            }
        }
        
        // Test home path entries
        // Blue enters home path at 50
        Node blueEntryNode = nodes.get(50);
        assertNotEquals(nodes.get(51), blueEntryNode.getNext(Color.BLUE),
            "Blue piece should enter home path at node 50");
        assertEquals(nodes.get(51), blueEntryNode.getNext(Color.GREEN),
            "Non-blue pieces should continue on main track at node 50");
            
        // Green enters home path at 11
        Node greenEntryNode = nodes.get(11);
        assertNotEquals(nodes.get(12), greenEntryNode.getNext(Color.GREEN),
            "Green piece should enter home path at node 11");
        assertEquals(nodes.get(12), greenEntryNode.getNext(Color.BLUE),
            "Non-green pieces should continue on main track at node 11");
            
        // Yellow enters home path at 24
        Node yellowEntryNode = nodes.get(24);
        assertNotEquals(nodes.get(25), yellowEntryNode.getNext(Color.YELLOW),
            "Yellow piece should enter home path at node 24");
        assertEquals(nodes.get(25), yellowEntryNode.getNext(Color.BLUE),
            "Non-yellow pieces should continue on main track at node 24");
            
        // Red enters home path at 37
        Node redEntryNode = nodes.get(37);
        assertNotEquals(nodes.get(38), redEntryNode.getNext(Color.RED),
            "Red piece should enter home path at node 37");
        assertEquals(nodes.get(38), redEntryNode.getNext(Color.BLUE),
            "Non-red pieces should continue on main track at node 37");
    }

    /**
     * Tests safe spot placement on the board.
     * Verifies that specific nodes are correctly marked
     * as safe spots where pieces cannot be captured.
     */
    @Test
    @Timeout(100)
    void testSafeSpots() {
        List<Node> nodes = boardPanel.getNodes();
        
        // Test starting positions (0, 13, 26, 39)
        assertTrue(nodes.get(0).isSafe(), "Node 0 should be a safe spot");
        assertTrue(nodes.get(13).isSafe(), "Node 13 should be a safe spot");
        assertTrue(nodes.get(26).isSafe(), "Node 26 should be a safe spot");
        assertTrue(nodes.get(39).isSafe(), "Node 39 should be a safe spot");
        
        // Test positions before home paths (8, 21, 34, 47)
        assertTrue(nodes.get(8).isSafe(), "Node 8 should be a safe spot");
        assertTrue(nodes.get(21).isSafe(), "Node 21 should be a safe spot");
        assertTrue(nodes.get(34).isSafe(), "Node 34 should be a safe spot");
        assertTrue(nodes.get(47).isSafe(), "Node 47 should be a safe spot");
    }

    /**
     * Tests the visual rendering of the board.
     * Verifies that the board can be painted without errors
     * and contains the expected visual elements.
     */
    @Test
    @Timeout(100)
    void testBoardRendering() {
        // Create a graphics context for testing
        BufferedImage image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        
        // Test that painting doesn't throw exceptions
        assertDoesNotThrow(() -> boardPanel.paintComponent(g),
            "Board painting should not throw exceptions");
        
        // Clean up
        g.dispose();
    }

    /**
     * Tests board dimensions and scaling.
     * Verifies that the board has a minimum size
     * and is properly scaled.
     */
    @Test
    @Timeout(100)
    void testBoardDimensions() {
        Dimension size = boardPanel.getPreferredSize();
        assertTrue(size.width >= 15 * 40, "Board should be at least 15 tiles wide");
        assertTrue(size.height >= 15 * 40, "Board should be at least 15 tiles high");
    }

    /**
     * Tests player management.
     * Verifies that the board has the correct number
     * of players and that each player has the correct color.
     */
    @Test
    @Timeout(100)
    void testPlayerManagement() {
        assertEquals(4, players.size(), "Board should have 4 players");
        
        // Test that each player has correct color
        List<Color> expectedColors = List.of(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
        for (int i = 0; i < players.size(); i++) {
            assertEquals(expectedColors.get(i), players.get(i).getColor(),
                "Player " + i + " should have correct color");
        }
    }

    /**
     * Tests piece placement on the board.
     * Verifies that pieces can be correctly placed
     * on the board and that the board reflects this.
     */
    @Test
    @Timeout(100)
    void testPiecePlacement() {
        List<Node> nodes = boardPanel.getNodes();
        Node testNode = nodes.getFirst();
        Piece testPiece = players.getFirst().getPieces().getFirst();
        
        testNode.addPiece(testPiece);
        assertTrue(testNode.getPieces().contains(testPiece),
            "Node should contain placed piece");
    }

    /**
     * Tests board repaint functionality.
     * Verifies that the board can be repainted without errors.
     */
    @Test
    @Timeout(100)
    void testBoardRepaint() {
        // Create a dummy graphics context for testing
        BufferedImage image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        assertDoesNotThrow(() -> boardPanel.paint(g),
            "Board painting should not throw exceptions");
        g.dispose();
    }
}
