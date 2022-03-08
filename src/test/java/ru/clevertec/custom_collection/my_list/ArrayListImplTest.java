package ru.clevertec.custom_collection.my_list;

import org.junit.jupiter.api.Test;
import ru.clevertec.custom_collection.my_list.ArrayListImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArrayListImplTest {

    @Test
    void noArgsConstructor_empty_true() {
        List<String> list = new ArrayListImpl();

        assertTrue(list.isEmpty());
    }

    @Test
    void size_3_true() {
        List<String> list = new ArrayListImpl();

        list.add("123");
        list.add("qwerty");
        list.add(null);

        assertEquals(3, list.size());
    }

    @Test
    void add_atPos1_true() {
        List<String> list = ArrayListImpl.of("123", "qwerty", "dummy");

        list.add(1, "test");

        assertEquals( "test", list.get(1));
    }


    @Test
    void addAll_newList_correctOrderInResultSumList() {
        List<String> list = ArrayListImpl.of("123", "234", "345");

        list.addAll(ArrayListImpl.of("test", "dummy"));

        assertAll(
                () -> assertEquals(list.get(0),"123"),
                () -> assertEquals(list.get(1),"234"),
                () -> assertEquals(list.get(2),"345"),
                () -> assertEquals(list.get(3),"test"),
                () -> assertEquals(list.get(4),"dummy")
        );
    }

    @Test
    void addAll_newListAtPos1_correctOrderInResultSumList() {
        List<String> list = ArrayListImpl.of("123", "234", "345");

        list.addAll(1, ArrayListImpl.of("test", "dummy"));

        assertAll(
                () -> assertEquals(list.get(0),"123"),
                () -> assertEquals(list.get(1),"test"),
                () -> assertEquals(list.get(2),"dummy"),
                () -> assertEquals(list.get(3),"234"),
                () -> assertEquals(list.get(4),"345")
        );
    }

    @Test
    void set_elementAtPos1_equalsToElement() {
        List<String> list = ArrayListImpl.of("123", "234", "345");

        list.set(1, "test");

        assertEquals(list.get(1),"test");
    }

    @Test
    void contains_existingElement_true() {
        List<String> list = ArrayListImpl.of("123", "qwerty", null);

        assertTrue(list.contains("qwerty"));
    }

    @Test
    void contains_noNExistingElement_false() {
        List<String> list = ArrayListImpl.of("123", "qwerty", null);

        assertFalse(list.contains("test"));
    }

    @Test
    void indexOf_existingElement_1() {
        List<String> list = ArrayListImpl.of("test", "123", "qwerty");

        assertEquals(1, list.indexOf("123"));
    }

    @Test
    void indexOf_nonExistingElement_neg1() {
        List<String> list = ArrayListImpl.of("123", "qwerty", "test");

        assertEquals(list.indexOf("dummy"), -1);
    }

    @Test
    void remove_existingElement_sizeEquals2() {
        List<String> list = ArrayListImpl.of("123", "qwerty", null);

        list.remove("123");

        assertEquals(2, list.size());
    }

    @Test
    void removeByIndex_existingIndex_deletedElement() {
        List<String> list = ArrayListImpl.of("123", "qwerty", "test");

        list.remove(1);

        assertNotEquals("qwerty", list.get(1));
    }
}