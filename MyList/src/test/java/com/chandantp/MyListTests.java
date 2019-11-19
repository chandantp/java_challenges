package com.chandantp;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyListTests {

    @Test
    public void testEmptyListChecks() {
        MyList<Integer> myList = MyList.newList();
        assertTrue(myList.isEmpty());
        assertEquals(0, myList.size() );
        assertNull(myList.remove());
    }

    @Test
    public void testListAddOps() {
        MyList<Integer> myList = MyList.newList();

        myList.add(1);
        assertFalse(myList.isEmpty());
        assertEquals(1, myList.size());

        myList.add(2);
        assertFalse(myList.isEmpty());
        assertEquals(2, myList.size());
    }

    @Test
    public void testListRemoveOps() {
        MyList<String> myList = MyList.newList();

        myList.add("a");
        myList.add("b");
        myList.add("c");

        assertEquals("a", myList.remove());
        assertEquals("b", myList.remove());
        assertEquals("c", myList.remove());

        assertTrue(myList.isEmpty());
    }

    @Test
    public void testEmptyListToString() {
        MyList<Integer> myList = MyList.newList();
        assertEquals("()", myList.toString());
    }

    @Test
    public void testNonEmptyListToString() {
        MyList<Integer> myList = MyList.newList();

        myList.add(1);
        myList.add(2);
        myList.add(3);

        assertEquals("(1, 2, 3)", myList.toString());
    }

    @Test
    public void testReverseList() {
        MyList<Integer> myList = MyList.newList();

        myList.add(1);
        myList.add(2);
        myList.add(3);

        assertEquals("(1, 2, 3)", myList.toString());
        myList.reverse();
        assertEquals("(3, 2, 1)", myList.toString());

        myList.add(4);
        myList.add(5);
        assertEquals("(3, 2, 1, 4, 5)", myList.toString());
    }
}

