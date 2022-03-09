package ru.clevertec.custom_collection.my_list;

import ru.clevertec.custom_collection.exception.UnsupportedActionExcepton;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
 */

public class LinkedListImpl<T> extends AbstractSequentialList<T>
        implements List<T>, Deque<T>, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
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
     * Separate lockers for reading and writing.
     * Only a single thread at a time (a writer thread) can modify the shared data,
     * while any number of threads can concurrently read the data(reader threads).
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * If no threads have locked the ReadWriteLock for writing,
     * and no thread have requested a write lock,
     * multiple threads can lock the lock for reading.
     */
    private final Lock readLock = lock.readLock();

    /**
     * If no threads are reading or writing,
     * only one thread at a time can lock the lock for writing.
     */
    private final Lock writeLock = lock.writeLock();

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
        writeLock.lock();
        try {
            insertNode(index, element);
        } finally {
            writeLock.unlock();
        }
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
        writeLock.lock();
        try {
            if (c.isEmpty()) {
                return false;
            } else {
                for (T element : c) {
                    add(index++, element);
                }
                return true;
            }
        }finally {
            writeLock.unlock();
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
        readLock.lock();
        try {
            return getNode(index).element;
        } finally {
            readLock.unlock();
        }
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
        writeLock.lock();
        try {
            isValidIndex(index);
            getNode(index).element = element;
            return element;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        readLock.lock();
        try {
            return size;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return size == 0;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        readLock.lock();
        try {
            return indexOf(o) >= 0;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     */
    @Override
    public int indexOf(Object o) {
        readLock.lock();
        try {
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
        } finally {
            readLock.unlock();
        }
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
        writeLock.lock();
        try {
            return remove(o);
        } finally {
            writeLock.unlock();
        }
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
        writeLock.lock();
        try {
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
        } finally {
            writeLock.unlock();
        }
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
        writeLock.lock();
        try {
            insertNode(size, (T) o);
            return true;
        } finally {
            writeLock.unlock();
        }
    }


    /**
     * Inserts the specified element at the beginning of this list.
     *
     * @param o the element to add
     */
    @Override
    @SuppressWarnings("unchecked")
    public void addFirst(Object o) {
        writeLock.lock();
        try {
            Node<T> newNode = new Node<>((T) o);
            newNode.setNextNode(head);
            head = newNode;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        } finally {
            writeLock.unlock();
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
        writeLock.lock();
        try {
            Node<T> newNode = new Node<>((T) o);
            newNode.setPreviousNode(tail);
            tail = newNode;
        } catch (Exception e){
            throw new IllegalArgumentException();
        } finally {
            writeLock.unlock();
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
        writeLock.lock();
        try {
            return remove(0);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes and returns the last element from this list.
     *
     * @return the last element from this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T removeLast() {
        writeLock.lock();
        try {
            return remove(size - 1);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Returns the first element in this list.
     *
     * @return the first element in this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T getFirst() {
        readLock.lock();
        try {
            if (head == null) throw new NoSuchElementException();
            return head.element;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns the last element in this list.
     *
     * @return the last element in this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T getLast() {
        readLock.lock();
        try {
            if (tail == null) throw new NoSuchElementException();
            return tail.element;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public T element() {
        readLock.lock();
        try {
            return getFirst();
        } finally {
            readLock.unlock();
        }
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
        writeLock.lock();
        try {
            return removeFirst();
        } finally {
            writeLock.unlock();
        }
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
        writeLock.lock();
        try {
            if (c.isEmpty()) {
                return false;
            } else {
                for (Object element : c) {
                    add(element);
                }
                return true;
            }
        } finally {
            writeLock.unlock();
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
        writeLock.lock();
        try {
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
        } finally {
            writeLock.unlock();
        }
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
        writeLock.lock();
        try {
            Node<T> node = getNode(index);
            if (!deleteNode(node)) throw new NoSuchElementException();
            return node.element;
        } finally {
            writeLock.unlock();
        }
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
        readLock.lock();
        try {
            int index = 0;
            Object[] array = new Object[size];
            Node<T> currentNode = head;
            while (currentNode != null) {
                array[index++] = currentNode.element;
                currentNode = currentNode.previousNode;
            }
            return array;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public Iterator<T> iterator() {
        readLock.lock();
        try {
            return new IteratorImpl();
        } finally {
            readLock.unlock();
        }
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
    @SafeVarargs
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
        writeLock.lock();
        try {
            addFirst(o);
        } catch (IllegalArgumentException e){
            return false;
        } finally {
            writeLock.unlock();
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
        writeLock.lock();
        try {
            addLast(o);
        } catch (IllegalArgumentException e){
            return false;
        } finally {
            writeLock.unlock();
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
        writeLock.lock();
        try {
            T removed;
            try {
                removed = remove(0);
            } catch (IllegalArgumentException e) {
                return null;
            }
            return removed;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Retrieves and removes the last element of this list.
     *
     * @return the last element of this list.
     */
    @Override
    public T pollLast() {
        writeLock.lock();
        try {
            T removed;
            try {
                removed = remove(size - 1);
            } catch (IllegalArgumentException e) {
                return null;
            }
            return removed;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Retrieves, but does not remove, the first element of this list.
     *
     * @return the first element of this list
     */
    @Override
    public T peekFirst() {
        readLock.lock();
        try {
            return head.element;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Retrieves, but does not remove, the last element of this list.
     *
     * @return the last element of this list.
     */
    @Override
    public T peekLast() {
        readLock.lock();
        try {
            return tail.element;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Adds the specified element as the tail (last element) of this list.
     *
     * @param o the element to add
     * @return {@code true})
     */
    @Override
    public boolean offer(T o) {
        writeLock.lock();
        try{
            add(o);
        } catch (IllegalArgumentException e){
            return false;
        } finally {
            writeLock.unlock();
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
        writeLock.lock();
        try {
            Node<T> oldHead = head;
            remove(head);
            return oldHead.element;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list
     */
    @Override
    public T peek() {
        readLock.lock();
        try {
            return head.element;
        } finally {
            readLock.unlock();
        }
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
        writeLock.lock();
        try {
            addFirst(o);
        } finally {
            writeLock.unlock();
        }
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
        writeLock.lock();
        try {
            return removeLast();
        } finally {
            writeLock.unlock();
        }
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
