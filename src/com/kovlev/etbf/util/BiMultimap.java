package com.kovlev.etbf.util;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * code from https://stackoverflow.com/questions/20390923/do-we-have-a-multibimap
 * @param <K> Key
 * @param <V> Value
 */
public class BiMultimap<K, V> {
    private final SetMultimap<K, V> keysToValues = HashMultimap.create();

    private final SetMultimap<V, K> valuesToKeys = HashMultimap.create();

    public Set<V> getValues(K key) {
        return keysToValues.get(key);
    }

    public Set<K> getKeys(V value) {
        return valuesToKeys.get(value);
    }

    public boolean put(K key, V value) {
        return keysToValues.put(key, value) && valuesToKeys.put(value, key);
    }

    public boolean putAll(K key, Iterable<? extends V> values) {
        boolean changed = false;
        for (V value : values) {
            changed = put(key, value) || changed;
        }
        return changed;
    }

    public int size() {
        return keysToValues.size();
    }
}


