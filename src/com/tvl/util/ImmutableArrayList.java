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
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An immutable array with O(1) indexable lookup time.
 *
 * @param <T> The type of element stored by the array.
 */
public final class ImmutableArrayList<T> extends AbstractImmutableList<T> implements ImmutableList<T>, ReadOnlyList<T> {

    @Nonnull
    public static final ImmutableArrayList<?> EMPTY_ARRAY = new ImmutableArrayList<Object>(new Object[0]);

    @Nonnull
    private final T[] array;

    private ImmutableArrayList(@Nonnull T[] items) {
        this.array = items;
    }

    /**
     * Gets an empty {@link ImmutableArrayList} instance.
     *
     * @param <T> The type of elements stored in the array.
     * @return An empty immutable array.
     */
    @Nonnull
    public static <T> ImmutableArrayList<T> empty() {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        ImmutableArrayList<T> emptyArray = (ImmutableArrayList<T>)EMPTY_ARRAY;
        return emptyArray;
    }

    /**
     * Creates an empty {@link ImmutableArrayList}.
     *
     * @param <T> The type of elements stored in the array.
     * @return An empty immutable array.
     */
    @Nonnull
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
    @Nonnull
    public static <T> ImmutableArrayList<T> create(T item) {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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
    @Nonnull
    public static <T> ImmutableArrayList<T> create(T item1, T item2) {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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
    @Nonnull
    public static <T> ImmutableArrayList<T> create(T item1, T item2, T item3) {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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
    @Nonnull
    public static <T> ImmutableArrayList<T> create(T item1, T item2, T item3, T item4) {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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
    @Nonnull
    public static <T> ImmutableArrayList<T> create(@Nonnull T... items) {
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
    @Nonnull
    public static <T> ImmutableArrayList<T> createAll(@Nonnull Iterable<? extends T> items) {
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

    /**
     * Creates a new immutable array from the specified range of elements in an existing array.
     *
     * @param <T> The type of element stored in the array.
     * @param items The array to initialize the immutable array with. A defensive copy is made.
     * @param fromIndex The index of the first element (inclusive) to include in the immutable array.
     * @param toIndex The index of the last element (exclusive) to include in the immutable array.
     * @return The new immutable array.
     */
    @Nonnull
    public static <T> ImmutableArrayList<T> createAll(@Nonnull T[] items, int fromIndex, int toIndex) {
        Requires.notNull(items, "items");
        Requires.range(fromIndex >= 0 && fromIndex <= items.length, "fromIndex");
        Requires.range(toIndex >= 0 && toIndex <= items.length, "toIndex");
        Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");

        if (fromIndex == toIndex) {
            // avoid allocating a new array
            return create();
        }

        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        T[] array = (T[])new Object[toIndex - fromIndex];
        System.arraycopy(items, fromIndex, array, 0, array.length);
        return new ImmutableArrayList<T>(array);
    }

    /**
     * Creates a new immutable array from the specified range of elements in an existing array.
     *
     * @param <T> The type of element stored in the array.
     * @param items The array to initialize the immutable array with. A defensive copy is made only when the specified
     * range does not include all elements of the source array.
     * @param fromIndex The index of the first element (inclusive) to include in the immutable array.
     * @param toIndex The index of the last element (exclusive) to include in the immutable array.
     * @return The new immutable array.
     */
    @Nonnull
    public static <T> ImmutableArrayList<T> createAll(@Nonnull ImmutableArrayList<T> items, int fromIndex, int toIndex) {
        Requires.notNull(items, "items");
        Requires.range(fromIndex >= 0 && fromIndex <= items.size(), "fromIndex");
        Requires.range(toIndex >= 0 && toIndex <= items.size(), "toIndex");
        Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");

        if (fromIndex == toIndex) {
            // avoid allocating a new array
            return create();
        }

        if (fromIndex == 0 && toIndex == items.size()) {
            return items;
        }

        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        T[] array = (T[])new Object[toIndex - fromIndex];
        System.arraycopy(items.array, fromIndex, array, 0, array.length);
        return new ImmutableArrayList<T>(array);
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
    @Nonnull
    public static <Source, Result> ImmutableArrayList<Result> createAll(@Nonnull ImmutableArrayList<Source> items, @Nonnull Function<? super Source, Result> selector) {
        Requires.notNull(selector, "selector");

        int length = items.size();
        if (length == 0) {
            return create();
        }

        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        Result[] array = (Result[])new Object[length];
        for (int i = 0; i < length; i++) {
            array[i] = selector.apply(items.get(i));
        }

        return new ImmutableArrayList<Result>(array);
    }

    /**
     * Creates an immutable array by applying a transformation function to the elements of an existing array.
     *
     * @param <Source> The type of elements stored in the source array.
     * @param <Result> The type of elements stored in the target array.
     * @param items The existing immutable array.
     * @param fromIndex The index of the first element (inclusive) to include in the immutable array.
     * @param toIndex The index of the last element (exclusive) to include in the immutable array.
     * @param selector The transformation function to apply to each element of {@code items} to obtain the target array.
     * @return A new immutable array containing the transformed elements.
     */
    @Nonnull
    public static <Source, Result> ImmutableArrayList<Result> createAll(@Nonnull ImmutableArrayList<Source> items, int fromIndex, int toIndex, @Nonnull Function<? super Source, Result> selector) {
        Requires.notNull(items, "items");
        Requires.range(fromIndex >= 0 && fromIndex <= items.size(), "fromIndex");
        Requires.range(toIndex >= 0 && toIndex <= items.size(), "toIndex");
        Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");
        Requires.notNull(selector, "selector");

        if (fromIndex == toIndex) {
            // avoid allocating a new array
            return create();
        }

        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        Result[] array = (Result[])new Object[toIndex - fromIndex];
        for (int i = 0; i < array.length; i++) {
            array[i] = selector.apply(items.get(i + fromIndex));
        }

        return new ImmutableArrayList<Result>(array);
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
    @Nonnull
    public static <Source, Arg, Result> ImmutableArrayList<Result> createAll(@Nonnull ImmutableArrayList<Source> items, @Nonnull BiFunction<? super Source, Arg, Result> selector, Arg arg) {
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

    /**
     * Creates an immutable array by applying a transformation function to a range of elements of an existing array.
     *
     * @param <Source> The type of elements stored in the source array.
     * @param <Arg> The type of the additional argument to the transformation function.
     * @param <Result> The type of elements stored in the target array.
     * @param items The existing immutable array.
     * @param fromIndex The index of the first element (inclusive) to include in the immutable array.
     * @param toIndex The index of the last element (exclusive) to include in the immutable array.
     * @param selector The transformation function to apply to each element of {@code items} to obtain the target array.
     * @param arg An additional argument to pass to the transformation function.
     * @return A new immutable array containing the transformed elements.
     */
    @Nonnull
    public static <Source, Arg, Result> ImmutableArrayList<Result> createAll(@Nonnull ImmutableArrayList<Source> items, int fromIndex, int toIndex, @Nonnull BiFunction<? super Source, Arg, Result> selector, Arg arg) {
        Requires.notNull(items, "items");
        Requires.notNull(selector, "selector");
        Requires.range(fromIndex >= 0 && fromIndex <= items.size(), "fromIndex");
        Requires.range(toIndex >= 0 && toIndex <= items.size(), "toIndex");
        Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");

        if (fromIndex == toIndex) {
            // avoid allocating a new array
            return create();
        }

        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        Result[] array = (Result[])new Object[toIndex - fromIndex];
        for (int i = 0; i < array.length; i++) {
            array[i] = selector.apply(items.get(i + fromIndex), arg);
        }

        return new ImmutableArrayList<Result>(array);
    }

    /**
     * Creates a new instance of the {@link Builder} class.
     *
     * @param <T> The type of elements stored in the array.
     * @return A new builder.
     */
    @Nonnull
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
    @Nonnull
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
    public static <T> int binarySearch(@Nonnull ImmutableArrayList<T> array, T value) {
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
    public static <T> int binarySearch(@Nonnull ImmutableArrayList<T> array, T value, @Nullable Comparator<? super T> comparator) {
        Requires.notNull(array, "array");
        return binarySearch(array, value, 0, array.size(), comparator);
    }

    /**
     * Searches a range of the specified array for the specified object using the binary search algorithm. The range
     * must be sorted into ascending {@linkplain Comparable natural ordering} prior to making this call. If it is not
     * sorted, the results are unspecified. If the range contains multiple elements equal to the specified object, there
     * is no guarantee which one will be found.
     *
     * @param <T> The type of elements stored in the array.
     * @param array The array to be searched.
     * @param value The value to search for.
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param toIndex The index of the last element (exclusive) to be searched.
     * @return The index of the specified {@code value} in the specified array, if {@code value} is found. If
     * {@code value} is not found and {@code value} is less than one or more elements in {@code array}, a negative
     * number which is the bitwise complement of the index of the first element that is larger than {@code value}. If
     * {@code value} is not found and {@code value} is greater than all of the elements in {@code array}, a negative
     * number which is the bitwise complement of (the index of the last element plus 1).
     *
     * @throws ClassCastException If {@code value} is not comparable to the elements of the array within the specified
     * range.
     * @throws IllegalArgumentException if {@code fromIndex > toIndex}
     * @throws IndexOutOfBoundsException if {@code fromIndex < 0 or toIndex > array.size()}.
     */
    public static <T> int binarySearch(@Nonnull ImmutableArrayList<T> array, T value, int fromIndex, int toIndex) {
        return binarySearch(array, value, fromIndex, toIndex, null);
    }

    /**
     * Searches a range of the specified array for the specified object using the binary search algorithm. The range
     * must be sorted into ascending order according to the specified comparator (as by the
     * {@link #sort(int, int, Comparator) sort(int, int, Comparator)} method) prior to making this
     * call. If it is not sorted, the results are unspecified. If the range contains multiple elements equal to the
     * specified object, there is no guarantee which one will be found.
     *
     * @param <T> The type of elements stored in the array.
     * @param array The array to be searched.
     * @param value The value to search for.
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param toIndex The index of the last element (exclusive) to be searched.
     * @param comparator The comparator by which the array is ordered. A {@code null} value indicates that the elements'
     * {@linkplain Comparable natural ordering} should be used.
     * @return The index of the specified {@code value} in the specified array, if {@code value} is found. If
     * {@code value} is not found and {@code value} is less than one or more elements in {@code array}, a negative
     * number which is the bitwise complement of the index of the first element that is larger than {@code value}. If
     * {@code value} is not found and {@code value} is greater than all of the elements in {@code array}, a negative
     * number which is the bitwise complement of (the index of the last element plus 1).
     *
     * @throws ClassCastException if the range contains elements that are not <i>mutually comparable</i> using the
     * specified comparator, or the search key is not comparable to the elements in the range using this comparator.
     * @throws IllegalArgumentException if {@code fromIndex > toIndex}
     * @throws IndexOutOfBoundsException if {@code fromIndex < 0 or toIndex > array.size()}.
     */
    public static <T> int binarySearch(@Nonnull ImmutableArrayList<T> array, T value, int fromIndex, int toIndex, @Nullable Comparator<? super T> comparator) {
        Requires.notNull(array, "array");
        if (comparator == null) {
            comparator = Comparators.anyComparator();
        }

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
     * @param fromIndex The index at which to begin the search.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int indexOf(T item, int fromIndex, @Nonnull EqualityComparator<? super T> equalityComparator) {
        return indexOf(item, fromIndex, size(), equalityComparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(T item, int fromIndex, int toIndex, @Nonnull EqualityComparator<? super T> equalityComparator) {
        Requires.notNull(equalityComparator, "equalityComparator");
        Requires.range(fromIndex >= 0 && fromIndex <= size(), "fromIndex");
        Requires.range(toIndex >= 0 && toIndex <= size(), "toIndex");
        Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");

        for (int i = fromIndex; i < toIndex; i++) {
            if (equalityComparator.equals(array[i], item)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(T item, int fromIndex, int toIndex, @Nonnull EqualityComparator<? super T> equalityComparator) {
        Requires.notNull(equalityComparator, "equalityComparator");
        Requires.range(fromIndex >= 0 && fromIndex <= size(), "fromIndex");
        Requires.range(toIndex >= 0 && toIndex <= size(), "toIndex");
        Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");

        for (int i = toIndex - 1; i >= fromIndex; i--) {
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
    public void copyTo(@Nonnull T[] destination) {
        System.arraycopy(array, 0, destination, 0, size());
    }

    /**
     * Copies the elements of this array to the specified array.
     *
     * @param destination The array to copy to.
     * @param destinationIndex The index into the destination array to which the first copied element is written.
     */
    public void copyTo(@Nonnull T[] destination, int destinationIndex) {
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
    public void copyTo(int sourceIndex, @Nonnull T[] destination, int destinationIndex, int length) {
        System.arraycopy(array, sourceIndex, destination, destinationIndex, length);
    }

    /**
     * Returns a new array with the specified value inserted at the specified location.
     *
     * @param index The zero-based index into the array at which the new item should be added.
     * @param element The item to insert into the array.
     * @return A new {@link ImmutableArrayList}.
     */
    @Nonnull
    @CheckReturnValue
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
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> addAll(int index, @Nonnull Iterable<? extends T> items) {
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
    @Nonnull
    @CheckReturnValue
    public ImmutableArrayList<T> addAll(int index, @Nonnull ImmutableArrayList<? extends T> items) {
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
    @Nonnull
    @CheckReturnValue
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
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> addAll(@Nonnull Iterable<? extends T> items) {
        return addAll(size(), items);
    }

    /**
     * Adds the specified values to the array.
     *
     * @param items The values to add.
     * @return A new immutable array with the elements added.
     */
    @Nonnull
    @CheckReturnValue
    public ImmutableArrayList<T> addAll(@Nonnull ImmutableArrayList<? extends T> items) {
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
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> set(int index, T value) {
        Requires.range(index >= 0 && index <= size(), "index");

        T[] tmp = array.clone();
        tmp[index] = value;
        return new ImmutableArrayList<T>(tmp);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @CheckReturnValue
    @Override
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
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> replace(T oldValue, T newValue, @Nonnull EqualityComparator<? super T> equalityComparator) {
        int index = indexOf(oldValue, 0, equalityComparator);
        if (index < 0) {
            throw new IllegalArgumentException("Cannot find the old value");
        }

        return set(index, newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> remove(T value) {
        return remove(value, EqualityComparators.defaultComparator());
    }

    /**
     * Returns an array with the first occurrence of the specified element removed from the array.
     *
     * <p>If no match is found, the current array is returned.</p>
     *
     * @param value The item to remove.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The new immutable array.
     */
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> remove(T value, @Nonnull EqualityComparator<? super T> equalityComparator) {
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
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> remove(int index) {
        return removeAll(index, index + 1);
    }

    /**
     * Returns an array with the elements at the specified position removed.
     *
     * @param fromIndex The index of the first element (inclusive) to be removed.
     * @param toIndex The index of the last element (exclusive) to be removed.
     * @return The new immutable array.
     */
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> removeAll(int fromIndex, int toIndex) {
        Requires.range(fromIndex >= 0 && fromIndex <= size(), "fromIndex");
        Requires.range(toIndex >= 0 && toIndex <= size(), "toIndex");
        Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");

        int count = toIndex - fromIndex;
        if (count == 0) {
            return this;
        }

        T[] tmp = Arrays.copyOf(array, array.length - count);
        System.arraycopy(array, toIndex, tmp, fromIndex, size() - toIndex);
        return new ImmutableArrayList<T>(tmp);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> removeAll(@Nonnull Iterable<? extends T> items) {
        return removeAll(items, EqualityComparators.defaultComparator());
    }

    /**
     * Removes the specified values from this array.
     *
     * @param items The items to remove if matches are found in this array.
     * @param equalityComparator The equality comparator to use in the search.
     * @return A new immutable array with the elements removed.
     */
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> removeAll(@Nonnull Iterable<? extends T> items, @Nonnull EqualityComparator<? super T> equalityComparator) {
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
    @Nonnull
    @CheckReturnValue
    public ImmutableArrayList<T> removeAll(@Nonnull ImmutableArrayList<? extends T> items) {
        return removeAll(items, EqualityComparators.defaultComparator());
    }

    /**
     * Removes the specified values from this array.
     *
     * @param items The items to remove if matches are found in this array.
     * @param equalityComparator The equality comparator to use in the search.
     * @return A new immutable array with the elements removed.
     */
    @Nonnull
    @CheckReturnValue
    public ImmutableArrayList<T> removeAll(@Nonnull ImmutableArrayList<? extends T> items, @Nonnull EqualityComparator<? super T> equalityComparator) {
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
     * Removes all the elements that match the conditions defined by the specified predicate.
     *
     * @param predicate The predicate that defines the conditions of the elements to remove.
     * @return The new immutable array.
     */
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> removeIf(@Nonnull Predicate<? super T> predicate) {
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
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableArrayList<T> clear() {
        return empty();
    }

    /**
     * Returns a sorted instance of this array.
     *
     * @return A sorted instance of this array.
     */
    @Nonnull
    @CheckReturnValue
    public ImmutableArrayList<T> sort() {
        return sort(0, size(), null);
    }

    /**
     * Returns a sorted instance of this array.
     *
     * @param comparator The comparator to use in sorting. If {@code null}, a default comparator is used.
     * @return A sorted instance of this array.
     */
    @Nonnull
    @CheckReturnValue
    public ImmutableArrayList<T> sort(@Nullable Comparator<? super T> comparator) {
        return sort(0, size(), comparator);
    }

    /**
     * Returns a sorted instance of this array.
     *
     * @param fromIndex The index of the first element (inclusive) to be sorted.
     * @param toIndex The index of the last element (exclusive) to be sorted.
     * @param comparator The comparator to use in sorting. If {@code null}, a default comparator is used.
     * @return A sorted instance of this array.
     */
    @Nonnull
    @CheckReturnValue
    public ImmutableArrayList<T> sort(int fromIndex, int toIndex, @Nullable Comparator<? super T> comparator) {
        Requires.range(fromIndex >= 0 && fromIndex <= size(), "fromIndex");
        Requires.range(toIndex >= 0 && toIndex <= size(), "toIndex");
        Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");

        if (comparator == null) {
            comparator = Comparators.anyComparator();
        }

        int count = toIndex - fromIndex;

        // 0 and 1 element arrays don't need to be sorted.
        if (count > 1) {
            // Avoid copying the entire array when the array is already sorted.
            boolean outOfOrder = false;
            for (int i = fromIndex + 1; i < toIndex; i++) {
                if (comparator.compare(array[i - 1], array[i]) > 0) {
                    outOfOrder = true;
                    break;
                }
            }

            if (outOfOrder) {
                Builder<T> builder = toBuilder();
                builder.sort(fromIndex, toIndex, comparator);
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
    @Nonnull
    @CheckReturnValue
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
    @Nonnull
    @CheckReturnValue
    public static <T> ImmutableArrayList<T> castUp(@Nonnull ImmutableArrayList<? extends T> items) {
        // Since this class is immutable, we can actually return the same instance
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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
    @Nonnull
    @CheckReturnValue
    public <Other> ImmutableArrayList<Other> castArray(@Nonnull Class<Other> clazz) {
        Requires.notNull(clazz, "clazz");
        for (T item : this) {
            clazz.cast(item);
        }

        // It is now safe to cast the array.
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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
    @Nullable
    @CheckReturnValue
    public <Other> ImmutableArrayList<Other> as(@Nonnull Class<Other> clazz) {
        Requires.notNull(clazz, "clazz");
        for (T item : this) {
            if (item != null && !clazz.isInstance(item)) {
                return null;
            }
        }

        // It is now safe to cast the array.
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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
    @Nonnull
    @CheckReturnValue
    public <Result> Iterable<Result> ofType(@Nonnull final Class<Result> clazz) {
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

            @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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
        @Nonnull
        @CheckReturnValue
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
        @Nonnull
        @CheckReturnValue
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
        public boolean addAll(@Nonnull Collection<? extends T> c) {
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
        public boolean addAll(@Nonnull Iterable<? extends T> items) {
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
        public void addAll(@Nonnull T... items) {
            Requires.notNull(items, "items");

            int offset = size();
            resize(size() + items.length);
            System.arraycopy(items, 0, elements, offset, items.length);
        }

        public void addAll(@Nonnull T[] items, int length) {
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
        public void addAll(@Nonnull ImmutableArrayList<? extends T> items) {
            Requires.notNull(items, "items");
            addAll(items, items.size());
        }

        public void addAll(@Nonnull ImmutableArrayList<? extends T> items, int length) {
            Requires.range(length >= 0, "length");
            addAll(items.array, length);
        }

        /**
         * Adds the specified items to the end of the array.
         *
         * @param items The items to add.
         */
        public void addAll(@Nonnull Builder<? extends T> items) {
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

        public int indexOf(T o, int startIndex, int count, @Nonnull EqualityComparator<? super T> equalityComparator) {
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
            return lastIndexOf((T)o, 0, size(), EqualityComparators.defaultComparator());
        }

        public int lastIndexOf(T o, int fromIndex) {
            Requires.range(fromIndex >= 0 && fromIndex <= size(), "fromIndex");

            return lastIndexOf(o, fromIndex, size(), EqualityComparators.defaultComparator());
        }

        public int lastIndexOf(T o, int fromIndex, int toIndex) {
            return lastIndexOf(o, fromIndex, toIndex, EqualityComparators.defaultComparator());
        }

        public int lastIndexOf(T o, int fromIndex, int toIndex, @Nonnull EqualityComparator<? super T> equalityComparator) {
            Requires.notNull(equalityComparator, "equalityComparator");
            Requires.range(fromIndex >= 0 && fromIndex <= size(), "fromIndex");
            Requires.range(toIndex >= 0 && toIndex <= size(), "toIndex");
            Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");

            if (equalityComparator == EqualityComparators.defaultComparator()) {
                int result = Arrays.asList(elements).subList(fromIndex, toIndex).lastIndexOf(o);
                if (result >= 0) {
                    result += fromIndex;
                }

                return result;
            } else {
                for (int i = toIndex - 1; i >= fromIndex; i--) {
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
        public void sort(@Nullable Comparator<? super T> comparator) {
            sort(0, count, comparator);
        }

        /**
         * Sorts the specified range of elements in the collection using the specified {@link Comparator} to compare
         * elements.
         *
         * @param fromIndex The index of the first element (inclusive) to be sorted.
         * @param toIndex The index of the last element (exclusive) to be sorted.
         * @param comparator The {@link Comparator} to use for comparing elements, or {@code null} to sort the elements
         * according to their natural {@link Comparable} order.
         *
         * @see Arrays#sort(Object[], int, int, Comparator)
         */
        public void sort(int fromIndex, int toIndex, @Nullable Comparator<? super T> comparator) {
            Requires.range(fromIndex >= 0 && fromIndex <= size(), "fromIndex");
            Requires.range(toIndex >= 0 && toIndex <= size(), "toIndex");
            Requires.argument(fromIndex <= toIndex, "fromIndex", "fromIndex must be less than or equal to toIndex");

            Arrays.sort(elements, fromIndex, toIndex, comparator);
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
        public boolean containsAll(@Nonnull Collection<?> c) {
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
        public boolean addAll(int index, @Nonnull Collection<? extends T> c) {
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
        public boolean removeAll(@Nonnull Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Nonnull
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
            throw new UnsupportedOperationException("This iterator is read-only.");
        }
    }
}
