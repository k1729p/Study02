package kp.company.controller;

import kp.Constants;
import kp.company.domain.Employee;
import kp.company.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;

/**
 * The REST web service controller for {@link Employee}s.
 */
@RestController
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EmployeeRepository employeeRepository;

    /**
     * The constructor.
     *
     * @param employeeRepository the {@link Employee} repository
     */
    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Finds the {@link Employee}s.
     *
     * @return the {@link Flux} with the {@link Employee}s
     */
    @GetMapping(Constants.FIND_EMPLOYEES)
    public Flux<Employee> findEmployees() {

        final Flux<Employee> employeesFlux = employeeRepository.findAll(Constants.LASTNAME_FIRSTNAME_SORT);
        logger.info("findEmployees():");
        return employeesFlux;
    }

    /**
     * Finds the {@link Employee} by the first and last name. Uses the query by example.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @return the {@link ResponseEntity} {@link Mono} with the {@link Employee}
     */
    @GetMapping(Constants.FIND_EMPLOYEES_BY_FIRST_NAME_AND_LAST_NAME)
    public Mono<ResponseEntity<Employee>> findEmployeeByFirstNameAndLastName(String firstName, String lastName) {

        final Example<Employee> employeeExample = Example.of(Employee.of(firstName, lastName));
        final Mono<ResponseEntity<Employee>> responseMono = employeeRepository.findOne(employeeExample)
                .map(ResponseEntity::ok).switchIfEmpty(Mono.error(Constants.NOT_FOUND_EXCEPTION));
        logger.info("findEmployeeByFirstNameAndLastName(): firstName[{}], lastName[{}]", firstName, lastName);
        return responseMono;
    }
}
