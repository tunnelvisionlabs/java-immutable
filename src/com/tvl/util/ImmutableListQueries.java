// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.tvl.util.function.Function;
import com.tvl.util.function.Predicate;
import java.util.Comparator;

interface ImmutableListQueries<T> extends ReadOnlyList<T> {
    /**
     * Transforms an immutable list by applying a conversion function to each element of the list.
     *
     * @param <U> The type of element stored in the converted list.
     * @param converter The conversion function to apply to each element of the list.
     * @return The transformed immutable list.
     */
    <U> ImmutableList<U> convertAll(Function<? super T, U> converter);

    /**
     * Creates a new immutable list containing the specified range of elements from the current list.
     *
     * @param fromIndex The index of the first element (inclusive) to be included in the sub-list.
     * @param toIndex The index of the last element (exclusive) to be included in the sub-list.
     * @return The immutable list.
     */
    ImmutableList<T> subList(int fromIndex, int toIndex);

    void copyTo(T[] array);

    void copyTo(T[] array, int arrayIndex);

    void copyTo(int index, T[] array, int arrayIndex, int count);

    /**
     * Determines whether the {@link ImmutableList} contains elements that match the conditions defined by the specified
     * predicate.
     *
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return {@code true} if the immutable list contains one or more elements that match the conditions defined by
     * {@code match}; otherwise, {@code false}.
     */
    boolean exists(Predicate<? super T> match);

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the first
     * occurrence within the entire immutable list.
     *
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return The first element that matches the conditions defined by {@code match}, if found; otherwise,
     * {@code null}.
     */
    T find(Predicate<? super T> match);

    /**
     * Retrieves all the elements that match the conditions defined by the specified predicate.
     *
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return An immutable list containing all the elements that match the conditions defined by {@code match}, if
     * found; otherwise, an empty immutable list.
     */
    ImmutableList<T> retainIf(Predicate<? super T> match);

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the
     * zero-based index of the first occurrence within the entire immutable list.
     *
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return The zero-based index of the first element that matches the conditions defined by {@code match}, if found;
     * otherwise, -1.
     */
    int findIndex(Predicate<? super T> match);

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the
     * zero-based index of the first occurrence within the range of elements that extends from the specified index to
     * the last element.
     *
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return The zero-based index of the first element that matches the conditions defined by {@code match}, if found;
     * otherwise, -1.
     */
    int findIndex(int fromIndex, Predicate<? super T> match);

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the
     * zero-based index of the first occurrence within the range of elements that extends from {@code startIndex}
     * through (but not including) {@code toIndex}.
     *
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param toIndex The index of the last element (exclusive) to be searched.
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return The zero-based index of the first element that matches the conditions defined by {@code match}, if found;
     * otherwise, -1.
     */
    int findIndex(int fromIndex, int toIndex, Predicate<? super T> match);

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the last
     * occurrence within the entire immutable list.
     *
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return The last element that matches the conditions defined by {@code match}, if found; otherwise, {@code null}.
     */
    T findLast(Predicate<? super T> match);

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the
     * zero-based index of the last occurrence within the entire immutable list.
     *
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return The zero-based index of the last element that matches the conditions defined by {@code match}, if found;
     * otherwise, -1.
     */
    int findLastIndex(Predicate<? super T> match);

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the
     * zero-based index of the last occurrence within the range of elements that extends from the specified index to the
     * last element.
     *
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return The zero-based index of the last element that matches the conditions defined by {@code match}, if found;
     * otherwise, -1.
     */
    int findLastIndex(int fromIndex, Predicate<? super T> match);

    /**
     * Searches for an element that matches the conditions defined by the specified predicate, and returns the
     * zero-based index of the last occurrence within the range of elements that extends from {@code startIndex} through
     * (but not including) {@code toIndex}.
     *
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param toIndex The index of the last element (exclusive) to be searched.
     * @param match The {@link Predicate} that defines the conditions of the elements to search for.
     * @return The zero-based index of the last element that matches the conditions defined by {@code match}, if found;
     * otherwise, -1.
     */
    int findLastIndex(int fromIndex, int toIndex, Predicate<? super T> match);

    /**
     * Determines whether every element in the {@link ImmutableList} matches the conditions defined by the specified
     * predicate.
     *
     * @param match The {@link Predicate} that defines the conditions to check against the elements.
     * @return {@code true} if every element in the immutable list matches the conditions defined by {@code match};
     * otherwise, {@code false}. If the list is empty, this method returns {@code true}.
     */
    boolean trueForAll(Predicate<? super T> match);

    /**
     * Searches an entire one-dimensional sorted immutable list for a specific element, using the default comparator for
     * elements of type {@code T}.
     *
     * @param item The object to search for.
     * @return The index of the specified {@code item} in the list, if {@code item} is found. If {@code item} is not
     * found and {@code item} is less than one or more elements in the list, a negative number which is the bitwise
     * complement of the index of the first element that is larger than {@code item}. If {@code item} is not found and
     * {@code item} is greater than all of the elements in the list, a negative number which is the bitwise complement
     * of (the index of the last element plus 1).
     */
    int binarySearch(T item);

    /**
     * Searches an entire one-dimensional sorted immutable list for a specific element using the specified comparator.
     *
     * @param item The object to search for.
     * @param comparator The comparator to use for comparing elements, or {@code null} to use the default comparator for
     * elements of type {@code T}.
     * @return The index of the specified {@code item} in the list, if {@code item} is found. If {@code item} is not
     * found and {@code item} is less than one or more elements in the list, a negative number which is the bitwise
     * complement of the index of the first element that is larger than {@code item}. If {@code item} is not found and
     * {@code item} is greater than all of the elements in the list, a negative number which is the bitwise complement
     * of (the index of the last element plus 1).
     */
    int binarySearch(T item, Comparator<? super T> comparator);

    /**
     * Searches a range of the specified immutable list for the specified object using the binary search algorithm. The
     * range must be sorted into ascending order according to the specified comparator (as by the
     * {@link ImmutableTreeList#sort(int, int, Comparator) sort(int, int, Comparator)} method) prior to making this
     * call. If it is not sorted, the results are unspecified. If the range contains multiple elements equal to the
     * specified object, there is no guarantee which one will be found.
     *
     * @param fromIndex The index of the first element (inclusive) to be searched.
     * @param toIndex The index of the last element (exclusive) to be searched.
     * @param item The value to search for.
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
    int binarySearch(int fromIndex, int toIndex, T item, Comparator<? super T> comparator);
}
