// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;

public abstract class SimpleElementImmutablesTestBase extends ImmutablesTestBase {

    protected abstract <T> Iterable<T> getIterableOf(T... contents);

    protected final <T> Iterable<T> getIterableOf(Iterable<T> contents, Class<T> clazz) {
        T[] elements = Iterables.toArray(contents, clazz);
        return getIterableOf(elements);
    }

}
