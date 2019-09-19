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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class LocalCacheTest {

    @Test
    public void testLocalCache() {
        LocalCache<String, Integer> cache = SpongeUtils.cacheBuilder().build();// (text) -> text.length());
        cache.put("1", 1);
        cache.put("2", 2);
        cache.put("3", 3);

        assertEquals(1, cache.getIfPresent("1").intValue());
        assertEquals(2, cache.getIfPresent("2").intValue());
        assertEquals(3, cache.getIfPresent("3").intValue());
        assertNull(cache.getIfPresent("4"));
        assertEquals(4, cache.get("4", () -> 4).intValue());
        assertEquals(4, cache.getIfPresent("4").intValue());
        cache.invalidate("4");
        assertNull(cache.getIfPresent("4"));
    }

    @Test
    public void testLocalLoadingCache() {
        LocalLoadingCache<String, Integer> cache = SpongeUtils.cacheBuilder().build((text) -> Integer.parseInt(text));

        assertNull(cache.getIfPresent("1"));
        assertEquals(1, cache.get("1").intValue());
        assertEquals(2, cache.get("2").intValue());
        assertEquals(3, cache.get("3").intValue());

        assertEquals(4, cache.get("4", () -> 4).intValue());
        assertEquals(4, cache.getIfPresent("4").intValue());
        cache.invalidate("4");
        assertNull(cache.getIfPresent("4"));
        assertEquals(4, cache.get("4").intValue());
        cache.refresh("4");
        assertEquals(4, cache.getIfPresent("4").intValue());
        assertEquals(4, cache.size());
    }
}
