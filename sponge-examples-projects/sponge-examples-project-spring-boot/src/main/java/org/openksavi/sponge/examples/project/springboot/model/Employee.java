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

package org.openksavi.sponge.examples.project.springboot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_employee")
    @SequenceGenerator(name = "seq_employee", allocationSize = 2)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    private String role;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Equipment> equipment = new ArrayList<>();

    protected Employee() {
    }

    public Employee(String name, String role, Equipment... equipment) {
        this.name = name;
        this.role = role;

        addEquipment(equipment);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void addEquipment(Equipment... equipment) {
        List<Equipment> newEquipment = Arrays.asList(equipment);
        newEquipment.forEach(e -> e.setEmployee(this));

        this.equipment.addAll(newEquipment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(name, employee.name) && Objects.equals(role, employee.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, role);
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + id + ", name='" + name + '\'' + ", role='" + role + '\'' + ", equipment='" + equipment + '\'' + '}';
    }
}
