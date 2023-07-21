package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.myapp.IntegrationTest;
import com.myapp.domain.Habitaciones;
import com.myapp.repository.EntityManager;
import com.myapp.repository.HabitacionesRepository;
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
 * Integration tests for the {@link HabitacionesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class HabitacionesResourceIT {

    private static final String DEFAULT_TIPO = "AAAAAAAAAA";
    private static final String UPDATED_TIPO = "BBBBBBBBBB";

    private static final Long DEFAULT_PISO = 1L;
    private static final Long UPDATED_PISO = 2L;

    private static final Boolean DEFAULT_DISPONIBLE = false;
    private static final Boolean UPDATED_DISPONIBLE = true;

    private static final String ENTITY_API_URL = "/api/habitaciones";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HabitacionesRepository habitacionesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Habitaciones habitaciones;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Habitaciones createEntity(EntityManager em) {
        Habitaciones habitaciones = new Habitaciones().tipo(DEFAULT_TIPO).piso(DEFAULT_PISO).disponible(DEFAULT_DISPONIBLE);
        return habitaciones;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Habitaciones createUpdatedEntity(EntityManager em) {
        Habitaciones habitaciones = new Habitaciones().tipo(UPDATED_TIPO).piso(UPDATED_PISO).disponible(UPDATED_DISPONIBLE);
        return habitaciones;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Habitaciones.class).block();
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
        habitaciones = createEntity(em);
    }

    @Test
    void createHabitaciones() throws Exception {
        int databaseSizeBeforeCreate = habitacionesRepository.findAll().collectList().block().size();
        // Create the Habitaciones
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitaciones))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeCreate + 1);
        Habitaciones testHabitaciones = habitacionesList.get(habitacionesList.size() - 1);
        assertThat(testHabitaciones.getTipo()).isEqualTo(DEFAULT_TIPO);
        assertThat(testHabitaciones.getPiso()).isEqualTo(DEFAULT_PISO);
        assertThat(testHabitaciones.getDisponible()).isEqualTo(DEFAULT_DISPONIBLE);
    }

    @Test
    void createHabitacionesWithExistingId() throws Exception {
        // Create the Habitaciones with an existing ID
        habitaciones.setId(1L);

        int databaseSizeBeforeCreate = habitacionesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllHabitacionesAsStream() {
        // Initialize the database
        habitacionesRepository.save(habitaciones).block();

        List<Habitaciones> habitacionesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Habitaciones.class)
            .getResponseBody()
            .filter(habitaciones::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(habitacionesList).isNotNull();
        assertThat(habitacionesList).hasSize(1);
        Habitaciones testHabitaciones = habitacionesList.get(0);
        assertThat(testHabitaciones.getTipo()).isEqualTo(DEFAULT_TIPO);
        assertThat(testHabitaciones.getPiso()).isEqualTo(DEFAULT_PISO);
        assertThat(testHabitaciones.getDisponible()).isEqualTo(DEFAULT_DISPONIBLE);
    }

    @Test
    void getAllHabitaciones() {
        // Initialize the database
        habitacionesRepository.save(habitaciones).block();

        // Get all the habitacionesList
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
            .value(hasItem(habitaciones.getId().intValue()))
            .jsonPath("$.[*].tipo")
            .value(hasItem(DEFAULT_TIPO))
            .jsonPath("$.[*].piso")
            .value(hasItem(DEFAULT_PISO.intValue()))
            .jsonPath("$.[*].disponible")
            .value(hasItem(DEFAULT_DISPONIBLE.booleanValue()));
    }

    @Test
    void getHabitaciones() {
        // Initialize the database
        habitacionesRepository.save(habitaciones).block();

        // Get the habitaciones
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, habitaciones.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(habitaciones.getId().intValue()))
            .jsonPath("$.tipo")
            .value(is(DEFAULT_TIPO))
            .jsonPath("$.piso")
            .value(is(DEFAULT_PISO.intValue()))
            .jsonPath("$.disponible")
            .value(is(DEFAULT_DISPONIBLE.booleanValue()));
    }

    @Test
    void getNonExistingHabitaciones() {
        // Get the habitaciones
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingHabitaciones() throws Exception {
        // Initialize the database
        habitacionesRepository.save(habitaciones).block();

        int databaseSizeBeforeUpdate = habitacionesRepository.findAll().collectList().block().size();

        // Update the habitaciones
        Habitaciones updatedHabitaciones = habitacionesRepository.findById(habitaciones.getId()).block();
        updatedHabitaciones.tipo(UPDATED_TIPO).piso(UPDATED_PISO).disponible(UPDATED_DISPONIBLE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedHabitaciones.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedHabitaciones))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeUpdate);
        Habitaciones testHabitaciones = habitacionesList.get(habitacionesList.size() - 1);
        assertThat(testHabitaciones.getTipo()).isEqualTo(UPDATED_TIPO);
        assertThat(testHabitaciones.getPiso()).isEqualTo(UPDATED_PISO);
        assertThat(testHabitaciones.getDisponible()).isEqualTo(UPDATED_DISPONIBLE);
    }

    @Test
    void putNonExistingHabitaciones() throws Exception {
        int databaseSizeBeforeUpdate = habitacionesRepository.findAll().collectList().block().size();
        habitaciones.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, habitaciones.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchHabitaciones() throws Exception {
        int databaseSizeBeforeUpdate = habitacionesRepository.findAll().collectList().block().size();
        habitaciones.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamHabitaciones() throws Exception {
        int databaseSizeBeforeUpdate = habitacionesRepository.findAll().collectList().block().size();
        habitaciones.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitaciones))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateHabitacionesWithPatch() throws Exception {
        // Initialize the database
        habitacionesRepository.save(habitaciones).block();

        int databaseSizeBeforeUpdate = habitacionesRepository.findAll().collectList().block().size();

        // Update the habitaciones using partial update
        Habitaciones partialUpdatedHabitaciones = new Habitaciones();
        partialUpdatedHabitaciones.setId(habitaciones.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedHabitaciones.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedHabitaciones))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeUpdate);
        Habitaciones testHabitaciones = habitacionesList.get(habitacionesList.size() - 1);
        assertThat(testHabitaciones.getTipo()).isEqualTo(DEFAULT_TIPO);
        assertThat(testHabitaciones.getPiso()).isEqualTo(DEFAULT_PISO);
        assertThat(testHabitaciones.getDisponible()).isEqualTo(DEFAULT_DISPONIBLE);
    }

    @Test
    void fullUpdateHabitacionesWithPatch() throws Exception {
        // Initialize the database
        habitacionesRepository.save(habitaciones).block();

        int databaseSizeBeforeUpdate = habitacionesRepository.findAll().collectList().block().size();

        // Update the habitaciones using partial update
        Habitaciones partialUpdatedHabitaciones = new Habitaciones();
        partialUpdatedHabitaciones.setId(habitaciones.getId());

        partialUpdatedHabitaciones.tipo(UPDATED_TIPO).piso(UPDATED_PISO).disponible(UPDATED_DISPONIBLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedHabitaciones.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedHabitaciones))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeUpdate);
        Habitaciones testHabitaciones = habitacionesList.get(habitacionesList.size() - 1);
        assertThat(testHabitaciones.getTipo()).isEqualTo(UPDATED_TIPO);
        assertThat(testHabitaciones.getPiso()).isEqualTo(UPDATED_PISO);
        assertThat(testHabitaciones.getDisponible()).isEqualTo(UPDATED_DISPONIBLE);
    }

    @Test
    void patchNonExistingHabitaciones() throws Exception {
        int databaseSizeBeforeUpdate = habitacionesRepository.findAll().collectList().block().size();
        habitaciones.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, habitaciones.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchHabitaciones() throws Exception {
        int databaseSizeBeforeUpdate = habitacionesRepository.findAll().collectList().block().size();
        habitaciones.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitaciones))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamHabitaciones() throws Exception {
        int databaseSizeBeforeUpdate = habitacionesRepository.findAll().collectList().block().size();
        habitaciones.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(habitaciones))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Habitaciones in the database
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteHabitaciones() {
        // Initialize the database
        habitacionesRepository.save(habitaciones).block();

        int databaseSizeBeforeDelete = habitacionesRepository.findAll().collectList().block().size();

        // Delete the habitaciones
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, habitaciones.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Habitaciones> habitacionesList = habitacionesRepository.findAll().collectList().block();
        assertThat(habitacionesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
