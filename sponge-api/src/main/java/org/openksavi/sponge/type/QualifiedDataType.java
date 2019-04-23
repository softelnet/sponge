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

import org.openksavi.sponge.SpongeConstants;

/**
 * A qualified data type.
 */
public class QualifiedDataType<T> {

    /** The qualified name path. Can be {@code null} for the root type or a path that has at least one element unnamed. */
    private String path;

    /** The type. */
    private DataType<T> type;

    /** The flag that informs if this qualified type is a root. */
    private boolean isRoot;

    public QualifiedDataType(String path, DataType<T> type, boolean isRoot) {
        this.path = path;
        this.type = type;
        this.isRoot = isRoot;
    }

    public QualifiedDataType(String path, DataType<T> type) {
        this(path, type, true);
    }

    public QualifiedDataType(DataType<T> type) {
        this(null, type);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DataType<T> getType() {
        return type;
    }

    public void setType(DataType<T> type) {
        this.type = type;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public <C> QualifiedDataType<C> createChild(DataType<C> childType) {
        String parentPath = path != null ? path + SpongeConstants.ACTION_SUB_ARG_SEPARATOR : (isRoot ? "" : null);

        return new QualifiedDataType<>(parentPath != null && childType.getName() != null ? parentPath + childType.getName() : null,
                childType, false);
    }
}
