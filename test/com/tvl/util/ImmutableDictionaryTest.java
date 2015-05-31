// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Collections;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableDictionaryTest extends ImmutableDictionaryTestBase {
    @Test
    public void addExistingKeySameValueTest() {
        addExistingKeySameValueTestHelper(emptyHashMap(ordinalComparator(), ordinalComparator()), "Company", "Microsoft", "Microsoft");
        addExistingKeySameValueTestHelper(emptyHashMap(ordinalComparator(), ordinalIgnoreCaseComparator()), "Company", "Microsoft", "MICROSOFT");
    }

    @Test
    public void addExistingKeyDifferentValueTest() {
        addExistingKeyDifferentValueTestHelper(emptyHashMap(ordinalComparator(), ordinalComparator()), "Company", "Microsoft", "MICROSOFT");
    }

//[Fact]
//public void UnorderedChangeTest()
//{
//    var map = Empty<string, string>(StringComparer.Ordinal)
//        .Add("Johnny", "Appleseed")
//        .Add("JOHNNY", "Appleseed");
//    Assert.Equal(2, map.Count);
//    Assert.True(map.ContainsKey("Johnny"));
//    Assert.False(map.ContainsKey("johnny"));
//    var newMap = map.WithComparers(StringComparer.OrdinalIgnoreCase);
//    Assert.Equal(1, newMap.Count);
//    Assert.True(newMap.ContainsKey("Johnny"));
//    Assert.True(newMap.ContainsKey("johnny")); // because it's case insensitive
//}

//[Fact]
//public void ToSortedTest()
//{
//    var map = Empty<string, string>(StringComparer.Ordinal)
//        .Add("Johnny", "Appleseed")
//        .Add("JOHNNY", "Appleseed");
//    var sortedMap = map.ToImmutableSortedDictionary(StringComparer.Ordinal);
//    Assert.Equal(sortedMap.Count, map.Count);
//    CollectionAssertAreEquivalent<KeyValuePair<string, string>>(sortedMap.ToList(), map.ToList());
//}

    @Test
    public void setItemUpdateEqualKeyTest() {
        ImmutableHashMap<String, Integer> map = ImmutableDictionaryTest.<String, Integer>emptyHashMap().withComparators(ordinalIgnoreCaseComparator())
            .put("A", 1);
        map = map.put("a", 2);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("a", map.keySet().iterator().next());
    }

    /**
     * Verifies that the specified value comparator is applied when checking for equality.
     */
    @Test
    public void setItemUpdateEqualKeyWithValueEqualityByComparator() {
        ImmutableHashMap<String, CaseInsensitiveString> map = ImmutableDictionaryTest.<String, CaseInsensitiveString>emptyHashMap().withComparators(ordinalIgnoreCaseComparator(), new MyStringOrdinalComparer());
        String key = "key";
        String value1 = "Hello";
        String value2 = "hello";
        map = map.put(key, new CaseInsensitiveString(value1));
        map = map.put(key, new CaseInsensitiveString(value2));
        Assert.assertEquals(value2, map.get(key).getValue());

        Assert.assertSame(map, map.put(key, new CaseInsensitiveString(value2)));
    }

    @Test
    @Override
    public void emptyTest() {
        super.emptyTest();
        emptyTestHelperHash(empty(), 5);
    }

    @Test
    public void containsValueTest() {
        containsValueTestHelper(ImmutableHashMap.empty(), 1, new GenericParameterHelper());
    }

    @Test
    public void iteratorWithHashCollisionsTest() {
        ImmutableMap<Integer, GenericParameterHelper> emptyMap = ImmutableDictionaryTest.emptyHashMap(new BadHasher<Integer>());
        iteratorTestHelper(emptyMap);
    }

    @Test
    public void create() {
        Iterable<Map.Entry<String, String>> pairs = Collections.singletonMap("a", "b").entrySet();
        StringComparator keyComparator = ordinalIgnoreCaseComparator();
        StringComparator valueComparator = ordinalComparator();

        ImmutableHashMap<String, String> map = ImmutableHashMap.create();
        Assert.assertEquals(0, map.size());
        Assert.assertSame(EqualityComparators.defaultComparator(), map.getKeyComparator());
        Assert.assertSame(EqualityComparators.defaultComparator(), map.getValueComparator());

        map = ImmutableHashMap.create(keyComparator);
        Assert.assertEquals(0, map.size());
        Assert.assertSame(keyComparator, map.getKeyComparator());
        Assert.assertSame(EqualityComparators.defaultComparator(), map.getValueComparator());

        map = ImmutableHashMap.create(keyComparator, valueComparator);
        Assert.assertEquals(0, map.size());
        Assert.assertSame(keyComparator, map.getKeyComparator());
        Assert.assertSame(valueComparator, map.getValueComparator());

        map = ImmutableHashMap.createAll(pairs);
        Assert.assertEquals(1, map.size());
        Assert.assertSame(EqualityComparators.defaultComparator(), map.getKeyComparator());
        Assert.assertSame(EqualityComparators.defaultComparator(), map.getValueComparator());

        map = ImmutableHashMap.createAll(keyComparator, pairs);
        Assert.assertEquals(1, map.size());
        Assert.assertSame(keyComparator, map.getKeyComparator());
        Assert.assertSame(EqualityComparators.defaultComparator(), map.getValueComparator());

        map = ImmutableHashMap.createAll(keyComparator, valueComparator, pairs);
        Assert.assertEquals(1, map.size());
        Assert.assertSame(keyComparator, map.getKeyComparator());
        Assert.assertSame(valueComparator, map.getValueComparator());
    }

//[Fact]
//public void ToImmutableDictionary()
//{
//    IEnumerable<KeyValuePair<string, string>> pairs = new Dictionary<string, string> { { "a", "B" } };
//    var keyComparer = StringComparer.OrdinalIgnoreCase;
//    var valueComparer = StringComparer.CurrentCulture;

//    ImmutableDictionary<string, string> dictionary = pairs.ToImmutableDictionary();
//    Assert.Equal(1, dictionary.Count);
//    Assert.Same(EqualityComparer<string>.Default, dictionary.KeyComparer);
//    Assert.Same(EqualityComparer<string>.Default, dictionary.ValueComparer);

//    dictionary = pairs.ToImmutableDictionary(keyComparer);
//    Assert.Equal(1, dictionary.Count);
//    Assert.Same(keyComparer, dictionary.KeyComparer);
//    Assert.Same(EqualityComparer<string>.Default, dictionary.ValueComparer);

//    dictionary = pairs.ToImmutableDictionary(keyComparer, valueComparer);
//    Assert.Equal(1, dictionary.Count);
//    Assert.Same(keyComparer, dictionary.KeyComparer);
//    Assert.Same(valueComparer, dictionary.ValueComparer);

//    dictionary = pairs.ToImmutableDictionary(p => p.Key.ToUpperInvariant(), p => p.Value.ToLowerInvariant());
//    Assert.Equal(1, dictionary.Count);
//    Assert.Equal("A", dictionary.Keys.Single());
//    Assert.Equal("b", dictionary.Values.Single());
//    Assert.Same(EqualityComparer<string>.Default, dictionary.KeyComparer);
//    Assert.Same(EqualityComparer<string>.Default, dictionary.ValueComparer);

//    dictionary = pairs.ToImmutableDictionary(p => p.Key.ToUpperInvariant(), p => p.Value.ToLowerInvariant(), keyComparer);
//    Assert.Equal(1, dictionary.Count);
//    Assert.Equal("A", dictionary.Keys.Single());
//    Assert.Equal("b", dictionary.Values.Single());
//    Assert.Same(keyComparer, dictionary.KeyComparer);
//    Assert.Same(EqualityComparer<string>.Default, dictionary.ValueComparer);

//    dictionary = pairs.ToImmutableDictionary(p => p.Key.ToUpperInvariant(), p => p.Value.ToLowerInvariant(), keyComparer, valueComparer);
//    Assert.Equal(1, dictionary.Count);
//    Assert.Equal("A", dictionary.Keys.Single());
//    Assert.Equal("b", dictionary.Values.Single());
//    Assert.Same(keyComparer, dictionary.KeyComparer);
//    Assert.Same(valueComparer, dictionary.ValueComparer);

//    var list = new int[] { 1, 2 };
//    var intDictionary = list.ToImmutableDictionary(n => (double)n);
//    Assert.Equal(1, intDictionary[1.0]);
//    Assert.Equal(2, intDictionary[2.0]);
//    Assert.Equal(2, intDictionary.Count);

//    var stringIntDictionary = list.ToImmutableDictionary(n => n.ToString(), StringComparer.OrdinalIgnoreCase);
//    Assert.Same(StringComparer.OrdinalIgnoreCase, stringIntDictionary.KeyComparer);
//    Assert.Equal(1, stringIntDictionary["1"]);
//    Assert.Equal(2, stringIntDictionary["2"]);
//    Assert.Equal(2, intDictionary.Count);

//    Assert.Throws<ArgumentNullException>(() => list.ToImmutableDictionary<int, int>(null));
//    Assert.Throws<ArgumentNullException>(() => list.ToImmutableDictionary<int, int, int>(null, v => v));
//    Assert.Throws<ArgumentNullException>(() => list.ToImmutableDictionary<int, int, int>(k => k, null));

//    list.ToDictionary(k => k, v => v, null); // verifies BCL behavior is to not throw.
//    list.ToImmutableDictionary(k => k, v => v, null, null);
//}

//[Fact]
//public void ToImmutableDictionaryOptimized()
//{
//    var dictionary = ImmutableDictionary.Create<string, string>();
//    var result = dictionary.ToImmutableDictionary();
//    Assert.Same(dictionary, result);

//    var cultureComparer = StringComparer.CurrentCulture;
//    result = dictionary.WithComparers(cultureComparer, StringComparer.OrdinalIgnoreCase);
//    Assert.Same(cultureComparer, result.KeyComparer);
//    Assert.Same(StringComparer.OrdinalIgnoreCase, result.ValueComparer);
//}

    @Test
    public void withComparators() {
        ImmutableHashMap<String, String> map = ImmutableHashMap.<String, String>create().add("a", "1").add("B", "1");
        Assert.assertSame(EqualityComparators.defaultComparator(), map.getKeyComparator());
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertFalse(map.containsKey("A"));

        map = map.withComparators(ordinalIgnoreCaseComparator());
        Assert.assertSame(ordinalIgnoreCaseComparator(), map.getKeyComparator());
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertTrue(map.containsKey("A"));
        Assert.assertTrue(map.containsKey("b"));

        map = map.withComparators(ordinalIgnoreCaseComparator(), ordinalComparator());
        Assert.assertSame(ordinalIgnoreCaseComparator(), map.getKeyComparator());
        Assert.assertSame(ordinalComparator(), map.getValueComparator());
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertTrue(map.containsKey("A"));
        Assert.assertTrue(map.containsKey("b"));
    }

    @Test
    public void withComparatorsCollisions() {
        // First check where collisions have matching values.
        ImmutableHashMap<String, String> map = ImmutableHashMap.<String, String>create()
            .add("a", "1").add("A", "1");
        map = map.withComparators(ordinalIgnoreCaseComparator());
        Assert.assertSame(ordinalIgnoreCaseComparator(), map.getKeyComparator());
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertEquals("1", map.get("a"));

        // Now check where collisions have conflicting values.
        map = ImmutableHashMap.<String, String>create()
            .add("a", "1").add("A", "2").add("b", "3");

        try {
            map.withComparators(ordinalIgnoreCaseComparator());
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }

        // Force all values to be considered equal.
        map = map.withComparators(ordinalIgnoreCaseComparator(), EverythingEqual.instance());
        Assert.assertSame(ordinalIgnoreCaseComparator(), map.getKeyComparator());
        Assert.assertSame(EverythingEqual.instance(), map.getValueComparator());
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("a"));
        Assert.assertTrue(map.containsKey("b"));
    }

    @Test
    public void collisionExceptionMessageContainsKey() {
        ImmutableHashMap<String, String> map = ImmutableHashMap.<String, String>create()
            .add("firstKey", "1").add("secondKey", "2");

        try {
            map.add("firstKey", "3");
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.containsString("firstKey"));
        }
    }

    @Test
    public void withComparatorsEmptyCollection() {
        ImmutableHashMap<String, String> map = ImmutableHashMap.create();
        Assert.assertSame(EqualityComparators.defaultComparator(), map.getKeyComparator());
        map = map.withComparators(ordinalIgnoreCaseComparator());
        Assert.assertSame(ordinalIgnoreCaseComparator(), map.getKeyComparator());
    }

//[Fact]
//public void GetValueOrDefaultOfIImmutableDictionary()
//{
//    IImmutableDictionary<string, int> empty = ImmutableDictionary.Create<string, int>();
//    IImmutableDictionary<string, int> populated = ImmutableDictionary.Create<string, int>().Add("a", 5);
//    Assert.Equal(0, empty.GetValueOrDefault("a"));
//    Assert.Equal(1, empty.GetValueOrDefault("a", 1));
//    Assert.Equal(5, populated.GetValueOrDefault("a"));
//    Assert.Equal(5, populated.GetValueOrDefault("a", 1));
//}

    //@Test
    //public void getValueOrDefaultOfConcreteType() {
    //    ImmutableHashMap<String, Integer> empty = ImmutableHashMap.create();
    //    ImmutableHashMap<String, Integer> populated = ImmutableHashMap.<String, Integer>create().add("a", 5);
    //    Assert.assertEquals(0, empty.getValueOrDefault("a"));
    //    Assert.assertEquals(1, empty.getValueOrDefault("a", 1));
    //    Assert.assertEquals(5, populated.getValueOrDefault("a"));
    //    Assert.assertEquals(5, populated.getValueOrDefault("a", 1));
    //}

//[Fact]
//public void EnumeratorRecyclingMisuse()
//{
//    var collection = ImmutableDictionary.Create<int, int>().Add(5, 3);
//    var enumerator = collection.GetEnumerator();
//    var enumeratorCopy = enumerator;
//    Assert.True(enumerator.MoveNext());
//    Assert.False(enumerator.MoveNext());
//    enumerator.Dispose();
//    Assert.Throws<ObjectDisposedException>(() => enumerator.MoveNext());
//    Assert.Throws<ObjectDisposedException>(() => enumerator.Reset());
//    Assert.Throws<ObjectDisposedException>(() => enumerator.Current);
//    Assert.Throws<ObjectDisposedException>(() => enumeratorCopy.MoveNext());
//    Assert.Throws<ObjectDisposedException>(() => enumeratorCopy.Reset());
//    Assert.Throws<ObjectDisposedException>(() => enumeratorCopy.Current);
//    enumerator.Dispose(); // double-disposal should not throw
//    enumeratorCopy.Dispose();

//    // We expect that acquiring a new enumerator will use the same underlying Stack<T> object,
//    // but that it will not throw exceptions for the new enumerator.
//    enumerator = collection.GetEnumerator();
//    Assert.True(enumerator.MoveNext());
//    Assert.False(enumerator.MoveNext());
//    Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//    enumerator.Dispose();
//}

    //[Fact]
    //public void DebuggerAttributesValid()
    //{
    //    DebuggerAttributes.ValidateDebuggerDisplayReferences(ImmutableDictionary.Create<int, int>());
    //    DebuggerAttributes.ValidateDebuggerTypeProxyProperties(ImmutableDictionary.Create<string, int>());

    //    object rootNode = DebuggerAttributes.GetFieldValue(ImmutableDictionary.Create<string, string>(), "_root");
    //    DebuggerAttributes.ValidateDebuggerDisplayReferences(rootNode);
    //}

    @Override
    protected <K, V> ImmutableMap<K, V> empty() {
        return ImmutableDictionaryTest.emptyHashMap();
    }

    @Override
    protected <V> ImmutableMap<String, V> empty(StringComparator comparator) {
        return ImmutableHashMap.create(comparator);
    }

    @Override
    protected <K, V> EqualityComparator<? super V> getValueComparator(ImmutableMap<K, V> map) {
        return ((ImmutableHashMap<K, V>)map).getValueComparator();
    }

    @Override
    protected <K, V> BinaryTree<?> getRootNode(ImmutableMap<K, V> map) {
        return ((ImmutableHashMap<K, V>)map).getRoot();
    }

    protected <K, V> void containsValueTestHelper(ImmutableHashMap<K, V> map, K key, V value) {
        Assert.assertFalse(map.containsValue(value));
        Assert.assertTrue(map.add(key, value).containsValue(value));
    }

    private static <K, V> ImmutableHashMap<K, V> emptyHashMap() {
        return ImmutableDictionaryTest.emptyHashMap(null, null);
    }

    private static <K, V> ImmutableHashMap<K, V> emptyHashMap(EqualityComparator<K> keyComparator) {
        return ImmutableDictionaryTest.emptyHashMap(keyComparator, null);
    }

    private static <K, V> ImmutableHashMap<K, V> emptyHashMap(EqualityComparator<K> keyComparator, EqualityComparator<V> valueComparator) {
        return ImmutableHashMap.<K, V>empty().withComparators(keyComparator, valueComparator);
    }

    private <K, V> void emptyTestHelperHash(ImmutableMap<K, V> empty, K someKey) {
        Assert.assertSame(EqualityComparators.defaultComparator(), ((HashKeyCollection<?>)empty).getKeyComparator());
    }

    /**
     * An ordinal comparator for case-insensitive strings.
     */
    private static class MyStringOrdinalComparer implements EqualityComparator<CaseInsensitiveString> {
        @Override
        public boolean equals(CaseInsensitiveString x, CaseInsensitiveString y) {
            return ordinalComparator().equals(x.getValue(), y.getValue());
        }

        @Override
        public int hashCode(CaseInsensitiveString obj) {
            return ordinalComparator().hashCode(obj.getValue());
        }
    }

    /**
     * A {@link String} wrapper that considers equality based on case-insensitivity.
     */
    private static class CaseInsensitiveString {
        private final String value;

        public CaseInsensitiveString(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return ordinalIgnoreCaseComparator().hashCode(getValue());
        }

        @Override
        public boolean equals(Object o) {
            return ordinalIgnoreCaseComparator().equals(getValue(), ((CaseInsensitiveString)o).getValue());
        }
    }
}
