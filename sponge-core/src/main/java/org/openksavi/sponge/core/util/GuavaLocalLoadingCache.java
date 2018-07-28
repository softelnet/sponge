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

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.LoadingCache;

/**
 * A Guava based local loading cache.
 */
public class GuavaLocalLoadingCache<K, V> extends GuavaLocalCache<K, V> implements LocalLoadingCache<K, V> {

    public GuavaLocalLoadingCache(LoadingCache<K, V> cache) {
        super(cache);
    }

    protected LoadingCache<K, V> getLoadingCache() {
        return (LoadingCache<K, V>) cache;
    }

    @Override
    public V get(K key) {
        return getLoadingCache().getUnchecked(key);
    }

    @Override
    public Map<K, V> getAll(Iterable<? extends K> keys) {
        try {
            return getLoadingCache().getAll(keys);
        } catch (ExecutionException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    @Override
    public void refresh(K key) {
        getLoadingCache().refresh(key);
    }
}
