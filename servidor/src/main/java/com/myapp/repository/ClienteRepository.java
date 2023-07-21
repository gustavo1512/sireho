package com.myapp.repository;

import com.myapp.domain.Cliente;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Cliente entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClienteRepository extends ReactiveCrudRepository<Cliente, Long>, ClienteRepositoryInternal {
    @Override
    <S extends Cliente> Mono<S> save(S entity);

    @Override
    Flux<Cliente> findAll();

    @Override
    Mono<Cliente> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ClienteRepositoryInternal {
    <S extends Cliente> Mono<S> save(S entity);

    Flux<Cliente> findAllBy(Pageable pageable);

    Flux<Cliente> findAll();

    Mono<Cliente> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Cliente> findAllBy(Pageable pageable, Criteria criteria);
}
