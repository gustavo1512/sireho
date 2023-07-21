package com.myapp.web.rest;

import com.myapp.domain.Reservaciones;
import com.myapp.repository.ReservacionesRepository;
import com.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.myapp.domain.Reservaciones}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ReservacionesResource {

    private final Logger log = LoggerFactory.getLogger(ReservacionesResource.class);

    private static final String ENTITY_NAME = "reservaciones";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReservacionesRepository reservacionesRepository;

    public ReservacionesResource(ReservacionesRepository reservacionesRepository) {
        this.reservacionesRepository = reservacionesRepository;
    }

    /**
     * {@code POST  /reservaciones} : Create a new reservaciones.
     *
     * @param reservaciones the reservaciones to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reservaciones, or with status {@code 400 (Bad Request)} if the reservaciones has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reservaciones")
    public Mono<ResponseEntity<Reservaciones>> createReservaciones(@RequestBody Reservaciones reservaciones) throws URISyntaxException {
        log.debug("REST request to save Reservaciones : {}", reservaciones);
        if (reservaciones.getId() != null) {
            throw new BadRequestAlertException("A new reservaciones cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return reservacionesRepository
            .save(reservaciones)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/reservaciones/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /reservaciones/:id} : Updates an existing reservaciones.
     *
     * @param id the id of the reservaciones to save.
     * @param reservaciones the reservaciones to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservaciones,
     * or with status {@code 400 (Bad Request)} if the reservaciones is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reservaciones couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reservaciones/{id}")
    public Mono<ResponseEntity<Reservaciones>> updateReservaciones(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Reservaciones reservaciones
    ) throws URISyntaxException {
        log.debug("REST request to update Reservaciones : {}, {}", id, reservaciones);
        if (reservaciones.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservaciones.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return reservacionesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return reservacionesRepository
                    .save(reservaciones)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /reservaciones/:id} : Partial updates given fields of an existing reservaciones, field will ignore if it is null
     *
     * @param id the id of the reservaciones to save.
     * @param reservaciones the reservaciones to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reservaciones,
     * or with status {@code 400 (Bad Request)} if the reservaciones is not valid,
     * or with status {@code 404 (Not Found)} if the reservaciones is not found,
     * or with status {@code 500 (Internal Server Error)} if the reservaciones couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reservaciones/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Reservaciones>> partialUpdateReservaciones(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Reservaciones reservaciones
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reservaciones partially : {}, {}", id, reservaciones);
        if (reservaciones.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservaciones.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return reservacionesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Reservaciones> result = reservacionesRepository
                    .findById(reservaciones.getId())
                    .map(existingReservaciones -> {
                        if (reservaciones.getFechaInicio() != null) {
                            existingReservaciones.setFechaInicio(reservaciones.getFechaInicio());
                        }
                        if (reservaciones.getFechaFinal() != null) {
                            existingReservaciones.setFechaFinal(reservaciones.getFechaFinal());
                        }

                        return existingReservaciones;
                    })
                    .flatMap(reservacionesRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /reservaciones} : get all the reservaciones.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reservaciones in body.
     */
    @GetMapping(value = "/reservaciones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Reservaciones>> getAllReservaciones() {
        log.debug("REST request to get all Reservaciones");
        return reservacionesRepository.findAll().collectList();
    }

    /**
     * {@code GET  /reservaciones} : get all the reservaciones as a stream.
     * @return the {@link Flux} of reservaciones.
     */
    @GetMapping(value = "/reservaciones", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Reservaciones> getAllReservacionesAsStream() {
        log.debug("REST request to get all Reservaciones as a stream");
        return reservacionesRepository.findAll();
    }

    /**
     * {@code GET  /reservaciones/:id} : get the "id" reservaciones.
     *
     * @param id the id of the reservaciones to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reservaciones, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reservaciones/{id}")
    public Mono<ResponseEntity<Reservaciones>> getReservaciones(@PathVariable Long id) {
        log.debug("REST request to get Reservaciones : {}", id);
        Mono<Reservaciones> reservaciones = reservacionesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(reservaciones);
    }

    /**
     * {@code DELETE  /reservaciones/:id} : delete the "id" reservaciones.
     *
     * @param id the id of the reservaciones to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reservaciones/{id}")
    public Mono<ResponseEntity<Void>> deleteReservaciones(@PathVariable Long id) {
        log.debug("REST request to delete Reservaciones : {}", id);
        return reservacionesRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
