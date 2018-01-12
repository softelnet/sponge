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

package org.openksavi.sponge.kotlin.test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;

public class SimpleKotlinTest {

    @Test
    public void testKotlin() throws ScriptException {
        ScriptEngineFactory factory = new ScriptEngineManager().getEngineByExtension("kts").getFactory();
        ScriptEngine engine = factory.getScriptEngine();
        engine.eval("val x = 3");
        engine.eval("class A");
        engine.eval("A()");
        engine.eval("fun a()\n {\n val y=1\nprintln(y)\n}\n");
        engine.eval("fun onStartup() {\n" + "    var x = \"abc\"\n" + "    println(x)\n" + "}");
    }
}
