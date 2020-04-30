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

package org.openksavi.sponge.integration.tests.java.examples;

import org.openksavi.sponge.java.JActionBuilder;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.provided.ProvidedMeta;
import org.openksavi.sponge.type.provided.ProvidedValue;

/**
 * Sponge Knowledge base.
 */
public class ActionsBuilder extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("initialized_UpperEchoAction", false);
        getSponge().setVariable("called_UpperEchoActionMulti", false);
        getSponge().setVariable("called_NoArgAndResultAction", false);
    }

    @Override
    public void onLoad() {
        getSponge().enable(new JActionBuilder("UpperEchoAction").withLabel("Echo Action").withDescription("Returns the upper case string")
                .withArg(new StringType("text").withLabel("Argument 1").withDescription("Argument 1 description"))
                .withResult(new StringType().withLabel("Upper case string").withDescription("Result description"))
                .withOnInit(action -> getSponge().setVariable("initialized_" + action.getMeta().getName(), true))
                .withOnCallArgs((action, args) -> ((String) args.get(0)).toUpperCase()));

        getSponge()
                .enable(new JActionBuilder("UpperEchoActionMulti").withLabel("Echo Action").withDescription("Returns the upper case string")
                        .withArg(new StringType("text").withLabel("Argument 1").withDescription("Argument 1 description"))
                        .withResult(new StringType().withLabel("Upper case string").withDescription("Result description"))
                        .withOnCallArgs((action, args) -> {
                            getSponge().getLogger().info("Action {} called", action.getMeta().getName());
                            getSponge().setVariable("called_" + action.getMeta().getName(), true);
                            return ((String) args.get(0)).toUpperCase();
                        }));

        getSponge().enable(new JActionBuilder("NoArgAndResultAction")
                .withOnCallArgsNoResult((action, args) -> getSponge().setVariable("called_" + action.getMeta().getName(), true)));

        getSponge().enable(
                new JActionBuilder("ProvidedArgsAction").withArg(new StringType("text").withProvided(new ProvidedMeta().withValue()))
                        .withNonCallable().withOnProvideArgs((action, context) -> {
                            if (context.getProvide().contains("text")) {
                                context.getProvided().put("text", new ProvidedValue<>().withValue("ABC"));
                            }
                        }));
    }
}
