// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.tvl.util.function.Predicate;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

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
    @Nonnull
    @CheckReturnValue
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
    int indexOf(T item, int fromIndex, int toIndex, @Nonnull EqualityComparator<? super T> equalityComparator);

    /**
     * Searches for the specified object and returns the zero-based index of the last occurrence within the range of
     * elements in the immutable list that extends from {@code startIndex} through (but not including) {@code toIndex}.
     *
     * @param item The object to locate in the {@link ImmutableList}. The value can be {@code null} for reference types.
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param toIndex The index of the last element (exclusive) to be searched.
     * @param equalityComparator The equality comparator to use in the search.
     * @return The zero-based index of the last occurrence of {@code item} within the range of elements in the immutable
     * list that extends from {@code startIndex} through (but not including) {@code toIndex}.
     */
    int lastIndexOf(T item, int fromIndex, int toIndex, @Nonnull EqualityComparator<? super T> equalityComparator);

    /**
     * Adds the specified value to this list.
     *
     * @param value The value to add.
     * @return A new list with the element added.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> add(T value);

    /**
     * Adds the specified values to this list.
     *
     * @param items The values to add.
     * @return A new list with the elements added.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> addAll(@Nonnull Iterable<? extends T> items);

    /**
     * Inserts the specified value at the specified index.
     *
     * @param index The index at which to insert the value.
     * @param element The element to insert.
     * @return The new immutable list.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> add(int index, T element);

    /**
     * Inserts the specified values at the specified index.
     *
     * @param index The index at which to insert the values.
     * @param items The elements to insert.
     * @return The new immutable list.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> addAll(int index, @Nonnull Iterable<? extends T> items);

    /**
     * Removes the specified value from the list.
     *
     * @param value The value to remove.
     * @param equalityComparator The equality comparator to use in the search.
     * @return A new list with the element removed, or this list if the element is not in this list.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> remove(T value, @Nonnull EqualityComparator<? super T> equalityComparator);

    /**
     * Removes all the elements that match the conditions defined by the specified predicate.
     *
     * @param predicate The {@link Predicate} that defines the conditions of the elements to remove.
     * @return The new list.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> removeIf(@Nonnull Predicate<? super T> predicate);

    /**
     * Removes the specified values from this list.
     *
     * @param items The items to remove if matches are found in this list.
     * @param equalityComparator The equality comparator to use in the search.
     * @return A new list with the elements removed.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> removeAll(@Nonnull Iterable<? extends T> items, @Nonnull EqualityComparator<? super T> equalityComparator);

    /**
     * Removes the element at the specified index.
     *
     * @param index The index.
     * @return A new list with the element removed.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> remove(int index);

    /**
     * Remove the specified values from this list.
     *
     * @param fromIndex The index of the first element (inclusive) to be removed.
     * @param toIndex The index of the last element (exclusive) to be removed.
     * @return A new list with the elements removed.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> removeAll(int fromIndex, int toIndex);

    /**
     * Replaces an element in the list at a given position with the specified element.
     *
     * @param index The position in the list of the element to replace.
     * @param value The element to replace the old element with.
     * @return The new list, even if the value being replaced is equal to the new value for that position.
     */
    @Nonnull
    @CheckReturnValue
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
    @Nonnull
    @CheckReturnValue
    ImmutableList<T> replace(T oldValue, T newValue, @Nonnull EqualityComparator<? super T> equalityComparator);

}
