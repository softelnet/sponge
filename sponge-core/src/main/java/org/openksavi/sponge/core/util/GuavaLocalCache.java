/*
 * Copyright 2016-2018 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.core.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;

/**
 * A Guava based local cache.
 */
public class GuavaLocalCache<K, V> implements LocalCache<K, V> {

    protected Cache<K, V> cache;

    public GuavaLocalCache(Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key, Callable<? extends V> loader) {
        try {
            return cache.get(key, loader);
        } catch (ExecutionException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    @Override
    public V getIfPresent(K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void invalidate(K key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public void invalidate(Iterable<?> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }
}
