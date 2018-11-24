package com.kovlev.etbf.util;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Bidirectional Multimap
 * code from https://stackoverflow.com/questions/20390923/do-we-have-a-multibimap
 * @param <K> Key
 * @param <V> Value
 */
public class BiMultimap<K, V> {
    private final SetMultimap<K, V> keysToValues = HashMultimap.create();

    private final SetMultimap<V, K> valuesToKeys = HashMultimap.create();

    /**
     * Retrieves all values of a key
     * @param key The key to query
     * @return The corresponding values
     */
    public Set<V> getValues(K key) {
        return keysToValues.get(key);
    }

    /**
     * Retrieves all keys of a value
     * @param value The value to query
     * @return The corresponding keys
     */
    public Set<K> getKeys(V value) {
        return valuesToKeys.get(value);
    }

    /**
     * Puts a key-value pair into the bimultimap
     * @param key The key part
     * @param value Te value part
     * @return True, if the pair did not exist before
     */
    public boolean put(K key, V value) {
        return keysToValues.put(key, value) && valuesToKeys.put(value, key);
    }

    /**
     * @return The size of the bimultimap
     */
    public int size() {
        return keysToValues.size();
    }
}


