package kp.company.repository;

import kp.company.domain.AggregateRelation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * The MongoDB repository for relations between the department and employee.
 */
public interface AggregateRelationRepository extends ReactiveMongoRepository<AggregateRelation, String> {
    /**
     * Finds the {@link AggregateRelation} by the department id.
     *
     * @param departmentId the department id
     * @return the {@link Flux} with the {@link AggregateRelation}
     */
    Flux<AggregateRelation> findByDepartmentId(String departmentId);

    /**
     * Finds the {@link AggregateRelation} by the employee id.
     *
     * @param employeeId the employee id
     * @return the {@link Flux} with the {@link AggregateRelation}
     */
    Flux<AggregateRelation> findByEmployeeId(String employeeId);
}
