/* Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.examples.script;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class HelloWorldTest extends AbstractScriptExamplesTest {

    @Parameters(name = "{0}, timeout={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // @formatter:off
                { "py/hello_world", 2 },
                { "rb/hello_world", 2 },
                { "groovy/hello_world", 2 },
                { "js/hello_world", 2 },
                //@formatter:on
        });
    }

    public HelloWorldTest(String exampleId, int timeout) {
        super(exampleId, timeout);
    }
}
