package upei.project;

import upei.project.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test cases for AIPlayer and HumanPlayer behaviors.
 */
public class PlayerTests {

    /**
     * Test AI aggressive move to send opponent back.
     */
    @Test
    public void testAIAggressiveMove() {
        Piece aiPiece = new Piece(Color.RED);
        Piece opponentPiece = new Piece(Color.BLUE);
        aiPiece.move(10);
        opponentPiece.move(13);

        List<Piece> aiPieces = List.of(aiPiece);
        List<Piece> opponentPieces = List.of(opponentPiece);

        Player aiPlayer = new AIPlayer("AI", Color.RED, aiPieces);
        Player opponent = new AIPlayer("Opponent", Color.BLUE, opponentPieces);

        aiPlayer.makeMove(3, List.of(aiPlayer, opponent));
        assertEquals(13, aiPiece.getPosition(), "AI should move aggressively to send opponent back.");
    }

    /**
     * Test AI safe move logic.
     */
    @Test
    public void testAISafeMove() {
        Piece aiPiece = new Piece(Color.RED);
        Piece opponentPiece = new Piece(Color.BLUE);
        aiPiece.move(5);
        opponentPiece.move(8);

        List<Piece> aiPieces = List.of(aiPiece);
        List<Piece> opponentPieces = List.of(opponentPiece);

        Player aiPlayer = new AIPlayer("AI", Color.RED, aiPieces);
        Player opponent = new AIPlayer("Opponent", Color.BLUE, opponentPieces);

        aiPlayer.makeMove(2, List.of(aiPlayer, opponent));
        assertEquals(7, aiPiece.getPosition(), "AI should make a safe move.");
    }
}

