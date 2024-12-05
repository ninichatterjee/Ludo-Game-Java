package upei.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main game class that manages the Ludo game state and user interface.
 * This class is responsible for:
 * - Creating and managing the game window
 * - Initializing players (both human and AI)
 * - Managing game turns and dice rolls
 * - Handling game state transitions
 * - Providing user feedback through the UI
 *
 * The game supports one human player (Blue) and three AI players
 * (Green, Yellow, and Red) with customizable strategies.
 *
 * @author UPEI Project Team
 * @version 1.0
 */
public class LudoGame extends JFrame {
    /** Logger for game events and errors */
    private static final Logger LOGGER = Logger.getLogger(LudoGame.class.getName());
    
    /** List of all players in the game */
    private final List<Player> players;
    
    /** Panel displaying the game board */
    private final BoardPanel boardPanel;
    
    /** Index of the current player's turn */
    private int currentPlayerIndex = 0;
    
    /** Current dice roll value */
    private int dieRoll;
    
    /** Button for rolling the dice */
    private final JButton rollButton;
    
    /** Label displaying game status messages */
    private final JLabel statusLabel;
    
    /** Flag indicating if the game has ended */
    private boolean isGameOver = false;
    
    /** Random number generator for dice rolls */
    private final Random random = new Random();

    /**
     * Creates and initializes a new Ludo game.
     * Sets up the game window, board, players, and UI components.
     */
    public LudoGame() {
        setTitle("Ludo Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        players = new ArrayList<>();
        boardPanel = new BoardPanel(players);
        initializePlayers();

        add(boardPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Blue's turn! Roll the dice.");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(statusLabel, BorderLayout.NORTH);

        rollButton = new JButton("Roll Dice");
        rollButton.setBackground(new Color(100, 200, 100));
        rollButton.setFont(new Font("Arial", Font.BOLD, 14));
        rollButton.setPreferredSize(new Dimension(120, 40));
        rollButton.addActionListener(this::rollDice);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rollButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(buttonPanel, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(800, 800));
        setResizable(true);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Initializes all players in the game.
     * Creates one human player (Blue) and three AI players with different colors.
     * Sets up player relationships and board references.
     */
    private void initializePlayers() {
        // Create human player (Blue)
        players.add(new HumanPlayer("Blue", Color.BLUE, boardPanel, this));

        // Create AI players
        players.add(new AIPlayer("Green", Color.GREEN, boardPanel));
        players.add(new AIPlayer("Yellow", Color.YELLOW, boardPanel));
        players.add(new AIPlayer("Red", Color.RED, boardPanel));

        // Set the list of all players for each player
        for (Player player : players) {
            player.setAllPlayers(players);
        }
    }

    /**
     * Handles the dice roll action.
     * Generates a random roll, updates the UI, and triggers the current player's move.
     * Also handles game over conditions and error cases.
     *
     * @param evt The action event from the roll button
     */
    private void rollDice(ActionEvent evt) {
        try {
            if (isGameOver) {
                JOptionPane.showMessageDialog(this,
                        "Game Over! " + players.get(currentPlayerIndex).getName() + " wins!");
                return;
            }

            if (!players.get(currentPlayerIndex).isHuman()) {
                return; // Only allow human players to roll
            }

            dieRoll = random.nextInt(6) + 1;
            statusLabel.setText(players.get(currentPlayerIndex).getName() + " rolled a " + dieRoll);

            rollButton.setEnabled(false);
            makeMove();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during dice roll", e);
            JOptionPane.showMessageDialog(this,
                    "An error occurred while rolling the dice: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            rollButton.setEnabled(true);
        }
    }

    /**
     * Triggers the current player's move based on the dice roll.
     * Updates the game state, board, and UI accordingly.
     * Handles game over conditions and error cases.
     */
    public void makeMove() {
        Player currentPlayer = players.get(currentPlayerIndex);
        try {
            currentPlayer.makeMove(dieRoll, players);
            boardPanel.repaint();

            if (currentPlayer.hasWon()) {
                isGameOver = true;
                JOptionPane.showMessageDialog(this,
                        currentPlayer.getName() + " has won the game!",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            // If next player is AI, make AI move
            if (!players.get(currentPlayerIndex).isHuman()) {
                dieRoll = random.nextInt(6) + 1;
                statusLabel.setText(players.get(currentPlayerIndex).getName() + " rolled a " + dieRoll);
                
                // Create and show an "OK" button dialog
                JButton okButton = new JButton("OK");
                JDialog dialog = new JDialog(this, "AI Move", true);
                dialog.setLayout(new FlowLayout());
                dialog.add(new JLabel(statusLabel.getText()));
                dialog.add(okButton);
                dialog.setSize(300, 100);
                dialog.setLocationRelativeTo(this);
                
                okButton.addActionListener(e -> {
                    dialog.dispose();
                    makeMove();  // Continue with the next move after OK is clicked
                });
                
                dialog.setVisible(true);  // This will block until OK is clicked
                return;  // Return here to prevent immediate recursive call
            } else {
                statusLabel.setText(players.get(currentPlayerIndex).getName() + "'s turn! Roll the dice.");
                rollButton.setEnabled(true);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during player move", e);
            JOptionPane.showMessageDialog(this,
                    "An error occurred during the move: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            rollButton.setEnabled(true);
        }
    }

    /**
     * Main entry point for the Ludo game application.
     * Creates and displays the game window.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LudoGame().setVisible(true);
        });
    }
}