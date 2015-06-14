// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Collection;

/**
 * Provides static utility methods for working with immutable collection instances.
 */
public final class Immutables {

    /**
     * Prevents an instance of the {@link Immutables} class from being created.
     */
    private Immutables() {
    }

    /**
     * Iterates a sequence exactly once and produces an {@link ImmutableArrayList} of its contents.
     *
     * @param <T> The type of element in the sequence.
     * @param source The sequence to iterate.
     * @return An {@link ImmutableArrayList}.
     */
    public static <T> ImmutableArrayList<T> toImmutableArrayList(Iterable<T> source) {
        return ImmutableArrayList.createAll(source);
    }

    /**
     * Iterates a sequence exactly once and produces an {@link ImmutableTreeList} of its contents.
     *
     * @param <T> The type of element in the sequence.
     * @param source The sequence to iterate.
     * @return An {@link ImmutableTreeList}.
     */
    public static <T> ImmutableTreeList<T> toImmutableTreeList(Iterable<T> source) {
        return ImmutableTreeList.createAll(source);
    }

    static <T> ImmutableArrayList<T> asImmutableArrayList(Iterable<T> source) {
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
    static Integer tryGetCount(Iterable<?> iterable) {
        if (iterable instanceof Collection<?>) {
            return ((Collection<?>)iterable).size();
        }

        if (iterable instanceof ReadOnlyCollection<?>) {
            return ((ReadOnlyCollection<?>)iterable).size();
        }

        return null;
    }
}
