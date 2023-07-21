package com.myapp.repository;

import com.myapp.domain.TipoCargo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TipoCargo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TipoCargoRepository extends ReactiveCrudRepository<TipoCargo, Long>, TipoCargoRepositoryInternal {
    @Override
    <S extends TipoCargo> Mono<S> save(S entity);

    @Override
    Flux<TipoCargo> findAll();

    @Override
    Mono<TipoCargo> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TipoCargoRepositoryInternal {
    <S extends TipoCargo> Mono<S> save(S entity);

    Flux<TipoCargo> findAllBy(Pageable pageable);

    Flux<TipoCargo> findAll();

    Mono<TipoCargo> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TipoCargo> findAllBy(Pageable pageable, Criteria criteria);
}
