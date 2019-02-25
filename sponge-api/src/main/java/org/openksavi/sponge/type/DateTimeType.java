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

import java.util.Map;

import org.openksavi.sponge.type.provided.ProvidedMeta;

/**
 * A date/time type.
 */
public class DateTimeType extends DataType<Object> {

    /** The date/time kind. */
    private DateTimeKind dateTimeKind;

    public DateTimeType() {
        this(null);
    }

    public DateTimeType(String name) {
        this(name, DateTimeKind.DATE_TIME);
    }

    public DateTimeType(String name, DateTimeKind dateTimeKind) {
        super(DataTypeKind.DATE_TIME, name);

        this.dateTimeKind = dateTimeKind;
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

    public DateTimeType withDateTime() {
        setDateTimeKind(DateTimeKind.DATE_TIME);
        return this;
    }

    public DateTimeType withDateTimeZone() {
        setDateTimeKind(DateTimeKind.DATE_TIME_ZONE);
        return this;
    }

    public DateTimeType withDate() {
        setDateTimeKind(DateTimeKind.DATE);
        return this;
    }

    public DateTimeType withTime() {
        setDateTimeKind(DateTimeKind.TIME);
        return this;
    }

    public DateTimeType withInstant() {
        setDateTimeKind(DateTimeKind.INSTANT);
        return this;
    }
}
