// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

public interface ReadOnlyCollection<T> extends Iterable<T> {

    /**
     * Returns the number of elements in this collection. If this collection contains more than
     * {@link Integer#MAX_VALUE} elements, returns {@link Integer#MAX_VALUE}.
     *
     * @return the number of elements in this collection
     */
    int size();

    /**
     * Returns {@code true} if this collection contains no elements.
     *
     * @return {@code true} if this collection contains no elements; otherwise, {@code false}.
     */
    boolean isEmpty();

}
