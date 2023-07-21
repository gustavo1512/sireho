package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.myapp.IntegrationTest;
import com.myapp.domain.Colaborador;
import com.myapp.repository.ColaboradorRepository;
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
 * Integration tests for the {@link ColaboradorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ColaboradorResourceIT {

    private static final String DEFAULT_NOMBRE_COLABORADOR = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_COLABORADOR = "BBBBBBBBBB";

    private static final String DEFAULT_CARGO = "AAAAAAAAAA";
    private static final String UPDATED_CARGO = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTAMENTO = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTAMENTO = "BBBBBBBBBB";

    private static final Long DEFAULT_NUM_TELEFONO = 1L;
    private static final Long UPDATED_NUM_TELEFONO = 2L;

    private static final String DEFAULT_CORREO = "AAAAAAAAAA";
    private static final String UPDATED_CORREO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/colaboradors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Colaborador colaborador;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Colaborador createEntity(EntityManager em) {
        Colaborador colaborador = new Colaborador()
            .nombreColaborador(DEFAULT_NOMBRE_COLABORADOR)
            .cargo(DEFAULT_CARGO)
            .departamento(DEFAULT_DEPARTAMENTO)
            .numTelefono(DEFAULT_NUM_TELEFONO)
            .correo(DEFAULT_CORREO);
        return colaborador;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Colaborador createUpdatedEntity(EntityManager em) {
        Colaborador colaborador = new Colaborador()
            .nombreColaborador(UPDATED_NOMBRE_COLABORADOR)
            .cargo(UPDATED_CARGO)
            .departamento(UPDATED_DEPARTAMENTO)
            .numTelefono(UPDATED_NUM_TELEFONO)
            .correo(UPDATED_CORREO);
        return colaborador;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Colaborador.class).block();
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
        colaborador = createEntity(em);
    }

    @Test
    void createColaborador() throws Exception {
        int databaseSizeBeforeCreate = colaboradorRepository.findAll().collectList().block().size();
        // Create the Colaborador
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(colaborador))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeCreate + 1);
        Colaborador testColaborador = colaboradorList.get(colaboradorList.size() - 1);
        assertThat(testColaborador.getNombreColaborador()).isEqualTo(DEFAULT_NOMBRE_COLABORADOR);
        assertThat(testColaborador.getCargo()).isEqualTo(DEFAULT_CARGO);
        assertThat(testColaborador.getDepartamento()).isEqualTo(DEFAULT_DEPARTAMENTO);
        assertThat(testColaborador.getNumTelefono()).isEqualTo(DEFAULT_NUM_TELEFONO);
        assertThat(testColaborador.getCorreo()).isEqualTo(DEFAULT_CORREO);
    }

    @Test
    void createColaboradorWithExistingId() throws Exception {
        // Create the Colaborador with an existing ID
        colaborador.setId(1L);

        int databaseSizeBeforeCreate = colaboradorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(colaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllColaboradorsAsStream() {
        // Initialize the database
        colaboradorRepository.save(colaborador).block();

        List<Colaborador> colaboradorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Colaborador.class)
            .getResponseBody()
            .filter(colaborador::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(colaboradorList).isNotNull();
        assertThat(colaboradorList).hasSize(1);
        Colaborador testColaborador = colaboradorList.get(0);
        assertThat(testColaborador.getNombreColaborador()).isEqualTo(DEFAULT_NOMBRE_COLABORADOR);
        assertThat(testColaborador.getCargo()).isEqualTo(DEFAULT_CARGO);
        assertThat(testColaborador.getDepartamento()).isEqualTo(DEFAULT_DEPARTAMENTO);
        assertThat(testColaborador.getNumTelefono()).isEqualTo(DEFAULT_NUM_TELEFONO);
        assertThat(testColaborador.getCorreo()).isEqualTo(DEFAULT_CORREO);
    }

    @Test
    void getAllColaboradors() {
        // Initialize the database
        colaboradorRepository.save(colaborador).block();

        // Get all the colaboradorList
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
            .value(hasItem(colaborador.getId().intValue()))
            .jsonPath("$.[*].nombreColaborador")
            .value(hasItem(DEFAULT_NOMBRE_COLABORADOR))
            .jsonPath("$.[*].cargo")
            .value(hasItem(DEFAULT_CARGO))
            .jsonPath("$.[*].departamento")
            .value(hasItem(DEFAULT_DEPARTAMENTO))
            .jsonPath("$.[*].numTelefono")
            .value(hasItem(DEFAULT_NUM_TELEFONO.intValue()))
            .jsonPath("$.[*].correo")
            .value(hasItem(DEFAULT_CORREO));
    }

    @Test
    void getColaborador() {
        // Initialize the database
        colaboradorRepository.save(colaborador).block();

        // Get the colaborador
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, colaborador.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(colaborador.getId().intValue()))
            .jsonPath("$.nombreColaborador")
            .value(is(DEFAULT_NOMBRE_COLABORADOR))
            .jsonPath("$.cargo")
            .value(is(DEFAULT_CARGO))
            .jsonPath("$.departamento")
            .value(is(DEFAULT_DEPARTAMENTO))
            .jsonPath("$.numTelefono")
            .value(is(DEFAULT_NUM_TELEFONO.intValue()))
            .jsonPath("$.correo")
            .value(is(DEFAULT_CORREO));
    }

    @Test
    void getNonExistingColaborador() {
        // Get the colaborador
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingColaborador() throws Exception {
        // Initialize the database
        colaboradorRepository.save(colaborador).block();

        int databaseSizeBeforeUpdate = colaboradorRepository.findAll().collectList().block().size();

        // Update the colaborador
        Colaborador updatedColaborador = colaboradorRepository.findById(colaborador.getId()).block();
        updatedColaborador
            .nombreColaborador(UPDATED_NOMBRE_COLABORADOR)
            .cargo(UPDATED_CARGO)
            .departamento(UPDATED_DEPARTAMENTO)
            .numTelefono(UPDATED_NUM_TELEFONO)
            .correo(UPDATED_CORREO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedColaborador.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedColaborador))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeUpdate);
        Colaborador testColaborador = colaboradorList.get(colaboradorList.size() - 1);
        assertThat(testColaborador.getNombreColaborador()).isEqualTo(UPDATED_NOMBRE_COLABORADOR);
        assertThat(testColaborador.getCargo()).isEqualTo(UPDATED_CARGO);
        assertThat(testColaborador.getDepartamento()).isEqualTo(UPDATED_DEPARTAMENTO);
        assertThat(testColaborador.getNumTelefono()).isEqualTo(UPDATED_NUM_TELEFONO);
        assertThat(testColaborador.getCorreo()).isEqualTo(UPDATED_CORREO);
    }

    @Test
    void putNonExistingColaborador() throws Exception {
        int databaseSizeBeforeUpdate = colaboradorRepository.findAll().collectList().block().size();
        colaborador.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, colaborador.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(colaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchColaborador() throws Exception {
        int databaseSizeBeforeUpdate = colaboradorRepository.findAll().collectList().block().size();
        colaborador.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(colaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamColaborador() throws Exception {
        int databaseSizeBeforeUpdate = colaboradorRepository.findAll().collectList().block().size();
        colaborador.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(colaborador))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateColaboradorWithPatch() throws Exception {
        // Initialize the database
        colaboradorRepository.save(colaborador).block();

        int databaseSizeBeforeUpdate = colaboradorRepository.findAll().collectList().block().size();

        // Update the colaborador using partial update
        Colaborador partialUpdatedColaborador = new Colaborador();
        partialUpdatedColaborador.setId(colaborador.getId());

        partialUpdatedColaborador.nombreColaborador(UPDATED_NOMBRE_COLABORADOR).numTelefono(UPDATED_NUM_TELEFONO).correo(UPDATED_CORREO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedColaborador.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedColaborador))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeUpdate);
        Colaborador testColaborador = colaboradorList.get(colaboradorList.size() - 1);
        assertThat(testColaborador.getNombreColaborador()).isEqualTo(UPDATED_NOMBRE_COLABORADOR);
        assertThat(testColaborador.getCargo()).isEqualTo(DEFAULT_CARGO);
        assertThat(testColaborador.getDepartamento()).isEqualTo(DEFAULT_DEPARTAMENTO);
        assertThat(testColaborador.getNumTelefono()).isEqualTo(UPDATED_NUM_TELEFONO);
        assertThat(testColaborador.getCorreo()).isEqualTo(UPDATED_CORREO);
    }

    @Test
    void fullUpdateColaboradorWithPatch() throws Exception {
        // Initialize the database
        colaboradorRepository.save(colaborador).block();

        int databaseSizeBeforeUpdate = colaboradorRepository.findAll().collectList().block().size();

        // Update the colaborador using partial update
        Colaborador partialUpdatedColaborador = new Colaborador();
        partialUpdatedColaborador.setId(colaborador.getId());

        partialUpdatedColaborador
            .nombreColaborador(UPDATED_NOMBRE_COLABORADOR)
            .cargo(UPDATED_CARGO)
            .departamento(UPDATED_DEPARTAMENTO)
            .numTelefono(UPDATED_NUM_TELEFONO)
            .correo(UPDATED_CORREO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedColaborador.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedColaborador))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeUpdate);
        Colaborador testColaborador = colaboradorList.get(colaboradorList.size() - 1);
        assertThat(testColaborador.getNombreColaborador()).isEqualTo(UPDATED_NOMBRE_COLABORADOR);
        assertThat(testColaborador.getCargo()).isEqualTo(UPDATED_CARGO);
        assertThat(testColaborador.getDepartamento()).isEqualTo(UPDATED_DEPARTAMENTO);
        assertThat(testColaborador.getNumTelefono()).isEqualTo(UPDATED_NUM_TELEFONO);
        assertThat(testColaborador.getCorreo()).isEqualTo(UPDATED_CORREO);
    }

    @Test
    void patchNonExistingColaborador() throws Exception {
        int databaseSizeBeforeUpdate = colaboradorRepository.findAll().collectList().block().size();
        colaborador.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, colaborador.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(colaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchColaborador() throws Exception {
        int databaseSizeBeforeUpdate = colaboradorRepository.findAll().collectList().block().size();
        colaborador.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(colaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamColaborador() throws Exception {
        int databaseSizeBeforeUpdate = colaboradorRepository.findAll().collectList().block().size();
        colaborador.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(colaborador))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Colaborador in the database
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteColaborador() {
        // Initialize the database
        colaboradorRepository.save(colaborador).block();

        int databaseSizeBeforeDelete = colaboradorRepository.findAll().collectList().block().size();

        // Delete the colaborador
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, colaborador.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Colaborador> colaboradorList = colaboradorRepository.findAll().collectList().block();
        assertThat(colaboradorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
