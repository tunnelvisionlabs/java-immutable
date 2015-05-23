// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util.function;

/**
 * Represents a function that accepts one argument and produces a result.
 *
 * @param <T> The type of the input to the function.
 * @param <R> The type of the result of the function.
 */
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t The function argument.
     * @return The function result.
     */
    R apply(T t);

}
