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

import java.time.Duration;
import java.util.function.Function;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * A local cache builder.
 */
public class LocalCacheBuilder {

    private CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();

    public LocalCacheBuilder expireAfterAccess(Duration duration) {
        builder.expireAfterAccess(duration);
        return this;
    }

    public LocalCacheBuilder expireAfterWrite(Duration duration) {
        builder.expireAfterWrite(duration);
        return this;
    }

    public LocalCacheBuilder initialCapacity(int initialCapacity) {
        builder.initialCapacity(initialCapacity);
        return this;
    }

    public LocalCacheBuilder maximumSize(long maximumSize) {
        builder.maximumSize(maximumSize);
        return this;
    }

    public LocalCacheBuilder refreshAfterWrite(Duration duration) {
        builder.refreshAfterWrite(duration);
        return this;
    }

    public LocalCacheBuilder softValues() {
        builder.softValues();
        return this;
    }

    public LocalCacheBuilder weakKeys() {
        builder.weakKeys();
        return this;
    }

    public LocalCacheBuilder weakValues() {
        builder.weakValues();
        return this;
    }

    public <K, V> LocalLoadingCache<K, V> build(Function<K, V> loader) {
        return new GuavaLocalLoadingCache<>(builder.build(new CacheLoader<K, V>() {

            @Override
            public V load(K key) {
                return loader.apply(key);
            }
        }));
    }

    public <K, V> LocalCache<K, V> build() {
        return new GuavaLocalCache<>(builder.build());
    }
}
