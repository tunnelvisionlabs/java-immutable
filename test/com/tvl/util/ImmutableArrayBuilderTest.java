// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import com.tvl.util.function.BiFunction;
import com.tvl.util.function.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class ImmutableArrayBuilderTest extends SimpleElementImmutablesTestBase {

    @Test
    public void createBuilderDefaultCapacity() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        Assert.assertNotNull(builder);
        Assert.assertNotSame(builder, ImmutableArrayList.createBuilder());
    }

    @Test
    public void createBuilderInvalidCapacity() {
        thrown.expect(instanceOf(IllegalArgumentException.class));
        ImmutableArrayList.createBuilder(-1);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Test
    public void normalConstructionValueType() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(3);
        Assert.assertEquals(0, builder.size());
        for (int i = 0; i < builder.size(); i++) {
            Assert.assertEquals(null, builder.get(i));
        }

        builder.add(5);
        builder.add(6);
        builder.add(7);

        Assert.assertEquals((Object)5, builder.get(0));
        Assert.assertEquals((Object)6, builder.get(1));
        Assert.assertEquals((Object)7, builder.get(2));
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Test
    public void normalConstructionRefType() {
        ImmutableArrayList.Builder<GenericParameterHelper> builder = ImmutableArrayList.createBuilder(3);
        Assert.assertEquals(0, builder.size());
        for (int i = 0; i < builder.size(); i++) {
            Assert.assertNull(builder.get(i));
        }

        builder.add(new GenericParameterHelper(5));
        builder.add(new GenericParameterHelper(6));
        builder.add(new GenericParameterHelper(7));

        Assert.assertEquals(5, builder.get(0).getData());
        Assert.assertEquals(6, builder.get(1).getData());
        Assert.assertEquals(7, builder.get(2).getData());
    }

    @Test
    public void addRangeIterable() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(2);
        builder.addAll((Iterable<Integer>)Collections.singletonList(1));
        Assert.assertEquals(1, builder.size());

        builder.addAll((Iterable<Integer>)Collections.singletonList(2));
        Assert.assertEquals(2, builder.size());

        // Exceed capacity
        builder.addAll(new Range(3, 2)); // use an enumerable without a breakable Count
        Assert.assertEquals(4, builder.size());

        assertEqualSequences(new Range(1, 4), builder);
    }

    @Test
    public void add() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(0);
        builder.add(1);
        builder.add(2);
        Assert.assertEquals(Arrays.asList(1, 2), builder);
        Assert.assertEquals(2, builder.size());

        builder = ImmutableArrayList.createBuilder(1);
        builder.add(1);
        builder.add(2);
        Assert.assertEquals(Arrays.asList(1, 2), builder);
        Assert.assertEquals(2, builder.size());

        builder = ImmutableArrayList.createBuilder(2);
        builder.add(1);
        builder.add(2);
        Assert.assertEquals(Arrays.asList(1, 2), builder);
        Assert.assertEquals(2, builder.size());
    }

    @Test
    public void addRangeBuilder() {
        ImmutableArrayList.Builder<Integer> builder1 = ImmutableArrayList.createBuilder(2);
        ImmutableArrayList.Builder<Integer> builder2 = ImmutableArrayList.createBuilder(2);

        builder1.addAll(builder2);
        Assert.assertEquals(0, builder1.size());
        Assert.assertEquals(0, builder2.size());

        builder2.add(1);
        builder2.add(2);
        builder1.addAll(builder2);
        Assert.assertEquals(2, builder1.size());
        Assert.assertEquals(2, builder2.size());
        Assert.assertEquals(Arrays.asList(1, 2), builder1);
    }

    @Test
    public void addRangeImmutableArray() {
        ImmutableArrayList.Builder<Integer> builder1 = ImmutableArrayList.createBuilder(2);

        ImmutableArrayList<Integer> array = ImmutableArrayList.create(1, 2, 3);

        builder1.addAll(array);
        assertEqualSequences(Arrays.asList(1, 2, 3), builder1);
    }

    @Test
    public void addRangeDerivedArray() {
        ImmutableArrayList.Builder<Object> builder = ImmutableArrayList.createBuilder();
        builder.addAll(Arrays.asList("a", "b"));
        assertEqualSequences(Arrays.asList("a", "b"), builder);
    }

    @Test
    public void addRangeDerivedImmutableArray() {
        ImmutableArrayList.Builder<Object> builder = ImmutableArrayList.createBuilder();
        builder.addAll(ImmutableArrayList.create(new String[] { "a", "b" }));
        assertEqualSequences(Arrays.asList("a", "b"), builder);
    }

    @Test
    public void addRangeDerivedBuilder() {
        ImmutableArrayList.Builder<String> builder = ImmutableArrayList.createBuilder();
        builder.addAll("a", "b");

        ImmutableArrayList.Builder<Object> builderBase = ImmutableArrayList.createBuilder();
        builderBase.addAll(builder);
        assertEqualSequences(Arrays.asList("a", "b"), builderBase);
    }

    @Test
    public void contains() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        Assert.assertFalse(builder.contains(1));
        builder.add(1);
        Assert.assertTrue(builder.contains(1));
    }

    @Test
    public void indexOf() {
        IndexOfTests.indexOfTest(
            new Function<Iterable<Integer>, ImmutableArrayList.Builder<Integer>>() {
                @Override
                public ImmutableArrayList.Builder<Integer> apply(Iterable<Integer> sequence) {
                    return (ImmutableArrayList.Builder<Integer>)getIterableOf(sequence, Integer.class);
                }
            },
            new BiFunction<ImmutableArrayList.Builder<Integer>, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableArrayList.Builder<Integer> b, Integer v) {
                    return b.indexOf(v);
                }
            },
            new IndexOfTests.TriFunction<ImmutableArrayList.Builder<Integer>, Integer, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableArrayList.Builder<Integer> b, Integer v, Integer i) {
                    return b.indexOf(v, i);
                }
            },
            new IndexOfTests.QuadFunction<ImmutableArrayList.Builder<Integer>, Integer, Integer, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableArrayList.Builder<Integer> b, Integer v, Integer i, Integer c) {
                    return b.indexOf(v, i, c);
                }
            },
            new IndexOfTests.PentFunction<ImmutableArrayList.Builder<Integer>, Integer, Integer, Integer, EqualityComparator<? super Integer>, Integer>() {
                @Override
                public Integer apply(ImmutableArrayList.Builder<Integer> b, Integer v, Integer i, Integer c, EqualityComparator<? super Integer> eq) {
                    return b.indexOf(v, i, c, eq);
                }
            }
        );
    }

    @Test
    public void lastIndexOf() {
        IndexOfTests.lastIndexOfTest(
            new Function<Iterable<Integer>, ImmutableArrayList.Builder<Integer>>() {
                @Override
                public ImmutableArrayList.Builder<Integer> apply(Iterable<Integer> sequence) {
                    return (ImmutableArrayList.Builder<Integer>)getIterableOf(sequence, Integer.class);
                }
            },
            new BiFunction<ImmutableArrayList.Builder<Integer>, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableArrayList.Builder<Integer> b, Integer v) {
                    return b.lastIndexOf(v);
                }
            },
            new IndexOfTests.TriFunction<ImmutableArrayList.Builder<Integer>, Integer, EqualityComparator<? super Integer>, Integer>() {
                @Override
                public Integer apply(ImmutableArrayList.Builder<Integer> b, Integer v, EqualityComparator<? super Integer> eq) {
                    //return b.lastIndexOf(v, eq);
                    if (b.isEmpty()) {
                        return b.lastIndexOf(v, 0, 0, eq);
                    } else {
                        return b.lastIndexOf(v, b.size() - 1, b.size(), eq);
                    }
                }
            },
            new IndexOfTests.TriFunction<ImmutableArrayList.Builder<Integer>, Integer, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableArrayList.Builder<Integer> b, Integer v, Integer i) {
                    return b.lastIndexOf(v, i);
                }
            },
            new IndexOfTests.QuadFunction<ImmutableArrayList.Builder<Integer>, Integer, Integer, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableArrayList.Builder<Integer> b, Integer v, Integer i, Integer c) {
                    return b.lastIndexOf(v, i, c);
                }
            },
            new IndexOfTests.PentFunction<ImmutableArrayList.Builder<Integer>, Integer, Integer, Integer, EqualityComparator<? super Integer>, Integer>() {
                @Override
                public Integer apply(ImmutableArrayList.Builder<Integer> b, Integer v, Integer i, Integer c, EqualityComparator<? super Integer> eq) {
                    return b.lastIndexOf(v, i, c, eq);
                }
            }
        );
    }

    @Test
    public void insert() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(1, 2, 3);
        builder.add(1, 4);
        builder.add(4, 5);
        Assert.assertEquals(Arrays.asList(1, 4, 2, 3, 5), builder);
    }

    @Test
    public void insertIndexOutOfBounds1() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(1, 2, 3);
        builder.add(1, 4);
        builder.add(4, 5);
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.add(-1, 0);
    }

    @Test
    public void insertIndexOutOfBounds2() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(1, 2, 3);
        builder.add(1, 4);
        builder.add(4, 5);
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.add(builder.size() + 1, 0);
    }

    @Test
    public void remove() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(1, 2, 3, 4);
        Assert.assertTrue(builder.remove((Integer)1));
        Assert.assertFalse(builder.remove((Integer)6));
        assertEqualSequences(Arrays.asList(2, 3, 4), builder);
        Assert.assertTrue(builder.remove((Integer)3));
        assertEqualSequences(Arrays.asList(2, 4), builder);
        Assert.assertTrue(builder.remove((Integer)4));
        assertEqualSequences(Collections.singletonList(2), builder);
        Assert.assertTrue(builder.remove((Integer)2));
        Assert.assertEquals(0, builder.size());
    }

    @Test
    public void removeAt() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(1, 2, 3, 4);
        builder.remove(0);
        assertEqualSequences(Arrays.asList(2, 3, 4), builder);
        builder.remove(1);
        assertEqualSequences(Arrays.asList(2, 4), builder);
        builder.remove(1);
        assertEqualSequences(Collections.singletonList(2), builder);
        builder.remove(0);
        Assert.assertEquals(0, builder.size());
    }

    @Test
    public void removeAtNegative() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(1, 2, 3, 4);
        builder.remove(0);
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.remove(-1);
    }

    @Test
    public void removeAtSize() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(1, 2, 3, 4);
        builder.remove(0);
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.remove(3);
    }

    @Test
    public void reverseContents() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(1, 2, 3, 4);
        builder.reverse();
        assertEqualSequences(Arrays.asList(4, 3, 2, 1), builder);

        builder.remove(0);
        builder.reverse();
        assertEqualSequences(Arrays.asList(1, 2, 3), builder);

        builder.remove(0);
        builder.reverse();
        assertEqualSequences(Arrays.asList(3, 2), builder);

        builder.remove(0);
        builder.reverse();
        assertEqualSequences(Collections.singletonList(2), builder);

        builder.remove(0);
        builder.reverse();
        assertEqualSequences(Collections.emptyList(), builder);
    }

    @Test
    public void sort() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(2, 4, 1, 3);
        builder.sort();
        assertEqualSequences(Arrays.asList(1, 2, 3, 4), builder);
    }

    @Test
    public void sortNullComparator() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(2, 4, 1, 3);
        builder.sort(null);
        assertEqualSequences(Arrays.asList(1, 2, 3, 4), builder);
    }

    @Test
    public void sortOneElementArray() {
        Integer[] resultantArray = { 4 };

        ImmutableArrayList.Builder<Integer> builder1 = ImmutableArrayList.createBuilder();
        builder1.add(4);
        builder1.sort();
        assertEqualSequences(Arrays.asList(resultantArray), builder1);

        ImmutableArrayList.Builder<Integer> builder2 = ImmutableArrayList.createBuilder();
        builder2.add(4);
        builder2.sort(Comparators.<Integer>defaultComparator());
        assertEqualSequences(Arrays.asList(resultantArray), builder2);

        ImmutableArrayList.Builder<Integer> builder3 = ImmutableArrayList.createBuilder();
        builder3.add(4);
        builder3.sort(0, 1, Comparators.<Integer>defaultComparator());
        assertEqualSequences(Arrays.asList(resultantArray), builder3);
    }

    @Test
    public void sortRange() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(2, 4, 1, 3);

        builder.sort(builder.size(), 0, Comparators.<Integer>defaultComparator());
        assertEqualSequences(Arrays.asList(2, 4, 1, 3), builder);

        builder.sort(1, 2, Comparators.<Integer>defaultComparator());
        assertEqualSequences(Arrays.asList(2, 1, 4, 3), builder);
    }

    @Test
    public void sortRangeIndexNegative() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(2, 4, 1, 3);
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.sort(-1, 2, Comparators.<Integer>defaultComparator());
    }

    @Test
    public void sortRangeCountHigh() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(2, 4, 1, 3);
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.sort(1, 4, Comparators.<Integer>defaultComparator());
    }

    @Test
    public void sortRangeCountNegative() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(2, 4, 1, 3);
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.sort(0, -1, Comparators.<Integer>defaultComparator());
    }

    @Test
    @Ignore("String comparators are not currently working as expected.")
    public void sortComparator() {
        ImmutableArrayList.Builder<String> builder1 = ImmutableArrayList.createBuilder();
        ImmutableArrayList.Builder<String> builder2 = ImmutableArrayList.createBuilder();
        builder1.addAll("c", "B", "a");
        builder2.addAll("c", "B", "a");
        builder1.sort(ordinalIgnoreCaseComparator());
        builder2.sort(ordinalComparator());
        assertEqualSequences(Arrays.asList("a", "B", "c"), builder1);
        assertEqualSequences(Arrays.asList("B", "a", "c"), builder2);
    }

    @Test
    public void count() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(3);

        // Initial count is at zero, which is less than capacity.
        Assert.assertEquals(0, builder.size());

        // Expand the accessible region of the array by increasing the count, but still below capacity.
        builder.resize(2);
        Assert.assertEquals(2, builder.size());
        Assert.assertEquals(2, new ArrayList<Integer>(builder).size());
        Assert.assertEquals(null, builder.get(0));
        Assert.assertEquals(null, builder.get(1));
        try {
            builder.get(2);
            Assert.fail("Expected an exception");
        } catch (IndexOutOfBoundsException ignored) {
        }

        // Expand the accessible region of the array beyond the current capacity.
        builder.resize(4);
        Assert.assertEquals(4, builder.size());
        Assert.assertEquals(4, new ArrayList<Integer>(builder).size());
        Assert.assertEquals(null, builder.get(0));
        Assert.assertEquals(null, builder.get(1));
        Assert.assertEquals(null, builder.get(2));
        Assert.assertEquals(null, builder.get(3));
        try {
            builder.get(4);
            Assert.fail("Expected an exception");
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void countContract() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(100);
        builder.addAll(new Range(1, 100));
        builder.resize(10);
        assertEqualSequences(Arrays.asList(Iterables.toArray(new Range(1, 10), Integer.class)), builder);
        builder.resize(100);
        assertEqualSequences(Arrays.asList(Iterables.toArray(Iterables.concat(new Range(1, 10), Arrays.asList(new Integer[90])), Integer.class)), builder);
    }

    @Test
    public void indexSetter() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();

        builder.resize(1);
        builder.set(0, 2);
        Assert.assertEquals(2, (int)builder.get(0));

        builder.resize(10);
        builder.set(9, 3);
        Assert.assertEquals(3, (int)builder.get(9));

        builder.resize(2);
        Assert.assertEquals(2, (int)builder.get(0));
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.get(2);
    }

    @Test
    public void indexSetterEmptyIndexZero() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.set(0, 1);
    }

    @Test
    public void indexSetterIndexNegative() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.set(-1, 1);
    }

    @Test
    public void toImmutable() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder();
        builder.addAll(1, 2, 3);

        ImmutableArrayList<Integer> array = builder.toImmutable();
        Assert.assertEquals(1, (int)array.get(0));
        Assert.assertEquals(2, (int)array.get(1));
        Assert.assertEquals(3, (int)array.get(2));

        // Make sure that subsequent mutation doesn't impact the immutable array.
        builder.set(1, 5);
        Assert.assertEquals(5, (int)builder.get(1));
        Assert.assertEquals(2, (int)array.get(1));

        builder.clear();
        Assert.assertTrue(builder.toImmutable().isEmpty());
    }

    @Test
    public void clear() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(2);
        builder.add(1);
        builder.add(1);
        builder.clear();
        Assert.assertEquals(0, builder.size());
        thrown.expect(instanceOf(IndexOutOfBoundsException.class));
        builder.get(0);
    }

    @Test
    public void mutationsSucceedAfterToImmutable() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(1);
        builder.add(1);
        ImmutableArrayList<Integer> immutable = builder.toImmutable();
        builder.set(0, 0);
        Assert.assertEquals(0, (int)builder.get(0));
        Assert.assertEquals(1, (int)immutable.get(0));
    }

    @Test
    public void iterator() {
        ImmutableArrayList.Builder<Integer> empty = ImmutableArrayList.createBuilder(0);
        Iterator<Integer> enumerator = empty.iterator();
        Assert.assertFalse(enumerator.hasNext());

        ImmutableArrayList.Builder<Integer> manyElements = ImmutableArrayList.createBuilder(3);
        manyElements.addAll(1, 2, 3);
        enumerator = manyElements.iterator();

        Assert.assertTrue(enumerator.hasNext());
        Assert.assertEquals(1, (int)enumerator.next());
        Assert.assertTrue(enumerator.hasNext());
        Assert.assertEquals(2, (int)enumerator.next());
        Assert.assertTrue(enumerator.hasNext());
        Assert.assertEquals(3, (int)enumerator.next());

        Assert.assertFalse(enumerator.hasNext());
    }

    @Test
    public void moveToImmutableNormal() {
        ImmutableArrayList.Builder<String> builder = createBuilderWithCount(2);
        Assert.assertEquals(2, builder.size());
        Assert.assertEquals(2, builder.getCapacity());
        builder.set(1, "b");
        builder.set(0, "a");
        ImmutableArrayList<String> array = builder.moveToImmutable();
        assertEqualSequences(Arrays.asList("a", "b"), array);
        Assert.assertEquals(0, builder.size());
        Assert.assertEquals(0, builder.getCapacity());
    }

    @Test
    public void moveToImmutableRepeat() {
        ImmutableArrayList.Builder<String> builder = createBuilderWithCount(2);
        builder.set(0, "a");
        builder.set(1, "b");
        ImmutableArrayList<String> array1 = builder.moveToImmutable();
        ImmutableArrayList<String> array2 = builder.moveToImmutable();
        assertEqualSequences(Arrays.asList("a", "b"), array1);
        Assert.assertEquals(0, array2.size());
    }

    @Test
    public void moveToImmutablePartialFill() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(4);
        builder.add(42);
        builder.add(13);
        Assert.assertEquals(4, builder.getCapacity());
        Assert.assertEquals(2, builder.size());
        thrown.expect(IllegalStateException.class);
        builder.moveToImmutable();
    }

    @Test
    public void moveToImmutablePartialFillWithCountUpdate() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(4);
        builder.add(42);
        builder.add(13);
        Assert.assertEquals(4, builder.getCapacity());
        Assert.assertEquals(2, builder.size());
        builder.resize(builder.getCapacity());
        ImmutableArrayList<Integer> array = builder.moveToImmutable();
        assertEqualSequences(Arrays.asList(42, 13, null, null), array);
    }

    @Test
    public void moveToImmutableThenUse() {
        ImmutableArrayList.Builder<String> builder = createBuilderWithCount(2);
        Assert.assertEquals(2, builder.moveToImmutable().size());
        Assert.assertEquals(0, builder.getCapacity());
        builder.add("a");
        builder.add("b");
        Assert.assertEquals(2, builder.size());
        Assert.assertTrue(builder.getCapacity() >= 2);
        assertEqualSequences(Arrays.asList("a", "b"), builder.moveToImmutable());
    }

    @Test
    public void moveToImmutableAfterClear() {
        ImmutableArrayList.Builder<String> builder = createBuilderWithCount(2);
        builder.set(0, "a");
        builder.set(1, "b");
        builder.clear();
        thrown.expect(instanceOf(IllegalStateException.class));
        builder.moveToImmutable();
    }

    @Test
    public void moveToImmutableAddToCapacity() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(3);
        for (int i = 0; i < builder.getCapacity(); i++) {
            builder.add(i);
        }

        assertEqualSequences(Arrays.asList(0, 1, 2), builder.moveToImmutable());
    }

    @Test
    public void moveToImmutableInsertToCapacity() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(3);
        for (int i = 0; i < builder.getCapacity(); i++) {
            builder.add(i, i);
        }

        assertEqualSequences(Arrays.asList(0, 1, 2), builder.moveToImmutable());
    }

    @Test
    public void moveToImmutableAddRangeToCapacity() {
        Integer[] array = { 1, 2, 3, 4, 5 };
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(array.length);
        builder.addAll(array);
        assertEqualSequences(Arrays.asList(array), builder.moveToImmutable());
    }

    @Test
    public void moveToImmutableAddRemoveAddToCapacity() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(3);
        for (int i = 0; i < builder.getCapacity(); i++) {
            builder.add(i);
            builder.remove(i);
            builder.add(i);
        }

        assertEqualSequences(Arrays.asList(0, 1, 2), builder.moveToImmutable());
    }

    @Test
    public void capacitySetToZero() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(10);
        builder.setCapacity(0);
        Assert.assertEquals(0, builder.getCapacity());
        Assert.assertArrayEquals(new Integer[] {}, builder.toArray());
    }

    @Test
    public void capacitySetToLessThanCount() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(10);
        builder.add(1);
        builder.add(1);
        thrown.expect(instanceOf(IllegalArgumentException.class));
        builder.setCapacity(1);
    }

    @Test
    public void capacitySetToCount() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(10);
        builder.add(1);
        builder.add(2);
        builder.setCapacity(builder.size());
        Assert.assertEquals(2, builder.getCapacity());
        Assert.assertArrayEquals(new Integer[] { 1, 2 }, builder.toArray());
    }

    @Test
    public void capacitySetToCapacity() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(10);
        builder.add(1);
        builder.add(2);
        builder.setCapacity(builder.getCapacity());
        Assert.assertEquals(10, builder.getCapacity());
        Assert.assertArrayEquals(new Integer[] { 1, 2 }, builder.toArray());
    }

    @Test
    public void capacitySetToBiggerCapacity() {
        ImmutableArrayList.Builder<Integer> builder = ImmutableArrayList.createBuilder(10);
        builder.add(1);
        builder.add(2);
        builder.setCapacity(20);
        Assert.assertEquals(20, builder.getCapacity());
        Assert.assertEquals(2, builder.size());
        Assert.assertArrayEquals(new Integer[] { 1, 2 }, builder.toArray());
    }

    private static <T> ImmutableArrayList.Builder<T> createBuilderWithCount(int count) {
        ImmutableArrayList.Builder<T> builder = ImmutableArrayList.createBuilder(count);
        builder.resize(count);
        return builder;
    }

    @Override
    protected <T> Iterable<T> getIterableOf(T... contents) {
        ImmutableArrayList.Builder<T> builder = ImmutableArrayList.createBuilder(contents.length);
        builder.addAll(Arrays.asList(contents));
        return builder;
    }
}
