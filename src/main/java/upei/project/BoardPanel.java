package upei.project;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

class BoardPanel extends JPanel {
    public static final int TILE_SIZE = 40;
    public static final int PIECE_SIZE = 30;
    static final Color BOARD_COLOR = Color.WHITE;
    static final Color GRID_COLOR = Color.LIGHT_GRAY;
    static final Color SAFE_SPOT_COLOR = new Color(220, 220, 220);
    
    static Node[] nodes;  // Array of all nodes on the board
    final List<Player> players;

    public BoardPanel(List<Player> players) {
        this.players = players;
        setPreferredSize(new Dimension(TILE_SIZE * 15, TILE_SIZE * 15));
        setBackground(BOARD_COLOR);
        initializeNodes();
    }

    /**
     * Initializes the board's nodes and their connections
     */
    private void initializeNodes() {
        nodes = new Node[76];  // 52 main path + 24 home path nodes (6 per color)
        
        // Create main path nodes
        for (int i = 0; i < 52; i++) {
            Point pos = getPositionCoordinates(i);
            boolean isSafe = isSafePosition(i);
            nodes[i] = new Node(i, pos.x, pos.y, isSafe);
        }

        // Connect main path nodes
        for (int i = 0; i < 52; i++) {
            nodes[i].setNext(nodes[(i + 1) % 52]);
        }

        // Create and connect home paths
        createHomePath(Color.BLUE, 52, 50);
        createHomePath(Color.GREEN, 58, 11);
        createHomePath(Color.YELLOW, 64, 24);
        createHomePath(Color.RED, 70, 37);
    }

    /**
     * Creates a home path for a specific color
     */
    private void createHomePath(Color color, int startIndex, int entryPosition) {
        // Create home path nodes
        for (int i = 0; i < 6; i++) {
            Point pos = getHomePathCoordinates(color, i);
            nodes[startIndex + i] = new Node(startIndex + i, pos.x, pos.y, true);
        }

        // Connect home path nodes
        for (int i = 0; i < 5; i++) {
            nodes[startIndex + i].setNext(nodes[startIndex + i + 1]);
        }

        // Connect main path to home path
        nodes[entryPosition].setHomePathNext(nodes[startIndex]);
    }

    /**
     * Gets the coordinates for a position on the main path
     */
    private Point getPositionCoordinates(int position) {
        int x, y;
        int boardSize = 15 * TILE_SIZE;
        
        // Bottom row (0-5)
        if (position <= 5) {
            x = (6 + position) * TILE_SIZE;
            y = 9 * TILE_SIZE;
        }
        // Right column (6-11)
        else if (position <= 11) {
            x = 14 * TILE_SIZE;
            y = (8 - (position - 6)) * TILE_SIZE;
        }
        // Top row (12-18)
        else if (position <= 18) {
            x = (14 - (position - 11)) * TILE_SIZE;
            y = 2 * TILE_SIZE;
        }
        // Left column up (19-24)
        else if (position <= 24) {
            x = 8 * TILE_SIZE;
            y = (2 + (position - 18)) * TILE_SIZE;
        }
        // Left row (25-31)
        else if (position <= 31) {
            x = 0;
            y = (8 - (position - 25)) * TILE_SIZE;
        }
        // Bottom row (32-37)
        else if (position <= 37) {
            x = (position - 31) * TILE_SIZE;
            y = 14 * TILE_SIZE;
        }
        // Right column up (38-43)
        else if (position <= 43) {
            x = 6 * TILE_SIZE;
            y = (14 - (position - 37)) * TILE_SIZE;
        }
        // Top row to start (44-51)
        else {
            x = (6 + (position - 43)) * TILE_SIZE;
            y = 8 * TILE_SIZE;
        }
        
        return new Point(x, y);
    }

    /**
     * Gets the coordinates for a position on a home path
     */
    private Point getHomePathCoordinates(Color color, int step) {
        int x, y;
        
        if (color == Color.BLUE) {
            x = 7 * TILE_SIZE;
            y = (9 - step) * TILE_SIZE;
        }
        else if (color == Color.GREEN) {
            x = (8 + step) * TILE_SIZE;
            y = 7 * TILE_SIZE;
        }
        else if (color == Color.YELLOW) {
            x = 7 * TILE_SIZE;
            y = (5 + step) * TILE_SIZE;
        }
        else { // RED
            x = (6 - step) * TILE_SIZE;
            y = 7 * TILE_SIZE;
        }
        
        return new Point(x, y);
    }

    /**
     * Checks if a position is a safe spot
     */
    private boolean isSafePosition(int position) {
        int[] safeSpots = {0, 8, 13, 21, 26, 34, 39, 47};
        for (int spot : safeSpots) {
            if (position == spot) return true;
        }
        return false;
    }

    /**
     * Gets a node at a specific position
     */
    public static Node getNodeAtPosition(int position) {
        if (position < 0 || position >= nodes.length) return null;
        return nodes[position];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawBoard(g2d);
        drawSafeSpots(g2d);
        drawPieces(g2d);
    }

    private void drawBoard(Graphics2D g) {
        int boardSize = TILE_SIZE * 15;

        // Draw grid
        g.setColor(GRID_COLOR);
        for (int i = 0; i <= 15; i++) {
            g.drawLine(0, i * TILE_SIZE, boardSize, i * TILE_SIZE);
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, boardSize);
        }

        // Draw home zones with transparency
        int zoneSize = TILE_SIZE * 6;
        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

        // Blue home (top-left)
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, zoneSize, zoneSize);

        // Green home (top-right)
        g.setColor(Color.GREEN);
        g.fillRect(TILE_SIZE * 9, 0, zoneSize, zoneSize);

        // Yellow home (bottom-left)
        g.setColor(Color.YELLOW);
        g.fillRect(0, TILE_SIZE * 9, zoneSize, zoneSize);

        // Red home (bottom-right)
        g.setColor(Color.RED);
        g.fillRect(TILE_SIZE * 9, TILE_SIZE * 9, zoneSize, zoneSize);

        g.setComposite(originalComposite);

        // Draw home zone borders
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(0, 0, zoneSize, zoneSize);
        g.drawRect(TILE_SIZE * 9, 0, zoneSize, zoneSize);
        g.drawRect(0, TILE_SIZE * 9, zoneSize, zoneSize);
        g.drawRect(TILE_SIZE * 9, TILE_SIZE * 9, zoneSize, zoneSize);
    }

    private void drawSafeSpots(Graphics2D g) {
        g.setColor(SAFE_SPOT_COLOR);
        for (int safeSpot : Player.SAFE_SPOTS) {
            int x = (safeSpot % 15) * TILE_SIZE;
            int y = (safeSpot / 15) * TILE_SIZE;
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            
            // Draw "SAFE" text
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("SAFE", x + 5, y + TILE_SIZE/2);
            g.setColor(SAFE_SPOT_COLOR);
        }
    }

    private void drawPieces(Graphics2D g) {
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                Point pos = piece.getDisplayPosition();
                if (pos != null) {
                    // Draw piece shadow
                    g.setColor(new Color(0, 0, 0, 50));
                    g.fillOval(pos.x + 2, pos.y + 2, PIECE_SIZE, PIECE_SIZE);
                    
                    // Draw piece
                    g.setColor(piece.getColor());
                    g.fillOval(pos.x, pos.y, PIECE_SIZE, PIECE_SIZE);
                    
                    // Draw piece border
                    g.setColor(piece.getColor().darker());
                    g.setStroke(new BasicStroke(2));
                    g.drawOval(pos.x, pos.y, PIECE_SIZE, PIECE_SIZE);
                }
            }
        }
    }
}
