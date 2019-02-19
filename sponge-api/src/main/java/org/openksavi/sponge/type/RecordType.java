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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A record type. This type requires a list of record field types. A value of this type has to be an instance of Map<String, Object> with
 * elements corresponding to the field names and values.
 */
public class RecordType extends DataType<Map<String, Object>> {

    /** The record type name. */
    private String name;

    /** The field types. */
    private List<RecordTypeField> fields;

    protected RecordType() {
        this(Collections.emptyList());
    }

    public RecordType(List<RecordTypeField> fields) {
        this(null, fields);
    }

    public RecordType(String name, List<RecordTypeField> fields) {
        super(DataTypeKind.RECORD);
        setName(name);
        setFields(fields);
    }

    @Override
    public RecordType withFormat(String format) {
        return (RecordType) super.withFormat(format);
    }

    @Override
    public RecordType withFeatures(Map<String, Object> features) {
        return (RecordType) super.withFeatures(features);
    }

    @Override
    public RecordType withFeature(String name, Object value) {
        return (RecordType) super.withFeature(name, value);
    }

    @Override
    public RecordType withDefaultValue(Map<String, Object> value) {
        return (RecordType) super.withDefaultValue(value);
    }

    @Override
    public RecordType withNullable(boolean nullable) {
        return (RecordType) super.withNullable(nullable);
    }

    @Override
    public RecordType withNullable() {
        return (RecordType) super.withNullable();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RecordTypeField> getFields() {
        return fields;
    }

    public void setFields(List<RecordTypeField> fields) {
        this.fields = new ArrayList<>(fields);
    }
}
