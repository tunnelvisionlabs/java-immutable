// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util.test;

import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

public abstract class ImmutablesTestBase {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

}
