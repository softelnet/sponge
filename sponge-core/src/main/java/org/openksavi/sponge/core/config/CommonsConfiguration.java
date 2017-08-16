/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.core.config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;

import org.openksavi.sponge.config.Configuration;

/**
 * This class implements a configuration using Apache Commons Configuration.
 */
public class CommonsConfiguration implements Configuration {

    private BaseHierarchicalConfiguration config;

    public CommonsConfiguration(BaseHierarchicalConfiguration config) {
        this.config = config;
    }

    public BaseHierarchicalConfiguration getInternalConfiguration() {
        return config;
    }

    @Override
    public void setVariables(Properties variables) {
        variables.forEach((name, value) -> config.setProperty((String) name, value));
    }

    @Override
    public void setVariable(String name, Object value) {
        config.setProperty(name, value);
    }

    @Override
    public Object getVariable(String name) {
        return config.getProperty(name);
    }

    @Override
    public String getName() {
        return config.getRootElementName();
    }

    @Override
    public CommonsConfiguration getChildConfiguration(String child) {
        return new CommonsConfiguration(
                hasChildConfiguration(child) ? (BaseHierarchicalConfiguration) config.configurationAt(child) : new CombinedConfiguration());
    }

    @Override
    public boolean hasChildConfiguration(String key) {
        return !config.configurationsAt(key).isEmpty();
    }

    @Override
    public CommonsConfiguration[] getChildConfigurationsOf(String key) {
        return createConfigurations(config.childConfigurationsAt(key));
    }

    @Override
    public CommonsConfiguration[] getConfigurationsAt(String key) {
        return createConfigurations(config.configurationsAt(key));
    }

    protected CommonsConfiguration[] createConfigurations(List<HierarchicalConfiguration<ImmutableNode>> subs) {
        CommonsConfiguration[] result = new CommonsConfiguration[subs.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = new CommonsConfiguration((BaseHierarchicalConfiguration) subs.get(i));
        }

        return result;
    }

    @Override
    public String getValue() {
        return config.getString(".");
    }

    @Override
    public String getValue(String defaultValue) {
        return config.getString(".", defaultValue);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return config.getInteger(key, defaultValue);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return config.getLong(key, defaultValue);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return config.getBoolean(key, defaultValue);
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return config.getByte(key, defaultValue);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return config.getDouble(key, defaultValue);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return config.getFloat(key, defaultValue);
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return config.getShort(key, defaultValue);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return config.getBigDecimal(key, defaultValue);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return config.getBigInteger(key, defaultValue);
    }

    @Override
    public String getAttribute(String name, String defaultValue) {
        return config.getString("[@" + name + "]", defaultValue);
    }

    @Override
    public String toString() {
        return config.getRootElementName();
    }
}
