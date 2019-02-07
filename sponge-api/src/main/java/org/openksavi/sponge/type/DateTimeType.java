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

/**
 * A date/time type.
 */
public class DateTimeType extends DataType<Object> {

    /** The date/time kind. */
    private DateTimeKind dateTimeKind;

    public DateTimeType() {
        this(DateTimeKind.DATE_TIME);
    }

    public DateTimeType(DateTimeKind dateTimeKind) {
        super(DataTypeKind.DATE_TIME);

        this.dateTimeKind = dateTimeKind;
    }

    @Override
    public DateTimeType format(String format) {
        return (DateTimeType) super.format(format);
    }

    @Override
    public DateTimeType features(Map<String, Object> features) {
        return (DateTimeType) super.features(features);
    }

    @Override
    public DateTimeType feature(String name, Object value) {
        return (DateTimeType) super.feature(name, value);
    }

    @Override
    public DateTimeType defaultValue(Object value) {
        return (DateTimeType) super.defaultValue(value);
    }

    @Override
    public DateTimeType nullable(boolean nullable) {
        return (DateTimeType) super.nullable(nullable);
    }

    public DateTimeKind getDateTimeKind() {
        return dateTimeKind;
    }

    public void setDateTimeKind(DateTimeKind dateTimeKind) {
        this.dateTimeKind = dateTimeKind;
    }

    public DateTimeType dateTime() {
        setDateTimeKind(DateTimeKind.DATE_TIME);
        return this;
    }

    public DateTimeType dateTimeZone() {
        setDateTimeKind(DateTimeKind.DATE_TIME_ZONE);
        return this;
    }

    public DateTimeType date() {
        setDateTimeKind(DateTimeKind.DATE);
        return this;
    }

    public DateTimeType time() {
        setDateTimeKind(DateTimeKind.TIME);
        return this;
    }

    public DateTimeType instant() {
        setDateTimeKind(DateTimeKind.INSTANT);
        return this;
    }
}
