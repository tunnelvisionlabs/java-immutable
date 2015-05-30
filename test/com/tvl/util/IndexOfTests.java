// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import com.tvl.util.function.BiFunction;
import com.tvl.util.function.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;

public class IndexOfTests {
    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    interface QuadFunction<T, U, V, W, R> {
        R apply(T t, U u, V v, W w);
    }

    interface PentFunction<T, U, V, W, X, R> {
        R apply(T t, U u, V v, W w, X x);
    }

    public static <T> void indexOfTest(
        Function<Iterable<Integer>, T> factory,
        BiFunction<T, Integer, Integer> indexOfItem,
        TriFunction<T, Integer, Integer, Integer> indexOfItemIndex,
        QuadFunction<T, Integer, Integer, Integer, Integer> indexOfItemIndexCount,
        PentFunction<T, Integer, Integer, Integer, EqualityComparator<? super Integer>, Integer> indexOfItemIndexCountEQ) {
        T emptyCollection = factory.apply(Collections.<Integer>emptyList());
        T collection1256 = factory.apply(Arrays.asList(1, 2, 5, 6));

        try {
            indexOfItemIndexCountEQ.apply(emptyCollection, 100, 1, 1, EqualityComparators.defaultComparator());
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            indexOfItemIndexCountEQ.apply(emptyCollection, 100, -1, 1, EqualityComparators.defaultComparator());
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            indexOfItemIndexCountEQ.apply(collection1256, 100, 1, 20, EqualityComparators.defaultComparator());
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            indexOfItemIndexCountEQ.apply(collection1256, 100, 1, -1, EqualityComparators.defaultComparator());
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            indexOfItemIndexCountEQ.apply(emptyCollection, 100, 1, 1, new CustomComparator(50));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            indexOfItemIndexCountEQ.apply(emptyCollection, 100, -1, 1, new CustomComparator(50));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            indexOfItemIndexCountEQ.apply(collection1256, 100, 1, 20, new CustomComparator(1));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            indexOfItemIndexCountEQ.apply(collection1256, 100, 1, -1, new CustomComparator(1));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        Assert.assertEquals(-1, (int)indexOfItem.apply(emptyCollection, 5));
        Assert.assertEquals(-1, (int)indexOfItemIndex.apply(emptyCollection, 5, 0));
        Assert.assertEquals(2, (int)indexOfItemIndex.apply(collection1256, 5, 1));
        Assert.assertEquals(-1, (int)indexOfItemIndexCount.apply(emptyCollection, 5, 0, 0));
        Assert.assertEquals(-1, (int)indexOfItemIndexCount.apply(collection1256, 5, 1, 1));
        Assert.assertEquals(2, (int)indexOfItemIndexCount.apply(collection1256, 5, 1, 2));

        // Create a list with contents: 100,101,102,103,104,100,101,102,103,104
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(Iterables.concat(new Range(100, 5), new Range(100, 5)));
        ArrayList<Integer> bclList = new ArrayList<Integer>(list.toBuilder());
        Assert.assertEquals(-1, (int)indexOfItem.apply(factory.apply(list), 6));

        if (factory.apply(list) instanceof List<?>) {
            Assert.assertEquals(-1, ((List<?>)factory.apply(list)).indexOf(6));
        }

        for (int idx = 0; idx < list.size(); idx++) {
            for (int count = 0; count <= list.size() - idx; count++) {
                for (int match : Iterables.concat(list, Collections.singletonList(88))) {
                    int expected = bclList.subList(idx, idx + count).indexOf(match);
                    if (expected >= 0) {
                        expected += idx;
                    }

                    int actual = indexOfItemIndexCount.apply(factory.apply(list), match, idx, count);
                    Assert.assertEquals(expected, actual);

                    actual = indexOfItemIndexCountEQ.apply(factory.apply(list), match, idx, count, new CustomComparator(count));
                    Assert.assertEquals(count > 0 ? idx + count - 1 : -1, actual);

                    if (count == list.size()) {
                        // Also test the IndexOf overload that takes no count parameter.
                        actual = indexOfItemIndex.apply(factory.apply(list), match, idx);
                        Assert.assertEquals(expected, actual);

                        if (idx == 0) {
                            // Also test the IndexOf overload that takes no index parameter.
                            actual = indexOfItem.apply(factory.apply(list), match);
                            Assert.assertEquals(expected, actual);
                        }
                    }
                }
            }
        }
    }

    private static class CustomComparator implements EqualityComparator<Integer> {
        private final int matchOnXIteration;
        private int iteration;

        public CustomComparator(int matchOnXIteration) {
            this.matchOnXIteration = matchOnXIteration;
        }

        @Override
        public boolean equals(Integer a, Integer b) {
            return ++iteration == matchOnXIteration;
        }

        @Override
        public int hashCode(Integer o) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }
}
