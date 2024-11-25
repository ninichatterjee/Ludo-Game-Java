package upei.project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import upei.project.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Ludo game components.
 */
public class LudoTests {

    /**
     * Test the GamePath class initialization.
     */
    @Test
    public void testGamePathInitialization() {
        GamePath path = new GamePath(10);
        Node head = path.getHead();
        assertNotNull(head, "Head node should not be null.");

        int expectedPosition = 1;
        while (head != null) {
            assertEquals(expectedPosition, head.getPosition());
            head = head.getNext();
            expectedPosition++;
        }
    }

    /**
     * Test AIPlayer move generation.
     */
    @Test
    public void testAIPlayerMove() {
        AIPlayer ai = new AIPlayer("Bot", "Red");
        int move = ai.getNextMove();
        assertTrue(move >= 1 && move <= 6, "AI move should be between 1 and 6.");
    }
}

