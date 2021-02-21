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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.openksavi.sponge.examples.project.springboot.model.Employee;
import org.openksavi.sponge.examples.project.springboot.model.EmployeeNotFoundException;
import org.openksavi.sponge.examples.project.springboot.model.Employee_;
import org.openksavi.sponge.examples.project.springboot.repository.EmployeeRepository;

@Service
public class DefaultEmployeeService implements EmployeeService {

    private final EmployeeRepository repository;

    private final EntityManager em;

    public DefaultEmployeeService(EmployeeRepository repository, EntityManager em) {
        this.repository = repository;
        this.em = em;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Employee> all() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public Employee createNew(Employee newEmployee) {
        return repository.save(newEmployee);
    }

    @Transactional(readOnly = true)
    @Override
    public Employee getById(Long id) {
        return repository.findById(id) //
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Transactional
    @Override
    public Employee update(Long id, Employee newEmployee) {
        return repository.findById(id).map(employee -> {
            employee.setName(newEmployee.getName());
            employee.setRole(newEmployee.getRole());
            return repository.save(employee);
        }).orElseGet(() -> {
            newEmployee.setId(id);
            return repository.save(newEmployee);
        });
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Employee> queryByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> employee = cq.from(Employee.class);
        employee.fetch(Employee_.EQUIPMENT);

        ParameterExpression<String> paramName = cb.parameter(String.class);
        cq.where(cb.like(employee.get(Employee_.NAME), paramName));
        cq.select(employee);

        TypedQuery<Employee> query = em.createQuery(cq);
        query.setParameter(paramName, name);

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Employee> findByNameLike(String name) {
        return repository.findByNameLikeIgnoreCase(name);
    }
}
