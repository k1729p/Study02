package kp.company.controller;

import kp.Constants;
import kp.company.domain.Aggregate;
import kp.company.domain.AggregateRelation;
import kp.company.repository.AggregateRelationRepository;
import kp.company.repository.DepartmentRepository;
import kp.company.repository.EmployeeRepository;
import kp.utils.SampleDatasetLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Phaser;
import java.util.function.Function;

/**
 * The REST web service main controller.
 */
@RestController
public class CompanyController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AggregateRelationRepository relationRepository;

    private static final boolean LOAD_AND_DELETE_SAMPLE_DATASET = false;
    /**
     * The constructor.
     *
     * @param departmentRepository the {@link DepartmentRepository}.
     * @param employeeRepository   the {@link EmployeeRepository}.
     * @param relationRepository   the {@link AggregateRelationRepository}.
     */
    public CompanyController(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository,
                             AggregateRelationRepository relationRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.relationRepository = relationRepository;
    }

    /**
     * Loads the sample dataset.
     *
     * @return the dataset loading confirmation response
     */
    @GetMapping(Constants.LOAD_SAMPLE_DATASET_PATH)
    public String loadSampleDataset() {

        SampleDatasetLoader.loadSampleDataset(departmentRepository, employeeRepository, relationRepository);
        if(LOAD_AND_DELETE_SAMPLE_DATASET) {
            SampleDatasetLoader.deleteSampleDataset(departmentRepository, employeeRepository, relationRepository);
        }
        logger.info("loadSampleDataset():");
        return Constants.LOAD_SAMPLE_DATASET_RESULT;
    }

    /**
     * Finds the department and employees by department name.
     * <p>
     * Subscribes to the {@link Aggregate} flux and returns the {@link ResponseEntity} {@link Mono}
     * with the {@link Aggregate} (the wrapper object for the department and employees).
     * </p>
     *
     * @param departmentName the department name
     * @return the {@link ResponseEntity} {@link Mono} with the {@link Aggregate}
     */
    @GetMapping(Constants.FIND_AGGREGATE_BY_DEPARTMENT_NAME)
    public Mono<ResponseEntity<Aggregate>> findAggregateByDepartmentName(String departmentName) {

        final Set<Aggregate> aggregateSet = Collections.synchronizedSet(new HashSet<>());
        final Phaser phaser = new Phaser(1);
        phaser.register();
        findAggregateFluxByDepartmentName(departmentName).subscribe(company -> {
            aggregateSet.add(company);
            logger.debug("findAggregateByDepartmentName(): department name[{}]", company.department().name());
        }, e -> {
            logger.error("findAggregateByDepartmentName(): department name[{}], exception[{}]",
                    departmentName, e.getMessage());
            phaser.forceTermination();
        }, () -> {
            logger.debug("findAggregateByDepartmentName(): completed");
            phaser.arriveAndDeregister();
        });
        phaser.arriveAndAwaitAdvance();
        final Mono<ResponseEntity<Aggregate>> responseMono = aggregateSet.stream().findFirst().map(ResponseEntity::ok)
                .map(Mono::just).orElse(Mono.error(Constants.NOT_FOUND_EXCEPTION));
        logger.info("findAggregateByDepartmentName(): department name[{}]", departmentName);
        return responseMono;
    }

    /**
     * Finds the department and employees by department name.
     *
     * @param departmentName the department name
     * @return the {@link Aggregate} {@link Flux} with the department and employees
     */
    @GetMapping(Constants.FIND_AGGREGATE_FLUX_BY_DEPARTMENT_NAME)
    public Flux<Aggregate> findAggregateFluxByDepartmentName(String departmentName) {

        final Function<Tuple2<String, Aggregate>, Flux<Aggregate>> tupleMapper =
                tuple -> relationRepository
                        .findByDepartmentId(tuple.getT1()).map(AggregateRelation::employeeId)
                        .flatMap(employeeRepository::findById).map(tuple.getT2()::addEmployee);
        final Flux<Aggregate> companyFlux = departmentRepository.findByName(departmentName)
                .map(department -> Tuples.of(department.id(), Aggregate.of(department))).flatMap(tupleMapper);
        logger.info("findAggregateFluxByDepartmentName(): departmentName[{}]", departmentName);
        return companyFlux;
    }

}