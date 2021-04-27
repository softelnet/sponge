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

package org.openksavi.sponge.remoteapi.server.camel;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.camel.Exchange;
import org.apache.camel.http.common.DefaultHttpBinding;
import org.apache.camel.http.common.HttpCommonComponent;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.support.ObjectHelper;

/**
 * WARNING: This class is used only to access a protected Camel method DefaultHttpBinding.appendHeaders.
 */
public class CamelHttpBindingAsUtil extends DefaultHttpBinding {

    public void appendHeaders(Exchange exchange, HttpServletResponse httpResponse, HttpCommonComponent httpComponent) {
        HeaderFilterStrategy runningComponentHeaderFilterStrategy = httpComponent.getHttpBinding() != null
                ? httpComponent.getHttpBinding().getHeaderFilterStrategy() : getHeaderFilterStrategy();

        for (Map.Entry<String, Object> entry : exchange.getMessage().getHeaders().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // use an iterator as there can be multiple values. (must not use a delimiter)
            final Iterator<?> it = ObjectHelper.createIterator(value, null, true);
            while (it.hasNext()) {
                String headerValue = convertHeaderValueToString(exchange, it.next());
                if (headerValue != null && runningComponentHeaderFilterStrategy != null
                        && !runningComponentHeaderFilterStrategy.applyFilterToCamelHeaders(key, headerValue, exchange)) {
                    httpResponse.addHeader(key, headerValue);
                }
            }
        }
    }
}
