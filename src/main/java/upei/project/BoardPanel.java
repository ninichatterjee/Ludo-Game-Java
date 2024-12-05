package upei.project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Graphical representation of the Ludo game board.
 * This panel manages both the visual display and the logical structure
 * of the game board, including node connections and piece positions.
 *
 * The board consists of:
 * - Main track (52 nodes, positions 0-51)
 * - Home stretches for each color
 * - Base areas for each player's pieces
 * - Safe spots where pieces cannot be captured
 *
 * Board layout:
 * - 15x15 grid of tiles
 * - Each tile is 40x40 pixels
 * - Pieces are 30x30 pixels with centered offset
 *
 * @author UPEI Project Team
 * @version 1.0
 */
public class BoardPanel extends JPanel {
    /** Size of each board tile in pixels */
    private static final int TILE_SIZE = 40;
    
    /** Size of each game piece in pixels */
    private static final int PIECE_SIZE = 30;
    
    /** Offset to center pieces within tiles */
    private static final float PIECE_OFFSET = (TILE_SIZE - PIECE_SIZE) / 2.0f;
    
    /** List of all nodes on the board */
    private final List<Node> nodes;
    
    /** List of players in the game */
    private List<Player> players;

    /**
     * Creates a new game board panel.
     * Initializes the board with nodes and their connections.
     *
     * @param players List of players in the game
     */
    public BoardPanel(List<Player> players) {
        this.players = players;
        this.nodes = new ArrayList<>();
        setPreferredSize(new Dimension(15 * TILE_SIZE, 15 * TILE_SIZE));
        initializeNodes();
    }

    /**
     * Updates the list of players and refreshes the display.
     *
     * @param players New list of players
     */
    public void initializePlayers(List<Player> players) {
        this.players = players;
        repaint();
    }

    /**
     * Initializes all nodes on the board and their connections.
     * Creates and connects:
     * - Main track nodes (0-51)
     * - Home stretch paths for each color
     * - Safe spots at strategic positions
     */
    private void initializeNodes() {
        // Initialize board nodes with their positions and coordinates
        // Main track nodes (0-51)
        for (int i = 0; i < 52; i++) {
            int x, y;
            // Calculate x,y coordinates based on position
            if (i < 13) {
                x = (6 + i) * TILE_SIZE;
                y = 6 * TILE_SIZE;
            } else if (i < 26) {
                x = 14 * TILE_SIZE;
                y = ((i - 13) + 6) * TILE_SIZE;
            } else if (i < 39) {
                x = (14 - (i - 26)) * TILE_SIZE;
                y = 14 * TILE_SIZE;
            } else {
                x = 6 * TILE_SIZE;
                y = (14 - (i - 39)) * TILE_SIZE;
            }
            nodes.add(new Node(i, x, y, isNodeSafe(i)));
        }

        // First connect all nodes in the main track
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setNext(nodes.get((i + 1) % nodes.size()));
        }

        // Then add home stretch paths
        addHomeStretch(Color.BLUE, 50, 6, 7, true);
        addHomeStretch(Color.GREEN, 11, 8, 6, false);
        addHomeStretch(Color.YELLOW, 24, 14, 8, true);
        addHomeStretch(Color.RED, 37, 7, 14, false);
    }

    /**
     * Creates and connects a home stretch path for a specific color.
     * Each home stretch consists of 5 nodes leading to the final home position.
     *
     * @param color Color of the home stretch
     * @param startPos Position on main track where home stretch begins
     * @param startX X-coordinate of first home stretch node
     * @param startY Y-coordinate of first home stretch node
     * @param horizontal true if home stretch extends horizontally, false for vertical
     */
    private void addHomeStretch(Color color, int startPos, int startX, int startY, boolean horizontal) {
        Node start = nodes.get(startPos);
        Node prev = start;
        
        // Create and connect home stretch nodes
        Node[] homeNodes = new Node[5];
        for (int i = 0; i < 5; i++) {
            int x = horizontal ? (startX + i) * TILE_SIZE : startX * TILE_SIZE;
            int y = horizontal ? startY * TILE_SIZE : (startY + i) * TILE_SIZE;
            homeNodes[i] = new Node(300 + startPos * 5 + i, x, y, true);
            if (i > 0) {
                homeNodes[i - 1].setNext(homeNodes[i]);
            }
        }
        
        // Set the home path next for the entry node
        // This preserves the main track connection set earlier
        start.setHomePathNext(homeNodes[0]);
    }

    /**
     * Determines if a node position is a safe spot.
     * Safe spots are positions where pieces cannot be captured.
     * These include:
     * - Starting positions (0, 13, 26, 39)
     * - Positions before home stretches (8, 21, 34, 47)
     *
     * @param position Node position to check
     * @return true if the position is a safe spot
     */
    private boolean isNodeSafe(int position) {
        // Define safe spots on the board
        return position == 0 || position == 13 || position == 26 || position == 39 ||
               position == 8 || position == 21 || position == 34 || position == 47;
    }

    /**
     * Paints the game board and all pieces.
     * Overrides JPanel's paintComponent to provide custom rendering.
     *
     * @param g Graphics context for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
    }

    /**
     * Draws the game board structure.
     * Includes:
     * - White background
     * - Gray tiles for regular spots
     * - Orange tiles for safe spots
     * - Black borders around tiles
     *
     * @param g Graphics context for painting
     */
    private void drawBoard(Graphics g) {
        // Draw board background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw nodes
        for (Node node : nodes) {
            g.setColor(node.isSafe() ? Color.ORANGE : Color.LIGHT_GRAY);
            g.fillRect(node.getX(), node.getY(), TILE_SIZE, TILE_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(node.getX(), node.getY(), TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Draws all game pieces on the board.
     * Handles pieces in different states:
     * - In base (starting area)
     * - On main track
     * - In home stretch
     * - Reached final home
     *
     * Each piece is drawn with its color and number for identification.
     *
     * @param g Graphics context for painting
     */
    private void drawPieces(Graphics g) {
        for (Player player : players) {
            g.setColor(player.getColor());
            int baseX, baseY;
            
            // Calculate base coordinates based on color
            if (player.getColor() == Color.BLUE) {
                baseX = 2 * TILE_SIZE;
                baseY = 2 * TILE_SIZE;
            } else if (player.getColor() == Color.GREEN) {
                baseX = 2 * TILE_SIZE;
                baseY = 9 * TILE_SIZE;
            } else if (player.getColor() == Color.YELLOW) {
                baseX = 9 * TILE_SIZE;
                baseY = 2 * TILE_SIZE;
            } else { // RED
                baseX = 9 * TILE_SIZE;
                baseY = 9 * TILE_SIZE;
            }
            
            // Draw each piece
            int pieceInBase = 0;
            int pieceInHome = 0;
            int pieceNumber = 1;
            for (Piece piece : player.getPieces()) {
                int pieceX, pieceY;
                
                if (piece.isAtHome()) {
                    // Draw piece in base area
                    int row = pieceInBase / 2;
                    int col = pieceInBase % 2;
                    pieceX = baseX + col * TILE_SIZE + (int)PIECE_OFFSET;
                    pieceY = baseY + row * TILE_SIZE + (int)PIECE_OFFSET;
                    pieceInBase++;
                } else if (piece.hasReachedHome()) {
                    // Draw piece in home area
                    if (player.getColor() == Color.BLUE) {
                        pieceX = 7 * TILE_SIZE + (int)PIECE_OFFSET;
                        pieceY = (7 + pieceInHome) * TILE_SIZE + (int)PIECE_OFFSET;
                    } else if (player.getColor() == Color.GREEN) {
                        pieceX = (7 + pieceInHome) * TILE_SIZE + (int)PIECE_OFFSET;
                        pieceY = 7 * TILE_SIZE + (int)PIECE_OFFSET;
                    } else if (player.getColor() == Color.YELLOW) {
                        pieceX = 7 * TILE_SIZE + (int)PIECE_OFFSET;
                        pieceY = (7 - pieceInHome) * TILE_SIZE + (int)PIECE_OFFSET;
                    } else { // RED
                        pieceX = (7 - pieceInHome) * TILE_SIZE + (int)PIECE_OFFSET;
                        pieceY = 7 * TILE_SIZE + (int)PIECE_OFFSET;
                    }
                    pieceInHome++;
                } else {
                    // Draw piece on track
                    Node node = piece.getCurrentNode();
                    if (node != null) {
                        pieceX = node.getX() + (int)PIECE_OFFSET;
                        pieceY = node.getY() + (int)PIECE_OFFSET;
                    } else {
                        continue; // Skip if no valid position
                    }
                }
                
                // Draw the piece
                g.fillOval(pieceX, pieceY, PIECE_SIZE, PIECE_SIZE);
                
                // Draw piece number
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics metrics = g.getFontMetrics();
                String number = String.valueOf(pieceNumber);
                int textX = pieceX + (PIECE_SIZE - metrics.stringWidth(number)) / 2;
                int textY = pieceY + ((PIECE_SIZE + metrics.getHeight()) / 2) - 2;
                g.drawString(number, textX, textY);
                g.setColor(player.getColor());
                
                pieceNumber++;
            }
        }
    }

    /**
     * Finds a node at a specific position on the board.
     *
     * @param position Position to find (0-51 for main track, 300+ for home stretches)
     * @return Node at the specified position, or null if not found
     */
    public Node getNodeAtPosition(int position) {
        for (Node node : nodes) {
            if (node.getPosition() == position) {
                return node;
            }
        }
        return null;
    }

    /**
     * Gets all nodes on the board.
     * Includes both main track and home stretch nodes.
     *
     * @return List of all board nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }
}
