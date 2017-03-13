// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.EmptyStackException;

/**
 * An immutable queue.
 *
 * @param <T> The type of elements in the queue.
 */
public interface ImmutableQueue<T> extends Iterable<T> {

    /**
     * Gets a value indicating whether this is the empty queue.
     *
     * @return {@code true} if this queue is empty; otherwise {@code false}.
     */
    boolean isEmpty();

    /**
     * Gets an empty queue.
     *
     * @return The empty queue.
     */
    ImmutableQueue<T> clear();

    /**
     * Gets the element at the front of the queue.
     *
     * @return The element at the front of the queue.
     * @throws EmptyStackException if the queue is empty.
     */
    T peek();

    /**
     * Adds an element to the back of the queue.
     *
     * @param value The element to add to the queue.
     * @return The new queue.
     */
    ImmutableQueue<T> add(T value);

    /**
     * Returns a queue that is missing the front element.
     *
     * @return The new queue; never {@code null}.
     * @throws EmptyStackException if the queue is empty.
     */
    ImmutableQueue<T> poll();

}
