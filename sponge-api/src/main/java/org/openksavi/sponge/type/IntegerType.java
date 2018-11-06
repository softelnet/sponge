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
public class IntegerType extends Type<Number> {

    private Long minValue;

    private Long maxValue;

    private boolean exclusiveMin = false;

    private boolean exclusiveMax = false;

    public IntegerType() {
        super(TypeKind.INTEGER);
    }

    @Override
    public IntegerType format(String format) {
        return (IntegerType) super.format(format);
    }

    @Override
    public IntegerType features(Map<String, Object> features) {
        return (IntegerType) super.features(features);
    }

    @Override
    public IntegerType feature(String name, Object value) {
        return (IntegerType) super.feature(name, value);
    }

    @Override
    public IntegerType defaultValue(Number value) {
        return (IntegerType) super.defaultValue(value);
    }

    @Override
    public IntegerType nullable(boolean nullable) {
        return (IntegerType) super.nullable(nullable);
    }

    public IntegerType minValue(Long minValue) {
        setMinValue(minValue);
        return this;
    }

    public IntegerType maxValue(Long maxValue) {
        setMaxValue(maxValue);
        return this;
    }

    public IntegerType exclusiveMin(boolean exclusiveMin) {
        setExclusiveMin(exclusiveMin);
        return this;
    }

    public IntegerType exclusiveMax(boolean exclusiveMax) {
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
