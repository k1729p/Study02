package kp;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.IntBinaryOperator;

/**
 * The constants.
 */
@SuppressWarnings("doclint:missing")
public final class Constants {
    private static final String ROOT = "/";
    public static final String LOAD_SAMPLE_DATASET_PATH = ROOT + "loadSampleDataset";
    public static final String FIND_AGGREGATE_BY_DEPARTMENT_NAME = ROOT + "aggregate/find/findByDepartmentName";
    public static final String FIND_AGGREGATE_FLUX_BY_DEPARTMENT_NAME = ROOT
                                                                        + "aggregateFlux/find/findByDepartmentName";
    public static final String FIND_DEPARTMENTS = ROOT + "departments";
    public static final String FIND_DEPARTMENT_BY_NAME = ROOT + "departments/find/findByName";
    public static final String FIND_EMPLOYEES = ROOT + "employees";
    public static final String FIND_EMPLOYEES_BY_FIRST_NAME_AND_LAST_NAME = ROOT
                                                                            + "employees/find/findByFirstNameAndLastName";

    public static final String LOAD_SAMPLE_DATASET_RESULT = "The sample dataset was loaded with success.";
    public static final Sort NAME_SORT = Sort.by("name");
    public static final Sort LASTNAME_FIRSTNAME_SORT = Sort.by("lastName", "firstName");
    public static final ResponseStatusException NOT_FOUND_EXCEPTION = new ResponseStatusException(HttpStatus.NOT_FOUND);

    public static final IntBinaryOperator EMP_INDEX_FUN = (depIndex, empIndex) -> 100 * depIndex + empIndex;
    public static final int DEP_INDEX_LOWER_BOUND = 1;
    public static final int DEP_INDEX_UPPER_BOUND = 2;
    public static final int EMP_INDEX_LOWER_BOUND = 1;
    public static final int EMP_INDEX_UPPER_BOUND = 2;

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}
