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

package org.openksavi.sponge.examples;

import org.openksavi.sponge.java.JAction;

public class PowerEchoAction extends JAction {

    @Override
    public Object onCall(Object[] args) {
        getLogger().debug("PowerEchoAction called");
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                getLogger().debug("Arg " + i + ": " + args[i] + " (" + args[i].getClass().getName() + ")");
                if (args[i] instanceof String) {
                    args[i] = ((String) args[i]).toUpperCase();
                } else if (args[i] instanceof Number) {
                    args[i] = Integer.valueOf(((Number) args[i]).intValue() + 1);
                }
            }
        } else {
            getLogger().debug("Action called without args.");
        }

        getLogger().debug("Action successfuly called with {} argument(s).", args == null ? 0 : args.length);

        return args;
    }
}
