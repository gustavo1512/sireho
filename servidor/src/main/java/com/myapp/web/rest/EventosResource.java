package com.myapp.web.rest;

import com.myapp.domain.Eventos;
import com.myapp.repository.EventosRepository;
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
 * REST controller for managing {@link com.myapp.domain.Eventos}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EventosResource {

    private final Logger log = LoggerFactory.getLogger(EventosResource.class);

    private static final String ENTITY_NAME = "eventos";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventosRepository eventosRepository;

    public EventosResource(EventosRepository eventosRepository) {
        this.eventosRepository = eventosRepository;
    }

    /**
     * {@code POST  /eventos} : Create a new eventos.
     *
     * @param eventos the eventos to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventos, or with status {@code 400 (Bad Request)} if the eventos has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/eventos")
    public Mono<ResponseEntity<Eventos>> createEventos(@RequestBody Eventos eventos) throws URISyntaxException {
        log.debug("REST request to save Eventos : {}", eventos);
        if (eventos.getId() != null) {
            throw new BadRequestAlertException("A new eventos cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return eventosRepository
            .save(eventos)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/eventos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /eventos/:id} : Updates an existing eventos.
     *
     * @param id the id of the eventos to save.
     * @param eventos the eventos to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventos,
     * or with status {@code 400 (Bad Request)} if the eventos is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventos couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/eventos/{id}")
    public Mono<ResponseEntity<Eventos>> updateEventos(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Eventos eventos
    ) throws URISyntaxException {
        log.debug("REST request to update Eventos : {}, {}", id, eventos);
        if (eventos.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventos.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventosRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return eventosRepository
                    .save(eventos)
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
     * {@code PATCH  /eventos/:id} : Partial updates given fields of an existing eventos, field will ignore if it is null
     *
     * @param id the id of the eventos to save.
     * @param eventos the eventos to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventos,
     * or with status {@code 400 (Bad Request)} if the eventos is not valid,
     * or with status {@code 404 (Not Found)} if the eventos is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventos couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/eventos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Eventos>> partialUpdateEventos(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Eventos eventos
    ) throws URISyntaxException {
        log.debug("REST request to partial update Eventos partially : {}, {}", id, eventos);
        if (eventos.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventos.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eventosRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Eventos> result = eventosRepository
                    .findById(eventos.getId())
                    .map(existingEventos -> {
                        if (eventos.getNombreEvento() != null) {
                            existingEventos.setNombreEvento(eventos.getNombreEvento());
                        }
                        if (eventos.getFechaHora() != null) {
                            existingEventos.setFechaHora(eventos.getFechaHora());
                        }
                        if (eventos.getResponsable() != null) {
                            existingEventos.setResponsable(eventos.getResponsable());
                        }
                        if (eventos.getCapacidad() != null) {
                            existingEventos.setCapacidad(eventos.getCapacidad());
                        }
                        if (eventos.getParticipantes() != null) {
                            existingEventos.setParticipantes(eventos.getParticipantes());
                        }

                        return existingEventos;
                    })
                    .flatMap(eventosRepository::save);

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
     * {@code GET  /eventos} : get all the eventos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventos in body.
     */
    @GetMapping(value = "/eventos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Eventos>> getAllEventos() {
        log.debug("REST request to get all Eventos");
        return eventosRepository.findAll().collectList();
    }

    /**
     * {@code GET  /eventos} : get all the eventos as a stream.
     * @return the {@link Flux} of eventos.
     */
    @GetMapping(value = "/eventos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Eventos> getAllEventosAsStream() {
        log.debug("REST request to get all Eventos as a stream");
        return eventosRepository.findAll();
    }

    /**
     * {@code GET  /eventos/:id} : get the "id" eventos.
     *
     * @param id the id of the eventos to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventos, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/eventos/{id}")
    public Mono<ResponseEntity<Eventos>> getEventos(@PathVariable Long id) {
        log.debug("REST request to get Eventos : {}", id);
        Mono<Eventos> eventos = eventosRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(eventos);
    }

    /**
     * {@code DELETE  /eventos/:id} : delete the "id" eventos.
     *
     * @param id the id of the eventos to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/eventos/{id}")
    public Mono<ResponseEntity<Void>> deleteEventos(@PathVariable Long id) {
        log.debug("REST request to delete Eventos : {}", id);
        return eventosRepository
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
