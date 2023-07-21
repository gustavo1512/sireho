package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.myapp.IntegrationTest;
import com.myapp.domain.TipoCargo;
import com.myapp.repository.EntityManager;
import com.myapp.repository.TipoCargoRepository;
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
 * Integration tests for the {@link TipoCargoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TipoCargoResourceIT {

    private static final String DEFAULT_NOMBRE_CARGO = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_CARGO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tipo-cargos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TipoCargoRepository tipoCargoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TipoCargo tipoCargo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoCargo createEntity(EntityManager em) {
        TipoCargo tipoCargo = new TipoCargo().nombreCargo(DEFAULT_NOMBRE_CARGO);
        return tipoCargo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoCargo createUpdatedEntity(EntityManager em) {
        TipoCargo tipoCargo = new TipoCargo().nombreCargo(UPDATED_NOMBRE_CARGO);
        return tipoCargo;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TipoCargo.class).block();
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
        tipoCargo = createEntity(em);
    }

    @Test
    void createTipoCargo() throws Exception {
        int databaseSizeBeforeCreate = tipoCargoRepository.findAll().collectList().block().size();
        // Create the TipoCargo
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoCargo))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeCreate + 1);
        TipoCargo testTipoCargo = tipoCargoList.get(tipoCargoList.size() - 1);
        assertThat(testTipoCargo.getNombreCargo()).isEqualTo(DEFAULT_NOMBRE_CARGO);
    }

    @Test
    void createTipoCargoWithExistingId() throws Exception {
        // Create the TipoCargo with an existing ID
        tipoCargo.setId(1L);

        int databaseSizeBeforeCreate = tipoCargoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoCargo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTipoCargosAsStream() {
        // Initialize the database
        tipoCargoRepository.save(tipoCargo).block();

        List<TipoCargo> tipoCargoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TipoCargo.class)
            .getResponseBody()
            .filter(tipoCargo::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tipoCargoList).isNotNull();
        assertThat(tipoCargoList).hasSize(1);
        TipoCargo testTipoCargo = tipoCargoList.get(0);
        assertThat(testTipoCargo.getNombreCargo()).isEqualTo(DEFAULT_NOMBRE_CARGO);
    }

    @Test
    void getAllTipoCargos() {
        // Initialize the database
        tipoCargoRepository.save(tipoCargo).block();

        // Get all the tipoCargoList
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
            .value(hasItem(tipoCargo.getId().intValue()))
            .jsonPath("$.[*].nombreCargo")
            .value(hasItem(DEFAULT_NOMBRE_CARGO));
    }

    @Test
    void getTipoCargo() {
        // Initialize the database
        tipoCargoRepository.save(tipoCargo).block();

        // Get the tipoCargo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tipoCargo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tipoCargo.getId().intValue()))
            .jsonPath("$.nombreCargo")
            .value(is(DEFAULT_NOMBRE_CARGO));
    }

    @Test
    void getNonExistingTipoCargo() {
        // Get the tipoCargo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTipoCargo() throws Exception {
        // Initialize the database
        tipoCargoRepository.save(tipoCargo).block();

        int databaseSizeBeforeUpdate = tipoCargoRepository.findAll().collectList().block().size();

        // Update the tipoCargo
        TipoCargo updatedTipoCargo = tipoCargoRepository.findById(tipoCargo.getId()).block();
        updatedTipoCargo.nombreCargo(UPDATED_NOMBRE_CARGO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTipoCargo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTipoCargo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeUpdate);
        TipoCargo testTipoCargo = tipoCargoList.get(tipoCargoList.size() - 1);
        assertThat(testTipoCargo.getNombreCargo()).isEqualTo(UPDATED_NOMBRE_CARGO);
    }

    @Test
    void putNonExistingTipoCargo() throws Exception {
        int databaseSizeBeforeUpdate = tipoCargoRepository.findAll().collectList().block().size();
        tipoCargo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tipoCargo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoCargo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTipoCargo() throws Exception {
        int databaseSizeBeforeUpdate = tipoCargoRepository.findAll().collectList().block().size();
        tipoCargo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoCargo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTipoCargo() throws Exception {
        int databaseSizeBeforeUpdate = tipoCargoRepository.findAll().collectList().block().size();
        tipoCargo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoCargo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTipoCargoWithPatch() throws Exception {
        // Initialize the database
        tipoCargoRepository.save(tipoCargo).block();

        int databaseSizeBeforeUpdate = tipoCargoRepository.findAll().collectList().block().size();

        // Update the tipoCargo using partial update
        TipoCargo partialUpdatedTipoCargo = new TipoCargo();
        partialUpdatedTipoCargo.setId(tipoCargo.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTipoCargo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTipoCargo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeUpdate);
        TipoCargo testTipoCargo = tipoCargoList.get(tipoCargoList.size() - 1);
        assertThat(testTipoCargo.getNombreCargo()).isEqualTo(DEFAULT_NOMBRE_CARGO);
    }

    @Test
    void fullUpdateTipoCargoWithPatch() throws Exception {
        // Initialize the database
        tipoCargoRepository.save(tipoCargo).block();

        int databaseSizeBeforeUpdate = tipoCargoRepository.findAll().collectList().block().size();

        // Update the tipoCargo using partial update
        TipoCargo partialUpdatedTipoCargo = new TipoCargo();
        partialUpdatedTipoCargo.setId(tipoCargo.getId());

        partialUpdatedTipoCargo.nombreCargo(UPDATED_NOMBRE_CARGO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTipoCargo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTipoCargo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeUpdate);
        TipoCargo testTipoCargo = tipoCargoList.get(tipoCargoList.size() - 1);
        assertThat(testTipoCargo.getNombreCargo()).isEqualTo(UPDATED_NOMBRE_CARGO);
    }

    @Test
    void patchNonExistingTipoCargo() throws Exception {
        int databaseSizeBeforeUpdate = tipoCargoRepository.findAll().collectList().block().size();
        tipoCargo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tipoCargo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoCargo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTipoCargo() throws Exception {
        int databaseSizeBeforeUpdate = tipoCargoRepository.findAll().collectList().block().size();
        tipoCargo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoCargo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTipoCargo() throws Exception {
        int databaseSizeBeforeUpdate = tipoCargoRepository.findAll().collectList().block().size();
        tipoCargo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoCargo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TipoCargo in the database
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTipoCargo() {
        // Initialize the database
        tipoCargoRepository.save(tipoCargo).block();

        int databaseSizeBeforeDelete = tipoCargoRepository.findAll().collectList().block().size();

        // Delete the tipoCargo
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tipoCargo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<TipoCargo> tipoCargoList = tipoCargoRepository.findAll().collectList().block();
        assertThat(tipoCargoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
