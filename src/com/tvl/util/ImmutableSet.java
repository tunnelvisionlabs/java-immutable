// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

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
    @Nonnull
    @CheckReturnValue
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
    @Nonnull
    @CheckReturnValue
    ImmutableSet<T> add(T value);

    /**
     * Removes the specified value from this set.
     *
     * @param value The value to remove.
     * @return A new set with the element removed, or this set if the element is not in this set.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableSet<T> remove(T value);

    /**
     * Searches the set for a given value and returns the equal value it finds, if any.
     *
     * <p>
     * This can be useful when you want to reuse a previously stored reference instead of a newly constructed one (so
     * that more sharing of references can occur) or to look up a value that has more complete data than the value you
     * currently have, although their comparator functions indicate they are equal.</p>
     *
     * @param equalValue The value to search for.
     * @return The value from the set that the search found, or {@code null} if the search yielded no match.
     */
    T tryGetValue(T equalValue);

    /**
     * Produces a set that contains elements that exist in both this set and the specified sequence.
     *
     * @param other The set to intersect with this one.
     * @return A new set that contains any elements that exist in both sets.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableSet<T> intersect(@Nonnull Iterable<? extends T> other);

    /**
     * Removes a given set of items from this set.
     *
     * @param other The items to remove from this set.
     * @return The new set with the items removed; or the original set if none of the items were in the set.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableSet<T> except(@Nonnull Iterable<? extends T> other);

    /**
     * Produces a set that contains elements either in this set or a given sequence, but not both.
     *
     * @param other The other sequence of items.
     * @return The new set.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableSet<T> symmetricExcept(@Nonnull Iterable<? extends T> other);

    /**
     * Adds a given set of items to this set.
     *
     * @param other The items to add.
     * @return The new set with the items added; or the original set if all the items were already in the set.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableSet<T> union(@Nonnull Iterable<? extends T> other);

    /**
     * Checks whether a given sequence of items entirely describe the contents of this set.
     *
     * @param other The sequence of items to check against this set.
     * @return {@code true} if the sets are equal; otherwise, {@code false}.
     */
    @Nonnull
    @CheckReturnValue
    boolean setEquals(@Nonnull Iterable<? extends T> other);

    /**
     * Determines whether the current set is a proper subset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a proper subset of {@code other}; otherwise, {@code false}.
     */
    boolean isProperSubsetOf(@Nonnull Iterable<? extends T> other);

    /**
     * Determines whether the current set is a proper superset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a proper superset of {@code other}; otherwise, {@code false}.
     */
    boolean isProperSupersetOf(@Nonnull Iterable<? extends T> other);

    /**
     * Determines whether the current set is a subset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a subset of {@code other}; otherwise, {@code false}.
     */
    boolean isSubsetOf(@Nonnull Iterable<? extends T> other);

    /**
     * Determines whether the current set is a superset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a superset of {@code other}; otherwise, {@code false}.
     */
    boolean isSupersetOf(@Nonnull Iterable<? extends T> other);

    /**
     * Determines whether the current set overlaps with the specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set and {@code other} share at least one common element; otherwise,
     * {@code false}.
     */
    boolean overlaps(@Nonnull Iterable<? extends T> other);

}
