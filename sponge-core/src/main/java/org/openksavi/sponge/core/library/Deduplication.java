/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.core.library;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.openksavi.sponge.event.Event;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Deduplication {

    private String attributeName;

    private CacheBuilder cacheBuilder = CacheBuilder.newBuilder();

    private Cache<Pair<String, Object>, Boolean> cache;

    public Deduplication(String attributeName) {
        this.attributeName = attributeName;
    }

    public CacheBuilder<Pair<String, Object>, Boolean> getCacheBuilder() {
        return cacheBuilder;
    }

    public synchronized Cache<Pair<String, Object>, Boolean> getCache() {
        if (cache == null) {
            cache = cacheBuilder.build();
        }

        return cache;
    }

    public boolean onAccept(Event event) {
        Pair<String, Object> key = ImmutablePair.of(event.getName(), event.get(getAttributeName()));

        Cache<Pair<String, Object>, Boolean> cache = getCache();
        synchronized (cache) {
            boolean hasOccured = cache.getIfPresent(key) != null;

            if (!hasOccured) {
                cache.put(key, true);
                return true;
            } else {
                return false;
            }
        }
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
