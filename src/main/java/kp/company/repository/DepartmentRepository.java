package kp.company.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import kp.company.domain.Department;
import reactor.core.publisher.Flux;

/**
 * The MongoDB repository for the {@link Department} objects.
 *
 */
public interface DepartmentRepository extends ReactiveMongoRepository<Department, String> {
	/**
	 * Finds the {@link Department} by the {@link Department} name
	 * 
	 * @param name the {@link Department} name
	 * @return the {@link Flux} with the {@link Department}
	 */
	Flux<Department> findByName(String name);
}