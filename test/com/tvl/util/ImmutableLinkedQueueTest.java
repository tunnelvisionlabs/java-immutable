// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;

public class ImmutableLinkedQueueTest extends SimpleElementImmutablesTestBase {

    private <T> void addPollTestHelper(T... items) {
        assert items != null;

        ImmutableLinkedQueue<T> queue = ImmutableLinkedQueue.<T>empty();
        int i = 0;
        for (T item : items) {
            ImmutableLinkedQueue<T> nextQueue = queue.add(item);
            Assert.assertNotSame(queue, nextQueue); //, "Add returned this instead of a new instance.");
            Assert.assertEquals(i, Iterables.size(queue)); //, "Add mutated the queue.");
            Assert.assertEquals(++i, Iterables.size(nextQueue));
            queue = nextQueue;
        }

        i = 0;
        for (T element : queue) {
            Assert.assertSame(items[i++], element);
        }

        i = 0;
        for (T element : (Iterable<T>)queue) {
            Assert.assertSame(items[i++], element);
        }

        i = items.length;
        for (T expectedItem : items) {
            T actualItem = queue.peek();
            Assert.assertSame(expectedItem, actualItem);
            ImmutableLinkedQueue<T> nextQueue = queue.poll();
            Assert.assertNotSame(queue, nextQueue); //, "Poll returned this instead of a new instance.");
            Assert.assertEquals(i, Iterables.size(queue));
            Assert.assertEquals(--i, Iterables.size(nextQueue));
            queue = nextQueue;
        }
    }

    @Test
    public void enumerationOrder() {
        ImmutableLinkedQueue<Integer> queue = ImmutableLinkedQueue.<Integer>empty();

        // Push elements onto the backwards stack.
        queue = queue.add(1).add(2).add(3);
        Assert.assertEquals(1, (int)queue.peek());

        // Force the backwards stack to be reversed and put into forwards.
        queue = queue.poll();

        // Push elements onto the backwards stack again.
        queue = queue.add(4).add(5);

        // Now that we have some elements on the forwards and backwards stack,
        // 1. enumerate all elements to verify order.
        assertEqualSequences(Arrays.asList(2, 3, 4, 5), queue);

        // 2. poll all elements to verify order
        int[] actual = new int[Iterables.size(queue)];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = queue.peek();
            queue = queue.poll();
        }
    }

    @Test
    public void getEnumeratorText() {
        ImmutableLinkedQueue<Integer> queue = ImmutableLinkedQueue.create(5);
        Iterator<Integer> enumeratorStruct = queue.iterator();
        Assert.assertTrue(enumeratorStruct.hasNext());
        Assert.assertEquals(5, (int)enumeratorStruct.next());
        Assert.assertFalse(enumeratorStruct.hasNext());
        thrown.expect(NoSuchElementException.class);
        enumeratorStruct.next();
    }

    @Test
    public void addPollTest() {
        this.addPollTestHelper(new GenericParameterHelper(1), new GenericParameterHelper(2), new GenericParameterHelper(3));
        this.<GenericParameterHelper>addPollTestHelper();

        // interface test
        ImmutableQueue<GenericParameterHelper> queueInterface = ImmutableLinkedQueue.<GenericParameterHelper>create();
        ImmutableQueue<GenericParameterHelper> populatedQueueInterface = queueInterface.add(new GenericParameterHelper(5));
        Assert.assertEquals(new GenericParameterHelper(5), populatedQueueInterface.peek());
    }

    @Test
    public void pollOutValue() {
        ImmutableLinkedQueue<Integer> queue = ImmutableLinkedQueue.<Integer>empty().add(5).add(6);
        int head = queue.peek();
        queue = queue.poll();
        Assert.assertEquals(5, head);
        head = queue.peek();
        ImmutableLinkedQueue<Integer> emptyQueue = queue.poll();
        Assert.assertEquals(6, head);
        Assert.assertTrue(emptyQueue.isEmpty());

        // Also check that the interface extension method works.
        ImmutableQueue<Integer> interfaceQueue = queue;
        head = interfaceQueue.peek();
        Assert.assertSame(emptyQueue, interfaceQueue.poll());
        Assert.assertEquals(6, head);
    }

    @Test
    public void clearTest() {
        ImmutableLinkedQueue<GenericParameterHelper> emptyQueue = ImmutableLinkedQueue.<GenericParameterHelper>create();
        Assert.assertSame(emptyQueue, emptyQueue.clear());
        ImmutableLinkedQueue<GenericParameterHelper> nonEmptyQueue = emptyQueue.add(new GenericParameterHelper(3));
        Assert.assertSame(emptyQueue, nonEmptyQueue.clear());

        // Interface test
        ImmutableQueue<GenericParameterHelper> queueInterface = nonEmptyQueue;
        Assert.assertSame(emptyQueue, queueInterface.clear());
    }

    @Test
    @SuppressWarnings({ "ObjectEqualsNull", "IncompatibleEquals" })
    public void equalsTest() {
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().equals(null));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().equals("hi"));
        Assert.assertTrue(ImmutableLinkedQueue.<Integer>empty().equals(ImmutableLinkedQueue.<Integer>empty()));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().add(3).equals(ImmutableLinkedQueue.<Integer>empty().add(3)));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().add(5).equals(ImmutableLinkedQueue.<Integer>empty().add(3)));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().add(3).add(5).equals(ImmutableLinkedQueue.<Integer>empty().add(3)));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().add(3).equals(ImmutableLinkedQueue.<Integer>empty().add(3).add(5)));

        // Also be sure to compare equality of partially polled queues since that moves data to different fields.
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().add(3).add(1).add(2).poll().equals(ImmutableLinkedQueue.<Integer>empty().add(1).add(2)));
    }

    @Test
    public void peekEmptyThrows() {
        thrown.expect(instanceOf(EmptyStackException.class));
        ImmutableLinkedQueue.<GenericParameterHelper>empty().peek();
    }

    @Test
    public void pollEmptyThrows() {
        thrown.expect(instanceOf(EmptyStackException.class));
        ImmutableLinkedQueue.<GenericParameterHelper>empty().poll();
    }

    @Test
    public void create() {
        ImmutableLinkedQueue<Integer> queue = ImmutableLinkedQueue.create();
        Assert.assertTrue(queue.isEmpty());

        queue = ImmutableLinkedQueue.create(1);
        Assert.assertFalse(queue.isEmpty());
        assertEqualSequences(Arrays.asList(1), queue);

        queue = ImmutableLinkedQueue.create(1, 2);
        Assert.assertFalse(queue.isEmpty());
        assertEqualSequences(Arrays.asList(1, 2), queue);

        queue = ImmutableLinkedQueue.createAll(Arrays.asList(1, 2));
        Assert.assertFalse(queue.isEmpty());
        assertEqualSequences(Arrays.asList(1, 2), queue);
    }

    @Test
    public void createNull() {
        thrown.expect(NullPointerException.class);
        ImmutableLinkedQueue.<Integer>create((Integer[])null);
    }

    @Test
    public void createAllNull() {
        thrown.expect(NullPointerException.class);
        ImmutableLinkedQueue.<Integer>createAll((Iterable<Integer>)null);
    }

    @Test
    public void empty() {
        // We already test create(), so just prove that empty has the same effect.
        Assert.assertSame(ImmutableLinkedQueue.<Integer>create(), ImmutableLinkedQueue.<Integer>empty());
    }

    @Override
    protected <T> Iterable<T> getIterableOf(T... contents) {
        ImmutableLinkedQueue<T> queue = ImmutableLinkedQueue.<T>empty();
        for (T item : contents) {
            queue = queue.add(item);
        }

        return queue;
    }
}
