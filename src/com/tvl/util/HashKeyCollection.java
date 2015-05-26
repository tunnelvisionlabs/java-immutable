// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

/**
 * Defined on a generic collection that hashes its contents using an {@link EqualityComparator}.
 *
 * @param <K> The type of element hashed in the collection.
 */
interface HashKeyCollection<K> {
    /**
     * Gets the comparator used to obtain hash codes for the keys and check equality.
     *
     * @return The comparator used to obtain hash codes for the keys and check equality.
     */
    EqualityComparator<K> getKeyComparator();
}
