package upei.project;

import java.awt.*;
import java.util.Scanner;
import java.util.Random;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * HumanPlayer class allowing human interaction for moves.
 */
public class HumanPlayer extends Player {

    public HumanPlayer(String name, Color color, List<Piece> pieces) {
        super(name, color, pieces);
    }

    @Override
    public void makeMove(int dieRoll, List<Player> allPlayers) {
        Piece chosenPiece = null;

        while (chosenPiece == null) {
            String input = JOptionPane.showInputDialog(null, "Enter the piece number to move (1 -" + pieces.size() + ")");

            try {
                int pieceIndex = Integer.parseInt(input) - 1;
                if (pieceIndex >= 0 && pieceIndex < pieces.size()) {
                    chosenPiece = pieces.get(pieceIndex);
                }
                else JOptionPane.showMessageDialog(null, "Invalid piece number.");

            } catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number!");
            }
        }

        chosenPiece.move(dieRoll);
    }

    @Override
    public boolean isHuman() { return true; }
}
