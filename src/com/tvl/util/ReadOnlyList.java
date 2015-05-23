// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

public interface ReadOnlyList<T> extends ReadOnlyCollection<T> {

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >= size()})
     */
    T get(int index);

}
