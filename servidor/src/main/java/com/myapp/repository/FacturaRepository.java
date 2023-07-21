package com.myapp.repository;

import com.myapp.domain.Factura;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Factura entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacturaRepository extends ReactiveCrudRepository<Factura, Long>, FacturaRepositoryInternal {
    @Override
    <S extends Factura> Mono<S> save(S entity);

    @Override
    Flux<Factura> findAll();

    @Override
    Mono<Factura> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FacturaRepositoryInternal {
    <S extends Factura> Mono<S> save(S entity);

    Flux<Factura> findAllBy(Pageable pageable);

    Flux<Factura> findAll();

    Mono<Factura> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Factura> findAllBy(Pageable pageable, Criteria criteria);
}
