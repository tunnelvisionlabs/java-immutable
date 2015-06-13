// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import com.tvl.util.function.Function;
import com.tvl.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ImmutableListTestBase extends SimpleElementImmutablesTestBase {
    abstract <T> ImmutableListQueries<T> getListQuery(ImmutableTreeList<T> list);

    @Test
    public void copyToEmptyTest() {
        Integer[] array = new Integer[0];
        this.getListQuery(ImmutableTreeList.<Integer>empty()).copyTo(array);
        this.getListQuery(ImmutableTreeList.<Integer>empty()).copyTo(array, 0);
        this.getListQuery(ImmutableTreeList.<Integer>empty()).copyTo(0, array, 0, 0);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void copyToTest() {
        ImmutableListQueries<Integer> listQuery = this.getListQuery(ImmutableTreeList.create(1, 2));
        Iterable<Integer> list = listQuery;

        Integer[] array = new Integer[2];
        listQuery.copyTo(array);
        assertEqualSequences(list, Arrays.asList(array));

        array = new Integer[2];
        listQuery.copyTo(array, 0);
        assertEqualSequences(list, Arrays.asList(array));

        array = new Integer[2];
        listQuery.copyTo(0, array, 0, listQuery.size());
        assertEqualSequences(list, Arrays.asList(array));

        array = new Integer[1]; // shorter than source length
        listQuery.copyTo(0, array, 0, array.length);
        assertEqualSequences(Collections.singletonList(1), Arrays.asList(array));

        array = new Integer[3];
        listQuery.copyTo(1, array, 2, 1);
        assertEqualSequences(Arrays.asList(null, null, 2), Arrays.asList(array));
    }

    //[Fact]
    //public void ForEachTest()
    //{
    //    this.GetListQuery(ImmutableList<int>.Empty).ForEach(n => Assert.True(false, "Empty list should not invoke this."));

    //    var list = ImmutableList<int>.Empty.AddRange(Enumerable.Range(5, 3));
    //    var hitTest = new bool[list.Max() + 1];
    //    this.GetListQuery(list).ForEach(i =>
    //    {
    //        Assert.False(hitTest[i]);
    //        hitTest[i] = true;
    //    });

    //    for (int i = 0; i < hitTest.Length; i++)
    //    {
    //        Assert.Equal(list.Contains(i), hitTest[i]);
    //        Assert.Equal(((IList)list).Contains(i), hitTest[i]);
    //    }
    //}

    @Test
    public void existsTest() {
        Assert.assertFalse(getListQuery(ImmutableTreeList.empty()).exists(new Predicate<Object>() {
            @Override
            public boolean test(Object o) {
                return true;
            }
        }));

        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(new Range(1, 5));
        Assert.assertTrue(getListQuery(list).exists(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer == 3;
            }
        }));
        Assert.assertFalse(getListQuery(list).exists(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer == 8;
            }
        }));
    }

    @Test
    public void findIfTest() {
        Assert.assertTrue(getListQuery(ImmutableTreeList.<Integer>empty()).retainIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return true;
            }
        }).isEmpty());
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(Arrays.asList(2, 3, 4, 5, 6));
        ImmutableList<Integer> actual = getListQuery(list).retainIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return (integer % 2) == 1;
            }
        });
        List<Integer> expected = Arrays.asList(3, 5);
        assertEqualSequences(expected, actual);
    }

    @Test
    public void findTest() {
        Assert.assertEquals(null, getListQuery(ImmutableTreeList.<Integer>empty()).find(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return true;
            }
        }));
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(Arrays.asList(2, 3, 4, 5, 6));
        Assert.assertEquals(3, (int)getListQuery(list).find(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return (integer % 2) == 1;
            }
        }));
    }

    @Test
    public void findLastTest() {
        Assert.assertEquals(null, getListQuery(ImmutableTreeList.<Integer>empty()).findLast(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                Assert.fail("Predicate should not have been invoked.");
                return true;
            }
        }));
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(Arrays.asList(2, 3, 4, 5, 6));
        Assert.assertEquals(5, (int)getListQuery(list).findLast(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return (integer % 2) == 1;
            }
        }));
    }

    @Test
    public void findIndexTest() {
        final Predicate<Object> TRUE = new Predicate<Object>() {
            @Override
            public boolean test(Object o) {
                return true;
            }
        };

        Assert.assertEquals(-1, getListQuery(ImmutableTreeList.<Integer>empty()).findIndex(TRUE));
        Assert.assertEquals(-1, getListQuery(ImmutableTreeList.<Integer>empty()).findIndex(0, TRUE));
        Assert.assertEquals(-1, getListQuery(ImmutableTreeList.<Integer>empty()).findIndex(0, 0, TRUE));

        // Create a list with contents: 100,101,102,103,104,100,101,102,103,104
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(Iterables.concat(new Range(100, 5), new Range(100, 5)));
        ArrayList<Integer> bclList = new ArrayList<Integer>(Arrays.asList(Iterables.toArray(list, Integer.class)));
        Assert.assertEquals(-1, getListQuery(list).findIndex(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer == 6;
            }
        }));

        for (int idx = 0; idx < list.size(); idx++) {
            for (int count = 0; count <= list.size() - idx; count++) {
                for (final Integer c : list) {
                    final AtomicInteger predicateInvocationCount = new AtomicInteger();
                    Predicate<Integer> match = new Predicate<Integer>() {
                        @Override
                        public boolean test(Integer integer) {
                            predicateInvocationCount.incrementAndGet();
                            return c.equals(integer);
                        }
                    };

                    //int expected = bclList.findIndex(idx, count, match);
                    int expected = bclList.subList(idx, idx + count).indexOf(c);
                    int expectedInvocationCount = expected >= 0 ? expected + 1 : count;
                    if (expected >= 0) {
                        expected += idx;
                    }

                    predicateInvocationCount.set(0);
                    int actual = getListQuery(list).findIndex(idx, idx + count, match);
                    int actualInvocationCount = predicateInvocationCount.get();
                    Assert.assertEquals(expected, actual);
                    Assert.assertEquals(expectedInvocationCount, actualInvocationCount);

                    if (count == list.size()) {
                        // Also test the FindIndex overload that takes no count parameter.
                        predicateInvocationCount.set(0);
                        actual = getListQuery(list).findIndex(idx, match);
                        Assert.assertEquals(expected, actual);
                        Assert.assertEquals(expectedInvocationCount, actualInvocationCount);

                        if (idx == 0) {
                            // Also test the FindIndex overload that takes no index parameter.
                            predicateInvocationCount.set(0);
                            actual = getListQuery(list).findIndex(match);
                            Assert.assertEquals(expected, actual);
                            Assert.assertEquals(expectedInvocationCount, actualInvocationCount);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void findLastIndexTest() {
        Assert.assertEquals(-1, getListQuery(ImmutableTreeList.<Integer>empty()).findLastIndex(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return true;
            }
        }));
        Assert.assertEquals(-1, getListQuery(ImmutableTreeList.<Integer>empty()).findLastIndex(0, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return true;
            }
        }));
        Assert.assertEquals(-1, getListQuery(ImmutableTreeList.<Integer>empty()).findLastIndex(0, 0, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return true;
            }
        }));

        // Create a list with contents: 100,101,102,103,104,100,101,102,103,104
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(Iterables.concat(new Range(100, 5), new Range(100, 5)));
        ArrayList<Integer> bclList = new ArrayList<Integer>(list.toBuilder());
        Assert.assertEquals(-1, getListQuery(list).findLastIndex(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer == 6;
            }
        }));

        for (int idx = 0; idx < list.size(); idx++) {
            for (int count = 0; count <= idx + 1; count++) {
                for (final int c : list) {
                    final AtomicInteger predicateInvocationCount = new AtomicInteger();
                    Predicate<Integer> match = new Predicate<Integer>() {
                        @Override
                        public boolean test(Integer integer) {
                            predicateInvocationCount.incrementAndGet();
                            return integer == c;
                        }
                    };

                    int expected = bclList.subList(idx - count + 1, idx + 1).lastIndexOf(c);
                    int expectedInvocationCount = expected >= 0 ? count - expected : count;
                    if (expected >= 0) {
                        expected += idx - count + 1;
                    }

                    int actual = getListQuery(list).findLastIndex(idx, count, match);
                    int actualInvocationCount = predicateInvocationCount.get();
                    Assert.assertEquals(expected, actual);
                    Assert.assertEquals(expectedInvocationCount, actualInvocationCount);

                    if (count == list.size()) {
                        // Also test the FindIndex overload that takes no count parameter.
                        predicateInvocationCount.set(0);
                        actual = getListQuery(list).findLastIndex(idx, match);
                        Assert.assertEquals(expected, actual);
                        Assert.assertEquals(expectedInvocationCount, actualInvocationCount);

                        if (idx == list.size() - 1) {
                            // Also test the FindIndex overload that takes no index parameter.
                            predicateInvocationCount.set(0);
                            actual = getListQuery(list).findLastIndex(match);
                            Assert.assertEquals(expected, actual);
                            Assert.assertEquals(expectedInvocationCount, actualInvocationCount);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void convertAllTest() {
        Assert.assertTrue(getListQuery(ImmutableTreeList.<Integer>empty()).convertAll(new Function<Integer, Float>() {
            @Override
            public Float apply(Integer integer) {
                return (float)integer;
            }
        }).isEmpty());
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(new Range(5, 10));
        Function<Integer, Double> converter = new Function<Integer, Double>() {
            @Override
            public Double apply(Integer integer) {
                return 2.0 * integer;
            }
        };
        List<Double> expected = Arrays.asList(10.0, 12.0, 14.0, 16.0, 18.0, 20.0, 22.0, 24.0, 26.0, 28.0);
        ImmutableList<Double> actual = getListQuery(list).convertAll(converter);
        assertEqualSequences(expected, actual);
    }

    @Test
    public void getRangeTest() {
        Assert.assertTrue(getListQuery(ImmutableTreeList.<Integer>empty()).subList(0, 0).isEmpty());
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(new Range(5, 10));
        ArrayList<Integer> bclList = new ArrayList<Integer>(list.toBuilder());

        for (int index = 0; index < list.size(); index++) {
            for (int count = 0; count < list.size() - index; count++) {
                List<Integer> expected = bclList.subList(index, index + count);
                ImmutableList<Integer> actual = getListQuery(list).subList(index, index + count);
                assertEqualSequences(expected, actual);
            }
        }
    }

    @Test
    public void trueForAllTest() {
        Predicate<Integer> FALSE = new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return false;
            }
        };
        Assert.assertTrue(getListQuery(ImmutableTreeList.<Integer>empty()).trueForAll(FALSE));
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(new Range(5, 10));
        trueForAllTestHelper(list, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return (integer % 2) == 0;
            }
        });
        trueForAllTestHelper(list, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return (integer % 2) == 1;
            }
        });
        trueForAllTestHelper(list, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return true;
            }
        });
    }

    @Test
    public void removeAllTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(new Range(5, 10));
        removeAllTestHelper(list, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return false;
            }
        });
        removeAllTestHelper(list, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return true;
            }
        });
        removeAllTestHelper(list, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer < 7;
            }
        });
        removeAllTestHelper(list, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer > 7;
            }
        });
        removeAllTestHelper(list, new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer == 7;
            }
        });
    }

    @Test
    public void reverseTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().addAll(new Range(5, 10));
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size() - i; j++) {
                reverseTestHelper(list, i, j);
            }
        }
    }

    @Test
    public void sortTest() {
        List<ImmutableTreeList<Integer>> scenarios = new ArrayList<ImmutableTreeList<Integer>>(3);
        scenarios.add(ImmutableTreeList.<Integer>empty());
        scenarios.add(ImmutableTreeList.<Integer>empty().addAll(new Range(1, 50)));
        scenarios.add(ImmutableTreeList.<Integer>empty().addAll(new Range(1, 50)).reverse());

        for (ImmutableTreeList<Integer> scenario : scenarios) {
            ArrayList<Integer> expected = new ArrayList<Integer>(scenario.toBuilder());
            Collections.sort(expected);
            ArrayList<Integer> actual = sortTestHelper(scenario);
            assertEqualSequences(expected, actual);

            expected = new ArrayList<Integer>(scenario.toBuilder());
            Comparator<Integer> comparison = new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1 > o2 ? 1 : (o1 < o2 ? -1 : 0);
                }
            };
            Collections.sort(expected, comparison);
            actual = sortTestHelper(scenario, comparison);
            assertEqualSequences(expected, actual);

            expected = new ArrayList<Integer>(scenario.toBuilder());
            Comparator<Integer> comparator = new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            };
            Collections.sort(expected, comparator);
            actual = sortTestHelper(scenario, comparator);
            assertEqualSequences(expected, actual);

            for (int i = 0; i < scenario.size(); i++) {
                for (int j = 0; j < scenario.size() - i; j++) {
                    expected = new ArrayList<Integer>(scenario.toBuilder());
                    Collections.sort(expected.subList(i, i + j), comparator);
                    actual = sortTestHelper(scenario, i, j, comparator);
                    assertEqualSequences(expected, actual);
                }
            }
        }
    }

    @Test
    public void binarySearch() {
        ArrayList<Integer> basis = new ArrayList<Integer>();
        for (int i = 0; i < 50; i++) {
            basis.add((i + 1) * 2);
        }

        ImmutableListQueries<Integer> query = getListQuery(ImmutableTreeList.createAll(basis));
        for (int value = basis.get(0) - 1; value <= basis.get(basis.size() - 1) + 1; value++) {
            int expected = Collections.binarySearch(basis, value);
            int actual = query.binarySearch(value);
            Assert.assertEquals(expected, actual);

            for (int index = 0; index < basis.size() - 1; index++) {
                for (int count = 0; count <= basis.size() - index; count++) {
                    expected = Collections.binarySearch(basis.subList(index, index + count), value);
                    if (expected >= 0) {
                        expected += index;
                    } else {
                        expected -= index;
                    }

                    actual = query.binarySearch(index, index + count, value, null);
                    Assert.assertEquals(expected, actual);
                }
            }
        }
    }

    @Test
    public void binarySearchPartialSortedList() {
        ImmutableTreeList<Integer> reverseSorted = ImmutableTreeList.createAll(new Range(1, 150)).reverse();
        for (int i = 0; i < reverseSorted.size(); i++) {
            reverseSorted.set(i, reverseSorted.get(i) * 2);
        }

        binarySearchPartialSortedListHelper(reverseSorted, 0, 50);
        binarySearchPartialSortedListHelper(reverseSorted, 50, 50);
        binarySearchPartialSortedListHelper(reverseSorted, 100, 50);
    }

    private void binarySearchPartialSortedListHelper(ImmutableTreeList<Integer> inputData, int sortedIndex, int sortedLength) {
        Requires.range(sortedIndex >= 0, "sortedIndex");
        Requires.range(sortedLength > 0, "sortedLength");
        inputData = inputData.sort(sortedIndex, sortedLength, Comparators.<Integer>defaultComparator());
        int min = inputData.get(sortedIndex);
        int max = inputData.get(sortedIndex + sortedLength - 1);

        ArrayList<Integer> basis = new ArrayList<Integer>(inputData.toBuilder());
        ImmutableListQueries<Integer> query = getListQuery(ImmutableTreeList.createAll(inputData));
        for (int value = min - 1; value <= max + 1; value++) {
            for (int index = sortedIndex; index < sortedIndex + sortedLength; index++) // make sure the index we pass in is always within the sorted range in the list.
            {
                for (int count = 0; count <= sortedLength - index; count++) {
                    int expected = Collections.binarySearch(basis.subList(index, index + count), value);
                    if (expected >= 0) {
                        expected += index;
                    } else {
                        expected -= index;
                    }

                    int actual = query.binarySearch(index, index + count, value, null);
                    Assert.assertEquals(expected, actual);
                }
            }
        }
    }

    //[Fact]
    //public void SyncRoot()
    //{
    //    var collection = (ICollection)this.GetEnumerableOf<int>();
    //    Assert.NotNull(collection.SyncRoot);
    //    Assert.Same(collection.SyncRoot, collection.SyncRoot);
    //}

    @Test
    public void iteratorTest() {
        Iterable<Integer> iterable = getIterableOf(1);
        assertEqualSequences(Collections.singletonList(1), iterable); // exercises the iterator
    }

    protected abstract <T> void removeAllTestHelper(ImmutableTreeList<T> list, Predicate<? super T> test);

    protected abstract <T> void reverseTestHelper(ImmutableTreeList<T> list, int index, int count);

    protected abstract <T> ArrayList<T> sortTestHelper(ImmutableTreeList<T> list);

    protected abstract <T> ArrayList<T> sortTestHelper(ImmutableTreeList<T> list, Comparator<? super T> comparator);

    protected abstract <T> ArrayList<T> sortTestHelper(ImmutableTreeList<T> list, int index, int count, Comparator<? super T> comparator);

    private <T> void trueForAllTestHelper(ImmutableTreeList<T> list, Predicate<? super T> test) {
        ArrayList<T> bclList = new ArrayList<T>(list.toBuilder());
        boolean expected = trueForAll(bclList, test);
        boolean actual = getListQuery(list).trueForAll(test);
        Assert.assertEquals(expected, actual);
    }

    private static <T> boolean trueForAll(ArrayList<T> list, Predicate<? super T> test) {
        for (T item : list) {
            if (!test.test(item)) {
                return false;
            }
        }

        return true;
    }
}
