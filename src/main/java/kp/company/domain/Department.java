package kp.company.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The department.
 * <p>
 * A domain object to be persisted to the MongoDB. Identified with {@link Document}.
 * </p>
 *
 * @param id   the id
 * @param name the name
 */
@Document
public record Department(@Id String id, String name) {

    /**
     * Creates the department from name.
     *
     * @param name the department name
     * @return the department
     */
    public static Department of(String name) {
        return new Department(null, name);
    }

    /**
     * Creates the department from index.
     *
     * @param index the index
     * @return the department
     */
    public static Department fromIndex(int index) {
        return Department.of("D-Name-%d".formatted(index));
    }
}