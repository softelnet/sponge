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

package org.openksavi.sponge.restapi.security;

import java.util.Objects;

import org.openksavi.sponge.ProcessorNotFoundException;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.restapi.RestApiConstants;

public abstract class KnowledgeBaseProvidedSecurityService extends BaseRestApiSecurityService {

    protected KnowledgeBaseProvidedSecurityService() {
        //
    }

    @Override
    public boolean canCallAction(User user, ActionAdapter actionAdapter) {
        return canUseKnowledgeBase(user, actionAdapter.getKnowledgeBase());
    }

    @Override
    public boolean canSendEvent(User user, String eventName) {
        try {
            return getEngine().getOperations().call(Boolean.class, RestApiConstants.ACTION_CAN_SEND_EVENT, eventName);
        } catch (ProcessorNotFoundException e) {
            if (Objects.equals(e.getProcessorName(), RestApiConstants.ACTION_CAN_SEND_EVENT)) {
                return false;
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean canUseKnowledgeBase(User user, KnowledgeBase knowledgeBase) {
        try {
            return getEngine().getOperations().call(Boolean.class, RestApiConstants.ACTION_CAN_USE_KNOWLEDGE_BASE, user,
                    knowledgeBase.getName());
        } catch (ProcessorNotFoundException e) {
            if (Objects.equals(e.getProcessorName(), RestApiConstants.ACTION_CAN_USE_KNOWLEDGE_BASE)) {
                return false;
            } else {
                throw e;
            }
        }
    }
}
