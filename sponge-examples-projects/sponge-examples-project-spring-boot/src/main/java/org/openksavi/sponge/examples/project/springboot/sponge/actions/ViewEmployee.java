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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.openksavi.sponge.action.ProvideArgsContext;
import org.openksavi.sponge.examples.project.springboot.model.Employee;
import org.openksavi.sponge.examples.project.springboot.service.EmployeeService;
import org.openksavi.sponge.examples.project.springboot.sponge.TypeUtils;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.type.IntegerType;
import org.openksavi.sponge.type.provided.ProvidedMeta;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;

@Component
public class ViewEmployee extends JAction {

    private final EmployeeService employeeService;

    @Autowired
    public ViewEmployee(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public void onConfigure() {
        withLabel("View employee").withArgs(
                new IntegerType("employeeId").withAnnotated().withFeature("visible", false),
                TypeUtils.createEmployeeRecordType("employee").withReadOnly().withProvided(
                        new ProvidedMeta().withValue().withOverwrite().withDependency("employeeId"))
        ).withNonCallable()
        .withFeature("visible", false).withFeature("cancelLabel", "Close").withFeature("icon", "book-open");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onProvideArgs(ProvideArgsContext context) {
        if (context.getProvide().contains("employee")) {
            Long employeeId = ((AnnotatedValue<Number>) context.getCurrent().get("employeeId")).getValue().longValue();

            Employee employee = employeeService.getById(employeeId);
            context.getProvided().put("employee", new ProvidedValue<>().withValue(TypeUtils.createEmployeeMap(employee)));
        }
    }
}
