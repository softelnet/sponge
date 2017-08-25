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

package org.openksavi.sponge.core.kb;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;

/**
 * The provider of script-based class instances that caches parsed expressions.
 *
 * @param <S> the type of the script.
 * @param <S> the type of the instance to provide.
 */
public class CachedScriptClassInstancePovider<S, T> {

    private Function<String, S> createScriptFunction;

    private String format;

    private BiFunction<S, Class<T>, T> createInstanceFunction;

    /** The cache. {@code null} if caching is turned off. */
    private LoadingCache<String, S> cache;

    public CachedScriptClassInstancePovider(Engine engine, Function<String, S> createScriptFunction, String format,
            BiFunction<S, Class<T>, T> createInstanceFunction) {
        this.createScriptFunction = createScriptFunction;
        this.format = format;
        this.createInstanceFunction = createInstanceFunction;

        long cacheExpireTime = engine.getDefaultParameters().getScriptClassInstancePoviderCacheExpireTime();
        if (cacheExpireTime >= 0) {
            // Turn on the cache.
            CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
            if (cacheExpireTime > 0) {
                builder.expireAfterAccess(cacheExpireTime, TimeUnit.MILLISECONDS);
            }

            cache = builder.build(new CacheLoader<String, S>() {

                @Override
                public S load(String className) throws Exception {
                    return createScript(className);
                }
            });
        }
    }

    public T newInstance(String className, Class<T> javaClass) {
        try {
            return createInstanceFunction.apply(cache != null ? cache.get(className) : createScript(className), javaClass);
        } catch (ExecutionException e) {
            throw Utils.wrapException(getClass().getSimpleName(), e.getCause() != null ? e.getCause() : e);
        } catch (Exception e) {
            throw Utils.wrapException(getClass().getSimpleName(), e);
        }
    }

    public void invalidate() {
        if (cache != null) {
            cache.invalidateAll();
        }
    }

    protected S createScript(String className) {
        return createScriptFunction.apply(String.format(format, className));
    }
}
