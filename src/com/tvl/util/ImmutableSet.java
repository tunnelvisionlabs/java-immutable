// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

/**
 * A set of elements that con only be modified by creating a new instance of the set.
 *
 * Mutations on this set generate new sets. Incremental changes to a set share as much memory as possible with the prior
 * versions of a set, while allowing garbage collection to clean up any unique set data that is no longer being
 * referenced.
 *
 * @param <T> The type of element stored in the set.
 */
public interface ImmutableSet<T> extends ReadOnlyCollection<T> {

    /**
     * Gets an empty set that retains the same sort or unordered semantics that this instance has.
     *
     * @return The empty set.
     */
    ImmutableSet<T> clear();

    /**
     * Determines whether this set contains the specified value.
     *
     * @param value The value.
     * @return {@code true} if this set contains the specified value; otherwise, {@code false}.
     */
    boolean contains(T value);

    /**
     * Adds the specified value to this set.
     *
     * @param value The value to add.
     * @return A new set with the element added, or this set if the element is already in this set.
     */
    ImmutableSet<T> add(T value);

    /**
     * Removes the specified value from this set.
     *
     * @param value The value to remove.
     * @return A new set with the element removed, or this set if the element is not in this set.
     */
    ImmutableSet<T> remove(T value);

    /**
     * Produces a set that contains elements that exist in both this set and the specified sequence.
     *
     * @param other The set to intersect with this one.
     * @return A new set that contains any elements that exist in both sets.
     */
    ImmutableSet<T> intersect(Iterable<? extends T> other);

    /**
     * Removes a given set of items from this set.
     *
     * @param other The items to remove from this set.
     * @return The new set with the items removed; or the original set if none of the items were in the set.
     */
    ImmutableSet<T> except(Iterable<? extends T> other);

    /**
     * Produces a set that contains elements either in this set or a given sequence, but not both.
     *
     * @param other The other sequence of items.
     * @return The new set.
     */
    ImmutableSet<T> symmetricExcept(Iterable<? extends T> other);

    /**
     * Adds a given set of items to this set.
     *
     * @param other The items to add.
     * @return The new set with the items added; or the original set if all the items were already in the set.
     */
    ImmutableSet<T> union(Iterable<? extends T> other);

    /**
     * Checks whether a given sequence of items entirely describe the contents of this set.
     *
     * @param other The sequence of items to check against this set.
     * @return {@code true} if the sets are equal; otherwise, {@code false}.
     */
    ImmutableSet<T> setEquals(Iterable<? extends T> other);

    /**
     * Determines whether the current set is a proper subset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a proper subset of {@code other}; otherwise, {@code false}.
     */
    boolean isProperSubsetOf(Iterable<? extends T> other);

    /**
     * Determines whether the current set is a proper superset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a proper superset of {@code other}; otherwise, {@code false}.
     */
    boolean isProperSupersetOf(Iterable<? extends T> other);

    /**
     * Determines whether the current set is a subset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a subset of {@code other}; otherwise, {@code false}.
     */
    boolean isSubsetOf(Iterable<? extends T> other);

    /**
     * Determines whether the current set is a superset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a superset of {@code other}; otherwise, {@code false}.
     */
    boolean isSupersetOf(Iterable<? extends T> other);

    /**
     * Determines whether the current set overlaps with the specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set and {@code other} share at least one common element; otherwise,
     * {@code false}.
     */
    boolean overlaps(Iterable<? extends T> other);

}
