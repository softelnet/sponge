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

package org.openksavi.sponge.remoteapi.server;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest;
import org.openksavi.sponge.remoteapi.model.request.RequestHeader;
import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.model.response.ActionCallResponse;
import org.openksavi.sponge.remoteapi.model.response.SpongeResponse;

/**
 * A Remote API operation that delegates a custom Remote API request to an action call (e.g. to allow implementing an operation body in a
 * scripting language but keeping a static API).
 *
 * @param <I> a request.
 * @param <O> a response.
 * @param <A> an action result.
 */
@SuppressWarnings("rawtypes")
public class ActionDelegateOperation<I extends SpongeRequest, O extends SpongeResponse, A> extends RemoteApiOperation<I, O> {

    @SuppressWarnings("unchecked")
    protected ActionDelegateOperation(String name, String description, Class<I> requestClass, String requestDescription,
            Class<O> responseClass, String responseDescription, String actionName, Function<I, List<Object>> argsMapper,
            BiConsumer<O, A> resultMapper) {
        super(name, description, requestClass, requestDescription, responseClass, responseDescription, (service, request, exchange) -> {
            ActionCallRequest actionCallRequest = new ActionCallRequest();
            actionCallRequest.getParams().setHeader(new RequestHeader());

            actionCallRequest.setId(request.getId());
            actionCallRequest.getParams().getHeader().setUsername(request.getParams().getHeader().getUsername());
            actionCallRequest.getParams().getHeader().setPassword(request.getParams().getHeader().getPassword());
            actionCallRequest.getParams().getHeader().setAuthToken(request.getParams().getHeader().getAuthToken());
            actionCallRequest.getParams().getHeader().setFeatures(request.getParams().getHeader().getFeatures());

            // The default naming convention for an action name if not provided.
            final String delegateActionName = actionName != null ? actionName : StringUtils.capitalize(name);

            actionCallRequest.getParams().setName(delegateActionName);
            actionCallRequest.getParams().setArgs(argsMapper != null ? argsMapper.apply(request) : Arrays.asList(request));

            ActionCallResponse actionCallResponse = service.call(actionCallRequest);

            Object rawResult = actionCallResponse.getResult().getValue();

            O response;
            try {
                response = ConstructorUtils.invokeConstructor(responseClass);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                throw SpongeUtils.wrapException(e);
            } catch (InvocationTargetException e) {
                throw SpongeUtils.wrapException(e.getTargetException());
            }

            resultMapper.accept(response, (A) rawResult);

            response.setId(actionCallResponse.getId());

            return response;
        });
    }

    public static <I extends SpongeRequest, O extends SpongeResponse, A> ActionDelegateRemoteApiOperationBuilder<I, O, A> builder() {
        return new ActionDelegateRemoteApiOperationBuilder<>();
    }

    public static class ActionDelegateRemoteApiOperationBuilder<I extends SpongeRequest, O extends SpongeResponse, A> {

        private String name;

        private String description;

        private Class<I> requestClass;

        private String requestDescription;

        private Class<O> responseClass;

        private String responseDescription;

        private String actionName;

        private Function<I, List<Object>> argsMapper;

        private BiConsumer<O, A> resultMapper;

        public ActionDelegateRemoteApiOperationBuilder<I, O, A> name(String name) {
            this.name = name;
            return this;
        }

        public ActionDelegateRemoteApiOperationBuilder<I, O, A> description(String description) {
            this.description = description;
            return this;
        }

        public ActionDelegateRemoteApiOperationBuilder<I, O, A> requestClass(Class<I> requestClass) {
            this.requestClass = requestClass;
            return this;
        }

        public ActionDelegateRemoteApiOperationBuilder<I, O, A> requestDescription(String requestDescription) {
            this.requestDescription = requestDescription;
            return this;
        }

        public ActionDelegateRemoteApiOperationBuilder<I, O, A> responseClass(Class<O> responseClass) {
            this.responseClass = responseClass;
            return this;
        }

        public ActionDelegateRemoteApiOperationBuilder<I, O, A> responseDescription(String responseDescription) {
            this.responseDescription = responseDescription;
            return this;
        }

        public ActionDelegateRemoteApiOperationBuilder<I, O, A> actionName(String actionName) {
            this.actionName = actionName;
            return this;
        }

        public ActionDelegateRemoteApiOperationBuilder<I, O, A> argsMapper(Function<I, List<Object>> argsMapper) {
            this.argsMapper = argsMapper;
            return this;
        }

        public ActionDelegateRemoteApiOperationBuilder<I, O, A> resultMapper(BiConsumer<O, A> resultMapper) {
            this.resultMapper = resultMapper;
            return this;
        }

        public ActionDelegateOperation<I, O, A> build() {
            Validate.notNull(name, "The operation name must be set");
            Validate.notNull(requestClass, "The operation request class must be set");
            Validate.notNull(requestDescription, "The operation request description must be set");
            Validate.notNull(responseClass, "The operation response class must be set");
            Validate.notNull(responseDescription, "The operation response description must be set");
            Validate.notNull(argsMapper, "The action args mapper must be set");
            Validate.notNull(resultMapper, "The action result mapper must be set");

            return new ActionDelegateOperation<>(name, description, requestClass, requestDescription, responseClass, responseDescription,
                    actionName, argsMapper, resultMapper);
        }
    }
}
