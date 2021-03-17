/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.util;

import org.apache.commons.fileupload.FileItemIterator;

public class FormDataMultiPartContext {

    private String json;

    private FileItemIterator fileItemIterator;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public FileItemIterator getFileItemIterator() {
        return fileItemIterator;
    }

    public void setFileItemIterator(FileItemIterator fileItemIterator) {
        this.fileItemIterator = fileItemIterator;
    }
}
