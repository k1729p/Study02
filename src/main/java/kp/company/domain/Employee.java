package kp.company.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The employee.
 * <p>
 * A domain object to be persisted to the MongoDB. Identified with {@link Document}.
 * </p>
 *
 * @param id        the id
 * @param firstName the first name
 * @param lastName  the last name
 */
@Document
public record Employee(@Id String id, String firstName, String lastName) {
    /**
     * Creates the employee from names.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @return the employee
     */
    public static Employee of(String firstName, String lastName) {
        return new Employee(null, firstName, lastName);
    }

    /**
     * Creates the employee from index.
     *
     * @param index the index
     * @return the employee
     */
    public static Employee fromIndex(int index) {
        return Employee.of("EF-Name-%2d".formatted(index), "EL-Name-%2d".formatted(index));
    }

}
