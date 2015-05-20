// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.EmptyStackException;

/**
 * An immutable stack.
 *
 * @param <T> The type of elements stored in the stack.
 */
public interface ImmutableStack<T> extends Iterable<T> {

    /**
     * Gets a value indicating whether this is the empty stack.
     *
     * @return {@code true} if this stack is empty; otherwise {@code false}.
     */
    boolean isEmpty();

    /**
     * Gets an empty stack.
     */
    ImmutableStack<T> clear();

    /**
     * Pushes an element onto a stack and returns the new stack.
     *
     * @param value The element to push onto the stack.
     * @return The new stack.
     */
    ImmutableStack<T> push(T value);

    /**
     * Pops the top element off the stack.
     *
     * @return The new stack; never {@code null}.
     * @throws EmptyStackException if the stack is empty.
     */
    ImmutableStack<T> pop();

    /**
     * Gets the element on the top of the stack.
     *
     * @return The element on the top of the stack.
     * @throws EmptyStackException if the stack is empty.
     */
    T peek();

}
