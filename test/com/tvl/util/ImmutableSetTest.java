// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tvl.util.function.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class ImmutableSetTest extends ImmutablesTestBase {
    @Test
    public final void addTest() {
        this.addTestHelper(this.<Integer>empty(), 3, 5, 4, 3);
    }

    @Test
    public final void addDuplicatesTest() {
        Integer[] arrayWithDuplicates = ImmutableArrayList.createAll(new Range(1, 100)).addAll(new Range(1, 100)).toBuilder().toArray(new Integer[0]);
        this.addTestHelper(this.<Integer>empty(), arrayWithDuplicates);
    }

    @Test
    public final void removeTest() {
        this.removeTestHelper(this.<Integer>empty().add(3).add(5), 5, 3);
    }

    @Test
    public final void addRemoveLoadTest() {
        Double[] data = this.generateDummyFillData();
        this.addRemoveLoadTestHelper(this.<Double>empty(), data);
    }

    @Test
    public final void removeNonExistingTest() {
        this.removeNonExistingTest(this.<Integer>empty());
    }

    @Test
    public final void addBulkFromImmutableToEmpty() {
        ImmutableSet<Integer> set = this.<Integer>empty().add(5);
        ImmutableSet<Integer> empty2 = this.<Integer>empty();
        assertSame("Filling an empty immutable set with the contents of another immutable set with the exact same comparer should return the other set.", set, empty2.union(set));
    }

    @Test
    public final void exceptTest() {
        this.exceptTestHelper(this.<Integer>empty().add(1).add(3).add(5).add(7), 3, 7);
    }

    @Test
    public final void symmetricExceptTest() {
        this.symmetricExceptTestHelper(this.<Integer>empty().add(1).add(3).add(5).add(7), Iterables.toArray(new Range(0, 9), Integer.class));
        this.symmetricExceptTestHelper(this.<Integer>empty().add(1).add(3).add(5).add(7), Iterables.toArray(new Range(0, 5), Integer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void iteratorTest() {
        Comparator<? super Double> comparator = null;
        ImmutableSet<Double> set = this.empty();
        if (set instanceof SortKeyCollection<?>) {
            comparator = ((SortKeyCollection<Double>)set).getKeyComparator();
        }

        this.iteratorTestHelper(set, comparator, 3., 5., 1.);
        Double[] data = this.generateDummyFillData();
        this.iteratorTestHelper(set, comparator, data);
    }

    @Test
    public final void intersectTest() {
        this.intersectTestHelper(this.<Integer>empty().union(new Range(1, 10)), 8, 3, 5);
    }

    @Test
    public final void unionTest() {
        this.unionTestHelper(this.<Integer>empty(), new Integer[] { 1, 3, 5, 7 });
        this.unionTestHelper(this.<Integer>empty().union(Arrays.asList(2, 4, 6)), new Integer[] { 1, 3, 5, 7 });
        this.unionTestHelper(this.<Integer>empty().union(Arrays.asList(1, 2, 3)), new Integer[0]);
        this.unionTestHelper(this.<Integer>empty().union(Arrays.asList(2)), Iterables.toArray(new Range(0, 1000), Integer.class));
    }

    @Test
    public void setEqualsTest() {
        assertTrue(this.<Integer>empty().setEquals(this.<Integer>empty()));
        ImmutableSet<Integer> nonEmptySet = this.<Integer>empty().add(5);
        assertTrue(nonEmptySet.setEquals(nonEmptySet));

        this.setCompareTestHelper(
            new Function<ImmutableSet<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final ImmutableSet<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            return s.setEquals(t);
                        }
                    };
                }
            },
            new Function<Set<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final Set<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            return s.equals(Sets.newHashSet(t));
                        }
                    };
                }
            },
            this.getSetEqualsScenarios());
    }

    @Test
    public void isProperSubsetOfTest() {
        this.setCompareTestHelper(
            new Function<ImmutableSet<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final ImmutableSet<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            return s.isProperSubsetOf(t);
                        }
                    };
                }
            },
            new Function<Set<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final Set<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            HashSet<Integer> set = Sets.newHashSet(t);
                            return s.size() < set.size() && set.containsAll(s);
                        }
                    };
                }
            },
            this.getIsProperSubsetOfScenarios());
    }

    @Test
    public void isProperSupersetOfTest() {
        this.setCompareTestHelper(
            new Function<ImmutableSet<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final ImmutableSet<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            return s.isProperSupersetOf(t);
                        }
                    };
                }
            },
            new Function<Set<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final Set<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            HashSet<Integer> set = Sets.newHashSet(t);
                            return s.size() > set.size() && s.containsAll(set);
                        }
                    };
                }
            },
            Iterables.transform(
                this.getIsProperSubsetOfScenarios(),
                new com.google.common.base.Function<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>, Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>>() {
                    @Override
                    public Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean> apply(Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean> from) {
                        return flip(from);
                    }
                }));
    }

    @Test
    public void isSubsetOfTest() {
        this.setCompareTestHelper(
            new Function<ImmutableSet<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final ImmutableSet<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            return s.isSubsetOf(t);
                        }
                    };
                }
            },
            new Function<Set<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final Set<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            HashSet<Integer> set = Sets.newHashSet(t);
                            return s.size() <= set.size() && set.containsAll(s);
                        }
                    };
                }
            },
            this.getIsSubsetOfScenarios());
    }

    @Test
    public void isSupersetOfTest() {
        this.setCompareTestHelper(
            new Function<ImmutableSet<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final ImmutableSet<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            return s.isSupersetOf(t);
                        }
                    };
                }
            },
            new Function<Set<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final Set<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            HashSet<Integer> set = Sets.newHashSet(t);
                            return s.size() >= set.size() && s.containsAll(set);
                        }
                    };
                }
            },
            Iterables.transform(
                this.getIsSubsetOfScenarios(),
                new com.google.common.base.Function<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>, Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>>() {
                    @Override
                    public Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean> apply(Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean> from) {
                        return flip(from);
                    }
                }));
    }

    @Test
    public void overlapsTest() {
        this.setCompareTestHelper(
            new Function<ImmutableSet<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final ImmutableSet<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            return s.overlaps(t);
                        }
                    };
                }
            },
            new Function<Set<Integer>, Function<Iterable<Integer>, Boolean>>() {
                @Override
                public Function<Iterable<Integer>, Boolean> apply(final Set<Integer> s) {
                    return new Function<Iterable<Integer>, Boolean>() {
                        @Override
                        public Boolean apply(Iterable<Integer> t) {
                            HashSet<Integer> set = Sets.newHashSet(t);
                            set.retainAll(s);
                            return !set.isEmpty();
                        }
                    };
                }
            },
            this.getOverlapsScenarios());
    }

    @Test
    public final void equalsTest() {
        assertFalse(this.<Integer>empty().equals(null));
        assertFalse(this.<Integer>empty().equals("hi"));
        assertTrue(this.<Integer>empty().equals(this.<Integer>empty()));
        assertFalse(this.<Integer>empty().add(3).equals(this.<Integer>empty().add(3)));
        assertFalse(this.<Integer>empty().add(5).equals(this.<Integer>empty().add(3)));
        assertFalse(this.<Integer>empty().add(3).add(5).equals(this.<Integer>empty().add(3)));
        assertFalse(this.<Integer>empty().add(3).equals(this.<Integer>empty().add(3).add(5)));
    }

    @Test
    public final void hashCodeTest() {
        // verify that get hash code is the default address based one.
        assertEquals(EqualityComparators.defaultComparator().hashCode(this.<Integer>empty()), this.<Integer>empty().hashCode());
    }

    @Test
    public final void clearTest() {
        ImmutableSet<Integer> originalSet = this.<Integer>empty();
        ImmutableSet<Integer> nonEmptySet = originalSet.add(5);
        ImmutableSet<Integer> clearedSet = nonEmptySet.clear();
        assertSame(originalSet, clearedSet);
    }

//    [Fact]
//    public void ISetMutationMethods()
//    {
//        var set = (ISet<int>)this.Empty<int>();
//        Assert.Throws<NotSupportedException>(() => set.Add(0));
//        Assert.Throws<NotSupportedException>(() => set.ExceptWith(null));
//        Assert.Throws<NotSupportedException>(() => set.UnionWith(null));
//        Assert.Throws<NotSupportedException>(() => set.IntersectWith(null));
//        Assert.Throws<NotSupportedException>(() => set.SymmetricExceptWith(null));
//    }
//
//    [Fact]
//    public void ICollectionOfTMembers()
//    {
//        var set = (ICollection<int>)this.Empty<int>();
//        Assert.Throws<NotSupportedException>(() => set.Add(1));
//        Assert.Throws<NotSupportedException>(() => set.Clear());
//        Assert.Throws<NotSupportedException>(() => set.Remove(1));
//        Assert.True(set.IsReadOnly);
//    }
//
//    [Fact]
//    public void ICollectionMethods()
//    {
//        ICollection builder = (ICollection)this.Empty<string>();
//        string[] array = new string[0];
//        builder.CopyTo(array, 0);
//
//        builder = (ICollection)this.Empty<string>().Add("a");
//        array = new string[builder.Count + 1];
//
//        builder.CopyTo(array, 1);
//        Assert.Equal(new[] { null, "a" }, array);
//
//        Assert.True(builder.IsSynchronized);
//        Assert.NotNull(builder.SyncRoot);
//        Assert.Same(builder.SyncRoot, builder.SyncRoot);
//    }

    protected abstract boolean getIncludesGetHashCodeDerivative();

//    internal static List<T> ToListNonGeneric<T>(System.Collections.IEnumerable sequence)
//    {
//        Contract.Requires(sequence != null);
//        var list = new List<T>();
//        var enumerator = sequence.GetEnumerator();
//        while (enumerator.MoveNext())
//        {
//            list.Add((T)enumerator.Current);
//        }
//
//        return list;
//    }

    protected abstract <T> ImmutableSet<T> empty();

    protected abstract <T> Set<T> emptyMutable();

    abstract <T> BinaryTree<T> getRootNode(ImmutableSet<T> set);

    protected final void tryGetValueTestHelper(ImmutableSet<String> set) {
        Requires.notNull(set, "set");

        String expected = "egg";
        set = set.add(expected);
        String lookupValue = expected.toUpperCase();
        String actual = set.tryGetValue(lookupValue);
        assertNotNull(actual);
        assertSame(expected, actual);

        actual = set.tryGetValue("foo");
        assertNull(actual);

        assertNull(set.clear().tryGetValue("nonexistent"));
    }

    protected final <T> ImmutableSet<T> setWith(T... items) {
        return this.<T>empty().union(Arrays.asList(items));
    }

    protected final <T> void customSortTestHelper(ImmutableSet<T> emptySet, boolean matchOrder, T[] injectedValues, T[] expectedValues) {
        assertNotNull(emptySet);
        assertNotNull(injectedValues);
        assertNotNull(expectedValues);

        ImmutableSet<T> set = emptySet;
        for (T value : injectedValues) {
            set = set.add(value);
        }

        assertEquals(expectedValues.length, set.size());
        if (matchOrder) {
            assertThat(set, containsInAnyOrder(expectedValues));
        } else {
            assertThat(set, contains(expectedValues));
        }
    }

    /**
     * Tests various aspects of a set. This should be called only from the unordered or sorted overloads of this method.
     *
     * @param <T> The type of element stored in the set.
     * @param emptySet The empty set.
     */
    protected final <T> void emptyTestHelper(ImmutableSet<T> emptySet) {
        assertNotNull(emptySet);

        assertEquals("Empty set should have a size() of 0", 0, emptySet.size());
        assertEquals("Enumeration of an empty set yielded elements.", 0, emptySet.size());
        assertSame(emptySet, emptySet.clear());
    }

    private Iterable<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>> getSetEqualsScenarios() {
        List<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>> result = new ArrayList<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>>();
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(this.<Integer>setWith(), Collections.<Integer>emptyList(), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(setWith(5), Arrays.asList(5), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(setWith(5), Arrays.asList(5, 5), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(setWith(5, 8), Arrays.asList(5, 5), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(setWith(5, 8), Arrays.asList(5, 7), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(setWith(5, 8), Arrays.asList(5, 8), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(setWith(5), Collections.<Integer>emptyList(), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(this.<Integer>setWith(), Arrays.asList(5), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(setWith(5, 8), Arrays.asList(5), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(setWith(5), Arrays.asList(5, 8), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(setWith(5, 8), setWith(5, 8), true));

        return result;
    }

    private Iterable<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>> getIsProperSubsetOfScenarios() {
        List<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>> result = new ArrayList<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>>();
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.<Integer>emptyList(), Collections.<Integer>emptyList(), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Collections.<Integer>emptyList(), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Collections.singletonList(2), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Arrays.asList(2, 3), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Arrays.asList(1, 2), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.<Integer>emptyList(), Collections.singletonList(1), true));

        return result;
    }

    private Iterable<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>> getIsSubsetOfScenarios() {
        List<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>> result = new ArrayList<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>>();
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.<Integer>emptyList(), Collections.<Integer>emptyList(), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Collections.singletonList(1), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Arrays.asList(1, 2), Arrays.asList(1, 2), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Collections.<Integer>emptyList(), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Collections.singletonList(2), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Arrays.asList(2, 3), false));

        // By definition, any proper subset is also a subset.
        // But because a subset may not be a proper subset, we filter the proper- scenarios.
        Iterables.addAll(result, Iterables.filter(getIsProperSubsetOfScenarios(), new Predicate<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>>() {
            @Override
            public boolean apply(Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean> input) {
                return input.getItem3();
            }
        }));
        return result;
    }

    private Iterable<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>> getOverlapsScenarios() {
        List<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>> result = new ArrayList<Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>>();
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.<Integer>emptyList(), Collections.<Integer>emptyList(), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.<Integer>emptyList(), Collections.singletonList(1), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Collections.singletonList(2), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Arrays.asList(2, 3), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Arrays.asList(1, 2), Collections.singletonList(3), false));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Arrays.asList(1, 2), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Arrays.asList(1, 2), Collections.singletonList(1), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Collections.singletonList(1), Collections.singletonList(1), true));
        result.add(new Tuple3<Iterable<Integer>, Iterable<Integer>, Boolean>(Arrays.asList(1, 2), Arrays.asList(2, 3, 4), true));

        return result;
    }

    private <T> void setCompareTestHelper(Function<ImmutableSet<T>, Function<Iterable<T>, Boolean>> operation, Function<Set<T>, Function<Iterable<T>, Boolean>> baselineOperation, Iterable<Tuple3<Iterable<T>, Iterable<T>, Boolean>> scenarios) {
        //const string message = "Scenario #{0}: Set 1: {1}, Set 2: {2}";

        int iteration = 0;
        for (Tuple3<Iterable<T>, Iterable<T>, Boolean> scenario : scenarios) {
            iteration++;

            // Figure out the response expected based on the BCL mutable collections.
            Set<T> baselineSet = this.emptyMutable();
            baselineSet.addAll(Lists.newArrayList(scenario.getItem1()));
            Function<Iterable<T>, Boolean> expectedFunc = baselineOperation.apply(baselineSet);
            boolean expected = expectedFunc.apply(scenario.getItem2());
            assertEquals("Test scenario has an expected result that is inconsistent with BCL mutable collection behavior.", expected, scenario.getItem3());

            @SuppressWarnings("unchecked")
            Function<Iterable<T>, Boolean> actualFunc = operation.apply(this.setWith((T[])Iterables.toArray(scenario.getItem1(), Object.class)));
            //Object[] args = new Object[] { iteration, toStringDeferred(scenario.getItem1()), toStringDeferred(scenario.getItem2()) };
            assertEquals(scenario.getItem3(), actualFunc.apply(this.setWith((T[])Iterables.toArray(scenario.getItem2(), Object.class)))); //, message, args);
            assertEquals(scenario.getItem3(), actualFunc.apply(scenario.getItem2())); //, message, args);
        }
    }

    private static <T> Tuple3<Iterable<T>, Iterable<T>, Boolean> flip(Tuple3<Iterable<T>, Iterable<T>, Boolean> scenario) {
        return new Tuple3<Iterable<T>, Iterable<T>, Boolean>(scenario.getItem2(), scenario.getItem1(), scenario.getItem3());
    }

    private <T> void removeTestHelper(ImmutableSet<T> set, T... values) {
        assertNotNull(set);
        assertNotNull(values);

        assertSame(set, set.except(Collections.<T>emptyList()));

        int initialCount = set.size();
        int removedCount = 0;
        for (T value : values) {
            ImmutableSet<T> nextSet = set.remove(value);
            assertNotSame(set, nextSet);
            assertEquals(initialCount - removedCount, set.size());
            assertEquals(initialCount - removedCount - 1, nextSet.size());

            assertSame("Removing a non-existing element should not change the set reference.", nextSet, nextSet.remove(value));
            removedCount++;
            set = nextSet;
        }

        assertEquals(initialCount - removedCount, set.size());
    }

    private void removeNonExistingTest(ImmutableSet<Integer> emptySet) {
        assertSame(emptySet, emptySet.remove(5));

        // Also fill up a set with many elements to build up the tree, then remove from various places in the tree.
        final int Size = 200;
        ImmutableSet<Integer> set = emptySet;
        for (int i = 0; i < Size; i += 2) {
            // only even numbers!
            set = set.add(i);
        }

        // Verify that removing odd numbers doesn't change anything.
        for (int i = 1; i < Size; i += 2) {
            ImmutableSet<Integer> setAfterRemoval = set.remove(i);
            assertSame(set, setAfterRemoval);
        }
    }

    private <T> void addRemoveLoadTestHelper(ImmutableSet<T> set, T[] data) {
        assertNotNull(set);
        assertNotNull(data);

        for (T value : data) {
            ImmutableSet<T> newSet = set.add(value);
            assertNotSame(set, newSet);
            set = newSet;
        }

        for (T value : data) {
            assertTrue(set.contains(value));
        }

        for (T value : data) {
            ImmutableSet<T> newSet = set.remove(value);
            assertNotSame(set, newSet);
            set = newSet;
        }
    }

    protected final <T> void iteratorTestHelper(ImmutableSet<T> emptySet, Comparator<? super T> comparator, T... values) {
        ImmutableSet<T> set = emptySet;
        for (T value : values) {
            set = set.add(value);
        }

        //List<T> nonGenericEnumerableList = toListNonGeneric(set);
        //assertThat(nonGenericEnumerableList, containsInAnyOrder(values));
        List<T> list = Lists.newArrayList(set);
        assertThat(set, containsInAnyOrder(values));

        if (comparator != null) {
            Arrays.sort(values, comparator);
            assertThat(set, contains(values));
        }

        // Apply some less common uses to the enumerator to test its metal.
        Iterator<T> iterator = set.iterator();

        //Assert.Throws<InvalidOperationException>(() => enumerator.Current);
        //enumerator.Reset(); // reset isn't usually called before MoveNext
        //Assert.Throws<InvalidOperationException>(() => enumerator.Current);
        manuallyIterateTest(list, iterator);
        assertFalse(iterator.hasNext()); // call it again to make sure it still returns false
        try {
            iterator.next();
            fail("Expected an exception");
        } catch (NoSuchElementException ex) {
        }

        //enumerator.Reset();
        //Assert.Throws<InvalidOperationException>(() => enumerator.Current);
        //ManuallyEnumerateTest(list, enumerator);
        //Assert.Throws<InvalidOperationException>(() => enumerator.Current);
        //
        //// this time only partially enumerate
        //enumerator.Reset();
        //enumerator.MoveNext();
        //enumerator.Reset();
        //ManuallyEnumerateTest(list, enumerator);
        //Assert.Throws<ObjectDisposedException>(() => enumerator.Reset());
        //Assert.Throws<ObjectDisposedException>(() => enumerator.MoveNext());
        //Assert.Throws<ObjectDisposedException>(() => enumerator.Current);
    }

    private <T> void exceptTestHelper(ImmutableSet<T> set, T... valuesToRemove) {
        //Contract.Requires(set != null);
        //Contract.Requires(valuesToRemove != null);

        HashSet<T> expectedSet = Sets.newHashSet(set);
        expectedSet.removeAll(Arrays.asList(valuesToRemove));

        ImmutableSet<T> actualSet = set.except(Arrays.asList(valuesToRemove));
        assertThat(actualSet, containsInAnyOrder(expectedSet.toArray()));

        this.verifyAvlTreeState(actualSet);
    }

    private <T> void symmetricExceptTestHelper(ImmutableSet<T> set, T... otherCollection) {
        //Contract.Requires(set != null);
        //Contract.Requires(otherCollection != null);

        HashSet<T> intersection = Sets.newHashSet(set);
        intersection.retainAll(Arrays.asList(otherCollection));

        HashSet<T> expectedSet = Sets.newHashSet(set);
        expectedSet.addAll(Arrays.asList(otherCollection));
        expectedSet.removeAll(intersection);

        ImmutableSet<T> actualSet = set.symmetricExcept(Arrays.asList(otherCollection));
        assertThat(actualSet, containsInAnyOrder(expectedSet.toArray()));

        this.verifyAvlTreeState(actualSet);
    }

    private <T> void intersectTestHelper(ImmutableSet<T> set, T... values) {
        //Contract.Requires(set != null);
        //Contract.Requires(values != null);

        assertTrue(set.intersect(Collections.<T>emptyList()).size() == 0);

        HashSet<T> expected = Sets.newHashSet(set);
        expected.retainAll(Arrays.asList(values));

        ImmutableSet<T> actual = set.intersect(Arrays.asList(values));
        assertThat(actual, containsInAnyOrder(expected.toArray()));

        this.verifyAvlTreeState(actual);
    }

    private <T> void unionTestHelper(ImmutableSet<T> set, T... values) {
        //Contract.Requires(set != null);
        //Contract.Requires(values != null);

        HashSet<T> expected = Sets.newHashSet(set);
        expected.addAll(Arrays.asList(values));

        ImmutableSet<T> actual = set.union(Arrays.asList(values));
        assertThat(actual, containsInAnyOrder(expected.toArray()));

        this.verifyAvlTreeState(actual);
    }

    private <T> void addTestHelper(ImmutableSet<T> set, T... values) {
        //Contract.Requires(set != null);
        //Contract.Requires(values != null);

        assertSame(set, set.union(Collections.<T>emptyList()));

        int initialCount = set.size();

        HashSet<T> uniqueValues = new HashSet<T>(Arrays.asList(values));
        ImmutableSet<T> enumerateAddSet = set.union(Arrays.asList(values));
        assertEquals(initialCount + uniqueValues.size(), enumerateAddSet.size());
        for (T value : values) {
            assertTrue(enumerateAddSet.contains(value));
        }

        int addedCount = 0;
        for (T value : values) {
            boolean duplicate = set.contains(value);
            ImmutableSet<T> nextSet = set.add(value);
            assertTrue(nextSet.size() > 0);
            assertEquals(initialCount + addedCount, set.size());
            int expectedCount = initialCount + addedCount;
            if (!duplicate) {
                expectedCount++;
            }

            assertEquals(expectedCount, nextSet.size());
            assertEquals(duplicate, set.contains(value));
            assertTrue(nextSet.contains(value));
            if (!duplicate) {
                addedCount++;
            }

            assertSame(String.format("Adding duplicate value %s should keep the original reference.", value), nextSet, nextSet.add(value));
            set = nextSet;
        }
    }

    private <T> void verifyAvlTreeState(ImmutableSet<T> set) {
        BinaryTree<T> rootNode = this.getRootNode(set);
        TestExtensionMethods.verifyBalanced(rootNode);
        TestExtensionMethods.verifyHeightIsWithinTolerance(rootNode, set.size());
    }
}
