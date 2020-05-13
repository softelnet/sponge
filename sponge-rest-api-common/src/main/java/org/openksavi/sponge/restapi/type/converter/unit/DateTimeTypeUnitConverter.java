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

package org.openksavi.sponge.restapi.type.converter.unit;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.DateTimeType;

/**
 * Date/time values are serialized using a data type format or to ISO strings if a format is not defined.
 */
public class DateTimeTypeUnitConverter<T> extends BaseUnitTypeConverter<Object, DateTimeType> {

    public DateTimeTypeUnitConverter() {
        super(DataTypeKind.DATE_TIME);
    }

    @Override
    public Object marshal(TypeConverter converter, DateTimeType type, Object value) {
        switch (type.getDateTimeKind()) {
        case INSTANT:
            // Formatter not used.
            return ((Instant) value).toString();
        default:
            return getFormatter(type).format((TemporalAccessor) value);
        }
    }

    @Override
    public Object unmarshal(TypeConverter converter, DateTimeType type, Object value) {
        String stringValue = value.toString();
        switch (type.getDateTimeKind()) {
        case DATE_TIME:
            return LocalDateTime.parse(stringValue, getFormatter(type));
        case DATE_TIME_ZONE:
            return ZonedDateTime.parse(stringValue, getFormatter(type));
        case DATE:
            return LocalDate.parse(stringValue, getFormatter(type));
        case TIME:
            return LocalTime.parse(stringValue, getFormatter(type));
        case INSTANT:
            // Formatter not used.
            return Instant.parse(stringValue);
        default:
            throw new SpongeException("Unsupported DateTime kind " + type.getDateTimeKind());
        }
    }

    protected static DateTimeFormatter getFormatter(DateTimeType type) {
        if (type.getFormat() != null) {
            return DateTimeFormatter.ofPattern(type.getFormat());
        }

        switch (type.getDateTimeKind()) {
        case DATE_TIME:
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        case DATE_TIME_ZONE:
            return DateTimeFormatter.ISO_ZONED_DATE_TIME;
        case DATE:
            return DateTimeFormatter.ISO_LOCAL_DATE;
        case TIME:
            return DateTimeFormatter.ISO_LOCAL_TIME;
        case INSTANT:
            return DateTimeFormatter.ISO_INSTANT;
        default:
            throw new SpongeException("Unsupported DateTime kind " + type.getDateTimeKind());
        }
    }
}
