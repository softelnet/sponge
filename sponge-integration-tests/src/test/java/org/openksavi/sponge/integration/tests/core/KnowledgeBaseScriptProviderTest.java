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

package org.openksavi.sponge.integration.tests.core;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.jython.PythonConstants;

public class KnowledgeBaseScriptProviderTest {

    @Test
    public void testKnowledgeBaseScriptProvider() throws InterruptedException {
        String reloadingVariableName = "reloading";
        String startingVariableName = "starting";

        String kbScriptBody = String.format(
                "def onAfterReload():\n\tsponge.setVariable('%s', True)"
                        + "\ndef onStartup():\n\tsponge.logger.debug('Starting')\n\tsponge.setVariable('%s', True)",
                reloadingVariableName, startingVariableName);
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBaseString("stringKb", PythonConstants.TYPE, kbScriptBody).build();
        engine.startup();

        try {
            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.getOperations().hasVariable(startingVariableName)
                    && engine.getOperations().getVariable(Boolean.class, startingVariableName));

            engine.reload();

            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.getOperations().hasVariable(reloadingVariableName)
                    && engine.getOperations().getVariable(Boolean.class, reloadingVariableName));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
