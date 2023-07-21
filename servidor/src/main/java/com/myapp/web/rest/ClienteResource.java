package com.myapp.web.rest;

import com.myapp.domain.Cliente;
import com.myapp.repository.ClienteRepository;
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
 * REST controller for managing {@link com.myapp.domain.Cliente}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClienteResource {

    private final Logger log = LoggerFactory.getLogger(ClienteResource.class);

    private static final String ENTITY_NAME = "cliente";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClienteRepository clienteRepository;

    public ClienteResource(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * {@code POST  /clientes} : Create a new cliente.
     *
     * @param cliente the cliente to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cliente, or with status {@code 400 (Bad Request)} if the cliente has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/clientes")
    public Mono<ResponseEntity<Cliente>> createCliente(@RequestBody Cliente cliente) throws URISyntaxException {
        log.debug("REST request to save Cliente : {}", cliente);
        if (cliente.getId() != null) {
            throw new BadRequestAlertException("A new cliente cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return clienteRepository
            .save(cliente)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/clientes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /clientes/:id} : Updates an existing cliente.
     *
     * @param id the id of the cliente to save.
     * @param cliente the cliente to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cliente,
     * or with status {@code 400 (Bad Request)} if the cliente is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cliente couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/clientes/{id}")
    public Mono<ResponseEntity<Cliente>> updateCliente(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Cliente cliente
    ) throws URISyntaxException {
        log.debug("REST request to update Cliente : {}, {}", id, cliente);
        if (cliente.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cliente.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clienteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return clienteRepository
                    .save(cliente)
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
     * {@code PATCH  /clientes/:id} : Partial updates given fields of an existing cliente, field will ignore if it is null
     *
     * @param id the id of the cliente to save.
     * @param cliente the cliente to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cliente,
     * or with status {@code 400 (Bad Request)} if the cliente is not valid,
     * or with status {@code 404 (Not Found)} if the cliente is not found,
     * or with status {@code 500 (Internal Server Error)} if the cliente couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/clientes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Cliente>> partialUpdateCliente(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Cliente cliente
    ) throws URISyntaxException {
        log.debug("REST request to partial update Cliente partially : {}, {}", id, cliente);
        if (cliente.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cliente.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clienteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Cliente> result = clienteRepository
                    .findById(cliente.getId())
                    .map(existingCliente -> {
                        if (cliente.getNombre() != null) {
                            existingCliente.setNombre(cliente.getNombre());
                        }
                        if (cliente.getApellido() != null) {
                            existingCliente.setApellido(cliente.getApellido());
                        }
                        if (cliente.getDireccion() != null) {
                            existingCliente.setDireccion(cliente.getDireccion());
                        }
                        if (cliente.getCorreo() != null) {
                            existingCliente.setCorreo(cliente.getCorreo());
                        }
                        if (cliente.getTelefono() != null) {
                            existingCliente.setTelefono(cliente.getTelefono());
                        }

                        return existingCliente;
                    })
                    .flatMap(clienteRepository::save);

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
     * {@code GET  /clientes} : get all the clientes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientes in body.
     */
    @GetMapping(value = "/clientes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Cliente>> getAllClientes() {
        log.debug("REST request to get all Clientes");
        return clienteRepository.findAll().collectList();
    }

    /**
     * {@code GET  /clientes} : get all the clientes as a stream.
     * @return the {@link Flux} of clientes.
     */
    @GetMapping(value = "/clientes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Cliente> getAllClientesAsStream() {
        log.debug("REST request to get all Clientes as a stream");
        return clienteRepository.findAll();
    }

    /**
     * {@code GET  /clientes/:id} : get the "id" cliente.
     *
     * @param id the id of the cliente to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cliente, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clientes/{id}")
    public Mono<ResponseEntity<Cliente>> getCliente(@PathVariable Long id) {
        log.debug("REST request to get Cliente : {}", id);
        Mono<Cliente> cliente = clienteRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cliente);
    }

    /**
     * {@code DELETE  /clientes/:id} : delete the "id" cliente.
     *
     * @param id the id of the cliente to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/clientes/{id}")
    public Mono<ResponseEntity<Void>> deleteCliente(@PathVariable Long id) {
        log.debug("REST request to delete Cliente : {}", id);
        return clienteRepository
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
