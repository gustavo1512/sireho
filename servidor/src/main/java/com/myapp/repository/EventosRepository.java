package com.myapp.repository;

import com.myapp.domain.Eventos;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Eventos entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventosRepository extends ReactiveCrudRepository<Eventos, Long>, EventosRepositoryInternal {
    @Override
    <S extends Eventos> Mono<S> save(S entity);

    @Override
    Flux<Eventos> findAll();

    @Override
    Mono<Eventos> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EventosRepositoryInternal {
    <S extends Eventos> Mono<S> save(S entity);

    Flux<Eventos> findAllBy(Pageable pageable);

    Flux<Eventos> findAll();

    Mono<Eventos> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Eventos> findAllBy(Pageable pageable, Criteria criteria);
}
