package upei.project;

import javax.swing.*;

/**
 * Main class to run the Ludo game
 */
public class Main {
    public static void main(String[] args) {
        // Ensure the GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create and show the game
            LudoGame game = new LudoGame();
            game.setVisible(true);

            // Display welcome message with game instructions
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
