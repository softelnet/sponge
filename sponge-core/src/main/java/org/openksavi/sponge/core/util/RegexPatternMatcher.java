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

package org.openksavi.sponge.core.util;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.openksavi.sponge.util.PatternMatcher;

/**
 * The regular expression pattern matcher.
 */
public class RegexPatternMatcher implements PatternMatcher {

    private LoadingCache<String, Pattern> compiledPatterns = CacheBuilder.newBuilder().build(new CacheLoader<String, Pattern>() {

        @Override
        public Pattern load(String pattern) throws Exception {
            return Pattern.compile(pattern);
        }
    });

    @Override
    public Pattern getPattern(String pattern) {
        try {
            return compiledPatterns.get(pattern);
        } catch (ExecutionException e) {
            throw Utils.wrapException("getPattern", e);
        }
    }

    @Override
    public boolean matches(String pattern, String text) {
        return getPattern(pattern).matcher(text).matches();
    }

    @Override
    public boolean matchesAny(String[] patterns, String text) {
        for (String pattern : patterns) {
            if (matches(pattern, text)) {
                return true;
            }
        }

        return false;
    }
}
