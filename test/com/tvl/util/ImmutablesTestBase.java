// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

public abstract class ImmutablesTestBase {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Gets the number of operations to perform in randomized tests.
     *
     * @return The number of operations to perform in randomized tests.
     */
    protected final int getRandomOperationsCount() {
        return 100;
    }

    protected static <T> void manuallyIterateTest(List<T> expectedResults, Iterator<T> iterator) {
        Object[] manualArray = new Object[expectedResults.size()];
        int i = 0;

        while (iterator.hasNext()) {
            manualArray[i++] = iterator.next();
        }

        Assert.assertFalse(iterator.hasNext());

        try {
            iterator.next();
            Assert.fail();
        } catch (NoSuchElementException ignored) {
        }

        Assert.assertFalse(iterator.hasNext());

        try {
            iterator.next();
            Assert.fail();
        } catch (NoSuchElementException ignored) {
        }

        Assert.assertEquals(expectedResults.size(), i); //, "Enumeration did not produce enough elements.");
        assertEqualSequences(expectedResults, Arrays.asList(manualArray));
    }

    protected Double[] generateDummyFillData() {
        return generateDummyFillData(1000);
    }

    /**
     * Generates an array of unique values.
     *
     * @param length The desired length of the array.
     * @return An array of doubles.
     */
    protected Double[] generateDummyFillData(int length) {
        Requires.range(length >= 0, "length");

        long seed = System.nanoTime();

        System.err.format("Random seed %s%n", seed);

        Random random = new Random(seed);
        Double[] inputs = new Double[length];
        HashSet<Double> ensureUniqueness = new HashSet<Double>();
        for (int i = 0; i < inputs.length; i++) {
            double input;
            do {
                input = random.nextDouble();
            } while (!ensureUniqueness.add(input));
            inputs[i] = input;
        }

        return inputs;
    }

    protected static StringComparator ordinalComparator() {
        return OrdinalStringComparator.INSTANCE;
    }

    protected static StringComparator ordinalIgnoreCaseComparator() {
        return OrdinalIgnoreCaseStringComparator.INSTANCE;
    }

    protected static <T> void assertEqualSequences(Iterable<? extends T> left, Iterable<? extends T> right) {
        Object[] leftArray = Iterables.toArray(left, Object.class);
        Object[] rightArray = Iterables.toArray(right, Object.class);
        Assert.assertArrayEquals(leftArray, rightArray);
    }

    protected static <T> void assertNotEqualSequences(Iterable<? extends T> left, Iterable<? extends T> right) {
        Object[] leftArray = Iterables.toArray(left, Object.class);
        Object[] rightArray = Iterables.toArray(right, Object.class);
        Assert.assertThat(leftArray, not(equalTo(rightArray)));
    }

    protected static abstract class StringComparator implements Comparator<String>, EqualityComparator<String> {
    }

    protected static final class OrdinalStringComparator extends StringComparator {
        private static final StringComparator INSTANCE = new OrdinalStringComparator();

        private final Comparator<? super String> comparator = Comparators.<String>defaultComparator();
        private final EqualityComparator<? super String> equalityComparator = EqualityComparators.defaultComparator();

        @Override
        public int compare(String o1, String o2) {
            return comparator.compare(o1, o2);
        }

        @Override
        public boolean equals(String a, String b) {
            return equalityComparator.equals(a, b);
        }

        @Override
        public int hashCode(String o) {
            return equalityComparator.hashCode(o);
        }
    }

    protected static final class OrdinalIgnoreCaseStringComparator extends StringComparator {
        private static final StringComparator INSTANCE = new OrdinalIgnoreCaseStringComparator();

        @Override
        public int compare(String o1, String o2) {
            if (o1 != null) {
                o1 = o1.toUpperCase();
            }

            if (o2 != null) {
                o2 = o2.toUpperCase();
            }

            return OrdinalStringComparator.INSTANCE.compare(o1, o2);
        }

        @Override
        public boolean equals(String a, String b) {
            if (a != null) {
                a = a.toUpperCase();
            }

            if (b != null) {
                b = b.toUpperCase();
            }

            return OrdinalStringComparator.INSTANCE.equals(a, b);
        }

        @Override
        public int hashCode(String o) {
            if (o != null) {
                o = o.toUpperCase();
            }

            return OrdinalStringComparator.INSTANCE.hashCode(o);
        }
    }
}
