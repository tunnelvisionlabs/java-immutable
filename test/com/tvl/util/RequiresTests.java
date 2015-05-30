// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;

public class RequiresTests extends ImmutablesTestBase {
    @Test
    public void argument() {
        Requires.argument(true);
        Requires.argument(true, "parameterName", "message");

        try {
            Requires.argument(false);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }

        try {
            Requires.argument(false, "parameterName", "message");
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void failRange() {
        //Assert.Throws<ArgumentOutOfRangeException>(() => Requires.FailRange("parameterName"));

        try {
            Requires.failRange("parameterName", "message");
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void range() {
        Requires.range(true, "parameterName");
        Requires.range(true, "parameterName", "message");

        try {
            Requires.range(false, "parameterName");
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Requires.range(false, "parameterName", "message");
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void notNull() {
        Requires.notNull(new Object(), "parameterName");

        thrown.expect(instanceOf(NullPointerException.class));
        thrown.expectMessage("parameterName");
        Requires.notNull(null, "parameterName");
    }
}
