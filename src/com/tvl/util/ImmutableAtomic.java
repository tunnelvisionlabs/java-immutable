// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.tvl.util.function.BiFunction;
import com.tvl.util.function.Function;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Contains atomic exchange mechanisms for immutable collections.
 */
public enum ImmutableAtomic {
    ;

    /**
     * Mutates a value in-place with optimistic locking transaction semantics via a specified transformation function.
     * The transformation is retried as many times as necessary to win the optimistic locking race.
     *
     * @param <T> The type of data.
     * @param location The variable or field to be changed, which may be accessed by multiple threads.
     * @param transformer A function that mutates the value. This function should be side-effect free, as it may run
     * multiple times when races occur with other threads.
     * @return {@code true} if the location's value is changed by applying the result of the {@code transformer}
     * function; {@code false} if the location's value remained the same because the last invocation of
     * {@code transformer} returned the existing value.
     */
    public static <T> boolean update(@Nonnull AtomicReference<T> location, @Nonnull Function<? super T, ? extends T> transformer) {
        Requires.notNull(location, "location");
        Requires.notNull(transformer, "transformer");

        boolean successful;
        do {
            T oldValue = location.get();
            T newValue = transformer.apply(oldValue);
            if (oldValue == newValue) {
                // No change was actually required.
                return false;
            }

            successful = location.compareAndSet(oldValue, newValue);
        } while (!successful);

        return true;
    }

    /**
     * Mutates a value in-place with optimistic locking transaction semantics via a specified transformation function.
     * The transformation is retried as many times as necessary to win the optimistic locking race.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <T> The type of data.
     * @param updater The updater for the field to be changed, which may be accessed by multiple threads.
     * @param obj The object whose field to update.
     * @param transformer A function that mutates the value. This function should be side-effect free, as it may run
     * multiple times when races occur with other threads.
     * @return {@code true} if the location's value is changed by applying the result of the {@code transformer}
     * function; {@code false} if the location's value remained the same because the last invocation of
     * {@code transformer} returned the existing value.
     */
    public static <C, T> boolean update(@Nonnull AtomicReferenceFieldUpdater<? super C, T> updater, @Nonnull C obj, @Nonnull Function<? super T, ? extends T> transformer) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");
        Requires.notNull(transformer, "transformer");

        boolean successful;
        do {
            T oldValue = updater.get(obj);
            T newValue = transformer.apply(oldValue);
            if (oldValue == newValue) {
                // No change was actually required.
                return false;
            }

            successful = updater.compareAndSet(obj, oldValue, newValue);
        } while (!successful);

        return true;
    }

    /**
     * Mutates a value in-place with optimistic locking transaction semantics via a specified transformation function.
     * The transformation is retried as many times as necessary to win the optimistic locking race.
     *
     * @param <T> The type of data.
     * @param <State> The type of argument passed to the {@code transformer}.
     * @param location The variable or field to be changed, which may be accessed by multiple threads.
     * @param transformer A function that mutates the value. This function should be side-effect free, as it may run
     * multiple times when races occur with other threads.
     * @param transformerArgument The argument to pass to {@code transformer}.
     * @return {@code true} if the location's value is changed by applying the result of the {@code transformer}
     * function; {@code false} if the location's value remained the same because the last invocation of
     * {@code transformer} returned the existing value.
     */
    public static <T, State> boolean update(@Nonnull AtomicReference<T> location, @Nonnull BiFunction<? super T, ? super State, ? extends T> transformer, State transformerArgument) {
        Requires.notNull(transformer, "transformer");

        boolean successful;
        do {
            T oldValue = location.get();
            T newValue = transformer.apply(oldValue, transformerArgument);
            if (oldValue == newValue) {
                // No change was actually required.
                return false;
            }

            successful = location.compareAndSet(oldValue, newValue);
        } while (!successful);

        return true;
    }

    /**
     * Mutates a value in-place with optimistic locking transaction semantics via a specified transformation function.
     * The transformation is retried as many times as necessary to win the optimistic locking race.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <T> The type of data.
     * @param <State> The type of argument passed to the {@code transformer}.
     * @param updater The updater for the field to be changed, which may be accessed by multiple threads.
     * @param obj The object whose field to update.
     * @param transformer A function that mutates the value. This function should be side-effect free, as it may run
     * multiple times when races occur with other threads.
     * @param transformerArgument The argument to pass to {@code transformer}.
     * @return {@code true} if the location's value is changed by applying the result of the {@code transformer}
     * function; {@code false} if the location's value remained the same because the last invocation of
     * {@code transformer} returned the existing value.
     */
    public static <C, T, State> boolean update(@Nonnull AtomicReferenceFieldUpdater<? super C, T> updater, @Nonnull C obj, @Nonnull BiFunction<? super T, ? super State, ? extends T> transformer, State transformerArgument) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");
        Requires.notNull(transformer, "transformer");

        boolean successful;
        do {
            T oldValue = updater.get(obj);
            T newValue = transformer.apply(oldValue, transformerArgument);
            if (oldValue == newValue) {
                // No change was actually required.
                return false;
            }

            successful = updater.compareAndSet(obj, oldValue, newValue);
        } while (!successful);

        return true;
    }

    /**
     * Obtains the value for the specified key from a dictionary, or adds a new value to the dictionary where the key
     * did not previously exist.
     *
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param <State> The type of argument supplied to the value factory.
     * @param location The variable or field to atomically update if the specified {@code key} is not in the dictionary.
     * @param key The key for the value to retrieve or add.
     * @param valueFactory The function to execute to obtain the value to insert into the dictionary if the key is not
     * found.
     * @param factoryArgument The argument to pass to the value factory.
     * @return The value obtained from the dictionary or {@code valueFactory} if it was not present.
     */
    public static <K, V, State> V getOrAdd(@Nonnull AtomicReference<ImmutableHashMap<K, V>> location, K key, @Nonnull BiFunction<? super K, ? super State, ? extends V> valueFactory, State factoryArgument) {
        Requires.notNull(valueFactory, "valueFactory");

        ImmutableHashMap<K, V> map = location.get();
        Requires.notNull(map, "location");

        V value = map.get(key);
        if (value != null) {
            return value;
        }

        value = valueFactory.apply(key, factoryArgument);
        return getOrAdd(location, key, value);
    }

    /**
     * Obtains the value for the specified key from a dictionary, or adds a new value to the dictionary where the key
     * did not previously exist.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param <State> The type of argument supplied to the value factory.
     * @param updater The updater for the field to atomically update if the specified {@code key} is not in the
     * dictionary.
     * @param obj The object whose field to update.
     * @param key The key for the value to retrieve or add.
     * @param valueFactory The function to execute to obtain the value to insert into the dictionary if the key is not
     * found.
     * @param factoryArgument The argument to pass to the value factory.
     * @return The value obtained from the dictionary or {@code valueFactory} if it was not present.
     */
    public static <C, K, V, State> V getOrAdd(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableHashMap<K, V>> updater, @Nonnull C obj, K key, @Nonnull BiFunction<? super K, ? super State, ? extends V> valueFactory, State factoryArgument) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");
        Requires.notNull(valueFactory, "valueFactory");

        ImmutableHashMap<K, V> map = updater.get(obj);
        Requires.notNull(map, "updater");

        V value = map.get(key);
        if (value != null) {
            return value;
        }

        value = valueFactory.apply(key, factoryArgument);
        return getOrAdd(updater, obj, key, value);
    }

    /**
     * Obtains the value for the specified key from a dictionary, or adds a new value to the dictionary where the key
     * did not previously exist.
     *
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param location The variable or field to atomically update if the specified {@code key} is not in the dictionary.
     * @param key The key for the value to retrieve or add.
     * @param valueFactory The function to execute to obtain the value to insert into the dictionary if the key is not
     * found. This delegate will not be invoked more than once.
     * @return The value obtained from the dictionary or {@code valueFactory} if it was not present.
     */
    public static <K, V> V getOrAdd(@Nonnull AtomicReference<ImmutableHashMap<K, V>> location, K key, @Nonnull Function<? super K, ? extends V> valueFactory) {
        Requires.notNull(valueFactory, "valueFactory");

        ImmutableHashMap<K, V> map = location.get();
        Requires.notNull(map, "location");

        V value = map.get(key);
        if (value != null) {
            return value;
        }

        value = valueFactory.apply(key);
        return getOrAdd(location, key, value);
    }

    /**
     * Obtains the value for the specified key from a dictionary, or adds a new value to the dictionary where the key
     * did not previously exist.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param updater The updater for the field to atomically update if the specified {@code key} is not in the dictionary.
     * @param obj The object whose field to update.
     * @param key The key for the value to retrieve or add.
     * @param valueFactory The function to execute to obtain the value to insert into the dictionary if the key is not
     * found. This delegate will not be invoked more than once.
     * @return The value obtained from the dictionary or {@code valueFactory} if it was not present.
     */
    public static <C, K, V> V getOrAdd(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableHashMap<K, V>> updater, @Nonnull C obj, K key, @Nonnull Function<? super K, ? extends V> valueFactory) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");
        Requires.notNull(valueFactory, "valueFactory");

        ImmutableHashMap<K, V> map = updater.get(obj);
        Requires.notNull(map, "updater");

        V value = map.get(key);
        if (value != null) {
            return value;
        }

        value = valueFactory.apply(key);
        return getOrAdd(updater, obj, key, value);
    }

    /**
     * Obtains the value for the specified key from a dictionary, or adds a new value to the dictionary where the key
     * did not previously exist.
     *
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param location The variable or field to atomically update if the specified {@code key} is not in the dictionary.
     * @param key The key for the value to retrieve or add.
     * @param value The value to add to the dictionary if one is not already present.
     * @return The value obtained from the dictionary or {@code value} if it was not present.
     */
    public static <K, V> V getOrAdd(@Nonnull AtomicReference<ImmutableHashMap<K, V>> location, K key, V value) {
        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            // Need to use containsKey to avoid infinite loop for map with null values
            if (priorCollection.containsKey(key)) {
                return priorCollection.get(key);
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.add(key, value);
            successful = location.compareAndSet(priorCollection, updatedCollection);
        } while (!successful);

        // We won the race-condition and have updated the collection.
        // Return the value that is in the collection (as of the Interlocked operation).
        return value;
    }

    /**
     * Obtains the value for the specified key from a dictionary, or adds a new value to the dictionary where the key
     * did not previously exist.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param updater The updater for the field to atomically update if the specified {@code key} is not in the
     * dictionary.
     * @param obj The object whose field to update.
     * @param key The key for the value to retrieve or add.
     * @param value The value to add to the dictionary if one is not already present.
     * @return The value obtained from the dictionary or {@code value} if it was not present.
     */
    public static <C, K, V> V getOrAdd(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableHashMap<K, V>> updater, @Nonnull C obj, K key, V value) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");

        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            // Need to use containsKey to avoid infinite loop for map with null values
            if (priorCollection.containsKey(key)) {
                return priorCollection.get(key);
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.add(key, value);
            successful = updater.compareAndSet(obj, priorCollection, updatedCollection);
        } while (!successful);

        // We won the race-condition and have updated the collection.
        // Return the value that is in the collection (as of the Interlocked operation).
        return value;
    }

    /**
     * Obtains the value from a dictionary after having added it or updated an existing entry.
     *
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param location The variable or field to atomically update if the specified {@code key} is not in the dictionary.
     * @param key The key for the value to add or update.
     * @param addValueFactory The function that receives the key and returns a new value to add to the dictionary when
     * no value previously exists.
     * @param updateValueFactory The function that receives the key and prior value and returns the new value with which
     * to update the dictionary.
     * @return The added or updated value.
     */
    public static <K, V> V addOrUpdate(@Nonnull AtomicReference<ImmutableHashMap<K, V>> location, K key, @Nonnull Function<? super K, ? extends V> addValueFactory, @Nonnull BiFunction<? super K, ? super V, ? extends V> updateValueFactory) {
        Requires.notNull(addValueFactory, "addValueFactory");
        Requires.notNull(updateValueFactory, "updateValueFactory");

        V newValue;
        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            if (priorCollection.containsKey(key)) {
                newValue = updateValueFactory.apply(key, priorCollection.get(key));
            } else {
                newValue = addValueFactory.apply(key);
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.put(key, newValue);
            successful = location.compareAndSet(priorCollection, updatedCollection);
        } while (!successful);

        // We won the race-condition and have updated the collection.
        // Return the value that is in the collection (as of the Interlocked operation).
        return newValue;
    }

    /**
     * Obtains the value from a dictionary after having added it or updated an existing entry.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param updater The updater for the field to atomically update if the specified {@code key} is not in the
     * dictionary.
     * @param obj The object whose field to update.
     * @param key The key for the value to add or update.
     * @param addValueFactory The function that receives the key and returns a new value to add to the dictionary when
     * no value previously exists.
     * @param updateValueFactory The function that receives the key and prior value and returns the new value with which
     * to update the dictionary.
     * @return The added or updated value.
     */
    public static <C, K, V> V addOrUpdate(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableHashMap<K, V>> updater, @Nonnull C obj, K key, @Nonnull Function<? super K, ? extends V> addValueFactory, @Nonnull BiFunction<? super K, ? super V, ? extends V> updateValueFactory) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");
        Requires.notNull(addValueFactory, "addValueFactory");
        Requires.notNull(updateValueFactory, "updateValueFactory");

        V newValue;
        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            if (priorCollection.containsKey(key)) {
                newValue = updateValueFactory.apply(key, priorCollection.get(key));
            } else {
                newValue = addValueFactory.apply(key);
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.put(key, newValue);
            successful = updater.compareAndSet(obj, priorCollection, updatedCollection);
        } while (!successful);

        // We won the race-condition and have updated the collection.
        // Return the value that is in the collection (as of the Interlocked operation).
        return newValue;
    }

    /**
     * Obtains the value from a dictionary after having added it or updated an existing entry.
     *
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param location The variable or field to atomically update if the specified {@code key} is not in the dictionary.
     * @param key The key for the value to add or update.
     * @param addValue The value to use if no previous value exists.
     * @param updateValueFactory The function that receives the key and prior value and returns the new value with which
     * to update the dictionary.
     * @return The added or updated value.
     */
    public static <K, V> V addOrUpdate(@Nonnull AtomicReference<ImmutableHashMap<K, V>> location, K key, V addValue, @Nonnull BiFunction<? super K, ? super V, ? extends V> updateValueFactory) {
        Requires.notNull(updateValueFactory, "updateValueFactory");

        V newValue;
        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            if (priorCollection.containsKey(key)) {
                newValue = updateValueFactory.apply(key, priorCollection.get(key));
            } else {
                newValue = addValue;
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.put(key, newValue);
            successful = location.compareAndSet(priorCollection, updatedCollection);
        } while (!successful);

        // We won the race-condition and have updated the collection.
        // Return the value that is in the collection (as of the Interlocked operation).
        return newValue;
    }

    /**
     * Obtains the value from a dictionary after having added it or updated an existing entry.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param updater The updater for the field to atomically update if the specified {@code key} is not in the
     * dictionary.
     * @param obj The object whose field to update.
     * @param key The key for the value to add or update.
     * @param addValue The value to use if no previous value exists.
     * @param updateValueFactory The function that receives the key and prior value and returns the new value with which
     * to update the dictionary.
     * @return The added or updated value.
     */
    public static <C, K, V> V addOrUpdate(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableHashMap<K, V>> updater, @Nonnull C obj, K key, V addValue, @Nonnull BiFunction<? super K, ? super V, ? extends V> updateValueFactory) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");
        Requires.notNull(updateValueFactory, "updateValueFactory");

        V newValue;
        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            if (priorCollection.containsKey(key)) {
                newValue = updateValueFactory.apply(key, priorCollection.get(key));
            } else {
                newValue = addValue;
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.put(key, newValue);
            successful = updater.compareAndSet(obj, priorCollection, updatedCollection);
        } while (!successful);

        // We won the race-condition and have updated the collection.
        // Return the value that is in the collection (as of the Interlocked operation).
        return newValue;
    }

    /**
     * Adds the specified key and value to the dictionary if no colliding key already exists in the dictionary.
     *
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param location The variable or field to atomically update if the specified {@code key} is not in the dictionary.
     * @param key The key to add, if is not already defined in the dictionary.
     * @param value The value to add.
     * @return {@code true} if the key was not previously set in the dictionary and the value was set; {@code false}
     * otherwise.
     */
    public static <K, V> boolean tryAdd(@Nonnull AtomicReference<ImmutableHashMap<K, V>> location, K key, V value) {
        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            if (priorCollection.containsKey(key)) {
                return false;
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.add(key, value);
            successful = location.compareAndSet(priorCollection, updatedCollection);
        } while (!successful);

        return true;
    }

    /**
     * Adds the specified key and value to the dictionary if no colliding key already exists in the dictionary.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param updater The updater for the field to atomically update if the specified {@code key} is not in the
     * dictionary.
     * @param obj The object whose field to update.
     * @param key The key to add, if is not already defined in the dictionary.
     * @param value The value to add.
     * @return {@code true} if the key was not previously set in the dictionary and the value was set; {@code false}
     * otherwise.
     */
    public static <C, K, V> boolean tryAdd(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableHashMap<K, V>> updater, @Nonnull C obj, K key, V value) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");

        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            if (priorCollection.containsKey(key)) {
                return false;
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.add(key, value);
            successful = updater.compareAndSet(obj, priorCollection, updatedCollection);
        } while (!successful);

        return true;
    }

    /**
     * Sets the specified key to the given value if the key already is set to a specific value.
     *
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param location The variable or field to atomically update if the specified {@code key} is not in the dictionary.
     * @param key The key to update.
     * @param expectValue The value that must already be set in the dictionary in order for the update to succeed.
     * @param updateValue The new value to set.
     * @return {@code true} if the key and comparison value were present in the dictionary and the update was made;
     * {@code false} otherwise.
     */
    public static <K, V> boolean tryUpdate(@Nonnull AtomicReference<ImmutableHashMap<K, V>> location, K key, V expectValue, V updateValue) {
        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            if (!priorCollection.containsKey(key)) {
                // The key isn't in the dictionary
                return false;
            }

            V priorValue = priorCollection.get(key);
            if (!EqualityComparators.defaultComparator().equals(priorValue, expectValue)) {
                // The current value doesn't match what the caller expected
                return false;
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.put(key, updateValue);
            successful = location.compareAndSet(priorCollection, updatedCollection);
        } while (!successful);

        return true;
    }

    /**
     * Sets the specified key to the given value if the key already is set to a specific value.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param updater The updater for the field to atomically update if the specified {@code key} is not in the
     * dictionary.
     * @param obj The object whose field to update.
     * @param key The key to update.
     * @param expectValue The value that must already be set in the dictionary in order for the update to succeed.
     * @param updateValue The new value to set.
     * @return {@code true} if the key and comparison value were present in the dictionary and the update was made;
     * {@code false} otherwise.
     */
    public static <C, K, V> boolean tryUpdate(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableHashMap<K, V>> updater, @Nonnull C obj, K key, V expectValue, V updateValue) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");

        boolean successful;
        do {
            ImmutableHashMap<K, V> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            if (!priorCollection.containsKey(key)) {
                // The key isn't in the dictionary
                return false;
            }

            V priorValue = priorCollection.get(key);
            if (!EqualityComparators.defaultComparator().equals(priorValue, expectValue)) {
                // The current value doesn't match what the caller expected
                return false;
            }

            ImmutableHashMap<K, V> updatedCollection = priorCollection.put(key, updateValue);
            successful = updater.compareAndSet(obj, priorCollection, updatedCollection);
        } while (!successful);

        return true;
    }

    /**
     * Removes an entry from the dictionary with the specified key if it is defined and returns its value.
     *
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param location The variable or field to atomically update if the specified {@code key} is in the dictionary.
     * @param key The key to remove.
     * @return The value from the pre-existing entry, if one exists; otherwise, {@code null}.
     */
    @Nullable
    public static <K, V> V tryRemove(@Nonnull AtomicReference<ImmutableHashMap<K, V>> location, K key) {
        while (true) {
            ImmutableHashMap<K, V> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            if (!priorCollection.containsKey(key)) {
                return null;
            }

            if (location.compareAndSet(priorCollection, priorCollection.remove(key))) {
                return priorCollection.get(key);
            }
        }
    }

    /**
     * Removes an entry from the dictionary with the specified key if it is defined and returns its value.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <K> The type of key stored by the dictionary.
     * @param <V> The type of value stored by the dictionary.
     * @param updater The updater for the field to atomically update if the specified {@code key} is in the dictionary.
     * @param obj The object whose field to update.
     * @param key The key to remove.
     * @return The value from the pre-existing entry, if one exists; otherwise, {@code null}.
     */
    @Nullable
    public static <C, K, V> V tryRemove(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableHashMap<K, V>> updater, @Nonnull C obj, K key) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");

        while (true) {
            ImmutableHashMap<K, V> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            if (!priorCollection.containsKey(key)) {
                return null;
            }

            if (updater.compareAndSet(obj, priorCollection, priorCollection.remove(key))) {
                return priorCollection.get(key);
            }
        }
    }

    /**
     * Pops a value from a stack.
     *
     * @param <T> The type of elements stored in the stack.
     * @param location The variable or field to atomically update.
     * @return The value popped from the stack, if it was non-empty; otherwise {@code null} if the stack was empty.
     */
    @Nullable
    public static <T> T tryPop(@Nonnull AtomicReference<ImmutableLinkedStack<T>> location) {
        while (true) {
            ImmutableLinkedStack<T> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            if (priorCollection.isEmpty()) {
                return null;
            }

            if (location.compareAndSet(priorCollection, priorCollection.pop())) {
                return priorCollection.peek();
            }
        }
    }

    /**
     * Pops a value from a stack.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <T> The type of elements stored in the stack.
     * @param updater The updater for the field to atomically update.
     * @param obj The object whose field to update.
     * @return The value popped from the stack, if it was non-empty; otherwise {@code null} if the stack was empty.
     */
    @Nullable
    public static <C, T> T tryPop(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableLinkedStack<T>> updater, @Nonnull C obj) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");

        while (true) {
            ImmutableLinkedStack<T> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            if (priorCollection.isEmpty()) {
                return null;
            }

            if (updater.compareAndSet(obj, priorCollection, priorCollection.pop())) {
                return priorCollection.peek();
            }
        }
    }

    /**
     * Pushes a new element onto a stack.
     *
     * @param <T> The type of elements stored in the stack.
     * @param location The variable or field to atomically update.
     * @param value The value to push.
     */
    public static <T> void push(@Nonnull AtomicReference<ImmutableLinkedStack<T>> location, T value) {
        boolean successful;
        do {
            ImmutableLinkedStack<T> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            successful = location.compareAndSet(priorCollection, priorCollection.push(value));
        } while (!successful);
    }

    /**
     * Pushes a new element onto a stack.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <T> The type of elements stored in the stack.
     * @param updater The updater for the field to atomically update.
     * @param obj The object whose field to update.
     * @param value The value to push.
     */
    public static <C, T> void push(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableLinkedStack<T>> updater, @Nonnull C obj, T value) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");

        boolean successful;
        do {
            ImmutableLinkedStack<T> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            successful = updater.compareAndSet(obj, priorCollection, priorCollection.push(value));
        } while (!successful);
    }

    /**
     * Atomically removes the element at the head of a queue and returns it to the caller, if the queue is not empty.
     *
     * @param <T> The type of element stored in the queue.
     * @param location The variable or field to atomically update.
     * @return The value from the head of the queue, if the queue is non-empty; otherwise, {@code null}.
     */
    @Nullable
    public static <T> T tryPoll(@Nonnull AtomicReference<ImmutableLinkedQueue<T>> location) {
        while (true) {
            ImmutableLinkedQueue<T> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            if (priorCollection.isEmpty()) {
                return null;
            }

            if (location.compareAndSet(priorCollection, priorCollection.poll())) {
                return priorCollection.peek();
            }
        }
    }

    /**
     * Atomically removes the element at the head of a queue and returns it to the caller, if the queue is not empty.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <T> The type of element stored in the queue.
     * @param updater The updater for the field to atomically update.
     * @param obj The object whose field to update.
     * @return The value from the head of the queue, if the queue is non-empty; otherwise, {@code null}.
     */
    @Nullable
    public static <C, T> T tryPoll(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableLinkedQueue<T>> updater, @Nonnull C obj) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");

        while (true) {
            ImmutableLinkedQueue<T> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            if (priorCollection.isEmpty()) {
                return null;
            }

            if (updater.compareAndSet(obj, priorCollection, priorCollection.poll())) {
                return priorCollection.peek();
            }
        }
    }

    /**
     * Atomically adds an element to the tail of a queue.
     *
     * @param <T> The type of element stored in the queue.
     * @param location The variable or field to atomically update.
     * @param value The value to add.
     */
    public static <T> void add(@Nonnull AtomicReference<ImmutableLinkedQueue<T>> location, T value) {
        boolean successful;
        do {
            ImmutableLinkedQueue<T> priorCollection = location.get();
            Requires.notNull(priorCollection, "location");

            successful = location.compareAndSet(priorCollection, priorCollection.add(value));
        } while (!successful);
    }

    /**
     * Atomically adds an element to the tail of a queue.
     *
     * @param <C> The type of the object holding the updatable field.
     * @param <T> The type of element stored in the queue.
     * @param updater The updater for the field to atomically update.
     * @param obj The object whose field to update.
     * @param value The value to add.
     */
    public static <C, T> void add(@Nonnull AtomicReferenceFieldUpdater<? super C, ImmutableLinkedQueue<T>> updater, @Nonnull C obj, T value) {
        Requires.notNull(updater, "updater");
        Requires.notNull(obj, "obj");

        boolean successful;
        do {
            ImmutableLinkedQueue<T> priorCollection = updater.get(obj);
            Requires.notNull(priorCollection, "updater");

            successful = updater.compareAndSet(obj, priorCollection, priorCollection.add(value));
        } while (!successful);
    }
}
