package kp.company.controller;

import kp.company.controller.base.ControllerTestsBase;
import kp.company.domain.Department;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import reactor.core.publisher.Flux;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static kp.Constants.*;
import static kp.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The {@link DepartmentController} tests.
 */
class DepartmentControllerTests extends ControllerTestsBase {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    /**
     * Should find {@link Department}s.
     *
     * @throws Exception the {@link Exception}
     */
    @Test
    void shouldFindDepartments() throws Exception {
        // GIVEN
        Mockito.when(departmentRepository.findAll(NAME_SORT)).thenReturn(EXPECTED_DEPARTMENTS_FLUX);
        // WHEN
        ResultActions resultActions = mockMvc.perform(get(FIND_DEPARTMENTS));
        // THEN
        if (VERBOSE) {
            resultActions.andDo(print());
        }
        resultActions.andExpect(status().isOk());
        final MvcResult result = resultActions.andReturn();
        @SuppressWarnings("unchecked") final List<Department> actualDepartments = (List<Department>) result.getAsyncResult();
        Assertions.assertNotNull(actualDepartments);
        Assertions.assertEquals(EXPECTED_DEPARTMENTS.size(), actualDepartments.size());
        for (int i = 0; i < EXPECTED_DEPARTMENTS.size(); i++) {
            Assertions.assertEquals(EXPECTED_DEPARTMENTS.get(i).name(), actualDepartments.get(i).name());
        }
        logger.info("shouldFindDepartments():");
    }

    /**
     * Should find {@link Department} by name.
     *
     * @throws Exception the {@link Exception}
     */
    @Test
    void shouldFindDepartmentByName() throws Exception {

        // GIVEN
        Mockito.when(departmentRepository.findByName(EXPECTED_DEPARTMENT_1.name()))
                .thenReturn(Flux.just(EXPECTED_DEPARTMENT_1));
        // WHEN
        ResultActions resultActions =
                mockMvc.perform(get(FIND_DEPARTMENT_BY_NAME).param("name", EXPECTED_DEPARTMENT_1.name()));
        // THEN
        if (VERBOSE) {
            resultActions.andDo(print());
        }
        resultActions.andExpect(status().isOk());
        final MvcResult result = resultActions.andReturn();
        @SuppressWarnings("unchecked") final List<Department> actualDepartments = (List<Department>) result.getAsyncResult();
        Assertions.assertNotNull(actualDepartments);
        Assertions.assertEquals(1, actualDepartments.size());
        final Department actualDepartment = actualDepartments.get(0);
        Assertions.assertNotNull(actualDepartment);
        Assertions.assertEquals(EXPECTED_DEPARTMENT_1.name(), actualDepartment.name());
        logger.info("shouldFindDepartmentByName():");
    }

}