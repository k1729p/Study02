package kp.company.controller;

import kp.company.controller.base.ControllerTestsBase;
import kp.company.domain.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.ResultActions;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static kp.Constants.*;
import static kp.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The {@link EmployeeController} tests.
 */
class EmployeeControllerTests extends ControllerTestsBase {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    /**
     * Should find {@link Employee}s.
     *
     * @throws Exception the {@link Exception}
     */
    @Test
    void shouldFindEmployees() throws Exception {
        // GIVEN
        Mockito.when(employeeRepository.findAll(LASTNAME_FIRSTNAME_SORT)).thenReturn(EXPECTED_EMPLOYEES_FLUX);
        // WHEN
        ResultActions resultActions = mockMvc.perform(get(FIND_EMPLOYEES));
        // THEN
        if (VERBOSE) {
            resultActions.andDo(print());
        }
        resultActions.andExpect(status().isOk());
        @SuppressWarnings("unchecked") final List<Employee> actualEmployees =
                (List<Employee>) resultActions.andReturn().getAsyncResult();
        Assertions.assertNotNull(actualEmployees);
        Assertions.assertEquals(EXPECTED_EMPLOYEES.size(), actualEmployees.size());
        for (int i = 0; i < EXPECTED_EMPLOYEES.size(); i++) {
            Assertions.assertEquals(EXPECTED_EMPLOYEES.get(i).firstName(), actualEmployees.get(i).firstName());
            Assertions.assertEquals(EXPECTED_EMPLOYEES.get(i).lastName(), actualEmployees.get(i).lastName());
        }
        logger.info("shouldFindEmployees():");
    }

    /**
     * Should find {@link Employee} by first name and last name.
     *
     * @throws Exception the {@link Exception}
     */
    @Test
    void shouldFindEmployeeByFirstNameAndLastName() throws Exception {
        // GIVEN
        Mockito.when(employeeRepository.findOne(Example.of(EXPECTED_EMPLOYEE_11)))
                .thenReturn(Mono.just(EXPECTED_EMPLOYEE_11));
        // WHEN
        ResultActions resultActions = mockMvc.perform(get(FIND_EMPLOYEES_BY_FIRST_NAME_AND_LAST_NAME)
                .param("firstName", EXPECTED_EMPLOYEE_11.firstName())
                .param("lastName", EXPECTED_EMPLOYEE_11.lastName()));
        // THEN
        if (VERBOSE) {
            resultActions.andDo(print());
        }
        resultActions.andExpect(status().isOk());
        @SuppressWarnings("unchecked") final ResponseEntity<Employee> responseEntity =
                (ResponseEntity<Employee>) resultActions.andReturn().getAsyncResult();
        Assertions.assertNotNull(responseEntity);
        final Employee actualEmployee = responseEntity.getBody();
        Assertions.assertNotNull(actualEmployee);
        Assertions.assertEquals(EXPECTED_EMPLOYEE_11.firstName(), actualEmployee.firstName());
        Assertions.assertEquals(EXPECTED_EMPLOYEE_11.lastName(), actualEmployee.lastName());
        logger.info("shouldFindEmployeeByFirstNameAndLastName():");
    }

}