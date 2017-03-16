// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ImmutableTreeSetTest extends ImmutableSetTest {
    private enum Operation {
        ADD,
        UNION,
        REMOVE,
        EXCEPT,
        LAST,
    }

    @Override
    protected boolean getIncludesGetHashCodeDerivative() {
        return false;
    }

    @Test
    public void randomOperationsTest() {
        int operationCount = this.getRandomOperationsCount();
        TreeSet<Integer> expected = new TreeSet<Integer>();
        ImmutableTreeSet<Integer> actual = ImmutableTreeSet.empty();

        long seed = System.nanoTime();
        System.out.format("Using random seed %s%n", seed);
        final Random random = new Random(seed);

        Operation[] operations = Operation.values();
        for (int iOp = 0; iOp < operationCount; iOp++) {
            switch (operations[random.nextInt(operations.length)]) {
            case ADD:
                int value = random.nextInt();
                System.out.format("Adding \"%s\" to the set.%n", value);
                expected.add(value);
                actual = actual.add(value);
                break;
            case UNION:
                int inputLength = random.nextInt(100);
                Integer[] values = Iterables.toArray(
                    Iterables.<Integer, Integer>transform(
                        new Range(0, inputLength),
                        new Function<Integer, Integer>() {
                            @Override
                            public Integer apply(Integer t) {
                                return random.nextInt();
                            }
                        }),
                    Integer.class);
                System.out.format("Adding %s elements to the set.%n", inputLength);
                expected.addAll(Arrays.asList(values));
                actual = actual.union(Arrays.asList(values));
                break;
            case REMOVE:
                if (expected.size() > 0) {
                    int position = random.nextInt(expected.size());
                    int element = Iterables.get(expected, position);
                    System.out.format("Removing element \"%s\" from the set.", element);
                    assertTrue(expected.remove(element));
                    actual = actual.remove(element);
                }

                break;
            case EXCEPT:
                Integer[] elements = Iterables.toArray(
                    Iterables.filter(
                        expected,
                        new Predicate<Integer>() {
                            @Override
                            public boolean apply(Integer input) {
                                return random.nextInt(2) == 0;
                            }
                        }),
                    Integer.class);
                System.out.format("Removing %s elements from the set.%n", elements.length);
                expected.removeAll(Arrays.asList(elements));
                actual = actual.except(Arrays.asList(elements));
                break;
            }

            assertEqualSequences(expected, actual);
        }
    }

    @Test
    public void emptyTest() {
        this.emptyTestHelper(this.<Integer>empty(), 5, null);
        this.emptyTestHelper(Immutables.toImmutableTreeSet(this.<String>empty(), ordinalIgnoreCaseComparator()), "a", ordinalIgnoreCaseComparator());
    }

    @Test
    public void customSort() {
        this.customSortTestHelper(
            ImmutableTreeSet.<String>empty().withComparator(ordinalComparator()),
            true,
            new String[] { "apple", "APPLE" },
            new String[] { "APPLE", "apple" });
        this.customSortTestHelper(
            ImmutableTreeSet.<String>empty().withComparator(ordinalIgnoreCaseComparator()),
            true,
            new String[] { "apple", "APPLE" },
            new String[] { "apple" });
    }

    @Test
    public void changeSortComparator() {
        ImmutableTreeSet<String> ordinalSet = ImmutableTreeSet.<String>empty()
            .withComparator(ordinalComparator())
            .add("apple")
            .add("APPLE");
        assertEquals(2, ordinalSet.size()); // claimed count
        assertFalse(ordinalSet.contains("aPpLe"));

        ImmutableTreeSet<String> ignoreCaseSet = ordinalSet.withComparator(ordinalIgnoreCaseComparator());
        assertEquals(1, ignoreCaseSet.size());
        assertTrue(ignoreCaseSet.contains("aPpLe"));
    }

//        [Fact]
//        public void ToUnorderedTest()
//        {
//            var result = ImmutableSortedSet<int>.Empty.Add(3).ToImmutableHashSet();
//            Assert.True(result.Contains(3));
//        }

    @Test
    public void toImmutableTreeSetTest() {
        ImmutableTreeSet<Integer> set = Immutables.toImmutableTreeSet(Arrays.asList(1, 2, 2));
        assertSame(Comparators.anyComparator(), set.getKeyComparator());
        assertEquals(2, set.size());
    }

    @Test
    public void indexOfTest() {
        ImmutableTreeSet<Integer> set = ImmutableTreeSet.empty();
        assertEquals(~0, set.indexOf(5));

        set = ImmutableTreeSet.<Integer>empty().union(Iterables.transform(
            new Range(1, 10),
            new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer n) {
                return n * 10; // 10, 20, 30, ... 100
            }
        }));
        assertEquals(0, set.indexOf(10));
        assertEquals(1, set.indexOf(20));
        assertEquals(4, set.indexOf(50));
        assertEquals(8, set.indexOf(90));
        assertEquals(9, set.indexOf(100));

        assertEquals(~0, set.indexOf(5));
        assertEquals(~1, set.indexOf(15));
        assertEquals(~2, set.indexOf(25));
        assertEquals(~5, set.indexOf(55));
        assertEquals(~9, set.indexOf(95));
        assertEquals(~10, set.indexOf(105));
    }

    @Test
    public void indexGetTest() {
        ImmutableTreeSet<Integer> set = ImmutableTreeSet.<Integer>empty()
            .union(Iterables.transform(
                new Range(1, 10),
                new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer n) {
                        return n * 10; // 10, 20, 30, ... 100
                    }
                }));

        int i = 0;
        for (Integer item : set) {
            assertSame(item, set.get(i++));
        }
    }

    @Test
    public void indexGetTestNegative() {
        ImmutableTreeSet<Integer> set = ImmutableTreeSet.<Integer>empty()
            .union(Iterables.transform(
                new Range(1, 10),
                new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer n) {
                        return n * 10; // 10, 20, 30, ... 100
                    }
                }));

        thrown.expect(IndexOutOfBoundsException.class);
        set.get(-1);
    }

    @Test
    public void indexGetTestOver() {
        ImmutableTreeSet<Integer> set = ImmutableTreeSet.<Integer>empty()
            .union(Iterables.transform(
                new Range(1, 10),
                new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer n) {
                        return n * 10; // 10, 20, 30, ... 100
                    }
                }));

        thrown.expect(IndexOutOfBoundsException.class);
        set.get(set.size());
    }

    @Test
    public void reverseTest() {
        Iterable<Integer> range = new Range(1, 10);
        ImmutableTreeSet<Integer> set = ImmutableTreeSet.<Integer>empty().union(range);
        Integer[] expected = Iterables.toArray(Lists.reverse(Lists.newArrayList(range)), Integer.class);
        Iterable<Integer> actual = set.reverse();
        assertThat(actual, contains(expected));
    }

    @Test
    public void maxTest() {
        assertEquals(5, (int)ImmutableTreeSet.<Integer>empty().union(new Range(1, 5)).getMax());
        assertNull(ImmutableTreeSet.<Integer>empty().getMax());
    }

    @Test
    public void minTest() {
        assertEquals(1, (int)ImmutableTreeSet.<Integer>empty().union(new Range(1, 5)).getMin());
        assertNull(ImmutableTreeSet.<Integer>empty().getMin());
    }

    @Test
    public void initialBulkAdd() {
        assertEquals(1, this.<Integer>empty().union(Arrays.asList(1, 1)).size());
        assertEquals(2, this.<Integer>empty().union(Arrays.asList(1, 2)).size());
    }

//        [Fact]
//        public void ICollectionOfTMethods()
//        {
//            ICollection<string> set = ImmutableSortedSet.Create<string>();
//            Assert.Throws<NotSupportedException>(() => set.Add("a"));
//            Assert.Throws<NotSupportedException>(() => set.Clear());
//            Assert.Throws<NotSupportedException>(() => set.Remove("a"));
//            Assert.True(set.IsReadOnly);
//        }
//
//        [Fact]
//        public void IListOfTMethods()
//        {
//            IList<string> set = ImmutableSortedSet.Create<string>("b");
//            Assert.Throws<NotSupportedException>(() => set.Insert(0, "a"));
//            Assert.Throws<NotSupportedException>(() => set.RemoveAt(0));
//            Assert.Throws<NotSupportedException>(() => set[0] = "a");
//            Assert.Equal("b", set[0]);
//            Assert.True(set.IsReadOnly);
//        }

    @Test
    public void unionOptimizationsTest() {
        ImmutableTreeSet<Integer> set = ImmutableTreeSet.create(1, 2, 3);
        ImmutableTreeSet.Builder<Integer> builder = set.toBuilder();

        assertSame(set, ImmutableTreeSet.<Integer>create().union(builder));
        assertSame(set, set.union(ImmutableTreeSet.<Integer>create()));

        ImmutableTreeSet<Integer> smallSet = ImmutableTreeSet.create(1);
        ImmutableTreeSet<Integer> unionSet = smallSet.union(set);
        assertSame(set, unionSet); // adding a larger set to a smaller set is reversed, and then the smaller in this case has nothing unique
    }

    @Test
    public void create() {
        Comparator<String> comparator = ordinalIgnoreCaseComparator();

        ImmutableTreeSet<String> set = ImmutableTreeSet.create();
        assertEquals(0, set.size());
        assertSame(Comparators.anyComparator(), set.getKeyComparator());

        set = ImmutableTreeSet.create(comparator);
        assertEquals(0, set.size());
        assertSame(comparator, set.getKeyComparator());

        set = ImmutableTreeSet.create("a");
        assertEquals(1, set.size());
        assertSame(Comparators.anyComparator(), set.getKeyComparator());

        set = ImmutableTreeSet.create(comparator, "a");
        assertEquals(1, set.size());
        assertSame(comparator, set.getKeyComparator());

        set = ImmutableTreeSet.create("a", "b");
        assertEquals(2, set.size());
        assertSame(Comparators.anyComparator(), set.getKeyComparator());

        set = ImmutableTreeSet.create(comparator, "a", "b");
        assertEquals(2, set.size());
        assertSame(comparator, set.getKeyComparator());

        set = ImmutableTreeSet.createAll(Arrays.asList("a", "b"));
        assertEquals(2, set.size());
        assertSame(Comparators.anyComparator(), set.getKeyComparator());

        set = ImmutableTreeSet.createAll(comparator, Arrays.asList("a", "b"));
        assertEquals(2, set.size());
        assertSame(comparator, set.getKeyComparator());
    }

//        [Fact]
//        public void IListMethods()
//        {
//            IList list = ImmutableSortedSet.Create("a", "b");
//            Assert.True(list.Contains("a"));
//            Assert.Equal("a", list[0]);
//            Assert.Equal("b", list[1]);
//            Assert.Equal(0, list.IndexOf("a"));
//            Assert.Equal(1, list.IndexOf("b"));
//            Assert.Throws<NotSupportedException>(() => list.Add("b"));
//            Assert.Throws<NotSupportedException>(() => list[3] = "c");
//            Assert.Throws<NotSupportedException>(() => list.Clear());
//            Assert.Throws<NotSupportedException>(() => list.Insert(0, "b"));
//            Assert.Throws<NotSupportedException>(() => list.Remove("a"));
//            Assert.Throws<NotSupportedException>(() => list.RemoveAt(0));
//            Assert.True(list.IsFixedSize);
//            Assert.True(list.IsReadOnly);
//        }

    @Test
    public void tryGetValueTest() {
        this.tryGetValueTestHelper(ImmutableTreeSet.<String>empty().withComparator(ordinalIgnoreCaseComparator()));
    }

//        [Fact]
//        public void EnumeratorRecyclingMisuse()
//        {
//            var collection = ImmutableSortedSet.Create<int>();
//            var enumerator = collection.GetEnumerator();
//            var enumeratorCopy = enumerator;
//            Assert.False(enumerator.MoveNext());
//            enumerator.Dispose();
//            Assert.Throws<ObjectDisposedException>(() => enumerator.MoveNext());
//            Assert.Throws<ObjectDisposedException>(() => enumerator.Reset());
//            Assert.Throws<ObjectDisposedException>(() => enumerator.Current);
//            Assert.Throws<ObjectDisposedException>(() => enumeratorCopy.MoveNext());
//            Assert.Throws<ObjectDisposedException>(() => enumeratorCopy.Reset());
//            Assert.Throws<ObjectDisposedException>(() => enumeratorCopy.Current);
//            enumerator.Dispose(); // double-disposal should not throw
//            enumeratorCopy.Dispose();
//
//            // We expect that acquiring a new enumerator will use the same underlying Stack<T> object,
//            // but that it will not throw exceptions for the new enumerator.
//            enumerator = collection.GetEnumerator();
//            Assert.False(enumerator.MoveNext());
//            Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//            enumerator.Dispose();
//        }
//
//        [Fact]
//        public void DebuggerAttributesValid()
//        {
//            DebuggerAttributes.ValidateDebuggerDisplayReferences(ImmutableSortedSet.Create<int>());
//            DebuggerAttributes.ValidateDebuggerTypeProxyProperties(ImmutableSortedSet.Create<string>("1", "2", "3"));
//
//            object rootNode = DebuggerAttributes.GetFieldValue(ImmutableSortedSet.Create<object>(), "_root");
//            DebuggerAttributes.ValidateDebuggerDisplayReferences(rootNode);
//        }

    @Override
    protected <T> ImmutableSet<T> empty() {
        return ImmutableTreeSet.<T>empty();
    }

    protected final <T> ImmutableTreeSet<T> emptyTyped() {
        return ImmutableTreeSet.<T>empty();
    }

    @Override
    protected <T> Set<T> emptyMutable() {
        return ImmutableTreeSet.createBuilder();
    }

    @Override
    <T> BinaryTree<T> getRootNode(ImmutableSet<T> set) {
        return ((ImmutableTreeSet<T>)set).getRoot();
    }

    /**
     * Tests various aspects of a sorted set.
     *
     * @param <T> The type of element stored in the set.
     * @param emptySet The empty set.
     * @param value A value that could be placed in the set.
     * @param comparator The comparator used to obtain the empty set, if any.
     */
    private <T> void emptyTestHelper(ImmutableSet<T> emptySet, T value, Comparator<? super T> comparator) {
        assertNotNull(emptySet);

        this.emptyTestHelper(emptySet);
        assertSame(emptySet, Immutables.toImmutableTreeSet(emptySet, comparator));
        assertSame(comparator != null ? comparator : Comparators.anyComparator(), ((SortKeyCollection<?>)emptySet).getKeyComparator());

        ImmutableSet<T> reemptied = emptySet.add(value).clear();
        assertSame("Getting the empty set from a non-empty instance did not preserve the comparer.", reemptied, Immutables.toImmutableTreeSet(reemptied, comparator));
    }

}
