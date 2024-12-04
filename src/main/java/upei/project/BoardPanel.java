package upei.project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BoardPanel handles the graphical representation of the Ludo game board
 */
public class BoardPanel extends JPanel {
    private static final int TILE_SIZE = 40;
    private static final int PIECE_SIZE = 30;
    private static final float PIECE_OFFSET = (TILE_SIZE - PIECE_SIZE) / 2.0f;
    private final List<Node> nodes;
    private final List<Player> players;

    public BoardPanel(List<Player> players) {
        this.players = players;
        this.nodes = new ArrayList<>();
        setPreferredSize(new Dimension(15 * TILE_SIZE, 15 * TILE_SIZE));
        initializeNodes();
    }

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

        // Connect nodes
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setNext(nodes.get((i + 1) % nodes.size()));
        }

        // Add home stretch paths
        addHomeStretch(Color.BLUE, 50, 6, 7, true);
        addHomeStretch(Color.GREEN, 11, 8, 6, false);
        addHomeStretch(Color.YELLOW, 24, 14, 8, true);
        addHomeStretch(Color.RED, 37, 7, 14, false);
    }

    private void addHomeStretch(Color color, int startPos, int startX, int startY, boolean horizontal) {
        Node start = nodes.get(startPos);
        Node prev = start;
        
        for (int i = 0; i < 5; i++) {
            int x = horizontal ? (startX + i) * TILE_SIZE : startX * TILE_SIZE;
            int y = horizontal ? startY * TILE_SIZE : (startY + i) * TILE_SIZE;
            Node homeNode = new Node(52 + startPos * 5 + i, x, y, true);
            prev.setHomePathNext(homeNode);
            prev = homeNode;
        }
    }

    private boolean isNodeSafe(int position) {
        // Define safe spots on the board
        return position == 0 || position == 13 || position == 26 || position == 39 ||
               position == 8 || position == 21 || position == 34 || position == 47;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
    }

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

    private void drawPieces(Graphics g) {
        for (Player player : players) {
            g.setColor(player.getColor());
            for (Piece piece : player.getPieces()) {
                if (!piece.isAtHome() && !piece.hasReachedHome()) {
                    Node node = piece.getCurrentNode();
                    g.fillOval(
                        (int)(node.getX() + PIECE_OFFSET),
                        (int)(node.getY() + PIECE_OFFSET),
                        PIECE_SIZE,
                        PIECE_SIZE
                    );
                }
            }
        }
    }

    /**
     * Gets a node at the specified position
     */
    public Node getNodeAtPosition(int position) {
        for (Node node : nodes) {
            if (node.getPosition() == position) {
                return node;
            }
        }
        return null;
    }
}
