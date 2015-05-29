// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

class BadHasher<T> implements EqualityComparator<T> {

    private final EqualityComparator<? super T> equalityComparator;

    BadHasher() {
        this(null);
    }

    BadHasher(EqualityComparator<? super T> equalityComparator) {
        if (equalityComparator != null) {
            this.equalityComparator = equalityComparator;
        } else {
            this.equalityComparator = EqualityComparators.defaultComparator();
        }
    }

    @Override
    public boolean equals(T a, T b) {
        return equalityComparator.equals(a, b);
    }

    @Override
    public int hashCode(T o) {
        return 1;
    }

}
