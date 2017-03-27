// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImmutableHashSetTest extends ImmutableSetTest {
    @Override
    protected boolean getIncludesGetHashCodeDerivative() {
        return true;
    }

    @Test
    public void testEmptyTest() {
        this.emptyTestHelper(this.<Integer>empty(), 5, null);
        //this.emptyTestHelper(this.<String>emptyTyped().withComparator(ordinalIgnoreCaseComparator()), "a", ordinalIgnoreCaseComparator());
    }

    @Test
    public void testCustomSort() {
        this.customSortTestHelper(
            ImmutableHashSet.<String>empty().withComparator(ordinalComparator()),
            false,
            new String[] { "apple", "APPLE" },
            new String[] { "apple", "APPLE" });
        this.customSortTestHelper(
            ImmutableHashSet.<String>empty().withComparator(ordinalIgnoreCaseComparator()),
            false,
            new String[] { "apple", "APPLE" },
            new String[] { "apple" });
    }

    @Test
    public void testChangeUnorderedEqualityComparator() {
        ImmutableHashSet<String> ordinalSet = ImmutableHashSet.<String>empty()
            .withComparator(ordinalComparator())
            .add("apple")
            .add("APPLE");
        assertEquals(2, ordinalSet.size());
        assertFalse(ordinalSet.contains("aPpLe"));

        ImmutableHashSet<String> ignoreCaseSet = ordinalSet.withComparator(ordinalIgnoreCaseComparator());
        assertEquals(1, ignoreCaseSet.size());
        assertTrue(ignoreCaseSet.contains("aPpLe"));
    }

    @Test
    public void testToSortTest() {
        ImmutableHashSet<String> set = ImmutableHashSet.<String>empty()
            .add("apple")
            .add("APPLE");
        ImmutableTreeSet<String> sorted = Immutables.toImmutableTreeSet(set);
        assertThat(sorted, containsInAnyOrder(Iterables.toArray(set, String.class)));
    }

    @Test
    public void testIteratorWithHashCollisionsTest() {
        ImmutableHashSet<Integer> emptySet = this.<Integer>emptyTyped().withComparator(new BadHasher<Integer>());
        this.iteratorTestHelper(emptySet, null, 3, 1, 5);
    }

    @Test
    public void testIteratorMisuse() {
        ImmutableHashSet<Integer> collection = ImmutableHashSet.<Integer>create().add(5);
        Iterator<Integer> iterator = collection.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(5, (int)iterator.next());
        assertFalse(iterator.hasNext());

        // We expect that acquiring a new iterator will not throw exceptions for the new iterator.
        iterator = collection.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(5, (int)iterator.next());
        assertFalse(iterator.hasNext());

        try {
            iterator.next();
            fail("Expected an exception");
        } catch (NoSuchElementException ex) {
        }
    }

//        [Fact]
//        public void EnumeratorRecyclingMisuse()
//        {
//            var collection = ImmutableHashSet.Create<int>().Add(5);
//            var enumerator = collection.GetEnumerator();
//            var enumeratorCopy = enumerator;
//            Assert.True(enumerator.MoveNext());
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
//            Assert.True(enumerator.MoveNext());
//            Assert.False(enumerator.MoveNext());
//            Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//            enumerator.Dispose();
//        }

    @Test
    public void testCreate() {
        StringComparator comparator = ordinalIgnoreCaseComparator();

        ImmutableHashSet<String> set = ImmutableHashSet.create();
        assertEquals(0, set.size());
        assertSame(EqualityComparators.<String>defaultComparator(), set.getKeyComparator());

        set = ImmutableHashSet.create(comparator);
        assertEquals(0, set.size());
        assertSame(comparator, set.getKeyComparator());

        set = ImmutableHashSet.create("a");
        assertEquals(1, set.size());
        assertSame(EqualityComparators.<String>defaultComparator(), set.getKeyComparator());

        set = ImmutableHashSet.create(comparator, "a");
        assertEquals(1, set.size());
        assertSame(comparator, set.getKeyComparator());

        set = ImmutableHashSet.create("a", "b");
        assertEquals(2, set.size());
        assertSame(EqualityComparators.<String>defaultComparator(), set.getKeyComparator());

        set = ImmutableHashSet.create(comparator, "a", "b");
        assertEquals(2, set.size());
        assertSame(comparator, set.getKeyComparator());

        set = ImmutableHashSet.createAll(Arrays.asList("a", "b"));
        assertEquals(2, set.size());
        assertSame(EqualityComparators.<String>defaultComparator(), set.getKeyComparator());

        set = ImmutableHashSet.createAll(comparator, Arrays.asList("a", "b"));
        assertEquals(2, set.size());
        assertSame(comparator, set.getKeyComparator());
    }

    /**
     * Verifies the non-removal of an item that does not belong to the set, but which happens to have a colliding hash
     * code with another value that <em>is</em> in the set.
     */
    @Test
    public void testRemoveValuesFromCollidedHashCode() {
        ImmutableHashSet<Integer> set = ImmutableHashSet.create(new BadHasher<Integer>(), 5, 6);
        assertSame(set, set.remove(2));
        ImmutableHashSet<Integer> setAfterRemovingFive = set.remove(5);
        assertEquals(1, setAfterRemovingFive.size());
        assertThat(setAfterRemovingFive, contains(6));
    }

    @Test
    public void testTryGetValueTest() {
        this.tryGetValueTestHelper(ImmutableHashSet.<String>empty().withComparator(ordinalIgnoreCaseComparator()));
    }

//        [Fact]
//        public void DebuggerAttributesValid()
//        {
//            DebuggerAttributes.ValidateDebuggerDisplayReferences(ImmutableHashSet.Create<string>());
//            DebuggerAttributes.ValidateDebuggerTypeProxyProperties(ImmutableHashSet.Create<int>(1, 2, 3));
//        }

    @Override
    protected <T> ImmutableSet<T> empty() {
        return ImmutableHashSet.empty();
    }

    protected <T> ImmutableHashSet<T> emptyTyped() {
        return ImmutableHashSet.empty();
    }

    @Override
    protected <T> Set<T> emptyMutable() {
        return new HashSet<T>();
    }

    @Override
    <T> BinaryTree<?> getRootNode(ImmutableSet<T> set) {
        return ((ImmutableHashSet<T>)set).getRoot();
    }

    /**
     * Tests various aspects of an unordered set.
     *
     * @param <T> The type of element stored in the set.
     * @param emptySet The empty set.
     * @param value A value that could be placed in the set.
     * @param comparator The comparator used to obtain the empty set, if any.
     */
    private <T> void emptyTestHelper(ImmutableSet<T> emptySet, T value, EqualityComparator<? super T> comparator) {
        assertNotNull(emptySet);

        this.emptyTestHelper(emptySet);
        assertSame(emptySet, Immutables.toImmutableHashSet(emptySet, comparator));
        assertSame(comparator != null ? comparator : EqualityComparators.defaultComparator(), ((HashKeyCollection<T>)(ImmutableHashSet<T>)emptySet).getKeyComparator());

        if (comparator == null) {
            assertSame(emptySet, ImmutableHashSet.empty());
        }

        ImmutableSet<T> reemptied = emptySet.add(value).clear();
        assertSame("Getting the empty set from a non-empty instance did not preserve the comparer.", reemptied, Immutables.toImmutableHashSet(reemptied, comparator));
    }
}
