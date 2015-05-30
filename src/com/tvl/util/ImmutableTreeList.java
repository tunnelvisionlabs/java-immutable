// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.tvl.util.function.Function;
import com.tvl.util.function.Predicate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class ImmutableTreeList<T> implements ImmutableList<T>, ImmutableListQueries<T>, OrderedCollection<T> {
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
        return empty();
    }

    public static <T> ImmutableTreeList<T> create(T item) {
        return ImmutableTreeList.<T>empty().add(item);
    }

    public static <T> ImmutableTreeList<T> create(T... items) {
        return createAll(Arrays.asList(items));
    }

    public static <T> ImmutableTreeList<T> createAll(Iterable<T> items) {
        return ImmutableTreeList.<T>empty().addAll(items);
    }

    public static <T> ImmutableTreeList.Builder<T> createBuilder() {
        return ImmutableTreeList.<T>empty().toBuilder();
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
        return binarySearch(item, null);
    }

    public int binarySearch(T item, Comparator<? super T> comparator) {
        return binarySearch(0, size(), item, comparator);
    }

    public int binarySearch(int index, int count, T item, Comparator<? super T> comparator) {
        return root.binarySearch(index, count, item, comparator);
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
        ImmutableTreeList.Node<T> result = root.add(value);
        return wrap(result);
    }

    @Override
    public ImmutableTreeList<T> addAll(Iterable<? extends T> items) {
        Requires.notNull(items, "items");

        // Some optimizations may apply if we're an empty list.
        if (isEmpty()) {
            return fillFromEmpty(items);
        }

        Node<T> result = root.addAll(items);
        return wrap(result);
    }

    @Override
    public ImmutableTreeList<T> add(int index, T element) {
        Requires.range(index >= 0 && index <= size(), "index");
        return wrap(root.insert(index, element));
    }

    @Override
    public ImmutableTreeList<T> addAll(int index, Iterable<? extends T> items) {
        Requires.range(index >= 0 && index <= size(), "index");
        Requires.notNull(items, "items");

        Node<T> result = root.addAll(index, items);
        return wrap(result);
    }

    public ImmutableTreeList<T> remove(T value) {
        return remove(value, EqualityComparators.defaultComparator());
    }

    @Override
    public ImmutableTreeList<T> remove(T value, EqualityComparator<? super T> equalityComparator) {
        int index = indexOf(value, 0, size(), equalityComparator);
        return index < 0 ? this : remove(index);
    }

    @Override
    public ImmutableTreeList<T> remove(int index, int count) {
        Requires.range(index >= 0 && (index < size() || (index == size() && count == 0)), "index");
        Requires.range(count >= 0 && index + count <= size(), "count");

        Node<T> result = root;
        int remaining = count;
        while (remaining-- > 0) {
            result = result.remove(index);
        }

        return wrap(result);
    }

    public ImmutableTreeList<T> removeAll(Iterable<? extends T> items) {
        return removeAll(items, EqualityComparators.defaultComparator());
    }

    @Override
    public ImmutableTreeList<T> removeAll(Iterable<? extends T> items, EqualityComparator<? super T> equalityComparator) {
        Requires.notNull(items, "items");
        Requires.notNull(equalityComparator, "equalityComparator");

        // Some optimizations may apply if we're an empty list.
        if (isEmpty()) {
            return this;
        }

        // Let's not implement in terms of ImmutableList.Remove so that we're not unnecessarily generating a new list
        // object for each item.
        Node<T> result = root;
        for (T item : items) {
            int index = result.indexOf(item, equalityComparator);
            if (index >= 0) {
                result = result.remove(index);
            }
        }

        return wrap(result);
    }

    @Override
    public ImmutableTreeList<T> remove(int index) {
        Requires.range(index >= 0 && index < size(), "index");
        Node<T> result = root.remove(index);
        return wrap(result);
    }

    @Override
    public ImmutableTreeList<T> removeIf(Predicate<? super T> predicate) {
        Requires.notNull(predicate, "predicate");
        return wrap(root.removeIf(predicate));
    }

    @Override
    public ImmutableTreeList<T> set(int index, T value) {
        return wrap(root.replace(index, value));
    }

    public ImmutableTreeList<T> replace(T oldValue, T newValue) {
        return replace(oldValue, newValue, EqualityComparators.defaultComparator());
    }

    @Override
    public ImmutableTreeList<T> replace(T oldValue, T newValue, EqualityComparator<? super T> equalityComparator) {
        Requires.notNull(equalityComparator, "equalityComparator");

        int index = indexOf(oldValue, 0, size(), equalityComparator);
        Requires.argument(index >= 0, "oldValue", "Cannot find old value.");
        return set(index, newValue);
    }

    public ImmutableTreeList<T> reverse() {
        return wrap(root.reverse());
    }

    public ImmutableTreeList<T> reverse(int index, int count) {
        return wrap(root.reverse(index, count));
    }

    public ImmutableTreeList<T> sort() {
        return wrap(root.sort());
    }

    public ImmutableTreeList<T> sort(Comparator<? super T> comparator) {
        Requires.notNull(comparator, "comparator");
        return wrap(root.sort(comparator));
    }

    public ImmutableTreeList<T> sort(int index, int count, Comparator<? super T> comparator) {
        Requires.notNull(comparator, "comparator");
        Requires.range(index >= 0, "index");
        Requires.range(count >= 0, "count");
        Requires.range(index + count <= size(), "count");

        return wrap(root.sort(index, count, comparator));
    }

    @Override
    public void copyTo(T[] array) {
        Requires.notNull(array, "array");
        Requires.range(array.length >= size(), "array");

        root.copyTo(array);
    }

    @Override
    public void copyTo(T[] array, int startIndex) {
        Requires.notNull(array, "array");
        Requires.range(startIndex >= 0, "startIndex");
        Requires.range(array.length >= startIndex + size(), "startIndex");

        root.copyTo(array, startIndex);
    }

    @Override
    public void copyTo(int index, T[] array, int startIndex, int count) {
        root.copyTo(index, array, startIndex, count);
    }

    @Override
    public ImmutableTreeList<T> getRange(int index, int count) {
        Requires.range(index >= 0, "index");
        Requires.range(count >= 0, "count");
        Requires.range(index + count <= size(), "count");

        return wrap(Node.nodeTreeFromList(this, index, count));
    }

    @Override
    public <U> ImmutableTreeList<U> convertAll(Function<? super T, ? extends U> converter) {
        Requires.notNull(converter, "converter");
        return ImmutableTreeList.wrapNode(root.convertAll(converter));
    }

    @Override
    public boolean exists(Predicate<? super T> match) {
        Requires.notNull(match, "match");
        return root.exists(match);
    }

    @Override
    public T find(Predicate<? super T> match) {
        Requires.notNull(match, "match");
        return root.find(match);
    }

    @Override
    public ImmutableTreeList<T> findIf(Predicate<? super T> match) {
        Requires.notNull(match, "match");
        return root.findIf(match);
    }

    @Override
    public int findIndex(Predicate<? super T> match) {
        Requires.notNull(match, "match");
        return root.findIndex(match);
    }

    @Override
    public int findIndex(int startIndex, Predicate<? super T> match) {
        Requires.notNull(match, "match");
        Requires.range(startIndex >= 0, "startIndex");
        Requires.range(startIndex <= size(), "startIndex");
        return root.findIndex(startIndex, match);
    }

    @Override
    public int findIndex(int startIndex, int count, Predicate<? super T> match) {
        Requires.notNull(match, "match");
        Requires.range(startIndex >= 0, "startIndex");
        Requires.range(count >= 0, "count");
        Requires.range(startIndex + count <= size(), "count");

        return root.findIndex(startIndex, count, match);
    }

    @Override
    public T findLast(Predicate<? super T> match) {
        Requires.notNull(match, "match");
        return root.findLast(match);
    }

    @Override
    public int findLastIndex(Predicate<? super T> match) {
        Requires.notNull(match, "match");
        return root.findLastIndex(match);
    }

    @Override
    public int findLastIndex(int startIndex, Predicate<? super T> match) {
        Requires.notNull(match, "match");
        Requires.range(startIndex >= 0, "startIndex");
        Requires.range(startIndex == 0 || startIndex < size(), "startIndex");
        return root.findLastIndex(startIndex, match);
    }

    @Override
    public int findLastIndex(int startIndex, int count, Predicate<? super T> match) {
        Requires.notNull(match, "match");
        Requires.range(startIndex >= 0, "startIndex");
        Requires.range(count <= size(), "count");
        Requires.range(startIndex - count + 1 >= 0, "startIndex");

        return root.findLastIndex(startIndex, count, match);
    }

    @Override
    public int indexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
        return root.indexOf(item, index, count, equalityComparator);
    }

    @Override
    public int lastIndexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
        return root.lastIndexOf(item, index, count, equalityComparator);
    }

    @Override
    public boolean trueForAll(Predicate<? super T> match) {
        Requires.notNull(match, "match");
        return root.trueForAll(match);
    }

    public boolean contains(T value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(T value) {
        return indexOf(value, 0, size(), EqualityComparators.defaultComparator());
    }

    @Override
    public Itr<T> iterator() {
        return new Itr<T>(root);
    }

    Node<T> getRoot() {
        return root;
    }

    private static <T> ImmutableTreeList<T> wrapNode(Node<T> root) {
        return root.isEmpty() ? ImmutableTreeList.<T>empty() : new ImmutableTreeList<T>(root);
    }

    private static <T> ImmutableTreeList<T> tryCastToImmutableList(Iterable<T> sequence) {
        if (sequence instanceof ImmutableTreeList<?>) {
            return (ImmutableTreeList<T>)sequence;
        }

        if (sequence instanceof Builder<?>) {
            return ((ImmutableTreeList.Builder<T>)sequence).toImmutable();
        }

        return null;
    }

    private static <T> OrderedCollection<T> asOrderedCollection(Iterable<T> sequence) {
        Requires.notNull(sequence, "sequence");

        if (sequence instanceof OrderedCollection<?>) {
            return (OrderedCollection<T>)sequence;
        }

        if (sequence instanceof List<?>) {
            return new ListOfTWrapper<T>((List<T>)sequence);
        }

        // It would be great if TreeSet<T> and TreeMap<T> provided indexers into their collections, but since they don't
        // we have to clone them to an array.
        return new FallbackWrapper<T>(sequence);
    }

    private ImmutableTreeList<T> wrap(Node<T> root) {
        if (root != this.root) {
            return root.isEmpty() ? clear() : new ImmutableTreeList<T>(root);
        } else {
            return this;
        }
    }

    private ImmutableTreeList<T> fillFromEmpty(Iterable<? extends T> items) {
        assert isEmpty();

        // If the items being added actually come from an ImmutableList<T> then there is no value in reconstructing it.
        ImmutableTreeList<? extends T> other = tryCastToImmutableList(items);
        if (other != null) {
            @SuppressWarnings("unchecked") // safe
            ImmutableTreeList<T> result = (ImmutableTreeList<T>)other;
            return result;
        }

        // Rather than build up the immutable structure in the incremental way, build it in such a way as to generate
        // minimal garbage, by assembling the immutable binary tree from leaf to root.  This requires that we know the
        // length of the item sequence in advance, and can index into that sequence like a list, so the one possible
        // piece of garbage produced is a temporary array to store the list while we build the tree.
        OrderedCollection<? extends T> list = asOrderedCollection(items);
        if (list.size() == 0) {
            return this;
        }

        Node<T> root = Node.nodeTreeFromList(list, 0, list.size());
        return new ImmutableTreeList<T>(root);
    }

    public static final class Itr<T> implements Iterator<T> {
        private final Builder<T> builder;

        private final int startIndex;

        private final int count;

        private int remainingCount;

        private boolean reversed;

        private Node<T> root;

        private Deque<Node<T>> stack;

        private Node<T> current;

        private int iteratingBuilderVersion;

        Itr(Node<T> root) {
            this(root, null, -1, -1, false);
        }

        Itr(Node<T> root, Builder<T> builder, int startIndex, int count, boolean reversed) {
            Requires.notNull(root, "root");
            Requires.range(startIndex >= -1, "startIndex");
            Requires.range(count >= -1, "count");
            Requires.argument(reversed || count == -1 || (startIndex == -1 ? 0 : startIndex) + count <= root.size());
            Requires.argument(!reversed || count == -1 || (startIndex == -1 ? root.size() - 1 : startIndex) - count + 1 >= 0);

            this.root = root;
            this.builder = builder;
            this.startIndex = startIndex >= 0 ? startIndex : (reversed ? root.size() - 1 : 0);
            this.count = count == -1 ? root.size() : count;
            this.remainingCount = this.count;
            this.reversed = reversed;
            this.iteratingBuilderVersion = builder != null ? builder.getVersion() : -1;
            //this.poolUserId = SecureObjectPool.newId();
            this.stack = null;
            if (this.count > 0) {
                this.stack = new ArrayDeque<Node<T>>();
                resetStack();
            }
        }

        @Override
        public boolean hasNext() {
            throwIfChanged();
            return remainingCount > 0;
        }

        @Override
        public T next() {
            if (stack != null) {
                if (remainingCount > 0 && !stack.isEmpty()) {
                    Node<T> n = stack.pop();
                    current = n;
                    pushNext(nextBranch(n));
                    remainingCount--;
                    return current.key;
                }
            }

            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("This iterator is read-only.");
        }

        private void resetStack() {
            stack.clear();

            Node<T> node = root;
            int skipNodes = reversed ? root.size() - startIndex - 1 : startIndex;
            while (!node.isEmpty() && skipNodes != previousBranch(node).size()) {
                if (skipNodes < previousBranch(node).size()) {
                    stack.push(node);
                    node = previousBranch(node);
                } else {
                    skipNodes -= previousBranch(node).size() + 1;
                    node = nextBranch(node);
                }
            }

            if (!node.isEmpty()) {
                stack.push(node);
            }
        }

        private Node<T> nextBranch(Node<T> node) {
            return reversed ? node.left : node.right;
        }

        private Node<T> previousBranch(Node<T> node) {
            return reversed ? node.right : node.left;
        }

        private void pushNext(Node<T> node) {
            Requires.notNull(node, "node");
            if (!node.isEmpty()) {
                Deque<Node<T>> stack = this.stack;
                while (!node.isEmpty()) {
                    stack.push(node);
                    node = previousBranch(node);
                }
            }
        }

        private void throwIfChanged() {
            if (builder != null && builder.getVersion() != iteratingBuilderVersion) {
                throw new IllegalStateException();
            }
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
            Requires.notNull(array, "array");
            Requires.range(array.length >= size(), "array");
            root.copyTo(array);
        }

        public void copyTo(T[] array, int arrayIndex) {
            Requires.notNull(array, "array");
            Requires.range(array.length >= arrayIndex + size(), "arrayIndex");
            root.copyTo(array, arrayIndex);
        }

        public void copyTo(int index, T[] array, int arrayIndex, int count) {
            root.copyTo(index, array, arrayIndex, count);
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
            @SuppressWarnings("unchecked")
            T[] result = (T[])new Object[size()];
            copyTo(result);
            return result;
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
            @SuppressWarnings("unchecked") // safe
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
        public Itr<T> iterator() {
            return new Itr<T>(this);
        }

        Iterator<T> iterator(Builder<T> builder) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        static <T> Node<T> nodeTreeFromList(OrderedCollection<? extends T> items, int start, int length) {
            Requires.notNull(items, "items");
            Requires.range(start >= 0, "start");
            Requires.range(length >= 0, "length");

            if (length == 0) {
                return empty();
            }

            int rightCount = (length - 1) / 2;
            int leftCount = (length - 1) - rightCount;
            Node<T> left = nodeTreeFromList(items, start, leftCount);
            Node<T> right = nodeTreeFromList(items, start + leftCount + 1, rightCount);
            return new Node<T>(items.get(start + leftCount), left, right, true);
        }

        Node<T> add(T key) {
            return insert(count, key);
        }

        Node<T> insert(int index, T key) {
            Requires.range(index >= 0 && index <= size(), "index");

            if (isEmpty()) {
                return new Node<T>(key, this, this);
            } else {
                Node<T> result;
                if (index <= left.count) {
                    Node<T> newLeft = left.insert(index, key);
                    result = mutateLeft(newLeft);
                } else {
                    Node<T> newRight = right.insert(index - left.count - 1, key);
                    result = mutateRight(newRight);
                }

                return makeBalanced(result);
            }
        }

        Node<T> addAll(Iterable<? extends T> keys) {
            return addAll(size(), keys);
        }

        Node<T> addAll(int index, Iterable<? extends T> keys) {
            Requires.range(index >= 0 && index <= size(), "index");
            Requires.notNull(keys, "keys");

            if (isEmpty()) {
                ImmutableTreeList<? extends T> other = tryCastToImmutableList(keys);
                if (other != null) {
                    @SuppressWarnings("unchecked") // safe
                    Node<T> result = (Node<T>)other.root;
                    return result;
                }

                OrderedCollection<? extends T> list = asOrderedCollection(keys);
                return Node.nodeTreeFromList(list, 0, list.size());
            } else {
                Node<T> result;
                if (index <= left.count) {
                    Node<T> newLeft = left.addAll(index, keys);
                    result = mutateLeft(newLeft);
                } else {
                    Node<T> newRight = right.addAll(index - left.count - 1, keys);
                    result = mutateRight(newRight);
                }

                return balanceNode(result);
            }
        }

        Node<T> remove(int index) {
            Requires.range(index >= 0 && index < size(), "index");

            Node<T> result = this;
            if (index == left.count) {
                // We have a match. If this is a leaf, just remove it by returning empty(). If we have only one child,
                // replace the node with the child.
                if (right.isEmpty() && left.isEmpty()) {
                    result = empty();
                } else if (right.isEmpty() && !left.isEmpty()) {
                    result = left;
                } else if (!right.isEmpty() && left.isEmpty()) {
                    result = right;
                } else {
                    // We have two children. Remove the next-highest node and replace this node with it.
                    Node<T> successor = right;
                    while (!successor.left.isEmpty()) {
                        successor = successor.left;
                    }

                    Node<T> newRight = right.remove(0);
                    result = successor.mutate(left, newRight);
                }
            } else if (index < left.count) {
                Node<T> newLeft = left.remove(index);
                result = mutateLeft(newLeft);
            } else {
                Node<T> newRight = right.remove(index - left.count - 1);
                result = mutateRight(newRight);
            }

            return result.isEmpty() ? result : makeBalanced(result);
        }

        Node<T> removeIf(Predicate<? super T> match) {
            Requires.notNull(match, "match");

            Node<T> result = this;
            int index = 0;
            for (T item : this) {
                if (match.test(item)) {
                    result = result.remove(index);
                } else {
                    index++;
                }
            }

            return result;
        }

        Node<T> replace(int index, T value) {
            Requires.range(index >= 0 && index <= size(), "index");

            Node<T> result = this;
            if (index == left.count) {
                // We have a match.
                result = mutate(value);
            } else if (index < left.count) {
                Node<T> newLeft = left.replace(index, value);
                result = mutateLeft(newLeft);
            } else {
                Node<T> newRight = right.replace(index - left.count - 1, value);
                result = mutateRight(newRight);
            }

            return result;
        }

        Node<T> reverse() {
            return reverse(0, size());
        }

        Node<T> reverse(int index, int count) {
            Requires.range(index >= 0, "index");
            Requires.range(count >= 0, "count");
            Requires.range(index + count <= size(), "index");

            Node<T> result = this;
            int start = index;
            int end = index + count - 1;
            while (start < end) {
                T a = result.get(start);
                T b = result.get(end);
                result = result.replace(end, a).replace(start, b);
                start++;
                end--;
            }

            return result;
        }

        Node<T> sort() {
            @SuppressWarnings("unchecked") // will throw at runtime if invalid.
            Comparator<? super T> comparator = (Comparator<? super T>)(Comparator<?>)Comparators.<Integer>defaultComparator();
            return sort(comparator);
        }

        Node<T> sort(Comparator<? super T> comparator) {
            Requires.notNull(comparator, "comparator");
            return sort(0, size(), comparator);
        }

        Node<T> sort(int index, int count, Comparator<? super T> comparator) {
            Requires.range(index >= 0, "index");
            Requires.range(count >= 0, "count");
            Requires.argument(index + count <= size());
            Requires.notNull(comparator, "comparer");

            // PERF: Eventually this might be reimplemented in a way that does not require allocating an array.
            ArrayList<T> arrayList = new ArrayList<T>(ImmutableTreeList.wrapNode(this).toBuilder());
            //Array.Sort(array, index, count, comparer);
            Collections.sort(arrayList.subList(index, index + count), comparator);
            return nodeTreeFromList(asOrderedCollection(arrayList), 0, size());
        }

        int binarySearch(int index, int count, T item, Comparator<? super T> comparator) {
            Requires.range(index >= 0, "index");
            Requires.range(count >= 0, "count");
            if (comparator == null) {
                @SuppressWarnings("unchecked") // will throw at runtime if invalid.
                Comparator<? super T> defaultComparator = (Comparator<? super T>)(Comparator<?>)Comparators.<Integer>defaultComparator();
                comparator = defaultComparator;
            }

            if (isEmpty() || count <= 0) {
                return ~index;
            }

            // If this node is not within range, defer to either branch as appropriate.
            int thisNodeIndex = left.size(); // this is only the index within the AVL tree, treating this node as root rather than a member of a larger tree.
            if (index + count <= thisNodeIndex) {
                return left.binarySearch(index, count, item, comparator);
            } else if (index > thisNodeIndex) {
                int result = right.binarySearch(index - thisNodeIndex - 1, count, item, comparator);
                int offset = thisNodeIndex + 1;
                return result < 0 ? result - offset : result + offset;
            }

            // We're definitely in the caller's designated range now.
            // Any possible match will be a descendant of this node (or this immediate one).
            // Some descendants may not be in range, but if we hit any it means no match was found,
            // and a negative response would come back from the above code to the below code.
            int compare = comparator.compare(item, key);
            if (compare == 0) {
                return thisNodeIndex;
            } else if (compare > 0) {
                int adjustedCount = count - (thisNodeIndex - index) - 1;
                int result = adjustedCount < 0 ? -1 : right.binarySearch(0, adjustedCount, item, comparator);
                int offset = thisNodeIndex + 1;
                return result < 0 ? result - offset : result + offset;
            } else {
                if (index == thisNodeIndex) {
                    // We can't go any further left.
                    return ~index;
                }

                int result = left.binarySearch(index, count, item, comparator);
                //return result < 0 ? result - thisNodeIndex : result + thisNodeIndex;
                return result;
            }
        }

        int indexOf(T item, EqualityComparator<? super T> equalityComparator) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        int indexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
            Requires.range(index >= 0, "index");
            Requires.range(count >= 0, "count");
            Requires.range(count <= size(), "count");
            Requires.range(index + count <= size(), "count");
            Requires.notNull(equalityComparator, "equalityComparator");

            Iterator<T> iterator = new Itr<T>(this, null, index, count, false);
            while (iterator.hasNext()) {
                if (equalityComparator.equals(item, iterator.next())) {
                    return index;
                }

                index++;
            }

            return -1;
        }

        int lastIndexOf(T item, int index, int count, EqualityComparator<? super T> equalityComparator) {
            Requires.notNull(equalityComparator, "equalityComparator");
            Requires.range(index >= 0, "index");
            Requires.range(count >= 0 && count <= size(), "count");
            Requires.argument(index - count + 1 >= 0);

            Iterator<T> iterator = new Itr<T>(this, null, index, count, true);
            while (iterator.hasNext()) {
                if (equalityComparator.equals(item, iterator.next())) {
                    return index;
                }

                index--;
            }

            return -1;
        }

        void copyTo(T[] array) {
            Requires.notNull(array, "array");
            Requires.argument(array.length >= size());

            int index = 0;
            for (T element : this) {
                array[index++] = element;
            }
        }

        void copyTo(T[] array, int arrayIndex) {
            Requires.notNull(array, "array");
            Requires.range(arrayIndex >= 0, "arrayIndex");
            Requires.range(arrayIndex <= array.length, "arrayIndex");
            Requires.argument(arrayIndex + size() <= array.length);

            for (T element : this) {
                array[arrayIndex++] = element;
            }
        }

        void copyTo(int index, T[] array, int arrayIndex, int count) {
            Requires.notNull(array, "array");
            Requires.range(index >= 0, "index");
            Requires.range(count >= 0, "count");
            Requires.range(index + count <= size(), "count");
            Requires.range(arrayIndex >= 0, "arrayIndex");
            Requires.range(arrayIndex + count <= array.length, "arrayIndex");

            Iterator<? extends T> iterator = new Itr<T>(this, null, index, count, false);
            while (iterator.hasNext()) {
                array[arrayIndex++] = iterator.next();
            }
        }

        <U> Node<U> convertAll(Function<? super T, ? extends U> converter) {
            Node<U> root = ImmutableTreeList.Node.empty();
            if (isEmpty()) {
                return root;
            }

            for (T item : this) {
                root = root.add(converter.apply(item));
            }

            return root;
        }

        boolean trueForAll(Predicate<? super T> match) {
            for (T item : this) {
                if (!match.test(item)) {
                    return false;
                }
            }

            return true;
        }

        boolean exists(Predicate<? super T> match) {
            Requires.notNull(match, "match");

            for (T item : this) {
                if (match.test(item)) {
                    return true;
                }
            }

            return false;
        }

        T find(Predicate<? super T> match) {
            Requires.notNull(match, "match");

            for (T item : this) {
                if (match.test(item)) {
                    return item;
                }
            }

            return null;
        }

        ImmutableTreeList<T> findIf(Predicate<? super T> match) {
            Requires.notNull(match, "match");

            if (isEmpty()) {
                return ImmutableTreeList.empty();
            }

            ArrayList<T> list = null;
            for (T item : this) {
                if (match.test(item)) {
                    if (list == null) {
                        list = new ArrayList<T>();
                    }

                    list.add(item);
                }
            }

            return list != null
                ? ImmutableTreeList.createAll(list)
                : ImmutableTreeList.<T>empty();
        }

        int findIndex(Predicate<? super T> match) {
            Requires.notNull(match, "match");

            return this.findIndex(0, count, match);
        }

        int findIndex(int startIndex, Predicate<? super T> match) {
            Requires.range(startIndex >= 0, "startIndex");
            Requires.range(startIndex <= size(), "startIndex");
            Requires.notNull(match, "match");

            return findIndex(startIndex, size() - startIndex, match);
        }

        int findIndex(int startIndex, int count, Predicate<? super T> match) {
            Requires.range(startIndex >= 0, "startIndex");
            Requires.range(count >= 0, "count");
            Requires.argument(startIndex + count <= size());
            Requires.notNull(match, "match");

            Iterator<T> iterator = new Itr<T>(this, null, startIndex, count, false);
            int index = startIndex;
            while (iterator.hasNext()) {
                if (match.test(iterator.next())) {
                    return index;
                }

                index++;
            }

            return -1;
        }

        T findLast(Predicate<? super T> match) {
            Requires.notNull(match, "match");

            Itr<T> iterator = new Itr<T>(this, null, -1, -1, true);
            while (iterator.hasNext()) {
                T current = iterator.next();
                if (match.test(current)) {
                    return current;
                }
            }

            return null;
        }

        int findLastIndex(Predicate<? super T> match) {
            Requires.notNull(match, "match");

            if (isEmpty()) {
                return -1;
            }

            return findLastIndex(size() - 1, size(), match);
        }

        int findLastIndex(int startIndex, Predicate<? super T> match) {
            Requires.notNull(match, "match");
            Requires.range(startIndex >= 0, "startIndex");
            Requires.range(startIndex == 0 || startIndex < size(), "startIndex");

            if (isEmpty()) {
                return -1;
            }

            return findLastIndex(startIndex, startIndex + 1, match);
        }

        int findLastIndex(int startIndex, int count, Predicate<? super T> match) {
            Requires.notNull(match, "match");
            Requires.range(startIndex >= 0, "startIndex");
            Requires.range(count <= size(), "count");
            Requires.argument(startIndex - count + 1 >= 0);

            Iterator<T> iterator = new Itr<T>(this, null, startIndex, count, true);
            int index = startIndex;
            while (iterator.hasNext()) {
                if (match.test(iterator.next())) {
                    return index;
                }

                index--;
            }

            return -1;
        }

        /**
         * Freezes this node and all descendant nodes so that any mutations require a new instance of the nodes.
         */
        void freeze() {
            // If this node is frozen, all its descendants must already be frozen.
            if (!frozen) {
                left.freeze();
                right.freeze();
                frozen = true;
            }
        }

        private static <T> Node<T> rotateLeft(Node<T> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();

            if (tree.right.isEmpty()) {
                return tree;
            }

            Node<T> right = tree.right;
            return right.mutateLeft(tree.mutateRight(right.left));
        }

        private static <T> Node<T> rotateRight(Node<T> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();

            if (tree.left.isEmpty()) {
                return tree;
            }

            Node<T> left = tree.left;
            return left.mutateRight(tree.mutateLeft(left.right));
        }

        private static <T> Node<T> doubleLeft(Node<T> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();

            if (tree.right.isEmpty()) {
                return tree;
            }

            Node<T> rotatedRightChild = tree.mutateRight(rotateRight(tree.right));
            return rotateLeft(rotatedRightChild);
        }

        private static <T> Node<T> doubleRight(Node<T> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();

            if (tree.left.isEmpty()) {
                return tree;
            }

            Node<T> rotatedLeftChild = tree.mutateLeft(rotateLeft(tree.left));
            return rotateRight(rotatedLeftChild);
        }

        private static int balance(Node<?> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();

            return tree.right.height - tree.left.height;
        }

        private static boolean isRightHeavy(Node<?> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();
            return balance(tree) >= 2;
        }

        private static boolean isLeftHeavy(Node<?> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();
            return balance(tree) <= -2;
        }

        private static <T> Node<T> makeBalanced(Node<T> tree) {
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

        private static <T> Node<T> balanceNode(Node<T> node) {
            while (isRightHeavy(node) || isLeftHeavy(node)) {
                if (isRightHeavy(node)) {
                    node = balance(node.right) < 0 ? doubleLeft(node) : rotateLeft(node);
                    node.mutateLeft(balanceNode(node.left));
                } else {
                    node = balance(node.left) > 0 ? doubleRight(node) : rotateRight(node);
                    node.mutateRight(balanceNode(node.right));
                }
            }

            return node;
        }

        private Node<T> mutateLeft(Node<T> left) {
            return mutate(left, null);
        }

        private Node<T> mutateRight(Node<T> right) {
            return mutate(null, right);
        }

        private Node<T> mutate(Node<T> left, Node<T> right) {
            if (frozen) {
                return new Node<T>(key, left != null ? left : this.left, right != null ? right : this.right);
            } else {
                if (left != null) {
                    this.left = left;
                }

                if (right != null) {
                    this.right = right;
                }

                height = (byte)(1 + Math.max(this.left.height, this.right.height));
                count = 1 + this.left.count + this.right.count;
                return this;
            }
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

    private static final class ListOfTWrapper<T> implements OrderedCollection<T> {
        private final List<T> collection;

        ListOfTWrapper(List<T> collection) {
            Requires.notNull(collection, "collection");
            this.collection = collection;
        }

        @Override
        public int size() {
            return collection.size();
        }

        @Override
        public T get(int index) {
            return collection.get(index);
        }

        @Override
        public Iterator<T> iterator() {
            return collection.iterator();
        }
    }

    private static final class FallbackWrapper<T> implements OrderedCollection<T> {
        private final Iterable<T> sequence;

        private List<T> collection;

        FallbackWrapper(Iterable<T> sequence) {
            Requires.notNull(sequence, "sequence");
            this.sequence = sequence;
        }

        @Override
        public int size() {
            if (collection == null) {
                Integer count = Immutables.tryGetCount(sequence);
                if (count != null) {
                    return count;
                }

                List<T> list = new ArrayList<T>();
                for (T item : sequence) {
                    list.add(item);
                }

                collection = list;
            }

            return collection.size();
        }

        @Override
        public T get(int index) {
            if (collection == null) {
                List<T> list = new ArrayList<T>();
                for (T item : sequence) {
                    list.add(item);
                }

                collection = list;
            }

            return collection.get(index);
        }

        @Override
        public Iterator<T> iterator() {
            return sequence.iterator();
        }
    }
}
