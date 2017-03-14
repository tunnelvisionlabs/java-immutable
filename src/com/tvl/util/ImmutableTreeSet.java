// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An immutable sorted set implementation.
 *
 * @param <T> The type of elements in the set.
 */
public class ImmutableTreeSet<T> implements ImmutableSet<T>, SortKeyCollection<T>, ReadOnlyList<T> {
    /**
     * This is the factor between the small collection's size and the large collection's size in a bulk operation, under
     * which recreating the entire collection using a fast method rather than some incremental update (that requires
     * tree rebalancing) is preferable.
     */
    private static final float REFILL_OVER_INCREMENTAL_THRESHOLD = 0.15f;

    /**
     * An empty sorted set with the default sort comparer.
     */
    private static final ImmutableTreeSet<?> EMPTY = new ImmutableTreeSet<Object>();

    /**
     * The root node of the AVL tree that stores this set.
     */
    private final Node<T> _root;

    /**
     * The comparator used to sort elements in this set.
     */
    private final Comparator<? super T> _comparator;

    /**
     * Returns an empty collection.
     *
     * @param <T> The type of items stored by the collection.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet<T> create() {
        return empty();
    }

    /**
     * Returns an empty collection.
     *
     * @param <T> The type of items stored by the collection.
     * @param comparator The comparator.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet<T> create(Comparator<? super T> comparator) {
        return ImmutableTreeSet.<T>empty().withComparator(comparator);
    }

    /**
     * Creates a new immutable collection prefilled by the specified item.
     *
     * @param <T> The type of items stored by the collection.
     * @param item The item to pre-populate.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet<T> create(T item) {
        return ImmutableTreeSet.<T>empty().add(item);
    }

    /**
     * Creates a new immutable collection prefilled with the specified item.
     *
     * @param <T> The type of items stored by the collection.
     * @param comparator The comparator.
     * @param item The item to pre-populate.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet<T> create(Comparator<? super T> comparator, T item) {
        return ImmutableTreeSet.<T>empty().withComparator(comparator).add(item);
    }

    /**
     * Creates a new immutable collection prefilled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param items The items to pre-populate.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet<T> createAll(Iterable<? extends T> items) {
        return ImmutableTreeSet.<T>empty().union(items);
    }

    /**
     * Creates a new immutable collection prefilled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param comparator The comparator.
     * @param items The items to pre-populate.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet<T> createAll(Comparator<? super T> comparator, Iterable<? extends T> items) {
        return ImmutableTreeSet.<T>empty().withComparator(comparator).union(items);
    }

    /**
     * Creates a new immutable collection prefilled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param items The items to pre-populate.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet<T> create(T... items) {
        return ImmutableTreeSet.<T>empty().union(Arrays.asList(items));
    }

    /**
     * Creates a new immutable collection prefilled with the specified items.
     *
     * @param <T> The type of items stored by the collection.
     * @param comparator The comparator.
     * @param items The items to pre-populate.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet<T> create(Comparator<? super T> comparator, T... items) {
        return ImmutableTreeSet.<T>empty().withComparator(comparator).union(Arrays.asList(items));
    }

    /**
     * Returns an empty collection.
     *
     * @param <T> The type of items stored by the collection.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet.Builder<T> createBuilder() {
        return ImmutableTreeSet.<T>create().toBuilder();
    }

    /**
     * Returns an empty collection.
     *
     * @param <T> The type of items stored by the collection.
     * @param comparer The comparator.
     * @return The immutable collection.
     */
    public static <T> ImmutableTreeSet.Builder<T> createBuilder(Comparator<? super T> comparator) {
        return ImmutableTreeSet.<T>create(comparator).toBuilder();
    }

    /**
     * Constructs a new instance of the {@link ImmutableTreeSet} class.
     */
    ImmutableTreeSet() {
        this(null);
    }

    /**
     * Constructs a new instance of the {@link ImmutableTreeSet} class.
     *
     * @param comparator The comparator.
     */
    ImmutableTreeSet(Comparator<? super T> comparator) {
        _root = Node.<T>emptyNode();
        if (comparator != null) {
            _comparator = comparator;
        } else {
            _comparator = Comparators.anyComparator();
        }
    }

    /**
     * Constructs a new instance of the {@link ImmutableTreeSet} class.
     *
     * @param root The root of the AVS tree with the contents of this set.
     * @param comparator The comparator.
     */
    private ImmutableTreeSet(Node<T> root, Comparator<? super T> comparator) {
        Requires.notNull(root, "root");
        Requires.notNull(comparator, "comparator");

        root.freeze();
        _root = root;
        _comparator = comparator;
    }

    public static <T> ImmutableTreeSet<T> empty() {
        @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
        ImmutableTreeSet<T> result = (ImmutableTreeSet<T>)EMPTY;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableTreeSet<T> clear() {
//            Contract.Ensures(Contract.Result<ImmutableSortedSet<T>>() != null);
//            Contract.Ensures(Contract.Result<ImmutableSortedSet<T>>().IsEmpty);
        return _root.isEmpty() ? this : ImmutableTreeSet.<T>empty().withComparator(_comparator);
    }

    /**
     * Gets the maximum value in the collection, as defined by the comparator.
     *
     * @return The maximum value in the set.
     */
    public final T getMax() {
        return _root.getMax();
    }

    /**
     * Gets the minimum value in the collection, as defined by the comparator.
     *
     * @return The minimum value in the set.
     */
    public final T getMin() {
        return _root.getMin();
    }

//        #region IImmutableSet<T> Properties

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isEmpty() {
        return _root.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int size() {
        return _root.size();
    }

//        #endregion

//        #region ISortKeyCollection<T> Properties

    /**
     * {@inheritDoc}
     */
    @Override
    public final Comparator<? super T> getKeyComparator() {
        return _comparator;
    }

//        #endregion

    /**
     * Gets the root node (for testing purposes).
     */
    final BinaryTree<T> getRoot() {
        return _root;
    }

//        #region IReadOnlyList<T> Indexers

    /**
     * Gets the element of the set at the given index.
     *
     * @param index The 0-based index of the element in the set to return.
     * @return The element at the given position.
     */
    @Override
    public final T get(int index) {
        return _root.get(index);
    }

//        #endregion

//        #region Public methods

    /**
     * Creates a collection with the same contents as this collection that can be efficiently mutated across multiple
     * operations using standard mutable interfaces.
     *
     * <p>
     * This is an O(1) operation and results in only a single (small) memory allocation. The mutable collection that is
     * returned is <em>not</em> thread-safe.</p>
     */
    public final Builder<T> toBuilder() {
        // We must not cache the instance created here and return it to various callers.
        // Those who request a mutable collection must get references to the collection
        // that version independently of each other.
        return new Builder<T>(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableTreeSet<T> add(T value) {
        Requires.notNull(value, "value");
        //Contract.Ensures(Contract.Result<ImmutableSortedSet<T>>() != null);
        return this.wrap(_root.add(value, _comparator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableTreeSet<T> remove(T value) {
        Requires.notNull(value, "value");
        //Contract.Ensures(Contract.Result<ImmutableSortedSet<T>>() != null);
        return this.wrap(_root.remove(value, _comparator));
    }

    /**
     * Searches the set for a given value and returns the equal value it finds, if any.
     *
     * <p>
     * This can be useful when you want to reuse a previously stored reference instead of a newly constructed one (so
     * that more sharing of references can occur) or to look up a value that has more complete data than the value you
     * currently have, although their comparer functions indicate they are equal.</p>
     *
     * @param equalValue The value to search for.
     * @return The value from the set that the search found, or {@code null} if the search yielded no match.
     */
    @Override
    public final T tryGetValue(T equalValue) {
        Requires.notNull(equalValue, "equalValue");

        Node<T> searchResult = _root.search(equalValue, _comparator);
        if (searchResult.isEmpty()) {
            return null;
        } else {
            return searchResult.getKey();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableTreeSet<T> intersect(Iterable<? extends T> other) {
        Requires.notNull(other, "other");
        //Contract.Ensures(Contract.Result<ImmutableSortedSet<T>>() != null);

        ImmutableTreeSet<T> newSet = this.clear();
        for (T item : other) {
            if (this.contains(item)) {
                newSet = newSet.add(item);
            }
        }

        return newSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableTreeSet<T> except(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        Node<T> result = _root;
        for (T item : other) {
            result = result.remove(item, _comparator);
        }

        return this.wrap(result);
    }

    /**
     * Produces a set that contains elements either in this set or a given sequence, but not both.
     *
     * @param other The other sequence of items.
     * @return The new set.
     */
    @Override
    public final ImmutableTreeSet<T> symmetricExcept(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        ImmutableTreeSet<T> otherAsSet = ImmutableTreeSet.<T>empty().union(other);

        ImmutableTreeSet<T> result = this.clear();
        for (T item : this) {
            if (!otherAsSet.contains(item)) {
                result = result.add(item);
            }
        }

        for (T item : otherAsSet) {
            if (!this.contains(item)) {
                result = result.add(item);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableTreeSet<T> union(Iterable<? extends T> other) {
        Requires.notNull(other, "other");
        //Contract.Ensures(Contract.Result<ImmutableSortedSet<T>>() != null);

        ImmutableTreeSet<? extends T> immutableSortedSet = tryCastToImmutableTreeSet(other);
        if (immutableSortedSet != null && immutableSortedSet.getKeyComparator() == this.getKeyComparator()) {
            // argument is a compatible immutable sorted set
            @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
            ImmutableTreeSet<T> variant = (ImmutableTreeSet<T>)immutableSortedSet;

            if (immutableSortedSet.isEmpty()) {
                return this;
            } else if (this.isEmpty()) {
                // Adding the argument to this collection is equivalent to returning the argument.
                return variant;
            } else if (immutableSortedSet.size() > this.size()) {
                // We're adding a larger set to a smaller set, so it would be faster to simply
                // add the smaller set to the larger set.
                return variant.union(this);
            }
        }

        if (this.isEmpty()) {
            return this.leafToRootRefill(other);
        }

        Integer count = Immutables.tryGetCount(other);
        if (count != null && (this.size() + count) * REFILL_OVER_INCREMENTAL_THRESHOLD > this.size()) {
            // The payload being added is so large compared to this collection's current size
            // that we likely won't see much memory reuse in the node tree by performing an
            // incremental update.  So just recreate the entire node tree since that will
            // likely be faster.
            return this.leafToRootRefill(other);
        }

        return this.unionIncremental(other);
    }

    public final ImmutableTreeSet<T> withComparator(Comparator<? super T> comparator) {
        //Contract.Ensures(Contract.Result<ImmutableSortedSet<T>>() != null);
        if (comparator == null) {
            comparator = Comparators.anyComparator();
        }

        if (comparator == _comparator) {
            return this;
        } else {
            ImmutableTreeSet<T> result = new ImmutableTreeSet<T>(Node.<T>emptyNode(), comparator);
            result = result.union(this);
            return result;
        }
    }

    /**
     * Checks whether a given sequence of items entirely describe the contents of this set.
     *
     * @param other The sequence of items to check against this set.
     * @return A value indicating whether the sets are equal.
     */
    @Override
    public final boolean setEquals(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        if (this == other) {
            return true;
        }

        ImmutableArrayList<? extends T> otherSet = toSortedSet(other, this.getKeyComparator());
        if (this.size() != otherSet.size()) {
            return false;
        }

        int matches = 0;
        for (T item : otherSet) {
            if (!this.contains(item)) {
                return false;
            }

            matches++;
        }

        return matches == this.size();
    }

    /**
     * Determines whether the current set is a property (strict) subset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a correct subset of other; otherwise, {@code false}.
     */
    @Override
    public final boolean isProperSubsetOf(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        if (this.isEmpty()) {
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
        ImmutableArrayList<? extends T> otherSet = toSortedSet(other, this.getKeyComparator());
        if (this.size() >= otherSet.size()) {
            return false;
        }

        int matches = 0;
        boolean extraFound = false;
        for (T item : otherSet) {
            if (this.contains(item)) {
                matches++;
            } else {
                extraFound = true;
            }

            if (matches == this.size() && extraFound) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether the current set is a correct superset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a correct superset of other; otherwise, {@code false}.
     */
    @Override
    public final boolean isProperSupersetOf(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        if (this.isEmpty()) {
            return false;
        }

        int count = 0;
        for (T item : other) {
            count++;
            if (!this.contains(item)) {
                return false;
            }
        }

        return this.size() > count;
    }

    /**
     * Determines whether a set is a subset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a subset of other; otherwise, {@code false}.
     */
    @Override
    public final boolean isSubsetOf(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        if (this.isEmpty()) {
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
        ImmutableArrayList<? extends T> otherSet = toSortedSet(other, this.getKeyComparator());
        int matches = 0;
        for (T item : otherSet) {
            if (this.contains(item)) {
                matches++;
            }
        }

        return matches == this.size();
    }

    /**
     * Determines whether the current set is a superset of a specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set is a superset of other; otherwise, {@code false}.
     */
    @Override
    public final boolean isSupersetOf(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        for (T item : other) {
            if (!this.contains(item)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether the current set overlaps with the specified collection.
     *
     * @param other The collection to compare to the current set.
     * @return {@code true} if the current set and other share at least one common element; otherwise, {@code false}.
     */
    @Override
    public final boolean overlaps(Iterable<? extends T> other) {
        Requires.notNull(other, "other");

        if (this.isEmpty()) {
            return false;
        }

        for (T item : other) {
            if (this.contains(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns an {@link Iterable} that iterates over this collection in reverse order.
     *
     * @return An enumerator that iterates over the {@link ImmutableTreeSet} in reverse order.
     */
    public final Iterable<T> reverse() {
        return new ReverseIterable<T>(_root);
    }

    /**
     * Gets the position within this set that the specified value does or would appear.
     *
     * @param item The value whose position is being sought.
     * @return The index of the specified {@code item} in the sorted set, if {@code item} is found. If {@code item} is
     * not found and {@code item} is less than one or more elements in this set, a negative number which is the bitwise
     * complement of the index of the first element that is larger than value. If {@code item} is not found and
     * {@code item} is greater than any of the elements in the set, a negative number which is the bitwise complement of
     * (the index of the last element plus 1).
     */
    public final int indexOf(T item) {
        Requires.notNull(item, "item");
        return _root.indexOf(item, _comparator);
    }

//        #endregion

//        #region IImmutableSet<T> Members

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean contains(T value) {
        Requires.notNull(value, "value");
        return _root.contains(value, _comparator);
    }

//        /// <summary>
//        /// See the <see cref="IImmutableSet{T}"/> interface.
//        /// </summary>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.Clear()
//        {
//            return this.Clear();
//        }
//
//        /// <summary>
//        /// See the <see cref="IImmutableSet{T}"/> interface.
//        /// </summary>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.Add(T value)
//        {
//            return this.Add(value);
//        }
//
//        /// <summary>
//        /// See the <see cref="IImmutableSet{T}"/> interface.
//        /// </summary>
//        [ExcludeFromCodeCoverage]
//        IImmutableSet<T> IImmutableSet<T>.Remove(T value)
//        {
//            return this.Remove(value);
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
//            _root.CopyTo(array, arrayIndex);
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
//        #region IList<T> methods
//
//        /// <summary>
//        /// See the <see cref="IList{T}"/> interface.
//        /// </summary>
//        T IList<T>.this[int index]
//        {
//            get { return this[index]; }
//            set { throw new NotSupportedException(); }
//        }
//
//        /// <summary>
//        /// See the <see cref="IList{T}"/> interface.
//        /// </summary>
//        void IList<T>.Insert(int index, T item)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// See the <see cref="IList{T}"/> interface.
//        /// </summary>
//        void IList<T>.RemoveAt(int index)
//        {
//            throw new NotSupportedException();
//        }
//
//        #endregion
//
//        #region IList properties
//
//        /// <summary>
//        /// Gets a value indicating whether the <see cref="IList"/> has a fixed size.
//        /// </summary>
//        /// <returns>true if the <see cref="IList"/> has a fixed size; otherwise, false.</returns>
//        bool IList.IsFixedSize
//        {
//            get { return true; }
//        }
//
//        /// <summary>
//        /// Gets a value indicating whether the <see cref="ICollection{T}"/> is read-only.
//        /// </summary>
//        /// <returns>true if the <see cref="ICollection{T}"/> is read-only; otherwise, false.
//        ///   </returns>
//        bool IList.IsReadOnly
//        {
//            get { return true; }
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
//
//        #region IList methods
//
//        /// <summary>
//        /// Adds an item to the <see cref="IList"/>.
//        /// </summary>
//        /// <param name="value">The object to add to the <see cref="IList"/>.</param>
//        /// <returns>
//        /// The position into which the new element was inserted, or -1 to indicate that the item was not inserted into the collection,
//        /// </returns>
//        /// <exception cref="System.NotSupportedException"></exception>
//        int IList.Add(object value)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// Clears this instance.
//        /// </summary>
//        /// <exception cref="System.NotSupportedException"></exception>
//        void IList.Clear()
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// Determines whether the <see cref="IList"/> contains a specific value.
//        /// </summary>
//        /// <param name="value">The object to locate in the <see cref="IList"/>.</param>
//        /// <returns>
//        /// true if the <see cref="object"/> is found in the <see cref="IList"/>; otherwise, false.
//        /// </returns>
//        bool IList.Contains(object value)
//        {
//            return this.Contains((T)value);
//        }
//
//        /// <summary>
//        /// Determines the index of a specific item in the <see cref="IList"/>.
//        /// </summary>
//        /// <param name="value">The object to locate in the <see cref="IList"/>.</param>
//        /// <returns>
//        /// The index of <paramref name="value"/> if found in the list; otherwise, -1.
//        /// </returns>
//        int IList.IndexOf(object value)
//        {
//            return this.IndexOf((T)value);
//        }
//
//        /// <summary>
//        /// Inserts an item to the <see cref="IList"/> at the specified index.
//        /// </summary>
//        /// <param name="index">The zero-based index at which <paramref name="value"/> should be inserted.</param>
//        /// <param name="value">The object to insert into the <see cref="IList"/>.</param>
//        /// <exception cref="System.NotSupportedException"></exception>
//        void IList.Insert(int index, object value)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// Removes the first occurrence of a specific object from the <see cref="IList"/>.
//        /// </summary>
//        /// <param name="value">The object to remove from the <see cref="IList"/>.</param>
//        /// <exception cref="System.NotSupportedException"></exception>
//        void IList.Remove(object value)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// Removes at.
//        /// </summary>
//        /// <param name="index">The index.</param>
//        /// <exception cref="System.NotSupportedException"></exception>
//        void IList.RemoveAt(int index)
//        {
//            throw new NotSupportedException();
//        }
//
//        /// <summary>
//        /// Gets or sets the <see cref="System.Object"/> at the specified index.
//        /// </summary>
//        /// <value>
//        /// The <see cref="System.Object"/>.
//        /// </value>
//        /// <param name="index">The index.</param>
//        /// <exception cref="System.NotSupportedException"></exception>
//        object IList.this[int index]
//        {
//            get { return this[index]; }
//            set { throw new NotSupportedException(); }
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
//        /// <param name="index">The zero-based index in <paramref name="array"/> at which copying begins.</param>
//        void ICollection.CopyTo(Array array, int index)
//        {
//            _root.CopyTo(array, index);
//        }
//
//        #endregion
//
//        #region IEnumerable<T> Members
//
//        /// <summary>
//        /// Returns an enumerator that iterates through the collection.
//        /// </summary>
//        /// <returns>
//        /// A <see cref="IEnumerator{T}"/> that can be used to iterate through the collection.
//        /// </returns>
//        [ExcludeFromCodeCoverage]
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
//        [ExcludeFromCodeCoverage]
//        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
//        {
//            return this.GetEnumerator();
//        }
//
//        #endregion

    /**
     * Returns an iterator that iterates through the collection.
     *
     * @return A {@link Iterator} that can be used to iterate through the collection.
     */
    @Override
    public final Itr<T> iterator() {
        return _root.iterator();
    }

    /**
     * Discovers an immutable sorted set for a given value, if possible.
     */
    private static <T> ImmutableTreeSet<T> tryCastToImmutableTreeSet(Iterable<T> sequence) {
        if (sequence instanceof ImmutableTreeSet<?>) {
            return (ImmutableTreeSet<T>)sequence;
        }

        if (sequence instanceof ImmutableTreeSet.Builder<?>) {
            Builder<T> builder = (Builder<T>)sequence;
            return builder.toImmutable();
        }

        return null;
    }

    /**
     * Creates a new sorted set wrapper for a node tree.
     *
     * @param root The root of the collection.
     * @param comparator The comparator used to build the tree.
     * @return The immutable sorted set instance.
     */
    private static <T> ImmutableTreeSet<T> wrap(Node<T> root, Comparator<? super T> comparator) {
        return root.isEmpty()
            ? ImmutableTreeSet.<T>empty().withComparator(comparator)
            : new ImmutableTreeSet<T>(root, comparator);
    }

    /**
     * Adds items to this collection using the standard spine rewrite and tree rebalance technique.
     *
     * <p>This method is least demanding on memory, providing the great chance of memory reuse and does not require
     * allocating memory large enough to store all items contiguously. It's performance is optimal for additions that do
     * not significantly dwarf the existing size of this collection.</p>
     *
     * @param items The items to add.
     * @return The new collection.
     */
    private ImmutableTreeSet<T> unionIncremental(Iterable<? extends T> items) {
        Requires.notNull(items, "items");
        //Contract.Ensures(Contract.Result<ImmutableSortedSet<T>>() != null);

        // Let's not implement in terms of ImmutableSortedSet.Add so that we're
        // not unnecessarily generating a new wrapping set object for each item.
        Node<T> result = _root;
        for (T item : items) {
            result = result.add(item, _comparator);
        }

        return this.wrap(result);
    }

    /**
     * Creates a wrapping collection type around a root node.
     *
     * @param root The root node to wrap.
     * @return A wrapping collection type for the new tree.
     */
    private ImmutableTreeSet<T> wrap(Node<T> root) {
        if (root != _root) {
            return root.isEmpty() ? this.clear() : new ImmutableTreeSet<T>(root, _comparator);
        } else {
            return this;
        }
    }

    /**
     * Creates an immutable sorted set with the contents from this collection and a sequence of elements.
     *
     * @param addedItems The sequence of elements to add to this set.
     * @return The immutable sorted set.
     */
    private ImmutableTreeSet<T> leafToRootRefill(Iterable<? extends T> addedItems) {
        Requires.notNull(addedItems, "addedItems");
        //Contract.Ensures(Contract.Result<ImmutableSortedSet<T>>() != null);

        // Rather than build up the immutable structure in the incremental way,
        // build it in such a way as to generate minimal garbage, by assembling
        // the immutable binary tree from leaf to root.  This requires
        // that we know the length of the item sequence in advance, sort it, 
        // and can index into that sequence like a list, so the limited
        // garbage produced is a temporary mutable data structure we use
        // as a reference when creating the immutable one.
        // The (mutable) SortedSet<T> is much faster at constructing its collection 
        // when passed a sequence into its constructor than into its Union method.
        Iterable<T> concat = ImmutableArrayList.createAll(this).addAll(addedItems);
        ImmutableArrayList<T> sortedSet = toSortedSet(concat, _comparator);
        Node<T> root = Node.nodeTreeFromSortedSet(sortedSet);
        return this.wrap(root);
    }

    private static <T> ImmutableArrayList<T> toSortedSet(Iterable<T> items, Comparator<? super T> comparator) {
        ImmutableArrayList.Builder<T> builder = ImmutableArrayList.createBuilder();
        builder.addAll(items);
        builder.sort(comparator);

        if (builder.size() < 2) {
            return builder.toImmutable();
        }

        int j = 1;
        for (int i = 1; i < builder.size(); i++) {
            if (comparator.compare(builder.get(i - 1), builder.get(i)) != 0) {
                builder.set(j++, builder.get(i));
            } else {
                // eliminating a duplicate... don't increment j
            }
        }

        // Currently ImmutableArrayList.Builder#subList is not implemented.
        //builder.subList(j, builder.size()).clear();
        return builder.toImmutable().removeAll(j, builder.size());
    }

    private static short convertToShort(int value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException("value");
        }

        return (short)value;
    }

    /**
     * Enumerates the contents of a binary tree.
     *
     * <p>This struct can and should be kept in exact sync with the other binary tree enumerators:
     * {@link ImmutableTreeList.Itr}, {@link ImmutableTreeMap.Itr}, and {@link ImmutableTreeSet.Itr}.</p>
     */
    public static final class Itr<T> implements Iterator<T> {
//            /// <summary>
//            /// The resource pool of reusable mutable stacks for purposes of enumeration.
//            /// </summary>
//            /// <remarks>
//            /// We utilize this resource pool to make "allocation free" enumeration achievable.
//            /// </remarks>
//            private static readonly SecureObjectPool<Stack<RefAsValueType<Node>>, Enumerator> s_enumeratingStacks =
//                new SecureObjectPool<Stack<RefAsValueType<Node>>, Enumerator>();

        /**
         * The builder being iterated, if applicable.
         */
        private final Builder<T> _builder;

//            /// <summary>
//            /// A unique ID for this instance of this enumerator.
//            /// Used to protect pooled objects from use after they are recycled.
//            /// </summary>
//            private readonly int _poolUserId;

        /**
         * A flag indicating whether this iterator works in reverse sort order.
         */
        private final boolean _reverse;

        /**
         * The set being iterated.
         */
        private Node<T> _root;

        /**
         * The stack to use for iterating the binary tree.
         */
        private Deque<Node<T>> _stack;

        /**
         * The node currently selected.
         */
        private Node<T> _current;

        /**
         * The version of the builder (when applicable) that is being iterated.
         */
        private int _iteratingBuilderVersion;

        /**
         * Initializes an {@link Itr} structure.
         *
         * @param root The root of the set to be iterated.
         */
        Itr(Node<T> root) {
            this(root, null, false);
        }

        /**
         * Initializes an {@link Itr} structure.
         *
         * @param root The root of the set to be iterated.
         * @param builder The builder, if applicable.
         */
        Itr(Node<T> root, Builder<T> builder) {
            this(root, builder, false);
        }

        /**
         * Initializes an {@link Itr} structure.
         *
         * @param root The root of the set to be iterated.
         * @param builder The builder, if applicable.
         * @param reverse {@code true} to enumerate the collection in reverse.
         */
        Itr(Node<T> root, Builder<T> builder, boolean reverse) {
            Requires.notNull(root, "root");

            _root = root;
            _builder = builder;
            _current = null;
            _reverse = reverse;
            _iteratingBuilderVersion = builder != null ? builder.getVersion() : -1;
            //_poolUserId = SecureObjectPool.NewId();
            _stack = new ArrayDeque<Node<T>>();
//                if (!s_enumeratingStacks.TryTake(this, out _stack))
//                {
//                    _stack = s_enumeratingStacks.PrepNew(this, new Stack<RefAsValueType<Node>>(root.Height));
//                }

            this.pushNext(_root);
        }

//            /// <inheritdoc/>
//            int ISecurePooledObjectUser.PoolUserId
//            {
//                get { return _poolUserId; }
//            }

//            /// <summary>
//            /// The current element.
//            /// </summary>
//            public T Current
//            {
//                get
//                {
//                    this.ThrowIfDisposed();
//                    if (_current != null)
//                    {
//                        return _current.Value;
//                    }
//
//                    throw new InvalidOperationException();
//                }
//            }
//
//            /// <summary>
//            /// The current element.
//            /// </summary>
//            object System.Collections.IEnumerator.Current
//            {
//                get { return this.Current; }
//            }
//
//            /// <summary>
//            /// Disposes of this enumerator and returns the stack reference to the resource pool.
//            /// </summary>
//            public void Dispose()
//            {
//                _root = null;
//                _current = null;
//                Stack<RefAsValueType<Node>> stack;
//                if (_stack != null && _stack.TryUse(ref this, out stack))
//                {
//                    stack.ClearFastWhenEmpty();
//                    s_enumeratingStacks.TryAdd(this, _stack);
//                    _stack = null;
//                }
//            }

        /**
         *
         * @return A value indicating whether there is another element in the enumeration.
         */
        @Override
        public boolean hasNext() {
            return !_stack.isEmpty();
        }

        /**
         * Advances enumeration to the next element.
         *
         * @return The next element in the iteration.
         */
        @Override
        public final T next() {
            //this.throwIfDisposed();
            this.throwIfChanged();

            Deque<Node<T>> stack = _stack;
            if (!stack.isEmpty()) {
                Node<T> n = stack.pop();
                _current = n;
                this.pushNext(_reverse ? n.getLeft() : n.getRight());
                return _current.getKey();
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

//            /// <summary>
//            /// Restarts enumeration.
//            /// </summary>
//            public void Reset()
//            {
//                this.ThrowIfDisposed();
//
//                _enumeratingBuilderVersion = _builder != null ? _builder.Version : -1;
//                _current = null;
//                var stack = _stack.Use(ref this);
//                stack.ClearFastWhenEmpty();
//                this.PushNext(_root);
//            }
//
//            /// <summary>
//            /// Throws an <see cref="ObjectDisposedException"/> if this enumerator has been disposed.
//            /// </summary>
//            private void ThrowIfDisposed()
//            {
//                Contract.Ensures(_root != null);
//                Contract.EnsuresOnThrow<ObjectDisposedException>(_root == null);
//
//                // Since this is a struct, copies might not have been marked as disposed.
//                // But the stack we share across those copies would know.
//                // This trick only works when we have a non-null stack.
//                // For enumerators of empty collections, there isn't any natural
//                // way to know when a copy of the struct has been disposed of.
//
//                if (_root == null || (_stack != null && !_stack.IsOwned(ref this)))
//                {
//                    Validation.Requires.FailObjectDisposed(this);
//                }
//            }

        /**
         * Throws an exception if the underlying builder's contents have been changed since iteration started.
         *
         * @throws ConcurrentModificationException Thrown if the collection has changed.
         */
        private void throwIfChanged() {
            if (_builder != null && _builder.getVersion() != _iteratingBuilderVersion) {
                throw new ConcurrentModificationException("CollectionModifiedDuringEnumeration");
            }
        }

        /**
         * Pushes this node and all its left (or right, if reversed) descendants onto the stack.
         *
         * @param node The starting node to push onto the stack.
         */
        private void pushNext(Node<T> node) {
            Requires.notNull(node, "node");
            while (!node.isEmpty()) {
                _stack.push(node);
                node = _reverse ? node.getRight() : node.getLeft();
            }
        }
    }

    /**
     * A reverse iterable of a sorted set.
     */
    private static class ReverseIterable<T> implements Iterable<T> {

        /**
         * The root node to iterate.
         */
        private final Node<T> _root;

        /**
         * Constructs a new instance of the {@link ReverseIterable} class.
         *
         * @param root The root of the data structure to reverse iterate.
         */
        ReverseIterable(Node<T> root) {
            Requires.notNull(root, "root");
            _root = root;
        }

        /**
         * Returns an iterator that iterates through the collection.
         *
         * @return An {@link Iterator} that can be used to iterate through the collection.
         */
        @Override
        public final Iterator<T> iterator() {
            return _root.reverse();
        }
    }

    /**
     * A node in the AVL tree storing this set.
     */
    static final class Node<T> implements BinaryTree<T>, Iterable<T> {
        /**
         * The default empty node.
         */
        private static final Node<?> EMPTY_NODE = new Node<Object>();

        /**
         * The key associated with this node.
         */
        private final T _key;

        /**
         * A value indicating whether this node has been frozen (made immutable).
         *
         * <p>Nodes must be frozen before ever being observed by a wrapping collection type to protect collections from
         * further mutations.</p>
         */
        private boolean _frozen;

        /**
         * The depth of the tree beneath this node.
         */
        private short _height; // AVL tree max height <= ~1.44 * log2(maxNodes + 2)

        /**
         * The number of elements contained by this subtree starting at this node.
         *
         * <p>If this node would benefit from saving 4 bytes, we could have only a few nodes scattered throughout the
         * graph actually record the count of nodes beneath them. Those without the count could query their descendants,
         * which would often short-circuit when they hit a node that <em>does</em> include a count field.</p>
         */
        private int _count;

        /**
         * The left tree.
         */
        private Node<T> _left;

        /**
         * The right tree.
         */
        private Node<T> _right;

        /**
         * Constructs a new instance of the {@link ImmutableTreeSet.Node} class that is pre-frozen.
         */
        private Node() {
            //Contract.ensures(this.isEmpty());
            _key = null;

            // the empty node is *always* frozen.
            _frozen = true;
        }

        /**
         * Constructs a new instance of the {@link ImmutableTreeSet.Node} class that is not yet frozen.
         *
         * @param key The value stored by this node.
         * @param left The left branch.
         * @param right The right branch.
         * @param frozen Whether this node is pre-frozen.
         */
        private Node(T key, Node<T> left, Node<T> right) {
            this(key, left, right, false);
        }

        /**
         * Constructs a new instance of the {@link ImmutableTreeSet.Node} class.
         *
         * @param key The value stored by this node.
         * @param left The left branch.
         * @param right The right branch.
         * @param frozen Whether this node is pre-frozen.
         */
        private Node(T key, Node<T> left, Node<T> right, boolean frozen) {
            Requires.notNull(key, "key");
            Requires.notNull(left, "left");
            Requires.notNull(right, "right");
            assert !frozen || (left._frozen && right._frozen);

            _key = key;
            _left = left;
            _right = right;
            _height = convertToShort(1 + Math.max(left._height, right._height));
            _count = 1 + left._count + right._count;
            _frozen = frozen;
        }

        static <T> Node<T> emptyNode() {
            @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
            Node<T> result = (Node<T>)EMPTY_NODE;
            return result;
        }

        /**
         * Gets a value indicating whether this instance is empty.
         *
         * @return {@code true} if this instance is empty; otherwise, {@code false}.
         */
        @Override
        public final boolean isEmpty() {
            return _left == null;
        }

        /**
         * Gets the height of the tree beneath this node.
         */
        @Override
        public final int getHeight() {
            return _height;
        }

        /**
         * Gets the left branch of this node.
         */
        @Override
        public final Node<T> getLeft() {
            return _left;
        }

        /**
         * Gets the right branch of this node.
         */
        @Override
        public final Node<T> getRight() {
            return _right;
        }

        /**
         * Gets the value represented by the current node.
         */
        @Override
        public final T getValue() {
            return _key;
        }

        /**
         * Gets the number of elements contained by this subtree starting at this node.
         */
        @Override
        public final int size() {
            return _count;
        }

        /**
         * Gets the key.
         */
        final T getKey() {
            return _key;
        }

        /**
         * Gets the maximum value in the collection, as defined by the comparator.
         *
         * @return The maximum value in the set.
         */
        final T getMax() {
            if (this.isEmpty()) {
                return null;
            }

            Node<T> n = this;
            while (!n._right.isEmpty()) {
                n = n._right;
            }

            return n._key;
        }

        /**
         * Gets the minimum value in the collection, as defined by the comparator.
         *
         * @return The minimum value in the set.
         */
        final T getMin() {
            if (this.isEmpty()) {
                return null;
            }

            Node<T> n = this;
            while (!n._left.isEmpty()) {
                n = n._left;
            }

            return n._key;
        }

        /**
         * Gets the element of the set at the given index.
         *
         * @param index The 0-based index of the element in the set to return.
         * @return The element at the given position.
         */
        final T get(int index) {
            Requires.range(index >= 0 && index < this.size(), "index");

            if (index < _left._count) {
                return _left.get(index);
            }

            if (index > _left._count) {
                return _right.get(index - _left._count - 1);
            }

            return _key;
        }

//            #region IEnumerable<T> Members

        /**
         * Returns an iterator that iterates through the collection.
         *
         * @return A {@link Iterator} that can be used to iterate through the collection.
         */
        @Override
        public final Itr<T> iterator() {
            return new Itr<T>(this);
        }

//            #endregion

        /**
         * Returns an enumerator that iterates through the collection.
         *
         * @param builder The builder, if applicable.
         * @return An {@link Iterator} that can be used to iterate through the collection.
         */
        final Itr<T> iterator(Builder<T> builder) {
            return new Itr<T>(this, builder);
        }

        /**
         * Creates a node tree from an existing (mutable) collection.
         *
         * <p>The input collection must already be sorted and cannot contain duplicate elements.</p>
         *
         * @param collection The collection.
         * @param comparator The comparator.
         * @return The root of the node tree.
         */
        static <T> Node<T> nodeTreeFromSortedSet(Iterable<T> collection) {
            Requires.notNull(collection, "collection");
            //Contract.Ensures(Contract.Result<Node>() != null);

            OrderedCollection<T> ordered = ImmutableTreeList.asOrderedCollection(collection);
            if (ordered.size() == 0) {
                return emptyNode();
            }

            return nodeTreeFromList(ordered, 0, ordered.size());
        }

        final void copyTo(T[] array, int arrayIndex) {
            Requires.notNull(array, "array");
            Requires.range(arrayIndex >= 0, "arrayIndex");
            Requires.range(array.length >= arrayIndex + this.size(), "arrayIndex");

            for (T item : this) {
                array[arrayIndex++] = item;
            }
        }

        final void copyTo(Object array, int arrayIndex) {
            Requires.notNull(array, "array");
            Requires.range(arrayIndex >= 0, "arrayIndex");
            Requires.range(Array.getLength(array) >= arrayIndex + this.size(), "arrayIndex");

            for (T item : this) {
                Array.set(array, arrayIndex++, item);
            }
        }

        /**
         * Adds the specified key to the tree.
         *
         * <p>Use {@link #tryAdd(Object, Comparator)} if change detection is required.</p>
         *
         * @param key The key.
         * @param comparator The comparator.
         * @return The modified tree if the key was added; otherwise, the current node if the key was already present.
         */
        final Node<T> add(T key, Comparator<? super T> comparator) {
            Node<T> result = tryAdd(key, comparator);
            if (result == null) {
                return this;
            }

            return result;
        }

        /**
         * Adds the specified key to the tree.
         *
         * @param key The key.
         * @param comparator The comparator.
         * @return The modified tree if the key was added; otherwise, {@code null} if the key was already present.
         */
        final Node<T> tryAdd(T key, Comparator<? super T> comparator) {
            Requires.notNull(key, "key");
            Requires.notNull(comparator, "comparator");

            if (this.isEmpty()) {
                return new Node<T>(key, this, this);
            } else {
                boolean mutated = false;
                Node<T> result = this;
                int compareResult = comparator.compare(key, _key);
                if (compareResult > 0) {
                    Node<T> newRight = _right.tryAdd(key, comparator);
                    if (newRight != null) {
                        mutated = true;
                        result = this.mutateRight(newRight);
                    }
                } else if (compareResult < 0) {
                    Node<T> newLeft = _left.tryAdd(key, comparator);
                    if (newLeft != null) {
                        mutated = true;
                        result = this.mutateLeft(newLeft);
                    }
                }

                return mutated ? makeBalanced(result) : null;
            }
        }

        /**
         * Removes the specified key from the tree.
         *
         * <p>Use {@link #tryRemove(Object, Comparator)} if change detection is required.</p>
         *
         * @param key The key.
         * @param comparator The comparator.
         * @return The modified tree if the key was removed; otherwise, the current node if the key was not present.
         */
        final Node<T> remove(T key, Comparator<? super T> comparator) {
            Node<T> result = tryRemove(key, comparator);
            if (result == null) {
                return this;
            }

            return result;
        }

        /**
         * Removes the specified key from the tree.
         *
         * @param key The key.
         * @param comparator The comparator.
         * @return The modified tree if the key was removed; otherwise, {@code null} if the key was not present.
         */
        final Node<T> tryRemove(T key, Comparator<? super T> comparator) {
            Requires.notNull(key, "key");
            Requires.notNull(comparator, "comparator");

            if (this.isEmpty()) {
                return null;
            } else {
                boolean mutated;

                Node<T> result = this;
                int compare = comparator.compare(key, _key);
                if (compare == 0) {
                    // We have a match.
                    mutated = true;

                    // If this is a leaf, just remove it 
                    // by returning Empty.  If we have only one child,
                    // replace the node with the child.
                    if (_right.isEmpty() && _left.isEmpty()) {
                        result = emptyNode();
                    } else if (_right.isEmpty() && !_left.isEmpty()) {
                        result = _left;
                    } else if (!_right.isEmpty() && _left.isEmpty()) {
                        result = _right;
                    } else {
                        // We have two children. Remove the next-highest node and replace
                        // this node with it.
                        Node<T> successor = _right;
                        while (!successor._left.isEmpty()) {
                            successor = successor._left;
                        }

                        Node<T> newRight = _right.remove(successor._key, comparator);
                        result = successor.mutate(_left, newRight);
                    }
                } else if (compare < 0) {
                    Node<T> newLeft = _left.tryRemove(key, comparator);
                    mutated = newLeft != null;
                    if (mutated) {
                        result = this.mutateLeft(newLeft);
                    }
                } else {
                    Node<T> newRight = _right.tryRemove(key, comparator);
                    mutated = newRight != null;
                    if (mutated) {
                        result = this.mutateRight(newRight);
                    }
                }

                if (!mutated) {
                    return null;
                }

                return result.isEmpty() ? result : makeBalanced(result);
            }
        }

        /**
         * Determines whether the specified key is in this tree.
         *
         * @param key The key.
         * @param comparator The comparator.
         * @return {@code true} if the tree contains the specified key; otherwise, {@code false}.
         */
        final boolean contains(T key, Comparator<? super T> comparator) {
            Requires.notNull(key, "key");
            Requires.notNull(comparator, "comparator");
            return !this.search(key, comparator).isEmpty();
        }

        /**
         * Freezes this node and all descendant nodes so that any mutations require a new instance of the nodes.
         */
        final void freeze() {
            // If this node is frozen, all its descendants must already be frozen.
            if (!_frozen) {
                _left.freeze();
                _right.freeze();
                _frozen = true;
            }
        }

        /**
         * Searches for the specified key.
         *
         * @param key The key to search for.
         * @param comparator The comparator.
         * @return The matching node, or {@link #emptyNode()} if no match was found.
         */
        final Node<T> search(T key, Comparator<? super T> comparator) {
            Requires.notNull(key, "key");
            Requires.notNull(comparator, "comparer");

            if (this.isEmpty()) {
                return this;
            } else {
                int compare = comparator.compare(key, _key);
                if (compare == 0) {
                    return this;
                } else if (compare > 0) {
                    return _right.search(key, comparator);
                } else {
                    return _left.search(key, comparator);
                }
            }
        }

        /**
         * Searches for the specified key.
         *
         * @param key The key to search for.
         * @param comparer The comparator.
         * @return The index of the specified {@code key} in the sorted set, if {@code key} is found. If {@code key} is
         * not found and {@code key} is less than one or more elements in this set, a negative number which is the
         * bitwise complement of the index of the first element that is larger than value. If {@code key} is not found
         * and {@code key} is greater than any of the elements in the set, a negative number which is the bitwise
         * complement of (the index of the last element plus 1).
         */
        final int indexOf(T key, Comparator<? super T> comparator) {
            Requires.notNull(key, "key");
            Requires.notNull(comparator, "comparator");

            if (this.isEmpty()) {
                return -1;
            } else {
                int compare = comparator.compare(key, _key);
                if (compare == 0) {
                    return _left.size();
                } else if (compare > 0) {
                    int result = _right.indexOf(key, comparator);
                    boolean missing = result < 0;
                    if (missing) {
                        result = ~result;
                    }

                    result = _left.size() + 1 + result;
                    if (missing) {
                        result = ~result;
                    }

                    return result;
                } else {
                    return _left.indexOf(key, comparator);
                }
            }
        }

        /**
         * Returns an {@link Iterator} that iterates over this collection in reverse order.
         *
         * @return An iterator that iterates over the {@link ImmutableTreeSet} in reverse order.
         */
        final Iterator<T> reverse() {
            return new Itr<T>(this, null, true);
        }

//            #region Tree balancing methods

        /**
         * AVL tree rotate left operation.
         *
         * @param tree The tree.
         * @return The rotated tree.
         */
        private static <T> Node<T> rotateLeft(Node<T> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();
            //Contract.Ensures(Contract.Result<Node>() != null);

            if (tree._right.isEmpty()) {
                return tree;
            }

            Node<T> right = tree._right;
            return right.mutateLeft(tree.mutateRight(right._left));
        }

        /**
         * AVL rotate right operation.
         *
         * @param tree The tree.
         * @return The rotated tree.
         */
        private static <T> Node<T> rotateRight(Node<T> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();
            //Contract.Ensures(Contract.Result<Node>() != null);

            if (tree._left.isEmpty()) {
                return tree;
            }

            Node<T> left = tree._left;
            return left.mutateRight(tree.mutateLeft(left._right));
        }

        /**
         * AVL rotate double-left operation.
         *
         * @param tree The tree.
         * @return The rotated tree.
         */
        private static <T> Node<T> doubleLeft(Node<T> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();
            //Contract.Ensures(Contract.Result<Node>() != null);

            if (tree._right.isEmpty()) {
                return tree;
            }

            Node<T> rotatedRightChild = tree.mutateRight(rotateRight(tree._right));
            return rotateLeft(rotatedRightChild);
        }

        /**
         * AVL rotate double-right operation.
         *
         * @param tree The tree.
         * @return The rotated tree.
         */
        private static <T> Node<T> doubleRight(Node<T> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();
            //Contract.Ensures(Contract.Result<Node>() != null);

            if (tree._left.isEmpty()) {
                return tree;
            }

            Node<T> rotatedLeftChild = tree.mutateLeft(rotateLeft(tree._left));
            return rotateRight(rotatedLeftChild);
        }

        /**
         * Returns a value indicating whether the tree is in balance.
         *
         * @param tree The tree.
         * @return 0 if the tree is in balance, a positive integer if the right side is heavy, or a negative integer if
         * the left side is heavy.
         */
        private static int balance(Node<?> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();

            return tree._right._height - tree._left._height;
        }

        /**
         * Determines whether the specified tree is right heavy.
         *
         * @param tree The tree.
         * @return {@code true} if the tree is right heavy; otherwise, {@code false}.
         */
        private static boolean isRightHeavy(Node<?> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();
            return balance(tree) >= 2;
        }

        /**
         * Determines whether the specified tree is left heavy.
         */
        private static boolean isLeftHeavy(Node<?> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();
            return balance(tree) <= -2;
        }

        /**
         * Balances the specified tree.
         *
         * @param tree The tree.
         * @return A balanced tree.
         */
        private static <T> Node<T> makeBalanced(Node<T> tree) {
            Requires.notNull(tree, "tree");
            assert !tree.isEmpty();
            //Contract.Ensures(Contract.Result<Node>() != null);

            if (isRightHeavy(tree)) {
                return balance(tree._right) < 0 ? doubleLeft(tree) : rotateLeft(tree);
            }

            if (isLeftHeavy(tree)) {
                return balance(tree._left) > 0 ? doubleRight(tree) : rotateRight(tree);
            }

            return tree;
        }

//            #endregion

        /**
         * Creates a node tree that contains the contents of a list.
         *
         * @param items An indexable list with the contents that the new node tree should contain.
         * @param start The starting index within {@code items} that should be captured by the node tree.
         * @param length The number of elements from {@code items} that should be captured by the node tree.
         * @return The root of the created node tree.
         */
        private static <T> Node<T> nodeTreeFromList(OrderedCollection<T> items, int start, int length) {
            Requires.notNull(items, "items");
            assert start >= 0;
            assert length >= 0;

            if (length == 0) {
                return emptyNode();
            }

            int rightCount = (length - 1) / 2;
            int leftCount = (length - 1) - rightCount;
            Node<T> left = nodeTreeFromList(items, start, leftCount);
            Node<T> right = nodeTreeFromList(items, start + leftCount + 1, rightCount);
            return new Node<T>(items.get(start + leftCount), left, right, true);
        }

        /**
         * Creates a node mutation, either by mutating this node (if not yet frozen) or by creating a clone of this node
         * with the described changes.
         *
         * @param left The left branch of the mutated node.
         * @return The mutated (or created) node.
         */
        private Node<T> mutateLeft(Node<T> left) {
            return mutate(left, null);
        }

        /**
         * Creates a node mutation, either by mutating this node (if not yet frozen) or by creating a clone of this node
         * with the described changes.
         *
         * @param right The right branch of the mutated node.
         * @return The mutated (or created) node.
         */
        private Node<T> mutateRight(Node<T> right) {
            return mutate(null, right);
        }

        /**
         * Creates a node mutation, either by mutating this node (if not yet frozen) or by creating a clone of this node
         * with the described changes.
         *
         * @param left The left branch of the mutated node.
         * @param right The right branch of the mutated node.
         * @return The mutated (or created) node.
         */
        private Node<T> mutate(Node<T> left, Node<T> right) {
            if (_frozen) {
                return new Node<T>(_key, left != null ? left : _left, right != null ? right : _right);
            } else {
                if (left != null) {
                    _left = left;
                }

                if (right != null) {
                    _right = right;
                }

                _height = convertToShort(1 + Math.max(_left._height, _right._height));
                _count = 1 + _left._count + _right._count;
                return this;
            }
        }
    }

    /**
     * A sorted set that mutates with little or no memory allocations, can produce and/or build on immutable sorted set
     * instances very efficiently.
     *
     * <p>While {@link ImmutableTreeSet#union} and other bulk change methods already provide fast bulk change operations
     * on the collection, this class allows multiple combinations of changes to be made to a set with equal
     * efficiency.</p>
     *
     * <p>Instance members of this class are <em>not</em> thread-safe.</p>
     */
    public static final class Builder<T> implements SortKeyCollection<T>, ReadOnlyCollection<T>, Set<T> {
        /**
         * The root of the binary tree that stores the collection. Contents are typically not entirely frozen.
         */
        private ImmutableTreeSet.Node<T> _root = ImmutableTreeSet.Node.<T>emptyNode();

        /**
         * The comparator to use for sorting the set.
         */
        private Comparator<? super T> _comparator = Comparators.anyComparator();

        /**
         * Caches an immutable instance that represents the current state of the collection.
         *
         * <p>{@code null} if no immutable view has been created for the current version.</p>
         */
        private ImmutableTreeSet<T> _immutable;

        /**
         * A number that increments every time the builder changes its contents.
         */
        private int _version;

        /**
         * The object callers may use to synchronize access to this collection.
         */
        private Object _syncRoot;

        /**
         * Constructs a new instance of the {@link Builder} class.
         *
         * @param set A set to act as the basis for a new set.
         */
        Builder(ImmutableTreeSet<T> set) {
            Requires.notNull(set, "set");
            _root = set._root;
            _comparator = set.getKeyComparator();
            _immutable = set;
        }

//            #region ISet<T> Properties

        @Override
        public final boolean isEmpty() {
            return this.getRoot().isEmpty();
        }

        /**
         * Gets the number of elements in this set.
         */
        @Override
        public final int size() {
            return this.getRoot().size();
        }

//            /// <summary>
//            /// Gets a value indicating whether this instance is read-only.
//            /// </summary>
//            /// <value>Always <c>false</c>.</value>
//            bool ICollection<T>.IsReadOnly
//            {
//                get { return false; }
//            }
//
//            #endregion

        /**
         * Gets the element of the set at the given index.
         *
         * <p>No {@code set} method is offered because the element being replaced may not sort to the same position in
         * the sorted collection as the replacing element.</p>
         *
         * @param index The 0-based index of the element in the set to return.
         * @return The element at the given position.
         */
        public final T get(int index) {
            return _root.get(index);
        }

        /**
         * Gets the maximum value in the collection, as defined by the comparator.
         *
         * @return The maximum value in the set.
         */
        public final T getMax() {
            return _root.getMax();
        }

        /**
         * Gets the minimum value in the collection, as defined by the comparator.
         *
         * @return The minimum value in the set.
         */
        public final T getMin() {
            return _root.getMin();
        }

        /**
         * Gets the {@link Comparator} object that is used to determine equality for the values in the
         * {@link ImmutableTreeSet}.
         *
         * @return The comparator that is used to determine equality for the values in the set.
         */
        @Override
        public final Comparator<? super T> getKeyComparator() {
            return _comparator;
        }

        /**
         * Sets the {@link Comparator} object that is used to determine equality for the values in the
         * {@link ImmutableTreeSet}.
         *
         * <p>
         * When changing the comparator in such a way as would introduce collisions, the conflicting elements are
         * dropped, leaving only one of each matching pair in the collection.</p>
         *
         * @param value The comparator that is used to determine equality for the values in the set.
         */
        public final void setKeyComparator(Comparator<T> value) {
            Requires.notNull(value, "value");

            if (value != _comparator) {
                Node<T> newRoot = Node.emptyNode();
                for (T item : this) {
                    newRoot = newRoot.add(item, value);
                }

                _immutable = null;
                _comparator = value;
                this.setRoot(newRoot);
            }
        }

        /**
         * Gets the current version of the contents of this builder.
         */
        final int getVersion() {
            return _version;
        }

        /**
         * Gets the root node that represents the data in this collection.
         */
        private Node<T> getRoot() {
            return _root;
        }

        /**
         * Sets the root node that represents the data in this collection.
         */
        private void setRoot(Node<T> value) {
            // We *always* increment the version number because some mutations
            // may not create a new value of root, although the existing root
            // instance may have mutated.
            _version++;

            if (_root != value) {
                _root = value;

                // Clear any cached value for the immutable view since it is now invalidated.
                _immutable = null;
            }
        }

//            #region ISet<T> Methods

        /**
         * Adds an element to the current set and returns a value to indicate if the element was successfully added.
         *
         * @param item The element to add to the set.
         * @return {@code true} if the element is added to the set; {@code false} if the element is already in the set.
         */
        @Override
        public final boolean add(T item) {
            Node<T> newRoot = this.getRoot().tryAdd(item, _comparator);
            if (newRoot == null) {
                return false;
            }

            this.setRoot(newRoot);
            return true;
        }

        /**
         * Removes all elements in the specified collection from the current set.
         *
         * @param other The collection of items to remove from the set.
         */
        public final void exceptWith(Iterable<? extends T> other) {
            Requires.notNull(other, "other");

            for (T item : other) {
                this.setRoot(this.getRoot().remove(item, _comparator));
            }
        }

        @Override
        public final boolean removeAll(Collection<?> c) {
            boolean changed = false;
            for (Object item : c) {
                @SuppressWarnings("unchecked")
                T typedItem = (T)item;
                changed |= remove(typedItem);
            }

            return changed;
        }

        /**
         * Modifies the current set so that it contains only elements that are also in a specified collection.
         *
         * @param other The collection to compare to the current set.
         */
        public final void intersectWith(Iterable<? extends T> other) {
            Requires.notNull(other, "other");

            Node<T> result = Node.emptyNode();
            for (T item : other) {
                if (this.contains(item)) {
                    result = result.add(item, _comparator);
                }
            }

            this.setRoot(result);
        }

        @Override
        public final boolean retainAll(Collection<?> c) {
            int version = getVersion();
            @SuppressWarnings("unchecked")
            Iterable<? extends T> typedOther = (Iterable<? extends T>)c;
            intersectWith(typedOther);
            return getVersion() != version;
        }

        /**
         * Determines whether the current set is a proper (strict) subset of a specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is a correct subset of {@code other}; otherwise, {@code false}.
         */
        public final boolean isProperSubsetOf(Iterable<? extends T> other) {
            return this.toImmutable().isProperSubsetOf(other);
        }

        /**
         * Determines whether the current set is a proper (strict) superset of a specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is a superset of {@code other}; otherwise, {@code false}.
         */
        public final boolean isProperSupersetOf(Iterable<? extends T> other) {
            return this.toImmutable().isProperSupersetOf(other);
        }

        /**
         * Determines whether the current set is a subset of a specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is a subset of {@code other}; otherwise, {@code false}.
         */
        public final boolean isSubsetOf(Iterable<? extends T> other) {
            return this.toImmutable().isSubsetOf(other);
        }

        /**
         * Determines whether the current set is a superset of a specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is a superset of {@code other}; otherwise, {@code false}.
         */
        public final boolean isSupersetOf(Iterable<? extends T> other) {
            return this.toImmutable().isSupersetOf(other);
        }

        @Override
        public final boolean containsAll(Collection<?> c) {
            @SuppressWarnings("unchecked")
            Iterable<? extends T> typedOther = (Iterable<? extends T>)c;
            return isSupersetOf(typedOther);
        }

        /**
         * Determines whether the current set overlaps with the specified collection.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set and {@code other} share at least one common element; otherwise,
         * {@code false}.
         */
        public final boolean overlaps(Iterable<? extends T> other) {
            return this.toImmutable().overlaps(other);
        }

        /**
         * Determines whether the current set and the specified collection contain the same elements.
         *
         * @param other The collection to compare to the current set.
         * @return {@code true} if the current set is equal to {@code other}; otherwise, {@code false}.
         */
        public final boolean setEquals(Iterable<? extends T> other) {
            return this.toImmutable().setEquals(other);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (!(obj instanceof Set<?>)) {
                return false;
            }

            Set<?> other = (Set<?>)obj;
            Set<? extends T> downcast = (Set<? extends T>)other;
            return setEquals(downcast);
        }

        @Override
        public int hashCode() {
            int result = 0;
            for (T item : this) {
                if (item != null) {
                    result += item.hashCode();
                }
            }

            return result;
        }

        /**
         * Modifies the current set so that it contains only elements that are present either in the current set or in
         * the specified collection, but not both.
         *
         * @param other The collection to compare to the current set.
         */
        public final void symmetricExceptWith(Iterable<? extends T> other) {
            this.setRoot(this.toImmutable().symmetricExcept(other)._root);
        }

        /**
         * Modifies the current set so that it contains all elements that are present in both the current set and in the
         * specified collection.
         *
         * @param other The collection to compare to the current set.
         */
        public final void unionWith(Iterable<? extends T> other) {
            Requires.notNull(other, "other");

            for (T item : other) {
                this.setRoot(this.getRoot().add(item, _comparator));
            }
        }

        @Override
        public final boolean addAll(Collection<? extends T> c) {
            int version = getVersion();
            unionWith(c);
            return getVersion() > version;
        }

//            /// <summary>
//            /// Adds an element to the current set and returns a value to indicate if the
//            /// element was successfully added.
//            /// </summary>
//            /// <param name="item">The element to add to the set.</param>
//            void ICollection<T>.Add(T item)
//            {
//                this.Add(item);
//            }

        /**
         * Removes all elements from this set.
         */
        @Override
        public final void clear() {
            this.setRoot(Node.<T>emptyNode());
        }

        /**
         * Determines whether the set contains a specific value.
         *
         * @param item The object to locate in the set.
         * @return {@code true} if item is found in the set; otherwise, {@code false}.
         */
        @Override
        public final boolean contains(Object item) {
            @SuppressWarnings("unchecked")
            T typedItem = (T)item;
            return this.getRoot().contains(typedItem, _comparator);
        }

//            /// <summary>
//            /// See <see cref="ICollection{T}"/>
//            /// </summary>
//            void ICollection<T>.CopyTo(T[] array, int arrayIndex)
//            {
//                _root.CopyTo(array, arrayIndex);
//            }

        @Override
        public final Object[] toArray() {
            return toArray(new Object[size()]);
        }

        @Override
        public final <U> U[] toArray(U[] a) {
            if (a.length < size()) {
                @SuppressWarnings(Suppressions.UNCHECKED_SAFE)
                U[] resized = (U[])Array.newInstance(a.getClass().getComponentType(), size());
                a = resized;
            }

            _root.copyTo(a, 0);
            return a;
        }

        /**
         * Removes the first occurrence of a specific object from the set.
         *
         * @param item The object to remove from the set.
         * @return {@code true} if the item was removed from the set; {@code false} if the item was not found in the
         * set.
         */
        @Override
        public final boolean remove(Object item) {
            @SuppressWarnings("unchecked")
            T typedItem = (T)item;
            Node<T> newRoot = this.getRoot().tryRemove(typedItem, _comparator);
            if (newRoot == null) {
                return false;
            }

            this.setRoot(newRoot);
            return true;
        }

        /**
         * Returns an iterator that iterates through the collection.
         *
         * @return A iterator that can be used to iterate through the collection.
         */
        @Override
        public final Itr<T> iterator() {
            return this.getRoot().iterator(this);
        }

//            /// <summary>
//            /// Returns an enumerator that iterates through the collection.
//            /// </summary>
//            /// <returns>A enumerator that can be used to iterate through the collection.</returns>
//            IEnumerator<T> IEnumerable<T>.GetEnumerator()
//            {
//                return this.Root.GetEnumerator();
//            }
//
//            /// <summary>
//            /// Returns an enumerator that iterates through the collection.
//            /// </summary>
//            /// <returns>A enumerator that can be used to iterate through the collection.</returns>
//            IEnumerator IEnumerable.GetEnumerator()
//            {
//                return this.GetEnumerator();
//            }
//
//            #endregion

        /**
         * Returns an {@link Iterable} that iterates over this collection in reverse order.
         *
         * @return An iterator that iterates over the {@link Builder} in reverse order.
         */
        public final Iterable<T> reverse() {
            return new ReverseIterable<T>(_root);
        }

        /**
         * Creates an immutable sorted set based on the contents of this instance.
         *
         * <p>This method is an O(n) operation, and approaches O(1) time as the number of actual mutations to the set
         * since the last call to this method approaches 0.</p>
         *
         * @return An immutable set.
         */
        public final ImmutableTreeSet<T> toImmutable() {
            // Creating an instance of ImmutableSortedSet<T> with our root node automatically freezes our tree,
            // ensuring that the returned instance is immutable.  Any further mutations made to this builder
            // will clone (and unfreeze) the spine of modified nodes until the next time this method is invoked.
            if (_immutable == null) {
                _immutable = ImmutableTreeSet.<T>wrap(this.getRoot(), _comparator);
            }

            return _immutable;
        }

//            #region ICollection members
//
//            /// <summary>
//            /// Copies the elements of the <see cref="ICollection"/> to an <see cref="Array"/>, starting at a particular <see cref="Array"/> index.
//            /// </summary>
//            /// <param name="array">The one-dimensional <see cref="Array"/> that is the destination of the elements copied from <see cref="ICollection"/>. The <see cref="Array"/> must have zero-based indexing.</param>
//            /// <param name="arrayIndex">The zero-based index in <paramref name="array"/> at which copying begins.</param>
//            /// <exception cref="System.NotImplementedException"></exception>
//            void ICollection.CopyTo(Array array, int arrayIndex)
//            {
//                this.Root.CopyTo(array, arrayIndex);
//            }
//
//            /// <summary>
//            /// Gets a value indicating whether access to the <see cref="ICollection"/> is synchronized (thread safe).
//            /// </summary>
//            /// <returns>true if access to the <see cref="ICollection"/> is synchronized (thread safe); otherwise, false.</returns>
//            /// <exception cref="System.NotImplementedException"></exception>
//            [DebuggerBrowsable(DebuggerBrowsableState.Never)]
//            bool ICollection.IsSynchronized
//            {
//                get { return false; }
//            }
//
//            /// <summary>
//            /// Gets an object that can be used to synchronize access to the <see cref="ICollection"/>.
//            /// </summary>
//            /// <returns>An object that can be used to synchronize access to the <see cref="ICollection"/>.</returns>
//            /// <exception cref="System.NotImplementedException"></exception>
//            [DebuggerBrowsable(DebuggerBrowsableState.Never)]
//            object ICollection.SyncRoot
//            {
//                get
//                {
//                    if (_syncRoot == null)
//                    {
//                        Threading.Interlocked.CompareExchange<Object>(ref _syncRoot, new Object(), null);
//                    }
//
//                    return _syncRoot;
//                }
//            }
//            #endregion
    }

}
