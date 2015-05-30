// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import com.tvl.util.function.BiFunction;
import com.tvl.util.function.Function;
import java.util.Arrays;
import java.util.Collections;
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

//[Fact]
//public void CreateFromArray()
//{
//    var source = new[] { 1, 2, 3 };
//    var immutable = ImmutableArray.Create(source);
//    Assert.Equal(source, immutable);
//}

//[Fact]
//public void CreateFromNullArray()
//{
//    int[] nullArray = null;
//    ImmutableArray<int> immutable = ImmutableArray.Create(nullArray);
//    Assert.False(immutable.IsDefault);
//    Assert.Equal(0, immutable.Length);
//}

//[Fact]
//public void Covariance()
//{
//    ImmutableArray<string> derivedImmutable = ImmutableArray.Create("a", "b", "c");
//    ImmutableArray<object> baseImmutable = derivedImmutable.As<object>();
//    Assert.False(baseImmutable.IsDefault);
//    // Must cast to object or the IEnumerable<object> overload of Equals would be used
//    Assert.Equal((object)derivedImmutable, baseImmutable, EqualityComparer<object>.Default);

//    // Make sure we can reverse that, as a means to verify the underlying array is the same instance.
//    ImmutableArray<string> derivedImmutable2 = baseImmutable.As<string>();
//    Assert.False(derivedImmutable2.IsDefault);
//    Assert.Equal(derivedImmutable, derivedImmutable2);

//    // Try a cast that would fail.
//    Assert.True(baseImmutable.As<Encoder>().IsDefault);
//}

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

///// <summary>
///// Verifies that using an ordinary Create factory method is smart enough to reuse
///// an underlying array when possible.
///// </summary>
//[Fact]
//public void CovarianceImplicit()
//{
//    ImmutableArray<string> derivedImmutable = ImmutableArray.Create("a", "b", "c");
//    ImmutableArray<object> baseImmutable = ImmutableArray.CreateRange<object>(derivedImmutable);
//    // Must cast to object or the IEnumerable<object> overload of Equals would be used
//    Assert.Equal((object)derivedImmutable, baseImmutable, EqualityComparer<object>.Default);

//    // Make sure we can reverse that, as a means to verify the underlying array is the same instance.
//    ImmutableArray<string> derivedImmutable2 = baseImmutable.As<string>();
//    Assert.Equal(derivedImmutable, derivedImmutable2);
//}

//[Fact]
//public void CastUpReference()
//{
//    ImmutableArray<string> derivedImmutable = ImmutableArray.Create("a", "b", "c");
//    ImmutableArray<object> baseImmutable = ImmutableArray<object>.CastUp(derivedImmutable);
//    // Must cast to object or the IEnumerable<object> overload of Equals would be used
//    Assert.Equal((object)derivedImmutable, baseImmutable, EqualityComparer<object>.Default);

//    // Make sure we can reverse that, as a means to verify the underlying array is the same instance.
//    Assert.Equal(derivedImmutable, baseImmutable.As<string>());
//    Assert.Equal(derivedImmutable, baseImmutable.CastArray<string>());
//}

//[Fact]
//public void CastUpReferenceDefaultValue()
//{
//    ImmutableArray<string> derivedImmutable = default(ImmutableArray<string>);
//    ImmutableArray<object> baseImmutable = ImmutableArray<object>.CastUp(derivedImmutable);
//    Assert.True(baseImmutable.IsDefault);
//    Assert.True(derivedImmutable.IsDefault);

//    // Make sure we can reverse that, as a means to verify the underlying array is the same instance.
//    ImmutableArray<string> derivedImmutable2 = baseImmutable.As<string>();
//    Assert.True(derivedImmutable2.IsDefault);
//    Assert.True(derivedImmutable == derivedImmutable2);
//}

//[Fact]
//public void CastUpRefToInterface()
//{
//    var stringArray = ImmutableArray.Create("a", "b");
//    var enumArray = ImmutableArray<IEnumerable>.CastUp(stringArray);
//    Assert.Equal(2, enumArray.Length);
//    Assert.Equal(stringArray, enumArray.CastArray<string>());
//    Assert.Equal(stringArray, enumArray.As<string>());
//}

//[Fact]
//public void CastUpInterfaceToInterface()
//{
//    var genericEnumArray = ImmutableArray.Create<IEnumerable<int>>(new List<int>(), new List<int>());
//    var legacyEnumArray = ImmutableArray<IEnumerable>.CastUp(genericEnumArray);
//    Assert.Equal(2, legacyEnumArray.Length);
//    Assert.Equal(genericEnumArray, legacyEnumArray.As<IEnumerable<int>>());
//    Assert.Equal(genericEnumArray, legacyEnumArray.CastArray<IEnumerable<int>>());
//}

//[Fact]
//public void CastUpArrayToSystemArray()
//{
//    var arrayArray = ImmutableArray.Create(new int[] { 1, 2 }, new int[] { 3, 4 });
//    var sysArray = ImmutableArray<Array>.CastUp(arrayArray);
//    Assert.Equal(2, sysArray.Length);
//    Assert.Equal(arrayArray, sysArray.As<int[]>());
//    Assert.Equal(arrayArray, sysArray.CastArray<int[]>());
//}

//[Fact]
//public void CastUpArrayToObject()
//{
//    var arrayArray = ImmutableArray.Create(new int[] { 1, 2 }, new int[] { 3, 4 });
//    var objArray = ImmutableArray<object>.CastUp(arrayArray);
//    Assert.Equal(2, objArray.Length);
//    Assert.Equal(arrayArray, objArray.As<int[]>());
//    Assert.Equal(arrayArray, objArray.CastArray<int[]>());
//}

//[Fact]
//public void CastUpDelegateToSystemDelegate()
//{
//    var delArray = ImmutableArray.Create<Action>(() => { }, () => { });
//    var sysDelArray = ImmutableArray<Delegate>.CastUp(delArray);
//    Assert.Equal(2, sysDelArray.Length);
//    Assert.Equal(delArray, sysDelArray.As<Action>());
//    Assert.Equal(delArray, sysDelArray.CastArray<Action>());
//}

//[Fact]
//public void CastArrayUnrelatedInterface()
//{
//    var strArray = ImmutableArray.Create<string>("cat", "dog");
//    var compArray = ImmutableArray<IComparable>.CastUp(strArray);
//    var enumArray = compArray.CastArray<IEnumerable>();
//    Assert.Equal(2, enumArray.Length);
//    Assert.Equal(strArray, enumArray.As<string>());
//    Assert.Equal(strArray, enumArray.CastArray<string>());
//}

//[Fact]
//public void CastArrayBadInterface()
//{
//    var formattableArray = ImmutableArray.Create<IFormattable>(1, 2);
//    Assert.Throws(typeof(InvalidCastException), () => formattableArray.CastArray<IComparable>());
//}

//[Fact]
//public void CastArrayBadRef()
//{
//    var objArray = ImmutableArray.Create<object>("cat", "dog");
//    Assert.Throws(typeof(InvalidCastException), () => objArray.CastArray<string>());
//}

//[Fact]
//public void ToImmutableArray()
//{
//    IEnumerable<int> source = new[] { 1, 2, 3 };
//    ImmutableArray<int> immutable = source.ToImmutableArray();
//    Assert.Equal(source, immutable);

//    ImmutableArray<int> immutable2 = immutable.ToImmutableArray();
//    Assert.Equal(immutable, immutable2); // this will compare array reference equality.
//}

//[Fact]
//public void Count()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.Length);
//    Assert.Throws<InvalidOperationException>(() => ((ICollection)s_emptyDefault).Count);
//    Assert.Throws<InvalidOperationException>(() => ((ICollection<int>)s_emptyDefault).Count);
//    Assert.Throws<InvalidOperationException>(() => ((IReadOnlyCollection<int>)s_emptyDefault).Count);

//    Assert.Equal(0, s_empty.Length);
//    Assert.Equal(0, ((ICollection)s_empty).Count);
//    Assert.Equal(0, ((ICollection<int>)s_empty).Count);
//    Assert.Equal(0, ((IReadOnlyCollection<int>)s_empty).Count);

//    Assert.Equal(1, s_oneElement.Length);
//    Assert.Equal(1, ((ICollection)s_oneElement).Count);
//    Assert.Equal(1, ((ICollection<int>)s_oneElement).Count);
//    Assert.Equal(1, ((IReadOnlyCollection<int>)s_oneElement).Count);
//}

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
    //public void containsEqualityComparer() {
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

//[Fact]
//public void Add()
//{
//    var source = new[] { 1, 2 };
//    var array1 = ImmutableArray.Create(source);
//    var array2 = array1.Add(3);
//    Assert.Equal(source, array1);
//    Assert.Equal(new[] { 1, 2, 3 }, array2);
//    Assert.Equal(new[] { 1 }, s_empty.Add(1));
//}

//[Fact]
//public void AddRange()
//{
//    var nothingToEmpty = s_empty.AddRange(Enumerable.Empty<int>());
//    Assert.False(nothingToEmpty.IsDefault);
//    Assert.True(nothingToEmpty.IsEmpty);

//    Assert.Equal(new[] { 1, 2 }, s_empty.AddRange(Enumerable.Range(1, 2)));
//    Assert.Equal(new[] { 1, 2 }, s_empty.AddRange(new[] { 1, 2 }));

//    Assert.Equal(new[] { 1, 2, 3, 4 }, s_manyElements.AddRange(new[] { 4 }));
//    Assert.Equal(new[] { 1, 2, 3, 4, 5 }, s_manyElements.AddRange(new[] { 4, 5 }));
//}

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

//[Fact]
//public void AddRangeNoOpIdentity()
//{
//    Assert.Equal(s_empty, s_empty.AddRange(s_empty));
//    Assert.Equal(s_oneElement, s_empty.AddRange(s_oneElement)); // struct overload
//    Assert.Equal(s_oneElement, s_empty.AddRange((IEnumerable<int>)s_oneElement)); // enumerable overload
//    Assert.Equal(s_oneElement, s_oneElement.AddRange(s_empty));
//}

//[Fact]
//public void Insert()
//{
//    var array1 = ImmutableArray.Create<char>();
//    Assert.Throws<ArgumentOutOfRangeException>(() => array1.Insert(-1, 'a'));
//    Assert.Throws<ArgumentOutOfRangeException>(() => array1.Insert(1, 'a'));

//    var insertFirst = array1.Insert(0, 'c');
//    Assert.Equal(new[] { 'c' }, insertFirst);

//    var insertLeft = insertFirst.Insert(0, 'a');
//    Assert.Equal(new[] { 'a', 'c' }, insertLeft);

//    var insertRight = insertFirst.Insert(1, 'e');
//    Assert.Equal(new[] { 'c', 'e' }, insertRight);

//    var insertBetween = insertLeft.Insert(1, 'b');
//    Assert.Equal(new[] { 'a', 'b', 'c' }, insertBetween);
//}

//[Fact]
//public void InsertDefault()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.Insert(-1, 10));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.Insert(1, 10));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.Insert(0, 10));
//}

//[Fact]
//public void InsertRangeNoOpIdentity()
//{
//    Assert.Equal(s_empty, s_empty.InsertRange(0, s_empty));
//    Assert.Equal(s_oneElement, s_empty.InsertRange(0, s_oneElement)); // struct overload
//    Assert.Equal(s_oneElement, s_empty.InsertRange(0, (IEnumerable<int>)s_oneElement)); // enumerable overload
//    Assert.Equal(s_oneElement, s_oneElement.InsertRange(0, s_empty));
//}

//[Fact]
//public void InsertRangeEmpty()
//{
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.Insert(-1, 10));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.Insert(1, 10));
//    Assert.Equal(new int[0], s_empty.InsertRange(0, Enumerable.Empty<int>()));
//    Assert.Equal(s_empty, s_empty.InsertRange(0, Enumerable.Empty<int>()));
//    Assert.Equal(new[] { 1 }, s_empty.InsertRange(0, new[] { 1 }));
//    Assert.Equal(new[] { 2, 3, 4 }, s_empty.InsertRange(0, new[] { 2, 3, 4 }));
//    Assert.Equal(new[] { 2, 3, 4 }, s_empty.InsertRange(0, Enumerable.Range(2, 3)));
//    Assert.Equal(s_manyElements, s_manyElements.InsertRange(0, Enumerable.Empty<int>()));
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_empty.InsertRange(1, s_oneElement));
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_empty.InsertRange(-1, s_oneElement));
//}

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

//[Fact]
//public void RemoveAll()
//{
//    Assert.Throws<ArgumentNullException>(() => s_oneElement.RemoveAll(null));

//    var array = ImmutableArray.CreateRange(Enumerable.Range(1, 10));
//    var removedEvens = array.RemoveAll(n => n % 2 == 0);
//    var removedOdds = array.RemoveAll(n => n % 2 == 1);
//    var removedAll = array.RemoveAll(n => true);
//    var removedNone = array.RemoveAll(n => false);

//    Assert.Equal(new[] { 1, 3, 5, 7, 9 }, removedEvens);
//    Assert.Equal(new[] { 2, 4, 6, 8, 10 }, removedOdds);
//    Assert.True(removedAll.IsEmpty);
//    Assert.Equal(Enumerable.Range(1, 10), removedNone);

//    Assert.False(s_empty.RemoveAll(n => false).IsDefault);
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.RemoveAll(n => false));
//}

//[Fact]
//public void RemoveRangeEnumerableTest()
//{
//    var list = ImmutableArray.Create(1, 2, 3);
//    Assert.Throws<ArgumentNullException>(() => list.RemoveRange(null));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.RemoveRange(new int[0]).IsDefault);
//    Assert.False(s_empty.RemoveRange(new int[0]).IsDefault);

//    ImmutableArray<int> removed2 = list.RemoveRange(new[] { 2 });
//    Assert.Equal(2, removed2.Length);
//    Assert.Equal(new[] { 1, 3 }, removed2);

//    ImmutableArray<int> removed13 = list.RemoveRange(new[] { 1, 3, 5 });
//    Assert.Equal(1, removed13.Length);
//    Assert.Equal(new[] { 2 }, removed13);

//    Assert.Equal(new[] { 1, 3, 6, 8, 9 }, ImmutableArray.CreateRange(Enumerable.Range(1, 10)).RemoveRange(new[] { 2, 4, 5, 7, 10 }));
//    Assert.Equal(new[] { 3, 6, 8, 9 }, ImmutableArray.CreateRange(Enumerable.Range(1, 10)).RemoveRange(new[] { 1, 2, 4, 5, 7, 10 }));

//    Assert.Equal(list, list.RemoveRange(new[] { 5 }));
//    Assert.Equal(ImmutableArray.Create<int>(), ImmutableArray.Create<int>().RemoveRange(new[] { 1 }));

//    var listWithDuplicates = ImmutableArray.Create(1, 2, 2, 3);
//    Assert.Equal(new[] { 1, 2, 3 }, listWithDuplicates.RemoveRange(new[] { 2 }));
//    Assert.Equal(new[] { 1, 3 }, listWithDuplicates.RemoveRange(new[] { 2, 2 }));
//    Assert.Equal(new[] { 1, 3 }, listWithDuplicates.RemoveRange(new[] { 2, 2, 2 }));
//}

//[Fact]
//public void Replace()
//{
//    Assert.Equal(new[] { 5 }, s_oneElement.Replace(1, 5));

//    Assert.Equal(new[] { 6, 2, 3 }, s_manyElements.Replace(1, 6));
//    Assert.Equal(new[] { 1, 6, 3 }, s_manyElements.Replace(2, 6));
//    Assert.Equal(new[] { 1, 2, 6 }, s_manyElements.Replace(3, 6));

//    Assert.Equal(new[] { 1, 2, 3, 4 }, ImmutableArray.Create(1, 3, 3, 4).Replace(3, 2));
//}

    @Test
    public void replaceMissingThrowsTest() {
        try {
            EMPTY.replace(5, 3);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

//[Fact]
//public void SetItem()
//{
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_empty.SetItem(0, 10));
//    Assert.Throws<NullReferenceException>(() => s_emptyDefault.SetItem(0, 10));
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_oneElement.SetItem(1, 10));
//    Assert.Throws<ArgumentOutOfRangeException>(() => s_empty.SetItem(-1, 10));

//    Assert.Equal(new[] { 12345 }, s_oneElement.SetItem(0, 12345));
//    Assert.Equal(new[] { 12345, 2, 3 }, s_manyElements.SetItem(0, 12345));
//    Assert.Equal(new[] { 1, 12345, 3 }, s_manyElements.SetItem(1, 12345));
//    Assert.Equal(new[] { 1, 2, 12345 }, s_manyElements.SetItem(2, 12345));
//}

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

//[Fact]
//public void IndexGetter()
//{
//    Assert.Equal(1, s_oneElement[0]);
//    Assert.Equal(1, ((IList)s_oneElement)[0]);
//    Assert.Equal(1, ((IList<int>)s_oneElement)[0]);
//    Assert.Equal(1, ((IReadOnlyList<int>)s_oneElement)[0]);

//    Assert.Throws<IndexOutOfRangeException>(() => s_oneElement[1]);
//    Assert.Throws<IndexOutOfRangeException>(() => s_oneElement[-1]);

//    Assert.Throws<NullReferenceException>(() => s_emptyDefault[0]);
//    Assert.Throws<InvalidOperationException>(() => ((IList)s_emptyDefault)[0]);
//    Assert.Throws<InvalidOperationException>(() => ((IList<int>)s_emptyDefault)[0]);
//    Assert.Throws<InvalidOperationException>(() => ((IReadOnlyList<int>)s_emptyDefault)[0]);
//}

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

//[Fact]
//public void BinarySearch()
//{
//    Assert.Throws<ArgumentNullException>(() => Assert.Equal(Array.BinarySearch(new int[0], 5), ImmutableArray.BinarySearch(default(ImmutableArray<int>), 5)));
//    Assert.Equal(Array.BinarySearch(new int[0], 5), ImmutableArray.BinarySearch(ImmutableArray.Create<int>(), 5));
//    Assert.Equal(Array.BinarySearch(new int[] { 3 }, 5), ImmutableArray.BinarySearch(ImmutableArray.Create(3), 5));
//    Assert.Equal(Array.BinarySearch(new int[] { 5 }, 5), ImmutableArray.BinarySearch(ImmutableArray.Create(5), 5));
//}

//[Fact]
//public void OfType()
//{
//    Assert.Equal(0, s_emptyDefault.OfType<int>().Count());
//    Assert.Equal(0, s_empty.OfType<int>().Count());
//    Assert.Equal(1, s_oneElement.OfType<int>().Count());
//    Assert.Equal(1, s_twoElementRefTypeWithNull.OfType<string>().Count());
//}

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
