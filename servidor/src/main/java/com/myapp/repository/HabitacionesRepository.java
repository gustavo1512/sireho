package com.myapp.repository;

import com.myapp.domain.Habitaciones;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Habitaciones entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HabitacionesRepository extends ReactiveCrudRepository<Habitaciones, Long>, HabitacionesRepositoryInternal {
    @Query("SELECT * FROM habitaciones entity WHERE entity.factura_habitaciones_id = :id")
    Flux<Habitaciones> findByFacturaHabitaciones(Long id);

    @Query("SELECT * FROM habitaciones entity WHERE entity.factura_habitaciones_id IS NULL")
    Flux<Habitaciones> findAllWhereFacturaHabitacionesIsNull();

    @Query("SELECT * FROM habitaciones entity WHERE entity.id not in (select reservaciones_id from reservaciones)")
    Flux<Habitaciones> findAllWhereReservacionesIsNull();

    @Override
    <S extends Habitaciones> Mono<S> save(S entity);

    @Override
    Flux<Habitaciones> findAll();

    @Override
    Mono<Habitaciones> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface HabitacionesRepositoryInternal {
    <S extends Habitaciones> Mono<S> save(S entity);

    Flux<Habitaciones> findAllBy(Pageable pageable);

    Flux<Habitaciones> findAll();

    Mono<Habitaciones> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Habitaciones> findAllBy(Pageable pageable, Criteria criteria);
}
