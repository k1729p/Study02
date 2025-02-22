package kp.company.domain;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The relation between {@link Department} and {@link Employee}.
 * <p>
 * A domain object to be persisted to the MongoDB. Identified with {@link Document}.
 * </p>
 *
 * @param departmentId the id of the {@link Department}
 * @param employeeId   the id of the {@link Employee}
 */
@Document
public record AggregateRelation(String departmentId, String employeeId) {
    /**
     * Creates the {@link AggregateRelation} from pair:
     * <ul>
     * <li>{@link Department}</li>
     * <li>{@link Employee}</li>
     * </ul>
     *
     * @param department the {@link Department}
     * @param employee   the {@link Employee}
     * @return the {@link AggregateRelation}
     */
    public static AggregateRelation of(Department department, Employee employee) {
        return new AggregateRelation(department.id(), employee.id());
    }
}
