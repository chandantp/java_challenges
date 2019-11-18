package com.chandantp;

public class MyList<T> {

    private class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }

        Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
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
        Node e = new Node<T>(data);

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
}
