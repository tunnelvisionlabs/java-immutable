// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Comparator;

final class Comparators {

    static Comparator<Object> defaultComparator() {
        return ComparableComparator.INSTANCE;
    }

    private static final class ComparableComparator implements Comparator<Object> {
        static final ComparableComparator INSTANCE = new ComparableComparator();

        @Override
        public int compare(Object o1, Object o2) {
            if (!(o1 instanceof Comparable<?>)) {
                throw new UnsupportedOperationException("o1 is not comparable");
            }

            if (!(o2 instanceof Comparable<?>)) {
                throw new UnsupportedOperationException("o2 is not comparable");
            }

            return ((Comparable)o1).compareTo(o2);
        }
    }
}
