// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Comparator;
import javax.annotation.Nonnull;

enum Comparators {
    ;

    @Nonnull
    static <T extends Comparable<T>> Comparator<T> defaultComparator() {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        Comparator<T> result = (ComparableComparator<T>)ComparableComparator.INSTANCE;
        return result;
    }

    @Nonnull
    static <T> Comparator<T> anyComparator() {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        Comparator<T> result = (Comparator<T>)ComparableComparator.INSTANCE;
        return result;
    }

    private static final class ComparableComparator<T extends Comparable<T>> implements Comparator<T> {
        static final ComparableComparator<?> INSTANCE = new ComparableComparator<Integer>();

        @Override
        public int compare(T o1, T o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            }

            return o1.compareTo(o2);
        }
    }
}
