package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.myapp.IntegrationTest;
import com.myapp.domain.Reservaciones;
import com.myapp.repository.EntityManager;
import com.myapp.repository.ReservacionesRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ReservacionesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ReservacionesResourceIT {

    private static final Instant DEFAULT_FECHA_INICIO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_INICIO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FECHA_FINAL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_FINAL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reservaciones";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReservacionesRepository reservacionesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Reservaciones reservaciones;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservaciones createEntity(EntityManager em) {
        Reservaciones reservaciones = new Reservaciones().fechaInicio(DEFAULT_FECHA_INICIO).fechaFinal(DEFAULT_FECHA_FINAL);
        return reservaciones;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reservaciones createUpdatedEntity(EntityManager em) {
        Reservaciones reservaciones = new Reservaciones().fechaInicio(UPDATED_FECHA_INICIO).fechaFinal(UPDATED_FECHA_FINAL);
        return reservaciones;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Reservaciones.class).block();
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
        reservaciones = createEntity(em);
    }

    @Test
    void createReservaciones() throws Exception {
        int databaseSizeBeforeCreate = reservacionesRepository.findAll().collectList().block().size();
        // Create the Reservaciones
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservaciones))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeCreate + 1);
        Reservaciones testReservaciones = reservacionesList.get(reservacionesList.size() - 1);
        assertThat(testReservaciones.getFechaInicio()).isEqualTo(DEFAULT_FECHA_INICIO);
        assertThat(testReservaciones.getFechaFinal()).isEqualTo(DEFAULT_FECHA_FINAL);
    }

    @Test
    void createReservacionesWithExistingId() throws Exception {
        // Create the Reservaciones with an existing ID
        reservaciones.setId(1L);

        int databaseSizeBeforeCreate = reservacionesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllReservacionesAsStream() {
        // Initialize the database
        reservacionesRepository.save(reservaciones).block();

        List<Reservaciones> reservacionesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Reservaciones.class)
            .getResponseBody()
            .filter(reservaciones::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(reservacionesList).isNotNull();
        assertThat(reservacionesList).hasSize(1);
        Reservaciones testReservaciones = reservacionesList.get(0);
        assertThat(testReservaciones.getFechaInicio()).isEqualTo(DEFAULT_FECHA_INICIO);
        assertThat(testReservaciones.getFechaFinal()).isEqualTo(DEFAULT_FECHA_FINAL);
    }

    @Test
    void getAllReservaciones() {
        // Initialize the database
        reservacionesRepository.save(reservaciones).block();

        // Get all the reservacionesList
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
            .value(hasItem(reservaciones.getId().intValue()))
            .jsonPath("$.[*].fechaInicio")
            .value(hasItem(DEFAULT_FECHA_INICIO.toString()))
            .jsonPath("$.[*].fechaFinal")
            .value(hasItem(DEFAULT_FECHA_FINAL.toString()));
    }

    @Test
    void getReservaciones() {
        // Initialize the database
        reservacionesRepository.save(reservaciones).block();

        // Get the reservaciones
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, reservaciones.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(reservaciones.getId().intValue()))
            .jsonPath("$.fechaInicio")
            .value(is(DEFAULT_FECHA_INICIO.toString()))
            .jsonPath("$.fechaFinal")
            .value(is(DEFAULT_FECHA_FINAL.toString()));
    }

    @Test
    void getNonExistingReservaciones() {
        // Get the reservaciones
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingReservaciones() throws Exception {
        // Initialize the database
        reservacionesRepository.save(reservaciones).block();

        int databaseSizeBeforeUpdate = reservacionesRepository.findAll().collectList().block().size();

        // Update the reservaciones
        Reservaciones updatedReservaciones = reservacionesRepository.findById(reservaciones.getId()).block();
        updatedReservaciones.fechaInicio(UPDATED_FECHA_INICIO).fechaFinal(UPDATED_FECHA_FINAL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedReservaciones.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedReservaciones))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeUpdate);
        Reservaciones testReservaciones = reservacionesList.get(reservacionesList.size() - 1);
        assertThat(testReservaciones.getFechaInicio()).isEqualTo(UPDATED_FECHA_INICIO);
        assertThat(testReservaciones.getFechaFinal()).isEqualTo(UPDATED_FECHA_FINAL);
    }

    @Test
    void putNonExistingReservaciones() throws Exception {
        int databaseSizeBeforeUpdate = reservacionesRepository.findAll().collectList().block().size();
        reservaciones.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reservaciones.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchReservaciones() throws Exception {
        int databaseSizeBeforeUpdate = reservacionesRepository.findAll().collectList().block().size();
        reservaciones.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamReservaciones() throws Exception {
        int databaseSizeBeforeUpdate = reservacionesRepository.findAll().collectList().block().size();
        reservaciones.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservaciones))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateReservacionesWithPatch() throws Exception {
        // Initialize the database
        reservacionesRepository.save(reservaciones).block();

        int databaseSizeBeforeUpdate = reservacionesRepository.findAll().collectList().block().size();

        // Update the reservaciones using partial update
        Reservaciones partialUpdatedReservaciones = new Reservaciones();
        partialUpdatedReservaciones.setId(reservaciones.getId());

        partialUpdatedReservaciones.fechaInicio(UPDATED_FECHA_INICIO).fechaFinal(UPDATED_FECHA_FINAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReservaciones.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReservaciones))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeUpdate);
        Reservaciones testReservaciones = reservacionesList.get(reservacionesList.size() - 1);
        assertThat(testReservaciones.getFechaInicio()).isEqualTo(UPDATED_FECHA_INICIO);
        assertThat(testReservaciones.getFechaFinal()).isEqualTo(UPDATED_FECHA_FINAL);
    }

    @Test
    void fullUpdateReservacionesWithPatch() throws Exception {
        // Initialize the database
        reservacionesRepository.save(reservaciones).block();

        int databaseSizeBeforeUpdate = reservacionesRepository.findAll().collectList().block().size();

        // Update the reservaciones using partial update
        Reservaciones partialUpdatedReservaciones = new Reservaciones();
        partialUpdatedReservaciones.setId(reservaciones.getId());

        partialUpdatedReservaciones.fechaInicio(UPDATED_FECHA_INICIO).fechaFinal(UPDATED_FECHA_FINAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReservaciones.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReservaciones))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeUpdate);
        Reservaciones testReservaciones = reservacionesList.get(reservacionesList.size() - 1);
        assertThat(testReservaciones.getFechaInicio()).isEqualTo(UPDATED_FECHA_INICIO);
        assertThat(testReservaciones.getFechaFinal()).isEqualTo(UPDATED_FECHA_FINAL);
    }

    @Test
    void patchNonExistingReservaciones() throws Exception {
        int databaseSizeBeforeUpdate = reservacionesRepository.findAll().collectList().block().size();
        reservaciones.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, reservaciones.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchReservaciones() throws Exception {
        int databaseSizeBeforeUpdate = reservacionesRepository.findAll().collectList().block().size();
        reservaciones.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamReservaciones() throws Exception {
        int databaseSizeBeforeUpdate = reservacionesRepository.findAll().collectList().block().size();
        reservaciones.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reservaciones))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Reservaciones in the database
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteReservaciones() {
        // Initialize the database
        reservacionesRepository.save(reservaciones).block();

        int databaseSizeBeforeDelete = reservacionesRepository.findAll().collectList().block().size();

        // Delete the reservaciones
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, reservaciones.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Reservaciones> reservacionesList = reservacionesRepository.findAll().collectList().block();
        assertThat(reservacionesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
