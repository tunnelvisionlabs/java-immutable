// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An immutable unordered map implementation.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public final class ImmutableHashMap<K, V> implements ImmutableMap<K, V>, HashKeyCollection<K> {

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
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        ImmutableHashMap<K, V> result = (ImmutableHashMap<K, V>)EMPTY_MAP;
        return result;
    }

    public static <K, V> ImmutableHashMap<K, V> create() {
        return empty();
    }

    public static <K, V> ImmutableHashMap<K, V> create(EqualityComparator<? super K> keyComparator) {
        return ImmutableHashMap.<K, V>empty().withComparators(keyComparator);
    }

    public static <K, V> ImmutableHashMap<K, V> create(EqualityComparator<? super K> keyComparator, EqualityComparator<? super V> valueComparator) {
        return ImmutableHashMap.<K, V>empty().withComparators(keyComparator, valueComparator);
    }

    public static <K, V> ImmutableHashMap<K, V> createAll(Iterable<Map.Entry<K, V>> items) {
        return ImmutableHashMap.<K, V>empty().addAll(items);
    }

    public static <K, V> ImmutableHashMap<K, V> createAll(EqualityComparator<? super K> keyComparator, Iterable<Map.Entry<K, V>> items) {
        return ImmutableHashMap.<K, V>create(keyComparator).addAll(items);
    }

    public static <K, V> ImmutableHashMap<K, V> createAll(EqualityComparator<? super K> keyComparator, EqualityComparator<? super V> valueComparator, Iterable<Map.Entry<K, V>> items) {
        return ImmutableHashMap.<K, V>create(keyComparator, valueComparator).addAll(items);
    }

    public static <K, V> ImmutableHashMap.Builder<K, V> createBuilder() {
        return ImmutableHashMap.<K, V>create().toBuilder();
    }

    public static <K, V> ImmutableHashMap.Builder<K, V> createBuilder(EqualityComparator<? super K> keyComparator) {
        return ImmutableHashMap.<K, V>create(keyComparator).toBuilder();
    }

    public static <K, V> ImmutableHashMap.Builder<K, V> createBuilder(EqualityComparator<? super K> keyComparator, EqualityComparator<? super V> valueComparator) {
        return ImmutableHashMap.<K, V>create(keyComparator, valueComparator).toBuilder();
    }

    private static <K, V> ImmutableHashMap<K, V> emptyWithComparators(Comparators<K, V> comparators) {
        Requires.notNull(comparators, "comparators");

        if (empty().comparators == comparators) {
            return empty();
        }

        return new ImmutableHashMap<K, V>(comparators);
    }

    private static <K, V> ImmutableHashMap<K, V> tryCastToImmutableMap(Iterable<? extends Map.Entry<K, V>> sequence) {
        if (sequence instanceof ImmutableHashMap.EntrySet) {
            return ((ImmutableHashMap.EntrySet<K, V>)(ImmutableHashMap.EntrySet)sequence).getMap();
        }

        if (sequence instanceof ImmutableHashMap.Builder.EntrySet) {
            return ((ImmutableHashMap.Builder.EntrySet<K, V>)(ImmutableHashMap.Builder.EntrySet)sequence).getBuilder().toImmutable();
        }
        //var builder = sequence as Builder;
        //if (builder != null)
        //{
        //    other = builder.ToImmutable();
        //    return true;
        //}

        return null;
    }

    private static <K, V> boolean containsKey(K key, MutationInput<K, V> origin) {
        int hashCode = origin.getKeyComparator().hashCode(key);
        HashBucket<K, V> bucket = origin.getRoot().getValueOrDefault(hashCode);
        if (bucket != null) {
            K equalKey = bucket.getKey(key, origin.getKeyOnlyComparator());
            return equalKey != null;
        }

        return false;
    }

    private static <K, V> boolean contains(Map.Entry<K, V> keyValuePair, MutationInput<K, V> origin) {
        int hashCode = origin.getKeyComparator().hashCode(keyValuePair.getKey());
        HashBucket<K, V> bucket = origin.getRoot().getValueOrDefault(hashCode);
        if (bucket != null) {
            KeyValuePair<K, V> entry = bucket.getEntry(keyValuePair.getKey(), origin.getKeyOnlyComparator());
            return entry != null && origin.getValueComparator().equals(entry.getValue(), keyValuePair.getValue());
        }

        return false;
    }

    private static <K, V> V getValue(K key, MutationInput<K, V> origin) {
        int hashCode = origin.getKeyComparator().hashCode(key);
        HashBucket<K, V> bucket = origin.getRoot().getValueOrDefault(hashCode);
        if (bucket != null) {
            return bucket.getValue(key, origin.getKeyOnlyComparator());
        }

        return null;
    }

    private static <K, V> K getKey(K equalKey, MutationInput<K, V> origin) {
        int hashCode = origin.getKeyComparator().hashCode(equalKey);
        HashBucket<K, V> bucket = origin.getRoot().getValueOrDefault(hashCode);
        if (bucket != null) {
            return bucket.getKey(equalKey, origin.getKeyOnlyComparator());
        }

        return null;
    }

    private static <K, V> MutationResult<K, V> add(K key, V value, KeyCollisionBehavior behavior, MutationInput<K, V> origin) {
        Requires.notNull(key, "key");

        int hashCode = origin.getKeyComparator().hashCode(key);
        HashBucket<K, V> bucket = origin.getRoot().getValueOrDefault(hashCode);
        if (bucket == null) {
            bucket = new HashBucket<K, V>();
        }

        KeyValuePair<HashBucket<K, V>, OperationResult> newBucket = bucket.add(key, value, origin.getKeyOnlyComparator(), origin.getValueComparator(), behavior);
        if (newBucket.getValue() == OperationResult.NO_CHANGE_REQUIRED)
        {
            return new MutationResult<K, V>(origin);
        }

        SortedIntegerKeyNode<HashBucket<K, V>> newRoot = updateRoot(origin.getRoot(), hashCode, newBucket.getKey(), origin.getHashBucketComparator());
        return new MutationResult<K, V>(newRoot, newBucket.getValue() == OperationResult.SIZE_CHANGED ? +1 : 0);
    }

    private static <K, V> MutationResult<K, V> addAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> items, MutationInput<K, V> origin) {
        return addAll(items, origin, KeyCollisionBehavior.THROW_IF_VALUE_DIFFERENT);
    }

    private static <K, V> MutationResult<K, V> addAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> items, MutationInput<K, V> origin, KeyCollisionBehavior behavior) {
        Requires.notNull(items, "items");

        int countAdjustment = 0;
        SortedIntegerKeyNode<HashBucket<K, V>> newRoot = origin.getRoot();
        for (Map.Entry<? extends K, ? extends V> pair : items) {
            int hashCode = origin.getKeyComparator().hashCode(pair.getKey());
            HashBucket<K, V> bucket = newRoot.getValueOrDefault(hashCode);
            if (bucket == null) {
                bucket = new HashBucket<K, V>();
            }

            KeyValuePair<HashBucket<K, V>, OperationResult> newBucket = bucket.add(pair.getKey(), pair.getValue(), origin.getKeyOnlyComparator(), origin.getValueComparator(), behavior);
            newRoot = updateRoot(newRoot, hashCode, newBucket.getKey(), origin.getHashBucketComparator());
            if (newBucket.getValue() == OperationResult.SIZE_CHANGED) {
                countAdjustment++;
            }
        }

        return new MutationResult<K, V>(newRoot, countAdjustment);
    }

    private static <K, V> MutationResult<K, V> remove(K key, MutationInput<K, V> origin) {
        int hashCode = origin.getKeyComparator().hashCode(key);
        HashBucket<K, V> bucket = origin.getRoot().getValueOrDefault(hashCode);
        if (bucket != null) {
            KeyValuePair<HashBucket<K, V>, OperationResult> removeResult = bucket.remove(key, origin.getKeyOnlyComparator());
            SortedIntegerKeyNode<HashBucket<K, V>> newRoot = updateRoot(origin.getRoot(), hashCode, removeResult.getKey(), origin.getHashBucketComparator());
            return new MutationResult<K, V>(newRoot, removeResult.getValue() == OperationResult.SIZE_CHANGED ? -1 : 0);
        }

        return new MutationResult<K, V>(origin);
    }

    private static <K, V> SortedIntegerKeyNode<HashBucket<K, V>> updateRoot(SortedIntegerKeyNode<HashBucket<K, V>> root, int hashCode, HashBucket<K, V> newBucket, EqualityComparator<? super HashBucket<K, V>> hashBucketComparator) {
        if (newBucket.isEmpty()) {
            SortedIntegerKeyNode.MutationResult<HashBucket<K, V>> result = root.remove(hashCode);
            return result.result;
        } else {
            SortedIntegerKeyNode.SetItemResult<HashBucket<K, V>> result = root.setItem(hashCode, newBucket, hashBucketComparator);
            return result.result;
        }
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
        return new Iterable<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    Iterator<Map.Entry<K, V>> entriesIterator = entrySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public K next() {
                        return entriesIterator.next().getKey();
                    }

                    @Override
                    public void remove() {
                        entriesIterator.remove();
                    }
                };
            }
        };
    }

    @Override
    public Iterable<V> values() {
        return new Iterable<V>() {
            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    Iterator<Map.Entry<K, V>> entriesIterator = entrySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return entriesIterator.next().getValue();
                    }

                    @Override
                    public void remove() {
                        entriesIterator.remove();
                    }
                };
            }
        };
    }

    private MutationInput<K, V> getOrigin() {
        return new MutationInput<K, V>(this);
    }

    public Builder<K, V> toBuilder() {
        return new Builder<K, V>(this);
    }

    @Override
    public ImmutableHashMap<K, V> add(K key, V value) {
        Requires.notNull(key, "key");

        ImmutableHashMap.MutationResult<K, V> result = add(key, value, KeyCollisionBehavior.THROW_IF_VALUE_DIFFERENT, new MutationInput<K, V>(this));
        return result.finalize(this);
    }

    @Override
    public ImmutableHashMap<K, V> addAll(Iterable<? extends Map.Entry<K, V>> entries) {
        Requires.notNull(entries, "entries");

        return addAll(entries, false);
    }

    @Override
    public ImmutableHashMap<K, V> put(K key, V value) {
        Requires.notNull(key, "key");

        MutationResult<K, V> result = add(key, value, KeyCollisionBehavior.SET_VALUE, new MutationInput<K, V>(this));
        return result.finalize(this);
    }

    @Override
    public ImmutableHashMap<K, V> putAll(Iterable<? extends Map.Entry<K, V>> entries) {
        Requires.notNull(entries, "entries");

        MutationResult<K, V> result = addAll(entries, getOrigin(), KeyCollisionBehavior.SET_VALUE);
        return result.finalize(this);
    }

    @Override
    public ImmutableHashMap<K, V> remove(K key) {
        Requires.notNull(key, "key");

        MutationResult<K, V> result = remove(key, new MutationInput<K, V>(this));
        return result.finalize(this);
    }

    @Override
    public ImmutableHashMap<K, V> removeAll(Iterable<? extends K> keys) {
        Requires.notNull(keys, "keys");

        int count = this.count;
        SortedIntegerKeyNode<HashBucket<K, V>> root = this.root;
        for (K key : keys) {
            int hashCode = getKeyComparator().hashCode(key);
            HashBucket<K, V> bucket = root.getValueOrDefault(hashCode);
            if (bucket != null) {
                KeyValuePair<HashBucket<K, V>, OperationResult> newBucket = bucket.remove(key, comparators.getKeyOnlyComparator());
                root = updateRoot(root, hashCode, newBucket.getKey(), comparators.getHashBucketComparator());
                if (newBucket.getValue() == OperationResult.SIZE_CHANGED) {
                    count--;
                }
            }
        }

        return wrap(root, count);
    }

    @Override
    public boolean containsKey(K key) {
        Requires.notNull(key, "key");
        return containsKey(key, new MutationInput<K, V>(this));
    }

    @Override
    public boolean contains(Map.Entry<K, V> pair) {
        return contains(pair, getOrigin());
    }

    @Override
    public V get(K key) {
        Requires.notNull(key, "key");
        return getValue(key, getOrigin());
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
            map = map.addAll(entrySet(), true);
            return map;
        }
    }

    public boolean containsValue(V value) {
        for (Map.Entry<K, V> entry : entrySet()) {
            if (getValueComparator().equals(value, entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterable<Map.Entry<K, V>> entrySet() {
        return new EntrySet<K, V>(this);
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

    private ImmutableHashMap<K, V> addAll(Iterable<? extends Map.Entry<K, V>> pairs, boolean avoidToHashMap) {
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

    SortedIntegerKeyNode<HashBucket<K, V>> getRoot() {
        return root;
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

    private static final class EntrySet<K, V> implements Iterable<Map.Entry<K, V>> {
        private final ImmutableHashMap<K, V> map;

        EntrySet(ImmutableHashMap<K, V> map) {
            Requires.notNull(map, "map");
            this.map = map;
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntrySetItr<K, V>(map.root);
        }

        ImmutableHashMap<K, V> getMap() {
            return map;
        }
    }

    private static final class EntrySetItr<K, V> implements Iterator<Map.Entry<K, V>> {
        private final Builder<K, V> builder;
        private SortedIntegerKeyNode.Itr<HashBucket<K, V>> mapIterator;
        private Iterator<Map.Entry<K, V>> bucketIterator;
        private int iteratingBuilderVersion;

        EntrySetItr(SortedIntegerKeyNode<HashBucket<K, V>> root) {
            this(root, null);
        }

        EntrySetItr(SortedIntegerKeyNode<HashBucket<K, V>> root, Builder<K, V> builder) {
            this.builder = builder;
            this.mapIterator = root.iterator();
            this.bucketIterator = null;
            this.iteratingBuilderVersion = builder != null ? builder.getVersion() : -1;
        }

        @Override
        public boolean hasNext() {
            if (bucketIterator != null && bucketIterator.hasNext()) {
                return true;
            }

            while (mapIterator.hasNext()) {
                SortedIntegerKeyNode.IntegerKeyEntry<HashBucket<K, V>> bucket = mapIterator.next();
                bucketIterator = bucket.getValue().iterator();
                if (bucketIterator.hasNext()) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public Map.Entry<K, V> next() {
            throwIfChanged();

            // hasNext() handles mapIterator and bucketIterator
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            assert bucketIterator != null && bucketIterator.hasNext();
            return bucketIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("This iterator is read-only.");
        }

        private void throwIfChanged() {
            if (builder != null && builder.getVersion() != iteratingBuilderVersion) {
                throw new IllegalStateException("The collection was modified during iteration.");
            }
        }
    }

    public static final class Builder<K, V> implements Map<K, V>, ReadOnlyMap<K, V> {
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

        public void setKeyComparator(EqualityComparator<? super K> value) {
            Requires.notNull(value, "value");
            if (value != this.getKeyComparator()) {
                Comparators<K, V> comparators = Comparators.get(value, getValueComparator());
                MutationInput<K, V> input = new MutationInput<K, V>(SortedIntegerKeyNode.<HashBucket<K, V>>emptyNode(), comparators, 0);
                MutationResult<K, V> result = ImmutableHashMap.addAll(entrySet(), input);

                this.immutable = null;
                this.comparators = comparators;
                this.count = result.getCountAdjustment(); // offset from 0
                this.setRoot(result.getRoot());
            }
        }

        public EqualityComparator<? super V> getValueComparator() {
            return comparators.getValueComparator();
        }

        public void setValueComparator(EqualityComparator<? super V> value) {
            Requires.notNull(value, "value");
            if (value != getValueComparator()) {
                // When the key comparator is the same but the value comparator is different, we don't need a whole new
                // tree because the structure of the tree does not depend on the value comparator. We just need a new
                // root node to store the new value comparator.
                this.comparators = comparators.withValueComparator(value);
                this.immutable = null; // invalidate cached immutable
            }
        }

        @Override
        public int size() {
            return count;
        }

        @Override
        public boolean isEmpty() {
            return count == 0;
        }

        @Override
        public Set<K> keySet() {
            return new KeySet();
        }

        @Override
        public Collection<V> values() {
            return new ValuesCollection();
        }

        int getVersion() {
            return version;
        }

        private MutationInput<K, V> getOrigin() {
            return new MutationInput<K, V>(getRoot(), comparators, count);
        }

        private SortedIntegerKeyNode<HashBucket<K, V>> getRoot() {
            return root;
        }

        private void setRoot(SortedIntegerKeyNode<HashBucket<K, V>> value) {
            // We *always* increment the version number because some mutations may not create a new value of root,
            // although the existing root instance may have mutated.
            version++;

            if (root != value) {
                root = value;

                // Clear any cached value for the immutable view since it is now invalidated.
                immutable = null;
            }
        }

        @Override
        public V get(Object key) {
            return ImmutableHashMap.getValue((K)key, getOrigin());
        }

        @Override
        public V put(K key, V value) {
            V previousValue = get(key);
            MutationResult<K, V> result = ImmutableHashMap.add(key, value, KeyCollisionBehavior.SET_VALUE, getOrigin());
            apply(result);
            return previousValue;
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            MutationResult<K, V> result = ImmutableHashMap.addAll(m.entrySet(), getOrigin());
            apply(result);
        }

        public void removeAll(Iterable<? extends K> keys) {
            Requires.notNull(keys, "keys");

            for (K key : keys) {
                remove(key);
            }
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return new EntrySet<K, V>(this);
        }

        public V getValueOrDefault(K key, V defaultValue) {
            if (!containsKey(key)) {
                return defaultValue;
            }

            return get(key);
        }

        public ImmutableHashMap<K, V> toImmutable() {
            // Creating an instance of ImmutableHashMap<T> with our root node automatically freezes our tree, ensuring
            // that the returned instance is immutable. Any further mutations made to this builder will clone (and
            // unfreeze) the spine of modified nodes until the next time this method is invoked.
            if (immutable == null) {
                immutable = ImmutableHashMap.wrap(root, comparators, count);
            }

            return immutable;
        }

        @Override
        public boolean containsKey(Object key) {
            return ImmutableHashMap.containsKey((K)key, getOrigin());
        }

        @Override
        public boolean containsValue(Object value) {
            for (Map.Entry<K, V> entry : entrySet()) {
                if (getValueComparator().equals((V)value, entry.getValue())) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public V remove(Object key) {
            V value = get(key);
            MutationResult<K, V> result = ImmutableHashMap.remove((K)key, getOrigin());
            apply(result);
            return value;
        }

        public K getKey(K equalKey) {
            return ImmutableHashMap.getKey(equalKey, getOrigin());
        }

        @Override
        public void clear() {
            setRoot(SortedIntegerKeyNode.<HashBucket<K, V>>emptyNode());
            count = 0;
        }

        private boolean apply(MutationResult<K, V> result) {
            setRoot(result.getRoot());
            count += result.getCountAdjustment();
            return result.getCountAdjustment() != 0;
        }

        private static final class EntrySet<K, V> implements Set<Map.Entry<K, V>> {
            private final Builder<K, V> builder;

            private EntrySet(Builder<K, V> builder) {
                Requires.notNull(builder, "builder");
                this.builder = builder;
            }

            Builder<K, V> getBuilder() {
                return builder;
            }

            @Override
            public int size() {
                return builder.size();
            }

            @Override
            public boolean isEmpty() {
                return builder.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new EntrySetItr<K, V>(builder.root, builder);
            }

            @Override
            public Object[] toArray() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public <T> T[] toArray(T[] a) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean add(Entry<K, V> kvEntry) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean addAll(Collection<? extends Entry<K, V>> c) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }

        private final class KeySet implements Set<K> {

            @Override
            public int size() {
                return Builder.this.size();
            }

            @Override
            public boolean isEmpty() {
                return Builder.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return Builder.this.containsKey(o);
            }

            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    Iterator<Map.Entry<K, V>> entriesIterator = Builder.this.entrySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public K next() {
                        return entriesIterator.next().getKey();
                    }

                    @Override
                    public void remove() {
                        entriesIterator.remove();
                    }
                };
            }

            @Override
            public Object[] toArray() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public <T> T[] toArray(T[] a) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean add(K k) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                Requires.notNull(c, "c");
                for (Object item : c) {
                    if (!contains(item)) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public boolean addAll(Collection<? extends K> c) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }

        private final class ValuesCollection implements Collection<V> {

            @Override
            public int size() {
                return Builder.this.size();
            }

            @Override
            public boolean isEmpty() {
                return Builder.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return Builder.this.containsValue(o);
            }

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    Iterator<Map.Entry<K, V>> entriesIterator = entrySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return entriesIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return entriesIterator.next().getValue();
                    }

                    @Override
                    public void remove() {
                        entriesIterator.remove();
                    }
                };
            }

            @Override
            public Object[] toArray() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public <T> T[] toArray(T[] a) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean add(V v) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean addAll(Collection<? extends V> c) {
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
            public void clear() {
                throw new UnsupportedOperationException("Not supported yet.");
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
        private final KeyValuePair<K, V> firstValue;
        /**
         * Any other elements that hash to the same value.
         *
         * This is {@code null} if and only if the entire bucket is empty (including {@link #firstValue}). It's empty if
         * {@link #firstValue} has an element but no additional elements.
         */
        private final ImmutableTreeList.Node<KeyValuePair<K, V>> additionalElements;

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
        private HashBucket(KeyValuePair<K, V> firstElement) {
            this(firstElement, null);
        }

        /**
         * Initializes a new instance of the {@link HashBucket} class.
         *
         * @param firstElement The first element in the bucket.
         * @param additionalElements The additional elements.
         */
        private HashBucket(KeyValuePair<K, V> firstElement, ImmutableTreeList.Node<KeyValuePair<K, V>> additionalElements) {
            this.firstValue = firstElement;
            this.additionalElements = additionalElements != null ? additionalElements : ImmutableTreeList.Node.<KeyValuePair<K, V>>empty();
        }

        private static <K, V> HashBucket<K, V> empty() {
            @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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
        ImmutableTreeList.Node<KeyValuePair<K, V>> getAdditionalElements() {
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
        KeyValuePair<HashBucket<K, V>, OperationResult> add(K key, V value, EqualityComparator<? super Map.Entry<K, V>> keyOnlyComparator, EqualityComparator<? super V> valueComparator, KeyCollisionBehavior behavior) {
            KeyValuePair<K, V> kv = KeyValuePair.create(key, value);
            if (isEmpty()) {
                return KeyValuePair.create(new HashBucket<K, V>(kv), OperationResult.SIZE_CHANGED);
            }

            if (keyOnlyComparator.equals(kv, firstValue)) {
                switch (behavior) {
                case SET_VALUE:
                    return KeyValuePair.create(new HashBucket<K, V>(kv, additionalElements), OperationResult.APPLIED_WITHOUT_SIZE_CHANGE);

                case SKIP:
                    return KeyValuePair.create(this, OperationResult.NO_CHANGE_REQUIRED);

                case THROW_IF_VALUE_DIFFERENT:
                    if (!valueComparator.equals(firstValue.getValue(), value)) {
                        throw new IllegalArgumentException(String.format("An element with the same key but a different value already exists. Key: %s", key));
                    }

                    return KeyValuePair.create(this, OperationResult.NO_CHANGE_REQUIRED);

                case THROW_ALWAYS:
                    throw new IllegalArgumentException(String.format("An element with the same key but a different value already exists. Key: %s", key));

                default:
                    throw new IllegalStateException("Unreachable");
                }
            }

            int keyCollisionIndex = additionalElements.indexOf(kv, keyOnlyComparator);
            if (keyCollisionIndex < 0) {
                return KeyValuePair.create(new HashBucket<K, V>(firstValue, additionalElements.add(kv)), OperationResult.SIZE_CHANGED);
            } else {
                switch (behavior) {
                case SET_VALUE:
                    return KeyValuePair.create(new HashBucket<K, V>(firstValue, additionalElements.replace(keyCollisionIndex, kv)), OperationResult.APPLIED_WITHOUT_SIZE_CHANGE);

                case SKIP:
                    return KeyValuePair.create(this, OperationResult.NO_CHANGE_REQUIRED);

                case THROW_IF_VALUE_DIFFERENT:
                    Map.Entry<K, V> existingEntry = additionalElements.get(keyCollisionIndex);
                    if (!valueComparator.equals(existingEntry.getValue(), value)) {
                        throw new IllegalArgumentException("The key already exists with a different value");
                    }

                    return KeyValuePair.create(this, OperationResult.NO_CHANGE_REQUIRED);

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
        KeyValuePair<HashBucket<K, V>, OperationResult> remove(K key, EqualityComparator<? super Map.Entry<K, V>> keyOnlyComparator) {
            if (isEmpty()) {
                return KeyValuePair.create(this, OperationResult.NO_CHANGE_REQUIRED);
            }

            KeyValuePair<K, V> kv = new KeyValuePair<K, V>(key, null);
            if (keyOnlyComparator.equals(firstValue, kv)) {
                if (additionalElements.isEmpty()) {
                    return KeyValuePair.create(HashBucket.<K, V>empty(), OperationResult.SIZE_CHANGED);
                } else {
                    // We can promote any element from the list into the first position, but it's most efficient to
                    // remove the root node in the binary tree that implements the list.
                    int indexOfRootNode = additionalElements.getLeft().size();
                    return KeyValuePair.create(new HashBucket<K, V>(additionalElements.getKey(), additionalElements.remove(indexOfRootNode)), OperationResult.SIZE_CHANGED);
                }
            }

            int index = additionalElements.indexOf(kv, keyOnlyComparator);
            if (index < 0) {
                return KeyValuePair.create(this, OperationResult.APPLIED_WITHOUT_SIZE_CHANGE);
            } else {
                return KeyValuePair.create(new HashBucket<K, V>(firstValue, additionalElements.remove(index)), OperationResult.SIZE_CHANGED);
            }
        }

        /**
         * Gets the value for the given key in the collection, if one exists.
         *
         * @param key The key to search for.
         * @param keyOnlyComparator The key comparator.
         * @return The value for the given key, or {@code null} if the key was not found.
         */
        V getValue(K key, EqualityComparator<? super Map.Entry<K, V>> keyOnlyComparator) {
            if (isEmpty()) {
                return null;
            }

            KeyValuePair<K, V> kv = new KeyValuePair<K, V>(key, null);
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
        K getKey(K equalKey, EqualityComparator<? super Map.Entry<K, V>> keyOnlyComparator) {
            if (isEmpty()) {
                return null;
            }

            KeyValuePair<K, V> kv = new KeyValuePair<K, V>(equalKey, null);
            if (keyOnlyComparator.equals(firstValue, kv)) {
                return firstValue.getKey();
            }

            int index = additionalElements.indexOf(kv, keyOnlyComparator);
            if (index < 0) {
                return null;
            }

            return additionalElements.get(index).getKey();
        }

        KeyValuePair<K, V> getEntry(K key, EqualityComparator<? super Map.Entry<K, V>> keyOnlyComparator) {
            if (isEmpty()) {
                return null;
            }

            KeyValuePair<K, V> kv = new KeyValuePair<K, V>(key, null);
            if (keyOnlyComparator.equals(firstValue, kv)) {
                return firstValue;
            }

            int index = additionalElements.indexOf(kv, keyOnlyComparator);
            if (index < 0) {
                return null;
            }

            return additionalElements.get(index);
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
            private Iterator<KeyValuePair<K, V>> additionalIterator;

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

        private final EqualityComparator<? super Map.Entry<K, V>> keyOnlyComparator;

        private final EqualityComparator<? super HashBucket<K, V>> hashBucketComparator;

        Comparators(EqualityComparator<? super K> keyComparator, EqualityComparator<? super V> valueComparator) {
            Requires.notNull(keyComparator, "keyComparator");
            Requires.notNull(valueComparator, "valueComparator");

            this.keyComparator = keyComparator;
            this.valueComparator = valueComparator;
            this.keyOnlyComparator = new EqualityComparator<Map.Entry<K, V>>() {
                @Override
                public boolean equals(Map.Entry<K, V> a, Map.Entry<K, V> b) {
                    return Comparators.this.equalKeys(a, b);
                }

                @Override
                public int hashCode(Map.Entry<K, V> o) {
                    return Comparators.this.keyHashCode(o);
                }
            };
            this.hashBucketComparator = new EqualityComparator<HashBucket<K, V>>() {
                @Override
                public boolean equals(HashBucket<K, V> a, HashBucket<K, V> b) {
                    return Comparators.this.equals(a, b);
                }

                @Override
                public int hashCode(HashBucket<K, V> o) {
                    return Comparators.this.hashCode(o);
                }
            };
        }

        static <K, V> Comparators<K, V> defaultComparators() {
            @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
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

        EqualityComparator<? super Map.Entry<K, V>> getKeyOnlyComparator() {
            return keyOnlyComparator;
        }

        EqualityComparator<? super V> getValueComparator() {
            return valueComparator;
        }

        EqualityComparator<? super HashBucket<K, V>> getHashBucketComparator() {
            return hashBucketComparator;
        }

        private boolean equals(HashBucket<K, V> x, HashBucket<K, V> y) {
            return x.getAdditionalElements() == y.getAdditionalElements()
                && getKeyComparator().equals(x.getFirstValue().getKey(), y.getFirstValue().getKey())
                && getValueComparator().equals(x.getFirstValue().getValue(), y.getFirstValue().getValue());
        }

        private int hashCode(HashBucket<K, V> obj) {
            return getKeyComparator().hashCode(obj.getFirstValue().getKey());
        }

        private boolean equalKeys(Map.Entry<K, V> x, Map.Entry<K, V> y) {
            return getKeyComparator().equals(x.getKey(), y.getKey());
        }

        private int keyHashCode(Map.Entry<K, V> obj) {
            return getKeyComparator().hashCode(obj.getKey());
        }

        Comparators<K, V> withValueComparator(EqualityComparator<? super V> valueComparator) {
            Requires.notNull(valueComparator, "valueComparator");
            if (valueComparator == this.valueComparator) {
                return this;
            }

            return get(getKeyComparator(), valueComparator);
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

        EqualityComparator<? super Map.Entry<K, V>> getKeyOnlyComparator() {
            return comparators.getKeyOnlyComparator();
        }

        EqualityComparator<? super V> getValueComparator() {
            return comparators.getValueComparator();
        }

        EqualityComparator<? super HashBucket<K, V>> getHashBucketComparator() {
            return comparators.getHashBucketComparator();
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
