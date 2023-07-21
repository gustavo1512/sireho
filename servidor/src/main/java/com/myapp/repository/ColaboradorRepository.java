package com.myapp.repository;

import com.myapp.domain.Colaborador;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Colaborador entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ColaboradorRepository extends ReactiveCrudRepository<Colaborador, Long>, ColaboradorRepositoryInternal {
    @Query("SELECT * FROM colaborador entity WHERE entity.tipo_cargo_colaborador_id = :id")
    Flux<Colaborador> findByTipoCargoColaborador(Long id);

    @Query("SELECT * FROM colaborador entity WHERE entity.tipo_cargo_colaborador_id IS NULL")
    Flux<Colaborador> findAllWhereTipoCargoColaboradorIsNull();

    @Override
    <S extends Colaborador> Mono<S> save(S entity);

    @Override
    Flux<Colaborador> findAll();

    @Override
    Mono<Colaborador> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ColaboradorRepositoryInternal {
    <S extends Colaborador> Mono<S> save(S entity);

    Flux<Colaborador> findAllBy(Pageable pageable);

    Flux<Colaborador> findAll();

    Mono<Colaborador> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Colaborador> findAllBy(Pageable pageable, Criteria criteria);
}
