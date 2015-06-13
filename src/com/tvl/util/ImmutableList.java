// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.tvl.util.function.Predicate;

/**
 * A list of elements that can only be modified by creating a new instance of the list.
 *
 * Mutations on this list generate new lists. Incremental changes to a list share as much memory as possible with the
 * prior versions of a list, while allowing garbage collection to clean up any unique list data that is no longer being
 * referenced.
 *
 * @param <T> The type of element stored in the list.
 */
public interface ImmutableList<T> extends ReadOnlyList<T> {

    /**
     * Gets an empty list that retains the same sort or unordered semantics that this instance has.
     *
     * @return The empty list.
     */
    ImmutableList<T> clear();

    /**
     * Searches for the specified object and returns the zero-based index of the first occurrence within the range of
     * elements in the {@link ImmutableList} that extends from {@code startIndex} through (but not including)
     * {@code toIndex}.
     *
     * @param item The object to locate in the {@link ImmutableList}, which can be {@code null}.
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param toIndex The index of the last element (exclusive) to be searched.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The zero-based index of the first occurrence of {@code item} within the range of elements that extends
     * from {@code startIndex} through (but not including) {@code toIndex}, if found;
     * otherwise, -1.
     */
    int indexOf(T item, int fromIndex, int toIndex, EqualityComparator<? super T> equalityComparator);

    /**
     * Searches for the specified object and returns the zero-based index of the last occurrence within the range of
     * elements in the {@link ImmutableList} that contains the specified number of elements and ends at the specified
     * index.
     *
     * @param item The object to locate in the {@link ImmutableList}. The value can be {@code null} for reference types.
     * @param index The starting position of the search. The search proceeds from {@code index} toward the beginning of
     * this instance.
     * @param count The number of elements in the section to search.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The zero-based index of the last occurrence of {@code item} within the range of elements in the
     * {@link ImmutableList} that contains {@code count} number of elements and ends at {@code index}, if found;
     * otherwise, -1.
     */
    int lastIndexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator);

    /**
     * Adds the specified value to this list.
     *
     * @param value The value to add.
     * @return A new list with the element added.
     */
    ImmutableList<T> add(T value);

    /**
     * Adds the specified values to this list.
     *
     * @param items The values to add.
     * @return A new list with the elements added.
     */
    ImmutableList<T> addAll(Iterable<? extends T> items);

    /**
     * Inserts the specified value at the specified index.
     *
     * @param index The index at which to insert the value.
     * @param element The element to insert.
     * @return The new immutable list.
     */
    ImmutableList<T> add(int index, T element);

    /**
     * Inserts the specified values at the specified index.
     *
     * @param index The index at which to insert the values.
     * @param items The elements to insert.
     * @return The new immutable list.
     */
    ImmutableList<T> addAll(int index, Iterable<? extends T> items);

    /**
     * Removes the specified value from the list.
     *
     * @param value The value to remove.
     * @param equalityComparator The equality comparator to use in the search.
     * @return A new list with the element removed, or this list if the element is not in this list.
     */
    ImmutableList<T> remove(T value, EqualityComparator<? super T> equalityComparator);

    /**
     * Removes all the elements that match the conditions defined by the specified predicate.
     *
     * @param predicate The {@link Predicate} that defines the conditions of the elements to remove.
     * @return The new list.
     */
    ImmutableList<T> removeIf(Predicate<? super T> predicate);

    /**
     * Removes the specified values from this list.
     *
     * @param items The items to remove if matches are found in this list.
     * @param equalityComparator The equality comparator to use in the search.
     * @return A new list with the elements removed.
     */
    ImmutableList<T> removeAll(Iterable<? extends T> items, EqualityComparator<? super T> equalityComparator);

    /**
     * Removes the element at the specified index.
     *
     * @param index The index.
     * @return A new list with the element removed.
     */
    ImmutableList<T> remove(int index);

    /**
     * Remove the specified values from this list.
     *
     * @param fromIndex The index of the first element (inclusive) to be removed.
     * @param toIndex The index of the last element (exclusive) to be removed.
     * @return A new list with the elements removed.
     */
    ImmutableList<T> removeAll(int fromIndex, int toIndex);

    /**
     * Replaces an element in the list at a given position with the specified element.
     *
     * @param index The position in the list of the element to replace.
     * @param value The element to replace the old element with.
     * @return The new list, even if the value being replaced is equal to the new value for that position.
     */
    ImmutableList<T> set(int index, T value);

    /**
     * Replaces the first equal element in the list with the specified element.
     *
     * @param oldValue The element to replace.
     * @param newValue The element to replace the old element with.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The new list, even if the value being replaced is equal to the new value for that position.
     * @throws IllegalArgumentException if the old value does not exist in the list.
     */
    ImmutableList<T> replace(T oldValue, T newValue, EqualityComparator<? super T> equalityComparator);

}
