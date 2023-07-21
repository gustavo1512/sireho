package com.myapp.web.rest;

import com.myapp.domain.Habitaciones;
import com.myapp.repository.HabitacionesRepository;
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
 * REST controller for managing {@link com.myapp.domain.Habitaciones}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HabitacionesResource {

    private final Logger log = LoggerFactory.getLogger(HabitacionesResource.class);

    private static final String ENTITY_NAME = "habitaciones";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HabitacionesRepository habitacionesRepository;

    public HabitacionesResource(HabitacionesRepository habitacionesRepository) {
        this.habitacionesRepository = habitacionesRepository;
    }

    /**
     * {@code POST  /habitaciones} : Create a new habitaciones.
     *
     * @param habitaciones the habitaciones to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new habitaciones, or with status {@code 400 (Bad Request)} if the habitaciones has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/habitaciones")
    public Mono<ResponseEntity<Habitaciones>> createHabitaciones(@RequestBody Habitaciones habitaciones) throws URISyntaxException {
        log.debug("REST request to save Habitaciones : {}", habitaciones);
        if (habitaciones.getId() != null) {
            throw new BadRequestAlertException("A new habitaciones cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return habitacionesRepository
            .save(habitaciones)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/habitaciones/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /habitaciones/:id} : Updates an existing habitaciones.
     *
     * @param id the id of the habitaciones to save.
     * @param habitaciones the habitaciones to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated habitaciones,
     * or with status {@code 400 (Bad Request)} if the habitaciones is not valid,
     * or with status {@code 500 (Internal Server Error)} if the habitaciones couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/habitaciones/{id}")
    public Mono<ResponseEntity<Habitaciones>> updateHabitaciones(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Habitaciones habitaciones
    ) throws URISyntaxException {
        log.debug("REST request to update Habitaciones : {}, {}", id, habitaciones);
        if (habitaciones.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, habitaciones.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return habitacionesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return habitacionesRepository
                    .save(habitaciones)
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
     * {@code PATCH  /habitaciones/:id} : Partial updates given fields of an existing habitaciones, field will ignore if it is null
     *
     * @param id the id of the habitaciones to save.
     * @param habitaciones the habitaciones to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated habitaciones,
     * or with status {@code 400 (Bad Request)} if the habitaciones is not valid,
     * or with status {@code 404 (Not Found)} if the habitaciones is not found,
     * or with status {@code 500 (Internal Server Error)} if the habitaciones couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/habitaciones/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Habitaciones>> partialUpdateHabitaciones(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Habitaciones habitaciones
    ) throws URISyntaxException {
        log.debug("REST request to partial update Habitaciones partially : {}, {}", id, habitaciones);
        if (habitaciones.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, habitaciones.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return habitacionesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Habitaciones> result = habitacionesRepository
                    .findById(habitaciones.getId())
                    .map(existingHabitaciones -> {
                        if (habitaciones.getTipo() != null) {
                            existingHabitaciones.setTipo(habitaciones.getTipo());
                        }
                        if (habitaciones.getPiso() != null) {
                            existingHabitaciones.setPiso(habitaciones.getPiso());
                        }
                        if (habitaciones.getDisponible() != null) {
                            existingHabitaciones.setDisponible(habitaciones.getDisponible());
                        }

                        return existingHabitaciones;
                    })
                    .flatMap(habitacionesRepository::save);

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
     * {@code GET  /habitaciones} : get all the habitaciones.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of habitaciones in body.
     */
    @GetMapping(value = "/habitaciones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Habitaciones>> getAllHabitaciones(@RequestParam(required = false) String filter) {
        if ("reservaciones-is-null".equals(filter)) {
            log.debug("REST request to get all Habitacioness where reservaciones is null");
            return habitacionesRepository.findAllWhereReservacionesIsNull().collectList();
        }
        log.debug("REST request to get all Habitaciones");
        return habitacionesRepository.findAll().collectList();
    }

    /**
     * {@code GET  /habitaciones} : get all the habitaciones as a stream.
     * @return the {@link Flux} of habitaciones.
     */
    @GetMapping(value = "/habitaciones", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Habitaciones> getAllHabitacionesAsStream() {
        log.debug("REST request to get all Habitaciones as a stream");
        return habitacionesRepository.findAll();
    }

    /**
     * {@code GET  /habitaciones/:id} : get the "id" habitaciones.
     *
     * @param id the id of the habitaciones to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the habitaciones, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/habitaciones/{id}")
    public Mono<ResponseEntity<Habitaciones>> getHabitaciones(@PathVariable Long id) {
        log.debug("REST request to get Habitaciones : {}", id);
        Mono<Habitaciones> habitaciones = habitacionesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(habitaciones);
    }

    /**
     * {@code DELETE  /habitaciones/:id} : delete the "id" habitaciones.
     *
     * @param id the id of the habitaciones to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/habitaciones/{id}")
    public Mono<ResponseEntity<Void>> deleteHabitaciones(@PathVariable Long id) {
        log.debug("REST request to delete Habitaciones : {}", id);
        return habitacionesRepository
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
