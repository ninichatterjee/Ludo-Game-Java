package upei.project;

import javax.swing.*;

/**
 * Main entry point for the Ludo game application.
 * This class initializes and launches the graphical user interface for the Ludo game.
 * It sets up the system look and feel and displays initial game instructions to the player.
 *
 * @author UPEI Project Team
 * @version 1.0
 */
public class Main {
    /**
     * The main method that starts the Ludo game application.
     * Creates the game window and displays it on the Event Dispatch Thread to ensure
     * thread safety in Swing operations. Shows a welcome message with game instructions
     * to help new players understand the rules.
     *
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Ensure the GUI is created on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for native platform appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Initialize the main game window
            LudoGame game = new LudoGame();
            game.setVisible(true);

            // Display comprehensive welcome message with game rules and instructions
            JOptionPane.showMessageDialog(game,
                "Welcome to Ludo!\n\n" +
                "Game Rules:\n" +
                "1. Roll the die by clicking the 'Roll Dice' button\n" +
                "2. Select a piece to move when prompted\n" +
                "3. Get all your pieces to home to win!\n\n" +
                "Special Rules:\n" +
                "- Roll a 6 to move a piece out of base\n" +
                "- Land on an opponent's piece to send it back to base\n" +
                "- Safe spots protect pieces from capture\n\n" +
                "You are playing as Blue. Good luck!",
                "Game Instructions",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
