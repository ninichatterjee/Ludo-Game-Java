package upei.project;

import java.util.Scanner;

/**
 * HumanPlayer class allowing human interaction for moves.
 */
public class HumanPlayer extends Player {
    private Scanner scanner;

    public HumanPlayer(String name, String color) {
        super(name, color);
        this.scanner = new Scanner(System.in);
    }

    @Override
    public int getNextMove() {
        System.out.println(name + " (" + color + "), enter your move (1-6): ");
        return scanner.nextInt();
    }
}
