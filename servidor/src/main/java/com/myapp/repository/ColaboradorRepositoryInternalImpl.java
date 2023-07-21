package com.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.myapp.domain.Colaborador;
import com.myapp.repository.rowmapper.ColaboradorRowMapper;
import com.myapp.repository.rowmapper.TipoCargoRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Colaborador entity.
 */
@SuppressWarnings("unused")
class ColaboradorRepositoryInternalImpl extends SimpleR2dbcRepository<Colaborador, Long> implements ColaboradorRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TipoCargoRowMapper tipocargoMapper;
    private final ColaboradorRowMapper colaboradorMapper;

    private static final Table entityTable = Table.aliased("colaborador", EntityManager.ENTITY_ALIAS);
    private static final Table tipoCargoColaboradorTable = Table.aliased("tipo_cargo", "tipoCargoColaborador");

    public ColaboradorRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TipoCargoRowMapper tipocargoMapper,
        ColaboradorRowMapper colaboradorMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Colaborador.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.tipocargoMapper = tipocargoMapper;
        this.colaboradorMapper = colaboradorMapper;
    }

    @Override
    public Flux<Colaborador> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Colaborador> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ColaboradorSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TipoCargoSqlHelper.getColumns(tipoCargoColaboradorTable, "tipoCargoColaborador"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(tipoCargoColaboradorTable)
            .on(Column.create("tipo_cargo_colaborador_id", entityTable))
            .equals(Column.create("id", tipoCargoColaboradorTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Colaborador.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Colaborador> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Colaborador> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Colaborador process(Row row, RowMetadata metadata) {
        Colaborador entity = colaboradorMapper.apply(row, "e");
        entity.setTipoCargoColaborador(tipocargoMapper.apply(row, "tipoCargoColaborador"));
        return entity;
    }

    @Override
    public <S extends Colaborador> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
