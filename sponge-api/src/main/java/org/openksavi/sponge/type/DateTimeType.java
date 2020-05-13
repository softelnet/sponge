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

package org.openksavi.sponge.type;

import java.time.temporal.Temporal;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.type.provided.ProvidedMeta;

/**
 * A date/time type.
 */
public class DateTimeType extends DataType<Object> {

    /** The feature name for the dateTimeKind. */
    public static final String FEATURE_DATE_TIME_KIND = "dateTimeKind";

    public static final String DEFAULT_DATE_FORMAT = "yyyy-dd-MM";

    /** The date/time kind. */
    private DateTimeKind dateTimeKind;

    /** The minimum value (optional). Technical note: the type is Object because this class is used internally to marshaling as well. */
    private Object minValue;

    /** The maximum value (optional). Technical note: the type is Object because this class is used internally to marshaling as well. */
    private Object maxValue;

    public DateTimeType() {
        this(null);
    }

    public DateTimeType(String name) {
        this(name, DateTimeKind.DATE_TIME);
    }

    public DateTimeType(String name, DateTimeKind dateTimeKind) {
        super(DataTypeKind.DATE_TIME, name);

        this.dateTimeKind = dateTimeKind;

        updateFormat();
    }

    @Override
    public DateTimeType withName(String name) {
        return (DateTimeType) super.withName(name);
    }

    @Override
    public DateTimeType withLabel(String label) {
        return (DateTimeType) super.withLabel(label);
    }

    @Override
    public DateTimeType withDescription(String description) {
        return (DateTimeType) super.withDescription(description);
    }

    @Override
    public DateTimeType withAnnotated(boolean annotated) {
        return (DateTimeType) super.withAnnotated(annotated);
    }

    @Override
    public DateTimeType withAnnotated() {
        return (DateTimeType) super.withAnnotated();
    }

    @Override
    public DateTimeType withFormat(String format) {
        return (DateTimeType) super.withFormat(format);
    }

    @Override
    public DateTimeType withFeatures(Map<String, Object> features) {
        return (DateTimeType) super.withFeatures(features);
    }

    @Override
    public DateTimeType withFeature(String name, Object value) {
        return (DateTimeType) super.withFeature(name, value);
    }

    @Override
    public DateTimeType withDefaultValue(Object value) {
        Validate.isTrue(value == null || value instanceof Temporal, "The DateTimeType default value has to be an instance of Temporal");
        return (DateTimeType) super.withDefaultValue(value);
    }

    @Override
    public DateTimeType withNullable(boolean nullable) {
        return (DateTimeType) super.withNullable(nullable);
    }

    @Override
    public DateTimeType withNullable() {
        return (DateTimeType) super.withNullable();
    }

    @Override
    public DateTimeType withReadOnly(boolean readOnly) {
        return (DateTimeType) super.withReadOnly(readOnly);
    }

    @Override
    public DateTimeType withReadOnly() {
        return (DateTimeType) super.withReadOnly();
    }

    @Override
    public DateTimeType withOptional() {
        return (DateTimeType) super.withOptional();
    }

    @Override
    public DateTimeType withProvided(ProvidedMeta provided) {
        return (DateTimeType) super.withProvided(provided);
    }

    public DateTimeKind getDateTimeKind() {
        return dateTimeKind;
    }

    public void setDateTimeKind(DateTimeKind dateTimeKind) {
        this.dateTimeKind = dateTimeKind;
    }

    public Object getMinValue() {
        return minValue;
    }

    public void setMinValue(Object minValue) {
        this.minValue = minValue;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Object maxValue) {
        this.maxValue = maxValue;
    }

    public DateTimeType withDateTimeKind(DateTimeKind dateTimeKind) {
        setDateTimeKind(dateTimeKind);
        updateFormat();
        return this;
    }

    public DateTimeType withDateTime() {
        return withDateTimeKind(DateTimeKind.DATE_TIME);
    }

    public DateTimeType withDateTimeZone() {
        return withDateTimeKind(DateTimeKind.DATE_TIME_ZONE);
    }

    public DateTimeType withDate() {
        return withDateTimeKind(DateTimeKind.DATE);
    }

    public DateTimeType withTime() {
        return withDateTimeKind(DateTimeKind.TIME);
    }

    public DateTimeType withInstant() {
        return withDateTimeKind(DateTimeKind.INSTANT);
    }

    public DateTimeType withMinValue(Temporal minValue) {
        setMinValue(minValue);
        return this;
    }

    public DateTimeType withMaxValue(Temporal maxValue) {
        setMaxValue(maxValue);
        return this;
    }

    private void updateFormat() {
        if (getFormat() == null) {
            switch (dateTimeKind) {
            case DATE:
                setFormat(DEFAULT_DATE_FORMAT);
                break;
            default:
                break;
            }
        }
    }
}
