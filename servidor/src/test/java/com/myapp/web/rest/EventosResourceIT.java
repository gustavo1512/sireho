package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.myapp.IntegrationTest;
import com.myapp.domain.Eventos;
import com.myapp.repository.EntityManager;
import com.myapp.repository.EventosRepository;
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
 * Integration tests for the {@link EventosResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EventosResourceIT {

    private static final String DEFAULT_NOMBRE_EVENTO = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_EVENTO = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_HORA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_HORA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RESPONSABLE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSABLE = "BBBBBBBBBB";

    private static final Long DEFAULT_CAPACIDAD = 1L;
    private static final Long UPDATED_CAPACIDAD = 2L;

    private static final Long DEFAULT_PARTICIPANTES = 1L;
    private static final Long UPDATED_PARTICIPANTES = 2L;

    private static final String ENTITY_API_URL = "/api/eventos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventosRepository eventosRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Eventos eventos;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Eventos createEntity(EntityManager em) {
        Eventos eventos = new Eventos()
            .nombreEvento(DEFAULT_NOMBRE_EVENTO)
            .fechaHora(DEFAULT_FECHA_HORA)
            .responsable(DEFAULT_RESPONSABLE)
            .capacidad(DEFAULT_CAPACIDAD)
            .participantes(DEFAULT_PARTICIPANTES);
        return eventos;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Eventos createUpdatedEntity(EntityManager em) {
        Eventos eventos = new Eventos()
            .nombreEvento(UPDATED_NOMBRE_EVENTO)
            .fechaHora(UPDATED_FECHA_HORA)
            .responsable(UPDATED_RESPONSABLE)
            .capacidad(UPDATED_CAPACIDAD)
            .participantes(UPDATED_PARTICIPANTES);
        return eventos;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Eventos.class).block();
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
        eventos = createEntity(em);
    }

    @Test
    void createEventos() throws Exception {
        int databaseSizeBeforeCreate = eventosRepository.findAll().collectList().block().size();
        // Create the Eventos
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventos))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeCreate + 1);
        Eventos testEventos = eventosList.get(eventosList.size() - 1);
        assertThat(testEventos.getNombreEvento()).isEqualTo(DEFAULT_NOMBRE_EVENTO);
        assertThat(testEventos.getFechaHora()).isEqualTo(DEFAULT_FECHA_HORA);
        assertThat(testEventos.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testEventos.getCapacidad()).isEqualTo(DEFAULT_CAPACIDAD);
        assertThat(testEventos.getParticipantes()).isEqualTo(DEFAULT_PARTICIPANTES);
    }

    @Test
    void createEventosWithExistingId() throws Exception {
        // Create the Eventos with an existing ID
        eventos.setId(1L);

        int databaseSizeBeforeCreate = eventosRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventos))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEventosAsStream() {
        // Initialize the database
        eventosRepository.save(eventos).block();

        List<Eventos> eventosList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Eventos.class)
            .getResponseBody()
            .filter(eventos::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(eventosList).isNotNull();
        assertThat(eventosList).hasSize(1);
        Eventos testEventos = eventosList.get(0);
        assertThat(testEventos.getNombreEvento()).isEqualTo(DEFAULT_NOMBRE_EVENTO);
        assertThat(testEventos.getFechaHora()).isEqualTo(DEFAULT_FECHA_HORA);
        assertThat(testEventos.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testEventos.getCapacidad()).isEqualTo(DEFAULT_CAPACIDAD);
        assertThat(testEventos.getParticipantes()).isEqualTo(DEFAULT_PARTICIPANTES);
    }

    @Test
    void getAllEventos() {
        // Initialize the database
        eventosRepository.save(eventos).block();

        // Get all the eventosList
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
            .value(hasItem(eventos.getId().intValue()))
            .jsonPath("$.[*].nombreEvento")
            .value(hasItem(DEFAULT_NOMBRE_EVENTO))
            .jsonPath("$.[*].fechaHora")
            .value(hasItem(DEFAULT_FECHA_HORA.toString()))
            .jsonPath("$.[*].responsable")
            .value(hasItem(DEFAULT_RESPONSABLE))
            .jsonPath("$.[*].capacidad")
            .value(hasItem(DEFAULT_CAPACIDAD.intValue()))
            .jsonPath("$.[*].participantes")
            .value(hasItem(DEFAULT_PARTICIPANTES.intValue()));
    }

    @Test
    void getEventos() {
        // Initialize the database
        eventosRepository.save(eventos).block();

        // Get the eventos
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, eventos.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(eventos.getId().intValue()))
            .jsonPath("$.nombreEvento")
            .value(is(DEFAULT_NOMBRE_EVENTO))
            .jsonPath("$.fechaHora")
            .value(is(DEFAULT_FECHA_HORA.toString()))
            .jsonPath("$.responsable")
            .value(is(DEFAULT_RESPONSABLE))
            .jsonPath("$.capacidad")
            .value(is(DEFAULT_CAPACIDAD.intValue()))
            .jsonPath("$.participantes")
            .value(is(DEFAULT_PARTICIPANTES.intValue()));
    }

    @Test
    void getNonExistingEventos() {
        // Get the eventos
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEventos() throws Exception {
        // Initialize the database
        eventosRepository.save(eventos).block();

        int databaseSizeBeforeUpdate = eventosRepository.findAll().collectList().block().size();

        // Update the eventos
        Eventos updatedEventos = eventosRepository.findById(eventos.getId()).block();
        updatedEventos
            .nombreEvento(UPDATED_NOMBRE_EVENTO)
            .fechaHora(UPDATED_FECHA_HORA)
            .responsable(UPDATED_RESPONSABLE)
            .capacidad(UPDATED_CAPACIDAD)
            .participantes(UPDATED_PARTICIPANTES);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEventos.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEventos))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
        Eventos testEventos = eventosList.get(eventosList.size() - 1);
        assertThat(testEventos.getNombreEvento()).isEqualTo(UPDATED_NOMBRE_EVENTO);
        assertThat(testEventos.getFechaHora()).isEqualTo(UPDATED_FECHA_HORA);
        assertThat(testEventos.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testEventos.getCapacidad()).isEqualTo(UPDATED_CAPACIDAD);
        assertThat(testEventos.getParticipantes()).isEqualTo(UPDATED_PARTICIPANTES);
    }

    @Test
    void putNonExistingEventos() throws Exception {
        int databaseSizeBeforeUpdate = eventosRepository.findAll().collectList().block().size();
        eventos.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, eventos.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventos))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEventos() throws Exception {
        int databaseSizeBeforeUpdate = eventosRepository.findAll().collectList().block().size();
        eventos.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventos))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEventos() throws Exception {
        int databaseSizeBeforeUpdate = eventosRepository.findAll().collectList().block().size();
        eventos.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventos))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEventosWithPatch() throws Exception {
        // Initialize the database
        eventosRepository.save(eventos).block();

        int databaseSizeBeforeUpdate = eventosRepository.findAll().collectList().block().size();

        // Update the eventos using partial update
        Eventos partialUpdatedEventos = new Eventos();
        partialUpdatedEventos.setId(eventos.getId());

        partialUpdatedEventos.capacidad(UPDATED_CAPACIDAD).participantes(UPDATED_PARTICIPANTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEventos.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEventos))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
        Eventos testEventos = eventosList.get(eventosList.size() - 1);
        assertThat(testEventos.getNombreEvento()).isEqualTo(DEFAULT_NOMBRE_EVENTO);
        assertThat(testEventos.getFechaHora()).isEqualTo(DEFAULT_FECHA_HORA);
        assertThat(testEventos.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testEventos.getCapacidad()).isEqualTo(UPDATED_CAPACIDAD);
        assertThat(testEventos.getParticipantes()).isEqualTo(UPDATED_PARTICIPANTES);
    }

    @Test
    void fullUpdateEventosWithPatch() throws Exception {
        // Initialize the database
        eventosRepository.save(eventos).block();

        int databaseSizeBeforeUpdate = eventosRepository.findAll().collectList().block().size();

        // Update the eventos using partial update
        Eventos partialUpdatedEventos = new Eventos();
        partialUpdatedEventos.setId(eventos.getId());

        partialUpdatedEventos
            .nombreEvento(UPDATED_NOMBRE_EVENTO)
            .fechaHora(UPDATED_FECHA_HORA)
            .responsable(UPDATED_RESPONSABLE)
            .capacidad(UPDATED_CAPACIDAD)
            .participantes(UPDATED_PARTICIPANTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEventos.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEventos))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
        Eventos testEventos = eventosList.get(eventosList.size() - 1);
        assertThat(testEventos.getNombreEvento()).isEqualTo(UPDATED_NOMBRE_EVENTO);
        assertThat(testEventos.getFechaHora()).isEqualTo(UPDATED_FECHA_HORA);
        assertThat(testEventos.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testEventos.getCapacidad()).isEqualTo(UPDATED_CAPACIDAD);
        assertThat(testEventos.getParticipantes()).isEqualTo(UPDATED_PARTICIPANTES);
    }

    @Test
    void patchNonExistingEventos() throws Exception {
        int databaseSizeBeforeUpdate = eventosRepository.findAll().collectList().block().size();
        eventos.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, eventos.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventos))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEventos() throws Exception {
        int databaseSizeBeforeUpdate = eventosRepository.findAll().collectList().block().size();
        eventos.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventos))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEventos() throws Exception {
        int databaseSizeBeforeUpdate = eventosRepository.findAll().collectList().block().size();
        eventos.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(eventos))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Eventos in the database
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEventos() {
        // Initialize the database
        eventosRepository.save(eventos).block();

        int databaseSizeBeforeDelete = eventosRepository.findAll().collectList().block().size();

        // Delete the eventos
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, eventos.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Eventos> eventosList = eventosRepository.findAll().collectList().block();
        assertThat(eventosList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
