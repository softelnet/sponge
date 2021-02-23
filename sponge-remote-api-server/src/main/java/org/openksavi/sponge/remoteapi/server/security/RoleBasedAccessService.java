/*
 * Copyright 2016-2021 The Sponge authors.
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.remoteapi.server.util.RemoteApiServerUtils;

public class RoleBasedAccessService extends BaseAccessService {

    private Map<String, Collection<String>> rolesToKb = Collections.synchronizedMap(new LinkedHashMap<>());

    private Map<String, Collection<String>> rolesToSendEvent = Collections.synchronizedMap(new LinkedHashMap<>());

    private Map<String, Collection<String>> rolesToSubscribeEvent = Collections.synchronizedMap(new LinkedHashMap<>());

    public RoleBasedAccessService() {
    }

    public Map<String, Collection<String>> getRolesToKb() {
        return rolesToKb;
    }

    public void setRolesToKb(Map<String, Collection<String>> rolesToKb) {
        this.rolesToKb = rolesToKb;
    }

    public Map<String, Collection<String>> getRolesToSendEvent() {
        return rolesToSendEvent;
    }

    public void setRolesToSendEvent(Map<String, Collection<String>> rolesToSendEvent) {
        this.rolesToSendEvent = rolesToSendEvent;
    }

    public Map<String, Collection<String>> getRolesToSubscribeEvent() {
        return rolesToSubscribeEvent;
    }

    public void setRolesToSubscribeEvent(Map<String, Collection<String>> rolesToSubscribeEvent) {
        this.rolesToSubscribeEvent = rolesToSubscribeEvent;
    }

    @Override
    public boolean canCallAction(UserContext userContext, ActionAdapter actionAdapter) {
        return canUseKnowledgeBase(userContext, actionAdapter.getKnowledgeBase());
    }

    @Override
    public boolean canSendEvent(UserContext userContext, String eventName) {
        return RemoteApiServerUtils.canAccessResource(rolesToSendEvent, userContext, eventName);
    }

    @Override
    public boolean canSubscribeEvent(UserContext userContext, String eventName) {
        return RemoteApiServerUtils.canAccessResource(rolesToSubscribeEvent, userContext, eventName);
    }

    @Override
    public boolean canUseKnowledgeBase(UserContext userContext, KnowledgeBase knowledgeBase) {
        return RemoteApiServerUtils.canAccessResource(rolesToKb, userContext, knowledgeBase.getName());
    }

    public void addRolesToKb(Map<String, Collection<String>> rolesToKb) {
        this.rolesToKb.putAll(rolesToKb);
    }

    public void addRolesToSendEvent(Map<String, Collection<String>> rolesToSendEvent) {
        this.rolesToSendEvent.putAll(rolesToSendEvent);
    }

    public void addRolesToSubscribeEvent(Map<String, Collection<String>> rolesToSubscribeEvent) {
        this.rolesToSubscribeEvent.putAll(rolesToSubscribeEvent);
    }
}
