// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import javax.annotation.Nullable;

/**
 * An interface for binary tree nodes that allow a common iterator to walk the graph.
 *
 * @param <T> The type of value for each node.
 */
interface BinaryTree<T> {

    /**
     * Gets the depth of the tree below this node.
     *
     * @return The depth of the tree below this node.
     */
    int getHeight();

    /**
     * Gets the number of non-empty nodes at this node and below.
     *
     * @return The number of non-empty nodes at this node and below.
     */
    int size();

    /**
     * Gets a value indicating whether this node is empty.
     *
     * @return {@code true} if this node is empty; otherwise, {@code false}.
     */
    boolean isEmpty();

    /**
     * Gets the left branch of this node.
     *
     * @return The left branch of this node, or {@code null} if this node is empty.
     */
    @Nullable
    BinaryTree<T> getLeft();

    /**
     * Gets the right branch of this node.
     *
     * @return The right branch of this node, or {@code null} if this node is empty.
     */
    @Nullable
    BinaryTree<T> getRight();

    /**
     * Gets the value represented by the current node.
     *
     * @return The value represented by the current node.
     */
    T getValue();
}
