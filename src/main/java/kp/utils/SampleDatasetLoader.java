package kp.utils;

import kp.company.domain.AggregateRelation;
import kp.company.domain.Department;
import kp.company.domain.Employee;
import kp.company.repository.AggregateRelationRepository;
import kp.company.repository.DepartmentRepository;
import kp.company.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.concurrent.Phaser;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static kp.Constants.*;

/**
 * The sample dataset loader for the MongoDB database.
 * <p>
 * The sample dataset:
 * </p>
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
 */
public class SampleDatasetLoader {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final boolean VERBOSE = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private SampleDatasetLoader() {
    }

    /**
     * Generates the dataset for departments with {@link Employee}s and saves it to the MongoDB database.
     *
     * @param departmentRepository the {@link DepartmentRepository}
     * @param employeeRepository   the {@link EmployeeRepository}
     * @param relationRepository   the {@link AggregateRelationRepository}
     */
    public static void loadSampleDataset(DepartmentRepository departmentRepository,
                                         EmployeeRepository employeeRepository,
                                         AggregateRelationRepository relationRepository) {

        if (Objects.isNull(departmentRepository) || Objects.isNull(employeeRepository) ||
            Objects.isNull(relationRepository)) {
            logger.error("loadSampleDataset(): All arguments must be non-null. DepartmentRepository is null[{}], " +
                         "employeeRepository is null[{}], relationRepository is null[{}].",
                    Objects.isNull(departmentRepository), Objects.isNull(employeeRepository),
                    Objects.isNull(relationRepository));
            return;
        }
        final Flux<Flux<AggregateRelation>> loaderFlux = prepareLoaderFlux(
                departmentRepository, employeeRepository, relationRepository);
        execute(loaderFlux);
    }

    /**
     * Deletes the sample dataset.
     *
     * @param departmentRepository the {@link DepartmentRepository}
     * @param employeeRepository   the {@link EmployeeRepository}
     * @param relationRepository   the {@link AggregateRelationRepository}
     */
    public static void deleteSampleDataset(DepartmentRepository departmentRepository,
                                           EmployeeRepository employeeRepository,
                                           AggregateRelationRepository relationRepository) {

        if (Objects.isNull(departmentRepository) || Objects.isNull(employeeRepository) || Objects.isNull(relationRepository)) {
            logger.error("deleteSampleDataset(): All arguments must be non-null. DepartmentRepository is null[{}], " +
                         "employeeRepository is null[{}], relationRepository is null[{}].",
                    Objects.isNull(departmentRepository), Objects.isNull(employeeRepository),
                    Objects.isNull(relationRepository));
            return;
        }
        final Phaser phaser = new Phaser(1);
        final Consumer<Throwable> errorConsumer = e -> {
            logger.error("deleteSampleDataset(): exception[{}]", e.getMessage());
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
     * @param departmentRepository the {@link DepartmentRepository}
     * @param employeeRepository   the {@link EmployeeRepository}
     * @param relationRepository   the {@link AggregateRelationRepository}
     * @return the loader {@link Flux}
     */
    private static Flux<Flux<AggregateRelation>> prepareLoaderFlux(DepartmentRepository departmentRepository,
                                                                   EmployeeRepository employeeRepository,
                                                                   AggregateRelationRepository relationRepository) {

        final Function<Integer, Mono<Flux<AggregateRelation>>> departmentMapper =
                prepareDepartmentMapper(departmentRepository, employeeRepository, relationRepository);
        final Flux<Flux<AggregateRelation>> addAllFlux = Flux.just(DEP_INDEX_LOWER_BOUND, DEP_INDEX_UPPER_BOUND)
                .flatMap(departmentMapper);

        final Mono<Void> deleteAllMono = departmentRepository.deleteAll().then(employeeRepository.deleteAll());
        return deleteAllMono.thenMany(addAllFlux);
    }

    /**
     * Prepares the department mapper.
     *
     * @param departmentRepository the {@link DepartmentRepository}
     * @param employeeRepository   the {@link EmployeeRepository}
     * @param relationRepository   the {@link AggregateRelationRepository}
     * @return the department mapper
     */
    private static Function<Integer, Mono<Flux<AggregateRelation>>> prepareDepartmentMapper(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            AggregateRelationRepository relationRepository) {

        final BiFunction<Integer, Department, Flux<AggregateRelation>> employeeMapper =
                prepareEmployeeMapper(employeeRepository, relationRepository);

        final BiFunction<Integer, Department, Mono<Flux<AggregateRelation>>> saveDepartmentMapper =
                (depIndex, department) -> departmentRepository.save(department)
                        .map(depSaved -> employeeMapper.apply(depIndex, depSaved));

        return depIndex -> Mono.just(Department.fromIndex(depIndex))
                .flatMap(department -> saveDepartmentMapper.apply(depIndex, department));
    }

    /**
     * Prepares the employee mapper.
     *
     * @param employeeRepository the {@link EmployeeRepository}
     * @param relationRepository the {@link AggregateRelationRepository}
     * @return the employee mapper.
     */
    private static BiFunction<Integer, Department, Flux<AggregateRelation>> prepareEmployeeMapper(
            EmployeeRepository employeeRepository,
            AggregateRelationRepository relationRepository) {

        final BiFunction<Department, Employee, Mono<AggregateRelation>> saveRelationMapper =
                (department, employee) -> relationRepository.save(AggregateRelation.of(department, employee))
                        .transform(mono -> VERBOSE ? mono.log() : mono);

        final BiFunction<Department, Employee, Mono<AggregateRelation>> saveEmployeeMapper =
                (department, employee) -> employeeRepository.save(employee)
                        .flatMap(empSaved -> saveRelationMapper.apply(department, empSaved));

        return (depIndex, department) -> Flux.just(EMP_INDEX_LOWER_BOUND, EMP_INDEX_UPPER_BOUND)
                .map(empIndex -> EMP_INDEX_FUN.applyAsInt(depIndex, empIndex)).map(Employee::fromIndex)
                .flatMap(employee -> saveEmployeeMapper.apply(department, employee));
    }

    /**
     * Subscribes to the database loader {@link Flux}.
     *
     * @param loaderFlux the database loader {@link Flux}
     */
    private static void execute(Flux<Flux<AggregateRelation>> loaderFlux) {

        final Phaser phaser = new Phaser(1);
        phaser.register();
        final Consumer<Flux<AggregateRelation>> relationConsumer =
                relationFlux -> relationFlux.subscribe(
                        relation -> logger
                                .debug("execute(): saved aggregate relation, departmentId[{}], employeeId[{}]",
                                        relation.departmentId(), relation.employeeId()),
                        e -> {
                            logger.error("execute(): saving aggregate relation exception[{}]", e.getMessage());
                            phaser.forceTermination();
                        },
                        () -> logger.info("execute(): saving aggregate relations completed"));

        loaderFlux.subscribe(relationConsumer,
                e -> {
                    logger.error("execute(): saving all exception[{}]", e.getMessage());
                    phaser.forceTermination();
                },
                () -> {
                    logger.info("execute(): saving all completed");
                    phaser.arriveAndDeregister();
                });
        phaser.arriveAndAwaitAdvance();
    }
}