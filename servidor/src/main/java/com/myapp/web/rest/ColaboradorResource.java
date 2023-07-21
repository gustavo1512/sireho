package com.myapp.web.rest;

import com.myapp.domain.Colaborador;
import com.myapp.repository.ColaboradorRepository;
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
 * REST controller for managing {@link com.myapp.domain.Colaborador}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ColaboradorResource {

    private final Logger log = LoggerFactory.getLogger(ColaboradorResource.class);

    private static final String ENTITY_NAME = "colaborador";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ColaboradorRepository colaboradorRepository;

    public ColaboradorResource(ColaboradorRepository colaboradorRepository) {
        this.colaboradorRepository = colaboradorRepository;
    }

    /**
     * {@code POST  /colaboradors} : Create a new colaborador.
     *
     * @param colaborador the colaborador to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new colaborador, or with status {@code 400 (Bad Request)} if the colaborador has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/colaboradors")
    public Mono<ResponseEntity<Colaborador>> createColaborador(@RequestBody Colaborador colaborador) throws URISyntaxException {
        log.debug("REST request to save Colaborador : {}", colaborador);
        if (colaborador.getId() != null) {
            throw new BadRequestAlertException("A new colaborador cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return colaboradorRepository
            .save(colaborador)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/colaboradors/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /colaboradors/:id} : Updates an existing colaborador.
     *
     * @param id the id of the colaborador to save.
     * @param colaborador the colaborador to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated colaborador,
     * or with status {@code 400 (Bad Request)} if the colaborador is not valid,
     * or with status {@code 500 (Internal Server Error)} if the colaborador couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/colaboradors/{id}")
    public Mono<ResponseEntity<Colaborador>> updateColaborador(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Colaborador colaborador
    ) throws URISyntaxException {
        log.debug("REST request to update Colaborador : {}, {}", id, colaborador);
        if (colaborador.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, colaborador.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return colaboradorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return colaboradorRepository
                    .save(colaborador)
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
     * {@code PATCH  /colaboradors/:id} : Partial updates given fields of an existing colaborador, field will ignore if it is null
     *
     * @param id the id of the colaborador to save.
     * @param colaborador the colaborador to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated colaborador,
     * or with status {@code 400 (Bad Request)} if the colaborador is not valid,
     * or with status {@code 404 (Not Found)} if the colaborador is not found,
     * or with status {@code 500 (Internal Server Error)} if the colaborador couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/colaboradors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Colaborador>> partialUpdateColaborador(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Colaborador colaborador
    ) throws URISyntaxException {
        log.debug("REST request to partial update Colaborador partially : {}, {}", id, colaborador);
        if (colaborador.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, colaborador.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return colaboradorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Colaborador> result = colaboradorRepository
                    .findById(colaborador.getId())
                    .map(existingColaborador -> {
                        if (colaborador.getNombreColaborador() != null) {
                            existingColaborador.setNombreColaborador(colaborador.getNombreColaborador());
                        }
                        if (colaborador.getCargo() != null) {
                            existingColaborador.setCargo(colaborador.getCargo());
                        }
                        if (colaborador.getDepartamento() != null) {
                            existingColaborador.setDepartamento(colaborador.getDepartamento());
                        }
                        if (colaborador.getNumTelefono() != null) {
                            existingColaborador.setNumTelefono(colaborador.getNumTelefono());
                        }
                        if (colaborador.getCorreo() != null) {
                            existingColaborador.setCorreo(colaborador.getCorreo());
                        }

                        return existingColaborador;
                    })
                    .flatMap(colaboradorRepository::save);

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
     * {@code GET  /colaboradors} : get all the colaboradors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of colaboradors in body.
     */
    @GetMapping(value = "/colaboradors", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Colaborador>> getAllColaboradors() {
        log.debug("REST request to get all Colaboradors");
        return colaboradorRepository.findAll().collectList();
    }

    /**
     * {@code GET  /colaboradors} : get all the colaboradors as a stream.
     * @return the {@link Flux} of colaboradors.
     */
    @GetMapping(value = "/colaboradors", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Colaborador> getAllColaboradorsAsStream() {
        log.debug("REST request to get all Colaboradors as a stream");
        return colaboradorRepository.findAll();
    }

    /**
     * {@code GET  /colaboradors/:id} : get the "id" colaborador.
     *
     * @param id the id of the colaborador to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the colaborador, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/colaboradors/{id}")
    public Mono<ResponseEntity<Colaborador>> getColaborador(@PathVariable Long id) {
        log.debug("REST request to get Colaborador : {}", id);
        Mono<Colaborador> colaborador = colaboradorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(colaborador);
    }

    /**
     * {@code DELETE  /colaboradors/:id} : delete the "id" colaborador.
     *
     * @param id the id of the colaborador to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/colaboradors/{id}")
    public Mono<ResponseEntity<Void>> deleteColaborador(@PathVariable Long id) {
        log.debug("REST request to delete Colaborador : {}", id);
        return colaboradorRepository
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
