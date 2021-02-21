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

package org.openksavi.sponge.examples.project.springboot.service;

import java.util.List;

import org.openksavi.sponge.examples.project.springboot.model.Employee;

public interface EmployeeService {

    List<Employee> all();

    Employee createNew(Employee newEmployee);

    Employee getById(Long id);

    Employee update(Long id, Employee newEmployee);

    void delete(Long id);

    List<Employee> queryByName(String name);

    List<Employee> findByNameLike(String name);
}
