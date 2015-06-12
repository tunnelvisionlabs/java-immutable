// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import com.tvl.util.function.BiFunction;
import com.tvl.util.function.Function;
import com.tvl.util.function.Predicate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableArrayTest extends SimpleElementImmutablesTestBase {
    private static final ImmutableArrayList<Integer> EMPTY = ImmutableArrayList.create();
    private static final ImmutableArrayList<Integer> ONE_ELEMENT = ImmutableArrayList.create(1);
    private static final ImmutableArrayList<Integer> MANY_ELEMENTS = ImmutableArrayList.create(1, 2, 3);
    private static final ImmutableArrayList<GenericParameterHelper> ONE_ELEMENT_REF_TYPE = ImmutableArrayList.create(new GenericParameterHelper(1));
    private static final ImmutableArrayList<String> TWO_ELEMENT_REF_TYPE_WITH_NULL = ImmutableArrayList.create("1", null);

    @Test
    public void createEmpty() {
        Assert.assertSame(ImmutableArrayList.<Integer>create(), ImmutableArrayList.<Integer>empty());
    }

    @Test
    public void createFromIterable() {
        try {
            ImmutableArrayList.createAll((Iterable<Integer>)null);
            Assert.fail();
        } catch (NullPointerException ignored) {
        }

        Iterable<Integer> source = Arrays.asList(1, 2, 3);
        ImmutableArrayList<Integer> array = ImmutableArrayList.createAll(source);
        Assert.assertEquals(3, array.size());
    }

    @Test
    public void createFromEmptyEnumerableReturnsSingleton() {
        Iterable<Integer> emptySource = Collections.emptyList();
        ImmutableArrayList<Integer> immutable = ImmutableArrayList.createAll(emptySource);

        Assert.assertSame(EMPTY, immutable);
    }

    @Test
    public void createRangeFromImmutableArrayWithSelector() {
        ImmutableArrayList<Integer> array = ImmutableArrayList.create(4, 5, 6, 7);

        ImmutableArrayList<Double> copy1 = ImmutableArrayList.createAll(array, new Function<Integer, Double>() {
            @Override
            public Double apply(Integer integer) {
                return integer + 0.5;
            }
        });
        assertEqualSequences(Arrays.asList(4.5, 5.5, 6.5, 7.5), copy1);

        ImmutableArrayList<Integer> copy2 = ImmutableArrayList.createAll(array, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return integer + 1;
            }
        });
        assertEqualSequences(Arrays.asList(5, 6, 7, 8), copy2);

        assertEqualSequences(Collections.emptyList(), ImmutableArrayList.createAll(EMPTY, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return integer;
            }
        }));

        thrown.expect(NullPointerException.class);
        ImmutableArrayList.createAll(array, (Function<Integer, Integer>)null);
    }

    @Test
    public void createRangeFromImmutableArrayWithSelectorAndArgument() {
        ImmutableArrayList<Integer> array = ImmutableArrayList.create(4, 5, 6, 7);

        ImmutableArrayList<Double> copy1 = ImmutableArrayList.createAll(array, new BiFunction<Integer, Double, Double>() {
            @Override
            public Double apply(Integer integer, Double aDouble) {
                return integer + aDouble;
            }
        }, 0.5);
        assertEqualSequences(Arrays.asList(4.5, 5.5, 6.5, 7.5), copy1);

        ImmutableArrayList<Integer> copy2 = ImmutableArrayList.createAll(array, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        }, 1);
        assertEqualSequences(Arrays.asList(5, 6, 7, 8), copy2);

        ImmutableArrayList<Object> copy3 = ImmutableArrayList.createAll(array, new BiFunction<Integer, Object, Object>() {
            @Override
            public Object apply(Integer integer, Object o) {
                return integer;
            }
        }, null);
        assertEqualSequences(Arrays.asList(4, 5, 6, 7), copy3);

        assertEqualSequences(Collections.emptyList(), ImmutableArrayList.createAll(EMPTY, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        }, 0));

        thrown.expect(NullPointerException.class);
        ImmutableArrayList.createAll(array, (BiFunction<Integer, Integer, Integer>)null, 0);
    }

//[Fact]
//public void CreateRangeSliceFromImmutableArrayWithSelector()
//{
//    var array = ImmutableArray.Create(4, 5, 6, 7);

//    var copy1 = ImmutableArray.CreateRange(array, 0, 0, i => i + 0.5);
//    Assert.Equal(new double[] { }, copy1);

//    var copy2 = ImmutableArray.CreateRange(array, 0, 0, i => i);
//    Assert.Equal(new int[] { }, copy2);

//    var copy3 = ImmutableArray.CreateRange(array, 0, 1, i => i * 2);
//    Assert.Equal(new int[] { 8 }, copy3);

//    var copy4 = ImmutableArray.CreateRange(array, 0, 2, i => i + 1);
//    Assert.Equal(new int[] { 5, 6 }, copy4);

//    var copy5 = ImmutableArray.CreateRange(array, 0, 4, i => i);
//    Assert.Equal(new int[] { 4, 5, 6, 7 }, copy5);

//    var copy6 = ImmutableArray.CreateRange(array, 3, 1, i => i);
//    Assert.Equal(new int[] { 7 }, copy6);

//    var copy7 = ImmutableArray.CreateRange(array, 3, 0, i => i);
//    Assert.Equal(new int[] { }, copy7);

//    var copy8 = ImmutableArray.CreateRange(array, 4, 0, i => i);
//    Assert.Equal(new int[] { }, copy8);

//    Assert.Throws<ArgumentNullException>(() => ImmutableArray.CreateRange(array, 0, 0, (Func<int, int>)null));
//    Assert.Throws<ArgumentNullException>(() => ImmutableArray.CreateRange(s_empty, 0, 0, (Func<int, int>)null));

//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, -1, 1, (Func<int, int>)null));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, -1, 1, i => i));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, 0, 5, i => i));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, 4, 1, i => i));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, 3, 2, i => i));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, 1, -1, i => i));
//}

//[Fact]
//public void CreateRangeSliceFromImmutableArrayWithSelectorAndArgument()
//{
//    var array = ImmutableArray.Create(4, 5, 6, 7);

//    var copy1 = ImmutableArray.CreateRange(array, 0, 0, (i, j) => i + j, 0.5);
//    Assert.Equal(new double[] { }, copy1);

//    var copy2 = ImmutableArray.CreateRange(array, 0, 0, (i, j) => i + j, 0);
//    Assert.Equal(new int[] { }, copy2);

//    var copy3 = ImmutableArray.CreateRange(array, 0, 1, (i, j) => i * j, 2);
//    Assert.Equal(new int[] { 8 }, copy3);

//    var copy4 = ImmutableArray.CreateRange(array, 0, 2, (i, j) => i + j, 1);
//    Assert.Equal(new int[] { 5, 6 }, copy4);

//    var copy5 = ImmutableArray.CreateRange(array, 0, 4, (i, j) => i + j, 0);
//    Assert.Equal(new int[] { 4, 5, 6, 7 }, copy5);

//    var copy6 = ImmutableArray.CreateRange(array, 3, 1, (i, j) => i + j, 0);
//    Assert.Equal(new int[] { 7 }, copy6);

//    var copy7 = ImmutableArray.CreateRange(array, 3, 0, (i, j) => i + j, 0);
//    Assert.Equal(new int[] { }, copy7);

//    var copy8 = ImmutableArray.CreateRange(array, 4, 0, (i, j) => i + j, 0);
//    Assert.Equal(new int[] { }, copy8);

//    var copy9 = ImmutableArray.CreateRange(array, 0, 1, (int i, object j) => i, null);
//    Assert.Equal(new int[] { 4 }, copy9);

//    Assert.Equal(new int[] { }, ImmutableArray.CreateRange(s_empty, 0, 0, (i, j) => i + j, 0));

//    Assert.Throws<ArgumentNullException>(() => ImmutableArray.CreateRange(array, 0, 0, (Func<int, int, int>)null, 0));
//    Assert.Throws<ArgumentNullException>(() => ImmutableArray.CreateRange(s_empty, 0, 0, (Func<int, int, int>)null, 0));

//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(s_empty, -1, 1, (Func<int, int, int>)null, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, -1, 1, (i, j) => i + j, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, 0, 5, (i, j) => i + j, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, 4, 1, (i, j) => i + j, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, 3, 2, (i, j) => i + j, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.CreateRange(array, 1, -1, (i, j) => i + j, 0));
//}

//[Fact]
//public void CreateFromSliceOfImmutableArray()
//{
//    var array = ImmutableArray.Create(4, 5, 6, 7);
//    Assert.Equal(new[] { 4, 5 }, ImmutableArray.Create(array, 0, 2));
//    Assert.Equal(new[] { 5, 6 }, ImmutableArray.Create(array, 1, 2));
//    Assert.Equal(new[] { 6, 7 }, ImmutableArray.Create(array, 2, 2));
//    Assert.Equal(new[] { 7 }, ImmutableArray.Create(array, 3, 1));
//    Assert.Equal(new int[0], ImmutableArray.Create(array, 4, 0));

//    Assert.Equal(new int[] { }, ImmutableArray.Create(s_empty, 0, 0));

//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(s_empty, 0, 1));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, -1, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, 0, -1));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, 0, array.Length + 1));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, 1, array.Length));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, array.Length + 1, 0));
//}

//[Fact]
//public void CreateFromSliceOfImmutableArrayOptimizations()
//{
//    var array = ImmutableArray.Create(4, 5, 6, 7);
//    var slice = ImmutableArray.Create(array, 0, array.Length);
//    Assert.Equal(array, slice); // array instance actually shared between the two
//}

//[Fact]
//public void CreateFromSliceOfImmutableArrayEmptyReturnsSingleton()
//{
//    var array = ImmutableArray.Create(4, 5, 6, 7);
//    var slice = ImmutableArray.Create(array, 1, 0);
//    Assert.Equal(s_empty, slice);
//}

//[Fact]
//public void CreateFromSliceOfArray()
//{
//    var array = new int[] { 4, 5, 6, 7 };
//    Assert.Equal(new[] { 4, 5 }, ImmutableArray.Create(array, 0, 2));
//    Assert.Equal(new[] { 5, 6 }, ImmutableArray.Create(array, 1, 2));
//    Assert.Equal(new[] { 6, 7 }, ImmutableArray.Create(array, 2, 2));
//    Assert.Equal(new[] { 7 }, ImmutableArray.Create(array, 3, 1));
//    Assert.Equal(new int[0], ImmutableArray.Create(array, 4, 0));

//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, -1, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, 0, -1));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, 0, array.Length + 1));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, 1, array.Length));
//    Assert.Throws<ArgumentOutOfRangeException>(() => ImmutableArray.Create(array, array.Length + 1, 0));
//}

//[Fact]
//public void CreateFromSliceOfArrayEmptyReturnsSingleton()
//{
//    var array = new int[] { 4, 5, 6, 7 };
//    var slice = ImmutableArray.Create(array, 1, 0);
//    Assert.Equal(s_empty, slice);
//    slice = ImmutableArray.Create(array, array.Length, 0);
//    Assert.Equal(s_empty, slice);
//}

    @Test
    public void createFromArray() {
        Integer[] source = {1, 2, 3};
        ImmutableArrayList<Integer> immutable = ImmutableArrayList.create(source);
        assertEqualSequences(Arrays.asList(source), immutable);
    }

    @Test
    public void createFromNullArray() {
        Integer[] nullArray = null;
        ImmutableArrayList<Integer> immutable = ImmutableArrayList.create(nullArray);
        Assert.assertNotNull(immutable);
        Assert.assertEquals(0, immutable.size());
    }

    @Test
    public void covariance() {
        ImmutableArrayList<String> derivedImmutable = ImmutableArrayList.create("a", "b", "c");
        ImmutableArrayList<Object> baseImmutable = derivedImmutable.as(Object.class);
        Assert.assertNotNull(baseImmutable);
        Assert.assertSame(derivedImmutable, baseImmutable);

        // Make sure we can reverse that, as a means to verify the underlying array is the same instance.
        ImmutableArrayList<String> derivedImmutable2 = baseImmutable.as(String.class);
        Assert.assertNotNull(derivedImmutable2);
        Assert.assertSame(derivedImmutable, derivedImmutable2);

        // Try a cast that would fail.
        Assert.assertNull(baseImmutable.as(Integer.class));
    }

    //[Fact]
    //public void DowncastOfDefaultStructs()
    //{
    //    ImmutableArray<string> derivedImmutable = default(ImmutableArray<string>);
    //    ImmutableArray<object> baseImmutable = derivedImmutable.As<object>();
    //    Assert.True(baseImmutable.IsDefault);
    //    Assert.True(derivedImmutable.IsDefault);

    //    // Make sure we can reverse that, as a means to verify the underlying array is the same instance.
    //    ImmutableArray<string> derivedImmutable2 = baseImmutable.As<string>();
    //    Assert.True(derivedImmutable2.IsDefault);
    //    Assert.True(derivedImmutable == derivedImmutable2);
    //}

    /**
     * Verifies that using an ordinary {@code createAll} factory method is smart enough to reuse an immutable array
     * instance when possible.
     */
    @Test
    public void covarianceImplicit() {
        ImmutableArrayList<String> derivedImmutable = ImmutableArrayList.create("a", "b", "c");
        ImmutableArrayList<Object> baseImmutable = ImmutableArrayList.<Object>createAll(derivedImmutable);
        Assert.assertSame(derivedImmutable, baseImmutable);

        // Make sure we can reverse that, as a means to verify the underlying array is the same instance.
        ImmutableArrayList<String> derivedImmutable2 = baseImmutable.as(String.class);
        Assert.assertSame(derivedImmutable, derivedImmutable2);
    }

    @Test
    public void castUpReference() {
        ImmutableArrayList<String> derivedImmutable = ImmutableArrayList.create("a", "b", "c");
        ImmutableArrayList<Object> baseImmutable = ImmutableArrayList.<Object>castUp(derivedImmutable);
        Assert.assertSame(derivedImmutable, baseImmutable);

        // Make sure we can reverse that, as a means to verify the underlying array is the same instance.
        Assert.assertSame(derivedImmutable, baseImmutable.as(String.class));
        Assert.assertSame(derivedImmutable, baseImmutable.castArray(String.class));
    }

    @Test
    public void castUpReferenceDefaultValue() {
        ImmutableArrayList<String> derivedImmutable = null;
        ImmutableArrayList<Object> baseImmutable = ImmutableArrayList.<Object>castUp(derivedImmutable);
        Assert.assertNull(baseImmutable);
        Assert.assertNull(derivedImmutable);
    }

    @Test
    public void castUpRefToInterface() {
        ImmutableArrayList<String> stringArray = ImmutableArrayList.create("a", "b");
        ImmutableArrayList<CharSequence> enumArray = ImmutableArrayList.<CharSequence>castUp(stringArray);
        Assert.assertEquals(2, enumArray.size());
        Assert.assertSame(stringArray, enumArray.castArray(String.class));
        Assert.assertSame(stringArray, enumArray.as(String.class));
    }

    @Test
    public void castUpInterfaceToInterface() {
        ImmutableArrayList<List<?>> genericEnumArray = ImmutableArrayList.<List<?>>create(new ArrayList<Integer>(), new ArrayList<Integer>());
        ImmutableArrayList<Iterable<?>> legacyEnumArray = ImmutableArrayList.<Iterable<?>>castUp(genericEnumArray);
        Assert.assertEquals(2, legacyEnumArray.size());
        Assert.assertSame(genericEnumArray, legacyEnumArray.as(List.class));
        Assert.assertSame(genericEnumArray, legacyEnumArray.castArray(List.class));
    }

    @Test
    public void castUpArrayToObject() {
        ImmutableArrayList<int[]> arrayArray = ImmutableArrayList.create(new int[]{1, 2}, new int[]{3, 4});
        ImmutableArrayList<Object> sysArray = ImmutableArrayList.<Object>castUp(arrayArray);
        Assert.assertEquals(2, sysArray.size());
        Assert.assertSame(arrayArray, sysArray.as(int[].class));
        Assert.assertSame(arrayArray, sysArray.castArray(int[].class));
    }

    //@Test
    //public void castUpDelegateToSystemDelegate()
    //{
    //    var delArray = ImmutableArray.Create<Action>(() => { }, () => { });
    //    var sysDelArray = ImmutableArray<Delegate>.CastUp(delArray);
    //    Assert.Equal(2, sysDelArray.Length);
    //    Assert.Equal(delArray, sysDelArray.As<Action>());
    //    Assert.Equal(delArray, sysDelArray.CastArray<Action>());
    //}

    @Test
    public void castArrayUnrelatedInterface() {
        ImmutableArrayList<String> strArray = ImmutableArrayList.create("cat", "dog");
        ImmutableArrayList<Comparable<String>> compArray = ImmutableArrayList.<Comparable<String>>castUp(strArray);
        ImmutableArrayList<CharSequence> enumArray = compArray.castArray(CharSequence.class);
        Assert.assertEquals(2, enumArray.size());
        Assert.assertSame(strArray, enumArray.as(String.class));
        Assert.assertSame(strArray, enumArray.castArray(String.class));
    }

    @Test
    public void castArrayBadInterface() {
        ImmutableArrayList<Serializable> serializableArray = ImmutableArrayList.<Serializable>create(1, 2);
        thrown.expect(ClassCastException.class);
        serializableArray.castArray(CharSequence.class);
    }

    @Test
    public void castArrayBadRef() {
        ImmutableArrayList<Object> objectArray = ImmutableArrayList.<Object>create("cat", "dog");
        thrown.expect(ClassCastException.class);
        objectArray.castArray(Integer.class);
    }

    @Test
    public void toImmutableArrayList() {
        Iterable<Integer> source = Arrays.asList(1, 2, 3);
        ImmutableArrayList<Integer> immutable = Immutables.toImmutableArrayList(source);
        assertEqualSequences(source, immutable);

        ImmutableArrayList<Integer> immutable2 = Immutables.toImmutableArrayList(immutable);
        Assert.assertSame(immutable, immutable2);
    }

    @Test
    public void size() {
        Assert.assertEquals(0, EMPTY.size());
        Assert.assertEquals(1, ONE_ELEMENT.size());
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(EMPTY.isEmpty());
        Assert.assertFalse(ONE_ELEMENT.isEmpty());
    }

//[Fact]
//public void IndexOfDefault()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.IndexOf(5));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.IndexOf(5, 0));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.IndexOf(5, 0, 0));
//}

//[Fact]
//public void LastIndexOfDefault()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.LastIndexOf(5));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.LastIndexOf(5, 0));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.LastIndexOf(5, 0, 0));
//}

//[Fact]
//public void IndexOf()
//{
//    IndexOfTests.IndexOfTest(
//        seq => ImmutableArray.CreateRange(seq),
//        (b, v) => b.IndexOf(v),
//        (b, v, i) => b.IndexOf(v, i),
//        (b, v, i, c) => b.IndexOf(v, i, c),
//        (b, v, i, c, eq) => b.IndexOf(v, i, c, eq));
//}

//[Fact]
//public void LastIndexOf()
//{
//    IndexOfTests.LastIndexOfTest(
//        seq => ImmutableArray.CreateRange(seq),
//        (b, v) => b.LastIndexOf(v),
//        (b, v, eq) => b.LastIndexOf(v, eq),
//        (b, v, i) => b.LastIndexOf(v, i),
//        (b, v, i, c) => b.LastIndexOf(v, i, c),
//        (b, v, i, c, eq) => b.LastIndexOf(v, i, c, eq));
//}

    @Test
    public void contains() {
        Assert.assertFalse(EMPTY.contains(0));
        Assert.assertTrue(ONE_ELEMENT.contains(1));
        Assert.assertFalse(ONE_ELEMENT.contains(2));
        Assert.assertTrue(MANY_ELEMENTS.contains(3));
        Assert.assertFalse(ONE_ELEMENT_REF_TYPE.contains(null));
        Assert.assertTrue(TWO_ELEMENT_REF_TYPE_WITH_NULL.contains(null));
    }

    //@Test
    //public void containsEqualityComparator() {
    //    ImmutableArrayList<String> array = ImmutableArrayList.create("a", "B");
    //    Assert.assertFalse(array.contains("A", ordinalComparator()));
    //    Assert.assertTrue(array.contains("A", ordinalIgnoreCaseComparator()));
    //    Assert.assertFalse(array.contains("b", ordinalComparator()));
    //    Assert.assertTrue(array.contains("b", ordinalIgnoreCaseComparator()));
    //}

//[Fact]
//public void Enumerator()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.GetEnumerator());

//    ImmutableArray<int>.Enumerator enumerator = default(ImmutableArray<int>.Enumerator);
//    Assert.Throws<NullReferenceException>(() => enumerator.Current);
//    Assert.Throws<NullReferenceException>(() => enumerator.MoveNext());

//    enumerator = s_empty.GetEnumerator();
//    Assert.Throws<IndexOutOfRangeException>(() => enumerator.Current);
//    Assert.False(enumerator.MoveNext());

//    enumerator = s_manyElements.GetEnumerator();
//    Assert.Throws<IndexOutOfRangeException>(() => enumerator.Current);

//    Assert.True(enumerator.MoveNext());
//    Assert.Equal(1, enumerator.Current);
//    Assert.True(enumerator.MoveNext());
//    Assert.Equal(2, enumerator.Current);
//    Assert.True(enumerator.MoveNext());
//    Assert.Equal(3, enumerator.Current);

//    Assert.False(enumerator.MoveNext());
//    Assert.Throws<IndexOutOfRangeException>(() => enumerator.Current);
//}

//[Fact]
//public void ObjectEnumerator()
//{
//    Assert.Throws<InvalidOperationException>(() => ((IEnumerable<int>)s_emptyDefault).GetEnumerator());

//    IEnumerator<int> enumerator = ((IEnumerable<int>)s_empty).GetEnumerator();
//    Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//    Assert.False(enumerator.MoveNext());

//    enumerator = ((IEnumerable<int>)s_manyElements).GetEnumerator();
//    Assert.Throws<InvalidOperationException>(() => enumerator.Current);

//    Assert.True(enumerator.MoveNext());
//    Assert.Equal(1, enumerator.Current);
//    Assert.True(enumerator.MoveNext());
//    Assert.Equal(2, enumerator.Current);
//    Assert.True(enumerator.MoveNext());
//    Assert.Equal(3, enumerator.Current);

//    Assert.False(enumerator.MoveNext());
//    Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//}

    @Test
    public void iteratorWithNullValues() {
        String[] enumerationResult = Iterables.toArray(TWO_ELEMENT_REF_TYPE_WITH_NULL, String.class);
        Assert.assertEquals("1", enumerationResult[0]);
        Assert.assertNull(enumerationResult[1]);
    }

    @Test
    public void equalityCheckComparesInternalArrayByReference() {
        ImmutableArrayList<Integer> immutable1 = ImmutableArrayList.create(1);
        ImmutableArrayList<Integer> immutable2 = ImmutableArrayList.create(1);
        Assert.assertNotEquals(immutable1, immutable2);

        Assert.assertTrue(immutable1.equals(immutable1));
    }

    @Test
    public void equalsObjectNull() {
        Assert.assertFalse(EMPTY.equals(null));
    }

//[Fact]
//public void OperatorsAndEquality()
//{
//    Assert.True(s_empty.Equals(s_empty));
//    var emptySame = s_empty;
//    Assert.True(s_empty == emptySame);
//    Assert.False(s_empty != emptySame);

//    // empty and default should not be seen as equal
//    Assert.False(s_empty.Equals(s_emptyDefault));
//    Assert.False(s_empty == s_emptyDefault);
//    Assert.True(s_empty != s_emptyDefault);
//    Assert.False(s_emptyDefault == s_empty);
//    Assert.True(s_emptyDefault != s_empty);

//    Assert.False(s_empty.Equals(s_oneElement));
//    Assert.False(s_empty == s_oneElement);
//    Assert.True(s_empty != s_oneElement);
//    Assert.False(s_oneElement == s_empty);
//    Assert.True(s_oneElement != s_empty);
//}

//[Fact]
//public void NullableOperators()
//{
//    ImmutableArray<int>? nullArray = null;
//    ImmutableArray<int>? nonNullDefault = s_emptyDefault;
//    ImmutableArray<int>? nonNullEmpty = s_empty;

//    Assert.True(nullArray == nonNullDefault);
//    Assert.False(nullArray != nonNullDefault);
//    Assert.True(nonNullDefault == nullArray);
//    Assert.False(nonNullDefault != nullArray);

//    Assert.False(nullArray == nonNullEmpty);
//    Assert.True(nullArray != nonNullEmpty);
//    Assert.False(nonNullEmpty == nullArray);
//    Assert.True(nonNullEmpty != nullArray);
//}

    @Test
    public void hashCodeTest() {
        Assert.assertNotEquals(0, EMPTY.hashCode());
        Assert.assertNotEquals(0, ONE_ELEMENT.hashCode());
    }

    @Test
    public void add() {
        Integer[] source = {1, 2};
        ImmutableArrayList<Integer> array1 = ImmutableArrayList.create(source);
        ImmutableArrayList<Integer> array2 = array1.add(3);
        assertEqualSequences(Arrays.asList(source), array1);
        assertEqualSequences(Arrays.asList(1, 2, 3), array2);
        assertEqualSequences(Collections.singletonList(1), EMPTY.add(1));
    }

    @Test
    public void addAll() {
        ImmutableArrayList<Integer> nothingToEmpty = EMPTY.addAll(Collections.<Integer>emptyList());
        Assert.assertTrue(nothingToEmpty.isEmpty());

        assertEqualSequences(Arrays.asList(1, 2), EMPTY.addAll(new Range(1, 2)));
        assertEqualSequences(Arrays.asList(1, 2), EMPTY.addAll(Arrays.asList(1, 2)));

        assertEqualSequences(Arrays.asList(1, 2, 3, 4), MANY_ELEMENTS.addAll(Collections.singletonList(4)));
        assertEqualSequences(Arrays.asList(1, 2, 3, 4, 5), MANY_ELEMENTS.addAll(Arrays.asList(4, 5)));
    }

//[Fact]
//public void AddRangeDefaultEnumerable()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.AddRange(Enumerable.Empty<int>()));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.AddRange(Enumerable.Range(1, 2)));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.AddRange(new[] { 1, 2 }));
//}

//[Fact]
//public void AddRangeDefaultStruct()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.AddRange(s_empty));
//    Assert.Throws<NullReferenceException>(() => s_empty.AddRange(s_emptyDefault));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.AddRange(s_oneElement));
//    Assert.Throws<NullReferenceException>(() => s_oneElement.AddRange(s_emptyDefault));

//    IEnumerable<int> emptyBoxed = s_empty;
//    IEnumerable<int> emptyDefaultBoxed = s_emptyDefault;
//    IEnumerable<int> oneElementBoxed = s_oneElement;
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.AddRange(emptyBoxed));
//    Assert.Throws<InvalidOperationException>(() => s_empty.AddRange(emptyDefaultBoxed));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.AddRange(oneElementBoxed));
//    Assert.Throws<InvalidOperationException>(() => s_oneElement.AddRange(emptyDefaultBoxed));
//}

    @Test
    public void addAllNoOpIdentity() {
        Assert.assertSame(EMPTY, EMPTY.addAll(EMPTY));
        Assert.assertSame(ONE_ELEMENT, EMPTY.addAll(ONE_ELEMENT)); // struct overload
        Assert.assertSame(ONE_ELEMENT, EMPTY.addAll((Iterable<Integer>)ONE_ELEMENT)); // enumerable overload
        Assert.assertSame(ONE_ELEMENT, ONE_ELEMENT.addAll(EMPTY));
    }

    @Test
    public void insert() {
        ImmutableArrayList<Character> array1 = ImmutableArrayList.create();
        try {
            array1.add(-1, 'a');
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            array1.add(1, 'a');
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        ImmutableArrayList<Character> insertFirst = array1.add(0, 'c');
        assertEqualSequences(Collections.singletonList('c'), insertFirst);

        ImmutableArrayList<Character> insertLeft = insertFirst.add(0, 'a');
        assertEqualSequences(Arrays.asList('a', 'c'), insertLeft);

        ImmutableArrayList<Character> insertRight = insertFirst.add(1, 'e');
        assertEqualSequences(Arrays.asList('c', 'e'), insertRight);

        ImmutableArrayList<Character> insertBetween = insertLeft.add(1, 'b');
        assertEqualSequences(Arrays.asList('a', 'b', 'c'), insertBetween);
    }

//[Fact]
//public void InsertDefault()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.Insert(-1, 10));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.Insert(1, 10));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.Insert(0, 10));
//}

    @Test
    public void insertAllNoOpIdentity() {
        Assert.assertSame(EMPTY, EMPTY.addAll(0, EMPTY));
        Assert.assertSame(ONE_ELEMENT, EMPTY.addAll(0, ONE_ELEMENT)); // struct overload
        Assert.assertSame(ONE_ELEMENT, EMPTY.addAll(0, (Iterable<Integer>)ONE_ELEMENT)); // enumerable overload
        Assert.assertSame(ONE_ELEMENT, ONE_ELEMENT.addAll(0, EMPTY));
    }

    @Test
    public void insertAllEmpty() {
        assertEqualSequences(Collections.emptyList(), EMPTY.addAll(0, Collections.<Integer>emptyList()));
        assertEqualSequences(EMPTY, EMPTY.addAll(0, Collections.<Integer>emptyList()));
        assertEqualSequences(Collections.singletonList(1), EMPTY.addAll(0, Collections.singletonList(1)));
        assertEqualSequences(Arrays.asList(2, 3, 4), EMPTY.addAll(0, Arrays.asList(2, 3, 4)));
        assertEqualSequences(Arrays.asList(2, 3, 4), EMPTY.addAll(0, new Range(2, 3)));
        assertEqualSequences(MANY_ELEMENTS, MANY_ELEMENTS.addAll(0, Collections.<Integer>emptyList()));

        try {
            EMPTY.addAll(1, ONE_ELEMENT);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            EMPTY.addAll(-1, ONE_ELEMENT);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

//[Fact]
//public void InsertRangeDefault()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(1, Enumerable.Empty<int>()));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(-1, Enumerable.Empty<int>()));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(0, Enumerable.Empty<int>()));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(0, new[] { 1 }));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(0, new[] { 2, 3, 4 }));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(0, Enumerable.Range(2, 3)));
//}

///// <summary>
///// Validates that a fixed bug in the inappropriate adding of the
///// Empty singleton enumerator to the reusable instances bag does not regress.
///// </summary>
//[Fact]
//public void EmptyEnumeratorReuseRegressionTest()
//{
//    IEnumerable<int> oneElementBoxed = s_oneElement;
//    IEnumerable<int> emptyBoxed = s_empty;
//    IEnumerable<int> emptyDefaultBoxed = s_emptyDefault;

//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.RemoveRange(emptyBoxed));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.RemoveRange(emptyDefaultBoxed));
//    Assert.Throws<InvalidOperationException>(() => s_empty.RemoveRange(emptyDefaultBoxed));
//    Assert.Equal(oneElementBoxed, oneElementBoxed);
//}

//[Fact]
//public void InsertRangeDefaultStruct()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(0, s_empty));
//    Assert.Throws<NullReferenceException>(() => s_empty.InsertRange(0, s_emptyDefault));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(0, s_oneElement));
//    Assert.Throws<NullReferenceException>(() => s_oneElement.InsertRange(0, s_emptyDefault));

//    IEnumerable<int> emptyBoxed = s_empty;
//    IEnumerable<int> emptyDefaultBoxed = s_emptyDefault;
//    IEnumerable<int> oneElementBoxed = s_oneElement;
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(0, emptyBoxed));
//    Assert.Throws<InvalidOperationException>(() => s_empty.InsertRange(0, emptyDefaultBoxed));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.InsertRange(0, oneElementBoxed));
//    Assert.Throws<InvalidOperationException>(() => s_oneElement.InsertRange(0, emptyDefaultBoxed));
//}

    @Test
    public void insertRangeLeft() {
        assertEqualSequences(Arrays.asList(7, 1, 2, 3), MANY_ELEMENTS.addAll(0, Collections.singletonList(7)));
        assertEqualSequences(Arrays.asList(7, 8, 1, 2, 3), MANY_ELEMENTS.addAll(0, Arrays.asList(7, 8)));
    }

    @Test
    public void insertRangeMid() {
        assertEqualSequences(Arrays.asList(1, 7, 2, 3), MANY_ELEMENTS.addAll(1, Collections.singletonList(7)));
        assertEqualSequences(Arrays.asList(1, 7, 8, 2, 3), MANY_ELEMENTS.addAll(1, Arrays.asList(7, 8)));
    }

    @Test
    public void insertRangeRight() {
        assertEqualSequences(Arrays.asList(1, 2, 3, 7), MANY_ELEMENTS.addAll(3, Collections.singletonList(7)));
        assertEqualSequences(Arrays.asList(1, 2, 3, 7, 8), MANY_ELEMENTS.addAll(3, Arrays.asList(7, 8)));
    }

    @Test
    public void removeAt() {
        try {
            EMPTY.remove(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            EMPTY.remove(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            EMPTY.remove(-1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        assertEqualSequences(Collections.emptyList(), ONE_ELEMENT.remove(0));
        assertEqualSequences(Arrays.asList(2, 3), MANY_ELEMENTS.remove(0));
        assertEqualSequences(Arrays.asList(1, 3), MANY_ELEMENTS.remove(1));
        assertEqualSequences(Arrays.asList(1, 2), MANY_ELEMENTS.remove(2));
    }

    @Test
    public void remove() {
        Assert.assertTrue(ONE_ELEMENT.remove((Integer)1).isEmpty());
        assertEqualSequences(Arrays.asList(2, 3), MANY_ELEMENTS.remove((Integer)1));
        assertEqualSequences(Arrays.asList(1, 3), MANY_ELEMENTS.remove((Integer)2));
        assertEqualSequences(Arrays.asList(1, 2), MANY_ELEMENTS.remove((Integer)3));
    }

//[Fact]
//public void RemoveRange()
//{
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_empty.RemoveRange(0, 0));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.RemoveRange(0, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_oneElement.RemoveRange(1, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_empty.RemoveRange(-1, 0));
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_oneElement.RemoveRange(0, 2));
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_oneElement.RemoveRange(0, -1));

//    var fourElements = ImmutableArray.Create(1, 2, 3, 4);
//    Assert.Equal(new int[0], s_oneElement.RemoveRange(0, 1));
//    Assert.Equal(s_oneElement.ToArray(), s_oneElement.RemoveRange(0, 0));
//    Assert.Equal(new[] { 3, 4 }, fourElements.RemoveRange(0, 2));
//    Assert.Equal(new[] { 1, 4 }, fourElements.RemoveRange(1, 2));
//    Assert.Equal(new[] { 1, 2 }, fourElements.RemoveRange(2, 2));
//}

//[Fact]
//public void RemoveRangeDefaultStruct()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.RemoveRange(s_empty));
//    Assert.Throws<ArgumentNullException>(() => Assert.Equal(s_empty, s_empty.RemoveRange(s_emptyDefault)));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.RemoveRange(s_oneElement));
//    Assert.Throws<ArgumentNullException>(() => Assert.Equal(s_oneElement, s_oneElement.RemoveRange(s_emptyDefault)));

//    IEnumerable<int> emptyBoxed = s_empty;
//    IEnumerable<int> emptyDefaultBoxed = s_emptyDefault;
//    IEnumerable<int> oneElementBoxed = s_oneElement;
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.RemoveRange(emptyBoxed));
//    Assert.Throws<InvalidOperationException>(() => s_empty.RemoveRange(emptyDefaultBoxed));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.RemoveRange(oneElementBoxed));
//    Assert.Throws<InvalidOperationException>(() => s_oneElement.RemoveRange(emptyDefaultBoxed));
//}

    @Test
    public void removeRangeNoOpIdentity() {
        Assert.assertSame(EMPTY, EMPTY.removeAll(EMPTY));
        Assert.assertSame(EMPTY, EMPTY.removeAll(ONE_ELEMENT)); // struct overload
        Assert.assertSame(EMPTY, EMPTY.removeAll((Iterable<Integer>)ONE_ELEMENT)); // enumerable overload
        Assert.assertSame(ONE_ELEMENT, ONE_ELEMENT.removeAll(EMPTY));
    }

    @Test
    public void removeIf() {
        try {
            ONE_ELEMENT.removeIf(null);
            Assert.fail();
        } catch (NullPointerException ignored) {
        }

        ImmutableArrayList<Integer> array = ImmutableArrayList.createAll(new Range(1, 10));
        ImmutableArrayList<Integer> removedEvens = array.removeIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer % 2 == 0;
            }
        });
        ImmutableArrayList<Integer> removedOdds = array.removeIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer % 2 == 1;
            }
        });
        ImmutableArrayList<Integer> removedAll = array.removeIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return true;
            }
        });
        ImmutableArrayList<Integer> removedNone = array.removeIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return false;
            }
        });

        assertEqualSequences(Arrays.asList(1, 3, 5, 7, 9), removedEvens);
        assertEqualSequences(Arrays.asList(2, 4, 6, 8, 10), removedOdds);
        Assert.assertTrue(removedAll.isEmpty());
        assertEqualSequences(new Range(1, 10), removedNone);

        Assert.assertTrue(EMPTY.removeIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return false;
            }
        }).isEmpty());
    }

    @Test
    public void removeAllIterableTest() {
        ImmutableArrayList<Integer> list = ImmutableArrayList.create(1, 2, 3);
        try {
            list.removeAll(null);
            Assert.fail();
        } catch (NullPointerException ignored) {
        }

        Assert.assertNotNull(EMPTY.removeAll(Collections.<Integer>emptyList()));

        ImmutableArrayList<Integer> removed2 = list.removeAll(Collections.singletonList(2));
        Assert.assertEquals(2, removed2.size());
        assertEqualSequences(Arrays.asList(1, 3), removed2);

        ImmutableArrayList<Integer> removed13 = list.removeAll(Arrays.asList(1, 3, 5));
        Assert.assertEquals(1, removed13.size());
        assertEqualSequences(Collections.singletonList(2), removed13);

        assertEqualSequences(Arrays.asList(1, 3, 6, 8, 9), ImmutableArrayList.createAll(new Range(1, 10)).removeAll(Arrays.asList(2, 4, 5, 7, 10)));
        assertEqualSequences(Arrays.asList(3, 6, 8, 9), ImmutableArrayList.createAll(new Range(1, 10)).removeAll(Arrays.asList(1, 2, 4, 5, 7, 10)));

        assertEqualSequences(list, list.removeAll(Collections.singletonList(5)));
        Assert.assertSame(ImmutableArrayList.<Integer>create(), ImmutableArrayList.<Integer>create().removeAll(Collections.singletonList(1)));

        ImmutableArrayList<Integer> listWithDuplicates = ImmutableArrayList.create(1, 2, 2, 3);
        assertEqualSequences(Arrays.asList(1, 2, 3), listWithDuplicates.removeAll(Collections.singletonList(2)));
        assertEqualSequences(Arrays.asList(1, 3), listWithDuplicates.removeAll(Arrays.asList(2, 2)));
        assertEqualSequences(Arrays.asList(1, 3), listWithDuplicates.removeAll(Arrays.asList(2, 2, 2)));
    }

    @Test
    public void replace() {
        assertEqualSequences(Collections.singletonList(5), ONE_ELEMENT.replace(1, 5));

        assertEqualSequences(Arrays.asList(6, 2, 3), MANY_ELEMENTS.replace(1, 6));
        assertEqualSequences(Arrays.asList(1, 6, 3), MANY_ELEMENTS.replace(2, 6));
        assertEqualSequences(Arrays.asList(1, 2, 6), MANY_ELEMENTS.replace(3, 6));

        assertEqualSequences(Arrays.asList(1, 2, 3, 4), ImmutableArrayList.create(1, 3, 3, 4).replace(3, 2));
    }

    @Test
    public void replaceMissingThrowsTest() {
        try {
            EMPTY.replace(5, 3);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void setItem() {
        try {
            EMPTY.set(0, 10);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            ONE_ELEMENT.set(1, 10);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            EMPTY.set(-1, 10);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        assertEqualSequences(Collections.singletonList(12345), ONE_ELEMENT.set(0, 12345));
        assertEqualSequences(Arrays.asList(12345, 2, 3), MANY_ELEMENTS.set(0, 12345));
        assertEqualSequences(Arrays.asList(1, 12345, 3), MANY_ELEMENTS.set(1, 12345));
        assertEqualSequences(Arrays.asList(1, 2, 12345), MANY_ELEMENTS.set(2, 12345));
    }

//[Fact]
//public void CopyToArray()
//{
//    {
//        var target = new int[s_manyElements.Length];
//        s_manyElements.CopyTo(target);
//        Assert.Equal(target, s_manyElements);
//    }

//    {
//        var target = new int[0];
//        Assert.Throws<NullReferenceException>(() => s_emptyDefault.CopyTo(target));
//    }
//}

//[Fact]
//public void CopyToIntArrayIntInt()
//{
//    var source = ImmutableArray.Create(1, 2, 3);
//    var target = new int[4];
//    source.CopyTo(1, target, 3, 1);
//    Assert.Equal(new[] { 0, 0, 0, 2 }, target);
//}

    @Test
    public void concat() {
        ImmutableArrayList<Integer> array1 = ImmutableArrayList.create(1, 2, 3);
        ImmutableArrayList<Integer> array2 = ImmutableArrayList.create(4, 5, 6);

        Iterable<Integer> concat = Iterables.concat(array1, array2);
        assertEqualSequences(Arrays.asList(1, 2, 3, 4, 5, 6), concat);
    }

///// <summary>
///// Verifies reuse of the original array when concatenated to an empty array.
///// </summary>
//[Fact]
//public void ConcatEdgeCases()
//{
//    // empty arrays
//    Assert.Equal(s_manyElements, s_manyElements.Concat(s_empty));
//    Assert.Equal(s_manyElements, s_empty.Concat(s_manyElements));

//    // default arrays
//    s_manyElements.Concat(s_emptyDefault);
//    Assert.Throws<InvalidOperationException>(() => s_manyElements.Concat(s_emptyDefault).Count());
//    Assert.Throws<InvalidOperationException>(() => s_emptyDefault.Concat(s_manyElements).Count());
//}

//[Fact]
//public void IsDefault()
//{
//    Assert.True(s_emptyDefault.IsDefault);
//    Assert.False(s_empty.IsDefault);
//    Assert.False(s_oneElement.IsDefault);
//}

//[Fact]
//public void IsDefaultOrEmpty()
//{
//    Assert.True(s_empty.IsDefaultOrEmpty);
//    Assert.True(s_emptyDefault.IsDefaultOrEmpty);
//    Assert.False(s_oneElement.IsDefaultOrEmpty);
//}

    @Test
    public void indexGetter() {
        Assert.assertEquals(1, (int)ONE_ELEMENT.get(0));

        try {
            ONE_ELEMENT.get(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            ONE_ELEMENT.get(-1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

//[Fact]
//public void ExplicitMethods()
//{
//    IList<int> c = s_oneElement;
//    Assert.Throws<NotSupportedException>(() => c.Add(3));
//    Assert.Throws<NotSupportedException>(() => c.Clear());
//    Assert.Throws<NotSupportedException>(() => c.Remove(3));
//    Assert.True(c.IsReadOnly);
//    Assert.Throws<NotSupportedException>(() => c.Insert(0, 2));
//    Assert.Throws<NotSupportedException>(() => c.RemoveAt(0));
//    Assert.Equal(s_oneElement[0], c[0]);
//    Assert.Throws<NotSupportedException>(() => c[0] = 8);

//    var enumerator = c.GetEnumerator();
//    Assert.True(enumerator.MoveNext());
//    Assert.Equal(s_oneElement[0], enumerator.Current);
//    Assert.False(enumerator.MoveNext());
//}

//[Fact]
//public void Sort()
//{
//    var array = ImmutableArray.Create(2, 4, 1, 3);
//    Assert.Equal(new[] { 1, 2, 3, 4 }, array.Sort());
//    Assert.Equal(new[] { 2, 4, 1, 3 }, array); // original array unaffected.
//}

    @Test
    public void sortNullComparator() {
        ImmutableArrayList<Integer> array = ImmutableArrayList.create(2, 4, 1, 3);
        assertEqualSequences(Arrays.asList(1, 2, 3, 4), array.sort(null));
        assertEqualSequences(Arrays.asList(2, 4, 1, 3), array); // original array unaffected.
    }

    @Test
    public void sortRange() {
        ImmutableArrayList<Integer> array = ImmutableArrayList.create(2, 4, 1, 3);
        try {
            array.sort(-1, 2, Comparators.<Integer>defaultComparator());
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            array.sort(1, 4, Comparators.<Integer>defaultComparator());
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        assertEqualSequences(Arrays.asList(2, 4, 1, 3), array.sort(array.size(), 0, Comparators.<Integer>defaultComparator()));
        assertEqualSequences(Arrays.asList(2, 1, 4, 3), array.sort(1, 2, Comparators.<Integer>defaultComparator()));
    }

    @Test
    public void sortComparator() {
        ImmutableArrayList<String> array = ImmutableArrayList.create("c", "B", "a");
        assertEqualSequences(Arrays.asList("a", "B", "c"), array.sort(ordinalIgnoreCaseComparator()));
        assertEqualSequences(Arrays.asList("B", "a", "c"), array.sort(ordinalComparator()));
    }

    @Test
    public void sortPreservesArrayWhenAlreadySorted() {
        ImmutableArrayList<Integer> sortedArray = ImmutableArrayList.create(1, 2, 3, 4);
        Assert.assertSame(sortedArray, sortedArray.sort());

        ImmutableArrayList<Integer> mostlySorted = ImmutableArrayList.create(1, 2, 3, 4, 6, 5, 7, 8, 9, 10);
        Assert.assertSame(mostlySorted, mostlySorted.sort(0, 5, Comparators.<Integer>defaultComparator()));
        Assert.assertSame(mostlySorted, mostlySorted.sort(5, 5, Comparators.<Integer>defaultComparator()));
        assertEqualSequences(new Range(1, 10), mostlySorted.sort(4, 2, Comparators.<Integer>defaultComparator()));
    }

    @Test
    public void toBuilder() {
        Assert.assertEquals(0, EMPTY.toBuilder().size());

        ImmutableArrayList.Builder<Integer> builder = ONE_ELEMENT.toBuilder();
        assertEqualSequences(ONE_ELEMENT, builder);

        builder = MANY_ELEMENTS.toBuilder();
        assertEqualSequences(MANY_ELEMENTS, builder);

        // Make sure that changing the builder doesn't change the original immutable array.
        int expected = MANY_ELEMENTS.get(0);
        builder.set(0, expected + 1);
        Assert.assertEquals(expected, (int)MANY_ELEMENTS.get(0));
        Assert.assertEquals(expected + 1, (int)builder.get(0));
    }

//[Fact]
//public void StructuralEquatableEqualsDefault()
//{
//    IStructuralEquatable eq = s_emptyDefault;

//    Assert.True(eq.Equals(s_emptyDefault, EqualityComparer<int>.Default));
//    Assert.False(eq.Equals(s_empty, EqualityComparer<int>.Default));
//    Assert.False(eq.Equals(s_oneElement, EqualityComparer<int>.Default));
//}

//[Fact]
//public void StructuralEquatableEquals()
//{
//    IStructuralEquatable array = new int[3] { 1, 2, 3 };
//    IStructuralEquatable immArray = ImmutableArray.Create(1, 2, 3);

//    var otherArray = new object[] { 1, 2, 3 };
//    var otherImmArray = ImmutableArray.Create(otherArray);
//    var unequalArray = new int[] { 1, 2, 4 };
//    var unequalImmArray = ImmutableArray.Create(unequalArray);
//    var unrelatedArray = new string[3];
//    var unrelatedImmArray = ImmutableArray.Create(unrelatedArray);
//    var otherList = new List<int> { 1, 2, 3 };
//    Assert.Equal(array.Equals(otherArray, EqualityComparer<int>.Default), immArray.Equals(otherImmArray, EqualityComparer<int>.Default));
//    Assert.Equal(array.Equals(otherList, EqualityComparer<int>.Default), immArray.Equals(otherList, EqualityComparer<int>.Default));
//    Assert.Equal(array.Equals(unrelatedArray, EverythingEqual<object>.Default), immArray.Equals(unrelatedImmArray, EverythingEqual<object>.Default));
//    Assert.Equal(array.Equals(new object(), EqualityComparer<int>.Default), immArray.Equals(new object(), EqualityComparer<int>.Default));
//    Assert.Equal(array.Equals(null, EqualityComparer<int>.Default), immArray.Equals(null, EqualityComparer<int>.Default));
//    Assert.Equal(array.Equals(unequalArray, EqualityComparer<int>.Default), immArray.Equals(unequalImmArray, EqualityComparer<int>.Default));
//}

//[Fact]
//public void StructuralEquatableEqualsArrayInterop()
//{
//    IStructuralEquatable array = new int[3] { 1, 2, 3 };
//    IStructuralEquatable immArray = ImmutableArray.Create(1, 2, 3);
//    var unequalArray = new int[] { 1, 2, 4 };

//    Assert.True(immArray.Equals(array, EqualityComparer<int>.Default));
//    Assert.False(immArray.Equals(unequalArray, EqualityComparer<int>.Default));
//}

//[Fact]
//public void StructuralEquatableGetHashCodeDefault()
//{
//    IStructuralEquatable defaultImmArray = s_emptyDefault;
//    Assert.Equal(0, defaultImmArray.GetHashCode(EqualityComparer<int>.Default));
//}

//[Fact]
//public void StructuralEquatableGetHashCode()
//{
//    IStructuralEquatable emptyArray = new int[0];
//    IStructuralEquatable emptyImmArray = s_empty;
//    IStructuralEquatable array = new int[3] { 1, 2, 3 };
//    IStructuralEquatable immArray = ImmutableArray.Create(1, 2, 3);

//    Assert.Equal(emptyArray.GetHashCode(EqualityComparer<int>.Default), emptyImmArray.GetHashCode(EqualityComparer<int>.Default));
//    Assert.Equal(array.GetHashCode(EqualityComparer<int>.Default), immArray.GetHashCode(EqualityComparer<int>.Default));
//    Assert.Equal(array.GetHashCode(EverythingEqual<int>.Default), immArray.GetHashCode(EverythingEqual<int>.Default));
//}

//[Fact]
//public void StructuralComparableDefault()
//{
//    IStructuralComparable def = s_emptyDefault;
//    IStructuralComparable mt = s_empty;

//    // default to default is fine, and should be seen as equal.
//    Assert.Equal(0, def.CompareTo(s_emptyDefault, Comparer<int>.Default));

//    // default to empty and vice versa should throw, on the basis that
//    // arrays compared that are of different lengths throw. Empty vs. default aren't really compatible.
//    Assert.Throws<ArgumentException>(() => def.CompareTo(s_empty, Comparer<int>.Default));
//    Assert.Throws<ArgumentException>(() => mt.CompareTo(s_emptyDefault, Comparer<int>.Default));
//}

//[Fact]
//public void StructuralComparable()
//{
//    IStructuralComparable array = new int[3] { 1, 2, 3 };
//    IStructuralComparable equalArray = new int[3] { 1, 2, 3 };
//    IStructuralComparable immArray = ImmutableArray.Create((int[])array);
//    IStructuralComparable equalImmArray = ImmutableArray.Create((int[])equalArray);

//    IStructuralComparable longerArray = new int[] { 1, 2, 3, 4 };
//    IStructuralComparable longerImmArray = ImmutableArray.Create((int[])longerArray);

//    Assert.Equal(array.CompareTo(equalArray, Comparer<int>.Default), immArray.CompareTo(equalImmArray, Comparer<int>.Default));

//    Assert.Throws<ArgumentException>(() => array.CompareTo(longerArray, Comparer<int>.Default));
//    Assert.Throws<ArgumentException>(() => immArray.CompareTo(longerImmArray, Comparer<int>.Default));

//    var list = new List<int> { 1, 2, 3 };
//    Assert.Throws<ArgumentException>(() => array.CompareTo(list, Comparer<int>.Default));
//    Assert.Throws<ArgumentException>(() => immArray.CompareTo(list, Comparer<int>.Default));
//}

//[Fact]
//public void StructuralComparableArrayInterop()
//{
//    IStructuralComparable array = new int[3] { 1, 2, 3 };
//    IStructuralComparable equalArray = new int[3] { 1, 2, 3 };
//    IStructuralComparable immArray = ImmutableArray.Create((int[])array);
//    IStructuralComparable equalImmArray = ImmutableArray.Create((int[])equalArray);

//    Assert.Equal(array.CompareTo(equalArray, Comparer<int>.Default), immArray.CompareTo(equalArray, Comparer<int>.Default));
//}

    @Test
    public void binarySearch() {
        Assert.assertEquals(Arrays.binarySearch(new int[0], 5), ImmutableArrayList.binarySearch(ImmutableArrayList.<Integer>create(), 5));
        Assert.assertEquals(Arrays.binarySearch(new int[] { 3 }, 5), ImmutableArrayList.binarySearch(ImmutableArrayList.create(3), 5));
        Assert.assertEquals(Arrays.binarySearch(new int[] { 5 }, 5), ImmutableArrayList.binarySearch(ImmutableArrayList.create(5), 5));
    }

    @Test
    public void ofType() {
        Assert.assertEquals(0, Iterables.size(EMPTY.ofType(Integer.class)));
        Assert.assertEquals(1, Iterables.size(ONE_ELEMENT.ofType(Integer.class)));
        Assert.assertEquals(1, Iterables.size(TWO_ELEMENT_REF_TYPE_WITH_NULL.ofType(String.class)));
    }

//[Fact]
//public void Add_ThreadSafety()
//{
//    // Note the point of this thread-safety test is *not* to test the thread-safety of the test itself.
//    // This test has a known issue where the two threads will stomp on each others updates, but that's not the point.
//    // The point is that ImmutableArray`1.Add should *never* throw. But if it reads its own T[] field more than once,
//    // it *can* throw because the field can be replaced with an array of another length.
//    // In fact, much worse can happen where we corrupt data if we are for example copying data out of the array
//    // in (for example) a CopyTo method and we read from the field more than once.
//    // Also noteworthy: this method only tests the thread-safety of the Add method.
//    // While it proves the general point, any method that reads 'this' more than once is vulnerable.
//    var array = ImmutableArray.Create<int>();
//    Action mutator = () =>
//    {
//        for (int i = 0; i < 100; i++)
//        {
//            ImmutableInterlocked.InterlockedExchange(ref array, array.Add(1));
//        }
//    };
//    Task.WaitAll(Task.Run(mutator), Task.Run(mutator));
//}

//[Fact]
//public void DebuggerAttributesValid()
//{
//    DebuggerAttributes.ValidateDebuggerDisplayReferences(ImmutableArray.Create<string>()); // verify empty
//    DebuggerAttributes.ValidateDebuggerDisplayReferences(ImmutableArray.Create(1, 2, 3));  // verify non-empty
//}

//[Fact]
//public void ICollectionSyncRoot_NotSupported()
//{
//    ICollection c = ImmutableArray.Create(1, 2, 3);
//    Assert.Throws<NotSupportedException>(() => c.SyncRoot);
//}

    @Override
    protected <T> Iterable<T> getIterableOf(T... contents) {
        return ImmutableArrayList.create(contents);
    }

///// <summary>
///// A structure that takes exactly 3 bytes of memory.
///// </summary>
//private struct ThreeByteStruct : IEquatable<ThreeByteStruct>
//{
//    public ThreeByteStruct(byte first, byte second, byte third)
//    {
//        this.Field1 = first;
//        this.Field2 = second;
//        this.Field3 = third;
//    }

//    public byte Field1;
//    public byte Field2;
//    public byte Field3;

//    public bool Equals(ThreeByteStruct other)
//    {
//        return this.Field1 == other.Field1
//            && this.Field2 == other.Field2
//            && this.Field3 == other.Field3;
//    }

//    public override bool Equals(object obj)
//    {
//        if (obj is ThreeByteStruct)
//        {
//            return this.Equals((ThreeByteStruct)obj);
//        }

//        return false;
//    }

//    public override int GetHashCode()
//    {
//        return this.Field1;
//    }
//}

///// <summary>
///// A structure that takes exactly 9 bytes of memory.
///// </summary>
//private struct NineByteStruct : IEquatable<NineByteStruct>
//{
//    public NineByteStruct(int first, int second, int third, int fourth, int fifth, int sixth, int seventh, int eighth, int ninth)
//    {
//        this.Field1 = (byte)first;
//        this.Field2 = (byte)second;
//        this.Field3 = (byte)third;
//        this.Field4 = (byte)fourth;
//        this.Field5 = (byte)fifth;
//        this.Field6 = (byte)sixth;
//        this.Field7 = (byte)seventh;
//        this.Field8 = (byte)eighth;
//        this.Field9 = (byte)ninth;
//    }

//    public byte Field1;
//    public byte Field2;
//    public byte Field3;
//    public byte Field4;
//    public byte Field5;
//    public byte Field6;
//    public byte Field7;
//    public byte Field8;
//    public byte Field9;

//    public bool Equals(NineByteStruct other)
//    {
//        return this.Field1 == other.Field1
//            && this.Field2 == other.Field2
//            && this.Field3 == other.Field3
//            && this.Field4 == other.Field4
//            && this.Field5 == other.Field5
//            && this.Field6 == other.Field6
//            && this.Field7 == other.Field7
//            && this.Field8 == other.Field8
//            && this.Field9 == other.Field9;
//    }

//    public override bool Equals(object obj)
//    {
//        if (obj is NineByteStruct)
//        {
//            return this.Equals((NineByteStruct)obj);
//        }

//        return false;
//    }

//    public override int GetHashCode()
//    {
//        return this.Field1;
//    }
//}

///// <summary>
///// A structure that requires 9 bytes of memory but occupies 12 because of memory alignment.
///// </summary>
//private struct TwelveByteStruct : IEquatable<TwelveByteStruct>
//{
//    public TwelveByteStruct(int first, int second, byte third)
//    {
//        this.Field1 = first;
//        this.Field2 = second;
//        this.Field3 = third;
//    }

//    public int Field1;
//    public int Field2;
//    public byte Field3;

//    public bool Equals(TwelveByteStruct other)
//    {
//        return this.Field1 == other.Field1
//            && this.Field2 == other.Field2
//            && this.Field3 == other.Field3;
//    }

//    public override bool Equals(object obj)
//    {
//        if (obj is TwelveByteStruct)
//        {
//            return this.Equals((TwelveByteStruct)obj);
//        }

//        return false;
//    }

//    public override int GetHashCode()
//    {
//        return this.Field1;
//    }
//}

//private struct StructWithReferenceTypeField
//{
//    public string foo;

//    public StructWithReferenceTypeField(string foo)
//    {
//        this.foo = foo;
//    }
//}
}
