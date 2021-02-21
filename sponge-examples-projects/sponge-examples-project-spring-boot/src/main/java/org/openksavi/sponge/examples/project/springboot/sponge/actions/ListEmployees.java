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
package org.openksavi.sponge.examples.project.springboot.sponge.actions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.openksavi.sponge.action.ProvideArgsContext;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.examples.project.springboot.model.Employee;
import org.openksavi.sponge.examples.project.springboot.service.EmployeeService;
import org.openksavi.sponge.features.model.SubAction;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.type.IntegerType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.provided.ProvidedMeta;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;

@Component
public class ListEmployees extends JAction {

    private final EmployeeService employeeService;

    @Autowired
    public ListEmployees(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public void onConfigure() {
        withLabel("Employees").withArgs(
            new StringType("search").withNullable().withLabel("Search").withFeature("responsive", true),
            new ListType<Number>("employees").withLabel("Employees").withElement(new IntegerType("employee").withAnnotated()).withFeatures(
                    SpongeUtils.immutableMapOf(
                    "createAction", new SubAction("CreateEmployee"),
                    "readAction", new SubAction("ViewEmployee").withArg("employeeId", "@this"),
                    "updateAction", new SubAction("UpdateEmployee").withArg("employeeId", "@this"),
                    "deleteAction", new SubAction("DeleteEmployee").withArg("employeeId", "@this"),
                    "refreshable", true
                )).withProvided(new ProvidedMeta().withValue().withOverwrite().withDependencies("search"))
        ).withNonCallable().withFeature("icon", "library");
    }

    @Override
    public void onProvideArgs(ProvideArgsContext context) {
        if (context.getProvide().contains("employees")) {
            String search = (String) context.getCurrent().get("search");
            List<Employee> employees = search != null ? employeeService.findByNameLike("%" + search + "%") : employeeService.all();
            List<AnnotatedValue<Long>> employeesArg = employees.stream()
                    .map(employee -> new AnnotatedValue<>(employee.getId()).withValueLabel(employee.getName()))
                    .collect(Collectors.toList());
            context.getProvided().put("employees", new ProvidedValue<>().withValue(employeesArg));
        }
    }
}
