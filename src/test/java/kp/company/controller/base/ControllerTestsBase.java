package kp.company.controller.base;

import kp.company.repository.AggregateRelationRepository;
import kp.company.repository.DepartmentRepository;
import kp.company.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The base class for the handler tests using the mocked:
 * <ul>
 * <li>{@link DepartmentRepository}</li>
 * <li>{@link EmployeeRepository}</li>
 * <li>{@link AggregateRelationRepository}</li>
 * </ul>
 * <p>
 * The tests are designed to be run as integration tests (not as unit tests),
 * hence the use of {@link MockitoBean}.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class ControllerTestsBase {
    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected DepartmentRepository departmentRepository;

    @MockitoBean
    protected EmployeeRepository employeeRepository;

    @MockitoBean
    protected AggregateRelationRepository relationRepository;
}