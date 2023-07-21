package com.myapp.repository;

import com.myapp.domain.Reservaciones;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Reservaciones entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReservacionesRepository extends ReactiveCrudRepository<Reservaciones, Long>, ReservacionesRepositoryInternal {
    @Query("SELECT * FROM reservaciones entity WHERE entity.habitaciones_reservaciones_id = :id")
    Flux<Reservaciones> findByHabitacionesReservaciones(Long id);

    @Query("SELECT * FROM reservaciones entity WHERE entity.habitaciones_reservaciones_id IS NULL")
    Flux<Reservaciones> findAllWhereHabitacionesReservacionesIsNull();

    @Query("SELECT * FROM reservaciones entity WHERE entity.cliente_reservaciones_id = :id")
    Flux<Reservaciones> findByClienteReservaciones(Long id);

    @Query("SELECT * FROM reservaciones entity WHERE entity.cliente_reservaciones_id IS NULL")
    Flux<Reservaciones> findAllWhereClienteReservacionesIsNull();

    @Query("SELECT * FROM reservaciones entity WHERE entity.colaborador_resrvaciones_id = :id")
    Flux<Reservaciones> findByColaboradorResrvaciones(Long id);

    @Query("SELECT * FROM reservaciones entity WHERE entity.colaborador_resrvaciones_id IS NULL")
    Flux<Reservaciones> findAllWhereColaboradorResrvacionesIsNull();

    @Query("SELECT * FROM reservaciones entity WHERE entity.eventos_reservaciones_id = :id")
    Flux<Reservaciones> findByEventosReservaciones(Long id);

    @Query("SELECT * FROM reservaciones entity WHERE entity.eventos_reservaciones_id IS NULL")
    Flux<Reservaciones> findAllWhereEventosReservacionesIsNull();

    @Override
    <S extends Reservaciones> Mono<S> save(S entity);

    @Override
    Flux<Reservaciones> findAll();

    @Override
    Mono<Reservaciones> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ReservacionesRepositoryInternal {
    <S extends Reservaciones> Mono<S> save(S entity);

    Flux<Reservaciones> findAllBy(Pageable pageable);

    Flux<Reservaciones> findAll();

    Mono<Reservaciones> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Reservaciones> findAllBy(Pageable pageable, Criteria criteria);
}
