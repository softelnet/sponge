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

package org.openksavi.sponge.util;

import java.util.regex.Pattern;

/**
 * The pattern matcher.
 */
public interface PatternMatcher {

    /**
     * Validates the pattern. Throws an exception if the pattern is invalid.
     *
     * @param pattern the string representation of the pattern.
     */
    void validatePattern(String pattern);

    /**
     * Returns the pattern for its string representation.
     *
     * @param pattern the string representation of the pattern.
     * @return the pattern.
     */
    Pattern getPattern(String pattern);

    /**
     * Returns {@code true} if the text matches the pattern.
     *
     * @param pattern the pattern.
     * @param text the text.
     * @return {@code true} if the text matches the pattern.
     */
    boolean matches(String pattern, String text);

    /**
     * Returns {@code true} if the text matches any of the patterns.
     *
     * @param patterns the patterns.
     * @param text the text.
     * @return {@code true} if the text matches any of the patterns.
     */
    boolean matchesAny(String[] patterns, String text);
}
