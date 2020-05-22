/*
 * Copyright 2016-2020 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.security;

import java.util.Arrays;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerConstants;
import org.openksavi.sponge.util.ValueHolder;

public class KnowledgeBaseProvidedAccessService extends BaseAccessService {

    @Override
    public boolean canCallAction(UserContext userContext, ActionAdapter actionAdapter) {
        return canUseKnowledgeBase(userContext, actionAdapter.getKnowledgeBase());
    }

    @Override
    public boolean canSendEvent(UserContext userContext, String eventName) {
        ValueHolder<Boolean> holder = getRemoteApiService().getEngine().getOperations().callIfExists(Boolean.class,
                RemoteApiServerConstants.ACTION_CAN_SEND_EVENT, Arrays.asList(userContext, eventName));
        return holder != null ? holder.getValue() : false;
    }

    @Override
    public boolean canSubscribeEvent(UserContext userContext, String eventName) {
        ValueHolder<Boolean> holder = getRemoteApiService().getEngine().getOperations().callIfExists(Boolean.class,
                RemoteApiServerConstants.ACTION_CAN_SUBSCRIBE_EVENT, Arrays.asList(userContext, eventName));
        return holder != null ? holder.getValue() : false;
    }

    @Override
    public boolean canUseKnowledgeBase(UserContext userContext, KnowledgeBase knowledgeBase) {
        ValueHolder<Boolean> holder = getRemoteApiService().getEngine().getOperations().callIfExists(Boolean.class,
                RemoteApiServerConstants.ACTION_CAN_USE_KNOWLEDGE_BASE, Arrays.asList(userContext, knowledgeBase.getName()));
        return holder != null ? holder.getValue() : false;
    }
}
