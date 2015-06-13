// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.tvl.util.function.BiFunction;
import com.tvl.util.function.Function;
import com.tvl.util.function.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An immutable array with O(1) indexable lookup time.
 *
 * @param <T> The type of element stored by the array.
 */
public final class ImmutableArrayList<T> implements ImmutableList<T>, ReadOnlyList<T> {

    public static final ImmutableArrayList<?> EMPTY_ARRAY = new ImmutableArrayList<Object>(new Object[0]);

    private final T[] array;

    private ImmutableArrayList(T[] items) {
        this.array = items;
    }

    /**
     * Gets an empty {@link ImmutableArrayList} instance.
     *
     * @param <T> The type of elements stored in the array.
     * @return An empty immutable array.
     */
    public static <T> ImmutableArrayList<T> empty() {
        @SuppressWarnings("unchecked") // safe
        ImmutableArrayList<T> emptyArray = (ImmutableArrayList<T>)EMPTY_ARRAY;
        return emptyArray;
    }

    /**
     * Creates an empty {@link ImmutableArrayList}.
     *
     * @param <T> The type of elements stored in the array.
     * @return An empty immutable array.
     */
    public static <T> ImmutableArrayList<T> create() {
        return empty();
    }

    /**
     * Creates an {@link ImmutableArrayList} with the specified element as its only member.
     *
     * @param <T> The type of element stored in the array.
     * @param item The element to store in the array.
     * @return A one-element array.
     */
    public static <T> ImmutableArrayList<T> create(T item) {
        @SuppressWarnings("unchecked") // safe
        T[] array = (T[])new Object[] { item };
        return new ImmutableArrayList<T>(array);
    }

    /**
     * Creates an {@link ImmutableArrayList} with the specified elements.
     *
     * @param <T> The type of element stored in the array.
     * @param item1 The first element to store in the array.
     * @param item2 The second element to store in the array.
     * @return A two-element array.
     */
    public static <T> ImmutableArrayList<T> create(T item1, T item2) {
        @SuppressWarnings("unchecked") // safe
        T[] array = (T[])new Object[] { item1, item2 };
        return new ImmutableArrayList<T>(array);
    }

    /**
     * Creates an {@link ImmutableArrayList} with the specified elements.
     *
     * @param <T> The type of element stored in the array.
     * @param item1 The first element to store in the array.
     * @param item2 The second element to store in the array.
     * @param item3 The third element to store in the array.
     * @return A three-element array.
     */
    public static <T> ImmutableArrayList<T> create(T item1, T item2, T item3) {
        @SuppressWarnings("unchecked") // safe
        T[] array = (T[])new Object[] { item1, item2, item3 };
        return new ImmutableArrayList<T>(array);
    }

    /**
     * Creates an {@link ImmutableArrayList} with the specified elements.
     *
     * @param <T> The type of element stored in the array.
     * @param item1 The first element to store in the array.
     * @param item2 The second element to store in the array.
     * @param item3 The third element to store in the array.
     * @param item4 The fourth element to store in the array.
     * @return A four-element array.
     */
    public static <T> ImmutableArrayList<T> create(T item1, T item2, T item3, T item4) {
        @SuppressWarnings("unchecked") // safe
        T[] array = (T[])new Object[] { item1, item2, item3, item4 };
        return new ImmutableArrayList<T>(array);
    }

    /**
     * Creates an {@link ImmutableArrayList} populated with the specified elements.
     *
     * @param <T> The type of element stored in the array.
     * @param items The elements to store in the array.
     * @return An immutable array.
     */
    public static <T> ImmutableArrayList<T> create(T... items) {
        // We can't trust that the array passed in will never be mutated by the caller. The caller may have passed in an
        // array explicitly (not relying on compiler 'T...' syntax) and could then change the array after the call,
        // thereby violating the immutable guarantee provided by this class. So we always copy the array to ensure it
        // won't ever change.
        //
        // Note that createDefensiveCopy treats null as an empty list, so there is no need to handle that case before
        // making the call.
        return createDefensiveCopy(items);
    }

    /**
     * Creates an {@link ImmutableArrayList} populated with the contents of the specified sequence.
     *
     * @param <T> The type of element stored in the array.
     * @param items The elements to store in the array.
     * @return An immutable array.
     */
    public static <T> ImmutableArrayList<T> createAll(Iterable<? extends T> items) {
        Requires.notNull(items, "items");

        // As an optimization, if the provided iterable is actually an ImmutableArrayList<? extends T> instance, simply
        // reuse the instance.
        ImmutableArrayList<? extends T> immutableArray = Immutables.asImmutableArrayList(items);
        if (immutableArray != null) {
            return castUp(immutableArray);
        }

        // We don't recognize the source as an array that is safe to use. So clone the sequence into an array and return
        // an immutable wrapper.
        Integer count = Immutables.tryGetCount(items);
        if (count != null) {
            if (count == 0) {
                // Return a wrapper around the singleton empty array.
                return create();
            } else {
                // We know how long the sequence is.
                ImmutableArrayList.Builder<T> builder = new Builder<T>(count);
                builder.resize(count);
                int current = 0;
                for (T item : items) {
                    builder.set(current, item);
                    current++;
                }

                return builder.moveToImmutable();
            }
        } else {
            ImmutableArrayList.Builder<T> builder = new Builder<T>();
            for (T item : items) {
                builder.add(item);
            }

            if (builder.size() == builder.getCapacity()) {
                return builder.moveToImmutable();
            } else {
                return builder.toImmutable();
            }
        }
    }

    public static <T> ImmutableArrayList<T> createAll(T[] items, int start, int length) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public static <T> ImmutableArrayList<T> createAll(ImmutableArrayList<T> items, int start, int length) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    /**
     * Creates an immutable array by applying a transformation function to the elements of an existing array.
     *
     * @param items The existing immutable array.
     * @param selector The transformation function to apply to each element of {@code items} to obtain the target array.
     * @param <Source> The type of elements stored in the source array.
     * @param <Result> The type of elements stored in the target array.
     * @return A new immutable array containing the transformed elements.
     */
    public static <Source, Result> ImmutableArrayList<Result> createAll(ImmutableArrayList<Source> items, Function<? super Source, Result> selector) {
        Requires.notNull(selector, "selector");

        int length = items.size();
        if (length == 0) {
            return create();
        }

        Result[] array = (Result[])new Object[length];
        for (int i = 0; i < length; i++) {
            array[i] = selector.apply(items.get(i));
        }

        return new ImmutableArrayList<Result>(array);
    }

    public static <Source, Result> ImmutableArrayList<Result> createAll(ImmutableArrayList<Source> items, int start, int length, Function<? super Source, Result> selector) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an immutable array by applying a transformation function to the elements of an existing array.
     *
     * @param items The existing immutable array.
     * @param selector The transformation function to apply to each element of {@code items} to obtain the target array.
     * @param arg An additional argument to pass to the transformation function.
     * @param <Source> The type of elements stored in the source array.
     * @param <Arg> The type of the additional argument to the transformation function.
     * @param <Result> The type of elements stored in the target array.
     * @return A new immutable array containing the transformed elements.
     */
    public static <Source, Arg, Result> ImmutableArrayList<Result> createAll(ImmutableArrayList<Source> items, BiFunction<? super Source, Arg, Result> selector, Arg arg) {
        Requires.notNull(selector, "selector");

        int length = items.size();

        if (length == 0) {
            return create();
        }

        Result[] array = (Result[])new Object[length];
        for (int i = 0; i < length; i++) {
            array[i] = selector.apply(items.get(i), arg);
        }

        return new ImmutableArrayList<Result>(array);
    }

    public static <Source, Arg, Result> ImmutableArrayList<Result> createAll(ImmutableArrayList<Source> items, int start, int length, BiFunction<? super Source, Arg, Result> selector, Arg arg) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates a new instance of the {@link Builder} class.
     *
     * @param <T> The type of elements stored in the array.
     * @return A new builder.
     */
    public static <T> ImmutableArrayList.Builder<T> createBuilder() {
        return ImmutableArrayList.<T>create().toBuilder();
    }

    /**
     * Creates a new instance of the {@link Builder} class with the specified initial capacity.
     *
     * @param <T> The type of elements stored in the array.
     * @param initialCapacity The size of the initial array backing the builder.
     * @return A new builder.
     */
    public static <T> ImmutableArrayList.Builder<T> createBuilder(int initialCapacity) {
        return new Builder<T>(initialCapacity);
    }

    /**
     * Searches an entire one-dimensional sorted {@link ImmutableArrayList} for a specific element, using the default
     * comparator for elements of type {@code T}.
     *
     * @param <T> The type of element stored in the array.
     * @param array The sorted, one-dimensional array to search.
     * @param value The object to search for.
     * @return The index of the specified {@code value} in the specified array, if {@code value} is found. If
     * {@code value} is not found and {@code value} is less than one or more elements in {@code array}, a negative
     * number which is the bitwise complement of the index of the first element that is larger than {@code value}. If
     * {@code value} is not found and {@code value} is greater than all of the elements in {@code array}, a negative
     * number which is the bitwise complement of (the index of the last element plus 1).
     */
    public static <T> int binarySearch(ImmutableArrayList<T> array, T value) {
        Requires.notNull(array, "array");
        return binarySearch(array, value, 0, array.size(), null);
    }

    /**
     * Searches an entire one-dimensional sorted {@link ImmutableArrayList} for a specific element using the specified
     * comparator.
     *
     * @param <T> The type of element stored in the array.
     * @param array The sorted, one-dimensional array to search.
     * @param value The object to search for.
     * @param comparator The comparator to use for comparing elements, or {@code null} to use the default comparator for
     * elements of type {@code T}.
     * @return The index of the specified {@code value} in the specified array, if {@code value} is found. If
     * {@code value} is not found and {@code value} is less than one or more elements in {@code array}, a negative
     * number which is the bitwise complement of the index of the first element that is larger than {@code value}. If
     * {@code value} is not found and {@code value} is greater than all of the elements in {@code array}, a negative
     * number which is the bitwise complement of (the index of the last element plus 1).
     */
    public static <T> int binarySearch(ImmutableArrayList<T> array, T value, Comparator<? super T> comparator) {
        Requires.notNull(array, "array");
        return binarySearch(array, value, 0, array.size(), comparator);
    }

    public static <T> int binarySearch(ImmutableArrayList<T> array, T value, int index, int length) {
        return binarySearch(array, value, index, length, null);
    }

    public static <T> int binarySearch(ImmutableArrayList<T> array, T value, int index, int length, Comparator<? super T> comparator) {
        Requires.notNull(array, "array");
        if (comparator == null) {
            comparator = (Comparator<? super T>)Comparators.<Integer>defaultComparator();
        }

        int fromIndex = index;
        int toIndex = fromIndex + length;
        return Arrays.binarySearch(array.array, fromIndex, toIndex, value, comparator);
    }

    private static <T> ImmutableArrayList<T> createDefensiveCopy(T[] items) {
        if (items == null) {
            return create();
        }

        if (items.length == 0) {
            return empty();
        }

        return new ImmutableArrayList<T>(Arrays.copyOf(items, items.length));
    }

    /**
     * Gets the element at the specified index in the read-only list.
     *
     * @param index The zero-based index of the element to get.
     * @return The element at the specified index in the read-only list.
     */
    @Override
    public T get(int index) {
        // bounds checks are intentionally omitted to maximize the ability of the JIT to inline this method.
        return array[index];
    }

    /**
     * Gets a value indicating whether this collection is empty.
     *
     * @return {@code true} if the collection is empty; otherwise, {@code false}.
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Gets the number of elements in the array.
     *
     * @return The number of elements in the array.
     */
    @Override
    public int size() {
        return array.length;
    }

    /**
     * Searches the array for the specified item.
     *
     * @param item The item to search for.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int indexOf(T item) {
        return indexOf(item, 0, size(), EqualityComparators.defaultComparator());
    }

    /**
     * Searches the array for the specified item.
     *
     * @param item The item to search for.
     * @param startIndex The index at which to begin the search.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int indexOf(T item, int startIndex) {
        return indexOf(item, startIndex, size() - startIndex, EqualityComparators.defaultComparator());
    }

    /**
     * Searches the array for the specified item.
     *
     * @param item The item to search for.
     * @param startIndex The index at which to begin the search.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int indexOf(T item, int startIndex, EqualityComparator<? super T> equalityComparator) {
        return indexOf(item, startIndex, size() - startIndex, equalityComparator);
    }

    /**
     * Searches the array for the specified item.
     *
     * @param item The item to search for.
     * @param startIndex The index at which to begin the search.
     * @param count The number of elements to search.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int indexOf(T item, int startIndex, int count) {
        return indexOf(item, startIndex, count, EqualityComparators.defaultComparator());
    }

    /**
     * Searches the array for the specified item.
     *
     * @param item The item to search for.
     * @param startIndex The index at which to begin the search.
     * @param count The number of elements to search.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    @Override
    public int indexOf(T item, int startIndex, int count, EqualityComparator<? super T> equalityComparator) {
        Requires.notNull(equalityComparator, "equalityComparator");

        if (count == 0 && startIndex == 0) {
            return -1;
        }

        Requires.range(startIndex >= 0 && startIndex < size(), "startIndex");
        Requires.range(count >= 0 && startIndex + count <= size(), "count");

        for (int i = startIndex; i < startIndex + count; i++) {
            if (equalityComparator.equals(array[i], item)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Searches the array for the specified item in reverse.
     *
     * @param item The item to search for.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int lastIndexOf(T item) {
        if (isEmpty()) {
            return -1;
        }

        return lastIndexOf(item, size() - 1, size(), EqualityComparators.defaultComparator());
    }

    /**
     * Searches the array for the specified item in reverse.
     *
     * @param item The item to search for.
     * @param startIndex The index at which to begin the search.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int lastIndexOf(T item, int startIndex) {
        if (isEmpty() && startIndex == 0) {
            return -1;
        }

        return lastIndexOf(item, startIndex, startIndex + 1, EqualityComparators.defaultComparator());
    }

    /**
     * Searches the array for the specified item in reverse.
     *
     * @param item The item to search for.
     * @param startIndex The index at which to begin the search.
     * @param count The number of elements to search.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int lastIndexOf(T item, int startIndex, int count) {
        return lastIndexOf(item, startIndex, count, EqualityComparators.defaultComparator());
    }

    /**
     * Searches the array for the specified item in reverse.
     *
     * @param item The item to search for.
     * @param startIndex The index at which to begin the search.
     * @param count The number of elements to search.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    @Override
    public int lastIndexOf(T item, int startIndex, int count, EqualityComparator<? super T> equalityComparator) {
        Requires.notNull(equalityComparator, "equalityComparator");

        if (startIndex == 0 && count == 0) {
            return -1;
        }

        Requires.range(startIndex >= 0 && startIndex < size(), "startIndex");
        Requires.range(count >= 0 && startIndex - count + 1 >= 0, "count");

        for (int i = startIndex; i >= startIndex - count + 1; i--) {
            if (equalityComparator.equals(item, array[i])) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Determines whether the specified item exists in the array.
     *
     * @param item The item to search for.
     * @return {@code true} if an equal value was found in the array; otherwise, {@code false}.
     */
    public boolean contains(T item) {
        return indexOf(item) >= 0;
    }

    /**
     * Copies the contents of this array to the specified array.
     *
     * @param destination The array to copy to.
     */
    public void copyTo(T[] destination) {
        System.arraycopy(array, 0, destination, 0, size());
    }

    /**
     * Copies the elements of this array to the specified array.
     *
     * @param destination The array to copy to.
     * @param destinationIndex The index into the destination array to which the first copied element is written.
     */
    public void copyTo(T[] destination, int destinationIndex) {
        System.arraycopy(array, 0, destination, destinationIndex, size());
    }

    /**
     * Copies the elements of this array to the specified array.
     *
     * @param sourceIndex The index into this collection of the first element to copy.
     * @param destination The array to copy to.
     * @param destinationIndex The index into the destination array to which the first copied element is written.
     * @param length The number of elements to copy.
     */
    public void copyTo(int sourceIndex, T[] destination, int destinationIndex, int length) {
        System.arraycopy(array, sourceIndex, destination, destinationIndex, length);
    }

    /**
     * Returns a new array with the specified value inserted at the specified location.
     *
     * @param index The zero-based index into the array at which the new item should be added.
     * @param element The item to insert into the array.
     * @return A new {@link ImmutableArrayList}.
     */
    @Override
    public ImmutableArrayList<T> add(int index, T element) {
        Requires.range(index >= 0 && index <= size(), "index");

        if (isEmpty()) {
            return create(element);
        }

        T[] tmp = Arrays.copyOf(array, size() + 1);
        System.arraycopy(array, index, tmp, index + 1, size() - index);
        tmp[index] = element;
        return new ImmutableArrayList<T>(tmp);
    }

    /**
     * Inserts the specified values at the specified index.
     *
     * @param index The index at which to insert the value.
     * @param items The elements to insert.
     * @return The new immutable collection.
     */
    @Override
    public ImmutableArrayList<T> addAll(int index, Iterable<? extends T> items) {
        Requires.range(index >= 0 && index <= size(), "index");
        Requires.notNull(items, "items");

        if (isEmpty()) {
            return ImmutableArrayList.createAll(items);
        }

        Integer count = Immutables.tryGetCount(items);
        if (count != null) {
            if (count == 0) {
                return this;
            }

            T[] tmp = Arrays.copyOf(array, array.length + count);
            System.arraycopy(tmp, index, tmp, index + count, array.length - index);
            int current = index;
            for (T item : items) {
                tmp[current++] = item;
            }

            return new ImmutableArrayList<T>(tmp);
        }

        Builder<T> builder = new Builder<T>(array.length);
        builder.addAll(this, index);
        for (T item : items) {
            builder.add(item);
        }

        builder.setCapacity(builder.size() + array.length - index);
        for (int i = index; i < array.length; i++) {
            builder.add(get(i));
        }

        return builder.moveToImmutable();
    }

    /**
     * Inserts the specified values at the specified index.
     *
     * @param index The index at which to insert the value.
     * @param items The elements to insert.
     * @return The new immutable collection.
     */
    public ImmutableArrayList<T> addAll(int index, ImmutableArrayList<? extends T> items) {
        Requires.notNull(items, "items");
        Requires.range(index >= 0 && index <= size(), "index");
        if (isEmpty()) {
            return castUp(items);
        } else if (items.isEmpty()) {
            return this;
        }

        return addAll(index, (Iterable<? extends T>)items);
    }

    /**
     * Returns a new array with the specified value inserted at the end.
     *
     * @param item The item to insert at the end of the array.
     * @return The new array.
     */
    @Override
    public ImmutableArrayList<T> add(T item) {
        if (isEmpty()) {
            return create(item);
        }

        return add(size(), item);
    }

    /**
     * Adds the specified values to the array.
     *
     * @param items The values to add.
     * @return A new immutable array with the elements added.
     */
    @Override
    public ImmutableArrayList<T> addAll(Iterable<? extends T> items) {
        return addAll(size(), items);
    }

    /**
     * Adds the specified values to the array.
     *
     * @param items The values to add.
     * @return A new immutable array with the elements added.
     */
    public ImmutableArrayList<T> addAll(ImmutableArrayList<? extends T> items) {
        Requires.notNull(items, "items");
        if (isEmpty()) {
            return castUp(items);
        } else if (items.isEmpty()) {
            return this;
        }

        return addAll((Iterable<? extends T>)items);
    }

    /**
     * Returns an array with the item at the specified position replaced.
     *
     * @param index The index of the item to replace.
     * @param value The new item.
     * @return The new immutable array.
     */
    @Override
    public ImmutableArrayList<T> set(int index, T value) {
        Requires.range(index >= 0 && index <= size(), "index");

        T[] tmp = array.clone();
        tmp[index] = value;
        return new ImmutableArrayList<T>(tmp);
    }

    /**
     * Replaces the first equal element in the list with the specified element.
     *
     * @param oldValue The element to replace.
     * @param newValue The element to replace the old element with.
     * @return The new immutable array, even if the value being replaced is equal to the new value for that position.
     * @throws IllegalArgumentException if the old value does not exist in the list.
     */
    public ImmutableArrayList<T> replace(T oldValue, T newValue) {
        return replace(oldValue, newValue, EqualityComparators.defaultComparator());
    }

    /**
     * Replaces the first equal element in the list with the specified element.
     *
     * @param oldValue The element to replace.
     * @param newValue The element to replace the old element with.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The new immutable array, even if the value being replaced is equal to the new value for that position.
     * @throws IllegalArgumentException if the old value does not exist in the list.
     */
    @Override
    public ImmutableArrayList<T> replace(T oldValue, T newValue, EqualityComparator<? super T> equalityComparator) {
        int index = indexOf(oldValue, 0, equalityComparator);
        if (index < 0) {
            throw new IllegalArgumentException("Cannot find the old value");
        }

        return set(index, newValue);
    }

    /**
     * Returns an array with the first occurrence of the specified element removed from the array.
     *
     * If no match is found, the current array is returned.
     *
     * @param value The item to remove.
     * @return The new immutable array.
     */
    public ImmutableArrayList<T> remove(T value) {
        return remove(value, EqualityComparators.defaultComparator());
    }

    /**
     * Returns an array with the first occurrence of the specified element removed from the array.
     *
     * If no match is found, the current array is returned.
     *
     * @param value The item to remove.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The new immutable array.
     */
    @Override
    public ImmutableArrayList<T> remove(T value, EqualityComparator<? super T> equalityComparator) {
        int index = indexOf(value, 0, equalityComparator);
        if (index < 0) {
            return this;
        } else {
            return remove(index);
        }
    }

    /**
     * Returns an array with the element at the specified index removed.
     *
     * @param index The zero-based index into the array for the element to omit from the returned array.
     * @return The new immutable array.
     */
    @Override
    public ImmutableArrayList<T> remove(int index) {
        return removeAll(index, 1);
    }

    /**
     * Returns an array with the elements at the specified position removed.
     *
     * @param index The zero-based index into the array for the element to omit from the returned array.
     * @param count The number of elements to remove.
     * @return The new immutable array.
     */
    @Override
    public ImmutableArrayList<T> removeAll(int index, int count) {
        Requires.range(index >= 0 && index < size(), "index");
        Requires.range(count >= 0 && index + count <= size(), "count");

        T[] tmp = Arrays.copyOf(array, array.length - count);
        System.arraycopy(array, index + count, tmp, index, size() - index - count);
        return new ImmutableArrayList<T>(tmp);
    }

    /**
     * Removes the specified values from this array.
     *
     * @param items The items to remove if matches are found in this array.
     * @return A new immutable array with the elements removed.
     */
    public ImmutableArrayList<T> removeAll(Iterable<? extends T> items) {
        return removeAll(items, EqualityComparators.defaultComparator());
    }

    /**
     * Removes the specified values from this array.
     *
     * @param items The items to remove if matches are found in this array.
     * @param equalityComparator The equality comparator to use in the search.
     * @return A new immutable array with the elements removed.
     */
    @Override
    public ImmutableArrayList<T> removeAll(Iterable<? extends T> items, EqualityComparator<? super T> equalityComparator) {
        Requires.notNull(items, "items");
        Requires.notNull(equalityComparator, "equalityComparator");

        SortedSet<Integer> indexesToRemove = new TreeSet<Integer>();
        for (T item : items) {
            int index = indexOf(item, 0, equalityComparator);
            while (index >= 0 && !indexesToRemove.add(index) && index + 1 < size()) {
                // This is a duplicate of one we've found. Try hard to find another instance in the list to remove.
                index = indexOf(item, index + 1, equalityComparator);
            }
        }

        return removeAtRange(indexesToRemove);
    }

    /**
     * Removes the specified values from this array.
     *
     * @param items The items to remove if matches are found in this array.
     * @return A new immutable array with the elements removed.
     */
    public ImmutableArrayList<T> removeAll(ImmutableArrayList<? extends T> items) {
        return removeAll(items, EqualityComparators.defaultComparator());
    }

    /**
     * Removes the specified values from this array.
     *
     * @param items The items to remove if matches are found in this array.
     * @param equalityComparator The equality comparator to use in the search.
     * @return A new immutable array with the elements removed.
     */
    public ImmutableArrayList<T> removeAll(ImmutableArrayList<? extends T> items, EqualityComparator<? super T> equalityComparator) {
        Requires.notNull(items, "items");
        Requires.notNull(equalityComparator, "equalityComparator");

        SortedSet<Integer> indexesToRemove = new TreeSet<Integer>();
        for (T item : items)
        {
            int index = indexOf(item, 0, equalityComparator);
            while (index >= 0 && !indexesToRemove.add(index) && index + 1 < size())
            {
                // This is a duplicate of one we've found. Try hard to find another instance in the list to remove.
                index = indexOf(item, index + 1, equalityComparator);
            }
        }

        return removeAtRange(indexesToRemove);
    }

    /**
     * Removes all the elements that match the conditions defined by the specified predicate.
     *
     * @param predicate The predicate that defines the conditions of the elements to remove.
     * @return The new immutable array.
     */
    @Override
    public ImmutableArrayList<T> removeIf(Predicate<? super T> predicate) {
        Requires.notNull(predicate, "predicate");
        if (isEmpty()) {
            return this;
        }

        ArrayList<Integer> removeIndexes = null;
        for (int i = 0; i < array.length; i++) {
            if (predicate.test(array[i])) {
                if (removeIndexes == null) {
                    removeIndexes = new ArrayList<Integer>();
                }

                removeIndexes.add(i);
            }
        }

        return removeIndexes != null ? removeAtRange(removeIndexes) : this;
    }

    /**
     * Returns an empty array.
     *
     * @return An empty immutable array.
     */
    @Override
    public ImmutableArrayList<T> clear() {
        return empty();
    }

    /**
     * Returns a sorted instance of this array.
     *
     * @return A sorted instance of this array.
     */
    public ImmutableArrayList<T> sort() {
        return sort(0, size(), null);
    }

    /**
     * Returns a sorted instance of this array.
     *
     * @param comparator The comparator to use in sorting. If {@code null}, a default comparator is used.
     * @return A sorted instance of this array.
     */
    public ImmutableArrayList<T> sort(Comparator<? super T> comparator) {
        return sort(0, size(), comparator);
    }

    /**
     * Returns a sorted instance of this array.
     *
     * @param index The index of the first element to consider in the sort.
     * @param count The number of elements to include in the sort.
     * @param comparator The comparator to use in sorting. If {@code null}, a default comparator is used.
     * @return A sorted instance of this array.
     */
    public ImmutableArrayList<T> sort(int index, int count, Comparator<? super T> comparator) {
        Requires.range(index >= 0, "index");
        Requires.range(count >= 0 && index + count <= size(), "count");

        if (comparator == null) {
            comparator = (Comparator<? super T>)(Comparator<?>)Comparators.<Integer>defaultComparator();
        }

        // 0 and 1 element arrays don't need to be sorted.
        if (count > 1) {
            // Avoid copying the entire array when the array is already sorted.
            boolean outOfOrder = false;
            for (int i = index + 1; i < index + count; i++) {
                if (comparator.compare(array[i - 1], array[i]) > 0) {
                    outOfOrder = true;
                    break;
                }
            }

            if (outOfOrder) {
                Builder<T> builder = toBuilder();
                builder.sort(index, count, comparator);
                return builder.moveToImmutable();
            }
        }

        return this;
    }

    /**
     * Returns a builder that is populated with the same contents as this array.
     *
     * @return The new builder.
     */
    public ImmutableArrayList.Builder<T> toBuilder() {
        if (isEmpty()) {
            return new Builder<T>();
        }

        Builder<T> builder = new Builder<T>(size());
        builder.addAll(this);
        return builder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Itr iterator() {
        return new Itr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImmutableArrayList<?>)) {
            return false;
        }

        ImmutableArrayList<?> other = (ImmutableArrayList<?>)obj;
        return this.array == other.array;
    }

    /**
     * Determines whether the current immutable array is equal to another immutable array.
     *
     * @param other The immutable array to compare to the current instance.
     * @return {@code true} if {@code other} is not null and has the same underlying array as the current instance;
     * otherwise, {@code false}.
     */
    public boolean equals(ImmutableArrayList<?> other) {
        if (other == null) {
            return false;
        }

        return this.array == other.array;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return ((Object)array).hashCode();
    }

    /**
     * Returns a covariant {@link ImmutableArrayList} instance based on an input instance.
     *
     * <p>Since {@link ImmutableArrayList} is an immutable data structure, the covariant cast can be safely and
     * efficiently performed without creating new object instances.</p>
     *
     * @param items The array.
     * @param <T> The type of items stored in the array.
     * @return The input {@code items}.
     */
    public static <T> ImmutableArrayList<T> castUp(ImmutableArrayList<? extends T> items) {
        // Since this class is immutable, we can actually return the same instance
        @SuppressWarnings("unchecked") // this is safe
        ImmutableArrayList<T> result = (ImmutableArrayList<T>)items;
        return result;
    }

    /**
     * Casts the current immutable array to an instance of an immutable array with another element type.
     *
     * <p>This method supports safe down- and cross-casts in the object hierarchy by validating the elements of the
     * current array at runtime. To efficiently perform covariant up-casts, use {@link #castUp(ImmutableArrayList)}
     * instead.</p>
     *
     * @param clazz The element type to cast the current array to.
     * @param <Other> The type of element stored in the array.
     * @return The current array as an instance of an immutable array of {@code Other} objects.
     * @throws ClassCastException if any object in the current instance cannot be cast to an instance of {@code Other}.
     */
    public <Other> ImmutableArrayList<Other> castArray(Class<Other> clazz) {
        Requires.notNull(clazz, "clazz");
        for (T item : this) {
            clazz.cast(item);
        }

        // It is now safe to cast the array.
        @SuppressWarnings("unchecked") // this is safe
        ImmutableArrayList<Other> result = (ImmutableArrayList<Other>)this;
        return result;
    }

    /**
     * Attempts to cast the current immutable array to an instance of an immutable array with another element type.
     *
     * <p>This method supports safe down- and cross-casts in the object hierarchy by validating the elements of the
     * current array at runtime. To efficiently perform covariant up-casts, use {@link #castUp(ImmutableArrayList)}
     * instead.</p>
     *
     * @param clazz The element type to cast the current array to.
     * @param <Other> The type of element stored in the array.
     * @return The current array as an instance of an immutable array of {@code Other} objects if all objects in the
     * array can be cast to {@code Other}; otherwise, {@code null}.
     */
    public <Other> ImmutableArrayList<Other> as(Class<Other> clazz) {
        Requires.notNull(clazz, "clazz");
        for (T item : this) {
            if (item != null && !clazz.isInstance(item)) {
                return null;
            }
        }

        // It is now safe to cast the array.
        @SuppressWarnings("unchecked") // this is safe
        ImmutableArrayList<Other> result = (ImmutableArrayList<Other>)this;
        return result;
    }

    /**
     * Filters the elements of this array to those assignable to the specified type.
     *
     * @param clazz The desired type of element.
     * @param <Result> The desired type of element.
     * @return An iterable that contains the elements from the current array which are instances of type {@code Result}.
     */
    public <Result> Iterable<Result> ofType(final Class<Result> clazz) {
        Requires.notNull(clazz, "clazz");
        return new Iterable<Result>() {
            @Override
            public Iterator<Result> iterator() {
                return new Iterator<Result>() {
                    private Iterator<T> outerIterator = ImmutableArrayList.this.iterator();
                    private Result nextElement;

                    @Override
                    public boolean hasNext() {
                        if (nextElement != null) {
                            return true;
                        }

                        while (outerIterator.hasNext()) {
                            T current = outerIterator.next();
                            if (clazz.isInstance(current)) {
                                nextElement = clazz.cast(current);
                                return true;
                            }
                        }

                        return false;
                    }

                    @Override
                    public Result next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }

                        Result result = nextElement;
                        nextElement = null;
                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("This iterator is read-only.");
                    }
                };
            }
        };
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    /**
     * Returns an array with the items at the specified indexes removed.
     *
     * @param indexesToRemove A sorted set of indexes to elements that should be omitted from the returned array.
     * @return The new immutable array.
     */
    private ImmutableArrayList<T> removeAtRange(Collection<Integer> indexesToRemove) {
        Requires.notNull(indexesToRemove, "indexesToRemove");

        if (indexesToRemove.isEmpty()) {
            return this;
        }

        T[] newArray = Arrays.copyOf(array, size() - indexesToRemove.size());
        int copied = 0;
        int removed = 0;
        int lastIndexRemoved = -1;
        for (int indexToRemove : indexesToRemove) {
            int copyLength = lastIndexRemoved == -1 ? indexToRemove : (indexToRemove - lastIndexRemoved - 1);
            assert indexToRemove > lastIndexRemoved; // We require that the input be a sorted set.
            System.arraycopy(array, copied + removed, newArray, copied, copyLength);
            removed++;
            copied += copyLength;
            lastIndexRemoved = indexToRemove;
        }

        System.arraycopy(array, copied + removed, newArray, copied, size() - (copied + removed));

        return new ImmutableArrayList<T>(newArray);
    }

    /**
     * A writable array accessor that can be converted to an {@link ImmutableArrayList}.
     *
     * @param <T> The type of element stored in the array.
     */
    public static final class Builder<T> implements List<T> {

        /**
         * The backing array for the builder.
         */
        private T[] elements;
        /**
         * The number of initialized elements in the array.
         */
        private int count;

        /**
         * Initializes a new instance of the {@link Builder} class.
         */
        Builder() {
            this(8);
        }

        /**
         * Initializes a new instance of the {@link Builder} class.
         *
         * @param capacity The initial capacity of the internal array.
         */
        Builder(int capacity) {
            Requires.argument(capacity >= 0, "capacity", "capacity must be greater than or equal to zero");

            @SuppressWarnings("unchecked") // safe
            T[] elementsArray = (T[])new Object[capacity];
            elements = elementsArray;
            count = 0;
        }

        /**
         * Gets the length of the internal array.
         *
         * @return The length of the internal array.
         */
        public int getCapacity() {
            return elements.length;
        }

        /**
         * Sets the length of the internal array.
         *
         * The internal array is reallocated to the given capacity if it is not already the specified length.
         *
         * @param value The length for the internal array.
         * @throws IllegalArgumentException if {@code value} is less than {@link #size()}.
         */
        public void setCapacity(int value) {
            Requires.argument(value >= count, "value", "Capacity must be greater than or equal to count");

            if (value != elements.length) {
                if (value > 0) {
                    elements = Arrays.copyOf(elements, value);
                } else {
                    elements = ImmutableArrayList.<T>empty().array;
                }
            }
        }

        /**
         * Gets the length of the builder.
         *
         * @return The length of the builder.
         */
        @Override
        public int size() {
            return count;
        }

        /**
         * Sets the length of the builder.
         *
         * If the value is decreased, the array contents are truncated. If the value is increased, the added elements
         * are initialized to {@code null}.
         *
         * @param size The length of the builder.
         * @throws IllegalArgumentException if {@code size} is less than 0.
         */
        public void resize(int size) {
            Requires.argument(size >= 0, "size", "size must be greater than or equal to zero");

            if (size < count) {
                // truncation mode
                Arrays.fill(elements, size, count, null);
            } else {
                // expansion mode
                ensureCapacity(size);
            }

            count = size;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty() {
            return count == 0;
        }

        /**
         * Gets the element at the specified index.
         *
         * @param index The index.
         * @return The element at the specified index.
         * @throws IndexOutOfBoundsException if {@code index} is less than 0 or greater than or equal to
         * {@link #size()}.
         */
        @Override
        public T get(int index) {
            Requires.range(index < size(), "index");

            return elements[index];
        }

        /**
         * Sets the element at the specified index.
         *
         * @param index The index.
         * @param element The new element value.
         * @return The previous value at the specified index.
         * @throws IndexOutOfBoundsException if {@code index} is less than 0 or greater than or equal to
         * {@link #size()}.
         */
        @Override
        public T set(int index, T element) {
            Requires.range(index < size(), "index");

            T result = get(index);
            elements[index] = element;
            return result;
        }

        /**
         * Returns an immutable copy of the current contents of this collection.
         *
         * @return An {@link ImmutableArrayList}.
         */
        public ImmutableArrayList<T> toImmutable() {
            if (isEmpty()) {
                return empty();
            }

            return new ImmutableArrayList<T>(Arrays.copyOf(elements, count));
        }

        /**
         * Extracts the internal array as an {@link ImmutableArrayList} and replaces it with a zero-length array.
         *
         * @return An {@link ImmutableArrayList}.
         * @throws IllegalStateException if {@link #size()} doesn't equal {@link #getCapacity()}.
         */
        public ImmutableArrayList<T> moveToImmutable() {
            if (getCapacity() != size()) {
                throw new IllegalStateException("moveToImmutable can only be performed when size equals capacity.");
            }

            T[] temp = elements;
            elements = ImmutableArrayList.<T>empty().array;
            count = 0;
            return new ImmutableArrayList<T>(temp);
        }

        /**
         * Removes all items from the collection.
         */
        @Override
        public void clear() {
            resize(0);
        }

        /**
         * Inserts an item to the list at the specified index.
         *
         * @param index The zero-based index at which {@code element} should be inserted.
         * @param element The object to insert into the list.
         * @throws IndexOutOfBoundsException if {@code index} is less than 0 or greater than or equal to
         * {@link #size()}.
         */
        @Override
        public void add(int index, T element) {
            Requires.range(index >= 0 && index <= size(), "index");

            ensureCapacity(size() + 1);
            if (index < count) {
                System.arraycopy(elements, index, elements, index + 1, size() - index);
            }

            count++;
            elements[index] = element;
        }

        /**
         * Adds an item to the collection.
         *
         * @param e The object to add to the collection.
         * @return This method always returns {@code true}.
         */
        @Override
        public boolean add(T e) {
            ensureCapacity(size() + 1);
            elements[count++] = e;
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            if (c.isEmpty()) {
                return false;
            }

            ensureCapacity(size() + c.size());
            for (T element : c) {
                add(element);
            }

            return true;
        }

        /**
         * Adds the specified items to the end of the array.
         *
         * @param items The items to add.
         * @return {@code true} if the collection changed as a result of the operation; otherwise, {@code false}.
         */
        public boolean addAll(Iterable<? extends T> items) {
            Requires.notNull(items, "items");

            Integer addedCount = Immutables.tryGetCount(items);
            if (addedCount != null) {
                ensureCapacity(size() + addedCount);
            }

            boolean result = false;
            for (T item : items) {
                add(item);
                result = true;
            }

            return result;
        }

        /**
         * Adds the specified items to the end of the array.
         *
         * @param items The items to add.
         */
        public void addAll(T... items) {
            Requires.notNull(items, "items");

            int offset = size();
            resize(size() + items.length);
            System.arraycopy(items, 0, elements, offset, items.length);
        }

        public void addAll(T[] items, int length) {
            Requires.notNull(items, "items");
            Requires.range(length >= 0, "length");

            int offset = size();
            resize(offset + length);

            System.arraycopy(items, 0, elements, offset, length);
        }

        /**
         * Adds the specified items to the end of the array.
         *
         * @param items The items to add.
         */
        public void addAll(ImmutableArrayList<? extends T> items) {
            Requires.notNull(items, "items");
            addAll(items, items.size());
        }

        public void addAll(ImmutableArrayList<? extends T> items, int length) {
            Requires.range(length >= 0, "length");
            addAll(items.array, length);
        }

        /**
         * Adds the specified items to the end of the array.
         *
         * @param items The items to add.
         */
        public void addAll(Builder<? extends T> items) {
            Requires.notNull(items, "items");
            addAll(items.elements, items.size());
        }

        /**
         * Removes the specified element from the array.
         *
         * @param o The element to remove.
         * @return {@code true} if the array changed as a result of the operation; otherwise, {@code false}.
         */
        @Override
        public boolean remove(Object o) {
            int index = indexOf(o);
            if (index >= 0) {
                remove(index);
                return true;
            }

            return false;
        }

        /**
         * Removes the element at the specified {@code index}.
         *
         * @param index The index of the element to remove from the array.
         * @return The value stored at the specified index prior to its removal.
         * @exception IndexOutOfBoundsException if {@code index < 0} or {@code index >= }{@link #size()}.
         */
        @Override
        public T remove(int index) {
            Requires.range(index >= 0 && index < size(), "index");

            T value = get(index);
            if (index < size() - 1) {
                System.arraycopy(elements, index + 1, elements, index, size() - index - 1);
            }

            resize(size() - 1);
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        @Override
        public Object[] toArray() {
            return Arrays.copyOf(elements, count);
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return Arrays.asList(elements).subList(0, count).toArray(a);
        }

        @Override
        public int indexOf(Object o) {
            return indexOf((T)o, 0, count, EqualityComparators.defaultComparator());
        }

        public int indexOf(T o, int startIndex) {
            return indexOf(o, startIndex, size() - startIndex, EqualityComparators.defaultComparator());
        }

        public int indexOf(T o, int startIndex, int count) {
            return indexOf(o, startIndex, count, EqualityComparators.defaultComparator());
        }

        public int indexOf(T o, int startIndex, int count, EqualityComparator<? super T> equalityComparator) {
            Requires.notNull(equalityComparator, "equalityComparator");

            if (count == 0 && startIndex == 0) {
                return -1;
            }

            Requires.range(startIndex >= 0 && startIndex < size(), "startIndex");
            Requires.range(count >= 0 && startIndex + count <= size(), "count");

            for (int i = startIndex; i < startIndex + count; i++) {
                if (equalityComparator.equals(elements[i], o)) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            if (isEmpty()) {
                return -1;
            }

            return lastIndexOf((T)o, size() - 1, size(), EqualityComparators.defaultComparator());
        }

        public int lastIndexOf(T o, int startIndex) {
            if (size() == 0 && startIndex == 0) {
                return -1;
            }

            Requires.range(startIndex >= 0 && startIndex < size(), "startIndex");

            return lastIndexOf(o, startIndex, startIndex + 1, EqualityComparators.defaultComparator());
        }

        public int lastIndexOf(T o, int startIndex, int count) {
            return lastIndexOf(o, startIndex, count, EqualityComparators.defaultComparator());
        }

        public int lastIndexOf(T o, int startIndex, int count, EqualityComparator<? super T> equalityComparator) {
            Requires.notNull(equalityComparator, "equalityComparator");

            if (count == 0 && startIndex == 0) {
                return -1;
            }

            Requires.range(startIndex >= 0 && startIndex < size(), "startIndex");
            Requires.range(count >= 0 && startIndex - count + 1 >= 0, "count");

            if (equalityComparator == EqualityComparators.defaultComparator()) {
                int result = Arrays.asList(elements).subList(startIndex - count + 1, startIndex + 1).lastIndexOf(o);
                if (result >= 0) {
                    result += startIndex - count + 1;
                }

                return result;
            } else {
                for (int i = startIndex; i >= startIndex - count + 1; i--) {
                    if (equalityComparator.equals(o, elements[i])) {
                        return i;
                    }
                }

                return -1;
            }
        }

        /**
         * Reverses the order of elements in the collection.
         */
        public void reverse() {
            int i = 0;
            int j = count - 1;
            while (i < j) {
                T temp = elements[i];
                elements[i] = elements[j];
                elements[j] = temp;
                i++;
                j--;
            }
        }

        /**
         * Sorts the collection according to the natural {@link Comparable} order of the elements.
         *
         * @see Arrays#sort(Object[])
         */
        public void sort() {
            sort(0, count, null);
        }

        /**
         * Sorts the collection using the specified {@link Comparator} to compare elements.
         *
         * @param comparator The {@link Comparator} to use for comparing elements, or {@code null} to sort the elements
         * according to their natural {@link Comparable} order.
         *
         * @see Arrays#sort(Object[], Comparator)
         */
        public void sort(Comparator<? super T> comparator) {
            sort(0, count, comparator);
        }

        public void sort(int index, int count, Comparator<? super T> comparator) {
            Requires.range(index >= 0, "index");
            Requires.range(count >= 0 && index + count <= size(), "count");
            Arrays.sort(elements, index, index + count, comparator);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<T> iterator() {
            return Arrays.asList(elements).subList(0, count).iterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListIterator<T> listIterator() {
            return Arrays.asList(elements).subList(0, count).listIterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListIterator<T> listIterator(int index) {
            return Arrays.asList(elements).subList(0, count).listIterator(index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            Requires.notNull(c, "c");
            for (Object o : c) {
                if (!contains(o)) {
                    return false;
                }
            }

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            Requires.notNull(c, "c");
            Requires.range(index >= 0 && index <= size(), "index");

            if (c.isEmpty()) {
                return false;
            }

            ArrayList<T> intermediate = new ArrayList<T>(c);
            ensureCapacity(size() + intermediate.size());
            count += intermediate.size();
            if (index < size()) {
                System.arraycopy(elements, index, elements, index + intermediate.size(), size() - index);
            }

            for (int i = 0; i < intermediate.size(); i++) {
                elements[index + i] = intermediate.get(i);
            }

            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String toString() {
            return Arrays.asList(elements).subList(0, count).toString();
        }

        private void ensureCapacity(int capacity) {
            if (elements.length < capacity) {
                elements = Arrays.copyOf(elements, Math.max(elements.length * 2, capacity));
            }
        }
    }

    public final class Itr implements Iterator<T> {

        int cursor;
        int lastRet = -1;

        @Override
        public boolean hasNext() {
            return cursor != size();
        }

        @Override
        public T next() {
            int i = cursor;
            if (i >= size()) {
                throw new NoSuchElementException();
            }

            cursor = i + 1;
            return array[lastRet = i];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
