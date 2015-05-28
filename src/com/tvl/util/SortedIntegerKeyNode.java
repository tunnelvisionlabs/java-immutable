// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Comparator;
import java.util.Map;

/**
 * A node in the AVL tree storing key/value pairs with integer keys.
 *
 * <p>This is a trimmed-down version of {@link ImmutableTreeMap.Node} with {@code K} fixed to be {@code int}. This
 * avoids boxing as well as multiple interface-based dispatches while examining each node in the tree during a lookup:
 * an interface call to the comparator's {@link Comparator#compare} method, and then an interface call to
 * {@link Integer#compareTo} as part of the default {@link Comparator} implementation.</p>
 *
 * @param <T> The type of value stored in the tree.
 */
final class SortedIntegerKeyNode<T> implements BinaryTree<SortedIntegerKeyNode.IntegerKeyEntry<T>> {

    /**
     * The default empty node.
     */
    private static final SortedIntegerKeyNode<?> EMPTY_NODE = new SortedIntegerKeyNode<Object>();

    /**
     * The key associated with this node.
     */
    private final int key;

    /**
     * The value associated with this node.
     */
    private final T value;

    /**
     * A value indicating whether this node has been frozen (made immutable).
     */
    private boolean frozen;

    /**
     * The depth of the tree beneath this node.
     */
    private byte height;

    /**
     * The left tree.
     */
    private SortedIntegerKeyNode<T> left;

    /**
     * The right tree.
     */
    private SortedIntegerKeyNode<T> right;

    /**
     * Initializes a new instance of the {@link SortedIntegerKeyNode} class that is pre-frozen.
     */
    private SortedIntegerKeyNode() {
        key = 0;
        value = null;
        frozen = true;
    }

    private SortedIntegerKeyNode(int key, T value, SortedIntegerKeyNode<T> left, SortedIntegerKeyNode<T> right) {
        this(key, value, left, right, false);
    }

    private SortedIntegerKeyNode(int key, T value, SortedIntegerKeyNode<T> left, SortedIntegerKeyNode<T> right, boolean frozen) {
        if (left == null) {
            throw new NullPointerException("left");
        }

        if (right == null) {
            throw new NullPointerException("right");
        }

        assert !frozen || (left.frozen && right.frozen);
        this.key = key;
        this.value = value;
        this.left = left;
        this.right = right;
        this.frozen = frozen;
        this.height = (byte)(1 + Math.max(left.height, right.height));
    }

    public static <T> SortedIntegerKeyNode<T> emptyNode() {
        SortedIntegerKeyNode<T> result = (SortedIntegerKeyNode<T>)EMPTY_NODE;
        return result;
    }

    @Override
    public boolean isEmpty() {
        return left == null;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public SortedIntegerKeyNode<T> getLeft() {
        return left;
    }

    @Override
    public SortedIntegerKeyNode<T> getRight() {
        return right;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported by this type.");
    }

    @Override
    public IntegerKeyEntry<T> getValue() {
        return new IntegerKeyEntry<T>(key, value);
    }

    SetItemResult<T> setItem(int key, T value, EqualityComparator<? super T> valueComparator) {
        Requires.notNull(valueComparator, "valueComparator");
        return setOrAdd(key, value, valueComparator, true);
    }

    MutationResult<T> remove(int key) {
        return removeRecursive(key);
    }

    T getValueOrDefault(int key) {
        SortedIntegerKeyNode<T> match = search(key);
        return match.isEmpty() ? null : match.value;
    }

    void freeze() {
        freeze(null);
    }

    void freeze(FreezeAction<? super T> freezeAction) {
        // If this node is frozen, all its descendants must already be frozen
        if (!frozen) {
            if (freezeAction != null) {
                freezeAction.apply(key, value);
            }

            left.freeze(freezeAction);
            right.freeze(freezeAction);
            frozen = true;
        }
    }

    private static <T> SortedIntegerKeyNode<T> rotateLeft(SortedIntegerKeyNode<T> tree) {
        Requires.notNull(tree, "tree");
        assert !tree.isEmpty();

        if (tree.right.isEmpty()) {
            return tree;
        }

        SortedIntegerKeyNode<T> right = tree.right;
        return right.mutateLeft(tree.mutateRight(right.left));
    }

    private static <T> SortedIntegerKeyNode<T> rotateRight(SortedIntegerKeyNode<T> tree) {
        Requires.notNull(tree, "tree");
        assert !tree.isEmpty();

        if (tree.left.isEmpty()) {
            return tree;
        }

        SortedIntegerKeyNode<T> left = tree.left;
        return left.mutateRight(tree.mutateLeft(left.right));
    }

    private static <T> SortedIntegerKeyNode<T> doubleLeft(SortedIntegerKeyNode<T> tree) {
        Requires.notNull(tree, "tree");
        assert !tree.isEmpty();

        if (tree.right.isEmpty()) {
            return tree;
        }

        SortedIntegerKeyNode<T> rotatedRightChild = tree.mutateRight(rotateRight(tree.right));
        return rotateLeft(rotatedRightChild);
    }

    private static <T> SortedIntegerKeyNode<T> doubleRight(SortedIntegerKeyNode<T> tree) {
        Requires.notNull(tree, "tree");
        assert !tree.isEmpty();

        if (tree.left.isEmpty()) {
            return tree;
        }

        SortedIntegerKeyNode<T> rotatedLeftChild = tree.mutateLeft(rotateLeft(tree.left));
        return rotateRight(rotatedLeftChild);
    }

    private static <T> int balance(SortedIntegerKeyNode<T> tree) {
        Requires.notNull(tree, "tree");
        assert !tree.isEmpty();

        return tree.right.height - tree.left.height;
    }

    private static <T> boolean isRightHeavy(SortedIntegerKeyNode<T> tree) {
        Requires.notNull(tree, "tree");
        assert !tree.isEmpty();

        return balance(tree) >= 2;
    }

    private static <T> boolean isLeftHeavy(SortedIntegerKeyNode<T> tree) {
        Requires.notNull(tree, "tree");
        assert !tree.isEmpty();

        return balance(tree) <= -2;
    }

    private static <T> SortedIntegerKeyNode<T> makeBalanced(SortedIntegerKeyNode<T> tree) {
        Requires.notNull(tree, "tree");
        assert !tree.isEmpty();

        if (isRightHeavy(tree)) {
            return balance(tree.right) < 0 ? doubleLeft(tree) : rotateLeft(tree);
        }

        if (isLeftHeavy(tree)) {
            return balance(tree.left) > 0 ? doubleRight(tree) : rotateRight(tree);
        }

        return tree;
    }

    //private static <T> SortedIntegerKeyNode<T> nodeTreeFromList(OrderedCollection<Map.Entry<Integer, T>> items, int start, int length) {
    //    throw new UnsupportedOperationException();
    //}

    private SetItemResult<T> setOrAdd(int key, T value, EqualityComparator<? super T> valueComparator, boolean overwriteExistingValue) {
        // Arg validation skipped in this private method because it's recursive and the tax
        // of revalidating arguments on each recursive call is significant.
        // All our callers are therefore required to have done input validation.
        boolean replacedExistingValue = false;
        if (isEmpty()) {
            boolean mutated = true;
            SortedIntegerKeyNode<T> result = new SortedIntegerKeyNode<T>(key, value, this, this);
            return new SetItemResult<T>(result, replacedExistingValue, mutated);
        } else {
            boolean mutated;
            SortedIntegerKeyNode<T> result = this;
            if (key > this.key) {
                SetItemResult<T> subResult = right.setOrAdd(key, value, valueComparator, overwriteExistingValue);
                replacedExistingValue = subResult.replacedExistingValue;
                mutated = subResult.mutated;
                if (mutated) {
                    result = mutateRight(subResult.result);
                }
            } else if (key < this.key) {
                SetItemResult<T> subResult = left.setOrAdd(key, value, valueComparator, overwriteExistingValue);
                replacedExistingValue = subResult.replacedExistingValue;
                mutated = subResult.mutated;
                if (mutated) {
                    result = mutateLeft(subResult.result);
                }
            } else {
                if (valueComparator.equals(this.value, value)) {
                    mutated = false;
                    return new SetItemResult<T>(result, replacedExistingValue, mutated);
                } else if (overwriteExistingValue) {
                    mutated = true;
                    replacedExistingValue = true;
                    result = new SortedIntegerKeyNode<T>(key, value, left, right);
                } else {
                    throw new IllegalArgumentException("An element with the same key but a different value already exists. Key: " + key);
                }
            }

            result = mutated ? makeBalanced(result) : result;
            return new SetItemResult<T>(result, replacedExistingValue, mutated);
        }
    }

    private MutationResult<T> removeRecursive(int key) {
        if (isEmpty()) {
            return new MutationResult<T>(this, false);
        }

        SortedIntegerKeyNode<T> result = this;
        boolean mutated;
        if (key == this.key) {
            // We have a match.
            mutated = true;

            // If this is a leaf, just remove it by returning emptyNode(). If we have only one child, replace the node
            // with the child.
            if (right.isEmpty() && left.isEmpty()) {
                result = emptyNode();
            } else if (right.isEmpty() && !left.isEmpty()) {
                result = left;
            } else if (!right.isEmpty() && left.isEmpty()) {
                result = right;
            } else {
                // We have two children. Remove the next-highest node and replace this node with it.
                SortedIntegerKeyNode<T> successor = right;
                while (!successor.left.isEmpty()) {
                    successor = successor.left;
                }

                MutationResult<T> newRight = right.remove(successor.key);
                result = successor.mutate(left, newRight.result);
            }
        } else if (key < this.key) {
            MutationResult<T> newLeft = left.remove(key);
            mutated = newLeft.mutated;
            if (mutated) {
                result = mutateLeft(newLeft.result);
            }
        } else {
            MutationResult<T> newRight = right.remove(key);
            mutated = newRight.mutated;
            if (mutated) {
                result = mutateRight(newRight.result);
            }
        }

        return new MutationResult<T>(result.isEmpty() ? result : makeBalanced(result), mutated);
    }

    private SortedIntegerKeyNode<T> mutateLeft(SortedIntegerKeyNode<T> left) {
        return mutate(left, null);
    }

    private SortedIntegerKeyNode<T> mutateRight(SortedIntegerKeyNode<T> right) {
        return mutate(null, right);
    }

    private SortedIntegerKeyNode<T> mutate(SortedIntegerKeyNode<T> left, SortedIntegerKeyNode<T> right) {
        if (frozen) {
            return new SortedIntegerKeyNode<T>(key, value, left != null ? left : this.left, right != null ? right : this.right);
        } else {
            if (left != null) {
                this.left = left;
            }

            if (right != null) {
                this.right = right;
            }

            height = (byte)(1 + Math.max(this.left.height, this.right.height));
            return this;
        }
    }

    private SortedIntegerKeyNode<T> search(int key) {
        if (isEmpty() || key == this.key) {
            return this;
        }

        if (key > this.key) {
            return right.search(key);
        }

        return left.search(key);
    }

    static class MutationResult<T> {

        public final SortedIntegerKeyNode<T> result;
        public final boolean mutated;

        public MutationResult(SortedIntegerKeyNode<T> result, boolean mutated) {
            this.result = result;
            this.mutated = mutated;
        }
    }

    static final class SetItemResult<T> extends MutationResult<T> {

        public final boolean replacedExistingValue;

        public SetItemResult(SortedIntegerKeyNode<T> result, boolean replacedExistingValue, boolean mutated) {
            super(result, mutated);
            this.replacedExistingValue = replacedExistingValue;
        }
    }

    static final class IntegerKeyEntry<T> implements Map.Entry<Integer, T> {
        private final int key;
        private final T value;

        IntegerKeyEntry(int key, T value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public T setValue(T value) {
            throw new UnsupportedOperationException("This entry is read-only.");
        }
    }

    interface FreezeAction<T> {
        void apply(int key, T value);
    }
}
