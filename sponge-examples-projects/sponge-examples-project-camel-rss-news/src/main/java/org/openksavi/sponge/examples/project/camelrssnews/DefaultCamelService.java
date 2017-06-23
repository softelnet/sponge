/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.examples.project.camelrssnews;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.springframework.stereotype.Service;

import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;

@Service("camelService")
public class DefaultCamelService implements CamelService {

    @Inject
    private Engine engine;

    @Inject
    private CamelContext camelContext;

    @Override
    @SuppressWarnings("unchecked")
    public void stopSourceRoutes() {
        Map<String, String> rssSources = engine.getOperations().getVariable(Map.class, CamelRssConstants.VAR_RSS_SOURCES);

        rssSources.forEach((source, url) -> {
            try {
                camelContext.stopRoute(source);
            } catch (Exception e) {
                throw Utils.wrapException(getClass().getSimpleName(), e);
            }
        });
    }
}
