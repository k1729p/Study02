package kp.company.controller;

import kp.company.controller.base.ControllerTestsBase;
import kp.company.domain.Aggregate;
import kp.company.domain.Department;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.ResultActions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;

import static kp.Constants.FIND_AGGREGATE_BY_DEPARTMENT_NAME;
import static kp.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The {@link CompanyController} tests.
 */
class CompanyControllerTests extends ControllerTestsBase {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    /**
     * Should find {@link Aggregate} by {@link Department} name.
     *
     * @throws Exception the {@link Exception}
     */
    @Test
    void shouldFindAggregateByDepartmentName() throws Exception {
        // GIVEN
        Mockito.when(relationRepository.findByDepartmentId(AGG_DEP_ID)).thenReturn(EXPECTED_AGGREGATE_RELATION_FLUX);
        Mockito.when(departmentRepository.findByName(EXPECTED_DEPARTMENT_1.name()))
                .thenReturn(Flux.just(EXPECTED_DEPARTMENT_WITH_ID));
        Mockito.when(employeeRepository.findById(AGG_EMP_ID)).thenReturn(Mono.just(EXPECTED_EMPLOYEE_WITH_ID));
        // WHEN
        ResultActions resultActions = mockMvc.perform(
                get(FIND_AGGREGATE_BY_DEPARTMENT_NAME).param("departmentName", EXPECTED_DEPARTMENT_1.name()));
        // THEN
        if (VERBOSE) {
            resultActions.andDo(print());
        }
        resultActions.andExpect(status().isOk());
        @SuppressWarnings("unchecked") final ResponseEntity<Aggregate> responseEntity =
                (ResponseEntity<Aggregate>) resultActions.andReturn().getAsyncResult();
        Assertions.assertNotNull(responseEntity);
        final Aggregate aggregate = responseEntity.getBody();
        Assertions.assertNotNull(aggregate);
        Assertions.assertEquals(EXPECTED_DEPARTMENT_1.name(), aggregate.department().name());
        Assertions.assertEquals(1, aggregate.employees().size());
        Assertions.assertTrue(aggregate.employees().contains(EXPECTED_EMPLOYEE_WITH_ID));
        logger.info("shouldFindAggregateByDepartmentName():");
    }

}