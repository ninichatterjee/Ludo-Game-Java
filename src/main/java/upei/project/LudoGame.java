package upei.project;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LudoGame extends JFrame {
    private final BoardPanel boardPanel;
    private final JLabel statusLabel;
    private final JButton rollButton;
    private int dieRoll;
    private int currentPlayerIndex = 0;
    private final List<Player> players = new ArrayList<>();
    private final Random random = new Random();
    private boolean isGameOver = false;

    public LudoGame() {
        setTitle("Ludo Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize board layout
        BoardPanel.initializeNodes();

        // Create players with pieces
        List<Piece> bluePieces = createPieces(Color.BLUE);
        List<Piece> greenPieces = createPieces(Color.GREEN);
        List<Piece> yellowPieces = createPieces(Color.YELLOW);
        List<Piece> redPieces = createPieces(Color.RED);

        // Create players
        players.add(new HumanPlayer("Blue", Color.BLUE, bluePieces));  // Human
        players.add(new AIPlayer("Green", Color.GREEN, greenPieces));  // AI
        players.add(new AIPlayer("Yellow", Color.YELLOW, yellowPieces));  // AI
        players.add(new AIPlayer("Red", Color.RED, redPieces));  // AI

        // Initialize components
        boardPanel = new BoardPanel(players);
        statusLabel = new JLabel("Blue's turn! Roll the dice.");
        rollButton = new JButton("Roll Dice");

        // Style components
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        rollButton.setFont(new Font("Arial", Font.BOLD, 14));
        rollButton.setPreferredSize(new Dimension(120, 40));
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rollButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Roll button action
        rollButton.addActionListener(e -> {
            if (!isGameOver) {
                rollDice();
            } else {
                JOptionPane.showMessageDialog(this, "Game Over! " + 
                    players.get(currentPlayerIndex).getName() + " wins!");
            }
        });

        // Add components
        add(statusLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Pack and center
        pack();
        setLocationRelativeTo(null);
    }

    private List<Piece> createPieces(Color color) {
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pieces.add(new Piece(color));
        }
        return pieces;
    }

    private void rollDice() {
        Player currentPlayer = players.get(currentPlayerIndex);
        dieRoll = random.nextInt(6) + 1;
        statusLabel.setText(currentPlayer.getName() + " rolled a " + dieRoll);
        
        // Disable roll button during move
        rollButton.setEnabled(false);
        
        // Use SwingWorker to handle AI moves without freezing UI
        if (!currentPlayer.isHuman()) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    try {
                        Thread.sleep(1000); // Add slight delay for AI moves
                    } catch (InterruptedException ignored) {}
                    currentPlayer.makeMove(dieRoll, players);
                    return null;
                }

                @Override
                protected void done() {
                    handlePostMove();
                }
            }.execute();
        } else {
            currentPlayer.makeMove(dieRoll, players);
            handlePostMove();
        }
    }

    private void handlePostMove() {
        // Update board
        boardPanel.repaint();
        
        // Check if game is over
        if (checkWinner(players.get(currentPlayerIndex))) {
            isGameOver = true;
            statusLabel.setText(players.get(currentPlayerIndex).getName() + " wins!");
            rollButton.setText("New Game");
        } else {
            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            Player nextPlayer = players.get(currentPlayerIndex);
            statusLabel.setText(nextPlayer.getName() + "'s turn! Roll the dice.");
        }
        
        // Re-enable roll button
        rollButton.setEnabled(true);
    }

    private boolean checkWinner(Player player) {
        // A player wins when all their pieces reach the finish (position 52)
        for (Piece piece : player.getPieces()) {
            if (piece.getPosition() < Player.BOARD_SIZE - 1) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LudoGame game = new LudoGame();
            game.setVisible(true);
        });
    }
}