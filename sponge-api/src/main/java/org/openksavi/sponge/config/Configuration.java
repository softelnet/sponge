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

package org.openksavi.sponge.config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;

/**
 * An interface representing a configuration.
 */
public interface Configuration {

    /**
     * Returns configuration name.
     *
     * @return configuration name.
     */
    String getName();

    /**
     * Sets configuration variables.
     *
     * @param variables configuration variables.
     */
    void setVariables(Properties variables);

    /**
     * Sets configuration variable.
     *
     * @param name configuration variable name.
     * @param value configuration variable value.
     */
    void setVariable(String name, Object value);

    /**
     * Returns configuration variable.
     *
     * @param name variable name.
     * @return variable value.
     */
    Object getVariable(String name);

    /**
     * Returns a child configuration. If such configuration doesn't exist, returns an empty configuration.
     *
     * @param key child configuration key.
     * @return child configuration.
     */
    Configuration getChildConfiguration(String key);

    /**
     * Returns {@code true} if there is a child configuration specified by {@code key}.
     *
     * @param key child configuration key.
     * @return {@code true} if there is a child configuration specified by {@code key}.
     */
    boolean hasChildConfiguration(String key);

    /**
     * Returns all child configurations of the node specified by {@code key}.
     *
     * @param key key of the parent node.
     * @return child configurations.
     */
    Configuration[] getChildConfigurationsOf(String key);

    /**
     * Returns all sub configurations specified by {@code key}.
     *
     * @param key key of the returned nodes.
     * @return sub configurations.
     */
    Configuration[] getConfigurationsAt(String key);

    /**
     * Returns string value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return string value.
     */
    String getString(String key, String defaultValue);

    /**
     * Returns integer value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return integer value.
     */
    Integer getInteger(String key, Integer defaultValue);

    /**
     * Returns long value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return long value.
     */
    Long getLong(String key, Long defaultValue);

    /**
     * Returns boolean value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return boolean value.
     */
    Boolean getBoolean(String key, Boolean defaultValue);

    /**
     * Returns byte value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return byte value.
     */
    Byte getByte(String key, Byte defaultValue);

    /**
     * Returns double value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return double value.
     */
    Double getDouble(String key, Double defaultValue);

    /**
     * Returns float value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return float value.
     */
    Float getFloat(String key, Float defaultValue);

    /**
     * Returns short value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return short value.
     */
    Short getShort(String key, Short defaultValue);

    /**
     * Returns big decimal value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return big decimal value.
     */
    BigDecimal getBigDecimal(String key, BigDecimal defaultValue);

    /**
     * Returns big integer value.
     *
     * @param key configuration key.
     * @param defaultValue default value.
     * @return big integer value.
     */
    BigInteger getBigInteger(String key, BigInteger defaultValue);

    /**
     * Returns configuration value.
     *
     * @return configuration value.
     */
    String getValue();

    /**
     * Returns configuration value.
     *
     * @param defaultValue default value.
     * @return configuration value.
     */
    String getValue(String defaultValue);

    /**
     * Returns attribute value.
     *
     * @param name attribute name.
     * @param defaultValue default value.
     * @return attribute value.
     */
    String getAttribute(String name, String defaultValue);
}
