package kp.company.repository;

import kp.company.domain.Employee;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * The MongoDB repository for the {@link Employee} objects.
 */
public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {
}
