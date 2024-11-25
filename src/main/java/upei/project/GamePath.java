package upei.project;

/**
 * Linked list representing the path on the board.
 */
public class GamePath {

    private Node head;

    public GamePath(int size) {
        head = new Node(1);
        Node current = head;
        for (int i = 2; i <= size; i++) {
            current.setNext(new Node(i));
            current = current.getNext();
        }
    }

    public Node getHead() {
        return head;
    }
}
