package com.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.myapp.domain.Habitaciones;
import com.myapp.repository.rowmapper.FacturaRowMapper;
import com.myapp.repository.rowmapper.HabitacionesRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Habitaciones entity.
 */
@SuppressWarnings("unused")
class HabitacionesRepositoryInternalImpl extends SimpleR2dbcRepository<Habitaciones, Long> implements HabitacionesRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final FacturaRowMapper facturaMapper;
    private final HabitacionesRowMapper habitacionesMapper;

    private static final Table entityTable = Table.aliased("habitaciones", EntityManager.ENTITY_ALIAS);
    private static final Table facturaHabitacionesTable = Table.aliased("factura", "facturaHabitaciones");

    public HabitacionesRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        FacturaRowMapper facturaMapper,
        HabitacionesRowMapper habitacionesMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Habitaciones.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.facturaMapper = facturaMapper;
        this.habitacionesMapper = habitacionesMapper;
    }

    @Override
    public Flux<Habitaciones> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Habitaciones> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = HabitacionesSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(FacturaSqlHelper.getColumns(facturaHabitacionesTable, "facturaHabitaciones"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(facturaHabitacionesTable)
            .on(Column.create("factura_habitaciones_id", entityTable))
            .equals(Column.create("id", facturaHabitacionesTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Habitaciones.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Habitaciones> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Habitaciones> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Habitaciones process(Row row, RowMetadata metadata) {
        Habitaciones entity = habitacionesMapper.apply(row, "e");
        entity.setFacturaHabitaciones(facturaMapper.apply(row, "facturaHabitaciones"));
        return entity;
    }

    @Override
    public <S extends Habitaciones> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
