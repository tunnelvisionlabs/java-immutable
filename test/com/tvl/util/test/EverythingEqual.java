// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util.test;

import com.tvl.util.EqualityComparator;

class EverythingEqual<T> implements EqualityComparator<T> {

    private static final EverythingEqual<?> INSTANCE = new EverythingEqual<Object>();

    private EverythingEqual() {
    }

    public static <T> EverythingEqual<T> instance() {
        @SuppressWarnings("unchecked") // safe; objects are never used
        EverythingEqual<T> result = (EverythingEqual<T>)INSTANCE;
        return result;
    }

    @Override
    public boolean equals(T a, T b) {
        return true;
    }

    @Override
    public int hashCode(T o) {
        return 1;
    }

}
