/*
 * Copyright 2016-2020 The Sponge authors.
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

package org.openksavi.sponge.features.model.ui;

import java.io.Serializable;

/**
 * An icon info.
 */
public class IconInfo implements Serializable {

    private static final long serialVersionUID = -9096035775039112799L;

    private String name;

    private String color;

    private Double size;

    private String url;

    public IconInfo() {
    }

    public IconInfo withName(String name) {
        setName(name);
        return this;
    }

    public IconInfo withColor(String color) {
        setColor(color);
        return this;
    }

    public IconInfo withSize(Double size) {
        setSize(size);
        return this;
    }

    public IconInfo withUrl(String url) {
        setUrl(url);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
