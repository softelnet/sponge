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

package org.openksavi.sponge.features.model.geo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openksavi.sponge.SpongeException;

/**
 * A geo CRS (Coordinate Reference Systems).
 */
public class GeoCrs implements Serializable, Cloneable {

    private static final long serialVersionUID = 4203026325077705441L;

    private String code;

    private String projection;

    private List<Double> resolutions = new ArrayList<>();

    public GeoCrs(String code, String projection) {
        this.code = code;
        this.projection = projection;
    }

    public GeoCrs(String code) {
        this(code, null);
    }

    public GeoCrs() {
    }

    public GeoCrs withCode(String code) {
        setCode(code);
        return this;
    }

    public GeoCrs withProjection(String projection) {
        setProjection(projection);
        return this;
    }

    // List of Numbers instead of Doubles to support Integer arrays in Jython.
    public GeoCrs withResolutions(List<Number> resolutions) {
        this.resolutions.addAll(resolutions.stream().map(r -> r != null ? r.doubleValue() : null).collect(Collectors.toList()));
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProjection() {
        return projection;
    }

    public void setProjection(String projection) {
        this.projection = projection;
    }

    public List<Double> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<Double> resolutions) {
        this.resolutions = resolutions;
    }

    @Override
    public GeoCrs clone() {
        try {
            GeoCrs cloned = (GeoCrs) super.clone();

            cloned.resolutions = resolutions != null ? new ArrayList<>(resolutions) : null;

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }
}
