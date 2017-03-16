// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/**
 * An immutable queue backed by singly-linked lists.
 *
 * @param <T> The type of element stored by the queue.
 */
public class ImmutableLinkedQueue<T> implements ImmutableQueue<T> {

    /**
     * The singleton empty queue.
     */
    private static final ImmutableLinkedQueue<?> EMPTY_QUEUE = new ImmutableLinkedQueue<Object>(ImmutableLinkedStack.empty(), ImmutableLinkedStack.empty());

    /**
     * The end of the queue that added elements are pushed onto.
     */
    private final ImmutableLinkedStack<T> backwards;

    /**
     * The end of the queue from which elements are polled.
     */
    private final ImmutableLinkedStack<T> forwards;

    /**
     * The backing field for {@link #getBackwardsReversed()}.
     */
    private ImmutableLinkedStack<T> backwardsReversed;

    /**
     * Initializes a new instance of the {@link ImmutableLinkedQueue} class.
     *
     * @param forward The forward stack.
     * @param backward The backward stack.
     */
    private ImmutableLinkedQueue(@Nonnull ImmutableLinkedStack<T> forward, @Nonnull ImmutableLinkedStack<T> backward) {
        Requires.notNull(forward, "forward");
        Requires.notNull(backward, "backward");

        this.forwards = forward;
        this.backwards = backward;
        this.backwardsReversed = null;
    }

    /**
     * Return an empty collection.
     *
     * @param <T> The type of items stored by the collection.
     * @return The immutable collection.
     */
    @Nonnull
    public static <T> ImmutableLinkedQueue<T> create() {
        return empty();
    }

    /**
     * Creates a new immutable collection pre-filled with the specified item.
     *
     * @param <T> The type of items stored by the collection.
     * @param item The item to pre-populate.
     * @return The immutable collection.
     */
    @Nonnull
    public static <T> ImmutableLinkedQueue<T> create(T item) {
        return ImmutableLinkedQueue.<T>empty().add(item);
    }

    /**
     * Creates a new immutable collection pre-filled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param items The items to pre-populate.
     * @return The immutable collection.
     */
    @Nonnull
    public static <T> ImmutableLinkedQueue<T> create(@Nonnull T... items) {
        Requires.notNull(items, "items");

        ImmutableLinkedQueue<T> queue = empty();
        for (T item : items) {
            queue = queue.add(item);
        }

        return queue;
    }

    /**
     * Creates a new immutable collection pre-filled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param items The items to pre-populate.
     * @return The immutable collection.
     */
    @Nonnull
    public static <T> ImmutableLinkedQueue<T> createAll(@Nonnull Iterable<? extends T> items) {
        Requires.notNull(items, "items");

        ImmutableLinkedQueue<T> queue = empty();
        for (T item : items) {
            queue = queue.add(item);
        }

        return queue;
    }

    @Nonnull
    public static <T> ImmutableLinkedQueue<T> empty() {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        ImmutableLinkedQueue<T> result = (ImmutableLinkedQueue<T>)EMPTY_QUEUE;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableLinkedQueue<T> clear() {
        return empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return forwards.isEmpty() && backwards.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }

        return forwards.peek();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableLinkedQueue<T> add(T value) {
        if (isEmpty()) {
            return new ImmutableLinkedQueue<T>(ImmutableLinkedStack.<T>empty().push(value), ImmutableLinkedStack.<T>empty());
        } else {
            return new ImmutableLinkedQueue<T>(forwards, backwards.push(value));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @CheckReturnValue
    @Override
    public ImmutableLinkedQueue<T> poll() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }

        ImmutableLinkedStack<T> f = forwards.pop();
        if (!f.isEmpty()) {
            return new ImmutableLinkedQueue<T>(f, backwards);
        } else if (backwards.isEmpty()) {
            return empty();
        } else {
            return new ImmutableLinkedQueue<T>(getBackwardsReversed(), ImmutableLinkedStack.<T>empty());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return new Itr<T>(this);
    }

    @Nonnull
    @CheckReturnValue
    private ImmutableLinkedStack<T> getBackwardsReversed() {
        if (backwardsReversed == null) {
            backwardsReversed = backwards.reverse();
        }

        return backwardsReversed;
    }

    private static final class Itr<T> implements Iterator<T> {

        /**
         * The original queue being enumerated.
         */
        @Nonnull
        private final ImmutableLinkedQueue<T> originalQueue;
        /**
         * The remaining forwards queue not yet enumerated.
         */
        private ImmutableLinkedStack<T> remainingForwardsStack;
        /**
         * The remaining backwards stack not yet enumerated. Its order is reversed when the field is first initialized.
         */
        private ImmutableLinkedStack<T> remainingBackwardsStack;

        /**
         * Initializes a new instance of the {@link Itr} class.
         *
         * @param originalQueue The queue to enumerate.
         */
        public Itr(@Nonnull ImmutableLinkedQueue<T> originalQueue) {
            this.originalQueue = originalQueue;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            if (remainingForwardsStack == null) {
                return !originalQueue.isEmpty();
            } else {
                return !remainingForwardsStack.isEmpty() || !remainingBackwardsStack.isEmpty();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T next() {
            if (remainingForwardsStack == null) {
                remainingForwardsStack = originalQueue.forwards;
                remainingBackwardsStack = originalQueue.getBackwardsReversed();
            }

            if (!remainingForwardsStack.isEmpty()) {
                T result = remainingForwardsStack.peek();
                remainingForwardsStack = remainingForwardsStack.pop();
                return result;
            } else if (!remainingBackwardsStack.isEmpty()) {
                T result = remainingBackwardsStack.peek();
                remainingBackwardsStack = remainingBackwardsStack.pop();
                return result;
            }

            throw new NoSuchElementException();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
