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

package org.openksavi.sponge.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionInfo {

    private static final String VERSION_PROPERTY = "version";

    private static final String VERSION_PROPERTIES = "/org/openksavi/sponge/version.properties";

    private static final String DESCRIPTION = "Sponge";

    private String version;

    /**
     * Returns the engine version.
     *
     * @return the engine version.
     */
    public String getVersion() {
        if (version == null) {
            synchronized (this) {
                version = readVersion();
            }
        }

        return version;
    }

    private String readVersion() {
        final Properties properties = new Properties();
        try (final InputStream stream = getClass().getResourceAsStream(VERSION_PROPERTIES)) {
            properties.load(stream);
        } catch (IOException e) {
            //
        }

        return properties.getProperty(VERSION_PROPERTY);
    }

    /**
     * Returns the engine description.
     *
     * @return the engine description.
     */
    public String getDescription() {
        return DESCRIPTION + " " + getVersion();
    }
}
