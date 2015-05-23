// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

/**
 * A comparison interface, which is capable of calculating hash codes for objects and comparing object instances for
 * equality.
 *
 * @param <T> The type of objects which can be compared by this comparator.
 */
public interface EqualityComparator<T> {

    /**
     * Compares two object instances for equality.
     *
     * @param a The first object, which may be {@code null}.
     * @param b The second object, which may be {@code null}.
     * @return {@code true} if {@code a} and {@code b} are equal; otherwise, {@code false}.
     */
    boolean equals(T a, T b);

    /**
     * Gets the hash code for an object.
     *
     * @param o The object.
     * @return The hash code for the object.
     */
    int hashCode(T o);

}
