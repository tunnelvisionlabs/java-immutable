// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;

public abstract class ImmutableDictionaryTestBase extends ImmutablesTestBase {
    @Test
    public void emptyTest() {
        emptyTestHelper(empty(), 5);
    }

    @Test
    public void enumeratorTest() {
        iteratorTestHelper(this.<Integer, GenericParameterHelper>empty());
    }

    @Test
    public void containsTest() {
        containsTestHelper(empty(), 5, "foo");
    }

    @Test
    public void removeTest() {
        removeTestHelper(empty(), 5);
    }

    @Test
    public void keysTest() {
        keysTestHelper(empty(), 5);
    }

    @Test
    public void valuesTest() {
        valuesTestHelper(empty(), 5);
    }

    @Test
    public void addAscendingTest() {
        addAscendingTestHelper(this.<Integer, GenericParameterHelper>empty());
    }

    @Test
    public void addRangeTest() {
        ImmutableMap<Integer, GenericParameterHelper> map = empty();

        ArrayList<KeyValuePair<Integer, GenericParameterHelper>> range = new ArrayList<KeyValuePair<Integer, GenericParameterHelper>>();
        for (int n : new Range(1, 100)) {
            range.add(KeyValuePair.create(n, new GenericParameterHelper()));
        }

        map = map.addAll(range);
        assertEqualSequences(ImmutableTreeList.createAll(map.keySet()).sort(), new Range(1, 100));

        verifyAvlTreeState(map);

        Assert.assertEquals(100, map.size());

        // Test optimization for empty map.
        ImmutableMap<Integer, GenericParameterHelper> map2 = empty();
        ImmutableMap<Integer, GenericParameterHelper> jointMap = map2.addAll(map.entrySet());
        Assert.assertSame(map, jointMap);

        jointMap = map2.addAll(TestExtensionMethods.toReadOnlyMap(map).entrySet());
        Assert.assertSame(map, jointMap);

        jointMap = map2.addAll(TestExtensionMethods.toBuilder(map).entrySet());
        Assert.assertSame(map, jointMap);
    }

    @Test
    public void addDescendingTest() {
        addDescendingTestHelper(this.<Integer, GenericParameterHelper>empty());
    }

    @Test
    public void addRemoveRandomDataTest() {
        addRemoveRandomDataTestHelper(this.<Double, GenericParameterHelper>empty());
    }

    @Test
    public void addRemoveEnumerableTest() {
        addRemoveEnumerableTestHelper(this.<Integer, Integer>empty());
    }

    @Test
    public void setItemTest() {
        ImmutableMap<String, Integer> map = this.<String, Integer>empty()
            .put("Microsoft", 100)
            .put("Corporation", 50);
        Assert.assertEquals(2, map.size());

        map = map.put("Microsoft", 200);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(200, (int)map.get("Microsoft"));

        // Set it to the same thing again and make sure it's all good.
        ImmutableMap<String, Integer> sameMap = map.put("Microsoft", 200);
        Assert.assertSame(map, sameMap);
    }

    @Test
    public void setItemsTest() {
        HashMap<String, Integer> template = new HashMap<String, Integer>();
        template.put("Microsoft", 100);
        template.put("Corporation", 50);
        ImmutableMap<String, Integer> map = this.<String, Integer>empty().putAll(template.entrySet());
        Assert.assertEquals(2, map.size());

        HashMap<String, Integer> changes = new HashMap<String, Integer>();
        changes.put("Microsoft", 150);
        changes.put("Dogs", 90);
        map = map.putAll(changes.entrySet());
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(150, (int)map.get("Microsoft"));
        Assert.assertEquals(50, (int)map.get("Corporation"));
        Assert.assertEquals(90, (int)map.get("Dogs"));

        map = map.putAll(
            Arrays.asList(
                KeyValuePair.create("Microsoft", 80),
                KeyValuePair.create("Microsoft", 70)
            ));
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(70, (int)map.get("Microsoft"));
        Assert.assertEquals(50, (int)map.get("Corporation"));
        Assert.assertEquals(90, (int)map.get("Dogs"));

        map = this.<String, Integer>empty().putAll(Arrays.asList(
            KeyValuePair.create("a", 1), KeyValuePair.create("b", 2), KeyValuePair.create("a", 3)
        ));
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(3, (int)map.get("a"));
        Assert.assertEquals(2, (int)map.get("b"));
    }

    @Test
    public void containsKeyTest() {
        containsKeyTestHelper(this.<Integer, GenericParameterHelper>empty(), 1, new GenericParameterHelper());
    }

    @Test
    public void indexGetNonExistingKeyReturnsNull() {
        Assert.assertNull(this.<Integer, Integer>empty().get(3));
    }

    @Test
    public void indexGetTest() {
        ImmutableMap<Integer, Integer> map = this.<Integer, Integer>empty().add(3, 5);
        Assert.assertEquals(5, (int)map.get(3));
    }

    //@Test
    //public void mapRemoveThrowsTest() {
    //    Map<Integer, Integer> map = TestExtensionMethods.toReadOnlyMap(this.<Integer, Integer>empty().add(5, 3));
    //    thrown.expect(UnsupportedOperationException.class);
    //    map.remove(5);
    //}

    //@Test
    //public void mapAddThrowsTest() {
    //    Map<Integer, Integer> map = TestExtensionMethods.toReadOnlyMap(this.<Integer, Integer>empty());
    //    thrown.expect(UnsupportedOperationException.class);
    //    map.put(5, 3);
    //}

    //@Test
    //public void mapPutThrowsTest() {
    //    Map<Integer, Integer> map = TestExtensionMethods.toReadOnlyMap(this.<Integer, Integer>empty());
    //    thrown.expect(UnsupportedOperationException.class);
    //    map.put(3, 5);
    //}

//        @Test
//        public void EqualsTest()
//        {
//            Assert.False(Empty<int, int>().Equals(null));
//            Assert.False(Empty<int, int>().Equals("hi"));
//            Assert.True(Empty<int, int>().Equals(Empty<int, int>()));
//            Assert.False(Empty<int, int>().Add(3, 2).Equals(Empty<int, int>().Add(3, 2)));
//            Assert.False(Empty<int, int>().Add(3, 2).Equals(Empty<int, int>().Add(3, 1)));
//            Assert.False(Empty<int, int>().Add(5, 1).Equals(Empty<int, int>().Add(3, 1)));
//            Assert.False(Empty<int, int>().Add(3, 1).Add(5, 1).Equals(Empty<int, int>().Add(3, 1)));
//            Assert.False(Empty<int, int>().Add(3, 1).Equals(Empty<int, int>().Add(3, 1).Add(5, 1)));
//
//            Assert.True(Empty<int, int>().ToReadOnlyDictionary().Equals(Empty<int, int>()));
//            Assert.True(Empty<int, int>().Equals(Empty<int, int>().ToReadOnlyDictionary()));
//            Assert.True(Empty<int, int>().ToReadOnlyDictionary().Equals(Empty<int, int>().ToReadOnlyDictionary()));
//            Assert.False(Empty<int, int>().Add(3, 1).ToReadOnlyDictionary().Equals(Empty<int, int>()));
//            Assert.False(Empty<int, int>().Equals(Empty<int, int>().Add(3, 1).ToReadOnlyDictionary()));
//            Assert.False(Empty<int, int>().ToReadOnlyDictionary().Equals(Empty<int, int>().Add(3, 1).ToReadOnlyDictionary()));
//        }

    /**
     * Verifies that the {@link #hashCode()} method returns a standard one.
     */
    @Test
    public void hashCodeTest() {
        ImmutableMap<String, Integer> map = empty();
        Assert.assertEquals(EqualityComparators.defaultComparator().hashCode(map), map.hashCode());
    }

//        @Test
//        public void ICollectionOfKVMembers()
//        {
//            var dictionary = (ICollection<KeyValuePair<string, int>>)Empty<string, int>();
//            Assert.Throws<NotSupportedException>(() => dictionary.Add(new KeyValuePair<string, int>()));
//            Assert.Throws<NotSupportedException>(() => dictionary.Remove(new KeyValuePair<string, int>()));
//            Assert.Throws<NotSupportedException>(() => dictionary.Clear());
//            Assert.True(dictionary.IsReadOnly);
//        }
//
//        @Test
//        public void ICollectionMembers()
//        {
//            ((ICollection)Empty<string, int>()).CopyTo(new object[0], 0);
//
//            var dictionary = (ICollection)Empty<string, int>().Add("a", 1);
//            Assert.True(dictionary.IsSynchronized);
//            Assert.NotNull(dictionary.SyncRoot);
//            Assert.Same(dictionary.SyncRoot, dictionary.SyncRoot);
//
//            var array = new object[2];
//            dictionary.CopyTo(array, 1);
//            Assert.Null(array[0]);
//            Assert.Equal(new DictionaryEntry("a", 1), (DictionaryEntry)array[1]);
//        }
//
//        @Test
//        public void IDictionaryOfKVMembers()
//        {
//            var dictionary = (IDictionary<string, int>)Empty<string, int>().Add("c", 3);
//            Assert.Throws<NotSupportedException>(() => dictionary.Add("a", 1));
//            Assert.Throws<NotSupportedException>(() => dictionary.Remove("a"));
//            Assert.Throws<NotSupportedException>(() => dictionary["a"] = 2);
//            Assert.Throws<KeyNotFoundException>(() => dictionary["a"]);
//            Assert.Equal(3, dictionary["c"]);
//        }
//
//        @Test
//        public void IDictionaryMembers()
//        {
//            var dictionary = (IDictionary)Empty<string, int>().Add("c", 3);
//            Assert.Throws<NotSupportedException>(() => dictionary.Add("a", 1));
//            Assert.Throws<NotSupportedException>(() => dictionary.Remove("a"));
//            Assert.Throws<NotSupportedException>(() => dictionary["a"] = 2);
//            Assert.Throws<NotSupportedException>(() => dictionary.Clear());
//            Assert.False(dictionary.Contains("a"));
//            Assert.True(dictionary.Contains("c"));
//            Assert.Throws<KeyNotFoundException>(() => dictionary["a"]);
//            Assert.Equal(3, dictionary["c"]);
//            Assert.True(dictionary.IsFixedSize);
//            Assert.True(dictionary.IsReadOnly);
//            Assert.Equal(new[] { "c" }, dictionary.Keys.Cast<string>().ToArray());
//            Assert.Equal(new[] { 3 }, dictionary.Values.Cast<int>().ToArray());
//        }
//
//        @Test
//        public void IDictionaryEnumerator()
//        {
//            var dictionary = (IDictionary)Empty<string, int>().Add("a", 1);
//            var enumerator = dictionary.GetEnumerator();
//            Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Key);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Value);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Entry);
//            Assert.True(enumerator.MoveNext());
//            Assert.Equal(enumerator.Entry, enumerator.Current);
//            Assert.Equal(enumerator.Key, enumerator.Entry.Key);
//            Assert.Equal(enumerator.Value, enumerator.Entry.Value);
//            Assert.Equal("a", enumerator.Key);
//            Assert.Equal(1, enumerator.Value);
//            Assert.False(enumerator.MoveNext());
//            Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Key);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Value);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Entry);
//            Assert.False(enumerator.MoveNext());
//
//            enumerator.Reset();
//            Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Key);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Value);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Entry);
//            Assert.True(enumerator.MoveNext());
//            Assert.Equal(enumerator.Key, ((DictionaryEntry)enumerator.Current).Key);
//            Assert.Equal(enumerator.Value, ((DictionaryEntry)enumerator.Current).Value);
//            Assert.Equal("a", enumerator.Key);
//            Assert.Equal(1, enumerator.Value);
//            Assert.False(enumerator.MoveNext());
//            Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Key);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Value);
//            Assert.Throws<InvalidOperationException>(() => enumerator.Entry);
//            Assert.False(enumerator.MoveNext());
//        }

    @Test
    public void tryGetKey() {
        ImmutableMap<String, Integer> dictionary = this.<Integer>empty(ordinalIgnoreCaseComparator())
            .add("a", 1);
        Assert.assertTrue(dictionary.containsKey("a"));
        String actualKey = dictionary.getKey("a");
        Assert.assertEquals("a", actualKey);

        Assert.assertTrue(dictionary.containsKey("A"));
        actualKey = dictionary.getKey("A");
        Assert.assertEquals("a", actualKey);

        Assert.assertFalse(dictionary.containsKey("b"));
        actualKey = dictionary.getKey("b");
        Assert.assertNull("b", actualKey);
    }

    protected <K, V> void emptyTestHelper(ImmutableMap<K, V> empty, K someKey) {
        Assert.assertSame(empty, empty.clear());
        Assert.assertEquals(0, empty.size());
        Assert.assertEquals(0, Iterables.size(empty.entrySet()));
        Assert.assertEquals(0, Iterables.size(empty.keySet()));
        Assert.assertEquals(0, Iterables.size(empty.values()));
        Assert.assertSame(EqualityComparators.defaultComparator(), getValueComparator(empty));
        Assert.assertFalse(empty.containsKey(someKey));
        Assert.assertFalse(empty.contains(new KeyValuePair<K, V>(someKey, null)));
        //Assert.assertEquals(null, empty.getValueOrDefault(someKey));

        V value = empty.get(someKey);
        Assert.assertFalse(empty.containsKey(someKey));
        Assert.assertEquals(null, value);
    }

    private <K extends Comparable<? super K>, V> ImmutableMap<K, V> addTestHelper(ImmutableMap<K, V> map, K key, V value) {
        assert map != null;
        assert key != null;

        ImmutableMap<K, V> addedMap = map.add(key, value);
        Assert.assertNotSame(map, addedMap);
        ////Assert.assertEquals(map.Count + 1, addedMap.Count);
        Assert.assertFalse(map.containsKey(key));
        Assert.assertTrue(addedMap.containsKey(key));
        Assert.assertSame(value, addedMap.get(key));

        this.verifyAvlTreeState(addedMap);
        return addedMap;
    }

    protected void addAscendingTestHelper(ImmutableMap<Integer, GenericParameterHelper> map) {
        assert map != null;

        for (int i = 0; i < 10; i++) {
            map = addTestHelper(map, i, new GenericParameterHelper(i));
        }

        Assert.assertEquals(10, map.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(map.containsKey(i));
        }
    }

    protected void addDescendingTestHelper(ImmutableMap<Integer, GenericParameterHelper> map) {
        for (int i = 10; i > 0; i--) {
            map = addTestHelper(map, i, new GenericParameterHelper(i));
        }

        Assert.assertEquals(10, map.size());
        for (int i = 10; i > 0; i--) {
            Assert.assertTrue(map.containsKey(i));
        }
    }

    protected void addRemoveRandomDataTestHelper(ImmutableMap<Double, GenericParameterHelper> map) {
        Requires.notNull(map, "map");

        Double[] inputs = generateDummyFillData();
        for (int i = 0; i < inputs.length; i++) {
            map = addTestHelper(map, inputs[i], new GenericParameterHelper());
        }

        Assert.assertEquals(inputs.length, map.size());
        for (int i = 0; i < inputs.length; i++) {
            Assert.assertTrue(map.containsKey(inputs[i]));
        }

        for (int i = 0; i < inputs.length; i++) {
            map = map.remove(inputs[i]);
            verifyAvlTreeState(map);
        }

        Assert.assertEquals(0, map.size());
    }

    protected void addRemoveEnumerableTestHelper(ImmutableMap<Integer, Integer> empty) {
        Requires.notNull(empty, "empty");

        Assert.assertSame(empty, empty.removeAll(Collections.<Integer>emptyList()));
        Assert.assertSame(empty, empty.addAll(Collections.<KeyValuePair<Integer, Integer>>emptyList()));
        ArrayList<KeyValuePair<Integer, Integer>> list = new ArrayList<KeyValuePair<Integer, Integer>>();
        list.add(KeyValuePair.create(3, 5));
        list.add(KeyValuePair.create(8, 10));
        ImmutableMap<Integer, Integer> nonEmpty = empty.addAll(list);
        verifyAvlTreeState(nonEmpty);
        ImmutableMap<Integer, Integer> halfRemoved = nonEmpty.removeAll(new Range(1, 5));
        Assert.assertEquals(1, halfRemoved.size());
        Assert.assertTrue(halfRemoved.containsKey(8));
        verifyAvlTreeState(halfRemoved);
    }

    protected <K, V> void addExistingKeySameValueTestHelper(ImmutableMap<K, V> map, K key, V value1, V value2) {
        Requires.notNull(map, "map");
        Requires.notNull(key, "key");
        Requires.argument(getValueComparator(map).equals(value1, value2));

        map = map.add(key, value1);
        Assert.assertSame(map, map.add(key, value2));
        Assert.assertSame(map, map.addAll(Collections.singletonList(KeyValuePair.create(key, value2))));
    }

    /**
     * Verifies that adding a key-value pair where the key already is in the map but with a different value throws.
     *
     * <p>Adding a key-value pair to a map where that key already exists, but with a different value, cannot fit the
     * semantic of "adding", either by just returning or mutating the value on the existing key. Throwing is the only
     * reasonable response.</p>
     *
     * @param <K> The type of key in the map.
     * @param <V> The type of value in the map.
     * @param map The map to manipulate.
     * @param key The key to add.
     * @param value1 The first value to add.
     * @param value2 The second value to add.
     */
    protected <K, V> void addExistingKeyDifferentValueTestHelper(ImmutableMap<K, V> map, K key, V value1, V value2) {
        Requires.notNull(map, "map");
        Requires.notNull(key, "key");
        Requires.argument(!getValueComparator(map).equals(value1, value2));

        ImmutableMap<K, V> map1 = map.add(key, value1);
        ImmutableMap<K, V> map2 = map.add(key, value2);

        try {
            map1.add(key, value2);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }

        try {
            map2.add(key, value1);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    protected <K, V> void containsKeyTestHelper(ImmutableMap<K, V> map, K key, V value) {
        Assert.assertFalse(map.containsKey(key));
        Assert.assertTrue(map.add(key, value).containsKey(key));
    }

    protected <K, V> void containsTestHelper(ImmutableMap<K, V> map, K key, V value) {
        Assert.assertFalse(map.contains(KeyValuePair.create(key, value)));
        //Assert.assertFalse(map.contains(key, value));
        Assert.assertTrue(map.add(key, value).contains(KeyValuePair.create(key, value)));
        //Assert.assertTrue(map.add(key, value).contains(key, value));
    }

    protected <K, V> void removeTestHelper(ImmutableMap<K, V> map, K key) {
        // no-op remove
        Assert.assertSame(map, map.remove(key));
        Assert.assertSame(map, map.removeAll(Collections.<K>emptyList()));

        // substantial remove
        ImmutableMap<K, V> addedMap = map.add(key, null);
        ImmutableMap<K, V> removedMap = addedMap.remove(key);
        Assert.assertNotSame(addedMap, removedMap);
        Assert.assertFalse(removedMap.containsKey(key));
    }

    protected <K, V> void keysTestHelper(ImmutableMap<K, V> map, K key) {
        Assert.assertEquals(0, Iterables.size(map.keySet()));
        Assert.assertEquals(0, TestExtensionMethods.toReadOnlyMap(map).keySet().size());

        ImmutableMap<K, V> nonEmpty = map.add(key, null);
        Assert.assertEquals(1, Iterables.size(nonEmpty.keySet()));
        Assert.assertEquals(1, TestExtensionMethods.toReadOnlyMap(nonEmpty).keySet().size());
        keysOrValuesTestHelper(TestExtensionMethods.toReadOnlyMap(nonEmpty).keySet(), key);
    }

    protected <K, V> void valuesTestHelper(ImmutableMap<K, V> map, K key) {
        Assert.assertEquals(0, Iterables.size(map.values()));
        Assert.assertEquals(0, TestExtensionMethods.toReadOnlyMap(map).values().size());

        ImmutableMap<K, V> nonEmpty = map.add(key, null);
        Assert.assertEquals(1, Iterables.size(nonEmpty.values()));
        Assert.assertEquals(1, TestExtensionMethods.toReadOnlyMap(nonEmpty).values().size());
        keysOrValuesTestHelper(TestExtensionMethods.toReadOnlyMap(nonEmpty).values(), null);
    }

    protected void iteratorTestHelper(ImmutableMap<Integer, GenericParameterHelper> map) {
        for (int i = 0; i < 10; i++) {
            map = addTestHelper(map, i, new GenericParameterHelper(i));
        }

        int j = 0;
        for (Map.Entry<Integer, GenericParameterHelper> pair : map.entrySet()) {
            Assert.assertEquals(j, (int)pair.getKey());
            Assert.assertEquals(j, pair.getValue().getData());
            j++;
        }

        ArrayList<Map.Entry<Integer, GenericParameterHelper>> list = new ArrayList<Map.Entry<Integer, GenericParameterHelper>>();
        for (Map.Entry<Integer, GenericParameterHelper> pair : map.entrySet()) {
            list.add(pair);
        }

        //Assert.assertEquals(list, ImmutableSetTest.toListNonGeneric(map));

        // Apply some less common uses to the enumerator to test its metal.
        Iterator<Map.Entry<Integer, GenericParameterHelper>> iterator = map.entrySet().iterator();

        manuallyIterateTest(list, iterator);
        iterator = map.entrySet().iterator();
        manuallyIterateTest(list, iterator);

        // this time only partially enumerate
        iterator = map.entrySet().iterator();
        iterator.next();
        iterator = map.entrySet().iterator();
        manuallyIterateTest(list, iterator);

        Iterator<Map.Entry<Integer, GenericParameterHelper>> manualIter = map.entrySet().iterator();
        while (manualIter.hasNext()) {
            manualIter.next();
        }
        Assert.assertFalse(manualIter.hasNext());

        try {
            manualIter.next();
            Assert.fail();
        } catch (NoSuchElementException ignored) {
        }
    }

    protected abstract <K, V> ImmutableMap<K, V> empty();

    protected abstract <V> ImmutableMap<String, V> empty(StringComparator comparator);

    protected abstract <K, V> EqualityComparator<? super V> getValueComparator(ImmutableMap<K, V> map);

    protected abstract <K, V> BinaryTree<?> getRootNode(ImmutableMap<K, V> map);

    private static <T> void keysOrValuesTestHelper(Collection<T> collection, T containedValue) {
        Requires.notNull(collection, "collection");

        Assert.assertTrue(collection.contains(containedValue));

        try {
            collection.add(null);
            Assert.fail();
        } catch (UnsupportedOperationException ignored) {
        }

        try {
            collection.clear();
            Assert.fail();
        } catch (UnsupportedOperationException ignored) {
        }

        //Assert.Throws<ArgumentNullException>(() => nonGeneric.CopyTo(null, 0));
        //var array = new T[collection.Count + 1];
        //nonGeneric.CopyTo(array, 1);
        //Assert.Equal(default(T), array[0]);
        //Assert.Equal(array.Skip(1), nonGeneric.Cast<T>().ToArray());
    }

    private <K, V> void verifyAvlTreeState(ImmutableMap<K, V> map) {
        BinaryTree<?> rootNode = this.getRootNode(map);
        TestExtensionMethods.verifyBalanced(rootNode);
        TestExtensionMethods.verifyHeightIsWithinTolerance(rootNode, map.size());
    }
}
