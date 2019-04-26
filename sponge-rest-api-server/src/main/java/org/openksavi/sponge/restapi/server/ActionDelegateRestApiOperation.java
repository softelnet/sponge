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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.SpongeRequest;
import org.openksavi.sponge.restapi.model.response.ActionCallResponse;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;

/**
 * A REST API operation that delegates a custom REST API request to an action call (e.g. to allow implementing an operation body in a
 * scripting language but keeping a static REST interface).
 *
 * @param <I> a request.
 * @param <O> a response.
 * @param <A> an action result.
 */
public class ActionDelegateRestApiOperation<I extends SpongeRequest, O extends SpongeResponse, A> extends RestApiOperation<I, O> {

    @SuppressWarnings("unchecked")
    public ActionDelegateRestApiOperation(String type, String description, Class<I> requestClass, String requestDescription,
            Class<O> responseClass, String responseDescription, String delegateActionName, RestApiService service,
            Function<I, List<Object>> argsMapper, BiConsumer<O, A> resultMapper) {
        super(type, description, requestClass, requestDescription, responseClass, responseDescription, (request, exchange) -> {
            ActionCallRequest actionCallRequest = new ActionCallRequest();
            // The default naming convention for an action name if not provided.
            String actionName = delegateActionName != null ? delegateActionName : StringUtils.capitalize(type);

            actionCallRequest.setId(request.getId());
            actionCallRequest.setUsername(request.getUsername());
            actionCallRequest.setPassword(request.getPassword());
            actionCallRequest.setAuthToken(request.getAuthToken());

            actionCallRequest.setName(actionName);
            actionCallRequest.setArgs(argsMapper != null ? argsMapper.apply(request) : Arrays.asList(request));

            ActionCallResponse actionCallResponse = service.call(actionCallRequest);

            Object rawResult = actionCallResponse.getResult();
            O response;
            if (resultMapper != null) {
                try {
                    response = ConstructorUtils.invokeConstructor(responseClass);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                    throw SpongeUtils.wrapException(e);
                } catch (InvocationTargetException e) {
                    throw SpongeUtils.wrapException(e.getTargetException());
                }

                resultMapper.accept(response, (A) rawResult);
            } else {
                Validate.isInstanceOf(responseClass, rawResult, "The %s action result class is %s but should be %s", actionName,
                        rawResult != null ? rawResult.getClass() : null, responseClass);

                response = (O) rawResult;
            }

            // An action shouldn't set the base response properties.
            response.setId(actionCallResponse.getId());
            response.setErrorMessage(actionCallResponse.getErrorMessage());
            response.setErrorCode(actionCallResponse.getErrorCode());
            response.setDetailedErrorMessage(actionCallResponse.getDetailedErrorMessage());

            return response;
        });
    }

    public ActionDelegateRestApiOperation(String type, String description, Class<I> requestClass, String requestDescription,
            Class<O> responseClass, String responseDescription, String delegateActionName, RestApiService service) {
        this(type, description, requestClass, requestDescription, responseClass, responseDescription, delegateActionName, service, null,
                null);
    }
}
