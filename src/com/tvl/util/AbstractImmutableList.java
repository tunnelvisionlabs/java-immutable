// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public abstract class AbstractImmutableList<T> implements ImmutableList<T> {

    /**
     * Replaces the first equal element in the list with the specified element.
     *
     * @param oldValue The element to replace.
     * @param newValue The element to replace the old element with.
     * @return The new list, even if the value being replaced is equal to the new value for that position.
     * @throws IllegalArgumentException if the old value does not exist in the list.
     */
    @Nonnull
    @CheckReturnValue
    public ImmutableList<T> replace(T oldValue, T newValue) {
        return replace(oldValue, newValue, EqualityComparators.defaultComparator());
    }

    /**
     * Returns a list with the first occurrence of the specified element removed from the list.
     *
     * <p>If no match is found, the current list is returned.</p>
     *
     * @param value The item to remove.
     * @return The new immutable list.
     */
    @Nonnull
    @CheckReturnValue
    public ImmutableList<T> remove(T value) {
        return remove(value, EqualityComparators.defaultComparator());
    }

    /**
     * Removes the specified values from this list.
     *
     * @param items The items to remove if matches are found in this list.
     * @return A new list with the elements removed.
     */
    @Nonnull
    @CheckReturnValue
    public ImmutableList<T> removeAll(@Nonnull Iterable<? extends T> items) {
        return removeAll(items, EqualityComparators.defaultComparator());
    }

    /**
     * Searches the list for the specified item.
     *
     * @param item The item to search for.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int indexOf(T item) {
        return indexOf(item, 0, size(), EqualityComparators.defaultComparator());
    }

    /**
     * Searches for the specified object and returns the zero-based index of the first occurrence within the entire
     * immutable list.
     *
     * @param item The object to locate in the {@link ImmutableList}, which can be {@code null}.
     * @param comparator The equality comparator to use in the search.
     * @return The zero-based index of the first occurrence of {@code item} within the immutable list, if found;
     * otherwise, -1.
     */
    public int indexOf(T item, @Nonnull EqualityComparator<? super T> comparator) {
        return indexOf(item, 0, size(), comparator);
    }

    /**
     * Searches the list for the specified item.
     *
     * @param item The item to search for.
     * @param fromIndex The index at which to begin the search.
     * @return The zero-based index into the list where the item was found; or -1 if it could not be found.
     */
    public int indexOf(T item, int fromIndex) {
        return indexOf(item, fromIndex, size(), EqualityComparators.defaultComparator());
    }

    /**
     * Searches for the specified object and returns the zero-based index of the first occurrence within the range of
     * elements in the immutable list that extends from {@code startIndex} through (but not including) {@code toIndex}.
     *
     * @param item The object to locate in the {@link ImmutableList}, which can be {@code null}.
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param toIndex The index of the last element (exclusive) to be searched.
     * @return The zero-based index of the first occurrence of {@code item} within the range of elements that extends
     * from {@code startIndex} through (but not including) {@code toIndex}, if found;
     * otherwise, -1.
     */
    public int indexOf(T item, int fromIndex, int toIndex) {
        return indexOf(item, fromIndex, toIndex, EqualityComparators.defaultComparator());
    }

    /**
     * Searches for the specified object and returns the zero-based index of the last occurrence within the entire
     * immutable list.
     *
     * @param item The object to locate in the {@link ImmutableList}, which can be {@code null}.
     * @return The zero-based index of the last occurrence of {@code item} within the immutable list, if found;
     * otherwise, -1.
     */
    public int lastIndexOf(T item) {
        if (isEmpty()) {
            return -1;
        }

        return lastIndexOf(item, 0, size(), EqualityComparators.defaultComparator());
    }

    /**
     * Searches for the specified object and returns the zero-based index of the last occurrence within the entire
     * immutable list.
     *
     * @param item The object to locate in the {@link ImmutableList}, which can be {@code null}.
     * @param comparator The equality comparator to use in the search.
     * @return The zero-based index of the last occurrence of {@code item} within the immutable list, if found;
     * otherwise, -1.
     */
    public int lastIndexOf(T item, @Nonnull EqualityComparator<? super T> comparator) {
        if (isEmpty()) {
            return -1;
        }

        return lastIndexOf(item, 0, size(), comparator);
    }

    /**
     * Searches the list for the specified item in reverse.
     *
     * @param item The item to search for.
     * @param fromIndex The index at which to begin the search.
     * @return The zero-based index into the list where the item was found; or -1 if it could not be found.
     */
    public int lastIndexOf(T item, int fromIndex) {
        if (isEmpty() && fromIndex == 0) {
            return -1;
        }

        return lastIndexOf(item, fromIndex, size(), EqualityComparators.defaultComparator());
    }

    /**
     * Searches the list for the specified item in reverse.
     *
     * @param item The item to search for.
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param toIndex The index of the last element (exclusive) to be searched.
     * @return The zero-based index into the array where the item was found; or -1 if it could not be found.
     */
    public int lastIndexOf(T item, int fromIndex, int toIndex) {
        return lastIndexOf(item, fromIndex, toIndex, EqualityComparators.defaultComparator());
    }
}
