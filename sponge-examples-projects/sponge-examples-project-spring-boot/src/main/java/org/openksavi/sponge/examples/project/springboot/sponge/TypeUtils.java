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

package org.openksavi.sponge.examples.project.springboot.sponge;

import java.util.Map;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.examples.project.springboot.model.Employee;
import org.openksavi.sponge.type.IntegerType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.StringType;

public final class TypeUtils {

    public static RecordType createEmployeeRecordType(String name) {
        return new RecordType(name).withFields(
                new IntegerType("id").withLabel("ID").withNullable().withFeature("visible", false),
                new StringType("name").withLabel("Name"),
                new StringType("role").withLabel("Role"));
    }

    public static Map<String, Object> createEmployeeMap(Employee employee) {
        return SpongeUtils.immutableMapOf("id", employee.getId(), "name", employee.getName(), "role", employee.getRole());
    }

    public static Employee createEmployeeFromMap(Map<String, Object> employeeMap) {
        Employee employee = new Employee((String) employeeMap.get("name"), (String) employeeMap.get("role"));

        if (employeeMap.containsKey("id")) {
            employee.setId(((Number) employeeMap.get("id")).longValue());
        }

        return employee;
    }
}
