package upei.project;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class BoardPanel extends JPanel {
    private final List<Player> players;

    public BoardPanel(List<Player> players) {
        this.players = players;
        setPreferredSize(new Dimension(500, 500));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
    }

    private void drawBoard(Graphics g) {
        int tileSize = 40;
        int boardSize = tileSize * 15;

        // Draw grid
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 15; i++) {
            g.drawLine(0, i * tileSize, boardSize, i * tileSize);
            g.drawLine(i * tileSize, 0, i * tileSize, boardSize);
        }

        // Draw home zones
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, tileSize * 6, tileSize * 6);

        g.setColor(Color.GREEN);
        g.fillRect(tileSize * 9, 0, tileSize * 6, tileSize * 6);

        g.setColor(Color.YELLOW);
        g.fillRect(0, tileSize * 9, tileSize * 6, tileSize * 6);

        g.setColor(Color.RED);
        g.fillRect(tileSize * 9, tileSize * 9, tileSize * 6, tileSize * 6);
    }

    private void drawPieces(Graphics g) {
        int tileSize = 40;

        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                if (piece.isAtHome()) {
                    continue; // Skip pieces in the base
                }
                int pos = piece.getPosition();
                int x = (pos % 15) * tileSize + tileSize / 4;
                int y = (pos / 15) * tileSize + tileSize / 4;

                g.setColor(piece.getColor());
                g.fillOval(x, y, tileSize / 2, tileSize / 2);
            }
        }
    }
}
