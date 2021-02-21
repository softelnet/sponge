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

package org.openksavi.sponge.examples.project.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import org.openksavi.sponge.examples.project.springboot.model.Employee;
import org.openksavi.sponge.examples.project.springboot.model.Employee_;
import org.openksavi.sponge.examples.project.springboot.model.Equipment;
import org.openksavi.sponge.examples.project.springboot.repository.EmployeeRepository;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private static final ExampleMatcher MODEL_MATCHER =
            ExampleMatcher.matching().withIgnorePaths(Employee_.ID).withIgnorePaths(Employee_.ROLE);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository) {
        return args -> {
            save(repository, new Employee("Bilbo Baggins", "burglar", new Equipment("phone1")));
            save(repository, new Employee("Frodo Baggins", "thief", new Equipment("car1")));
        };
    }

    private void save(EmployeeRepository repository, Employee employee) {
        Example<Employee> example = Example.of(employee, MODEL_MATCHER);
        if (!repository.exists(example)) {
            log.info("Preloading " + repository.save(employee));
        }
    }
}
