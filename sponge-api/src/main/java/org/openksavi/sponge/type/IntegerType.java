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

package org.openksavi.sponge.type;

import java.util.Map;

/**
 * An integer type that supports long as well (internally uses {@code Number}).
 */
public class IntegerType extends DataType<Number> {

    /** The minimum value (optional). */
    private Long minValue;

    /** The maximum value (optional). */
    private Long maxValue;

    /** Tells if the minimum value should be exclusive. Defaults to {@code false}. */
    private boolean exclusiveMin = false;

    /** Tells if the maximum value should be exclusive. Defaults to {@code false}. */
    private boolean exclusiveMax = false;

    public IntegerType() {
        super(DataTypeKind.INTEGER);
    }

    @Override
    public IntegerType withFormat(String format) {
        return (IntegerType) super.withFormat(format);
    }

    @Override
    public IntegerType withFeatures(Map<String, Object> features) {
        return (IntegerType) super.withFeatures(features);
    }

    @Override
    public IntegerType withFeature(String name, Object value) {
        return (IntegerType) super.withFeature(name, value);
    }

    @Override
    public IntegerType withDefaultValue(Number value) {
        return (IntegerType) super.withDefaultValue(value);
    }

    @Override
    public IntegerType withNullable(boolean nullable) {
        return (IntegerType) super.withNullable(nullable);
    }

    @Override
    public IntegerType withNullable() {
        return (IntegerType) super.withNullable();
    }

    public IntegerType withMinValue(Long minValue) {
        setMinValue(minValue);
        return this;
    }

    public IntegerType withMinValue(Long minValue, boolean exclusiveMin) {
        setMinValue(minValue);
        setExclusiveMin(exclusiveMin);
        return this;
    }

    public IntegerType withMaxValue(Long maxValue) {
        setMaxValue(maxValue);
        return this;
    }

    public IntegerType withMaxValue(Long maxValue, boolean exclusiveMax) {
        setMaxValue(maxValue);
        setExclusiveMax(exclusiveMax);
        return this;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isExclusiveMin() {
        return exclusiveMin;
    }

    public void setExclusiveMin(boolean exclusiveMin) {
        this.exclusiveMin = exclusiveMin;
    }

    public boolean isExclusiveMax() {
        return exclusiveMax;
    }

    public void setExclusiveMax(boolean exclusiveMax) {
        this.exclusiveMax = exclusiveMax;
    }
}
