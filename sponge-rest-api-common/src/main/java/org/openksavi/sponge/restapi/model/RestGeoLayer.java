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

package org.openksavi.sponge.restapi.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModel;

import org.openksavi.sponge.features.model.geo.GeoLayerType;
import org.openksavi.sponge.features.model.geo.GeoMarkerLayer;
import org.openksavi.sponge.features.model.geo.GeoTileLayer;

@ApiModel(value = "GeoLayer", description = "A geo layer")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = GeoTileLayer.class, name = GeoLayerType.CODE_TILE),
        @Type(value = GeoMarkerLayer.class, name = GeoLayerType.CODE_MARKER) })
public abstract class RestGeoLayer {
}
