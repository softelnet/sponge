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

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.restapi.model.RestActionArgMeta;
import org.openksavi.sponge.restapi.model.RestActionCall;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.RestActionResultMeta;
import org.openksavi.sponge.restapi.model.RestActionsResult;
import org.openksavi.sponge.restapi.model.RestCallResult;
import org.openksavi.sponge.restapi.model.RestEvent;
import org.openksavi.sponge.restapi.model.RestReloadResult;
import org.openksavi.sponge.restapi.model.RestSendResult;
import org.openksavi.sponge.restapi.model.RestVersionResult;

/**
 * Sponge REST service.
 */
public class RestApiService {

    private SpongeEngine engine;

    public RestApiService(SpongeEngine engine) {
        this.engine = engine;
    }

    public RestCallResult call(RestActionCall action) {
        try {
            return new RestCallResult(action.getName(),
                    engine.getActionManager().callAction(action.getName(), action.getArgs() != null ? action.getArgs().toArray() : null));
        } catch (Exception e) {
            engine.handleError(engine.getActionManager().getActionAdapter(action.getName()), e);
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

    public RestActionsResult getActions(Boolean metadataRequired) {
        boolean actualMetadataRequired =
                metadataRequired != null ? metadataRequired : RestApiConstants.REST_PARAM_ACTIONS_METADATA_REQUIRED_DEFAULT;

        return new RestActionsResult(engine.getActions().stream()
                .filter(action -> actualMetadataRequired ? action.getArgsMeta() != null && action.getResultMeta() != null : true)
                .map(action -> new RestActionMeta(action.getName(), action.getDisplayName(), createActionArgMetaList(action),
                        createActionResultMeta(action)))
                .collect(Collectors.toList()));
    }

    public RestActionsResult getActions() {
        return getActions(null);
    }

    public RestVersionResult getVersion() {
        return new RestVersionResult(engine.getVersion());
    }

    protected List<RestActionArgMeta> createActionArgMetaList(ActionAdapter actionAdapter) {
        return actionAdapter.getArgsMeta() != null ? Arrays.stream(actionAdapter.getArgsMeta())
                .map(meta -> new RestActionArgMeta(meta.getName(), meta.getType().getCode(), meta.isRequired(), meta.getDisplayName()))
                .collect(Collectors.toList()) : null;
    }

    protected RestActionResultMeta createActionResultMeta(ActionAdapter actionAdapter) {
        return actionAdapter.getResultMeta() != null ? new RestActionResultMeta(actionAdapter.getResultMeta().getType().getCode(),
                actionAdapter.getResultMeta().getDisplayName()) : null;
    }

    public RestReloadResult reload() {
        try {
            engine.reload();

            return new RestReloadResult();
        } catch (Exception e) {
            engine.handleError("REST reload", e);
            return RestReloadResult.fromException(e);
        }
    }
}
