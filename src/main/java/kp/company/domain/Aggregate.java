package kp.company.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The aggregate of {@link Department} and {@link Employee}s.<br>
 * This object is <b>NOT</b> persisted in the MongoDB.
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
	 * Adds the employee to the set.
	 * 
	 * @param employee the {@link Employee}
	 * @return the aggregate
	 */
	public Aggregate addEmployee(Employee employee) {
		if (Objects.nonNull(employee)) {
			this.employees().add(employee);
		}
		return this;
	}

}