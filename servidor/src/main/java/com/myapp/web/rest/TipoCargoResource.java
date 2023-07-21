package com.myapp.web.rest;

import com.myapp.domain.TipoCargo;
import com.myapp.repository.TipoCargoRepository;
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
 * REST controller for managing {@link com.myapp.domain.TipoCargo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TipoCargoResource {

    private final Logger log = LoggerFactory.getLogger(TipoCargoResource.class);

    private static final String ENTITY_NAME = "tipoCargo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TipoCargoRepository tipoCargoRepository;

    public TipoCargoResource(TipoCargoRepository tipoCargoRepository) {
        this.tipoCargoRepository = tipoCargoRepository;
    }

    /**
     * {@code POST  /tipo-cargos} : Create a new tipoCargo.
     *
     * @param tipoCargo the tipoCargo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tipoCargo, or with status {@code 400 (Bad Request)} if the tipoCargo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tipo-cargos")
    public Mono<ResponseEntity<TipoCargo>> createTipoCargo(@RequestBody TipoCargo tipoCargo) throws URISyntaxException {
        log.debug("REST request to save TipoCargo : {}", tipoCargo);
        if (tipoCargo.getId() != null) {
            throw new BadRequestAlertException("A new tipoCargo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tipoCargoRepository
            .save(tipoCargo)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/tipo-cargos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tipo-cargos/:id} : Updates an existing tipoCargo.
     *
     * @param id the id of the tipoCargo to save.
     * @param tipoCargo the tipoCargo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoCargo,
     * or with status {@code 400 (Bad Request)} if the tipoCargo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tipoCargo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tipo-cargos/{id}")
    public Mono<ResponseEntity<TipoCargo>> updateTipoCargo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TipoCargo tipoCargo
    ) throws URISyntaxException {
        log.debug("REST request to update TipoCargo : {}, {}", id, tipoCargo);
        if (tipoCargo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoCargo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tipoCargoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tipoCargoRepository
                    .save(tipoCargo)
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
     * {@code PATCH  /tipo-cargos/:id} : Partial updates given fields of an existing tipoCargo, field will ignore if it is null
     *
     * @param id the id of the tipoCargo to save.
     * @param tipoCargo the tipoCargo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoCargo,
     * or with status {@code 400 (Bad Request)} if the tipoCargo is not valid,
     * or with status {@code 404 (Not Found)} if the tipoCargo is not found,
     * or with status {@code 500 (Internal Server Error)} if the tipoCargo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tipo-cargos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TipoCargo>> partialUpdateTipoCargo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TipoCargo tipoCargo
    ) throws URISyntaxException {
        log.debug("REST request to partial update TipoCargo partially : {}, {}", id, tipoCargo);
        if (tipoCargo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoCargo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tipoCargoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TipoCargo> result = tipoCargoRepository
                    .findById(tipoCargo.getId())
                    .map(existingTipoCargo -> {
                        if (tipoCargo.getNombreCargo() != null) {
                            existingTipoCargo.setNombreCargo(tipoCargo.getNombreCargo());
                        }

                        return existingTipoCargo;
                    })
                    .flatMap(tipoCargoRepository::save);

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
     * {@code GET  /tipo-cargos} : get all the tipoCargos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tipoCargos in body.
     */
    @GetMapping(value = "/tipo-cargos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<TipoCargo>> getAllTipoCargos() {
        log.debug("REST request to get all TipoCargos");
        return tipoCargoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /tipo-cargos} : get all the tipoCargos as a stream.
     * @return the {@link Flux} of tipoCargos.
     */
    @GetMapping(value = "/tipo-cargos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TipoCargo> getAllTipoCargosAsStream() {
        log.debug("REST request to get all TipoCargos as a stream");
        return tipoCargoRepository.findAll();
    }

    /**
     * {@code GET  /tipo-cargos/:id} : get the "id" tipoCargo.
     *
     * @param id the id of the tipoCargo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tipoCargo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tipo-cargos/{id}")
    public Mono<ResponseEntity<TipoCargo>> getTipoCargo(@PathVariable Long id) {
        log.debug("REST request to get TipoCargo : {}", id);
        Mono<TipoCargo> tipoCargo = tipoCargoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tipoCargo);
    }

    /**
     * {@code DELETE  /tipo-cargos/:id} : delete the "id" tipoCargo.
     *
     * @param id the id of the tipoCargo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tipo-cargos/{id}")
    public Mono<ResponseEntity<Void>> deleteTipoCargo(@PathVariable Long id) {
        log.debug("REST request to delete TipoCargo : {}", id);
        return tipoCargoRepository
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
