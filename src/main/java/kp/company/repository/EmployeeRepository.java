package kp.company.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import kp.company.domain.Employee;

/**
 * The MongoDB repository for the {@link Employee} objects.
 *
 */
public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {
}
