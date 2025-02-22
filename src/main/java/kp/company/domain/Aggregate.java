package kp.company.domain;

import java.util.*;

/**
 * The aggregate of {@link Department} and {@link Employee}s.
 * <p>
 * This object is <b>NOT</b> persisted in the MongoDB.
 * </p>
 *
 * @param department the {@link Department}
 * @param employees  the list of the {@link Employee}s
 */
public record Aggregate(Department department, SortedSet<Employee> employees) {
    /**
     * Creates the aggregate from the {@link Department}.
     *
     * @param department the {@link Department}
     * @return the aggregate
     */
    public static Aggregate of(Department department) {

        final Comparator<Employee> employeeComparator = Comparator.comparing(Employee::lastName)
                .thenComparing(Employee::firstName).thenComparing(Employee::id);
        final SortedSet<Employee> sortedSet = Collections.synchronizedSortedSet(new TreeSet<>(employeeComparator));
        return new Aggregate(department, sortedSet);
    }

    /**
     * Adds the {@link Employee} to the set.
     *
     * @param employee the {@link Employee}
     * @return the aggregate
     */
    public Aggregate addEmployee(Employee employee) {

        Optional.ofNullable(employee).ifPresent(emp -> this.employees().add(emp));
        return this;
    }

}