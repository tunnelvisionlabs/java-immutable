// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Map;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An immutable key-value map.
 *
 * @param <K> The type of keys in the map.
 * @param <V> The type of values stored in the map.
 */
public interface ImmutableMap<K, V> extends ReadOnlyMap<K, V> {

    /**
     * Gets an empty map with equivalent ordering and key/value comparison rules.
     *
     * @return The empty map.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableMap<K, V> clear();

    /**
     * Adds the specified key and value to the map.
     *
     * If the given key-value pair is already in the map, the existing instance is returned.
     *
     * @param key The key of the entry to add.
     * @param value The value of the entry to add.
     * @return The new map containing the additional key-value pair.
     * @throws IllegalArgumentException if the given key already exists in the map, but has a different value.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableMap<K, V> add(@Nonnull K key, V value);

    /**
     * Adds the specified key-value pairs to the map.
     *
     * @param entries The pairs.
     * @return The new map containing the additional key-value pairs.
     * @exception IllegalArgumentException if one of the given keys already exists in the map, but has a different
     * value.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableMap<K, V> addAll(@Nonnull Iterable<? extends Map.Entry<K, V>> entries);

    /**
     * Sets the specified key and value in the map, possibly overwriting an existing value for the given key.
     *
     * If the given key-value pair are already in the map, the existing instance is returned. If the key already exists
     * but with a different value, a new instance with the overwritten value will be returned.
     *
     * @param key The key of the entry to add.
     * @param value The value of the entry to add.
     * @return The new map containing the additional key-value pair.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableMap<K, V> put(@Nonnull K key, V value);

    /**
     * Applies a given set of key=value pairs to an immutable map, replacing any conflicting keys in the resulting map.
     *
     * @param entries The key=value pairs to set on the map. Any keys that conflict with existing keys will overwrite
     * the previous values.
     * @return The new map.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableMap<K, V> putAll(@Nonnull Iterable<? extends Map.Entry<K, V>> entries);

    /**
     * Removes the specified key from the map with its associated value.
     *
     * @param key The key to remove.
     * @return A new map with the matching entry removed; or this instance if the key is not in the map.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableMap<K, V> remove(@Nonnull K key);

    /**
     * Removes the specified keys from the map with their associated values.
     *
     * @param keys The keys to remove.
     * @return A new map with those keys removed; or this instance if those keys are not in the map.
     */
    @Nonnull
    @CheckReturnValue
    ImmutableMap<K, V> removeAll(@Nonnull Iterable<? extends K> keys);

    /**
     * Determines whether this map contains the specified key-value pair.
     *
     * @param pair The key-value pair.
     * @return {@code true} if this map contains the key-value pair; otherwise, {@code false}.
     */
    boolean contains(@Nonnull Map.Entry<K, V> pair);

    /**
     * Searches the map for a given key and returns the equal key it finds, if any.
     *
     * This can be useful when you want to reuse a previously stored reference instead of a newly constructed one (so
     * that more sharing of references can occur) or to look up the canonical value, or a value that has more complete
     * data than the value you currently have, although their comparator functions indicate they are equal.
     *
     * @param equalKey The key to search for.
     * @return The key from the map that the search found, or {@code null} if the search yielded no match.
     */
    @Nullable
    K getKey(@Nonnull K equalKey);

}
