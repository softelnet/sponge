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

package org.openksavi.sponge.restapi.server;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.SpongeRequest;
import org.openksavi.sponge.restapi.model.response.ActionCallResponse;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;

/**
 * A REST API operation that delegates a custom REST API request to an action call (e.g. to allow implementing operation body in a scripting
 * language but keeping a static REST interface).
 */
public class ActionDelegateRestApiOperation<I extends SpongeRequest, O extends SpongeResponse> extends RestApiOperation<I, O> {

    public ActionDelegateRestApiOperation(String type, String description, Class<I> requestClass, String requestDescription,
            Class<O> responseClass, String responseDescription, String delegateActionName, RestApiService service) {
        super(type, description, requestClass, requestDescription, responseClass, responseDescription, (request, exchange) -> {
            ActionCallRequest actionCallRequest = new ActionCallRequest();
            // The default naming convention for an action name if not provided.
            String actionName = delegateActionName != null ? delegateActionName : StringUtils.capitalize(type);

            actionCallRequest.setName(actionName);
            actionCallRequest.setArgs(Arrays.asList(request));

            actionCallRequest.setId(request.getId());
            actionCallRequest.setUsername(request.getUsername());
            actionCallRequest.setPassword(request.getPassword());
            actionCallRequest.setAuthToken(request.getAuthToken());

            ActionCallResponse actionCallResponse = service.call(actionCallRequest);

            @SuppressWarnings("unchecked")
            O response = (O) actionCallResponse.getResult();
            Validate.isInstanceOf(responseClass, response, "The %s action result class is %s but should be %s", actionName,
                    response != null ? response.getClass() : null, responseClass);

            // An action shouldn't set the base response properties.
            response.setId(actionCallResponse.getId());
            response.setErrorMessage(actionCallResponse.getErrorMessage());
            response.setErrorCode(actionCallResponse.getErrorCode());
            response.setDetailedErrorMessage(actionCallResponse.getDetailedErrorMessage());

            return response;
        });
    }
}
