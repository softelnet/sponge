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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge Base. Library use.
 */
public class KnowledgeBaseLibrary extends JKnowledgeBase {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseLibrary.class);

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("hostStatus", Collections.synchronizedMap(new HashMap<String, String>()));
    }

    protected static String checkPageStatus(String host) {
        try {
            logger.info("Trying {}...", host);
            HttpURLConnection connection = ((HttpURLConnection) new URL("https://" + host).openConnection());
            connection.setRequestMethod("GET");
            connection.connect();
            logger.info("Host {} status: {}", host, connection.getResponseCode());
            return String.valueOf(connection.getResponseCode());
        } catch (Exception e) {
            logger.debug("Host {} error: {}", host, e);
            return "ERROR";
        }
    }

    public static class HttpStatusTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("checkStatus");
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onRun(Event event) {
            String status = checkPageStatus(event.get(String.class, "host"));
            getSponge().getVariable(Map.class, "hostStatus").put(event.get("host"), status);
        }
    }

    @Override
    public void onStartup() {
        getSponge().event("checkStatus").set("host", "www.wikipedia.org.unknown").send();
        getSponge().event("checkStatus").set("host", "www.wikipedia.org").send();
    }
}
