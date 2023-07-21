package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.myapp.IntegrationTest;
import com.myapp.domain.Cliente;
import com.myapp.repository.ClienteRepository;
import com.myapp.repository.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ClienteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ClienteResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDO = "BBBBBBBBBB";

    private static final String DEFAULT_DIRECCION = "AAAAAAAAAA";
    private static final String UPDATED_DIRECCION = "BBBBBBBBBB";

    private static final String DEFAULT_CORREO = "AAAAAAAAAA";
    private static final String UPDATED_CORREO = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFONO = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/clientes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Cliente cliente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cliente createEntity(EntityManager em) {
        Cliente cliente = new Cliente()
            .nombre(DEFAULT_NOMBRE)
            .apellido(DEFAULT_APELLIDO)
            .direccion(DEFAULT_DIRECCION)
            .correo(DEFAULT_CORREO)
            .telefono(DEFAULT_TELEFONO);
        return cliente;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cliente createUpdatedEntity(EntityManager em) {
        Cliente cliente = new Cliente()
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .direccion(UPDATED_DIRECCION)
            .correo(UPDATED_CORREO)
            .telefono(UPDATED_TELEFONO);
        return cliente;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Cliente.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        cliente = createEntity(em);
    }

    @Test
    void createCliente() throws Exception {
        int databaseSizeBeforeCreate = clienteRepository.findAll().collectList().block().size();
        // Create the Cliente
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cliente))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeCreate + 1);
        Cliente testCliente = clienteList.get(clienteList.size() - 1);
        assertThat(testCliente.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testCliente.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testCliente.getDireccion()).isEqualTo(DEFAULT_DIRECCION);
        assertThat(testCliente.getCorreo()).isEqualTo(DEFAULT_CORREO);
        assertThat(testCliente.getTelefono()).isEqualTo(DEFAULT_TELEFONO);
    }

    @Test
    void createClienteWithExistingId() throws Exception {
        // Create the Cliente with an existing ID
        cliente.setId(1L);

        int databaseSizeBeforeCreate = clienteRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cliente))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllClientesAsStream() {
        // Initialize the database
        clienteRepository.save(cliente).block();

        List<Cliente> clienteList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Cliente.class)
            .getResponseBody()
            .filter(cliente::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(clienteList).isNotNull();
        assertThat(clienteList).hasSize(1);
        Cliente testCliente = clienteList.get(0);
        assertThat(testCliente.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testCliente.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testCliente.getDireccion()).isEqualTo(DEFAULT_DIRECCION);
        assertThat(testCliente.getCorreo()).isEqualTo(DEFAULT_CORREO);
        assertThat(testCliente.getTelefono()).isEqualTo(DEFAULT_TELEFONO);
    }

    @Test
    void getAllClientes() {
        // Initialize the database
        clienteRepository.save(cliente).block();

        // Get all the clienteList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(cliente.getId().intValue()))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE))
            .jsonPath("$.[*].apellido")
            .value(hasItem(DEFAULT_APELLIDO))
            .jsonPath("$.[*].direccion")
            .value(hasItem(DEFAULT_DIRECCION))
            .jsonPath("$.[*].correo")
            .value(hasItem(DEFAULT_CORREO))
            .jsonPath("$.[*].telefono")
            .value(hasItem(DEFAULT_TELEFONO));
    }

    @Test
    void getCliente() {
        // Initialize the database
        clienteRepository.save(cliente).block();

        // Get the cliente
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cliente.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cliente.getId().intValue()))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE))
            .jsonPath("$.apellido")
            .value(is(DEFAULT_APELLIDO))
            .jsonPath("$.direccion")
            .value(is(DEFAULT_DIRECCION))
            .jsonPath("$.correo")
            .value(is(DEFAULT_CORREO))
            .jsonPath("$.telefono")
            .value(is(DEFAULT_TELEFONO));
    }

    @Test
    void getNonExistingCliente() {
        // Get the cliente
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCliente() throws Exception {
        // Initialize the database
        clienteRepository.save(cliente).block();

        int databaseSizeBeforeUpdate = clienteRepository.findAll().collectList().block().size();

        // Update the cliente
        Cliente updatedCliente = clienteRepository.findById(cliente.getId()).block();
        updatedCliente
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .direccion(UPDATED_DIRECCION)
            .correo(UPDATED_CORREO)
            .telefono(UPDATED_TELEFONO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCliente.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCliente))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
        Cliente testCliente = clienteList.get(clienteList.size() - 1);
        assertThat(testCliente.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCliente.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testCliente.getDireccion()).isEqualTo(UPDATED_DIRECCION);
        assertThat(testCliente.getCorreo()).isEqualTo(UPDATED_CORREO);
        assertThat(testCliente.getTelefono()).isEqualTo(UPDATED_TELEFONO);
    }

    @Test
    void putNonExistingCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().collectList().block().size();
        cliente.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cliente.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cliente))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().collectList().block().size();
        cliente.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cliente))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().collectList().block().size();
        cliente.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cliente))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClienteWithPatch() throws Exception {
        // Initialize the database
        clienteRepository.save(cliente).block();

        int databaseSizeBeforeUpdate = clienteRepository.findAll().collectList().block().size();

        // Update the cliente using partial update
        Cliente partialUpdatedCliente = new Cliente();
        partialUpdatedCliente.setId(cliente.getId());

        partialUpdatedCliente.direccion(UPDATED_DIRECCION).correo(UPDATED_CORREO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCliente.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCliente))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
        Cliente testCliente = clienteList.get(clienteList.size() - 1);
        assertThat(testCliente.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testCliente.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testCliente.getDireccion()).isEqualTo(UPDATED_DIRECCION);
        assertThat(testCliente.getCorreo()).isEqualTo(UPDATED_CORREO);
        assertThat(testCliente.getTelefono()).isEqualTo(DEFAULT_TELEFONO);
    }

    @Test
    void fullUpdateClienteWithPatch() throws Exception {
        // Initialize the database
        clienteRepository.save(cliente).block();

        int databaseSizeBeforeUpdate = clienteRepository.findAll().collectList().block().size();

        // Update the cliente using partial update
        Cliente partialUpdatedCliente = new Cliente();
        partialUpdatedCliente.setId(cliente.getId());

        partialUpdatedCliente
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .direccion(UPDATED_DIRECCION)
            .correo(UPDATED_CORREO)
            .telefono(UPDATED_TELEFONO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCliente.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCliente))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
        Cliente testCliente = clienteList.get(clienteList.size() - 1);
        assertThat(testCliente.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCliente.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testCliente.getDireccion()).isEqualTo(UPDATED_DIRECCION);
        assertThat(testCliente.getCorreo()).isEqualTo(UPDATED_CORREO);
        assertThat(testCliente.getTelefono()).isEqualTo(UPDATED_TELEFONO);
    }

    @Test
    void patchNonExistingCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().collectList().block().size();
        cliente.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cliente.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cliente))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().collectList().block().size();
        cliente.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cliente))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCliente() throws Exception {
        int databaseSizeBeforeUpdate = clienteRepository.findAll().collectList().block().size();
        cliente.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cliente))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cliente in the database
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCliente() {
        // Initialize the database
        clienteRepository.save(cliente).block();

        int databaseSizeBeforeDelete = clienteRepository.findAll().collectList().block().size();

        // Delete the cliente
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cliente.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Cliente> clienteList = clienteRepository.findAll().collectList().block();
        assertThat(clienteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
