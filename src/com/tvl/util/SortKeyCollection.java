// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Comparator;

/**
 * Defined on a generic collection that sorts its contents using a {@link Comparator}.
 *
 * @param <K> The type of element sorted in the collection.
 */
interface SortKeyCollection<K> {
    /**
     * Gets the comparator used to sort the keys.
     *
     * @return The comparator used to sort the keys.
     */
    Comparator<? super K> getKeyComparator();
}
