// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;

public class ImmutableLinkedStackTest extends SimpleElementImmutablesTestBase {

    /**
     * A test for Empty
     *
     * @param <T> The type of elements held in the stack.
     */
    private <T> void emptyTestHelper(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        ImmutableStack<T> actual = ImmutableLinkedStack.empty();
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isEmpty());
        Assert.assertSame(ImmutableLinkedStack.empty(), actual.clear());
        Assert.assertSame(ImmutableLinkedStack.empty(), actual.push(clazz.newInstance()).clear());
    }

    private <T> ImmutableLinkedStack<T> initStackHelper(T... values) {
        ImmutableLinkedStack<T> result = ImmutableLinkedStack.empty();
        for (T value : values) {
            result = result.push(value);
        }

        return result;
    }

    private <T> void pushAndCountTestHelper(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        ImmutableLinkedStack<T> actual0 = ImmutableLinkedStack.empty();
        Assert.assertEquals(0, Iterables.size(actual0));
        ImmutableLinkedStack<T> actual1 = actual0.push(clazz.newInstance());
        Assert.assertEquals(1, Iterables.size(actual1));
        Assert.assertEquals(0, Iterables.size(actual0));
        ImmutableLinkedStack<T> actual2 = actual1.push(clazz.newInstance());
        Assert.assertEquals(2, Iterables.size(actual2));
        Assert.assertEquals(0, Iterables.size(actual0));
    }

    private <T> void popTestHelper(T... values) {
        assert values != null;
        assert values.length > 0;

        ImmutableLinkedStack<T> full = this.initStackHelper(values);
        ImmutableLinkedStack<T> currentStack = full;

        // This loop tests the immutable properties of Pop.
        for (int expectedCount = values.length; expectedCount > 0; expectedCount--) {
            Assert.assertEquals(expectedCount, Iterables.size(currentStack));
            currentStack.pop();
            Assert.assertEquals(expectedCount, Iterables.size(currentStack));
            ImmutableLinkedStack<T> nextStack = currentStack.pop();
            Assert.assertEquals(expectedCount, Iterables.size(currentStack));
            Assert.assertNotSame(currentStack, nextStack);
            Assert.assertSame("Popping the stack 2X should yield the same shorter stack.", currentStack.pop(), currentStack.pop());
            currentStack = nextStack;
        }
    }

    private <T> void peekTestHelper(T... values) {
        assert values != null;
        assert values.length > 0;

        ImmutableLinkedStack<T> current = this.initStackHelper(values);
        for (int i = values.length - 1; i >= 0; i--) {
            Assert.assertSame(values[i], current.peek());
            T element = current.peek();
            current.pop();
            Assert.assertSame(current.peek(), element);
            ImmutableLinkedStack<T> next = current.pop();
            Assert.assertSame("Pop mutated the stack instance.", values[i], current.peek());
            current = next;
        }
    }

    private <T> void enumeratorTestHelper(T... values) {
        ImmutableLinkedStack<T> full = this.initStackHelper(values);

        int i = values.length - 1;
        for (T element : full) {
            Assert.assertSame(values[i--], element);
        }

        Assert.assertEquals(-1, i);

        i = values.length - 1;
        for (T element : full) {
            Assert.assertSame(values[i--], element);
        }

        Assert.assertEquals(-1, i);
    }

    @Test
    public void emptyTest() throws InstantiationException, IllegalAccessException {
        this.emptyTestHelper(GenericParameterHelper.class);
    }

    @Test
    public void pushAndCountTest() throws InstantiationException, IllegalAccessException {
        this.pushAndCountTestHelper(GenericParameterHelper.class);
    }

    @Test
    public void popTest() {
        this.popTestHelper(
            new GenericParameterHelper(1),
            new GenericParameterHelper(2),
            new GenericParameterHelper(3));
        this.popTestHelper(1, 2, 3);
    }

    @Test
    public void popOutValue() {
        ImmutableLinkedStack<Integer> stack = ImmutableLinkedStack.<Integer>empty().push(5).push(6);
        int top = stack.peek();
        stack = stack.pop();
        Assert.assertEquals(6, top);
        top = stack.peek();
        ImmutableLinkedStack<Integer> empty = stack.pop();
        Assert.assertEquals(5, top);
        Assert.assertTrue(empty.isEmpty());

        // Try again with the interface to verify extension method behavior.
        ImmutableStack<Integer> stackInterface = stack;
        top = stack.peek();
        Assert.assertSame(empty, stackInterface.pop());
        Assert.assertEquals(5, top);
    }

    @Test
    public void peekTest() {
        this.peekTestHelper(
            new GenericParameterHelper(1),
            new GenericParameterHelper(2),
            new GenericParameterHelper(3));
        this.peekTestHelper(1, 2, 3);
    }

    @Test
    public void enumeratorTest() {
        this.enumeratorTestHelper(new GenericParameterHelper(1), new GenericParameterHelper(2));
        this.<GenericParameterHelper>enumeratorTestHelper();

        this.enumeratorTestHelper(1, 2);
        this.<Integer>enumeratorTestHelper();

        ImmutableLinkedStack<Integer> stack = ImmutableLinkedStack.<Integer>create(5);
        Iterator<Integer> enumeratorStruct = stack.iterator();
        Assert.assertTrue(enumeratorStruct.hasNext());
        Assert.assertEquals(5, (int)enumeratorStruct.next());
        Assert.assertFalse(enumeratorStruct.hasNext());
        try {
            enumeratorStruct.next();
            Assert.fail();
        } catch (NoSuchElementException ex) {
        }
    }

    @Test
    @SuppressWarnings({ "ObjectEqualsNull", "IncompatibleEquals" })
    public void equalityTest() {
        Assert.assertFalse(ImmutableLinkedStack.<Integer>empty().equals(null));
        Assert.assertFalse(ImmutableLinkedStack.<Integer>empty().equals("hi"));
        assertEqualSequences(ImmutableLinkedStack.<Integer>empty(), ImmutableLinkedStack.<Integer>empty());
        assertEqualSequences(ImmutableLinkedStack.<Integer>empty().push(3), ImmutableLinkedStack.<Integer>empty().push(3));
        assertNotEqualSequences(ImmutableLinkedStack.<Integer>empty().push(5), ImmutableLinkedStack.<Integer>empty().push(3));
        assertNotEqualSequences(ImmutableLinkedStack.<Integer>empty().push(3).push(5), ImmutableLinkedStack.<Integer>empty().push(3));
        assertNotEqualSequences(ImmutableLinkedStack.<Integer>empty().push(3), ImmutableLinkedStack.<Integer>empty().push(3).push(5));
    }

    @Test
    public void emptyPeekThrows() {
        thrown.expect(instanceOf(EmptyStackException.class));
        thrown.expectMessage(nullValue(String.class));
        ImmutableLinkedStack.<GenericParameterHelper>empty().peek();
    }

    @Test
    public void emptyPopThrows() {
        thrown.expect(instanceOf(EmptyStackException.class));
        ImmutableLinkedStack.<GenericParameterHelper>empty().pop();
    }

    @Test
    public void create() {
        ImmutableLinkedStack<Integer> queue = ImmutableLinkedStack.<Integer>create();
        Assert.assertTrue(queue.isEmpty());

        queue = ImmutableLinkedStack.create(1);
        Assert.assertFalse(queue.isEmpty());
        assertEqualSequences(Arrays.asList(1), queue);

        queue = ImmutableLinkedStack.create(1, 2);
        Assert.assertFalse(queue.isEmpty());
        assertEqualSequences(Arrays.asList(2, 1), queue);

        queue = ImmutableLinkedStack.createAll((Iterable<Integer>)Arrays.asList(1, 2));
        Assert.assertFalse(queue.isEmpty());
        assertEqualSequences(Arrays.asList(2, 1), queue);
    }

    @Test
    public void testCreateNull() {
        thrown.expect(instanceOf(NullPointerException.class));
        ImmutableLinkedStack.create((Integer[])null);
    }

    @Test
    public void testCreateAllNull() {
        thrown.expect(instanceOf(NullPointerException.class));
        ImmutableLinkedStack.createAll((Iterable<Integer>)null);
    }

    @Override
    protected <T> Iterable<T> getIterableOf(T... contents) {
        ImmutableLinkedStack<T> stack = ImmutableLinkedStack.empty();
        List<T> contentsList = Arrays.asList(contents);
        Collections.reverse(contentsList);
        for (T value : contentsList) {
            stack = stack.push(value);
        }

        return stack;
    }

}
