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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.openksavi.sponge.action.ProvideArgsParameters;
import org.openksavi.sponge.core.engine.ConfigurationConstants;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.examples.project.springboot.model.Employee;
import org.openksavi.sponge.examples.project.springboot.service.EmployeeService;
import org.openksavi.sponge.remoteapi.client.DefaultSpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.type.value.AnnotatedValue;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class SpongeAppTest {

    static {
        System.setProperty(ConfigurationConstants.PROP_HOME, ".");
    }

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private EmployeeService service;

    @Test
    public void queryByName() {
        List<Employee> employees = service.queryByName("%Frodo%");
        assertEquals(1, employees.size());
    }

    @Test
    public void queryByNameError() {
        List<Employee> employees = service.queryByName("%");
        assertNotEquals(1, employees.size());
    }

    @Test
    public void findByNameLike() {
        List<Employee> employees = service.findByNameLike("%Frodo%");
        assertEquals(1, employees.size());
    }

    @SuppressWarnings({ "unchecked"})
    @Test
    public void queryByNameRemote() {
        try (SpongeClient client = createRemoteClient()) {
            ProvideArgsParameters employeesParams = new ProvideArgsParameters(Arrays.asList("employees"));

            List<AnnotatedValue<Map<String, Object>>> employees = (List<AnnotatedValue<Map<String, Object>>>) client.provideActionArgs("ListEmployees",
                    employeesParams).get("employees").getValue();
            assertEquals(2, employees.size());

            Number id0 = (Number) employees.get(0).getValue();

            ProvideArgsParameters employeParams0 = new ProvideArgsParameters(Arrays.asList("employee"), Lists.emptyList(),
                    SpongeUtils.immutableMapOf("employeeId", id0));

            Map<String, Object> employee0 = (Map<String, Object>) client.provideActionArgs("ViewEmployee",
                    employeParams0).get("employee").getValue();

            assertEquals("Bilbo Baggins", employee0.get("name"));
        }
    }

    @Test
    public void getVersionAdmin() {
        try (SpongeClient client = createRemoteClientAdmin()) {
            assertNotNull(client.getVersion());
        }
    }

    protected SpongeClient createRemoteClient() {
        return new DefaultSpongeClient(SpongeClientConfiguration.builder().url(String.format("%s/sponge", template.getRootUri())).build());
    }

    protected SpongeClient createRemoteClientAdmin() {
        return new DefaultSpongeClient(SpongeClientConfiguration.builder().url(String.format("%s/sponge", template.getRootUri()))
                .username("admin").password("password").build());
    }
}
