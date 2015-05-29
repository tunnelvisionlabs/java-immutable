// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import java.text.Collator;
import java.util.Comparator;
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

    protected static <T extends Comparable<T>> Comparator<T> defaultComparator() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
    }

    protected static StringComparator ordinalComparator() {
        Collator collator = Collator.getInstance();
        collator.setDecomposition(Collator.NO_DECOMPOSITION);
        collator.setStrength(Collator.IDENTICAL);
        return new StringComparator(collator);
    }

    protected static StringComparator ordinalIgnoreCaseComparator() {
        Collator collator = Collator.getInstance();
        collator.setDecomposition(Collator.NO_DECOMPOSITION);
        collator.setStrength(Collator.SECONDARY);
        return new StringComparator(collator);
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

    protected static final class StringComparator implements Comparator<Object>, EqualityComparator<Object> {
        private final Collator collator;

        public StringComparator(Collator collator) {
            this.collator = collator;
        }

        @Override
        public int compare(Object o1, Object o2) {
            return collator.compare(o1, o2);
        }

        @Override
        public boolean equals(Object a, Object b) {
            if (a != null && !(a instanceof String)) {
                return EqualityComparators.defaultComparator().equals(a, b);
            } else if (b != null && !(b instanceof String)) {
                return EqualityComparators.defaultComparator().equals(a, b);
            }

            return collator.equals((String)a, (String)b);
        }

        @Override
        public int hashCode(Object o) {
            if (o != null && !(o instanceof String)) {
                return EqualityComparators.defaultComparator().hashCode(o);
            }

            return collator.getCollationKey((String)o).hashCode();
        }
    }
}
