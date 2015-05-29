// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.tvl.util.function.Function;
import com.tvl.util.function.Predicate;
import java.util.Comparator;

interface ImmutableListQueries<T> extends ReadOnlyList<T> {
    <U> ImmutableTreeList<U> convertAll(Function<? super T, ? extends U> converter);

    ImmutableTreeList<T> getRange(int index, int count);

    void copyTo(T[] array);

    void copyTo(T[] array, int arrayIndex);

    void copyTo(int index, T[] array, int arrayIndex, int count);

    boolean exists(Predicate<? super T> match);

    T find(Predicate<? super T> match);

    ImmutableTreeList<T> findIf(Predicate<? super T> match);

    int findIndex(Predicate<? super T> match);

    int findIndex(int startIndex, Predicate<? super T> match);

    int findIndex(int startIndex, int count, Predicate<? super T> match);

    T findLast(Predicate<? super T> match);

    int findLastIndex(Predicate<? super T> match);

    int findLastIndex(int startIndex, Predicate<? super T> match);

    int findLastIndex(int startIndex, int count, Predicate<? super T> match);

    boolean trueForAll(Predicate<? super T> match);

    int binarySearch(T item);

    int binarySearch(T item, Comparator<? super T> comparator);

    int binarySearch(int index, int count, T item, Comparator<? super T> comparator);
}
