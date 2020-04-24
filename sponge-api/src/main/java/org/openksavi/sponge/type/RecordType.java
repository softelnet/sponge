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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.type.provided.ProvidedMeta;

/**
 * A record type. This type requires a list of named record field types. A value of this type has to be an instance of Map with elements
 * corresponding to the field names and values.
 */
@SuppressWarnings("rawtypes")
public class RecordType extends DataType<Map<String, Object>> {

    /** The field types. */
    private List<DataType> fields;

    /** The base record type. */
    private transient RecordType baseType;

    /** The flag that tells if inheritance has been applied to this type. */
    private transient boolean inheritationApplied = false;

    private transient Map<String, DataType> fieldLookupMap;

    public RecordType() {
        this((String) null);
    }

    public RecordType(String name) {
        this(name, new ArrayList<>());
    }

    public RecordType(List<DataType> fields) {
        this(null, fields);
    }

    public RecordType(String name, List<DataType> fields) {
        super(DataTypeKind.RECORD, name);
        setFields(fields);
    }

    @Override
    public RecordType withName(String name) {
        return (RecordType) super.withName(name);
    }

    @Override
    public RecordType withLabel(String label) {
        return (RecordType) super.withLabel(label);
    }

    @Override
    public RecordType withDescription(String description) {
        return (RecordType) super.withDescription(description);
    }

    @Override
    public RecordType withAnnotated(boolean annotated) {
        return (RecordType) super.withAnnotated(annotated);
    }

    @Override
    public RecordType withAnnotated() {
        return (RecordType) super.withAnnotated();
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

    @Override
    public RecordType withReadOnly(boolean readOnly) {
        return (RecordType) super.withReadOnly(readOnly);
    }

    @Override
    public RecordType withReadOnly() {
        return (RecordType) super.withReadOnly();
    }

    @Override
    public RecordType withOptional() {
        return (RecordType) super.withOptional();
    }

    @Override
    public RecordType withProvided(ProvidedMeta provided) {
        return (RecordType) super.withProvided(provided);
    }

    /**
     * Adds new fields. Replaces an already existing field that has the same name.
     *
     * @param fields the fields.
     * @return this type.
     */
    public RecordType withFields(List<DataType> fields) {
        fields.forEach(field -> {
            boolean replaced = false;
            for (int i = 0; i < this.fields.size(); i++) {
                if (Objects.equals(field.getName(), this.fields.get(i).getName())) {
                    this.fields.set(i, field);
                    replaced = true;
                }
            }

            if (!replaced) {
                this.fields.add(field);
            }
        });

        refreshFieldLookupMap();

        return this;
    }

    /**
     * Sets a base record type for this type. Used for inheritance.
     *
     * @param baseType the base record type.
     * @return this type.
     */
    public RecordType withBaseType(RecordType baseType) {
        setBaseType(baseType);
        return this;
    }

    public List<DataType> getFields() {
        return fields;
    }

    public void setFields(List<DataType> fields) {
        this.fields = fields != null ? new ArrayList<>(fields) : null;

        refreshFieldLookupMap();
    }

    public RecordType getBaseType() {
        return baseType;
    }

    public void setBaseType(RecordType baseType) {
        this.baseType = baseType;
    }

    public boolean isInheritationApplied() {
        return inheritationApplied;
    }

    public void setInheritationApplied(boolean inheritationApplied) {
        this.inheritationApplied = inheritationApplied;
    }

    protected void refreshFieldLookupMap() {
        fieldLookupMap =
                fields != null ? fields.stream().collect(Collectors.toMap(DataType::getName, Function.identity())) : new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends DataType<?>> T getFieldType(String fieldName) {
        return (T) Validate.notNull(fieldLookupMap.get(fieldName), "Field '%s' not found in the record type", fieldName);
    }

    @Override
    public RecordType clone() {
        RecordType cloned = (RecordType) super.clone();
        cloned.setFields(fields != null
                ? new ArrayList<>(fields.stream().map(field -> field != null ? field.clone() : null).collect(Collectors.toList())) : null);
        if (baseType != null) {
            cloned.baseType = baseType.clone();
        }

        return cloned;
    }
}
