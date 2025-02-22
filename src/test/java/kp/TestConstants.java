package kp;

import kp.company.domain.AggregateRelation;
import kp.company.domain.Department;
import kp.company.domain.Employee;
import reactor.core.publisher.Flux;

import java.util.List;

import static kp.Constants.*;

/**
 * Test constants.
 */
@SuppressWarnings("doclint:missing")
public final class TestConstants {
    public static final Department EXPECTED_DEPARTMENT_1 = Department.fromIndex(DEP_INDEX_LOWER_BOUND);
    public static final Department EXPECTED_DEPARTMENT_2 = Department.fromIndex(DEP_INDEX_LOWER_BOUND + 1);
    public static final List<Department> EXPECTED_DEPARTMENTS = List.of(EXPECTED_DEPARTMENT_1, EXPECTED_DEPARTMENT_2);
    public static final Flux<Department> EXPECTED_DEPARTMENTS_FLUX =
            Flux.just(EXPECTED_DEPARTMENT_1, EXPECTED_DEPARTMENT_2);
    public static final Employee EXPECTED_EMPLOYEE_11 =
            Employee.fromIndex(EMP_INDEX_FUN.applyAsInt(DEP_INDEX_LOWER_BOUND, EMP_INDEX_LOWER_BOUND));
    public static final Employee EXPECTED_EMPLOYEE_12 =
            Employee.fromIndex(EMP_INDEX_FUN.applyAsInt(DEP_INDEX_LOWER_BOUND, EMP_INDEX_LOWER_BOUND + 1));
    public static final Employee EXPECTED_EMPLOYEE_21 =
            Employee.fromIndex(EMP_INDEX_FUN.applyAsInt(DEP_INDEX_LOWER_BOUND + 1, EMP_INDEX_LOWER_BOUND));
    public static final Employee EXPECTED_EMPLOYEE_22 =
            Employee.fromIndex(EMP_INDEX_FUN.applyAsInt(DEP_INDEX_LOWER_BOUND + 1, EMP_INDEX_LOWER_BOUND + 1));
    public static final List<Employee> EXPECTED_EMPLOYEES =
            List.of(EXPECTED_EMPLOYEE_11, EXPECTED_EMPLOYEE_12, EXPECTED_EMPLOYEE_21, EXPECTED_EMPLOYEE_22);
    public static final Flux<Employee> EXPECTED_EMPLOYEES_FLUX =
            Flux.just(EXPECTED_EMPLOYEE_11, EXPECTED_EMPLOYEE_12, EXPECTED_EMPLOYEE_21, EXPECTED_EMPLOYEE_22);

    public static final String AGG_DEP_ID = "123";
    public static final String AGG_EMP_ID = "456";
    public static final Flux<AggregateRelation> EXPECTED_AGGREGATE_RELATION_FLUX =
            Flux.just(new AggregateRelation(AGG_DEP_ID, AGG_EMP_ID));
    public static final Department EXPECTED_DEPARTMENT_WITH_ID =
            new Department(AGG_DEP_ID, EXPECTED_DEPARTMENT_1.name());
    public static final Employee EXPECTED_EMPLOYEE_WITH_ID =
            new Employee(AGG_EMP_ID, Employee.fromIndex(11).firstName(), EXPECTED_EMPLOYEE_11.lastName());

    private TestConstants() {
        throw new IllegalStateException("Utility class");
    }
}
