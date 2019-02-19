/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.action;

/**
 * A value set metadata.
 */
public class ValueSetMeta {

    /** The flag specifying if the value set is limited only to the provided values. Defaults to {@code true}. */
    private boolean limited = true;

    public ValueSetMeta(boolean limited) {
        this.limited = limited;
    }

    public ValueSetMeta() {
    }

    public ValueSetMeta withLimited(boolean limited) {
        setLimited(limited);
        return this;
    }

    public ValueSetMeta withLimited() {
        return withLimited(true);
    }

    public ValueSetMeta withNotLimited() {
        return withLimited(false);
    }

    public boolean isLimited() {
        return limited;
    }

    public void setLimited(boolean limited) {
        this.limited = limited;
    }
}
