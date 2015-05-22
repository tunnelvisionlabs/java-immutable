// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An immutable stack backed by a singly-linked list.
 *
 * @param <T> The type of element stored by the stack.
 */
public final class ImmutableLinkedStack<T> implements ImmutableStack<T> {

    /**
     * The singleton empty stack.
     */
    private static final ImmutableLinkedStack<?> EMPTY_STACK = new ImmutableLinkedStack<Object>();

    /**
     * The element on the top of the stack.
     */
    private final T head;

    /**
     * A stack that contains the rest of the elements (under the top element);
     */
    private final ImmutableLinkedStack<T> tail;

    /**
     * Initializes a new instance of the {@link ImmutableLinkedStack} class that acts as the empty stack.
     */
    private ImmutableLinkedStack() {
        head = null;
        tail = null;
    }

    /**
     * Initializes a new instance of the {@link ImmutableLinkedStack} class.
     *
     * @param head The head element on the stack.
     * @param tail The rest of the elements on the stack.
     */
    private ImmutableLinkedStack(T head, ImmutableLinkedStack<T> tail) {
        if (tail == null) {
            throw new NullPointerException("tail");
        }

        this.head = head;
        this.tail = tail;
    }

    /**
     * Return an empty collection.
     *
     * @param <T> The type of items stored by the collection.
     * @return The immutable collection.
     */
    public static <T> ImmutableLinkedStack<T> create() {
        return empty();
    }

    /**
     * Creates a new immutable collection pre-filled with the specified item.
     *
     * @param <T> The type of items stored by the collection.
     * @param item The item to pre-populate.
     * @return The immutable collection.
     */
    public static <T> ImmutableLinkedStack<T> create(T item) {
        return ImmutableLinkedStack.<T>empty().push(item);
    }

    /**
     * Creates a new immutable collection pre-filled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param items The items to pre-populate.
     * @return The immutable collection.
     */
    public static <T> ImmutableLinkedStack<T> create(T... items) {
        if (items == null) {
            throw new NullPointerException("items");
        }

        ImmutableLinkedStack<T> stack = empty();
        for (T item : items) {
            stack = stack.push(item);
        }

        return stack;
    }

    /**
     * Creates a new immutable collection pre-filled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param items The items to pre-populate.
     * @return The immutable collection.
     */
    public static <T> ImmutableLinkedStack<T> createAll(Iterable<? extends T> items) {
        if (items == null) {
            throw new NullPointerException("items");
        }

        ImmutableLinkedStack<T> stack = empty();
        for (T item : items) {
            stack = stack.push(item);
        }

        return stack;
    }

    /**
     * Gets the empty stack, upon which all stacks are built.
     *
     * @param <T> The type of element stored by the stack.
     * @return The empty stack.
     */
    public static <T> ImmutableLinkedStack<T> empty() {
        return (ImmutableLinkedStack<T>)EMPTY_STACK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return tail == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableLinkedStack<T> clear() {
        return empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableLinkedStack<T> push(T value) {
        return new ImmutableLinkedStack<T>(value, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableLinkedStack<T> pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }

        return tail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }

        return head;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return new Itr<T>(this);
    }

    /**
     * Reverses the order of the stack.
     *
     * @return The reversed stack.
     */
    ImmutableLinkedStack<T> reverse() {
        ImmutableLinkedStack<T> result = clear();
        for (ImmutableLinkedStack<T> f = this; !f.isEmpty(); f = f.pop()) {
            result = result.push(f.peek());
        }

        return result;
    }

    private static final class Itr<T> implements Iterator<T> {

        /**
         * The original stack being enumerated.
         */
        private final ImmutableLinkedStack<T> originalStack;
        /**
         * The remaining stack not yet enumerated.
         */
        private ImmutableLinkedStack<T> remainingStack;

        /**
         * Initializes a new instance of the {@link Itr} class.
         *
         * @param originalStack The stack to enumerate.
         */
        public Itr(ImmutableLinkedStack<T> originalStack) {
            this.originalStack = originalStack;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            if (remainingStack == null) {
                return !originalStack.isEmpty();
            }

            return !remainingStack.isEmpty();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T next() {
            if (remainingStack == null) {
                remainingStack = originalStack;
            }

            if (remainingStack.isEmpty()) {
                throw new NoSuchElementException();
            }

            T result = remainingStack.peek();
            remainingStack = remainingStack.pop();
            return result;
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
