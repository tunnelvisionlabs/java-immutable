// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Map;

/**
 * This class provides a general-purpose, read-only implementation of {@link Map.Entry}.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
final class KeyValuePair<K, V> implements Map.Entry<K, V> {
    private final K key;
    private final V value;

    /**
     * Constructs a new instance of the {@link KeyValuePair} class with the specified key and value.
     *
     * @param key The key for the pair.
     * @param value The value for the pair.
     */
    KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Constructs a new instance of the {@link KeyValuePair} class with the specified key and value.
     *
     * @param <K> The type of the key.
     * @param <V> The type of the value.
     * @param key The key for the pair.
     * @param value The value for the pair.
     */
    public static <K, V> KeyValuePair<K, V> create(K key, V value) {
        return new KeyValuePair<K, V>(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException("This entry is read-only.");
    }
}
