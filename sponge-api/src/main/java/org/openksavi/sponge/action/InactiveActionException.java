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

import org.openksavi.sponge.SpongeException;

/**
 * An exception thrown when an action is inactive.
 */
public class InactiveActionException extends SpongeException {

    private static final long serialVersionUID = 6875407336569802104L;

    private static final String DEFAULT_MESSAGE_PATTERN = "The %s action is inactive";

    private String actionName;

    public InactiveActionException(String actionName) {
        super(String.format(DEFAULT_MESSAGE_PATTERN, actionName));

        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }
}
