package com.chandantp;

public class MyList<T> {

    private class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    Node<T> first = null;
    Node<T> last = null;
    int count = 0;

    private MyList() {}

    public static <T> MyList<T> newList() {
        return new MyList<T>();
    }

    boolean isEmpty() {
        return (first == null);
    }

    int size() {
        return count;
    }

    void add(T data) {
        Node<T> e = new Node<T>(data);

        if (isEmpty()) {
            first = e;
            last = e;
        } else {
            last.next = e;
            last = e;
        }
        count++;
    }

    T remove() {
        if (isEmpty()) {
            return null;
        }

        Node<T> e = first;
        first = first.next;
        count--;

        return e.data;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("(");
        Node<T> curr = first;
        while (curr != null) {
            if (buf.length() != 1) buf.append(", ");
            buf.append(curr.data);
            curr = curr.next;
        }
        buf.append(")");
        return buf.toString();
    }

    public void reverse() {
        if (!isEmpty()) {
            last = first;
            first = reverseList(first, null);
        }
    }

    private Node<T> reverseList(Node<T> curr, Node<T> prev) {
        Node<T> next = curr.next;
        curr.next = prev;
        return next != null ? reverseList(next, curr) : curr;
    }
}
