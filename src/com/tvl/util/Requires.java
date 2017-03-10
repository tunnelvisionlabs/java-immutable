// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

/**
 * This class defines common runtime checks which throw exceptions upon failure.
 *
 * These methods are used for argument validation throughout the immutable collections library.
 */
enum Requires {
    ;

    /**
     * Throws an exception if an argument is {@code null}.
     *
     * @param value The argument value.
     * @param parameterName The parameter name.
     * @param <T> The type of the parameter.
     * @exception NullPointerException if {@code value} is {@code null}.
     */
    public static <T> void notNull(T value, String parameterName) {
        if (value == null) {
            failNullPointer(parameterName);
        }
    }

    /**
     * Throws an exception if an argument is {@code null}.
     *
     * This method is used to include an argument validation in constructor chaining scenarios.
     *
     * @param value The argument value.
     * @param parameterName The parameter name.
     * @param <T> The type of the parameter.
     * @return This method returns {@code value}.
     * @exception NullPointerException if {@code value} is {@code null}.
     */
    public static <T> T notNullPassThrough(T value, String parameterName) {
        notNull(value, parameterName);
        return value;
    }

    /**
     * Throws an {@link IndexOutOfBoundsException} if a condition does not evaluate to true.
     *
     * @param condition The evaluated condition.
     * @param parameterName The name of the parameter being validated by the condition.
     * @exception IndexOutOfBoundsException if {@code condition} is false.
     */
    public static void range(boolean condition, String parameterName) {
        if (!condition) {
            failRange(parameterName, null);
        }
    }

    /**
     * Throws an {@link IndexOutOfBoundsException} if a condition does not evaluate to true.
     *
     * @param condition The evaluated condition.
     * @param parameterName The name of the parameter being validated by the condition.
     * @param message An additional message to include in the exception message if {@code condition} is false.
     * @exception IndexOutOfBoundsException if {@code condition} is false.
     */
    public static void range(boolean condition, String parameterName, String message) {
        if (!condition) {
            failRange(parameterName, message);
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if a condition does not evaluate to true.
     *
     * @param condition The evaluated condition.
     * @param parameterName The name of the parameter being validated by the condition.
     * @param message An additional message to include in the exception message if {@code condition} is false.
     * @exception IllegalArgumentException if {@code condition} is false.
     */
    public static void argument(boolean condition, String parameterName, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message + ": " + parameterName);
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if a condition does not evaluate to true.
     *
     * @param condition The evaluated condition.
     * @exception IllegalArgumentException if {@code condition} is false.
     */
    public static void argument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Throws a {@link NullPointerException}.
     *
     * @param parameterName The name of the parameter that was {@code null}.
     * @exception NullPointerException always.
     */
    private static void failNullPointer(String parameterName) {
        // Separating out this throwing operation helps with inlining of the caller
        throw new NullPointerException(parameterName);
    }

    /**
     * Throws an {@link IndexOutOfBoundsException}.
     *
     * @param parameterName The name of the parameter that was out of its allowed range.
     * @param message An optional additional message to include in the exception.
     * @exception IndexOutOfBoundsException always.
     */
    public static void failRange(String parameterName, String message) {
        if (message == null || message.isEmpty()) {
            throw new IndexOutOfBoundsException(parameterName);
        } else {
            throw new IndexOutOfBoundsException(message + ": " + parameterName);
        }
    }
}
