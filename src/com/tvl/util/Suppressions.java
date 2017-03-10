// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

/**
 * This class provides constants which can be passed to {@link SuppressWarnings} to document the reason for an exclusion
 * in addition to the exclusion itself.
 */
enum Suppressions {
    ;

    /**
     * This constant can be passed to {@link SuppressWarnings} to suppress {@code unchecked} warnings in cases where the
     * operation is guaranteed to preserve the type safety of the operation when the preconditions of the operation are
     * met by calling code.
     */
    public static final String UNCHECKED_SAFE = "unchecked";
}
