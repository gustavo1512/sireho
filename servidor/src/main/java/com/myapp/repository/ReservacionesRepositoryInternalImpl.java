package com.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.myapp.domain.Reservaciones;
import com.myapp.repository.rowmapper.ClienteRowMapper;
import com.myapp.repository.rowmapper.ColaboradorRowMapper;
import com.myapp.repository.rowmapper.EventosRowMapper;
import com.myapp.repository.rowmapper.HabitacionesRowMapper;
import com.myapp.repository.rowmapper.ReservacionesRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
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
 * Spring Data R2DBC custom repository implementation for the Reservaciones entity.
 */
@SuppressWarnings("unused")
class ReservacionesRepositoryInternalImpl extends SimpleR2dbcRepository<Reservaciones, Long> implements ReservacionesRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final HabitacionesRowMapper habitacionesMapper;
    private final ClienteRowMapper clienteMapper;
    private final ColaboradorRowMapper colaboradorMapper;
    private final EventosRowMapper eventosMapper;
    private final ReservacionesRowMapper reservacionesMapper;

    private static final Table entityTable = Table.aliased("reservaciones", EntityManager.ENTITY_ALIAS);
    private static final Table habitacionesReservacionesTable = Table.aliased("habitaciones", "habitacionesReservaciones");
    private static final Table clienteReservacionesTable = Table.aliased("cliente", "clienteReservaciones");
    private static final Table colaboradorResrvacionesTable = Table.aliased("colaborador", "colaboradorResrvaciones");
    private static final Table eventosReservacionesTable = Table.aliased("eventos", "eventosReservaciones");

    public ReservacionesRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        HabitacionesRowMapper habitacionesMapper,
        ClienteRowMapper clienteMapper,
        ColaboradorRowMapper colaboradorMapper,
        EventosRowMapper eventosMapper,
        ReservacionesRowMapper reservacionesMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Reservaciones.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.habitacionesMapper = habitacionesMapper;
        this.clienteMapper = clienteMapper;
        this.colaboradorMapper = colaboradorMapper;
        this.eventosMapper = eventosMapper;
        this.reservacionesMapper = reservacionesMapper;
    }

    @Override
    public Flux<Reservaciones> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Reservaciones> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ReservacionesSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(HabitacionesSqlHelper.getColumns(habitacionesReservacionesTable, "habitacionesReservaciones"));
        columns.addAll(ClienteSqlHelper.getColumns(clienteReservacionesTable, "clienteReservaciones"));
        columns.addAll(ColaboradorSqlHelper.getColumns(colaboradorResrvacionesTable, "colaboradorResrvaciones"));
        columns.addAll(EventosSqlHelper.getColumns(eventosReservacionesTable, "eventosReservaciones"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(habitacionesReservacionesTable)
            .on(Column.create("habitaciones_reservaciones_id", entityTable))
            .equals(Column.create("id", habitacionesReservacionesTable))
            .leftOuterJoin(clienteReservacionesTable)
            .on(Column.create("cliente_reservaciones_id", entityTable))
            .equals(Column.create("id", clienteReservacionesTable))
            .leftOuterJoin(colaboradorResrvacionesTable)
            .on(Column.create("colaborador_resrvaciones_id", entityTable))
            .equals(Column.create("id", colaboradorResrvacionesTable))
            .leftOuterJoin(eventosReservacionesTable)
            .on(Column.create("eventos_reservaciones_id", entityTable))
            .equals(Column.create("id", eventosReservacionesTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Reservaciones.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Reservaciones> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Reservaciones> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Reservaciones process(Row row, RowMetadata metadata) {
        Reservaciones entity = reservacionesMapper.apply(row, "e");
        entity.setHabitacionesReservaciones(habitacionesMapper.apply(row, "habitacionesReservaciones"));
        entity.setClienteReservaciones(clienteMapper.apply(row, "clienteReservaciones"));
        entity.setColaboradorResrvaciones(colaboradorMapper.apply(row, "colaboradorResrvaciones"));
        entity.setEventosReservaciones(eventosMapper.apply(row, "eventosReservaciones"));
        return entity;
    }

    @Override
    public <S extends Reservaciones> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
