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

import java.util.regex.Pattern;

public class PatternStringReplacer {

    private Pattern pattern;

    private String replaceText;

    public PatternStringReplacer(String replaceRegexp, String replaceText) {
        pattern = Pattern.compile(replaceRegexp);
        this.replaceText = replaceText;
    }

    public String replaceAll(String text) {
        return pattern.matcher(text).replaceAll(replaceText);
    }
}
