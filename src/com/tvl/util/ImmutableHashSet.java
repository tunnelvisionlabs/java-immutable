// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An immutable unordered hash set implementation.
 *
 * @param <T> The type of elements in the set.
 */
public final class ImmutableHashSet<T> implements ImmutableSet<T>, HashKeyCollection<T> {
    /**
     * An empty immutable hash set with the default comparer for {@code T}.
     */
    public static final ImmutableHashSet<?> EMPTY = new ImmutableHashSet<Object>(SortedIntegerKeyNode.<HashBucket<Object>>emptyNode(), EqualityComparators.<Object>defaultComparator(), 0);

    /**
     * The singleton delegate that freezes the contents of hash buckets when the root of the data structure is frozen.
     */
    private static final SortedIntegerKeyNode.FreezeAction<HashBucket<?>> FREEZE_BUCKET_ACTION =
        new SortedIntegerKeyNode.FreezeAction<HashBucket<?>>() {
            @Override
            public void apply(int key, HashBucket<?> value) {
                value.freeze();
            }
        };

    /**
     * The equality comparator used to hash the elements in the collection.
     */
    private final EqualityComparator<? super T> equalityComparator;

    /**
     * The number of elements in this collection.
     */
    private final int size;

    /**
     * The sorted dictionary that this hash set wraps. The key is the hash code and the value is the bucket of all items
     * that hashed to it.
     */
    private final SortedIntegerKeyNode<HashBucket<T>> root;

    /**
     * Constructs a new instance of the {@link ImmutableHashSet} class.
     *
     * @param equalityComparator The equality comparator.
     */
    ImmutableHashSet(EqualityComparator<? super T> equalityComparator) {
        this(SortedIntegerKeyNode.<HashBucket<T>>emptyNode(), equalityComparator, 0);
    }

    /**
     * Constructs a new instance of the {@link ImmutableHashSet} class.
     *
     * @param root The sorted set that this set wraps.
     * @param equalityComparator The equality comparator used by this instance.
     * @param count The number of elements in this collection.
     */
    private ImmutableHashSet(SortedIntegerKeyNode<HashBucket<T>> root, EqualityComparator<? super T> equalityComparator, int count) {
        Requires.notNull(root, "root");
        Requires.notNull(equalityComparator, "equalityComparator");

        root.freeze(FREEZE_BUCKET_ACTION);
        this.root = root;
        this.size = count;
        this.equalityComparator = equalityComparator;
    }

    /**
     * Gets an empty immutable hash set with the default comparer for {@code T}.
     * @param <T> The type of elements in the set.
     * @return An empty immutable hash set with the default comparer for {@code T}.
     */
    public static <T> ImmutableHashSet<T> empty() {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        ImmutableHashSet<T> result = (ImmutableHashSet<T>)EMPTY;
        return result;
    }

    /**
     * Returns an empty collection.
     *
     * @param <T> The type of items stored by the collection.
     * @return The immutable collection.
     */
    public static <T> ImmutableHashSet<T> create() {
        return ImmutableHashSet.<T>empty();
    }

    /**
     * Returns an empty collection.
     *
     * @param <T> The type of items stored by the collection.
     * @param equalityComparator The equality comparator.
     * @return The immutable collection.
     */
    public static <T> ImmutableHashSet<T> create(EqualityComparator<? super T> equalityComparator) {
        return ImmutableHashSet.<T>empty().withComparator(equalityComparator);
    }

    /**
     * Creates a new immutable collection prefilled with the specified item.
     *
     * @param <T> The type of items stored by the collection.
     * @param item The item to prepopulate.
     * @return The new immutable collection.
     */
    public static <T> ImmutableHashSet<T> create(T item) {
        return ImmutableHashSet.<T>empty().add(item);
    }

    /**
     * Creates a new immutable collection prefilled with the specified item.
     *
     * @param <T> The type of items stored by the collection.
     * @param equalityComparator The equality comparator.
     * @param item The item to prepopulate.
     * @return The new immutable collection.
     */
    public static <T> ImmutableHashSet<T> create(EqualityComparator<? super T> equalityComparator, T item) {
        return ImmutableHashSet.<T>empty().withComparator(equalityComparator).add(item);
    }

    /**
     * Creates a new immutable collection prefilled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param items The items to prepopulate.
     * @return The new immutable collection.
     */
    public static <T> ImmutableHashSet<T> createAll(Iterable<? extends T> items) {
        return ImmutableHashSet.<T>empty().union(items);
    }

    /**
     * Creates a new immutable collection prefilled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param equalityComparator The equality comparator.
     * @param items The items to prepopulate.
     * @return The new immutable collection.
     */
    public static <T> ImmutableHashSet<T> createAll(EqualityComparator<? super T> equalityComparator, Iterable<? extends T> items) {
        return ImmutableHashSet.<T>empty().withComparator(equalityComparator).union(items);
    }

    /**
     * Creates a new immutable collection prefilled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param items The items to prepopulate.
     * @return The new immutable collection.
     */
    public static <T> ImmutableHashSet<T> create(T... items) {
        return ImmutableHashSet.<T>empty().union(Arrays.asList(items));
    }

    /**
     * Creates a new immutable collection prefilled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param equalityComparator The equality comparator.
     * @param items The items to prepopulate.
     * @return The new immutable collection.
     */
    public static <T> ImmutableHashSet<T> create(EqualityComparator<? super T> equalityComparator, T... items) {
        return ImmutableHashSet.<T>empty().withComparator(equalityComparator).union(Arrays.asList(items));
    }

    /**
     * Creates a new immutable hash set builder.
     *
     * @param <T> The type of items stored by the collection.
     * @return The immutable collection.
     */
    public static <T> ImmutableHashSet.Builder<T> createBuilder() {
        return ImmutableHashSet.<T>create().toBuilder();
    }

    /**
     * Creates a new immutable hash set builder.
     *
     * @param <T> The type of items stored by the collection.
     * @param equalityComparator The equality comparator.
     * @return The immutable collection.
     */
    public static <T> ImmutableHashSet.Builder<T> createBuilder(EqualityComparator<? super T> equalityComparator) {
        return ImmutableHashSet.<T>create(equalityComparator).toBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableHashSet<T> clear() {
        //Contract.Ensures(Contract.Result<ImmutableHashSet<T>>() != null);
        //Contract.Ensures(Contract.Result<ImmutableHashSet<T>>().IsEmpty);
        return this.isEmpty() ? this : ImmutableHashSet.<T>empty().withComparator(equalityComparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

//        #region IHashKeyCollection<T> Properties

    /**
     * {@inheritDoc}
     */
    @Override
    public EqualityComparator<? super T> getKeyComparator() {
        return equalityComparator;
    }

//        #endregion
//
//        #region IImmutableSet<T> Properties
//
//        /// <summary>
//        /// See the <see cref="IImmutableSet{T}"/> interface.
//        /// </summary>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.Clear()
//        {
//            return this.Clear();
//        }
//
//        #endregion
//
//        #region ICollection Properties
//
//        /// <summary>
//        /// See <see cref="ICollection"/>.
//        /// </summary>
//        [DebuggerBrowsable(DebuggerBrowsableState.Never)]
//        object ICollection.SyncRoot
//        {
//            get { return this; }
//        }
//
//        /// <summary>
//        /// See the <see cref="ICollection"/> interface.
//        /// </summary>
//        [DebuggerBrowsable(DebuggerBrowsableState.Never)]
//        bool ICollection.IsSynchronized
//        {
//            get
//            {
//                // This is immutable, so it is always thread-safe.
//                return true;
//            }
//        }
//
//        #endregion

    /**
     * Gets the root node (for testing purposes).
     */
    BinaryTree<SortedIntegerKeyNode.IntegerKeyEntry<HashBucket<T>>> getRoot() {
        return root;
    }

    /**
     * Gets a data structure that captures the current state of this map, as an input into a query or mutating function.
     */
    private MutationInput<T> getOrigin() {
        return new MutationInput<T>(this);
    }

//        #region Public methods

    /**
     * Creates a collection with the same contents as this collection that can be efficiently mutated across multiple
     * operations using standard mutable interfaces.
     *
     * <p>This is an O(1) operation and results in only a single (small) memory allocation. The mutable collection that
     * is returned is <em>not</em> thread-safe.</p>
     */
    public Builder<T> toBuilder() {
        // We must not cache the instance created here and return it to various callers.
        // Those who request a mutable collection must get references to the collection
        // that version independently of each other.
        return new Builder<T>(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableHashSet<T> add(T item) {
        Requires.notNull(item, "item");
        //Contract.Ensures(Contract.Result<ImmutableHashSet<T>>() != null);

        MutationResult<T> result = add(item, this.getOrigin());
        return result.finalize(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableHashSet<T> remove(T item) {
        Requires.notNull(item, "item");
        //Contract.Ensures(Contract.Result<ImmutableHashSet<T>>() != null);

        MutationResult<T> result = remove(item, this.getOrigin());
        return result.finalize(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T tryGetValue(T equalValue) {
        Requires.notNull(equalValue, "equalValue");

        int hashCode = equalityComparator.hashCode(equalValue);
        HashBucket<T> bucket = root.getValueOrDefault(hashCode);
        if (bucket != null) {
            StrongBox<T> actualValue = new StrongBox<T>();
            if (bucket.tryExchange(equalValue, equalityComparator, actualValue)) {
                return actualValue.value;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableHashSet<T> union(Iterable<? extends T> other) {
        Requires.notNull(other, "other");
        //Contract.Ensures(Contract.Result<ImmutableHashSet<T>>() != null);

        return this.union(other, /*avoidWithComparator:*/ false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableHashSet<T> intersect(Iterable<? extends T> other) {
        Requires.notNull(other, "other");
        //Contract.Ensures(Contract.Result<ImmutableHashSet<T>>() != null);

        MutationResult<T> result = intersect(other, this.getOrigin());
        return result.finalize(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableHashSet<T> except(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        MutationResult<T> result = except(other, equalityComparator, root);
        return result.finalize(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableHashSet<T> symmetricExcept(Iterable<? extends T> other) {
        Requires.notNull(other, "other");
        //Contract.Ensures(Contract.Result<IImmutableSet<T>>() != null);

        MutationResult<T> result = symmetricExcept(other, this.getOrigin());
        return result.finalize(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setEquals(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        if (this == other) {
            return true;
        }

        return setEquals(other, this.getOrigin());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProperSubsetOf(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        return isProperSubsetOf(other, this.getOrigin());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProperSupersetOf(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        return isProperSupersetOf(other, this.getOrigin());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSubsetOf(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        return isSubsetOf(other, this.getOrigin());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupersetOf(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        return isSupersetOf(other, this.getOrigin());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean overlaps(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        return overlaps(other, this.getOrigin());
    }

//        #endregion
//
//        #region IImmutableSet<T> Methods
//
//        /// <summary>
//        /// See the <see cref="IImmutableSet{T}"/> interface.
//        /// </summary>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.Add(T item)
//        {
//            return this.Add(item);
//        }
//
//        /// <summary>
//        /// See the <see cref="IImmutableSet{T}"/> interface.
//        /// </summary>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.Remove(T item)
//        {
//            return this.Remove(item);
//        }
//
//        /// <summary>
//        /// See the <see cref="IImmutableSet{T}"/> interface.
//        /// </summary>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.Union(IEnumerable<T> other)
//        {
//            return this.Union(other);
//        }
//
//        /// <summary>
//        /// See the <see cref="IImmutableSet{T}"/> interface.
//        /// </summary>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.Intersect(IEnumerable<T> other)
//        {
//            return this.Intersect(other);
//        }
//
//        /// <summary>
//        /// See the <see cref="IImmutableSet{T}"/> interface.
//        /// </summary>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.Except(IEnumerable<T> other)
//        {
//            return this.Except(other);
//        }
//
//        /// <summary>
//        /// Produces a set that contains elements either in this set or a given sequence, but not both.
//        /// </summary>
//        /// <param name="other">The other sequence of items.</param>
//        /// <returns>The new set.</returns>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.SymmetricExcept(IEnumerable<T> other)
//        {
//            return this.SymmetricExcept(other);
//        }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(T item) {
        Requires.notNull(item, "item");
        return contains(item, this.getOrigin());
    }

    /**
     * {@inheritDoc}
     */
    public ImmutableHashSet<T> withComparator(EqualityComparator<? super T> equalityComparator) {
        //Contract.Ensures(Contract.Result<ImmutableHashSet<T>>() != null);
        if (equalityComparator == null) {
            equalityComparator = EqualityComparators.defaultComparator();
        }

        if (equalityComparator == this.equalityComparator) {
            return this;
        } else {
            ImmutableHashSet<T> result = new ImmutableHashSet<T>(equalityComparator);
            result = result.union(this, /*avoidWithComparator:*/ true);
            return result;
        }
    }

//        #endregion
//
//        #region ISet<T> Members
//
//        /// <summary>
//        /// See <see cref="ISet{T}"/>
//        /// </summary>
//        bool ISet<T>.Add(T item)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// See <see cref="ISet{T}"/>
//        /// </summary>
//        void ISet<T>.ExceptWith(IEnumerable<T> other)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// See <see cref="ISet{T}"/>
//        /// </summary>
//        void ISet<T>.IntersectWith(IEnumerable<T> other)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// See <see cref="ISet{T}"/>
//        /// </summary>
//        void ISet<T>.SymmetricExceptWith(IEnumerable<T> other)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// See <see cref="ISet{T}"/>
//        /// </summary>
//        void ISet<T>.UnionWith(IEnumerable<T> other)
//        {
//            throw new NotSupportedException();
//        }
//
//        #endregion
//
//        #region ICollection<T> members
//
//        /// <summary>
//        /// See the <see cref="ICollection{T}"/> interface.
//        /// </summary>
//        bool ICollection<T>.IsReadOnly
//        {
//            get { return true; }
//        }
//
//        /// <summary>
//        /// See the <see cref="ICollection{T}"/> interface.
//        /// </summary>
//        void ICollection<T>.CopyTo(T[] array, int arrayIndex)
//        {
//            Requires.NotNull(array, "array");
//            Requires.Range(arrayIndex >= 0, "arrayIndex");
//            Requires.Range(array.Length >= arrayIndex + this.Count, "arrayIndex");
//
//            foreach (T item in this)
//            {
//                array[arrayIndex++] = item;
//            }
//        }
//
//        /// <summary>
//        /// See the <see cref="IList{T}"/> interface.
//        /// </summary>
//        void ICollection<T>.Add(T item)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// See the <see cref="ICollection{T}"/> interface.
//        /// </summary>
//        void ICollection<T>.Clear()
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// See the <see cref="IList{T}"/> interface.
//        /// </summary>
//        bool ICollection<T>.Remove(T item)
//        {
//            throw new NotSupportedException();
//        }
//
//        #endregion
//
//        #region ICollection Methods
//
//        /// <summary>
//        /// Copies the elements of the <see cref="ICollection"/> to an <see cref="Array"/>, starting at a particular <see cref="Array"/> index.
//        /// </summary>
//        /// <param name="array">The one-dimensional <see cref="Array"/> that is the destination of the elements copied from <see cref="ICollection"/>. The <see cref="Array"/> must have zero-based indexing.</param>
//        /// <param name="arrayIndex">The zero-based index in <paramref name="array"/> at which copying begins.</param>
//        void ICollection.CopyTo(Array array, int arrayIndex)
//        {
//            Requires.NotNull(array, "array");
//            Requires.Range(arrayIndex >= 0, "arrayIndex");
//            Requires.Range(array.Length >= arrayIndex + this.Count, "arrayIndex");
//
//            foreach (T item in this)
//            {
//                array.SetValue(item, arrayIndex++);
//            }
//        }
//
//        #endregion
//
//        #region IEnumerable<T> Members

    /**
     * Returns an iterator that iterates through the collection.
     *
     * @return An {@link Iterator} that can be used to iterate through the collection.
     */
    @Override
    public Iterator<T> iterator() {
        return new IteratorImpl<T>(root);
    }

//        /// <summary>
//        /// Returns an enumerator that iterates through the collection.
//        /// </summary>
//        IEnumerator<T> IEnumerable<T>.GetEnumerator()
//        {
//            return this.GetEnumerator();
//        }
//
//        #endregion
//
//        #region IEnumerable Members
//
//        /// <summary>
//        /// Returns an enumerator that iterates through a collection.
//        /// </summary>
//        /// <returns>
//        /// An <see cref="IEnumerator"/> object that can be used to iterate through the collection.
//        /// </returns>
//        IEnumerator IEnumerable.GetEnumerator()
//        {
//            return this.GetEnumerator();
//        }
//
//        #endregion
//
//        #region Static query and manipulator methods

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> boolean isSupersetOf(Iterable<? extends T> other, MutationInput<T> origin) {
        Requires.notNull(other, "other");

        for (T item : other) {
            if (!contains(item, origin)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> MutationResult<T> add(T item, MutationInput<T> origin) {
        Requires.notNull(item, "item");

        StrongBox<OperationResult> result = new StrongBox<OperationResult>();
        int hashCode = origin.getEqualityComparator().hashCode(item);
        HashBucket<T> bucket = origin.getRoot().getValueOrDefault(hashCode);
        if (bucket == null) {
            bucket = HashBucket.empty();
        }

        HashBucket<T> newBucket = bucket.add(item, origin.getEqualityComparator(), result);
        if (result.value == OperationResult.NO_CHANGE_REQUIRED) {
            return new MutationResult<T>(origin.getRoot(), 0);
        }

        SortedIntegerKeyNode<HashBucket<T>> newRoot = updateRoot(origin.getRoot(), hashCode, newBucket);
        assert (result.value == OperationResult.SIZE_CHANGED);
        return new MutationResult<T>(newRoot, 1 /*result == OperationResult.SIZE_CHANGED ? 1 : 0*/);
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> MutationResult<T> remove(T item, MutationInput<T> origin) {
        Requires.notNull(item, "item");

        StrongBox<OperationResult> result = new StrongBox<OperationResult>(OperationResult.NO_CHANGE_REQUIRED);
        int hashCode = origin.getEqualityComparator().hashCode(item);
        SortedIntegerKeyNode<HashBucket<T>> newRoot = origin.getRoot();
        HashBucket<T> bucket = origin.getRoot().getValueOrDefault(hashCode);
        if (bucket != null) {
            HashBucket<T> newBucket = bucket.remove(item, origin.getEqualityComparator(), result);
            if (result.value == OperationResult.NO_CHANGE_REQUIRED) {
                return new MutationResult<T>(origin.getRoot(), 0);
            }

            newRoot = updateRoot(origin.getRoot(), hashCode, newBucket);
        }

        return new MutationResult<T>(newRoot, result.value == OperationResult.SIZE_CHANGED ? -1 : 0);
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> boolean contains(T item, MutationInput<T> origin) {
        int hashCode = origin.getEqualityComparator().hashCode(item);
        HashBucket<T> bucket = origin.getRoot().getValueOrDefault(hashCode);
        if (bucket != null) {
            return bucket.contains(item, origin.getEqualityComparator());
        }

        return false;
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> MutationResult<T> union(Iterable<? extends T> other, MutationInput<T> origin) {
        Requires.notNull(other, "other");

        int count = 0;
        SortedIntegerKeyNode<HashBucket<T>> newRoot = origin.getRoot();
        for (T item : other) {
            int hashCode = origin.getEqualityComparator().hashCode(item);
            HashBucket<T> bucket = newRoot.getValueOrDefault(hashCode);
            StrongBox<OperationResult> result = new StrongBox<OperationResult>();
            HashBucket<T> newBucket = bucket.add(item, origin.getEqualityComparator(), result);
            if (result.value == OperationResult.SIZE_CHANGED) {
                newRoot = updateRoot(newRoot, hashCode, newBucket);
                count++;
            }
        }

        return new MutationResult<T>(newRoot, count);
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> boolean overlaps(Iterable<? extends T> other, MutationInput<T> origin) {
        Requires.notNull(other, "other");

        if (origin.getRoot().isEmpty()) {
            return false;
        }

        for (T item : other) {
            if (contains(item, origin)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> boolean setEquals(Iterable<? extends T> other, MutationInput<T> origin) {
        Requires.notNull(other, "other");

        ImmutableHashSet<? extends T> otherSet = createAll(origin.getEqualityComparator(), other);
        if (origin.size() != otherSet.size()) {
            return false;
        }

        int matches = 0;
        for (T item : otherSet) {
            if (!contains(item, origin)) {
                return false;
            }

            matches++;
        }

        return matches == origin.size();
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> SortedIntegerKeyNode<HashBucket<T>> updateRoot(SortedIntegerKeyNode<HashBucket<T>> root, int hashCode, HashBucket<T> newBucket) {
        if (newBucket.isEmpty()) {
            return root.remove(hashCode).result;
        } else {
            return root.setItem(hashCode, newBucket, EqualityComparators.<HashBucket<T>>defaultComparator()).result;
        }
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> MutationResult<T> intersect(Iterable<? extends T> other, MutationInput<T> origin) {
        Requires.notNull(other, "other");

        SortedIntegerKeyNode<HashBucket<T>> newSet = SortedIntegerKeyNode.emptyNode();
        int size = 0;
        for (T item : other) {
            if (contains(item, origin)) {
                MutationResult<T> result = add(item, new MutationInput<T>(newSet, origin.getEqualityComparator(), size));
                newSet = result.getRoot();
                size += result.size();
            }
        }

        return new MutationResult<T>(newSet, size, SizeType.FINAL_VALUE);
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> MutationResult<T> except(Iterable<? extends T> other, EqualityComparator<? super T> equalityComparator, SortedIntegerKeyNode<HashBucket<T>> root) {
        Requires.notNull(other, "other");
        Requires.notNull(equalityComparator, "equalityComparator");
        Requires.notNull(root, "root");

        int size = 0;
        SortedIntegerKeyNode<HashBucket<T>> newRoot = root;
        for (T item : other) {
            int hashCode = equalityComparator.hashCode(item);
            HashBucket<T> bucket = newRoot.getValueOrDefault(hashCode);
            if (bucket != null) {
                StrongBox<OperationResult> result = new StrongBox<OperationResult>();
                HashBucket<T> newBucket = bucket.remove(item, equalityComparator, result);
                if (result.value == OperationResult.SIZE_CHANGED) {
                    size--;
                    newRoot = updateRoot(newRoot, hashCode, newBucket);
                }
            }
        }

        return new MutationResult<T>(newRoot, size);
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> MutationResult<T> symmetricExcept(Iterable<? extends T> other, MutationInput<T> origin) {
        Requires.notNull(other, "other");

        ImmutableHashSet<T> otherAsSet = ImmutableHashSet.<T>createAll(other);

        int size = 0;
        SortedIntegerKeyNode<HashBucket<T>> result = SortedIntegerKeyNode.emptyNode();
        for (T item : new NodeEnumerable<T>(origin.getRoot())) {
            if (!otherAsSet.contains(item)) {
                MutationResult<T> mutationResult = add(item, new MutationInput<T>(result, origin.getEqualityComparator(), size));
                result = mutationResult.getRoot();
                size += mutationResult.size();
            }
        }

        for (T item : otherAsSet) {
            if (!contains(item, origin)) {
                MutationResult<T> mutationResult = add(item, new MutationInput<T>(result, origin.getEqualityComparator(), size));
                result = mutationResult.getRoot();
                size += mutationResult.size();
            }
        }

        return new MutationResult<T>(result, size, SizeType.FINAL_VALUE);
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> boolean isProperSubsetOf(Iterable<? extends T> other, MutationInput<T> origin) {
        Requires.notNull(other, "other");

        if (origin.getRoot().isEmpty()) {
            return other.iterator().hasNext();
        }

        // To determine whether everything we have is also in another sequence,
        // we enumerate the sequence and "tag" whether it's in this collection,
        // then consider whether every element in this collection was tagged.
        // Since this collection is immutable we cannot directly tag.  So instead
        // we simply count how many "hits" we have and ensure it's equal to the
        // size of this collection.  Of course for this to work we need to ensure
        // the uniqueness of items in the given sequence, so we create a set based
        // on the sequence first.
        ImmutableHashSet<? extends T> otherSet = createAll(origin.getEqualityComparator(), other);
        if (origin.size() >= otherSet.size()) {
            return false;
        }

        int matches = 0;
        boolean extraFound = false;
        for (T item : otherSet) {
            if (contains(item, origin)) {
                matches++;
            } else {
                extraFound = true;
            }

            if (matches == origin.size() && extraFound) {
                return true;
            }
        }

        return false;
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> boolean isProperSupersetOf(Iterable<? extends T> other, MutationInput<T> origin) {
        Requires.notNull(other, "other");

        if (origin.getRoot().isEmpty()) {
            return false;
        }

        int matchCount = 0;
        for (T item : other) {
            matchCount++;
            if (!contains(item, origin)) {
                return false;
            }
        }

        return origin.size() > matchCount;
    }

    /**
     * Performs the set operation on a given data structure.
     */
    private static <T> boolean isSubsetOf(Iterable<? extends T> other, MutationInput<T> origin) {
        Requires.notNull(other, "other");

        if (origin.getRoot().isEmpty()) {
            return true;
        }

        // To determine whether everything we have is also in another sequence,
        // we enumerate the sequence and "tag" whether it's in this collection,
        // then consider whether every element in this collection was tagged.
        // Since this collection is immutable we cannot directly tag.  So instead
        // we simply count how many "hits" we have and ensure it's equal to the
        // size of this collection.  Of course for this to work we need to ensure
        // the uniqueness of items in the given sequence, so we create a set based
        // on the sequence first.
        ImmutableHashSet<? extends T> otherSet = createAll(origin.getEqualityComparator(), other);
        int matches = 0;
        for (T item : otherSet) {
            if (contains(item, origin)) {
                matches++;
            }
        }

        return matches == origin.size();
    }

//        #endregion

    /**
     * Wraps the specified data structure with an immutable collection wrapper.
     *
     * @param <T> The type of elements in the set.
     * @param root The root of the data structure.
     * @param equalityComparator The equality comparator.
     * @param size The number of elements in the data structure.
     * @return The immutable collection.
     */
    private static <T> ImmutableHashSet<T> wrap(SortedIntegerKeyNode<HashBucket<T>> root, EqualityComparator<? super T> equalityComparator, int size) {
        Requires.notNull(root, "root");
        Requires.notNull(equalityComparator, "equalityComparator");
        Requires.range(size >= 0, "size");
        return new ImmutableHashSet<T>(root, equalityComparator, size);
    }

    /**
     * Wraps the specified data structure with an immutable collection wrapper.
     *
     * @param root The root of the data structure.
     * @param adjustedSizeIfDifferentRoot The adjusted size if the root has changed.
     * @return The immutable collection.
     */
    private ImmutableHashSet<T> wrap(SortedIntegerKeyNode<HashBucket<T>> root, int adjustedSizeIfDifferentRoot) {
        return (root != this.root) ? new ImmutableHashSet<T>(root, equalityComparator, adjustedSizeIfDifferentRoot) : this;
    }

    /**
     * Bulk adds entries to the set.
     *
     * @param items The entries to add.
     * @param avoidWithComparator {@code true} when being called from {@link #withComparator} to avoid
     * {@link StackOverflowError}.
     */
    private ImmutableHashSet<T> union(Iterable<? extends T> items, boolean avoidWithComparator) {
        Requires.notNull(items, "items");
        //Contract.Ensures(Contract.Result<ImmutableHashSet<T>>() != null);

        // Some optimizations may apply if we're an empty set.
        if (this.isEmpty() && !avoidWithComparator) {
            // If the items being added actually come from an ImmutableHashSet<T>,
            // reuse that instance if possible.
            if (items instanceof ImmutableHashSet<?>) {
                ImmutableHashSet<? extends T> other = (ImmutableHashSet<? extends T>)items;
                ImmutableHashSet<? extends T> result = other.withComparator(this.getKeyComparator());

                // Safe because we just set the comparator
                @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
                ImmutableHashSet<T> upcast = (ImmutableHashSet<T>)result;

                return upcast;
            }
        }

        MutationResult<T> result = union(items, this.getOrigin());
        return result.finalize(this);
    }

    /**
     * Iterates the contents of the collection.
     */
    public static final class IteratorImpl<T> implements Iterator<T> {
        /**
         * The builder being iterated, if applicable.
         */
        private final Builder<T> builder;

        /**
         * The iterator over the sorted dictionary whose keys are hash values.
         */
        private SortedIntegerKeyNode.Itr<HashBucket<T>> mapIterator;

        /**
         * The iterator in use within an individual {@link HashBucket}.
         */
        private HashBucket<T>.IteratorImpl bucketIterator;

        /**
         * The version of the builder (when applicable) that is being iterated.
         */
        private int iteratingBuilderVersion;

        /**
         * Constructs a new instance of the {@link IteratorImpl} class.
         *
         * @param root The root.
         */
        IteratorImpl(SortedIntegerKeyNode<HashBucket<T>> root) {
            this(root, null);
        }

        /**
         * Constructs a new instance of the {@link IteratorImpl} class.
         *
         * @param root The root.
         * @param builder The builder, if applicable.
         */
        IteratorImpl(SortedIntegerKeyNode<HashBucket<T>> root, Builder<T> builder) {
            this.builder = builder;
            this.mapIterator = new SortedIntegerKeyNode.Itr<HashBucket<T>>(root);
            this.bucketIterator = HashBucket.<T>empty().iterator();
            this.iteratingBuilderVersion = builder != null ? builder.getVersion() : -1;
        }

        @Override
        public boolean hasNext() {
            throwIfChanged();
            return mapIterator.hasNext() || bucketIterator.hasNext();
        }

        @Override
        public T next() {
            throwIfChanged();
            if (!bucketIterator.hasNext()) {
                bucketIterator = mapIterator.next().getValue().iterator();
            }

            return bucketIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

//            /// <summary>
//            /// Gets the current element.
//            /// </summary>
//            public T Current
//            {
//                get
//                {
//                    _mapEnumerator.ThrowIfDisposed();
//                    return _bucketEnumerator.Current;
//                }
//            }
//
//            /// <summary>
//            /// Gets the current element.
//            /// </summary>
//            object IEnumerator.Current
//            {
//                get { return this.Current; }
//            }
//
//            /// <summary>
//            /// Advances the enumerator to the next element of the collection.
//            /// </summary>
//            /// <returns>
//            /// true if the enumerator was successfully advanced to the next element; false if the enumerator has passed the end of the collection.
//            /// </returns>
//            /// <exception cref="InvalidOperationException">The collection was modified after the enumerator was created. </exception>
//            public bool MoveNext()
//            {
//                this.ThrowIfChanged();
//
//                if (_bucketEnumerator.MoveNext())
//                {
//                    return true;
//                }
//
//                if (_mapEnumerator.MoveNext())
//                {
//                    _bucketEnumerator = new HashBucket.Enumerator(_mapEnumerator.Current.Value);
//                    return _bucketEnumerator.MoveNext();
//                }
//
//                return false;
//            }
//
//            /// <summary>
//            /// Sets the enumerator to its initial position, which is before the first element in the collection.
//            /// </summary>
//            /// <exception cref="InvalidOperationException">The collection was modified after the enumerator was created. </exception>
//            public void Reset()
//            {
//                _enumeratingBuilderVersion = _builder != null ? _builder.Version : -1;
//                _mapEnumerator.Reset();
//
//                // Resetting the bucket enumerator is pointless because we'll start on a new bucket later anyway.
//                _bucketEnumerator.Dispose();
//                _bucketEnumerator = default(HashBucket.Enumerator);
//            }
//
//            /// <summary>
//            /// Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
//            /// </summary>
//            public void Dispose()
//            {
//                _mapEnumerator.Dispose();
//                _bucketEnumerator.Dispose();
//            }

        /**
         * Throws an exception if the underlying builder's contents have been changed since enumeration started.
         *
         * @throws ConcurrentModificationException Thrown if the collection has changed.
         */
        private void throwIfChanged() throws ConcurrentModificationException {
            if (builder != null && builder.getVersion() != iteratingBuilderVersion) {
                throw new ConcurrentModificationException("CollectionModifiedDuringEnumeration");
            }
        }
    }

    /**
     * A hash set that mutates with little or no memory allocations, can produce and/or build on immutable hash set
     * instances very efficiently.
     *
     * <p>While {@link ImmutableHashSet#union(Iterable)} and other bulk change methods already provide fast bulk change
     * operations on the collection, this class allows multiple combinations of changes to be made to a set with equal
     * efficiency.</p>
     *
     * <p>Instance members of this class are <em>not</em> thread-safe.</p>
     */
    //[SuppressMessage("Microsoft.Naming", "CA1710:IdentifiersShouldHaveCorrectSuffix", Justification = "Ignored")]
    //[SuppressMessage("Microsoft.Design", "CA1034:NestedTypesShouldNotBeVisible", Justification = "Ignored")]
    //[DebuggerDisplay("Count = {Count}")]
    public static final class Builder<T> implements ReadOnlyCollection<T>, Set<T> {
        /**
         * The root of the binary tree that stores the collection. Contents are typically not entirely frozen.
         */
        private SortedIntegerKeyNode<HashBucket<T>> root = SortedIntegerKeyNode.emptyNode();

        /**
         * The equality comparator.
         */
        private EqualityComparator<? super T> equalityComparator;

        /**
         * The number of elements in this collection.
         */
        private int size;

        /**
         * Caches an immutable instance that represents the current state of the collection.
         *
         * <p>{@code null} if no immutable view has been created for the current {@link #version}.</p>
         */
        private ImmutableHashSet<T> immutable;

        /**
         * A number that increments every time the builder changes its contents.
         */
        private int version;

        /**
         * Constructs a new instance of the {@link Builder} class.
         *
         * @param set The set.
         */
        Builder(ImmutableHashSet<T> set) {
            Requires.notNull(set, "set");
            this.root = set.root;
            this.size = set.size;
            this.equalityComparator = set.equalityComparator;
            this.immutable = set;
        }

//            #region ISet<T> Properties

        /**
         * Gets the number of elements contained in the collection.
         *
         * @return The number of elements contained in the collection.
         */
        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

//            /// <summary>
//            /// Gets a value indicating whether the <see cref="ICollection{T}"/> is read-only.
//            /// </summary>
//            /// <returns>true if the <see cref="ICollection{T}"/> is read-only; otherwise, false.</returns>
//            bool ICollection<T>.IsReadOnly
//            {
//                get { return false; }
//            }
//
//            #endregion

        /**
         * Gets the key comparator.
         *
         * @return The key comparator.
         */
        public EqualityComparator<? super T> getKeyComparator() {
            return equalityComparator;
        }

        /**
         * Sets the key comparator.
         *
         * @param value The key comparator.
         */
        public void setKeyComparator(EqualityComparator<? super T> value) {
            Requires.notNull(value, "value");

            if (value != equalityComparator) {
                MutationResult<T> result = union(this, new MutationInput<T>(SortedIntegerKeyNode.<HashBucket<T>>emptyNode(), value, 0));

                this.immutable = null;
                this.equalityComparator = value;
                this.setRoot(result.getRoot());
                this.size = result.size(); // whether the offset or absolute, since the base is 0, it's no difference.
            }
        }

        /**
         * Gets the current version of the contents of this builder.
         */
        int getVersion() {
            return version;
        }

        /**
         * Gets the initial data to pass to a query or mutation method.
         */
        private MutationInput<T> getOrigin() {
            return new MutationInput<T>(this.getRoot(), equalityComparator, size);
        }

        /**
         * Gets the root of this data structure.
         */
        private SortedIntegerKeyNode<HashBucket<T>> getRoot() {
            return root;
        }

        /**
         * Sets the root of this data structure.
         */
        private void setRoot(SortedIntegerKeyNode<HashBucket<T>> value) {
            // We *always* increment the version number because some mutations
            // may not create a new value of root, although the existing root
            // instance may have mutated.
            version++;

            if (root != value) {
                root = value;

                // Clear any cached value for the immutable view since it is now invalidated.
                immutable = null;
            }
        }

//            #region Public methods

        /**
         * Returns an iterator that iterates through the collection.
         *
         * @return A {@link Iterator} that can be used to iterate through the collection.
         */
        @Override
        public IteratorImpl<T> iterator() {
            return new IteratorImpl<T>(root, this);
        }

        /**
         * Creates an immutable hash set based on the contents of this instance.
         *
         * <p>This method is an O(n) operation, and approaches O(1) time as the number of actual mutations to the set
         * since the last call to this method approaches 0.</p>
         *
         * @return An immutable set.
         */
        public ImmutableHashSet<T> toImmutable() {
            // Creating an instance of ImmutableSortedMap<T> with our root node automatically freezes our tree,
            // ensuring that the returned instance is immutable.  Any further mutations made to this builder
            // will clone (and unfreeze) the spine of modified nodes until the next time this method is invoked.
            if (immutable == null) {
                immutable = ImmutableHashSet.wrap(root, equalityComparator, size);
            }

            return immutable;
        }

//            #endregion
//
//            #region ISet<T> Methods

        /**
         * Adds the specified item.
         *
         * @param item The item.
         * @return {@code true} if the item did not already belong to the collection.
         */
        @Override
        public boolean add(T item) {
            MutationResult<T> result = ImmutableHashSet.add(item, this.getOrigin());
            this.apply(result);
            return result.size() != 0;
        }

        /**
         * Removes the first occurrence of a specific object from the collection.
         *
         * @param item The object to remove from the collection.
         * @return {@code true} if {@code item} was successfully removed from the collection; otherwise, {@code false}.
         * This method also returns {@code false} if {@code item} is not found in the original collection.
         */
        @Override
        public boolean remove(Object item) {
            MutationResult<T> result = ImmutableHashSet.remove((T)item, this.getOrigin());
            this.apply(result);
            return result.size() != 0;
        }

        /**
         * Determines whether the collection contains a specific value.
         *
         * @param item The object to locate in the collection.
         * @return {@code true} if {@code item} is found in the collection; otherwise, {@code false}.
         */
        @Override
        public boolean contains(Object item) {
            return ImmutableHashSet.contains((T)item, this.getOrigin());
        }

        /**
         * Removes all items from the collection.
         */
        @Override
        public void clear() {
            size = 0;
            this.setRoot(SortedIntegerKeyNode.<HashBucket<T>>emptyNode());
        }

        @Override
        public boolean addAll(Collection<? extends T> set) {
            int size = size();
            unionWith(set);
            return size() != size;
        }

        @Override
        public boolean removeAll(Collection<?> set) {
            int size = size();
            exceptWith((Iterable<? extends T>)set);
            return size() != size;
        }

        @Override
        public boolean retainAll(Collection<?> set) {
            int size = size();
            intersectWith((Iterable<? extends T>)set);
            return size() != size;
        }

        @Override
        public boolean containsAll(Collection<?> set) {
            return isSupersetOf((Iterable<? extends T>)set);
        }

        @Override
        public Object[] toArray() {
            Object[] result = new Object[size()];
            return toArray(result);
        }

        @Override
        public <U> U[] toArray(U[] a) {
            if (a.length < size()) {
                a = Arrays.copyOf(a, size());
            }

            int index = 0;
            for (T element : this) {
                a[index++] = (U)element;
            }

            return a;
        }

        /**
         * Removes all elements in the specified collection from the current set.
         *
         * @param other The collection of items to remove from the set.
         */
        public void exceptWith(Iterable<? extends T> other) {
            MutationResult<T> result = ImmutableHashSet.except(other, equalityComparator, root);
            this.apply(result);
        }

        /**
         * Modifies the current set so that it contains only elements that are also in a specified collection.
         *
         * @param other The collection to compare to the current set.
         */
        public void intersectWith(Iterable<? extends T> other) {
            MutationResult<T> result = ImmutableHashSet.intersect(other, this.getOrigin());
            this.apply(result);
        }

        /**
         * Determines whether the current set is a proper (strict) subset of a specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is a correct subset of {@code other}; otherwise, {@code false}.
         */
        public boolean isProperSubsetOf(Iterable<? extends T> other) {
            return ImmutableHashSet.isProperSubsetOf(other, this.getOrigin());
        }

        /**
         * Determines whether the current set is a proper (strict) superset of a specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is a superset of {@code other}; otherwise, {@code false}.
         */
        public boolean isProperSupersetOf(Iterable<? extends T> other) {
            return ImmutableHashSet.isProperSupersetOf(other, this.getOrigin());
        }

        /**
         * Determines whether the current set is a subset of a specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is a subset of {@code other}; otherwise, {@code false}.
         */
        public boolean isSubsetOf(Iterable<? extends T> other) {
            return ImmutableHashSet.isSubsetOf(other, this.getOrigin());
        }

        /**
         * Determines whether the current set is a superset of a specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is a superset of {@code other}; otherwise, {@code false}.
         */
        public boolean isSupersetOf(Iterable<? extends T> other) {
            return ImmutableHashSet.isSupersetOf(other, this.getOrigin());
        }

        /**
         * Determines whether the current set overlaps with the specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set and {@code other} share at least one common element; otherwise,
         * {@code false}.
         */
        public boolean overlaps(Iterable<? extends T> other) {
            return ImmutableHashSet.overlaps(other, this.getOrigin());
        }

        /**
         * Determines whether the current set and the specified collection contain the same elements.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is equal to {@code other}; otherwise, {@code false}.
         */
        public boolean setEquals(Iterable<? extends T> other) {
            if (this == other) {
                return true;
            }

            return ImmutableHashSet.setEquals(other, this.getOrigin());
        }

        /**
         * Modifies the current set so that it contains only elements that are present either in the current set or in
         * the specified collection, but not both.
         *
         * @param other The collection to compare to the current set.
         */
        public void symmetricExceptWith(Iterable<? extends T> other) {
            MutationResult<T> result = ImmutableHashSet.symmetricExcept(other, this.getOrigin());
            this.apply(result);
        }

        /**
         * Modifies the current set so that it contains all elements that are present in both the current set and in the
         * specified collection.
         *
         * @param other The collection to compare to the current set.
         */
        public void unionWith(Iterable<? extends T> other) {
            MutationResult<T> result = ImmutableHashSet.union(other, this.getOrigin());
            this.apply(result);
        }

//            #endregion
//
//            #region ICollection<T> Members
//
//            /// <summary>
//            /// Adds an item to the <see cref="ICollection{T}"/>.
//            /// </summary>
//            /// <param name="item">The object to add to the <see cref="ICollection{T}"/>.</param>
//            /// <exception cref="NotSupportedException">The <see cref="ICollection{T}"/> is read-only.</exception>
//            void ICollection<T>.Add(T item)
//            {
//                this.Add(item);
//            }
//
//            /// <summary>
//            /// See the <see cref="ICollection{T}"/> interface.
//            /// </summary>
//            void ICollection<T>.CopyTo(T[] array, int arrayIndex)
//            {
//                Requires.NotNull(array, "array");
//                Requires.Range(arrayIndex >= 0, "arrayIndex");
//                Requires.Range(array.Length >= arrayIndex + this.Count, "arrayIndex");
//
//                foreach (T item in this)
//                {
//                    array[arrayIndex++] = item;
//                }
//            }
//
//            #endregion
//
//            #region IEnumerable<T> Members
//
//            /// <summary>
//            /// Returns an enumerator that iterates through the collection.
//            /// </summary>
//            /// <returns>
//            /// A <see cref="IEnumerator{T}"/> that can be used to iterate through the collection.
//            /// </returns>
//            IEnumerator<T> IEnumerable<T>.GetEnumerator()
//            {
//                return this.GetEnumerator();
//            }
//
//            /// <summary>
//            /// Returns an enumerator that iterates through a collection.
//            /// </summary>
//            /// <returns>
//            /// An <see cref="IEnumerator"/> object that can be used to iterate through the collection.
//            /// </returns>
//            IEnumerator IEnumerable.GetEnumerator()
//            {
//                return this.GetEnumerator();
//            }
//
//            #endregion

        /**
         * Applies the result of some mutation operation to this instance.
         *
         * @param result The result.
         */
        private void apply(MutationResult<T> result) {
            this.setRoot(result.getRoot());
            if (result.getSizeType() == SizeType.ADJUSTMENT) {
                this.size += result.size();
            } else {
                this.size = result.size();
            }
        }
    }

    /**
     * The result of a mutation operation.
     */
    enum OperationResult {
        /**
         * The change required element(s) to be added or removed from the collection.
         */
        SIZE_CHANGED,
        /**
         * No change was required (the operation ended in a no-op).
         */
        NO_CHANGE_REQUIRED,
    }

    /**
     * Contains all the keys in the collection that hash to the same value.
     *
     * @param <T> The type of elements stored in the bucket.
     */
    static final class HashBucket<T> {
        private static final HashBucket<?> EMPTY = new HashBucket<Object>();

        /**
         * One of the values in this bucket.
         */
        private final T firstValue;

        /**
         * Any other elements that hash to the same value.
         *
         * <p>This is {@code null} if and only if the entire bucket is empty (including {@link #firstValue}). It's empty
         * if {@link #firstValue} has an element but no additional elements.</p>
         */
        private final ImmutableTreeList.Node<T> additionalElements;

        private HashBucket() {
            firstValue = null;
            additionalElements = null;
        }

        /**
         * Initializes a new instance of the {@link HashBucket} class.
         *
         * @param firstElement The first element
         */
        private HashBucket(T firstElement) {
            this(firstElement, null);
        }

        /**
         * Constructs a new instance of the {@link HashBucket} class.
         *
         * @param firstElement The first element.
         * @param additionalElements The additional elements.
         */
        private HashBucket(T firstElement, ImmutableTreeList.Node<T> additionalElements) {
            this.firstValue = firstElement;
            this.additionalElements = additionalElements != null ? additionalElements : ImmutableTreeList.Node.<T>empty();
        }

        public static <T> HashBucket<T> empty() {
            @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
            HashBucket<T> result = (HashBucket<T>)EMPTY;

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
         * Returns an iterator that iterates through the collection.
         */
        public IteratorImpl iterator() {
            return new IteratorImpl();
        }

        /**
         * Adds the specified value.
         *
         * @param value The value.
         * @param valueComparator The value comparator.
         * @param result A description of the effect was on adding an element to this {@link HashBucket}.
         * @return A new {@link HashBucket} that contains the added value and any values already held by this
         * {@link HashBucket}.
         */
        HashBucket<T> add(T value, EqualityComparator<? super T> valueComparator, StrongBox<OperationResult> result) {
            if (this.isEmpty()) {
                result.value = OperationResult.SIZE_CHANGED;
                return new HashBucket<T>(value);
            }

            if (valueComparator.equals(value, firstValue) || additionalElements.indexOf(value, valueComparator) >= 0) {
                result.value = OperationResult.NO_CHANGE_REQUIRED;
                return this;
            }

            result.value = OperationResult.SIZE_CHANGED;
            return new HashBucket<T>(firstValue, additionalElements.add(value));
        }

        /**
         * Determines whether the {@link HashBucket} contains the specified value.
         *
         * @param value The value.
         * @param valueComparator The value comparator.
         * @return
         */
        boolean contains(T value, EqualityComparator<? super T> valueComparator) {
            if (this.isEmpty()) {
                return false;
            }

            return valueComparator.equals(value, firstValue) || additionalElements.indexOf(value, valueComparator) >= 0;
        }

        /**
         * Searches the set for a given value and returns the equal value it finds, if any.
         *
         * @param value The value to search for.
         * @param valueComparator The value comparator.
         * @param existingValue The value from the set that the search found, or the original value if the search
         * yielded no match.
         * @return A value indicating whether the search was successful.
         */
        boolean tryExchange(T value, EqualityComparator<? super T> valueComparator, StrongBox<T> existingValue) {
            if (!this.isEmpty()) {
                if (valueComparator.equals(value, firstValue)) {
                    existingValue.value = firstValue;
                    return true;
                }

                int index = additionalElements.indexOf(value, valueComparator);
                if (index >= 0) {
                    existingValue.value = additionalElements.get(index);
                    return true;
                }
            }

            existingValue.value = value;
            return false;
        }

        /**
         * Removes the specified value if it exists in the collection.
         *
         * @param value The value.
         * @param equalityComparator The equality comparator.
         * @param result A description of the effect was on removing an element from this {@link HashBucket}.
         * @return A new {@link HashBucket} that does not contain the removed value and any values already held by this
         * {@link HashBucket}.
         */
        HashBucket<T> remove(T value, EqualityComparator<? super T> equalityComparator, StrongBox<OperationResult> result) {
            if (this.isEmpty()) {
                result.value = OperationResult.NO_CHANGE_REQUIRED;
                return this;
            }

            if (equalityComparator.equals(firstValue, value)) {
                if (additionalElements.isEmpty()) {
                    result.value = OperationResult.SIZE_CHANGED;
                    return HashBucket.empty();
                } else {
                    // We can promote any element from the list into the first position, but it's most efficient
                    // to remove the root node in the binary tree that implements the list.
                    int indexOfRootNode = additionalElements.getLeft().size();
                    result.value = OperationResult.SIZE_CHANGED;
                    return new HashBucket<T>(additionalElements.getKey(), additionalElements.remove(indexOfRootNode));
                }
            }

            int index = additionalElements.indexOf(value, equalityComparator);
            if (index < 0) {
                result.value = OperationResult.NO_CHANGE_REQUIRED;
                return this;
            } else {
                result.value = OperationResult.SIZE_CHANGED;
                return new HashBucket<T>(firstValue, additionalElements.remove(index));
            }
        }

        /**
         * Freezes this instance so that any further mutations require new memory allocations.
         */
        void freeze() {
            if (additionalElements != null) {
                additionalElements.freeze();
            }
        }

        /**
         * Describes the positions the iterator state machine may be in.
         */
        private enum Position {
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
             * The end of iteration has been reached.
             */
            END,
        }

        /**
         * Iterates all the elements in this instance.
         */
        final class IteratorImpl implements Iterator<T> {
//                /// <summary>
//                /// The bucket being enumerated.
//                /// </summary>
//                private readonly HashBucket _bucket;
//
//                /// <summary>
//                /// A value indicating whether this enumerator has been disposed.
//                /// </summary>
//                private bool _disposed;

            /**
             * The current position of this iterator.
             */
            private Position currentPosition;

            /**
             * The enumerator that represents the current position over the {@link #additionalElements} of the
             * {@link HashBucket}.
             */
            private ImmutableTreeList.Itr<T> additionalIterator;

            /**
             * Constructs a new instance of the {@link IteratorImpl} class.
             */
            IteratorImpl() {
                //this.disposed = false;
                //this.bucket = bucket;
                this.currentPosition = Position.BEFORE_FIRST;
                this.additionalIterator = ImmutableTreeList.<T>empty().iterator();
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

                default:
                    return false;
                }
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                switch (currentPosition) {
                case BEFORE_FIRST:
                    currentPosition = Position.FIRST;
                    return firstValue;

                case FIRST:
                    currentPosition = Position.ADDITIONAL;
                    additionalIterator = additionalElements.iterator();
                    return additionalIterator.next();

                case ADDITIONAL:
                    return additionalIterator.next();

                default:
                    throw new IllegalStateException("Unreachable");
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

//                /// <summary>
//                /// Gets the current element.
//                /// </summary>
//                object IEnumerator.Current
//                {
//                    get { return this.Current; }
//                }
//
//                /// <summary>
//                /// Gets the current element.
//                /// </summary>
//                public T Current
//                {
//                    get
//                    {
//                        this.ThrowIfDisposed();
//                        switch (_currentPosition)
//                        {
//                            case Position.First:
//                                return _bucket._firstValue;
//                            case Position.Additional:
//                                return _additionalEnumerator.Current;
//                            default:
//                                throw new InvalidOperationException();
//                        }
//                    }
//                }
//
//                /// <summary>
//                /// Advances the enumerator to the next element of the collection.
//                /// </summary>
//                /// <returns>
//                /// true if the enumerator was successfully advanced to the next element; false if the enumerator has passed the end of the collection.
//                /// </returns>
//                /// <exception cref="InvalidOperationException">The collection was modified after the enumerator was created. </exception>
//                public bool MoveNext()
//                {
//                    this.ThrowIfDisposed();
//                    if (_bucket.IsEmpty)
//                    {
//                        _currentPosition = Position.End;
//                        return false;
//                    }
//
//                    switch (_currentPosition)
//                    {
//                        case Position.BeforeFirst:
//                            _currentPosition = Position.First;
//                            return true;
//                        case Position.First:
//                            if (_bucket._additionalElements.IsEmpty)
//                            {
//                                _currentPosition = Position.End;
//                                return false;
//                            }
//
//                            _currentPosition = Position.Additional;
//                            _additionalEnumerator = new ImmutableList<T>.Enumerator(_bucket._additionalElements);
//                            return _additionalEnumerator.MoveNext();
//                        case Position.Additional:
//                            return _additionalEnumerator.MoveNext();
//                        case Position.End:
//                            return false;
//                        default:
//                            throw new InvalidOperationException();
//                    }
//                }
//
//                /// <summary>
//                /// Sets the enumerator to its initial position, which is before the first element in the collection.
//                /// </summary>
//                /// <exception cref="InvalidOperationException">The collection was modified after the enumerator was created. </exception>
//                public void Reset()
//                {
//                    this.ThrowIfDisposed();
//                    _additionalEnumerator.Dispose();
//                    _currentPosition = Position.BeforeFirst;
//                }
//
//                /// <summary>
//                /// Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
//                /// </summary>
//                public void Dispose()
//                {
//                    _disposed = true;
//                    _additionalEnumerator.Dispose();
//                }
//
//                /// <summary>
//                /// Throws an <see cref="ObjectDisposedException"/> if this enumerator has been disposed.
//                /// </summary>
//                private void ThrowIfDisposed()
//                {
//                    if (_disposed)
//                    {
//                        Validation.Requires.FailObjectDisposed(this);
//                    }
//                }
        }
    }

    /**
     * Description of the current data structure as input into a mutating or query method.
     */
    private static final class MutationInput<T> {
        /**
         * The root of the data structure for the collection.
         */
        private final SortedIntegerKeyNode<HashBucket<T>> root;

        /**
         * The equality comparator.
         */
        private final EqualityComparator<? super T> equalityComparator;

        /**
         * The current number of elements in the collection.
         */
        private final int size;

        /**
         * Constructs a new instance of the {@link MutationInput} class.
         *
         * @param set The set.
         */
        MutationInput(ImmutableHashSet<T> set) {
            Requires.notNull(set, "set");
            this.root = set.root;
            this.equalityComparator = set.equalityComparator;
            this.size = set.size;
        }

        /**
         * Constructs a new instance of the {@link MutationInput} class.
         *
         * @param root The root.
         * @param equalityComparator The equality comparator.
         * @param size The size.
         */
        MutationInput(SortedIntegerKeyNode<HashBucket<T>> root, EqualityComparator<? super T> equalityComparator, int size) {
            Requires.notNull(root, "root");
            Requires.notNull(equalityComparator, "equalityComparator");
            Requires.range(size >= 0, "size");
            this.root = root;
            this.equalityComparator = equalityComparator;
            this.size = size;
        }

        /**
         * Gets the root of the data structure for the collection.
         */
        SortedIntegerKeyNode<HashBucket<T>> getRoot() {
            return root;
        }

        /**
         * Gets the equality comparator.
         */
        EqualityComparator<? super T> getEqualityComparator() {
            return equalityComparator;
        }

        /**
         * Gets the current number of elements in the collection.
         */
        int size() {
            return size;
        }
    }

    /**
     * Interpretations for a {@link MutationResult#size()} member.
     */
    private enum SizeType {
        /**
         * The {@link MutationResult#size()} member describes an adjustment to the previous size of the collection.
         */
        ADJUSTMENT,

        /**
         * The {@link MutationResult#size()} member describes the actual size of the collection.
         */
        FINAL_VALUE,
    }

    /**
     * Describes the result of a mutation on the immutable data structure.
     */
    private static final class MutationResult<T> {
        /**
         * The root node of the data structure after the mutation.
         */
        private final SortedIntegerKeyNode<HashBucket<T>> root;

        /**
         * Either the number of elements added or removed from the collection as a result of the operation (a negative
         * number represents removed elements), or the total number of elements in the collection after the mutation.
         * The appropriate interpretation of this value is indicated by the {@link #sizeType} field.
         */
        private final int size;

        /**
         * Whether to consider the {@link #size} field to be a size adjustment or a total size.
         */
        private final SizeType sizeType;

        /**
         * Constructs a new instance of the {@link MutationResult} class.
         *
         * @param root The root node of the result.
         * @param size The size adjustment.
         */
        MutationResult(SortedIntegerKeyNode<HashBucket<T>> root, int size) {
            this(root, size, SizeType.ADJUSTMENT);
        }

        /**
         * Constructs a new instance of the {@link MutationResult} class.
         *
         * @param root The root node of the result.
         * @param size The total number of elements or a size adjustment.
         * @param sizeType The appropriate interpretation for the {@code size} parameter.
         */
        MutationResult(SortedIntegerKeyNode<HashBucket<T>> root, int size, SizeType sizeType) {
            Requires.notNull(root, "root");
            this.root = root;
            this.size = size;
            this.sizeType = sizeType;
        }

        /**
         * Gets the root node of the data structure after the mutation.
         */
        SortedIntegerKeyNode<HashBucket<T>> getRoot() {
            return root;
        }

        /**
         * Gets either the number of elements added or removed from the collection as a result of the operation (a
         * negative number represents removed elements), or the total number of elements in the collection after the
         * mutation. The appropriate interpretation of this value is indicated by the {@link #getSizeType()} method.
         */
        int size() {
            return size;
        }

        /**
         * Gets the appropriate interpretation for the {@link #size()} method; whether to be a size adjustment or total
         * size.
         */
        SizeType getSizeType() {
            return sizeType;
        }

        /**
         * Returns an immutable hash set that captures the result of this mutation.
         *
         * @param priorSet The prior version of the set. Used to capture the equality comparator and previous size, when
         * applicable.
         * @return The new collection.
         */
        ImmutableHashSet<T> finalize(ImmutableHashSet<T> priorSet) {
            Requires.notNull(priorSet, "priorSet");
            int size = this.size();
            if (this.getSizeType() == SizeType.ADJUSTMENT) {
                size += priorSet.size;
            }

            return priorSet.wrap(this.getRoot(), size);
        }
    }

    /**
     * Iterates over a sorted dictionary used for hash buckets.
     */
    private static final class NodeEnumerable<T> implements Iterable<T> {
        /**
         * The root of the sorted dictionary to enumerate.
         */
        private final SortedIntegerKeyNode<HashBucket<T>> root;

        /**
         * Constructs a new instance of the {@link NodeEnumerable} class.
         *
         * @param root The root.
         */
        NodeEnumerable(SortedIntegerKeyNode<HashBucket<T>> root) {
            Requires.notNull(root, "root");
            this.root = root;
        }

        /**
         * Returns an iterator that iterates through the collection.
         *
         * @return An {@link Iterator} that can be used to iterate through the collection.
         */
        @Override
        public IteratorImpl<T> iterator() {
            return new IteratorImpl<T>(root);
        }
    }

}
