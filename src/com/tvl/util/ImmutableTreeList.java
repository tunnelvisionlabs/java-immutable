// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.tvl.util.function.Function;
import com.tvl.util.function.Predicate;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class ImmutableTreeList<T> implements ImmutableList<T> {
    /**
     * An empty immutable list.
     */
    private static final ImmutableTreeList<?> EMPTY_LIST = new ImmutableTreeList<Object>();

    /**
     * The root node of the AVL tree that stores this set.
     */
    private final Node<T> root;

    private ImmutableTreeList() {
        root = Node.empty();
    }

    private ImmutableTreeList(Node<T> root) {
        Requires.notNull(root, "root");
        root.freeze();
        this.root = root;
    }

    public static <T> ImmutableTreeList<T> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static <T> ImmutableTreeList<T> create(T item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static <T> ImmutableTreeList<T> create(T... items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static <T> ImmutableTreeList<T> createAll(Iterable<T> items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static <T> ImmutableTreeList.Builder<T> createBuilder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static <T> ImmutableTreeList<T> empty() {
        @SuppressWarnings("unchecked") // safe
        ImmutableTreeList<T> result = (ImmutableTreeList<T>)EMPTY_LIST;
        return result;
    }

    @Override
    public ImmutableTreeList<T> clear() {
        return empty();
    }

    public int binarySearch(T item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int binarySearch(T item, Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int binarySearch(int index, int count, T item, Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEmpty() {
        return root.isEmpty();
    }

    @Override
    public int size() {
        return root.size();
    }

    @Override
    public T get(int index) {
        return root.get(index);
    }

    public Builder<T> toBuilder() {
        // We must not cache the instance created here and return it to various callers. Those who request a mutable
        // collection must get references to the collection that version independently of each other.
        return new Builder<T>(this);
    }

    @Override
    public ImmutableTreeList<T> add(T value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableTreeList<T> addAll(Iterable<? extends T> items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableTreeList<T> add(int index, T element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableTreeList<T> addAll(int index, Iterable<? extends T> items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> remove(T value) {
        return remove(value, EqualityComparators.defaultComparator());
    }

    @Override
    public ImmutableTreeList<T> remove(T value, EqualityComparator<? super T> equalityComparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableTreeList<T> remove(int index, int count) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> removeAll(Iterable<? extends T> items) {
        return removeAll(items, EqualityComparators.defaultComparator());
    }

    @Override
    public ImmutableTreeList<T> removeAll(Iterable<? extends T> items, EqualityComparator<? super T> equalityComparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableTreeList<T> remove(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableTreeList<T> removeIf(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableTreeList<T> set(int index, T value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> replace(T oldValue, T newValue) {
        return replace(oldValue, newValue, EqualityComparators.defaultComparator());
    }

    @Override
    public ImmutableTreeList<T> replace(T oldValue, T newValue, EqualityComparator<? super T> equalityComparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> reverse() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> reverse(int index, int count) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> sort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> sort(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> sort(int index, int count, Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void copyTo(T[] array) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void copyTo(T[] array, int startIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void copyTo(int index, T[] array, int startIndex, int count) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> getRange(int index, int count) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <U> ImmutableTreeList<U> convertAll(Function<T, U> converter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean exists(Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public T find(Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ImmutableTreeList<T> findAll(Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int findIndex(Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int findIndex(int startIndex, Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int findIndex(int startIndex, int count, Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public T findLast(Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int findLastIndex(Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int findLastIndex(int startIndex, Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int findLastIndex(int startIndex, int count, Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int indexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int lastIndexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean trueForAll(Predicate<? super T> match) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean contains(T value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int indexOf(T value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Itr<T> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    Node<T> getRoot() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <T> ImmutableTreeList<T> wrapNode(Node<T> root) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <T> ImmutableTreeList<T> tryCastToImmutableList(Iterable<T> sequence) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private ImmutableTreeList<T> wrap(Node<T> root) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private ImmutableTreeList<T> fillFromEmpty(Iterable<T> items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static final class Itr<T> implements Iterator<T> {
        @Override
        public boolean hasNext() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public T next() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("This iterator is read-only.");
        }
    }

    public static final class Builder<T> implements List<T>, ReadOnlyList<T> {
        private Node<T> root;

        private ImmutableTreeList<T> immutable;

        private int version;

        Builder(ImmutableTreeList<T> list) {
            Requires.notNull(list, "list");
            this.root = list.root;
            this.immutable = list;
        }

        @Override
        public int size() {
            return getRoot().size();
        }

        @Override
        public boolean isEmpty() {
            return getRoot().isEmpty();
        }

        int getVersion() {
            return version;
        }

        Node<T> getRoot() {
            return root;
        }

        void setRoot(Node<T> value) {
            // We *always* increment the version number because some mutations may not create a new value of root,
            // although the existing root instance may have mutated.
            version++;

            if (root != value) {
                root = value;

                // Clear any cached value for the immutable view since it is now invalidated.
                immutable = null;
            }
        }

        public T get(int index) {
            return getRoot().get(index);
        }

        @Override
        public T set(int index, T element) {
            T result = get(index);
            setRoot(getRoot().replace(index, element));
            return result;
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void add(int index, T element) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public T remove(int index) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean add(T t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clear() {
            setRoot(Node.<T>empty());
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Itr<T> iterator() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void copyTo(T[] array) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void copyTo(T[] array, int arrayIndex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void copyTo(int index, T[] array, int arrayIndex, int count) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ImmutableTreeList<T> getRange(int index, int count) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public <U> ImmutableTreeList<U> convertAll(Function<T, U> converter) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean exists(Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public T find(Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ImmutableTreeList<T> findAll(Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int findIndex(Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int findIndex(int startIndex, Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int findIndex(int startIndex, int count, Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public T findLast(Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int findLastIndex(Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int findLastIndex(int startIndex, Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int findLastIndex(int startIndex, int count, Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int indexOf(T item, int index) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int indexOf(T item, int index, int count) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int indexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int lastIndexOf(T item, int index) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int lastIndexOf(T item, int index, int count) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int lastIndexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean trueForAll(Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeIf(Predicate<? super T> match) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void reverse() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void reverse(int index, int count) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void sort() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void sort(Comparator<? super T> comparator) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void sort(int index, int count, Comparator<? super T> comparator) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int binarySearch(T item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int binarySearch(T item, Comparator<? super T> comparator) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int binarySearch(int index, int count, T item, Comparator<? super T> comparator) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ImmutableTreeList<T> toImmutable() {
            // Creating an instance of ImmutableList<T> with our root node automatically freezes our tree, ensuring that
            // the returned instance is immutable.  Any further mutations made to this builder will clone (and unfreeze)
            // the spine of modified nodes until the next time this method is invoked.
            if (immutable == null) {
                immutable = ImmutableTreeList.wrapNode(this.getRoot());
            }

            return immutable;
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T1> T1[] toArray(T1[] a) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ListIterator<T> listIterator() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * A node in the AVL tree storing this list.
     *
     * @param <T> The type of element stored in the list.
     */
    static final class Node<T> implements BinaryTree<T>, Iterable<T> {
        /**
         * The default empty node.
         */
        private static final Node<?> EMPTY_NODE = new Node<Object>();

        /**
         * The key associated with the node.
         */
        private T key;

        /**
         * A value indicating whether this node has been frozen (made immutable).
         *
         * <p>Nodes must be frozen before even being observed by a wrapping collection type to protect collections from
         * further mutations.</p>
         */
        private boolean frozen;

        /**
         * The depth of the tree beneath this node.
         */
        private byte height;

        /**
         * The number of elements contained by this subtree starting at this node.
         */
        private int count;

        /**
         * The left tree.
         */
        private Node<T> left;

        /**
         * The right tree.
         */
        private Node<T> right;

        /**
         * Initializes a new instance of the {@link Node} class which is pre-frozen.
         */
        private Node() {
            // The empty node is always frozen
            frozen = true;
        }

        /**
         * Initializes a new instance of the {@link Node} class that is not yet frozen.
         *
         * @param key The value stored by this node.
         * @param left The left branch.
         * @param right The right branch.
         */
        private Node(T key, Node<T> left, Node<T> right) {
            this(key, left, right, false);
        }

        /**
         * Initializes a new instance of the {@link Node} class.
         *
         * @param key The value stored by this node.
         * @param left The left branch.
         * @param right The right branch.
         * @param frozen {@code true} if the node should be pre-frozen; otherwise, {@code false}.
         */
        private Node(T key, Node<T> left, Node<T> right, boolean frozen) {
            Requires.notNull(left, "left");
            Requires.notNull(right, "right");
            assert !frozen || (left.frozen && right.frozen);

            this.key = key;
            this.left = left;
            this.right = right;
            this.height = (byte)(1 + Math.max(left.height, right.height));
            this.count = 1 + left.count + right.count;
            this.frozen = frozen;
        }

        /**
         * Gets an empty node.
         *
         * @param <T> The type of elements stored in the list.
         * @return An empty node.
         */
        public static <T> Node<T> empty() {
            Node<T> result = (Node<T>)EMPTY_NODE;
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
        public Node<T> getLeft() {
            return left;
        }

        @Override
        public Node<T> getRight() {
            return right;
        }

        @Override
        public T getValue() {
            return key;
        }

        @Override
        public int size() {
            return count;
        }

        /**
         * Gets the key.
         *
         * @return The key.
         */
        T getKey() {
            return key;
        }

        /**
         * Gets the element of the set at the given index.
         *
         * @param index The zero-based index of the element in the set to return.
         * @return The element at the given position.
         */
        T get(int index) {
            Requires.range(index >= 0 && index < size(), "index");

            if (index < left.count) {
                return left.get(index);
            }

            if (index > left.count) {
                return right.get(index - left.count - 1);
            }

            return key;
        }

        @Override
        public Iterator<T> iterator() {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Iterator<T> iterator(Builder<T> builder) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        static <T> Node<T> nodeTreeFromList(OrderedCollection<T> items, int start, int length) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> add(T key) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> insert(int index, T key) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> addAll(Iterable<? extends T> keys) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> addAll(int index, Iterable<? extends T> keys) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> remove(int index) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> removeIf(Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> replace(int index, T value) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> reverse() {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> reverse(int index, int count) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> sort() {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> sort(Comparator<? super T> comparator) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        Node<T> sort(int index, int count, Comparator<? super T> comparator) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int binarySearch(int index, int count, T item, Comparator<? super T> comparator) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int indexOf(T item, EqualityComparator<? super T> equalityComparator) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int indexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int lastIndexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        void copyTo(T[] array) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        void copyTo(T[] array, int arrayIndex) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        void copyTo(int index, T[] array, int arrayIndex, int count) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        <U> Node<U> convertAll(Function<T, U> converter) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        boolean trueForAll(Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        boolean exists(Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        T find(Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        ImmutableTreeList<T> findIf(Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int findIndex(Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int findIndex(int startIndex, Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int findIndex(int startIndex, int count, Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        T findLast(Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int findLastIndex(Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int findLastIndex(int startIndex, Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int findLastIndex(int startIndex, int count, Predicate<T> match) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        void freeze() {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private static <T> Node<T> rotateLeft(Node<T> tree) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private static <T> Node<T> rotateRight(Node<T> tree) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private static <T> Node<T> doubleLeft(Node<T> tree) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private static <T> Node<T> doubleRight(Node<T> tree) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private static int balance(Node<?> tree) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private static boolean isRightHeavy(Node<?> tree) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private static boolean isLeftHeavy(Node<?> tree) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private static <T> Node<T> makeBalanced(Node<T> tree) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private static <T> Node<T> balanceNode(Node<T> node) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        private Node<T> mutateLeft(Node<T> left) {
            return mutate(left, null);
        }

        private Node<T> mutateRight(Node<T> right) {
            return mutate(null, right);
        }

        private Node<T> mutate(Node<T> left, Node<T> right) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        /**
         * Creates a node mutation, either by mutating this node (if not yet frozen) or by creating a clone of this node
         * with the described changes.
         *
         * @param value The new value for this node.
         * @return The mutated (or created) node.
         */
        private Node<T> mutate(T value) {
            if (frozen) {
                return new Node<T>(value, left, right);
            } else {
                key = value;
                return this;
            }
        }
    }
}
