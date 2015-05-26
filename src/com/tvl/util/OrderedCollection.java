// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

/**
 * Describes an ordered collection of elements.
 *
 * @param <T> The type of element in the collection.
 */
interface OrderedCollection<T> extends Iterable<T> {
    /**
     * Gets the number of elements in the collection.
     *
     * @return The number of elements in the collection.
     */
    int size();

    /**
     * Gets the element in the collection at a given index.
     *
     * @param index The index.
     * @return The element at the given index.
     */
    T get(int index);
}
