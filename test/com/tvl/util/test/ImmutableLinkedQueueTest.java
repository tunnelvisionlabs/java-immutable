// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util.test;

import com.google.common.collect.Iterables;
import com.tvl.util.ImmutableLinkedQueue;
import com.tvl.util.ImmutableQueue;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;

public class ImmutableLinkedQueueTest extends SimpleElementImmutablesTestBase {

    private <T> void enqueueDequeueTestHelper(T... items) {
        assert items != null;

        ImmutableLinkedQueue<T> queue = ImmutableLinkedQueue.<T>empty();
        int i = 0;
        for (T item : items) {
            ImmutableLinkedQueue<T> nextQueue = queue.enqueue(item);
            Assert.assertNotSame(queue, nextQueue); //, "Enqueue returned this instead of a new instance.");
            Assert.assertEquals(i, Iterables.size(queue)); //, "Enqueue mutated the queue.");
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
            ImmutableLinkedQueue<T> nextQueue = queue.dequeue();
            Assert.assertNotSame(queue, nextQueue); //, "Dequeue returned this instead of a new instance.");
            Assert.assertEquals(i, Iterables.size(queue));
            Assert.assertEquals(--i, Iterables.size(nextQueue));
            queue = nextQueue;
        }
    }

    @Test
    public void enumerationOrder() {
        ImmutableLinkedQueue<Integer> queue = ImmutableLinkedQueue.<Integer>empty();

        // Push elements onto the backwards stack.
        queue = queue.enqueue(1).enqueue(2).enqueue(3);
        Assert.assertEquals(1, (int)queue.peek());

        // Force the backwards stack to be reversed and put into forwards.
        queue = queue.dequeue();

        // Push elements onto the backwards stack again.
        queue = queue.enqueue(4).enqueue(5);

            // Now that we have some elements on the forwards and backwards stack,
        // 1. enumerate all elements to verify order.
        assertEqualSequences(Arrays.asList(2, 3, 4, 5), queue);

        // 2. dequeue all elements to verify order
        int[] actual = new int[Iterables.size(queue)];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = queue.peek();
            queue = queue.dequeue();
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
    public void enqueueDequeueTest() {
        this.enqueueDequeueTestHelper(new GenericParameterHelper(1), new GenericParameterHelper(2), new GenericParameterHelper(3));
        this.<GenericParameterHelper>enqueueDequeueTestHelper();

        // interface test
        ImmutableQueue<GenericParameterHelper> queueInterface = ImmutableLinkedQueue.<GenericParameterHelper>create();
        ImmutableQueue<GenericParameterHelper> populatedQueueInterface = queueInterface.enqueue(new GenericParameterHelper(5));
        Assert.assertEquals(new GenericParameterHelper(5), populatedQueueInterface.peek());
    }

    @Test
    public void dequeueOutValue() {
        ImmutableLinkedQueue<Integer> queue = ImmutableLinkedQueue.<Integer>empty().enqueue(5).enqueue(6);
        int head = queue.peek();
        queue = queue.dequeue();
        Assert.assertEquals(5, head);
        head = queue.peek();
        ImmutableLinkedQueue<Integer> emptyQueue = queue.dequeue();
        Assert.assertEquals(6, head);
        Assert.assertTrue(emptyQueue.isEmpty());

        // Also check that the interface extension method works.
        ImmutableQueue<Integer> interfaceQueue = queue;
        head = interfaceQueue.peek();
        Assert.assertSame(emptyQueue, interfaceQueue.dequeue());
        Assert.assertEquals(6, head);
    }

    @Test
    public void clearTest() {
        ImmutableLinkedQueue<GenericParameterHelper> emptyQueue = ImmutableLinkedQueue.<GenericParameterHelper>create();
        Assert.assertSame(emptyQueue, emptyQueue.clear());
        ImmutableLinkedQueue<GenericParameterHelper> nonEmptyQueue = emptyQueue.enqueue(new GenericParameterHelper(3));
        Assert.assertSame(emptyQueue, nonEmptyQueue.clear());

        // Interface test
        ImmutableQueue<GenericParameterHelper> queueInterface = nonEmptyQueue;
        Assert.assertSame(emptyQueue, queueInterface.clear());
    }

    @Test
    @SuppressWarnings({"ObjectEqualsNull", "IncompatibleEquals"})
    public void equalsTest() {
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().equals(null));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().equals("hi"));
        Assert.assertTrue(ImmutableLinkedQueue.<Integer>empty().equals(ImmutableLinkedQueue.<Integer>empty()));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().enqueue(3).equals(ImmutableLinkedQueue.<Integer>empty().enqueue(3)));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().enqueue(5).equals(ImmutableLinkedQueue.<Integer>empty().enqueue(3)));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().enqueue(3).enqueue(5).equals(ImmutableLinkedQueue.<Integer>empty().enqueue(3)));
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().enqueue(3).equals(ImmutableLinkedQueue.<Integer>empty().enqueue(3).enqueue(5)));

        // Also be sure to compare equality of partially dequeued queues since that moves data to different fields.
        Assert.assertFalse(ImmutableLinkedQueue.<Integer>empty().enqueue(3).enqueue(1).enqueue(2).dequeue().equals(ImmutableLinkedQueue.<Integer>empty().enqueue(1).enqueue(2)));
    }

    @Test
    public void peekEmptyThrows() {
        thrown.expect(instanceOf(EmptyStackException.class));
        ImmutableLinkedQueue.<GenericParameterHelper>empty().peek();
    }

    @Test
    public void dequeueEmptyThrows() {
        thrown.expect(instanceOf(EmptyStackException.class));
        ImmutableLinkedQueue.<GenericParameterHelper>empty().dequeue();
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
            queue = queue.enqueue(item);
        }

        return queue;
    }
}
