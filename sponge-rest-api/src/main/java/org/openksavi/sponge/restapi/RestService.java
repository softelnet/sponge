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

package org.openksavi.sponge.restapi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.restapi.model.RestAction;
import org.openksavi.sponge.restapi.model.RestActionArgMetadata;
import org.openksavi.sponge.restapi.model.RestActionMetadata;
import org.openksavi.sponge.restapi.model.RestActionsResult;
import org.openksavi.sponge.restapi.model.RestCallResult;
import org.openksavi.sponge.restapi.model.RestEvent;
import org.openksavi.sponge.restapi.model.RestSendResult;
import org.openksavi.sponge.restapi.model.RestVersion;

/**
 * Sponge REST service.
 */
public class RestService {

    @Inject
    private SpongeEngine engine;

    public RestCallResult call(RestAction action) {
        try {
            return new RestCallResult(action.getName(),
                    engine.getActionManager().callAction(action.getName(), action.getArgs() != null ? action.getArgs().toArray() : null));
        } catch (Exception e) {
            return RestCallResult.fromException(action.getName(), e);
        }
    }

    public RestSendResult send(RestEvent event) {
        EventDefinition definition = engine.getOperations().event(event.getName());

        if (event.getAttributes() != null) {
            event.getAttributes().forEach((name, value) -> definition.set(name, value));
        }

        return new RestSendResult(definition.send().getId());
    }

    public RestActionsResult getActions() {
        return new RestActionsResult(engine.getActions().stream()
                .map(action -> new RestActionMetadata(action.getName(), action.getDisplayName(), createActionArgMetadataList(action)))
                .collect(Collectors.toList()));
    }

    public RestVersion getVersion() {
        return new RestVersion(engine.getVersion());
    }

    protected List<RestActionArgMetadata> createActionArgMetadataList(ActionAdapter actionAdapter) {
        return actionAdapter.getArgsMetadata() != null
                ? Arrays.stream(actionAdapter.getArgsMetadata())
                        .map(meta -> new RestActionArgMetadata(meta.getName(), meta.getType().getCode())).collect(Collectors.toList())
                : null;
    }
}
