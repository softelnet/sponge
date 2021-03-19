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

import java.util.Objects;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.lang3.StringUtils;

import org.openksavi.sponge.core.util.SpongeUtils;

public class FormDataMultiPartContext {

    private String json;

    private FileItemIterator fileItemIterator;

    private FileItemStream currentItem;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setFileItemIterator(FileItemIterator fileItemIterator) {
        this.fileItemIterator = fileItemIterator;
    }

    public boolean hasNextItem(String name) {
        try {

            if (currentItem == null) {
                do {
                    if (!fileItemIterator.hasNext()) {
                        return false;
                    }

                    currentItem = fileItemIterator.next();
                } while (StringUtils.isBlank(currentItem.getName())); // Skip empty files.
            }

            return itemMatches(currentItem, name);
        } catch (Exception e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    private boolean itemMatches(FileItemStream item, String name) {
        return Objects.equals(currentItem.getFieldName(), name) && !StringUtils.isBlank(currentItem.getName());
    }

    public FileItemStream nextItem(String name) {
        try {
            FileItemStream item;

            if (currentItem == null) {
                currentItem = fileItemIterator.next();
            }

            if (!itemMatches(currentItem, name)) {
                throw new IllegalStateException("There are no more items for " + name);
            }

            item = currentItem;
            currentItem = null;

            return item;
        } catch (Exception e) {
            throw SpongeUtils.wrapException(e);
        }
    }
}
