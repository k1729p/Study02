package kp.company.controller;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Phaser;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import kp.Constants;
import kp.company.domain.Aggregate;
import kp.company.domain.AggregateRelation;
import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.repository.AggregateRelationRepository;
import kp.company.repository.DepartmentRepository;
import kp.company.repository.EmployeeRepository;
import kp.utils.SampleDatasetLoader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * The REST web service main controller.
 *
 */
@RestController
public class CompanyController {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	private final DepartmentRepository departmentRepository;
	private final EmployeeRepository employeeRepository;
	private final AggregateRelationRepository relationRepository;

	/**
	 * The constructor.
	 * 
	 * @param departmentRepository the {@link Department} repository
	 * @param employeeRepository   the {@link Employee} repository
	 * @param relationRepository   the {@link AggregateRelation} repository
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
		logger.info("loadSampleDataset():");
		return Constants.LOAD_SAMPLE_DATASET_RESULT;
	}

	/**
	 * Finds the department and employees by department name.<br/>
	 * Subscribes to the {@link Aggregate} flux and returns the
	 * {@link ResponseEntity} {@link Mono} with the {@link Aggregate} (the wrapper
	 * object for the department and employees).
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
			logger.debug(
					String.format("findAggregateByDepartmentName(): department name[%s]", company.department().name()));
		}, exc -> {
			logger.error(String.format("findAggregateByDepartmentName(): department name[%s], exception[%s]",
					departmentName, exc.getMessage()));
			phaser.forceTermination();
		}, () -> {
			logger.debug("findAggregateByDepartmentName(): completed");
			phaser.arriveAndDeregister();
		});
		phaser.arriveAndAwaitAdvance();
		final Mono<ResponseEntity<Aggregate>> responseMono = aggregateSet.stream().findFirst().map(ResponseEntity::ok)
				.map(Mono::just).orElse(Mono.error(Constants.NOT_FOUND_EXCEPTION));
		logger.info(String.format("findAggregateByDepartmentName(): department name[%s]", departmentName));
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

		final Function<Tuple2<String, Aggregate>, Flux<Aggregate>> tupleMapper = tuple -> relationRepository
				.findByDepartmentId(tuple.getT1()).map(AggregateRelation::employeeId)
				.flatMap(employeeRepository::findById).map(tuple.getT2()::addEmployee);
		final Flux<Aggregate> companyFlux = departmentRepository.findByName(departmentName)
				.map(department -> Tuples.of(department.id(), Aggregate.of(department))).flatMap(tupleMapper);
		logger.info(String.format("findAggregateFluxByDepartmentName(): departmentName[%s]", departmentName));
		return companyFlux;
	}

}