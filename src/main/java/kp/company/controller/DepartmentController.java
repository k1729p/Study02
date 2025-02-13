package kp.company.controller;

import kp.Constants;
import kp.company.domain.Department;
import kp.company.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.lang.invoke.MethodHandles;

/**
 * The REST web service controller for {@link Department}s.
 */
@RestController
public class DepartmentController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DepartmentRepository departmentRepository;

    /**
     * The constructor.
     *
     * @param departmentRepository the {@link Department} repository
     */
    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * Finds all {@link Department}s.
     *
     * @return the {@link Flux} with the {@link Department}s
     */
    @GetMapping(Constants.FIND_DEPARTMENTS)
    public Flux<Department> findDepartments() {

        final Flux<Department> departmentsFlux = departmentRepository.findAll(Constants.NAME_SORT);
        logger.info("findDepartments():");
        return departmentsFlux;
    }

    /**
     * Finds the {@link Department} by the name.
     *
     * @param name the name
     * @return the {@link Flux} with the {@link Department}
     */
    @GetMapping(Constants.FIND_DEPARTMENT_BY_NAME)
    public Flux<Department> findDepartmentByName(String name) {

        final Flux<Department> departmentFlux = departmentRepository.findByName(name);
        logger.info("findDepartmentByName(): name[{}]", name);
        return departmentFlux;
    }

}
