package upei.project;

/**
 * Node class representing a single position on the board.
 */
public class Node {
    private int position;
    private Node next;

    public Node(int position) {
        this.position = position;
        this.next = null;
    }

    public int getPosition() {
        return position;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}