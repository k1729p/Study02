package kp.company.domain;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The relation between {@link Department} and {@link Employee}.<br>
 * A domain object to be persisted to the MongoDB.
 * 
 * @param departmentId the id of the {@link Department}
 * @param employeeId   the id of the {@link Employee}
 */
@Document
public record AggregateRelation(String departmentId, String employeeId) {
	/**
	 * Creates the {@link Aggregate} relation from pair: {@link Department} and
	 * {@link Employee}.
	 * 
	 * @param department the {@link Department}
	 * @param employee   the {@link Employee}
	 * @return the {@link Aggregate} relation
	 */
	public static AggregateRelation of(Department department, Employee employee) {
		return new AggregateRelation(department.id(), employee.id());
	}
}
