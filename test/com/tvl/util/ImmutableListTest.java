// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tvl.util.function.BiFunction;
import com.tvl.util.function.Function;
import com.tvl.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Random;

public class ImmutableListTest extends ImmutableListTestBase {
    private enum Operation
    {
        ADD,
        ADD_RANGE,
        INSERT,
        INSERT_RANGE,
        REMOVE_AT,
        REMOVE_RANGE,
        LAST,
    }

    @Test
    public void randomOperationsTest() {
        int operationCount = getRandomOperationsCount();
        ArrayList<Integer> expected = new ArrayList<Integer>();
        ImmutableTreeList<Integer> actual = ImmutableTreeList.empty();

        long seed = System.nanoTime();
        System.err.println("Using random seed " + seed);
        Random random = new Random(seed);

        for (int iOp = 0; iOp < operationCount; iOp++) {
            switch (Operation.values()[random.nextInt(Operation.LAST.ordinal())]) {
            case ADD:
                int value = random.nextInt();
                System.err.format("Adding \"%s\" to the list.%n", value);
                expected.add(value);
                actual = actual.add(value);
                verifyBalanced(actual);
                break;
            case ADD_RANGE:
                int inputLength = random.nextInt(100);
                Integer[] values = new Integer[inputLength];
                for (int i = 0; i < inputLength; i++) {
                    values[i] = random.nextInt();
                }
                System.err.format("Adding %s elements to the list.%n", inputLength);
                expected.addAll(Arrays.asList(values));
                actual = actual.addAll(Arrays.asList(values));
                verifyBalanced(actual);
                break;
            case INSERT:
                int position = random.nextInt(expected.size() + 1);
                value = random.nextInt();
                System.err.format("Adding \"%s\" to position %s in the list.%n", value, position);
                expected.add(position, value);
                actual = actual.add(position, value);
                verifyBalanced(actual);
                break;
            case INSERT_RANGE:
                inputLength = random.nextInt(100);
                values = new Integer[inputLength];
                for (int i = 0; i < inputLength; i++) {
                    values[i] = random.nextInt();
                }
                position = random.nextInt(expected.size() + 1);
                System.err.format("Adding %s elements to position %s in the list.%n", inputLength, position);
                expected.addAll(position, Arrays.asList(values));
                actual = actual.addAll(position, Arrays.asList(values));
                verifyBalanced(actual);
                break;
            case REMOVE_AT:
                if (!expected.isEmpty()) {
                    position = random.nextInt(expected.size());
                    System.err.format("Removing element at position %s from the list.%n", position);
                    expected.remove(position);
                    actual = actual.remove(position);
                    verifyBalanced(actual);
                }

                break;
            case REMOVE_RANGE:
                if (!expected.isEmpty()) {
                    position = random.nextInt(expected.size());
                    inputLength = random.nextInt(expected.size() - position);
                    System.err.format("Removing %s elements starting at position %s from the list.%n", inputLength, position);
                    expected.subList(position, position + inputLength).clear();
                    actual = actual.removeAll(position, position + inputLength);
                    verifyBalanced(actual);
                }

                break;
            }

            assertEqualSequences(expected, actual);
        }
    }

    @Test
    public void emptyTest() {
        ImmutableTreeList<GenericParameterHelper> empty = ImmutableTreeList.empty();
        Assert.assertSame(empty, ImmutableTreeList.empty());
        Assert.assertSame(empty, empty.clear());
        Assert.assertTrue(empty.isEmpty());
        Assert.assertEquals(0, empty.size());
        Assert.assertEquals(-1, empty.indexOf(new GenericParameterHelper()));
        Assert.assertEquals(-1, empty.indexOf(null));
    }

    @Test
    public void hashCodeVariesByInstance() {
        Assert.assertNotEquals(ImmutableTreeList.create().hashCode(), ImmutableTreeList.create(5).hashCode());
    }

    @Test
    public void addAndIndexerTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.empty();
        for (int i = 1; i <= 10; i++) {
            list = list.add(i * 10);
            Assert.assertFalse(list.isEmpty());
            Assert.assertEquals(i, list.size());
        }

        for (int i = 1; i <= 10; i++) {
            Assert.assertEquals(i * 10, (int)list.get(i - 1));
        }

        ImmutableTreeList<Integer> bulkList = ImmutableTreeList.<Integer>empty().addAll(Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100));
        assertEqualSequences(list, bulkList);
    }

    @Test
    public void addAllTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.empty();
        list = list.addAll(Arrays.asList(1, 2, 3));
        list = list.addAll(new Range(4, 2));
        list = list.addAll(ImmutableTreeList.<Integer>empty().addAll(Arrays.asList(6, 7, 8)));
        list = list.addAll(Arrays.asList(new Integer[0]));
        list = list.addAll(ImmutableTreeList.<Integer>empty().addAll(new Range(9, 1000)));
        assertEqualSequences(new Range(1, 1008), list);
    }

    @Test
    public void addAllOptimizationsTest() {
        // All these optimizations are tested based on filling an empty list.
        ImmutableTreeList<String> emptyList = ImmutableTreeList.empty();

        // Adding an empty list to an empty list should yield the original list.
        Assert.assertSame(emptyList, emptyList.addAll(Arrays.asList(new String[0])));

        // Adding a non-empty immutable list to an empty one should return the added list.
        ImmutableTreeList<String> nonEmptyListDefaultComparator = ImmutableTreeList.create("5");
        Assert.assertSame(nonEmptyListDefaultComparator, emptyList.addAll(nonEmptyListDefaultComparator));

        // Adding a Builder instance to an empty list should be seen through.
        ImmutableTreeList.Builder<String> builderOfNonEmptyListDefaultComparator = nonEmptyListDefaultComparator.toBuilder();
        Assert.assertSame(nonEmptyListDefaultComparator, emptyList.addAll(builderOfNonEmptyListDefaultComparator));
    }

    @Test
    public void addAllBalanceTest() {
        long randSeed = System.nanoTime();
        System.err.format("Random seed: %s%n", randSeed);
        Random random = new Random(randSeed);

        int expectedTotalSize = 0;

        ImmutableTreeList<Integer> list = ImmutableTreeList.empty();

        // Add some small batches, verifying balance after each
        for (int i = 0; i < 128; i++) {
            int batchSize = random.nextInt(32);
            System.err.format("Adding %s elements to the list%n", batchSize);
            list = list.addAll(new Range(expectedTotalSize + 1, batchSize));
            verifyBalanced(list);
            expectedTotalSize += batchSize;
        }

        // Add a single large batch to the end
        int largeBatchSize = random.nextInt(32768) + 32768;
        System.err.format("Adding %s elements to the list%n", largeBatchSize);
        list = list.addAll(new Range(expectedTotalSize + 1, largeBatchSize));
        verifyBalanced(list);
        expectedTotalSize += largeBatchSize;

        assertEqualSequences(new Range(1, expectedTotalSize), list);

        TestExtensionMethods.verifyHeightIsWithinTolerance(list.getRoot());
    }

    @Test
    public void insertAllRandomBalanceTest() {
        long randSeed = System.nanoTime();
        System.err.format("Random seed: %s%n", randSeed);
        Random random = new Random(randSeed);

        ImmutableTreeList<Integer> immutableList = ImmutableTreeList.create();
        ArrayList<Integer> list = new ArrayList<Integer>();

        final int maxBatchSize = 32;
        int valueCounter = 0;
        for (int i = 0; i < 24; i++) {
            int startPosition = random.nextInt(list.size() + 1);
            int length = random.nextInt(maxBatchSize + 1);
            Integer[] values = new Integer[length];
            for (int j = 0; j < length; j++) {
                values[j] = ++valueCounter;
            }

            immutableList = immutableList.addAll(startPosition, Arrays.asList(values));
            list.addAll(startPosition, Arrays.asList(values));

            assertEqualSequences(list, immutableList);
            TestExtensionMethods.verifyBalanced(immutableList.getRoot());
        }

        TestExtensionMethods.verifyHeightIsWithinTolerance(immutableList.getRoot());
    }

    @Test
    public void insertTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.empty();

        try {
            list.add(1, 5);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.add(-1, 5);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        list = list.add(0, 10);
        list = list.add(1, 20);
        list = list.add(2, 30);

        list = list.add(2, 25);
        list = list.add(1, 15);
        list = list.add(0, 5);

        Assert.assertEquals(6, list.size());
        Integer[] expectedList = { 5, 10, 15, 20, 25, 30 };
        Integer[] actualList = Iterables.toArray(list, Integer.class);
        Assert.assertArrayEquals(expectedList, actualList);

        try {
            list.add(7, 5);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.add(-1, 5);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void insertBalanceTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.create(1);

        list = list.add(0, 2);
        list = list.add(1, 3);

        verifyBalanced(list);
    }

    @Test
    public void insertAllTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.empty();

        try {
            list.addAll(1, Collections.singletonList(1));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.addAll(-1, Collections.singletonList(1));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        list = list.addAll(0, Arrays.asList(1, 4, 5));
        list = list.addAll(1, Arrays.asList(2, 3));
        list = list.addAll(2, Arrays.asList(new Integer[0]));
        assertEqualSequences(new Range(1, 5), list);


        try {
            list.addAll(6, Collections.singletonList(1));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.addAll(-1, Collections.singletonList(1));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void insertAllImmutableTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.empty();
        ImmutableTreeList<Integer> nonEmptyList = ImmutableTreeList.create(1);

        try {
            list.addAll(1, nonEmptyList);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.addAll(-1, nonEmptyList);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        list = list.addAll(0, ImmutableTreeList.create(1, 104, 105));
        list = list.addAll(1, ImmutableTreeList.create(2, 3));
        list = list.addAll(2, ImmutableTreeList.<Integer>empty());
        list = list.addAll(3, ImmutableTreeList.<Integer>empty().addAll(0, new Range(4, 100)));
        assertEqualSequences(new Range(1, 105), list);

        try {
            list.addAll(106, nonEmptyList);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.addAll(-1, nonEmptyList);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void nullHandlingTest() {
        ImmutableTreeList<GenericParameterHelper> list = ImmutableTreeList.empty();
        Assert.assertFalse(list.contains(null));
        Assert.assertEquals(-1, list.indexOf(null));

        list = list.add(null);
        Assert.assertEquals(1, list.size());
        Assert.assertNull(list.get(0));
        Assert.assertTrue(list.contains(null));
        Assert.assertEquals(0, list.indexOf(null));

        list = list.remove(null);
        Assert.assertEquals(0, list.size());
        Assert.assertTrue(list.isEmpty());
        Assert.assertFalse(list.contains(null));
        Assert.assertEquals(-1, list.indexOf(null));
    }

    @Test
    public void removeTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.empty();
        for (int i = 1; i <= 10; i++) {
            list = list.add(i * 10);
        }

        list = list.remove((Integer)30);
        Assert.assertEquals(9, list.size());
        Assert.assertFalse(list.contains(30));

        list = list.remove((Integer)100);
        Assert.assertEquals(8, list.size());
        Assert.assertFalse(list.contains(100));

        list = list.remove((Integer)10);
        Assert.assertEquals(7, list.size());
        Assert.assertFalse(list.contains(10));

        final Integer[] removeList = { 20, 70 };
        list = list.removeIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return Arrays.asList(removeList).contains(value);
            }
        });
        Assert.assertEquals(5, list.size());
        Assert.assertFalse(list.contains(20));
        Assert.assertFalse(list.contains(70));

        ImmutableList<Integer> list2 = ImmutableTreeList.empty();
        for (int i = 1; i <= 10; i++) {
            list2 = list2.add(i * 10);
        }

        list2 = list2.remove(30, EqualityComparators.defaultComparator());
        Assert.assertEquals(9, list2.size());
        Assert.assertFalse(Iterables.contains(list2, 30));

        list2 = list2.remove(100, EqualityComparators.defaultComparator());
        Assert.assertEquals(8, list2.size());
        Assert.assertFalse(Iterables.contains(list2, 100));

        list2 = list2.remove(10, EqualityComparators.defaultComparator());
        Assert.assertEquals(7, list2.size());
        Assert.assertFalse(Iterables.contains(list2, 10));

        list2 = list2.removeIf(new Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return Arrays.asList(removeList).contains(value);
            }
        });
        Assert.assertEquals(5, list2.size());
        Assert.assertFalse(Iterables.contains(list2, 20));
        Assert.assertFalse(Iterables.contains(list2, 70));
    }

    @Test
    public void removeNonExistentKeepsReference() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.empty();
        Assert.assertSame(list, list.remove((Integer)3));
    }

    @Test
    public void removeAtTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.empty();

        try {
            list.remove(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.remove(-1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.remove(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        for (int i = 1; i <= 10; i++) {
            list = list.add(i * 10);
        }

        list = list.remove(2);
        Assert.assertEquals(9, list.size());
        Assert.assertFalse(list.contains(30));

        list = list.remove(8);
        Assert.assertEquals(8, list.size());
        Assert.assertFalse(list.contains(100));

        list = list.remove(0);
        Assert.assertEquals(7, list.size());
        Assert.assertFalse(list.contains(10));
    }

    @Test
    public void indexOfAndContainsTest() {
        ArrayList<String> expectedList = new ArrayList<String>(Arrays.asList("Microsoft", "Windows", "Bing", "Visual Studio", "Comics", "Computers", "Laptops"));

        ImmutableTreeList<String> list = ImmutableTreeList.empty();
        for (String newElement : expectedList) {
            Assert.assertFalse(list.contains(newElement));
            list = list.add(newElement);
            Assert.assertTrue(list.contains(newElement));
            Assert.assertEquals(expectedList.indexOf(newElement), list.indexOf(newElement));
            Assert.assertEquals(expectedList.indexOf(newElement), list.indexOf(newElement.toUpperCase(), 0, list.size(), ordinalIgnoreCaseComparator()));
            Assert.assertEquals(-1, list.indexOf(newElement.toUpperCase()));

            for (String existingElement : expectedList) {
                if (EqualityComparators.defaultComparator().equals(existingElement, newElement)) {
                    break;
                }

                Assert.assertTrue(list.contains(existingElement));
                Assert.assertEquals(expectedList.indexOf(existingElement), list.indexOf(existingElement));
                Assert.assertEquals(expectedList.indexOf(existingElement), list.indexOf(existingElement.toUpperCase(), 0, list.size(), ordinalIgnoreCaseComparator()));
                Assert.assertEquals(-1, list.indexOf(existingElement.toUpperCase()));
            }
        }
    }

    @Test
    public void indexer() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.createAll(new Range(1, 3));
        Assert.assertEquals(1, (int)list.get(0));
        Assert.assertEquals(2, (int)list.get(1));
        Assert.assertEquals(3, (int)list.get(2));

        try {
            list.get(3);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.get(-1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void indexOf() {
        IndexOfTests.indexOfTest(
            new Function<Iterable<Integer>, ImmutableTreeList<Integer>>() {
                @Override
                public ImmutableTreeList<Integer> apply(Iterable<Integer> sequence) {
                    return ImmutableTreeList.createAll(sequence);
                }
            },
            new BiFunction<ImmutableTreeList<Integer>, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableTreeList<Integer> b, Integer v) {
                    return b.indexOf(v);
                }
            },
            new IndexOfTests.TriFunction<ImmutableTreeList<Integer>, Integer, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableTreeList<Integer> b, Integer v, Integer i) {
                    return b.indexOf(v, i);
                }
            },
            new IndexOfTests.QuadFunction<ImmutableTreeList<Integer>, Integer, Integer, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableTreeList<Integer> b, Integer v, Integer i, Integer c) {
                    return b.indexOf(v, i, i + c);
                }
            },
            new IndexOfTests.PentFunction<ImmutableTreeList<Integer>, Integer, Integer, Integer, EqualityComparator<? super Integer>, Integer>() {
                @Override
                public Integer apply(ImmutableTreeList<Integer> b, Integer v, Integer i, Integer c, EqualityComparator<? super Integer> eq) {
                    return b.indexOf(v, i, i + c, eq);
                }
            }
        );
        IndexOfTests.indexOfTest(
            new Function<Iterable<Integer>, AbstractImmutableList<Integer>>() {
                @Override
                public AbstractImmutableList<Integer> apply(Iterable<Integer> sequence) {
                    return ImmutableTreeList.createAll(sequence);
                }
            },
            new BiFunction<AbstractImmutableList<Integer>, Integer, Integer>() {
                @Override
                public Integer apply(AbstractImmutableList<Integer> b, Integer v) {
                    return b.indexOf(v);
                }
            },
            new IndexOfTests.TriFunction<AbstractImmutableList<Integer>, Integer, Integer, Integer>() {
                @Override
                public Integer apply(AbstractImmutableList<Integer> b, Integer v, Integer i) {
                    return b.indexOf(v, i);
                }
            },
            new IndexOfTests.QuadFunction<AbstractImmutableList<Integer>, Integer, Integer, Integer, Integer>() {
                @Override
                public Integer apply(AbstractImmutableList<Integer> b, Integer v, Integer i, Integer c) {
                    return b.indexOf(v, i, i + c);
                }
            },
            new IndexOfTests.PentFunction<AbstractImmutableList<Integer>, Integer, Integer, Integer, EqualityComparator<? super Integer>, Integer>() {
                @Override
                public Integer apply(AbstractImmutableList<Integer> b, Integer v, Integer i, Integer c, EqualityComparator<? super Integer> eq) {
                    return b.indexOf(v, i, i + c, eq);
                }
            }
        );
    }

    @Test
    public void lastIndexOf() {
        IndexOfTests.lastIndexOfTest(
            new Function<Iterable<Integer>, ImmutableTreeList<Integer>>() {
                @Override
                public ImmutableTreeList<Integer> apply(Iterable<Integer> sequence) {
                    return ImmutableTreeList.createAll(sequence);
                }
            },
            new BiFunction<ImmutableTreeList<Integer>, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableTreeList<Integer> b, Integer v) {
                    return b.lastIndexOf(v);
                }
            },
            new IndexOfTests.TriFunction<ImmutableTreeList<Integer>, Integer, EqualityComparator<? super Integer>, Integer>() {
                @Override
                public Integer apply(ImmutableTreeList<Integer> b, Integer v, EqualityComparator<? super Integer> eq) {
                    return b.lastIndexOf(v, eq);
                }
            },
            new IndexOfTests.TriFunction<ImmutableTreeList<Integer>, Integer, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableTreeList<Integer> b, Integer v, Integer f) {
                    return b.lastIndexOf(v, f);
                }
            },
            new IndexOfTests.QuadFunction<ImmutableTreeList<Integer>, Integer, Integer, Integer, Integer>() {
                @Override
                public Integer apply(ImmutableTreeList<Integer> b, Integer v, Integer f, Integer t) {
                    return b.lastIndexOf(v, f, t);
                }
            },
            new IndexOfTests.PentFunction<ImmutableTreeList<Integer>, Integer, Integer, Integer, EqualityComparator<? super Integer>, Integer>() {
                @Override
                public Integer apply(ImmutableTreeList<Integer> b, Integer v, Integer f, Integer t, EqualityComparator<? super Integer> eq) {
                    return b.lastIndexOf(v, f, t, eq);
                }
            }
        );
        IndexOfTests.lastIndexOfTest(
            new Function<Iterable<Integer>, AbstractImmutableList<Integer>>() {
                @Override
                public AbstractImmutableList<Integer> apply(Iterable<Integer> sequence) {
                    return ImmutableTreeList.createAll(sequence);
                }
            },
            new BiFunction<AbstractImmutableList<Integer>, Integer, Integer>() {
                @Override
                public Integer apply(AbstractImmutableList<Integer> b, Integer v) {
                    return b.lastIndexOf(v);
                }
            },
            new IndexOfTests.TriFunction<AbstractImmutableList<Integer>, Integer, EqualityComparator<? super Integer>, Integer>() {
                @Override
                public Integer apply(AbstractImmutableList<Integer> b, Integer v, EqualityComparator<? super Integer> eq) {
                    return b.lastIndexOf(v, eq);
                }
            },
            new IndexOfTests.TriFunction<AbstractImmutableList<Integer>, Integer, Integer, Integer>() {
                @Override
                public Integer apply(AbstractImmutableList<Integer> b, Integer v, Integer f) {
                    return b.lastIndexOf(v, f);
                }
            },
            new IndexOfTests.QuadFunction<AbstractImmutableList<Integer>, Integer, Integer, Integer, Integer>() {
                @Override
                public Integer apply(AbstractImmutableList<Integer> b, Integer v, Integer f, Integer t) {
                    return b.lastIndexOf(v, f, t);
                }
            },
            new IndexOfTests.PentFunction<AbstractImmutableList<Integer>, Integer, Integer, Integer, EqualityComparator<? super Integer>, Integer>() {
                @Override
                public Integer apply(AbstractImmutableList<Integer> b, Integer v, Integer f, Integer t, EqualityComparator<? super Integer> eq) {
                    return b.lastIndexOf(v, f, t, eq);
                }
            }
        );
    }

    @Test
    public void replaceTest() {
        // Verify replace at beginning, middle, and end.
        ImmutableTreeList<Integer> list = ImmutableTreeList.<Integer>empty().add(3).add(5).add(8);
        assertEqualSequences(Arrays.asList(4, 5, 8), list.replace(3, 4));
        assertEqualSequences(Arrays.asList(3, 6, 8), list.replace(5, 6));
        assertEqualSequences(Arrays.asList(3, 5, 9), list.replace(8, 9));

        // Verify replacement of first element when there are duplicates.
        list = ImmutableTreeList.<Integer>empty().add(3).add(3).add(5);
        assertEqualSequences(Arrays.asList(4, 3, 5), list.replace(3, 4));
        assertEqualSequences(Arrays.asList(4, 4, 5), list.replace(3, 4).replace(3, 4));
    }

    @Test
    public void replaceWithEqualityComparatorTest() {
        ImmutableTreeList<Person> list = ImmutableTreeList.create(new Person("Andrew", 20));
        Person newAge = new Person("Andrew", 21);
        ImmutableTreeList<Person> updatedList = list.replace(newAge, newAge, new NameOnlyEqualityComparator());
        Assert.assertEquals(newAge.getAge(), updatedList.get(0).getAge());
    }

    @Test
    public void replaceMissingThrowsTest() {
        thrown.expect(IllegalArgumentException.class);
        ImmutableTreeList.<Integer>empty().replace(5, 3);
    }

    @SuppressWarnings({ "EqualsBetweenInconvertibleTypes", "ObjectEqualsNull", "EqualsWithItself" })
    @Test
    public void equalsTest() {
        Assert.assertFalse(ImmutableTreeList.<Integer>empty().equals(null));
        Assert.assertFalse(ImmutableTreeList.<Integer>empty().equals("hi"));
        Assert.assertTrue(ImmutableTreeList.<Integer>empty().equals(ImmutableTreeList.<Integer>empty()));
        Assert.assertFalse(ImmutableTreeList.<Integer>empty().add(3).equals(ImmutableTreeList.<Integer>empty().add(3)));
    }

    @Test
    public void create() {
        ImmutableTreeList<String> list = ImmutableTreeList.create();
        Assert.assertEquals(0, list.size());

        list = ImmutableTreeList.create("a");
        Assert.assertEquals(1, list.size());

        list = ImmutableTreeList.create("a", "b");
        Assert.assertEquals(2, list.size());

        list = ImmutableTreeList.createAll(Arrays.asList("a", "b"));
        Assert.assertEquals(2, list.size());
    }

//[Fact]
//public void ToImmutableList()
//{
//    ImmutableList<string> list = new[] { "a", "b" }.ToImmutableList();
//    Assert.Equal(2, list.Count);

//    list = new[] { "a", "b" }.ToImmutableList();
//    Assert.Equal(2, list.Count);
//}

//[Fact]
//public void ToImmutableListOfSameType()
//{
//    var list = ImmutableList.Create("a");
//    Assert.Same(list, list.ToImmutableList());
//}

    @Test
    public void removeAllNullTest() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("items");
        ImmutableTreeList.empty().removeAll(null);
    }

    @Test
    public void removeAllArrayTest() {
        ImmutableTreeList<Integer> list = ImmutableTreeList.create(1, 2, 3);
        try {
            list.removeAll(-1, -1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.removeAll(0, -1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.removeAll(4, 4);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.removeAll(0, 4);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            list.removeAll(2, 4);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        list.removeAll(3, 3);
        Assert.assertEquals(3, list.size());
    }

//[Fact]
//public void RemoveRangeEnumerableTest()
//{
//    var list = ImmutableList.Create(1, 2, 3);
//    Assert.Throws<ArgumentNullException>(() => list.RemoveRange(null));

//    ImmutableList<int> removed2 = list.RemoveRange(new[] { 2 });
//    Assert.Equal(2, removed2.Count);
//    Assert.Equal(new[] { 1, 3 }, removed2);

//    ImmutableList<int> removed13 = list.RemoveRange(new[] { 1, 3, 5 });
//    Assert.Equal(1, removed13.Count);
//    Assert.Equal(new[] { 2 }, removed13);
//    Assert.Equal(new[] { 2 }, ((IImmutableList<int>)list).RemoveRange(new[] { 1, 3, 5 }));

//    Assert.Same(list, list.RemoveRange(new[] { 5 }));
//    Assert.Same(ImmutableList.Create<int>(), ImmutableList.Create<int>().RemoveRange(new[] { 1 }));

//    var listWithDuplicates = ImmutableList.Create(1, 2, 2, 3);
//    Assert.Equal(new[] { 1, 2, 3 }, listWithDuplicates.RemoveRange(new[] { 2 }));
//    Assert.Equal(new[] { 1, 3 }, listWithDuplicates.RemoveRange(new[] { 2, 2 }));

//    Assert.Throws<ArgumentNullException>(() => ((IImmutableList<int>)ImmutableList.Create(1, 2, 3)).RemoveRange(null));
//    Assert.Equal(new[] { 1, 3 }, ((IImmutableList<int>)ImmutableList.Create(1, 2, 3)).RemoveRange(new[] { 2 }));
//}

//[Fact]
//public void EnumeratorTest()
//{
//    var list = ImmutableList.Create("a");
//    var enumerator = list.GetEnumerator();
//    Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//    Assert.True(enumerator.MoveNext());
//    Assert.Equal("a", enumerator.Current);
//    Assert.False(enumerator.MoveNext());
//    Assert.Throws<InvalidOperationException>(() => enumerator.Current);

//    enumerator.Reset();
//    Assert.Throws<InvalidOperationException>(() => enumerator.Current);
//    Assert.True(enumerator.MoveNext());
//    Assert.Equal("a", enumerator.Current);
//    Assert.False(enumerator.MoveNext());
//    Assert.Throws<InvalidOperationException>(() => enumerator.Current);

//    enumerator.Dispose();
//    Assert.Throws<ObjectDisposedException>(() => enumerator.Reset());
//}

//[Fact]
//public void EnumeratorRecyclingMisuse()
//{
//    var collection = ImmutableList.Create(1);
//    var enumerator = collection.GetEnumerator();
//    var enumeratorCopy = enumerator;
//    Assert.True(enumerator.MoveNext());
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
//    Assert.Equal(collection[0], enumerator.Current);
//    enumerator.Dispose();
//}

    @Test
    public void reverseTest2() {
        ImmutableTreeList<Integer> emptyList = ImmutableTreeList.create();
        Assert.assertSame(emptyList, emptyList.reverse());

        ImmutableTreeList<Integer> populatedList = ImmutableTreeList.create(3, 2, 1);
        assertEqualSequences(Arrays.asList(1, 2, 3), populatedList.reverse());
    }

    @Test
    public void setItem() {
        ImmutableTreeList<Integer> emptyList = ImmutableTreeList.create();
        try {
            emptyList.get(-1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            emptyList.get(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            emptyList.get(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        ImmutableTreeList<Integer> listOfOne = emptyList.add(5);
        try {
            listOfOne.get(-1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        Assert.assertEquals(5, (int)listOfOne.get(0));

        try {
            listOfOne.get(1);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

//[Fact]
//public void IListIsReadOnly()
//{
//    IList list = ImmutableList.Create<int>();
//    Assert.True(list.IsReadOnly);
//    Assert.True(list.IsFixedSize);
//    Assert.Throws<NotSupportedException>(() => list.Add(1));
//    Assert.Throws<NotSupportedException>(() => list.Clear());
//    Assert.Throws<NotSupportedException>(() => list.Insert(0, 1));
//    Assert.Throws<NotSupportedException>(() => list.Remove(1));
//    Assert.Throws<NotSupportedException>(() => list.RemoveAt(0));
//    Assert.Throws<NotSupportedException>(() => list[0] = 1);
//}

//[Fact]
//public void IListOfTIsReadOnly()
//{
//    IList<int> list = ImmutableList.Create<int>();
//    Assert.True(list.IsReadOnly);
//    Assert.Throws<NotSupportedException>(() => list.Add(1));
//    Assert.Throws<NotSupportedException>(() => list.Clear());
//    Assert.Throws<NotSupportedException>(() => list.Insert(0, 1));
//    Assert.Throws<NotSupportedException>(() => list.Remove(1));
//    Assert.Throws<NotSupportedException>(() => list.RemoveAt(0));
//    Assert.Throws<NotSupportedException>(() => list[0] = 1);
//}

//[Fact]
//public void DebuggerAttributesValid()
//{
//    DebuggerAttributes.ValidateDebuggerDisplayReferences(ImmutableList.Create<int>());
//    DebuggerAttributes.ValidateDebuggerTypeProxyProperties(ImmutableList.Create<double>(1, 2, 3));

//    object rootNode = DebuggerAttributes.GetFieldValue(ImmutableList.Create<string>("1", "2", "3"), "_root");
//    DebuggerAttributes.ValidateDebuggerDisplayReferences(rootNode);
//}

    @Override
    protected <T> Iterable<T> getIterableOf(T... contents) {
        return ImmutableTreeList.<T>empty().addAll(Arrays.asList(contents));
    }

    @Override
    protected <T> void removeAllTestHelper(ImmutableTreeList<T> list, Predicate<? super T> test) {
        ArrayList<T> expected = new ArrayList<T>(list.toBuilder());
        for (ListIterator<T> it = expected.listIterator(); it.hasNext(); ) {
            T element = it.next();
            if (test.test(element)) {
                it.remove();
            }
        }

        ImmutableTreeList<T> actual = list.removeIf(test);
        assertEqualSequences(expected, new ArrayList<T>(actual.toBuilder()));
    }

    @Override
    protected <T> void reverseTestHelper(ImmutableTreeList<T> list, int index, int count) {
        ArrayList<T> expected = new ArrayList<T>(list.toBuilder());
        Collections.reverse(expected.subList(index, index + count));
        ImmutableTreeList<T> actual = list.reverse(index, index + count);
        assertEqualSequences(expected, new ArrayList<T>(actual.toBuilder()));
    }

    @Override
    protected <T> ArrayList<T> sortTestHelper(ImmutableTreeList<T> list) {
        return new ArrayList<T>(list.sort().toBuilder());
    }

    @Override
    protected <T> ArrayList<T> sortTestHelper(ImmutableTreeList<T> list, Comparator<? super T> comparator) {
        return new ArrayList<T>(list.sort(comparator).toBuilder());
    }

    @Override
    protected <T> ArrayList<T> sortTestHelper(ImmutableTreeList<T> list, int index, int count, Comparator<? super T> comparator) {
        return new ArrayList<T>(list.sort(index, index + count, comparator).toBuilder());
    }

    @Override
    <T> ImmutableListQueries<T> getListQuery(ImmutableTreeList<T> list) {
        return list;
    }

    private static <T> void verifyBalanced(ImmutableTreeList<T> tree) {
        TestExtensionMethods.verifyBalanced(tree.getRoot());
    }

    private static final class Person {
        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    private static class NameOnlyEqualityComparator implements EqualityComparator<Person> {
        public boolean equals(Person x, Person y) {
            return EqualityComparators.defaultComparator().equals(x.getName(), y.getName());
        }

        public int hashCode(Person obj) {
            return obj.getName().hashCode();
        }
    }
}
