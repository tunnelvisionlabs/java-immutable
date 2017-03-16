// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImmutableTreeSetBuilderTest extends ImmutablesTestBase {

    @Test
    public void createBuilder() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.createBuilder();
        assertNotNull(builder);

        builder = ImmutableTreeSet.createBuilder(ordinalIgnoreCaseComparator());
        assertSame(ordinalIgnoreCaseComparator(), builder.getKeyComparator());
    }

    @Test
    public void toBuilder() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.<Integer>empty().toBuilder();
        assertTrue(builder.add(3));
        assertTrue(builder.add(5));
        assertFalse(builder.add(5));
        assertEquals(2, builder.size());
        assertTrue(builder.contains(3));
        assertTrue(builder.contains(5));
        assertFalse(builder.contains(7));

        ImmutableTreeSet<Integer> set = builder.toImmutable();
        assertEquals(builder.size(), set.size());
        assertTrue(builder.add(8));
        assertEquals(3, builder.size());
        assertEquals(2, set.size());
        assertTrue(builder.contains(8));
        assertFalse(set.contains(8));
    }

    @Test
    public void builderFromSet() {
        ImmutableTreeSet<Integer> set = ImmutableTreeSet.<Integer>empty().add(1);
        ImmutableTreeSet.Builder<Integer> builder = set.toBuilder();
        assertTrue(builder.contains(1));
        assertTrue(builder.add(3));
        assertTrue(builder.add(5));
        assertFalse(builder.add(5));
        assertEquals(3, builder.size());
        assertTrue(builder.contains(3));
        assertTrue(builder.contains(5));
        assertFalse(builder.contains(7));

        ImmutableTreeSet<Integer> set2 = builder.toImmutable();
        assertEquals(builder.size(), set2.size());
        assertTrue(set2.contains(1));
        assertTrue(builder.add(8));
        assertEquals(4, builder.size());
        assertEquals(3, set2.size());
        assertTrue(builder.contains(8));

        assertFalse(set.contains(8));
        assertFalse(set2.contains(8));
    }

    @Test
    public void iterateBuilderWhileMutating() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.<Integer>empty().union(new Range(1, 10)).toBuilder();
        assertThat(builder, contains(Iterables.toArray(new Range(1, 10), Integer.class)));

        Iterator<Integer> iterator = builder.iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        builder.add(11);

        // Verify that a new enumerator will succeed.
        assertThat(builder, contains(Iterables.toArray(new Range(1, 11), Integer.class)));

        // Try enumerating further with the previous enumerable now that we've changed the collection.
        try {
            iterator.next();
            fail("Expected an exception");
        } catch (ConcurrentModificationException ex) {
        }

        //iterator.reset();
        //iterator.next(); // resetting should fix the problem.

        // Verify that by obtaining a new enumerator, we can enumerate all the contents.
        assertThat(builder, contains(Iterables.toArray(new Range(1, 11), Integer.class)));
    }

    @Test
    public void builderReusesUnchangedImmutableInstances() {
        ImmutableTreeSet<Integer> collection = ImmutableTreeSet.<Integer>empty().add(1);
        ImmutableTreeSet.Builder<Integer> builder = collection.toBuilder();
        assertSame(collection, builder.toImmutable()); // no changes at all.
        builder.add(2);

        ImmutableTreeSet<Integer> newImmutable = builder.toImmutable();
        assertNotSame(collection, newImmutable); // first ToImmutable with changes should be a new instance.
        assertSame(newImmutable, builder.toImmutable()); // second ToImmutable without changes should be the same instance.
    }

    @Test
    public void iteratorTest() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.create("a", "B").withComparator(ordinalComparator()).toBuilder();
        Iterable<String> iterable = builder;
        Iterator<String> iterator = iterable.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void maxMin() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 2, 3).toBuilder();
        assertEquals(1, (int)builder.getMin());
        assertEquals(3, (int)builder.getMax());
    }

    @Test
    public void clear() {
        ImmutableTreeSet<Integer> set = ImmutableTreeSet.<Integer>empty().add(1);
        ImmutableTreeSet.Builder<Integer> builder = set.toBuilder();
        builder.clear();
        assertEquals(0, builder.size());
    }

    @Test
    public void keyComparator() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.create("a", "B").toBuilder();
        assertSame(Comparators.anyComparator(), builder.getKeyComparator());
        assertTrue(builder.contains("a"));
        assertFalse(builder.contains("A"));

        builder.setKeyComparator(ordinalIgnoreCaseComparator());
        assertSame(ordinalIgnoreCaseComparator(), builder.getKeyComparator());
        assertEquals(2, builder.size());
        assertTrue(builder.contains("a"));
        assertTrue(builder.contains("A"));

        ImmutableTreeSet<String> set = builder.toImmutable();
        assertSame(ordinalIgnoreCaseComparator(), set.getKeyComparator());
    }

    @Test
    public void keyComparatorCollisions() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.create("a", "A").toBuilder();
        builder.setKeyComparator(ordinalIgnoreCaseComparator());
        assertEquals(1, builder.size());
        assertTrue(builder.contains("a"));

        ImmutableTreeSet<String> set = builder.toImmutable();
        assertSame(ordinalIgnoreCaseComparator(), set.getKeyComparator());
        assertEquals(1, set.size());
        assertTrue(set.contains("a"));
    }

    @Test
    public void keyComparatorEmptyCollection() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.<String>create().toBuilder();
        assertSame(Comparators.anyComparator(), builder.getKeyComparator());
        builder.setKeyComparator(ordinalIgnoreCaseComparator());
        assertSame(ordinalIgnoreCaseComparator(), builder.getKeyComparator());
        ImmutableTreeSet<String> set = builder.toImmutable();
        assertSame(ordinalIgnoreCaseComparator(), set.getKeyComparator());
    }

    @Test
    public void unionWith() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 2, 3).toBuilder();

        builder.unionWith(Arrays.asList(2, 3, 4));
        assertThat(builder, contains(1, 2, 3, 4));
    }

    @Test
    public void unionWith_Null() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 2, 3).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.unionWith(null);
    }

    @Test
    public void exceptWith() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 2, 3).toBuilder();

        builder.exceptWith(Arrays.asList(2, 3, 4));
        assertThat(builder, contains(1));
    }

    @Test
    public void exceptWith_Null() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 2, 3).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.exceptWith(null);
    }

    @Test
    public void symmetricExceptWith() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 2, 3).toBuilder();

        builder.symmetricExceptWith(Arrays.asList(2, 3, 4));
        assertThat(builder, contains(1, 4));
    }

    @Test
    public void symmetricExceptWith_Null() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 2, 3).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.symmetricExceptWith(null);
    }

    @Test
    public void intersectWith() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 2, 3).toBuilder();

        builder.intersectWith(Arrays.asList(2, 3, 4));
        assertThat(builder, contains(2, 3));
    }

    @Test
    public void intersectWith_Null() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 2, 3).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.intersectWith(null);
    }

    @Test
    public void isProperSubsetOf() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        assertFalse(builder.isProperSubsetOf(new Range(1, 3)));
        assertTrue(builder.isProperSubsetOf(new Range(1, 5)));
    }

    @Test
    public void isProperSubsetOf_Null() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.isProperSubsetOf(null);
    }

    @Test
    public void isProperSupersetOf() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        assertFalse(builder.isProperSupersetOf(new Range(1, 3)));
        assertTrue(builder.isProperSupersetOf(new Range(1, 2)));
    }

    @Test
    public void isProperSupersetOf_Null() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.isProperSupersetOf(null);
    }

    @Test
    public void isSubsetOf() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        assertFalse(builder.isSubsetOf(new Range(1, 2)));
        assertTrue(builder.isSubsetOf(new Range(1, 3)));
        assertTrue(builder.isSubsetOf(new Range(1, 5)));
    }

    @Test
    public void isSubsetOf_Null() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.isSubsetOf(null);
    }

    @Test
    public void isSupersetOf() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        assertFalse(builder.isSupersetOf(new Range(1, 4)));
        assertTrue(builder.isSupersetOf(new Range(1, 3)));
        assertTrue(builder.isSupersetOf(new Range(1, 2)));
    }

    @Test
    public void isSupersetOf_Null() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.isSupersetOf(null);
    }

    @Test
    public void overlaps() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        assertTrue(builder.overlaps(new Range(3, 2)));
        assertFalse(builder.overlaps(new Range(4, 3)));
    }

    @Test
    public void overlaps_Null() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.overlaps(null);
    }

    @Test
    public void remove() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.create("a").toBuilder();

        assertFalse(builder.remove("b"));
        assertTrue(builder.remove("a"));
    }

    @Test
    public void remove_Null() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.create("a").toBuilder();

        thrown.expect(NullPointerException.class);
        builder.remove(null);
    }

    @Test
    public void reverse() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.create("a", "b").toBuilder();
        assertThat(builder.reverse(), contains("b", "a"));
    }

    @Test
    public void setEquals() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.create("a").toBuilder();

        assertFalse(builder.setEquals(Arrays.asList("b")));
        assertTrue(builder.setEquals(Arrays.asList("a")));
        assertTrue(builder.setEquals(builder));
    }

    @Test
    public void setEquals_Null() {
        ImmutableTreeSet.Builder<String> builder = ImmutableTreeSet.create("a").toBuilder();

        thrown.expect(NullPointerException.class);
        builder.setEquals(null);
    }

//        [Fact]
//        public void ICollectionOfTMethods()
//        {
//            ICollection<string> builder = ImmutableSortedSet.Create("a").ToBuilder();
//            builder.Add("b");
//            Assert.True(builder.Contains("b"));
//
//            var array = new string[3];
//            builder.CopyTo(array, 1);
//            Assert.Equal(new[] { null, "a", "b" }, array);
//
//            Assert.False(builder.IsReadOnly);
//
//            Assert.Equal(new[] { "a", "b" }, builder.ToArray()); // tests enumerator
//        }
//
//        [Fact]
//        public void ICollectionMethods()
//        {
//            ICollection builder = ImmutableSortedSet.Create("a").ToBuilder();
//
//            var array = new string[builder.Count + 1];
//            builder.CopyTo(array, 1);
//            Assert.Equal(new[] { null, "a" }, array);
//
//            Assert.False(builder.IsSynchronized);
//            Assert.NotNull(builder.SyncRoot);
//            Assert.Same(builder.SyncRoot, builder.SyncRoot);
//        }

    @Test
    public void indexer() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 3, 2).toBuilder();
        assertEquals(1, (int)builder.get(0));
        assertEquals(2, (int)builder.get(1));
        assertEquals(3, (int)builder.get(2));
    }

    @Test
    public void indexerNegative() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 3, 2).toBuilder();

        thrown.expect(IndexOutOfBoundsException.class);
        builder.get(-1);
    }

    @Test
    public void indexerOver() {
        ImmutableTreeSet.Builder<Integer> builder = ImmutableTreeSet.create(1, 3, 2).toBuilder();

        thrown.expect(IndexOutOfBoundsException.class);
        builder.get(3);
    }

//        [Fact]
//        public void DebuggerAttributesValid()
//        {
//            DebuggerAttributes.ValidateDebuggerDisplayReferences(ImmutableSortedSet.CreateBuilder<string>());
//            DebuggerAttributes.ValidateDebuggerTypeProxyProperties(ImmutableSortedSet.CreateBuilder<int>());
//        }
}
