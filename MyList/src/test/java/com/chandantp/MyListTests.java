package com.chandantp;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyListTests {

    @Test
    public void testEmptyListChecks() {
        MyList<Integer> myList = MyList.newList();
        assertTrue(myList.isEmpty());
        assertEquals(myList.size(), 0);
        assertNull(myList.remove());
    }

    @Test
    public void testListAddOps() {
        MyList<Integer> myList = MyList.newList();

        myList.add(1);
        assertFalse(myList.isEmpty());
        assertEquals(myList.size(), 1);

        myList.add(2);
        assertFalse(myList.isEmpty());
        assertEquals(myList.size(), 2);
    }

    @Test
    public void testListRemoveOps() {
        MyList<String> myList = MyList.newList();

        myList.add("a");
        myList.add("b");
        myList.add("c");
        assertEquals(myList.size(), 3);

        assertEquals(myList.remove(), "a");
        assertEquals(myList.remove(), "b");
        assertEquals(myList.remove(), "c");

        assertTrue(myList.isEmpty());
    }
}

