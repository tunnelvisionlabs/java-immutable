// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util.function;

/**
 * Represents a function that accepts two arguments and produces a result.
 *
 * @param <T> The type of the first input to the function.
 * @param <U> The type of the second input to the function.
 * @param <R> The type of the result of the function.
 */
public interface BiFunction<T, U, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t The first function argument.
     * @param u The second function argument.
     * @return The function result.
     */
    R apply(T t, U u);

}
