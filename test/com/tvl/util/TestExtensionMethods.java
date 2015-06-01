// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Map;
import org.junit.Assert;

final class TestExtensionMethods {
    private static final double GOLDEN_RATIO = (1 + Math.sqrt(5)) / 2;

    static <K, V> Map<K, V> toReadOnlyMap(ImmutableMap<K, V> map) {
        Requires.notNull(map, "map");
        return toBuilder(map);
    }

    static <K, V> Map<K, V> toBuilder(ImmutableMap<K, V> map) {
        Requires.notNull(map, "map");

        if (map instanceof ImmutableHashMap<?, ?>) {
            return ((ImmutableHashMap<K, V>)map).toBuilder();
        }

        //if (map instanceof ImmutableTreeMap<K, V>) {
        //    return ((ImmutableTreeMap<K, V>)map).toBuilder();
        //}

        throw new UnsupportedOperationException();
    }

    /**
     * Verifies that a binary tree is balanced according to AVL rules.
     *
     * @param node The root node of the binary tree.
     */
    static void verifyBalanced(BinaryTree<?> node) {
        if (node.getLeft() != null) {
            verifyBalanced(node.getLeft());
        }

        if (node.getRight() != null) {
            verifyBalanced(node.getRight());
        }

        if (node.getRight() != null && node.getLeft() != null) {
            assertInRange(node.getLeft().getHeight() - node.getRight().getHeight(), -1, 1);
        } else if (node.getRight() != null) {
            assertInRange(node.getRight().getHeight(), 0, 1);
        } else if (node.getLeft() != null) {
            assertInRange(node.getLeft().getHeight(), 0, 1);
        }
    }

    private static void assertInRange(int value, int minimum, int maximumInclusive) {
        Assert.assertTrue(value >= minimum);
        Assert.assertTrue(value <= maximumInclusive);
    }

    static void verifyHeightIsWithinTolerance(BinaryTree<?> node) {
        verifyHeightIsWithinTolerance(node, null);
    }

    /**
     * Verifies that a binary tree is no taller than necessary to store the data if it were optimally balanced.
     *
     * @param node The root node.
     * @param count The number of nodes in the tree. May be {@code null} if {@link BinaryTree#size()} is functional.
     */
    static void verifyHeightIsWithinTolerance(BinaryTree<?> node, Integer count) {
        // http://en.wikipedia.org/wiki/AVL_tree
        double heightMustBeLessThan = log(2, GOLDEN_RATIO) * log(Math.sqrt(5) * ((count != null ? count : node.size()) + 2), 2) - 2;
        Assert.assertTrue(node.getHeight() < heightMustBeLessThan);
    }

    private static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    private TestExtensionMethods() {
    }
}
