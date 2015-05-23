// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util.function;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 *
 * @param <T> the type of the input to the predicate
 */
public interface Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate; otherwise, {@code false}.
     */
    boolean test(T t);

}
