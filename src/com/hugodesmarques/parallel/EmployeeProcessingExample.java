package com.hugodesmarques.parallel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeProcessingExample {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("Alice", "Engineering", 120000),
                new Employee("Bob", "Engineering", 110000),
                new Employee("Charlie", "HR", 90000),
                new Employee("David", "HR", 95000),
                new Employee("Eve", "Marketing", 105000),
                new Employee("Frank", "Engineering", 115000)
        );

        double salaryThreshold = 100000;

        // Usando ParallelStream para filtrar, mapear e agrupar funcionários
        Map<String, List<EmployeeDTO>> employeesByDepartment = employees.parallelStream()
                .filter(employee -> employee.salary() > salaryThreshold)
                .map(employee -> new EmployeeDTO(employee.name(), employee.department()))
                .collect(Collectors.groupingBy(EmployeeDTO::department));

        // Imprimindo o resultado
        employeesByDepartment.forEach((department, employeeList) -> {
            System.out.println("Department: " + department);
            employeeList.forEach(employeeDTO -> System.out.println("  " + employeeDTO));
        });
    }

    record EmployeeDTO(String name, String department) {}

    record Employee(String name, String department, double salary) {}
}