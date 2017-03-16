// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Collection;
import java.util.Comparator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides static utility methods for working with immutable collection instances.
 */
public enum Immutables {
    ;

    /**
     * Iterates a sequence exactly once and produces an {@link ImmutableArrayList} of its contents.
     *
     * @param <T> The type of element in the sequence.
     * @param source The sequence to iterate.
     * @return An {@link ImmutableArrayList}.
     */
    @Nonnull
    public static <T> ImmutableArrayList<T> toImmutableArrayList(@Nonnull Iterable<T> source) {
        return ImmutableArrayList.createAll(source);
    }

    /**
     * Iterates a sequence exactly once and produces an {@link ImmutableTreeList} of its contents.
     *
     * @param <T> The type of element in the sequence.
     * @param source The sequence to iterate.
     * @return An {@link ImmutableTreeList}.
     */
    @Nonnull
    public static <T> ImmutableTreeList<T> toImmutableTreeList(@Nonnull Iterable<T> source) {
        return ImmutableTreeList.createAll(source);
    }

    /**
     * Iterates a sequence exactly once and produces an immutable set of its contents.
     *
     * @param <T> The type of element in the sequence.
     * @param source The sequence to iterate.
     * @param comparator The comparator to use for initializing and adding members to the sorted set.
     * @return An immutable set.
     */
    @Nonnull
    public static <T> ImmutableTreeSet<T> toImmutableTreeSet(@Nonnull Iterable<? extends T> source, @Nonnull Comparator<? super T> comparator) {
        if (source instanceof ImmutableTreeSet<?>) {
            ImmutableTreeSet<? extends T> existingSet = (ImmutableTreeSet<? extends T>)source;

            // This cast is safe because we know the new comparator will accept T as an argument.
            @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
            ImmutableTreeSet<T> upcastSet = (ImmutableTreeSet<T>)existingSet;

            return upcastSet.withComparator(comparator);
        }

        return ImmutableTreeSet.<T>empty().withComparator(comparator).union(source);
    }

    /**
     * Iterates a sequence exactly once and produces an immutable set of its contents.
     *
     * @param <T> The type of element in the sequence.
     * @param source The sequence to iterate.
     * @return An immutable set.
     */
    @Nonnull
    public static <T> ImmutableTreeSet<T> toImmutableTreeSet(@Nonnull Iterable<? extends T> source) {
        return toImmutableTreeSet(source, null);
    }

    @Nullable
    static <T> ImmutableArrayList<T> asImmutableArrayList(@Nonnull Iterable<T> source) {
        if (source instanceof ImmutableArrayList<?>) {
            return (ImmutableArrayList<T>)source;
        }

        return null;
    }

    /**
     * Tries to divine the number of elements in a sequence without actually iterating each element.
     *
     * @param iterable The iterable source.
     * @return The number of elements in the iterable, if it could be determined; otherwise, {@code null}.
     */
    @Nullable
    static Integer tryGetCount(@Nonnull Iterable<?> iterable) {
        if (iterable instanceof Collection<?>) {
            return ((Collection<?>)iterable).size();
        }

        if (iterable instanceof ReadOnlyCollection<?>) {
            return ((ReadOnlyCollection<?>)iterable).size();
        }

        return null;
    }
}
