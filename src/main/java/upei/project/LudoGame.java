package upei.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class LudoGame extends JFrame {
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton rollButton;
    public int dieRoll;
    public int currentPlayerIndex = 0; // Player turn tracker
    final List<Player> players = new ArrayList<>();
    final Random random = new Random();
    public boolean isGameOver = false;

    public LudoGame() {
        setTitle("Ludo Game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create players with places
        List<Piece> bluePieces = createPieces(Color.BLUE);
        List<Piece> greenPieces = createPieces(Color.GREEN);
        List<Piece> yellowPieces = createPieces(Color.YELLOW);
        List<Piece> redPieces = createPieces(Color.RED);

        // Create players
        players.add(new HumanPlayer("Blue", Color.BLUE, bluePieces));  // Human
        players.add(new AIPlayer("Green", Color.GREEN, greenPieces)); // AI
        players.add(new AIPlayer("Yellow", Color.YELLOW, yellowPieces));  // Human
        players.add(new AIPlayer("Red", Color.RED, redPieces)); // AI

        // Initialize components
        boardPanel = new BoardPanel(players);
        statusLabel = new JLabel("Blue's turn! Roll the dice.");
        rollButton = new JButton("Roll Dice");

        // Roll button action
        rollButton.addActionListener(e -> {
            if (!isGameOver) {
                rollDice();
            } else {
                JOptionPane.showMessageDialog(this, "Game Over!");
            }
        });

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.NORTH);
        add(rollButton, BorderLayout.SOUTH);
    }

    private List<Piece> createPieces(Color color) {
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pieces.add(new Piece(color));
        }
        return pieces;
    }

    public void rollDice() {
        dieRoll = random.nextInt(6) + 1; // Roll 1-6
        Player currentPlayer = players.get(currentPlayerIndex);
        statusLabel.setText(currentPlayer.getName() + " rolled a " + dieRoll);

        // Make move based on player type
        currentPlayer.makeMove(dieRoll, players);

        // Update board and check if the game is over
        boardPanel.repaint();
        if (isGameOver()) {
            statusLabel.setText(currentPlayer.getName() + " wins!");
            isGameOver = true;
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            statusLabel.setText(players.get(currentPlayerIndex).getName() + "'s turn! Roll the dice.");
        }
    }

    // Check if the game is over (any player has won)
    public boolean isGameOver() {
        for (Player player : players) {
            if (checkWinner(player)) {
                return true; // A winner is found
            }
        }
        return false;
    }

    // Check if a player has won the game (i.e., moved all pieces to the finish)
    private boolean checkWinner(Player player) {
        // Implement this logic based on your game rules (e.g., checking if all pieces are home)
        for (Piece piece : player.getPieces()) {
            if (!piece.isAtHome()) {
                return false; // If any piece is not home, the player hasn't won
            }
        }
        return true; // All pieces are home, player has won
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LudoGame game = new LudoGame();
            game.setVisible(true);
        });
    }
}