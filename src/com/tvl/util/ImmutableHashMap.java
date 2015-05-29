// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * An immutable unordered map implementation.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public final class ImmutableHashMap<K, V> implements ImmutableMap<K, V> {

    /**
     * An empty immutable map with default equality comparators.
     */
    private static final ImmutableHashMap<?, ?> EMPTY_MAP = new ImmutableHashMap<Object, Object>();

    private static final SortedIntegerKeyNode.FreezeAction<HashBucket<?, ?>> FREEZE_BUCKET_ACTION = new SortedIntegerKeyNode.FreezeAction<HashBucket<?, ?>>() {

        @Override
        public void apply(int t, HashBucket<?, ?> u) {
            u.freeze();
        }
    };

    /**
     * This is the backing field for {@link #size()}.
     */
    private final int count;

    /**
     * The root node of the tree that stores this map.
     */
    private final SortedIntegerKeyNode<HashBucket<K, V>> root;

    /**
     * The comparer used when comparing hash buckets.
     */
    private final Comparators<K, V> comparators;

    private ImmutableHashMap() {
        this(null);
    }

    private ImmutableHashMap(Comparators<K, V> comparators) {
        this.comparators = comparators != null ? comparators : Comparators.<K, V>get(EqualityComparators.defaultComparator(), EqualityComparators.defaultComparator());
        this.root = SortedIntegerKeyNode.<HashBucket<K, V>>emptyNode();
        this.count = 0;
    }

    private ImmutableHashMap(SortedIntegerKeyNode<HashBucket<K, V>> root, Comparators<K, V> comparators, int count) {
        Requires.notNull(root, "root");
        Requires.notNull(comparators, "comparators");

        root.freeze(FREEZE_BUCKET_ACTION);
        this.root = root;
        this.count = count;
        this.comparators = comparators != null ? comparators : Comparators.<K, V>get(EqualityComparators.defaultComparator(), EqualityComparators.defaultComparator());
    }

    /**
     * Gets an empty immutable map with default equality comparators.
     */
    public static <K, V> ImmutableHashMap<K, V> empty() {
        @SuppressWarnings("unchecked") // safe
        ImmutableHashMap<K, V> result = (ImmutableHashMap<K, V>)EMPTY_MAP;
        return result;
    }

    public static <K, V> ImmutableHashMap<K, V> create() {
        return empty();
    }

    public static <K, V> ImmutableHashMap<K, V> create(EqualityComparator<K> keyComparator) {
        return ImmutableHashMap.<K, V>empty().withComparators(keyComparator);
    }

    public static <K, V> ImmutableHashMap<K, V> create(EqualityComparator<K> keyComparator, EqualityComparator<V> valueComparator) {
        return ImmutableHashMap.<K, V>empty().withComparators(keyComparator, valueComparator);
    }

    public static <K, V> ImmutableHashMap<K, V> createAll(Iterable<Map.Entry<K, V>> items) {
        return ImmutableHashMap.<K, V>empty().addAll(items);
    }

    public static <K, V> ImmutableHashMap<K, V> createAll(EqualityComparator<K> keyComparator, Iterable<Map.Entry<K, V>> items) {
        return ImmutableHashMap.<K, V>create(keyComparator).addAll(items);
    }

    public static <K, V> ImmutableHashMap<K, V> createAll(EqualityComparator<K> keyComparator, EqualityComparator<V> valueComparator, Iterable<Map.Entry<K, V>> items) {
        return ImmutableHashMap.create(keyComparator, valueComparator).addAll(items);
    }

    public static <K, V> ImmutableHashMap.Builder<K, V> createBuilder() {
        return ImmutableHashMap.<K, V>create().toBuilder();
    }

    public static <K, V> ImmutableHashMap.Builder<K, V> createBuilder(EqualityComparator<K> keyComparator) {
        return ImmutableHashMap.<K, V>create(keyComparator).toBuilder();
    }

    public static <K, V> ImmutableHashMap.Builder<K, V> createBuilder(EqualityComparator<K> keyComparator, EqualityComparator<V> valueComparator) {
        return ImmutableHashMap.create(keyComparator, valueComparator).toBuilder();
    }

    private static <K, V> ImmutableHashMap<K, V> emptyWithComparators(Comparators<K, V> comparators) {
        Requires.notNull(comparators, "comparators");

        if (empty().comparators == comparators) {
            return empty();
        }

        return new ImmutableHashMap<K, V>(comparators);
    }

    private static <K, V> ImmutableHashMap<K, V> tryCastToImmutableMap(Iterable<Map.Entry<K, V>> sequence) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <K, V> boolean containsKey(K key, MutationInput<K, V> origin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <K, V> boolean contains(Map.Entry<K, V> keyValuePair, MutationInput<K, V> origin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <K, V> V getValue(K key, MutationInput<K, V> origin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <K, V> K getKey(K equalKey, MutationInput<K, V> origin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <K, V> MutationResult<K, V> add(K key, V value, KeyCollisionBehavior behavior, MutationInput<K, V> origin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <K, V> MutationResult<K, V> addAll(Iterable<Map.Entry<K, V>> items, MutationInput<K, V> origin) {
        return addAll(items, origin, KeyCollisionBehavior.THROW_IF_VALUE_DIFFERENT);
    }

    private static <K, V> MutationResult<K, V> addAll(Iterable<Map.Entry<K, V>> items, MutationInput<K, V> origin, KeyCollisionBehavior behavior) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <K, V> MutationResult<K, V> remove(K key, MutationInput<K, V> origin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <K, V> SortedIntegerKeyNode<HashBucket<K, V>> updateRoot(SortedIntegerKeyNode<HashBucket<K, V>> root, int hashCode, HashBucket<K, V> newBucket, EqualityComparator<HashBucket<K, V>> hashBucketComparator) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static <K, V> ImmutableHashMap<K, V> wrap(SortedIntegerKeyNode<HashBucket<K, V>> root, Comparators<K, V> comparators, int count) {
        Requires.notNull(root, "root");
        Requires.notNull(comparators, "comparators");
        Requires.range(count >= 0, "count");
        return new ImmutableHashMap<K, V>(root, comparators, count);
    }

    @Override
    public ImmutableHashMap<K, V> clear() {
        return isEmpty() ? this : emptyWithComparators(comparators);
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    public EqualityComparator<? super K> getKeyComparator() {
        return comparators.getKeyComparator();
    }

    public EqualityComparator<? super V> getValueComparator() {
        return comparators.getValueComparator();
    }

    @Override
    public Iterable<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<V> values() {
        throw new UnsupportedOperationException();
    }

    private MutationInput<K, V> getOrigin() {
        return new MutationInput<K, V>(this);
    }

    public Builder<K, V> toBuilder() {
        return new Builder<K, V>(this);
    }

    @Override
    public ImmutableHashMap<K, V> add(K key, V value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableHashMap<K, V> addAll(Iterable<Map.Entry<K, V>> entries) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableHashMap<K, V> put(K key, V value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableHashMap<K, V> putAll(Iterable<Map.Entry<K, V>> entries) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableHashMap<K, V> remove(K key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImmutableHashMap<K, V> removeAll(Iterable<K> keys) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsKey(K key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Map.Entry<K, V> pair) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public V get(K key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public K getKey(K equalKey) {
        Requires.notNull(equalKey, "equalKey");
        return getKey(equalKey, getOrigin());
    }

    public ImmutableHashMap<K, V> withComparators(EqualityComparator<? super K> keyComparator) {
        return withComparators(keyComparator, comparators.getValueComparator());
    }

    public ImmutableHashMap<K, V> withComparators(EqualityComparator<? super K> keyComparator, EqualityComparator<? super V> valueComparator) {
        if (keyComparator == null) {
            keyComparator = EqualityComparators.defaultComparator();
        }

        if (valueComparator == null) {
            valueComparator = EqualityComparators.defaultComparator();
        }

        if (getKeyComparator() == keyComparator) {
            if (getValueComparator() == valueComparator) {
                return this;
            } else {
                // When the key comparator is the same but the value comparator is different, we don't need a whole new
                // tree because the structure of the tree does not depend on the value comparator. We just need a new
                // root node to store the new value comparator.
                Comparators<K, V> comparators = this.comparators.withValueComparator(valueComparator);
                return new ImmutableHashMap<K, V>(root, comparators, count);
            }
        } else {
            Comparators<K, V> comparators = Comparators.get(keyComparator, valueComparator);
            ImmutableHashMap<K, V> map = new ImmutableHashMap<K, V>(comparators);
            map = map.addAll(entrySet());
            return map;
        }
    }

    public boolean containsValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private ImmutableHashMap<K, V> wrap(SortedIntegerKeyNode<HashBucket<K, V>> root, int adjustedCountIfDifferentRoot) {
        if (root == null) {
            return clear();
        }

        if (this.root != root) {
            return root.isEmpty() ? clear() : new ImmutableHashMap<K, V>(root, comparators, adjustedCountIfDifferentRoot);
        }

        return this;
    }

    private ImmutableHashMap<K, V> addAll(Iterable<Map.Entry<K, V>> pairs, boolean avoidToHashMap) {
        Requires.notNull(pairs, "pairs");

        // Some optimizations may apply if we're an empty list
        if (isEmpty() && !avoidToHashMap) {
            // If the items being added actually come from an ImmutableHashMap<K, V> then there is no value in
            // reconstructing it.
            ImmutableHashMap<K, V> other = tryCastToImmutableMap(pairs);
            if (other != null) {
                return other.withComparators(getKeyComparator(), getValueComparator());
            }
        }

        MutationResult<K, V> result = addAll(pairs, getOrigin());
        return result.finalize(this);
    }

    /**
     * How to respond when a key collision is discovered.
     */
    private enum KeyCollisionBehavior {
        /**
         * Sets the value for the given key, even if that overwrites an existing value.
         */
        SET_VALUE,
        /**
         * Skips the mutating operation if a key conflict is detected.
         */
        SKIP,
        /**
         * Throw an exception if the key already exists with a different value.
         */
        THROW_IF_VALUE_DIFFERENT,
        /**
         * Throw an exception if the key already exists regardless of its value.
         */
        THROW_ALWAYS
    }

    /**
     * The result of a mutation operation.
     */
    private enum OperationResult {
        /**
         * The change was applied and did not require a change to the number of elements in the collection.
         */
        APPLIED_WITHOUT_SIZE_CHANGE,
        /**
         * The change required element(s) to be added or removed from the collection.
         */
        SIZE_CHANGED,
        /**
         * No change was required (the operation ended in a no-op).
         */
        NO_CHANGE_REQUIRED
    }

    public static final class Builder<K, V> {
        private SortedIntegerKeyNode<HashBucket<K, V>> root = SortedIntegerKeyNode.emptyNode();
        private Comparators<K, V> comparators;
        private int count;
        private ImmutableHashMap<K, V> immutable;
        private int version;

        Builder(ImmutableHashMap<K, V> map) {
            Requires.notNull(map, "map");
            this.root = map.root;
            this.comparators = map.comparators;
            this.count = map.count;
            this.immutable = map;
        }

        public EqualityComparator<? super K> getKeyComparator() {
            return comparators.getKeyComparator();
        }

        public void setKeyComparator(EqualityComparator<? super K> comparator) {
            Requires.notNull(comparator, "comparator");
            if (comparator != this.getKeyComparator()) {
                throw new UnsupportedOperationException();
            }
        }

        public EqualityComparator<? super V> getValueComparator() {
            return comparators.getValueComparator();
        }

        public void setValueComparator(EqualityComparator<? super V> comparator) {
            Requires.notNull(comparator, "comparator");
            if (comparator != this.getValueComparator()) {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * Contains all the key/value pairs in the collection whose key hashes to the same value.
     *
     * @param <K> The type of the keys.
     * @param <V> The type of the values.
     */
    private static final class HashBucket<K, V> implements Iterable<Map.Entry<K, V>> {
        private static final HashBucket<?, ?> EMPTY_BUCKET = new HashBucket<Object, Object>();

        /**
         * One of the values in this bucket.
         */
        private final Map.Entry<K, V> firstValue;
        /**
         * Any other elements that hash to the same value.
         *
         * This is {@code null} if and only if the entire bucket is empty (including {@link #firstValue}). It's empty if
         * {@link #firstValue} has an element but no additional elements.
         */
        private final ImmutableTreeList.Node<Map.Entry<K, V>> additionalElements;

        /**
         * Initializes a new instance of an empty {@link HashBucket}.
         */
        private HashBucket() {
            firstValue = new KeyValuePair<K, V>(null, null);
            additionalElements = null;
        }

        /**
         * Initializes a new instance of the {@link HashBucket} class.
         *
         * @param firstElement The first element in the bucket.
         */
        private HashBucket(Map.Entry<K, V> firstElement) {
            this(firstElement, null);
        }

        /**
         * Initializes a new instance of the {@link HashBucket} class.
         *
         * @param firstElement The first element in the bucket.
         * @param additionalElements The additional elements.
         */
        private HashBucket(Map.Entry<K, V> firstElement, ImmutableTreeList.Node<Map.Entry<K, V>> additionalElements) {
            this.firstValue = firstElement;
            this.additionalElements = additionalElements != null ? additionalElements : ImmutableTreeList.Node.<Map.Entry<K, V>>empty();
        }

        private static <K, V> HashBucket<K, V> empty() {
            @SuppressWarnings("unchecked")
            HashBucket<K, V> result = (HashBucket<K, V>)EMPTY_BUCKET;
            return result;
        }

        /**
         * Gets a value indicating whether this instance is empty.
         *
         * @return {@code true} if this instance is empty; otherwise, {@code false}.
         */
        boolean isEmpty() {
            return additionalElements == null;
        }

        /**
         * Gets the first value in this bucket.
         *
         * @return The first value in this bucket.
         * @throws IllegalStateException if this bucket is empty.
         */
        Map.Entry<K, V> getFirstValue() {
            if (isEmpty()) {
                throw new IllegalStateException();
            }

            return firstValue;
        }

        /**
         * Gets the list of additional (hash collision) elements.
         *
         * @return The list of additional elements.
         */
        ImmutableTreeList.Node<Map.Entry<K, V>> getAdditionalElements() {
            return additionalElements;
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<Map.Entry<K, V>> iterator() {
            return new Itr();
        }

        /**
         * Throws an exception to catch errors in comparing {@link HashBucket} instances.
         *
         * @param other Ignored.
         * @return This method does not return.
         * @throws UnsupportedOperationException in all cases.
         */
        @Override
        public boolean equals(Object other) {
            // This should never be called, as hash buckets don't know how to equate themselves.
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            // This should never be called, as hash buckets don't know how to equate themselves.
            throw new UnsupportedOperationException();
        }

        /**
         * Adds the specified key.
         *
         * @param key The key to add.
         * @param value The value to add.
         * @param keyOnlyComparator The key comparator.
         * @param valueComparator The value comparator.
         * @param behavior The intended behavior for certain cases that may come up during the operation.
         * @return A pair of values describing the result of the operation. The {@link KeyValuePair#getKey()} returns a
         * new {@link HashBucket} which includes the specified key-value pair. The {@link KeyValuePair#getValue()}
         * returns an {@link OperationResult} describing the type of change which was made to the collection.
         */
        KeyValuePair<HashBucket<K, V>, OperationResult> add(K key, V value, EqualityComparator<Map.Entry<K, V>> keyOnlyComparator, EqualityComparator<V> valueComparator, KeyCollisionBehavior behavior) {
            KeyValuePair<K, V> kv = new KeyValuePair<K, V>(key, value);
            if (isEmpty()) {
                return new KeyValuePair<HashBucket<K, V>, OperationResult>(new HashBucket<K, V>(kv), OperationResult.SIZE_CHANGED);
            }

            if (keyOnlyComparator.equals(kv, firstValue)) {
                switch (behavior) {
                case SET_VALUE:
                    return new KeyValuePair<HashBucket<K, V>, OperationResult>(new HashBucket<K, V>(kv, additionalElements), OperationResult.APPLIED_WITHOUT_SIZE_CHANGE);

                case SKIP:
                    return new KeyValuePair<HashBucket<K, V>, OperationResult>(this, OperationResult.NO_CHANGE_REQUIRED);

                case THROW_IF_VALUE_DIFFERENT:
                    if (!valueComparator.equals(firstValue.getValue(), value)) {
                        throw new IllegalArgumentException("The key already exists with a different value.");
                    }

                    return new KeyValuePair<HashBucket<K, V>, OperationResult>(this, OperationResult.NO_CHANGE_REQUIRED);

                case THROW_ALWAYS:
                    throw new IllegalArgumentException("The key already exists with a different value.");

                default:
                    throw new IllegalStateException("Unreachable");
                }
            }

            int keyCollisionIndex = additionalElements.indexOf(kv, keyOnlyComparator);
            if (keyCollisionIndex < 0) {
                return new KeyValuePair<HashBucket<K, V>, OperationResult>(new HashBucket<K, V>(firstValue, additionalElements.add(kv)), OperationResult.SIZE_CHANGED);
            } else {
                switch (behavior) {
                case SET_VALUE:
                    return new KeyValuePair<HashBucket<K, V>, OperationResult>(new HashBucket<K, V>(firstValue, additionalElements.replace(keyCollisionIndex, kv)), OperationResult.APPLIED_WITHOUT_SIZE_CHANGE);

                case SKIP:
                    return new KeyValuePair<HashBucket<K, V>, OperationResult>(this, OperationResult.NO_CHANGE_REQUIRED);

                case THROW_IF_VALUE_DIFFERENT:
                    Map.Entry<K, V> existingEntry = additionalElements.get(keyCollisionIndex);
                    if (!valueComparator.equals(existingEntry.getValue(), value)) {
                        throw new IllegalArgumentException("The key already exists with a different value");
                    }

                    return new KeyValuePair<HashBucket<K, V>, OperationResult>(this, OperationResult.NO_CHANGE_REQUIRED);

                case THROW_ALWAYS:
                    throw new IllegalArgumentException("The key already exists with a different value");

                default:
                    throw new IllegalStateException("Unreachable");
                }
            }
        }

        /**
         * Removes the specified value if it exists in the collection.
         *
         * @param key The key to remove.
         * @param keyOnlyComparator The equality comparator.
         * @return A pair of values describing the result of the operation. The {@link KeyValuePair#getKey()} returns a
         * new {@link HashBucket} which does not contain the specified key. The {@link KeyValuePair#getValue()} returns
         * an {@link OperationResult} describing the type of change which was made to the collection.
         */
        KeyValuePair<HashBucket<K, V>, OperationResult> remove(K key, EqualityComparator<Map.Entry<K, V>> keyOnlyComparator) {
            if (isEmpty()) {
                return new KeyValuePair<HashBucket<K, V>, OperationResult>(this, OperationResult.NO_CHANGE_REQUIRED);
            }

            Map.Entry<K, V> kv = new KeyValuePair<K, V>(key, null);
            if (keyOnlyComparator.equals(firstValue, kv)) {
                if (additionalElements.isEmpty()) {
                    return new KeyValuePair<HashBucket<K, V>, OperationResult>(HashBucket.<K, V>empty(), OperationResult.SIZE_CHANGED);
                } else {
                    // We can promote any element from the list into the first position, but it's most efficient to
                    // remove the root node in the binary tree that implements the list.
                    int indexOfRootNode = additionalElements.getLeft().size();
                    return new KeyValuePair<HashBucket<K, V>, OperationResult>(new HashBucket<K, V>(additionalElements.getKey(), additionalElements.remove(indexOfRootNode)), OperationResult.SIZE_CHANGED);
                }
            }

            int index = additionalElements.indexOf(kv, keyOnlyComparator);
            if (index < 0) {
                return new KeyValuePair<HashBucket<K, V>, OperationResult>(this, OperationResult.APPLIED_WITHOUT_SIZE_CHANGE);
            } else {
                return new KeyValuePair<HashBucket<K, V>, OperationResult>(new HashBucket<K, V>(firstValue, additionalElements.remove(index)), OperationResult.SIZE_CHANGED);
            }
        }

        /**
         * Gets the value for the given key in the collection, if one exists.
         *
         * @param key The key to search for.
         * @param keyOnlyComparator The key comparator.
         * @return The value for the given key, or {@code null} if the key was not found.
         */
        V getValue(K key, EqualityComparator<Map.Entry<K, V>> keyOnlyComparator) {
            if (isEmpty()) {
                return null;
            }

            Map.Entry<K, V> kv = new KeyValuePair<K, V>(key, null);
            if (keyOnlyComparator.equals(firstValue, kv)) {
                return firstValue.getValue();
            }

            int index = additionalElements.indexOf(kv, keyOnlyComparator);
            if (index < 0) {
                return null;
            }

            return additionalElements.get(index).getValue();
        }

        /**
         * Searches the map for a given key and returns the equal key it finds, if any.
         *
         * <p>This can be useful when you want to reuse a previously stored reference instead of a newly constructed one
         * (so that more sharing of references can occur) or to look up the canonical value, or a value that has more
         * complete data than the value you currently have, although their comparator functions indicate they are
         * equal.</p>
         *
         * @param equalKey The key to search for.
         * @param keyOnlyComparator The key comparator.
         * @return The key from the map that the search found, or {@code null} if the search yielded no match.
         */
        K getKey(K equalKey, EqualityComparator<Map.Entry<K, V>> keyOnlyComparator) {
            if (isEmpty()) {
                return null;
            }

            Map.Entry<K, V> kv = new KeyValuePair<K, V>(equalKey, null);
            if (keyOnlyComparator.equals(firstValue, kv)) {
                return firstValue.getKey();
            }

            int index = additionalElements.indexOf(kv, keyOnlyComparator);
            if (index < 0) {
                return null;
            }

            return additionalElements.get(index).getKey();
        }

        /**
         * Freezes this instance so that any further mutations require new memory allocations.
         */
        void freeze() {
            if (additionalElements != null) {
                additionalElements.freeze();
            }
        }

        final class Itr implements Iterator<Map.Entry<K, V>> {
            private Position currentPosition;
            private Iterator<Map.Entry<K, V>> additionalIterator;

            Itr() {
                currentPosition = Position.BEFORE_FIRST;
                additionalIterator = null;
            }

            @Override
            public boolean hasNext() {
                if (isEmpty()) {
                    return false;
                }

                switch (currentPosition) {
                case BEFORE_FIRST:
                    return true;

                case FIRST:
                    return !additionalElements.isEmpty();

                case ADDITIONAL:
                    return additionalIterator.hasNext();

                case END:
                    return false;

                default:
                    throw new IllegalStateException("Unreachable");
                }
            }

            @Override
            public Map.Entry<K, V> next() {
                if (isEmpty()) {
                    throw new NoSuchElementException();
                }

                switch (currentPosition) {
                case BEFORE_FIRST:
                    currentPosition = Position.FIRST;
                    return firstValue;

                case FIRST:
                    if (additionalElements.isEmpty()) {
                        currentPosition = Position.END;
                        throw new NoSuchElementException();
                    }

                    currentPosition = Position.ADDITIONAL;
                    additionalIterator = additionalElements.iterator();
                    return additionalIterator.next();

                case ADDITIONAL:
                    return additionalIterator.next();

                case END:
                    throw new NoSuchElementException();

                default:
                    throw new IllegalStateException("Unreachable");
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("This iterator is read-only.");
            }
        }

        /**
         * Describes the positions the iterator state machine can be in.
         */
        enum Position {
            /**
             * The first element has not yet been moved to.
             */
            BEFORE_FIRST,
            /**
             * We're at the {@link #firstValue} of the containing bucket.
             */
            FIRST,
            /**
             * We're iterating the {@link #additionalElements} in the bucket.
             */
            ADDITIONAL,
            /**
             * The end of the collection has been reached.
             */
            END
        }
    }

    private static final class Comparators<K, V> {

        private static final Comparators<?, ?> DEFAULT = new Comparators<Object, Object>(EqualityComparators.defaultComparator(), EqualityComparators.defaultComparator());

        private final EqualityComparator<? super K> keyComparator;

        private final EqualityComparator<? super V> valueComparator;

        Comparators(EqualityComparator<? super K> keyComparator, EqualityComparator<? super V> valueComparator) {
            Requires.notNull(keyComparator, "keyComparator");
            Requires.notNull(valueComparator, "valueComparator");

            this.keyComparator = keyComparator;
            this.valueComparator = valueComparator;
        }

        static <K, V> Comparators<K, V> defaultComparators() {
            @SuppressWarnings("unchecked") // safe
            Comparators<K, V> result = (Comparators<K, V>)DEFAULT;
            return result;
        }

        static <K, V> Comparators<K, V> get(EqualityComparator<? super K> keyComparator, EqualityComparator<? super V> valueComparator) {
            if (keyComparator == DEFAULT.keyComparator && valueComparator == DEFAULT.valueComparator) {
                return defaultComparators();
            }

            return new Comparators<K, V>(keyComparator, valueComparator);
        }

        EqualityComparator<? super K> getKeyComparator() {
            return keyComparator;
        }

        EqualityComparator<? super V> getValueComparator() {
            return valueComparator;
        }

        Comparators<K, V> withValueComparator(EqualityComparator<? super V> valueComparator) {
            Requires.notNull(valueComparator, "valueComparator");
            return valueComparator == this.valueComparator ? this : get(getKeyComparator(), valueComparator);
        }
    }

    /**
     * Description of the current data structure as input into a mutating or query method.
     *
     * @param <K> The type of keys in the map.
     * @param <V> The type of values in the map.
     */
    private static final class MutationInput<K, V> {
        /**
         * The root of the data structure for the collection.
         */
        private final SortedIntegerKeyNode<HashBucket<K, V>> root;
        /**
         * The comparator used when comparing hash buckets.
         */
        private final Comparators<K, V> comparators;
        /**
         * The current number of elements in the collection.
         */
        private final int count;

        MutationInput(SortedIntegerKeyNode<HashBucket<K, V>> root, Comparators<K, V> comparators, int count) {
            this.root = root;
            this.comparators = comparators;
            this.count = count;
        }

        MutationInput(ImmutableHashMap<K, V> map) {
            this.root = map.root;
            this.comparators = map.comparators;
            this.count = map.count;
        }

        SortedIntegerKeyNode<HashBucket<K, V>> getRoot() {
            return root;
        }

        EqualityComparator<? super K> getKeyComparator() {
            return comparators.getKeyComparator();
        }

        EqualityComparator<? super V> getValueComparator() {
            return comparators.getValueComparator();
        }

        /**
         * Gets the current number of elements in the collection.
         */
        int size() {
            return count;
        }
    }

    /**
     * Describes the result of a mutation on the immutable data structure.
     *
     * @param <K> The type of keys in the map.
     * @param <V> The type of values in the map.
     */
    private static final class MutationResult<K, V> {
        /**
         * The root node of the data structure after the mutation.
         */
        private final SortedIntegerKeyNode<HashBucket<K, V>> root;

        /**
         * The number of elements added or removed from the collection as a result of the operation (a negative number
         * represents removed elements).
         */
        private final int countAdjustment;

        /**
         * Constructs a new instance of the {@link MutationResult} class.
         *
         * @param unchangedInput The unchanged input.
         */
        MutationResult(MutationInput<K, V> unchangedInput) {
            root = unchangedInput.getRoot();
            countAdjustment = 0;
        }

        /**
         * Constructs a new instance of the {@link MutationResult} class.
         *
         * @param root The root.
         * @param countAdjustment The count adjustment.
         */
        MutationResult(SortedIntegerKeyNode<HashBucket<K, V>> root, int countAdjustment) {
            Requires.notNull(root, "root");
            this.root = root;
            this.countAdjustment = countAdjustment;
        }

        /**
         * Gets the root node of the data structure after the mutation.
         *
         * @return The root node of the data structure after the mutation.
         */
        SortedIntegerKeyNode<HashBucket<K, V>> getRoot() {
            return root;
        }

        /**
         * Gets the number of elements added or removed from the collection as a result of the operation (a negative
         * number represents removed elements).
         *
         * @return The number of elements added or removed from the collection as a result of the operation (a negative
         * number represents removed elements).
         */
        int getCountAdjustment() {
            return countAdjustment;
        }

        /**
         * Returns an immutable map that captures the result of this mutation.
         *
         * @param priorMap The prior version of the map. Used to capture the equality comparator and previous count,
         * when applicable.
         * @return The new collection.
         */
        ImmutableHashMap<K, V> finalize(ImmutableHashMap<K, V> priorMap) {
            Requires.notNull(priorMap, "priorMap");
            return priorMap.wrap(this.getRoot(), priorMap.count + getCountAdjustment());
        }
    }
}
