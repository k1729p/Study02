package kp.utils;

import static kp.Constants.DEP_INDEX_LOWER_BOUND;
import static kp.Constants.DEP_INDEX_UPPER_BOUND;
import static kp.Constants.EMP_INDEX_FUN;
import static kp.Constants.EMP_INDEX_LOWER_BOUND;
import static kp.Constants.EMP_INDEX_UPPER_BOUND;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Phaser;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kp.company.domain.AggregateRelation;
import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.repository.AggregateRelationRepository;
import kp.company.repository.DepartmentRepository;
import kp.company.repository.EmployeeRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The sample dataset loader for the MongoDB database.
 * 
 */
public class SampleDatasetLoader {
	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass().getName());

	private static final boolean VERBOSE = false;

	/**
	 * The hidden constructor.
	 */
	private SampleDatasetLoader() {
		super();
	}

	/**
	 * Generates the dataset for departments with {@link Employee}s and saves it to
	 * the MongoDB database.<br>
	 * <ol>
	 * <li>Department
	 * <ol>
	 * <li>Employee
	 * <li>Employee
	 * </ol>
	 * <li>Department
	 * <ol>
	 * <li>Employee
	 * <li>Employee
	 * </ol>
	 * </ol>
	 * 
	 * @param departmentRepository the {@link Department} repository
	 * @param employeeRepository   the {@link Employee} repository
	 * @param relationRepository   the {@link AggregateRelation} repository
	 */
	public static void loadSampleDataset(DepartmentRepository departmentRepository,
			EmployeeRepository employeeRepository, AggregateRelationRepository relationRepository) {
		final Flux<Flux<AggregateRelation>> loaderFlux = prepareLoaderFlux(departmentRepository, employeeRepository,
				relationRepository);
		execute(loaderFlux);
	}

	/**
	 * Deletes the sample dataset.
	 * 
	 * @param departmentRepository the {@link Department} repository
	 * @param employeeRepository   the {@link Employee} repository
	 * @param relationRepository   the {@link AggregateRelation} repository
	 */
	public static void deleteSampleDataset(DepartmentRepository departmentRepository,
			EmployeeRepository employeeRepository, AggregateRelationRepository relationRepository) {

		final Phaser phaser = new Phaser(1);
		final Consumer<Throwable> errorConsumer = exc -> {
			logger.error(String.format("deleteSampleDataset(): exception[%s]", exc.getMessage()));
			phaser.forceTermination();
		};
		phaser.register();
		relationRepository.deleteAll().subscribe(null, errorConsumer, phaser::arriveAndDeregister);
		phaser.arriveAndAwaitAdvance();
		phaser.register();
		employeeRepository.deleteAll().subscribe(null, errorConsumer, phaser::arriveAndDeregister);
		phaser.arriveAndAwaitAdvance();
		phaser.register();
		departmentRepository.deleteAll().subscribe(null, errorConsumer, phaser::arriveAndDeregister);
		phaser.arriveAndAwaitAdvance();
	}

	/**
	 * Prepares the loader {@link Flux}.
	 * 
	 * @param departmentRepository the {@link Department} repository
	 * @param employeeRepository   the {@link Employee} repository
	 * @param relationRepository   the {@link AggregateRelation} repository
	 * @return the loader {@link Flux}
	 */
	private static Flux<Flux<AggregateRelation>> prepareLoaderFlux(DepartmentRepository departmentRepository,
			EmployeeRepository employeeRepository, AggregateRelationRepository relationRepository) {

		final Mono<Void> deleteAllMono = departmentRepository.deleteAll().then(employeeRepository.deleteAll());

		final BiFunction<Department, Employee, Mono<AggregateRelation>> saveRelationMapper = (department,
				employee) -> relationRepository.save(AggregateRelation.of(department, employee))
						.transform(mono -> VERBOSE ? mono.log() : mono);

		final BiFunction<Department, Employee, Mono<AggregateRelation>> saveEmployeeMapper = (department,
				employee) -> employeeRepository.save(employee)
						.flatMap(empSaved -> saveRelationMapper.apply(department, empSaved));

		final BiFunction<Integer, Department, Flux<AggregateRelation>> createEmployeeMapper = (depIndex,
				department) -> Flux.just(EMP_INDEX_LOWER_BOUND, EMP_INDEX_UPPER_BOUND)
						.map(empIndex -> EMP_INDEX_FUN.applyAsInt(depIndex, empIndex)).map(Employee::fromIndex)
						.flatMap(employee -> saveEmployeeMapper.apply(department, employee));

		final BiFunction<Integer, Department, Mono<Flux<AggregateRelation>>> saveDepartmentMapper = (depIndex,
				department) -> departmentRepository.save(department)
						.map(depSaved -> createEmployeeMapper.apply(depIndex, depSaved));

		final Function<Integer, Mono<Flux<AggregateRelation>>> createDepartmentMapper = depIndex -> Mono
				.just(Department.fromIndex(depIndex))
				.flatMap(department -> saveDepartmentMapper.apply(depIndex, department));

		final Flux<Flux<AggregateRelation>> addAllFlux = Flux.just(DEP_INDEX_LOWER_BOUND, DEP_INDEX_UPPER_BOUND)
				.flatMap(createDepartmentMapper);

		return deleteAllMono.thenMany(addAllFlux);
	}

	/**
	 * Subscribes to the database loader {@link Flux}.
	 * 
	 * @param loaderFlux the database loader {@link Flux}
	 */
	private static void execute(Flux<Flux<AggregateRelation>> loaderFlux) {

		final Phaser phaser = new Phaser(1);
		phaser.register();
		final Consumer<Flux<AggregateRelation>> relationConsumer = relationFlux -> relationFlux.subscribe(
				relation -> logger
						.debug(String.format("execute(): saved aggregate relation, departmentId[%s], employeeId[%s]",
								relation.departmentId(), relation.employeeId())), /*-*/
				exc -> {
					logger.error(String.format("execute(): saving aggregate relation exception[%s]", exc.getMessage()));
					phaser.forceTermination();
				}, /*-*/
				() -> logger.info("execute(): saving aggregate relations completed"));

		loaderFlux.subscribe(relationConsumer, /*-*/
				exc -> {
					logger.error(String.format("execute(): saving all exception[%s]", exc.getMessage()));
					phaser.forceTermination();
				}, /*-*/
				() -> {
					logger.info("execute(): saving all completed");
					phaser.arriveAndDeregister();
				});
		phaser.arriveAndAwaitAdvance();
	}
}