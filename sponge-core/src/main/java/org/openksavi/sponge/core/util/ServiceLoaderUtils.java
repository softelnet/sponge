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

package org.openksavi.sponge.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Utility methods for service loading.
 */
public class ServiceLoaderUtils {

    /**
     * Loads service implementation using Java {@link ServiceLoader}. Returns {@code null} if no implementation has been found.
     *
     * @param serviceClass
     *            service interface.
     * @return service implementation.
     * @param <T>
     *            service.
     */
    public static <T> T loadService(Class<T> serviceClass) {
        return loadService(serviceClass, false);
    }

    /**
     * Loads service implementation using Java {@link ServiceLoader}.
     *
     * @param serviceClass
     *            service interface.
     * @param required
     *            if {@code true} and there is no implementation found, throws {@link IllegalArgumentException}.
     *            Otherwise returns {@code null}.
     * @return service implementation.
     * @param <T>
     *            service.
     */
    public static <T> T loadService(Class<T> serviceClass, boolean required) {
        List<T> result = loadServices(serviceClass);

        if (result.isEmpty()) {
            if (required) {
                throw new IllegalArgumentException("No ServiceLoader configuration for " + serviceClass.getName() + " has been found");
            } else {
                return null;
            }
        } else if (result.size() > 1) {
            throw new IllegalArgumentException(
                    "More than one ServiceLoader configuration for " + serviceClass.getName() + " has been found");
        }

        return result.get(0);
    }

    /**
     * Loads service implementations using Java {@link ServiceLoader}.
     *
     * @param serviceClass
     *            service interface.
     * @return service implementations.
     * @param <T>
     *            service.
     */
    public static <T> List<T> loadServices(Class<T> serviceClass) {
        Iterator<T> iterator = ServiceLoader.load(serviceClass).iterator();

        List<T> result = new ArrayList<>();

        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
    }
}
