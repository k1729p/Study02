package kp.company.repository;

import kp.company.domain.Department;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * The MongoDB repository for the {@link Department} objects.
 */
public interface DepartmentRepository extends ReactiveMongoRepository<Department, String> {
    /**
     * Finds the {@link Department} by the name.
     *
     * @param name the name of the {@link Department}
     * @return the {@link Flux} with the {@link Department}
     */
    Flux<Department> findByName(String name);
}