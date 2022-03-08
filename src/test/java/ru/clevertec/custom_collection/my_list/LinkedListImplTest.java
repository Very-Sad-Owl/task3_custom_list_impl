package ru.clevertec.custom_collection.my_list;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.clevertec.custom_collection.my_list.LinkedListImpl;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListImplTest {
    @Test
    void noArgsConstructor_noArgs_emptyList() {
        List<String> list = LinkedListImpl.of();

        assertTrue(list.isEmpty());
    }

    @Test
    void add_3elements_sizeIs3() {
        List<String> list = LinkedListImpl.of();

        list.add("123");
        list.add("qwerty");
        list.add(null);

        assertEquals(3, list.size());
    }

    @Test
    void add_addElementAtPos_posIsElement() {
        List<String> list = LinkedListImpl.of("123", "qwerty", "dummy");

        list.add(1, "test");

        assertEquals("test", list.get(1));
    }

    @Test
    void addFirst_element_elementIsFirst(){
        Deque<String> deque = new LinkedListImpl<>();
        deque.add("123");
        deque.add("qwerty");

        deque.addFirst("first");

        assertEquals("first", deque.getFirst());
    }

    @Test
    void addLast_element_elementIsLast(){
        Deque<String> deque = new LinkedListImpl<>();
        deque.add("123");
        deque.add("qwerty");

        deque.addLast("last");

        assertEquals("last", deque.getLast());
    }

    @Test
    void removeFirst_element_removedElement(){
        Deque<String> deque = new LinkedListImpl<>();
        deque.add("123");
        deque.add("qwerty");
        deque.add("last");

        String expected = "123";

        String actual = deque.removeFirst();

        assertEquals(expected, actual);
    }

    @Test
    void removeLast_element_removedElement(){
        Deque<String> deque = new LinkedListImpl<>();
        deque.add("123");
        deque.add("qwerty");
        deque.add("last");

        String expected = "last";

        String actual = deque.removeLast();

        assertEquals(expected, actual);
    }


    @Test
    void addAll_elements_elementsAddedInTail() {
        List<String> list = LinkedListImpl.of("123", "234", "345");

        list.addAll(LinkedListImpl.of("test", "dummy"));

        assertAll(
                () -> assertEquals("123", list.get(0)),
                () -> assertEquals("234", list.get(1)),
                () -> assertEquals("345", list.get(2)),
                () -> assertEquals("test", list.get(3)),
                () -> assertEquals("dummy", list.get(4))
        );
    }

    @Test
    void addAllFromIndex_elements_elementsInCorrectOrder() {
        List<String> list = LinkedListImpl.of("123", "234", "345");

        list.addAll(1, LinkedListImpl.of("test", "dummy"));

        assertAll(
                () -> assertEquals("123", list.get(0)),
                () -> assertEquals("test", list.get(1)),
                () -> assertEquals("dummy", list.get(2)),
                () -> assertEquals("234", list.get(3)),
                () -> assertEquals("345", list.get(4))
        );
    }

    @Test
    void set_elementAtPos1_elementInPos1() {
        List<String> list = LinkedListImpl.of("123", "234", "345");

        list.set(1, "test");

        assertEquals("test", list.get(1));
    }

    @Test
    void contains_existingElement_true() {
        List<String> list = LinkedListImpl.of("123", "qwerty", null);

        assertTrue(list.contains("qwerty"));
    }

    @Test
    void contains_nonExistingElement_false() {
        List<String> list = LinkedListImpl.of("123", "qwerty", null);

        assertFalse(list.contains("test"));
    }

    @Test
    void indexOf_existingElement_index() {
        List<String> list = LinkedListImpl.of("test", "123", "qwerty");

        assertEquals(1, list.indexOf("123"));
    }

    @Test
    void indexOf_existingElement_neg1() {
        List<String> list = LinkedListImpl.of("123", "qwerty", "test");

        assertEquals(list.indexOf("dummy"), -1);
    }

    @Test
    void remove_existingElement_sizeDecreasedBy1() {
        List<String> list = LinkedListImpl.of("123", "qwerty", null);

        list.remove("123");

        assertEquals(2, list.size());
    }

    @Test
    void remove_existingElement_removedElement() {
        List<String> list = LinkedListImpl.of("123", "qwerty", "test");

        list.remove(1);

        assertNotEquals("qwerty", list.get(1));
    }
}