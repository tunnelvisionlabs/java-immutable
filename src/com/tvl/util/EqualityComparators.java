// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

public abstract class EqualityComparators {

    /**
     * Gets a default {@link EqualityComparator} for instances of type {@code T}.
     *
     * @return A default {@link EqualityComparator} for instances of type {@code T}.
     */
    public static EqualityComparator<Object> defaultComparator() {
        return ObjectEqualityComparator.INSTANCE;
    }

    /**
     * Gets a default {@link EqualityComparator} for instances of type {@code T}.
     *
     * @param <T> The type of objects to compare.
     * @param clazz The type of objects to compare.
     * @return A default {@link EqualityComparator} for instances of type {@code T}.
     */
    public static <T> EqualityComparator<T> defaultComparator(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            throw new UnsupportedOperationException("Cannot create a generic comparator for primitive types.");
        }

        if (Comparable.class.isAssignableFrom(clazz)) {
            throw new UnsupportedOperationException("Not yet implemented.");
        }

        // Treat like a normal object
        @SuppressWarnings("unchecked") // safe
        EqualityComparator<T> comparator = (EqualityComparator<T>)ObjectEqualityComparator.INSTANCE;
        return comparator;
    }

    private static final class ObjectEqualityComparator implements EqualityComparator<Object> {
        /**
         * A singleton instance of the default {@link EqualityComparator} for objects.
         */
        public static final EqualityComparator<Object> INSTANCE = new ObjectEqualityComparator();

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object a, Object b) {
            return (a == b) || (a != null && a.equals(b));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode(Object o) {
            return o != null ? o.hashCode() : 0;
        }
    }

    private static final class ComparableEqualityComparator<T> implements EqualityComparator<Comparable<T>> {
        /**
         * A singleton instance of the default {@link EqualityComparator} for objects.
         */
        public static final EqualityComparator<Object> INSTANCE = new ObjectEqualityComparator();

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Comparable<T> a, Comparable<T> b) {
            return (a == b) || (a != null && a.equals(b));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode(Comparable<T> o) {
            return o != null ? o.hashCode() : 0;
        }
    }
}
