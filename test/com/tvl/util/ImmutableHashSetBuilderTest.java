// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ImmutableHashSetBuilderTest extends ImmutablesTestBase {
    @Test
    public void testCreateBuilder() {
        ImmutableHashSet.Builder<String> builder = ImmutableHashSet.createBuilder();
        assertSame(EqualityComparators.defaultComparator(), builder.getKeyComparator());

        builder = ImmutableHashSet.createBuilder(ordinalIgnoreCaseComparator());
        assertSame(ordinalIgnoreCaseComparator(), builder.getKeyComparator());
    }

    @Test
    public void testToBuilder() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.<Integer>empty().toBuilder();
        assertTrue(builder.add(3));
        assertTrue(builder.add(5));
        assertFalse(builder.add(5));
        assertEquals(2, builder.size());
        assertTrue(builder.contains(3));
        assertTrue(builder.contains(5));
        assertFalse(builder.contains(7));

        ImmutableHashSet<Integer> set = builder.toImmutable();
        assertEquals(builder.size(), set.size());
        assertTrue(builder.add(8));
        assertEquals(3, builder.size());
        assertEquals(2, set.size());
        assertTrue(builder.contains(8));
        assertFalse(set.contains(8));
    }

    @Test
    public void testBuilderFromSet() {
        ImmutableHashSet<Integer> set = ImmutableHashSet.<Integer>empty().add(1);
        ImmutableHashSet.Builder<Integer> builder = set.toBuilder();
        assertTrue(builder.contains(1));
        assertTrue(builder.add(3));
        assertTrue(builder.add(5));
        assertFalse(builder.add(5));
        assertEquals(3, builder.size());
        assertTrue(builder.contains(3));
        assertTrue(builder.contains(5));
        assertFalse(builder.contains(7));

        ImmutableHashSet<Integer> set2 = builder.toImmutable();
        assertEquals(builder.size(), set2.size());
        assertTrue(set2.contains(1));
        assertTrue(builder.add(8));
        assertEquals(4, builder.size());
        assertEquals(3, set2.size());
        assertTrue(builder.contains(8));

        assertFalse(set.contains(8));
        assertFalse(set2.contains(8));
    }

//        @Test
//        public void testIterateBuilderWhileMutating()
//        {
//            ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.<Integer>empty().union(new Range(1, 10)).toBuilder();
//            CollectionAssertAreEquivalent(new Range(1, 10), builder);
//
//            var enumerator = builder.GetEnumerator();
//            Assert.True(enumerator.MoveNext());
//            builder.Add(11);
//
//            // Verify that a new enumerator will succeed.
//            CollectionAssertAreEquivalent(Enumerable.Range(1, 11).ToArray(), builder.ToArray());
//
//            // Try enumerating further with the previous enumerable now that we've changed the collection.
//            Assert.Throws<InvalidOperationException>(() => enumerator.MoveNext());
//            enumerator.Reset();
//            enumerator.MoveNext(); // resetting should fix the problem.
//
//            // Verify that by obtaining a new enumerator, we can enumerate all the contents.
//            CollectionAssertAreEquivalent(Enumerable.Range(1, 11).ToArray(), builder.ToArray());
//        }

    @Test
    public void testBuilderReusesUnchangedImmutableInstances() {
        ImmutableHashSet<Integer> collection = ImmutableHashSet.<Integer>empty().add(1);
        ImmutableHashSet.Builder<Integer> builder = collection.toBuilder();
        assertSame("Set is reused if no changes are made", collection, builder.toImmutable());
        builder.add(2);

        ImmutableHashSet<Integer> newImmutable = builder.toImmutable();
        assertNotSame("first toImmutable with changes should be a new instance", collection, newImmutable);
        assertSame("second toImmutable without changes should be the same instance", newImmutable, builder.toImmutable());
    }

    @Test
    public void testIteratorTest() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.create(1).toBuilder();
        manuallyIterateTest(Collections.singletonList(1), builder.iterator());
    }

    @Test
    public void testClear() {
        ImmutableHashSet<Integer> set = ImmutableHashSet.create(1);
        ImmutableHashSet.Builder<Integer> builder = set.toBuilder();
        builder.clear();
        assertEquals(0, builder.size());
    }

    @Test
    public void testKeyComparator() {
        ImmutableHashSet.Builder<String> builder = ImmutableHashSet.create("a", "B").toBuilder();
        assertSame(EqualityComparators.defaultComparator(), builder.getKeyComparator());
        assertTrue(builder.contains("a"));
        assertFalse(builder.contains("A"));

        builder.setKeyComparator(ordinalIgnoreCaseComparator());
        assertSame(ordinalIgnoreCaseComparator(), builder.getKeyComparator());
        assertEquals(2, builder.size());
        assertTrue(builder.contains("a"));
        assertTrue(builder.contains("A"));

        ImmutableHashSet<String> set = builder.toImmutable();
        assertSame(ordinalIgnoreCaseComparator(), set.getKeyComparator());
    }

    @Test
    public void testKeyComparatorCollisions() {
        ImmutableHashSet.Builder<String> builder = ImmutableHashSet.create("a", "A").toBuilder();
        builder.setKeyComparator(ordinalIgnoreCaseComparator());
        assertEquals(1, builder.size());
        assertTrue(builder.contains("a"));

        ImmutableHashSet<String> set = builder.toImmutable();
        assertSame(ordinalIgnoreCaseComparator(), set.getKeyComparator());
        assertEquals(1, set.size());
        assertTrue(set.contains("a"));
    }

    @Test
    public void testKeyComparatorEmptyCollection() {
        ImmutableHashSet.Builder<String> builder = ImmutableHashSet.<String>create().toBuilder();
        assertSame(EqualityComparators.<String>defaultComparator(), builder.getKeyComparator());
        builder.setKeyComparator(ordinalIgnoreCaseComparator());
        assertSame(ordinalIgnoreCaseComparator(), builder.getKeyComparator());
        ImmutableHashSet<String> set = builder.toImmutable();
        assertSame(ordinalIgnoreCaseComparator(), set.getKeyComparator());
    }

    @Test
    public void testUnionWith() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.create(1, 2, 3).toBuilder();
        builder.unionWith(Arrays.asList(2, 3, 4));
        assertThat(builder, contains(1, 2, 3, 4));
    }

    @Test
    public void testUnionWithNull() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.create(1, 2, 3).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.unionWith(null);
    }

    @Test
    public void testExceptWith() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.create(1, 2, 3).toBuilder();
        builder.exceptWith(Arrays.asList(2, 3, 4));
        assertThat(builder, contains(1));
    }

    @Test
    public void testExceptWithNull() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.create(1, 2, 3).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.exceptWith(null);
    }

    @Test
    public void testSymmetricExceptWith() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.create(1, 2, 3).toBuilder();
        builder.symmetricExceptWith(Arrays.asList(2, 3, 4));
        assertThat(builder, contains(1, 4));
    }

    @Test
    public void testSymmetricExceptWithNull() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.create(1, 2, 3).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.symmetricExceptWith(null);
    }

    @Test
    public void testIntersectWith() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.create(1, 2, 3).toBuilder();
        builder.intersectWith(Arrays.asList(2, 3, 4));
        assertThat(builder, contains(2, 3));
    }

    @Test
    public void testIntersectWithNull() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.create(1, 2, 3).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.intersectWith(null);
    }

    @Test
    public void testIsProperSubsetOf() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();
        assertFalse(builder.isProperSubsetOf(new Range(1, 3)));
        assertTrue(builder.isProperSubsetOf(new Range(1, 5)));
    }

    @Test
    public void testIsProperSubsetOfNull() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.isProperSubsetOf(null);
    }

    @Test
    public void testIsProperSupersetOf() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();
        assertFalse(builder.isProperSupersetOf(new Range(1, 3)));
        assertTrue(builder.isProperSupersetOf(new Range(1, 2)));
    }

    @Test
    public void testIsProperSupersetOfNull() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.isProperSupersetOf(null);
    }

    @Test
    public void testIsSubsetOf() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();
        assertFalse(builder.isSubsetOf(new Range(1, 2)));
        assertTrue(builder.isSubsetOf(new Range(1, 3)));
        assertTrue(builder.isSubsetOf(new Range(1, 5)));
    }

    @Test
    public void testIsSubsetOfNull() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.isSubsetOf(null);
    }

    @Test
    public void testIsSupersetOf() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();
        assertFalse(builder.isSupersetOf(new Range(1, 4)));
        assertTrue(builder.isSupersetOf(new Range(1, 3)));
        assertTrue(builder.isSupersetOf(new Range(1, 2)));
    }

    @Test
    public void testIsSupersetOfNull() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.isSupersetOf(null);
    }

    @Test
    public void testOverlaps() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();
        assertTrue(builder.overlaps(new Range(3, 2)));
        assertFalse(builder.overlaps(new Range(4, 3)));
    }

    @Test
    public void testOverlapsNull() {
        ImmutableHashSet.Builder<Integer> builder = ImmutableHashSet.createAll(new Range(1, 3)).toBuilder();

        thrown.expect(NullPointerException.class);
        builder.overlaps(null);
    }

    @Test
    public void testRemove() {
        ImmutableHashSet.Builder<String> builder = ImmutableHashSet.create("a").toBuilder();
        assertFalse(builder.remove("b"));
        assertTrue(builder.remove("a"));
    }

    @Test
    public void testRemoveNull() {
        ImmutableHashSet.Builder<String> builder = ImmutableHashSet.create("a").toBuilder();

        thrown.expect(NullPointerException.class);
        builder.remove(null);
    }

    @Test
    public void testSetEquals() {
        ImmutableHashSet.Builder<String> builder = ImmutableHashSet.create("a").toBuilder();
        assertFalse(builder.setEquals(Collections.singletonList("b")));
        assertTrue(builder.setEquals(Collections.singletonList("a")));
        assertTrue(builder.setEquals(builder));
    }

    @Test
    public void testSetEqualsNull() {
        ImmutableHashSet.Builder<String> builder = ImmutableHashSet.create("a").toBuilder();

        thrown.expect(NullPointerException.class);
        builder.setEquals(null);
    }

//        [Fact]
//        public void ICollectionOfTMethods()
//        {
//            ICollection<string> builder = ImmutableHashSet.Create("a").ToBuilder();
//            builder.Add("b");
//            Assert.True(builder.Contains("b"));
//
//            var array = new string[3];
//            builder.CopyTo(array, 1);
//            Assert.Null(array[0]);
//            CollectionAssertAreEquivalent(new[] { null, "a", "b" }, array);
//
//            Assert.False(builder.IsReadOnly);
//
//            CollectionAssertAreEquivalent(new[] { "a", "b" }, builder.ToArray()); // tests enumerator
//        }
//
//        [Fact]
//        public void DebuggerAttributesValid()
//        {
//            DebuggerAttributes.ValidateDebuggerDisplayReferences(ImmutableHashSet.CreateBuilder<int>());
//        }
}
