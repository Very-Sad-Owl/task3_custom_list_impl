package ru.clevertec.custom_collection.my_list;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>Created as Task 3 for Clevertec.</p>
 * <p>Custom implementation of the {@link List} interface.</p>
 * <p>Some methods are not implemented yet so they are implemented from {@link AbstractList}
 * in order to be accessible at once if they are needed</p>
 * @param <T> the type of elements in this list
 * @author  Olga Mailychko
 * @see     Collection
 * @see     List
 * @see     Vector
 */

public class ArrayListImpl<T> extends AbstractList<T>
        implements List<T>, Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    /**
     * The size collection is appended by default.
     */
    private static final int DEFAULT_APPEND_SIZE = 2;

    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList will be expanded to DEFAULT_CAPACITY when
     * the first element is added.
     */
    private Object[] data;

    /**
     * The size of the ArrayList (the number of elements it contains).
     */
    private int size;

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
     * Constructs an empty list with the DEFAULT_INITIAL_CAPACITY
     */
    public ArrayListImpl(){
        data = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public ArrayListImpl(int initialCapacity) {
        if (initialCapacity > 0) {
            data = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            data = new Object[]{};
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
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
        try{
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
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public Iterator<T> iterator() {
        readLock.lock();
        try {
            return new Itr();
        } finally {
            readLock.unlock();
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
            return Arrays.copyOf(data, size);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param t element to be appended to this list
     * @return {@code true})
     */
    @Override
    public boolean add(T t) {
        writeLock.lock();
        try {
            ensureListSize();
            data[size++] = t;
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Expands list's capacity when it reaches its limit.
     */
    private void ensureListSize() {
        if (isOvercup()) {
            grow();
        }
    }

    /**
     * Checks if current size reaches current capacity.
     *
     * @return {@code true} if size reaches capacity's limit)
     */
    private boolean isOvercup(){
        return size == data.length;

    }

    /**
     * Increases the capacity to DEFAULT_APPEND_SIZE
     */
    private void grow(){
        grow(DEFAULT_APPEND_SIZE);
    }

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the appendSixe argument.
     *
     * @param appendSize the desired capacity
     */
    private void grow(int appendSize){
        data = Arrays.copyOf(data, size + appendSize);
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
            int index = indexOf(o);
            if (index == -1) throw new IndexOutOfBoundsException();
            leftShiftAndTrim(index);

            return true;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes the element at specified by index position and shifts list to the left
     *
     * @param index element's index to be removed from this list
     */
    private void leftShiftAndTrim(int index){
        int newSize =  size - 1;
        if (newSize > index){
            System.arraycopy(data, index + 1, data, index, newSize - index);
        }
        data[size = newSize] = null;
    }

    /**
     * Checks whether list contains specified collection
     *
     * @param c collection to be found
     * @return {@code true} if contains
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        readLock.lock();
        try {
            for (Object el : c) {
                if (!contains(el)) return false;
            }
            return true;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Modifies list's capacity in order to be appropriate to
     * be modified by specified collection
     *
     * @param c collection which will be added
     * @return new size of list
     */
    private int modifySizeToAdd(Collection<? extends T> c){
        if (c.size() == 0) {
            return size;
        }
        int newSize = size + c.size();
        if (data.length < newSize) {
            grow(newSize - data.length);
        }
        return newSize;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.
     *
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        writeLock.lock();
        try {
            int newSize = modifySizeToAdd(c);
            if (newSize == size) return false;
            System.arraycopy(c.toArray(), 0, data,
                    size, c.size());
            size = newSize;
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        writeLock.lock();
        try {
            if (!isValidIndex(index)) throw new IllegalArgumentException("invalid index");
            int newSize = modifySizeToAdd(c);
            if (newSize == size) return false;
            Object[] array = new Object[size - index];
            System.arraycopy(data, index, array, 0, size - index);
            System.arraycopy(c.toArray(), 0, data, index, c.size());
            System.arraycopy(array, 0, data, index + c.size(), array.length);
            size = newSize;
            return true;
        } finally {
            writeLock.unlock();
        }
    }

//    @Override
//    public boolean removeAll(Collection<?> c) {
//        return false;
//    }
//
//    @Override
//    public boolean removeIf(Predicate<? super T> filter) {
//        return false;
//    }
//
//    @Override
//    public boolean retainAll(Collection<?> c) {
//        return false;
//    }
//
//    @Override
//    public void replaceAll(UnaryOperator<T> operator) {
//
//    }

    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super T> c) {
        writeLock.lock();
        try {
            Arrays.sort((T[]) data, 0, size, c);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
    @Override
    public void clear() {
        writeLock.lock();
        try {
            for (int to = size, i = size = 0; i < to; i++)
                data[i] = null;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     */
    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        readLock.lock();
        try {
            if (!isValidIndex(index)) throw new IllegalArgumentException("invalid index");
            return (T) data[index];
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     */
    @Override
    @SuppressWarnings("unchecked")
    public T set(int index, T element) {
        writeLock.lock();
        try {
            if (!isValidIndex(index)) throw new IllegalArgumentException("invalid index");
            data[index] = element;
            return (T) data[index];
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Inserts the specified element at the specified position in this
     * list.
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     */
    @Override
    public void add(int index, T element) {
        writeLock.lock();
        try {
            if (!isValidIndex(index)) throw new IllegalArgumentException("invalid index");
            ensureListSize();
            System.arraycopy(data, index, data, index + 1, size - index);
            data[index] = element;
            ++size;
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
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        writeLock.lock();
        try {
            if (!isValidIndex(index)) throw new IllegalArgumentException("invalid index");
            Object object = data[index];
            leftShiftAndTrim(index);
            return (T) object;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Checks if passed index is valid
     *
     * @param index the index to be checked
     * @return {@code true} if index is valid
     */
    private boolean isValidIndex(int index){
        return index < size && index >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     */
    @Override
    public int indexOf(Object o) {
        readLock.lock();
        try {
            return indexOfInRange(o, 0, size);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns the the first occurrence of the specified element from start to end
     * in this list, or -1 if this list does not contain the element.
     *
     * @param o the index to be checked
     * @param start position from which search starts
     * @param end position until search runs
     * @return index of found element
     */
    private int indexOfInRange(Object o, int start, int end){
        if (o == null) {
            for (int i = start; i < end; i++) {
                if (data[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (o.equals(data[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Adds passed array of objects to list.
     *
     * @param objects array of objects list to be initialized with
     */
    @SafeVarargs
    static <T> List<T> of(T... objects) {
        List<T> list = new ArrayListImpl<>();
        list.addAll(Arrays.asList(objects));
        return list;
    }

//    @Override
//    public int lastIndexOf(Object o) {
//        return 0;
//    }
//
//    @Override
//    public ListIterator<T> listIterator() {
//        return null;
//    }
//
//    @Override
//    public ListIterator<T> listIterator(int index) {
//        return null;
//    }
//
//    @Override
//    public List<T> subList(int fromIndex, int toIndex) {
//        return null;
//    }
//
//    @Override
//    public Spliterator<T> spliterator() {
//        return null;
//    }
//
//    @Override
//    public Stream<T> stream() {
//        return null;
//    }
//
//    @Override
//    public Stream<T> parallelStream() {
//        return null;
//    }


    private class Itr implements Iterator<T> {

        int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor != size();
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (hasNext()) {
                return (T)data[cursor++];
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}


