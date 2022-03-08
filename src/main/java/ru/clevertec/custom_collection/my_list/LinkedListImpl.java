package ru.clevertec.custom_collection.my_list;

import ru.clevertec.custom_collection.exception.UnsupportedActionExcepton;

import java.io.Serializable;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * <p>Created as Task 3 for Clevertec.</p>
 * <p>Custom implementation of the {@link List} interface.</p>
 * <p>Some methods are not implemented yet so they are implemented from {@link AbstractSequentialList}
 * in order to be accessible at once if they are needed</p>
 * @param <T> the type of elements in this list
 * @author  Olga Mailychko
 * @see     Collection
 * @see     LinkedList
 * @see     Vector
 * @since   1.8
 */

public class LinkedListImpl<T> extends AbstractSequentialList<T>
        implements List<T>, Deque<T>, Cloneable, Serializable {

    /**
     * Current size of the list.
     */
    private int size;

    /**
     * Pointer to first node.
     */
    private Node<T> head;

    /**
     * Pointer to last node.
     */
    private Node<T> tail;

    /**
     * Returns the first element in this list.
     *
     * @return the first element in this list
     */
    public Node<T> getHead() {
        return head;
    }

    /**
     * Returns the last element in this list.
     *
     * @return the last element in this list
     */
    public Node<T> getTail() {
        return tail;
    }

    /**
     * Constructs an empty list.
     */
    public LinkedListImpl() {
    }


    /**
     * Inserts the specified element at the specified position in this list.
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     */
    @Override
    public void add(int index, T element) {
        insertNode(index, element);
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.
     *
     * @param index index at which to insert the first element
     *              from the specified collection
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (c.isEmpty()) {
            return false;
        } else {
            for (T element : c) {
                add(index++, element);
            }
            return true;
        }
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        throw new UnsupportedActionExcepton();
    }

    @Override
    public void sort(Comparator<? super T> c) {
        throw new UnsupportedActionExcepton();
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     */
    @Override
    public T get(int index) {
        return getNode(index).element;
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     */
    @Override
    public T set(int index, T element) {
        isValidIndex(index);
        getNode(index).element = element;
        return element;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     */
    @Override
    public int indexOf(Object o) {
        int index = 0;
        Node<T> currentNode = head;
        if (o == null) {
            while (currentNode != null) {
                if (currentNode.element == null) {
                    return index;
                }
                currentNode = currentNode.previousNode;
                index++;
            }
        } else {
            while (currentNode != null) {
                if (o.equals(currentNode.element)) {
                    return index;
                }
                currentNode = currentNode.previousNode;
                index++;
            }
        }
        return -1;
    }

    /**
     * Removes the first occurrence of the specified element in this
     * list (when traversing the list from head to tail). If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     */
    @Override
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    /**
     * Removes the last occurrence of the specified element in this
     * list (when traversing the list from head to tail). If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     */
    @Override
    public boolean removeLastOccurrence(Object o) {
        Node<T> currentNode = tail;
        if (o == null) {
            while (currentNode != null) {
                if (currentNode.element == null) {
                    deleteNode(currentNode);
                    return true;
                }
                currentNode = currentNode.nextNode;
            }
        } else {
            while (currentNode != null) {
                if (o.equals(currentNode.element)) {
                    deleteNode(currentNode);
                    return true;
                }
                currentNode = currentNode.nextNode;
            }
        }
        return false;

    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param o element to be appended to this list
     * @return {@code true}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean add(Object o) {
        insertNode(size, (T)o);
        return true;
    }


    /**
     * Inserts the specified element at the beginning of this list.
     *
     * @param o the element to add
     */
    @Override
    @SuppressWarnings("unchecked")
    public void addFirst(Object o) {
        try {
            Node<T> newNode = new Node<>((T) o);
            newNode.setNextNode(head);
            head = newNode;
        } catch (Exception e){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * <p>This method is equivalent to {@link #add}.
     *
     * @param o the element to add
     */
    @Override
    @SuppressWarnings("unchecked")
    public void addLast(Object o) {
        try {
            Node<T> newNode = new Node<>((T) o);
            newNode.setPreviousNode(tail);
            tail = newNode;
        } catch (Exception e){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Removes and returns the first element from this list.
     *
     * @return the first element from this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T removeFirst() {
        return remove(0);
    }

    /**
     * Removes and returns the last element from this list.
     *
     * @return the last element from this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T removeLast() {
        return remove(size - 1);
    }

    /**
     * Returns the first element in this list.
     *
     * @return the first element in this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T getFirst() {
        if (head == null) throw new NoSuchElementException();
        return head.element;
    }

    /**
     * Returns the last element in this list.
     *
     * @return the last element in this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T getLast() {
        if (tail == null) throw new NoSuchElementException();
        return tail.element;
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T element() {
        return getFirst();
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     */
    @Override
    public T remove() {
        return removeFirst();
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list.
     *
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(Collection c) {
        if (c.isEmpty()) {
            return false;
        } else {
            for (Object element : c) {
                add(element);
            }
            return true;
        }
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        Node<T> currentNode = head;
        if (o == null) {
            while (currentNode != null) {
                if (currentNode.element == null) {
                    deleteNode(currentNode);
                    return true;
                }
                currentNode = currentNode.previousNode;
            }
        } else {
            while (currentNode != null) {
                if (o.equals(currentNode.element)) {
                    deleteNode(currentNode);
                    return true;
                }
                currentNode = currentNode.previousNode;
            }
        }
        return false;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left.
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     */
    @Override
    public T remove(int index) {
        Node<T> node = getNode(index);
        if (!deleteNode(node)) throw new NoSuchElementException();
        return node.element;
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * @return an array containing all of the elements in this list in
     *         proper sequence
     */
    @Override
    public Object[] toArray() {
        int index = 0;
        Object[] array = new Object[size];
        Node<T> currentNode = head;
        while (currentNode != null) {
            array[index++] = currentNode.element;
            currentNode = currentNode.previousNode;
        }
        return array;
    }

    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public Iterator<T> iterator() {
        return new IteratorImpl();
    }

    @Override
    public Iterator descendingIterator() {
        throw new UnsupportedActionExcepton();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedActionExcepton();
    }

    @Override
    public Spliterator<T> spliterator() {
        throw new UnsupportedActionExcepton();
    }

    /**
     * Adds passed array of objects to list.
     *
     * @param objects array of objects list to be initialized with
     */
    static <T> List<T> of(T... objects) {
        List<T> list = new LinkedListImpl<>();
        list.addAll(Arrays.asList(objects));
        return list;
    }

    // Deque operations
    /**
     * Inserts the specified element at the front of this list.
     *
     * @param o the element to insert
     * @return {@code true})
     */
    @Override
    public boolean offerFirst(Object o) {
        try {
            addFirst(o);
        } catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }

    /**
     * Inserts the specified element at the end of this list.
     *
     * @param o the element to insert
     * @return {@code true} (as specified by {@link Deque#offerLast})
     */
    @Override
    public boolean offerLast(Object o) {
        try {
            addLast(o);
        } catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }

    /**
     * Retrieves and removes the first element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the first element of this list, or {@code null} if
     * this list is empty
     */
    @Override
    public T pollFirst() {
        T removed;
        try{
            removed = remove(0) ;
        } catch (IllegalArgumentException e){
            return null;
        }
        return removed;
    }

    /**
     * Retrieves and removes the last element of this list.
     *
     * @return the last element of this list.
     */
    @Override
    public T pollLast() {
        T removed;
        try{
            removed = remove(size - 1) ;
        } catch (IllegalArgumentException e){
            return null;
        }
        return removed;
    }

    /**
     * Retrieves, but does not remove, the first element of this list.
     *
     * @return the first element of this list
     */
    @Override
    public T peekFirst() {
        return head.element;
    }

    /**
     * Retrieves, but does not remove, the last element of this list.
     *
     * @return the last element of this list.
     */
    @Override
    public T peekLast() {
        return tail.element;
    }

    /**
     * Adds the specified element as the tail (last element) of this list.
     *
     * @param o the element to add
     * @return {@code true})
     */
    @Override
    public boolean offer(T o) {
        try{
            add(o);
        } catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }


    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list
     */
    @Override
    public T poll() {
        Node<T> oldHead = head;
        remove(head);
        return oldHead.element;
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list
     */
    @Override
    public T peek() {
        return head.element;
    }

    /**
     * Pushes an element onto the stack represented by this list.  In other
     * words, inserts the element at the front of this list.
     *
     * <p>This method is equivalent to {@link #addFirst}.
     *
     * @param o the element to push
     */
    @Override
    public void push(T o) {
        addFirst(o);
    }

    /**
     * Pops an element from the stack represented by this list.  In other
     * words, removes and returns the first element of this list.
     *
     * <p>This method is equivalent to {@link #removeFirst()}.
     *
     * @return the element at the front of this list (which is the top
     *         of the stack represented by this list)
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T pop() {
        return removeLast();
    }

//    @Override
//    public <T1> T1[] toArray(T1[] a) {
//        throw new ListException("Unsupported operation");
//    }
//
//    @Override
//    public boolean containsAll(Collection<?> c) {
//        throw new ListException("Unsupported operation");
//    }
//
//    @Override
//    public boolean removeAll(Collection<?> c) {
//        throw new ListException("Unsupported operation");
//    }
//
//    @Override
//    public boolean retainAll(Collection<?> c) {
//        throw new ListException("Unsupported operation");
//    }
//
//    @Override
//    public void clear() {
//        throw new ListException("Unsupported operation");
//    }
//
//    @Override
//    public int lastIndexOf(Object o) {
//        throw new ListException("Unsupported operation");
//    }
//
//    @Override
//    public ListIterator<T> listIterator() {
//        throw new ListException("Unsupported operation");
//    }
//
//    @Override
//    public ListIterator<T> listIterator(int index) {
//        throw new ListException("Unsupported operation");
//    }
//
//    @Override
//    public List<T> subList(int fromIndex, int toIndex) {
//        throw new ListException("Unsupported operation");
//    }

    private void insertNode(int index, T element) {
        try {
            if (index == size) {
                Node<T> lastNode = tail;
                Node<T> newNode = new Node<>(lastNode, null, element);
                tail = newNode;
                if (lastNode == null) {
                    head = newNode;
                } else {
                    lastNode.previousNode = newNode;
                }
            } else {
                Node<T> indexNode = getNode(index);
                Node<T> prevNode = indexNode.nextNode;
                Node<T> newNode = new Node<>(prevNode, indexNode, element);
                indexNode.nextNode = newNode;
                if (prevNode == null) {
                    head = newNode;
                } else {
                    prevNode.previousNode = newNode;
                }
            }
            size++;
        } catch (Exception e){
            throw new IllegalArgumentException();
        }
    }

    private Node<T> getNode(int index) {
        isValidIndex(index);
        Node<T> node;
        if (size / 2 > index) {
            node = head;
            for (int i = 0; i < index; i++) {
                node = node.previousNode;
            }
        } else {
            node = tail;
            for (int i = size - 1; i > index; i--) {
                node = node.nextNode;
            }
        }
        return node;
    }

    private void isValidIndex(int index) {
        if (index >= size || index < 0) {
            throw new IllegalArgumentException();
        }
    }

    private boolean deleteNode(Node<T> node) {
        if (node.nextNode == null && node.previousNode == null) return false;
        Node<T> prevElement = node.nextNode;
        Node<T> nextElement = node.previousNode;
        if (prevElement != null) {
            prevElement.previousNode = nextElement;
            if (prevElement.previousNode == null) {
                tail = prevElement;
            }
        }
        if (nextElement != null) {
            nextElement.nextNode = prevElement;
            if (nextElement.nextNode == null) {
                head = nextElement;
            }
        }
        size--;
        return true;
    }

    private static class Node<T> {

        T element;
        Node<T> nextNode;
        Node<T> previousNode;

        Node(){}

        Node(T element){
            this.element = element;
            this.nextNode = null;
            this.previousNode = null;
        }

        Node(Node<T> prev, Node<T> nextNode, T element) {
            this.element = element;
            this.nextNode = prev;
            this.previousNode = nextNode;
        }

        public T getData() {
            return element;
        }

        public void setData(T data) {
            this.element = data;
        }

        public Node getNextNode() {
            return nextNode;
        }

        public void setNextNode(Node<T> nextNode) {
            if (this.nextNode == null) {
                this.nextNode = nextNode;
            } else {
                Node<T> oldNext = this.nextNode;
                this.nextNode = nextNode;
                this.nextNode.nextNode = oldNext;
                this.nextNode.previousNode = this;
                oldNext.previousNode = this.nextNode;
            }
        }

        public Node getPreviousNode() {
            return previousNode;
        }

        public void setPreviousNode(Node<T> previousNode) {
            if (this.previousNode == null) {
                this.previousNode = previousNode;
            } else {
                Node<T> oldPrev = this.previousNode;
                this.previousNode = previousNode;
                this.previousNode.previousNode = oldPrev;
                this.previousNode.nextNode = this;
                oldPrev.nextNode = this.previousNode;
            }
        }
    }

    private class IteratorImpl implements Iterator<T> {

        private Node<T> currentNode = head;

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public T next() {
            if (hasNext()) {
                T element = currentNode.element;
                currentNode = currentNode.previousNode;
                return element;
            } else {
                throw new NoSuchElementException();
            }
        }
    }

}
