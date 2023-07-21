package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Reservaciones.
 */
@Table("reservaciones")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reservaciones implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("fecha_inicio")
    private Instant fechaInicio;

    @Column("fecha_final")
    private Instant fechaFinal;

    @Transient
    private Habitaciones habitacionesReservaciones;

    @Transient
    @JsonIgnoreProperties(value = { "reservaciones" }, allowSetters = true)
    private Cliente clienteReservaciones;

    @Transient
    @JsonIgnoreProperties(value = { "reservaciones", "tipoCargoColaborador" }, allowSetters = true)
    private Colaborador colaboradorResrvaciones;

    @Transient
    @JsonIgnoreProperties(value = { "reservaciones" }, allowSetters = true)
    private Eventos eventosReservaciones;

    @Column("habitaciones_reservaciones_id")
    private Long habitacionesReservacionesId;

    @Column("cliente_reservaciones_id")
    private Long clienteReservacionesId;

    @Column("colaborador_resrvaciones_id")
    private Long colaboradorResrvacionesId;

    @Column("eventos_reservaciones_id")
    private Long eventosReservacionesId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reservaciones id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getFechaInicio() {
        return this.fechaInicio;
    }

    public Reservaciones fechaInicio(Instant fechaInicio) {
        this.setFechaInicio(fechaInicio);
        return this;
    }

    public void setFechaInicio(Instant fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Instant getFechaFinal() {
        return this.fechaFinal;
    }

    public Reservaciones fechaFinal(Instant fechaFinal) {
        this.setFechaFinal(fechaFinal);
        return this;
    }

    public void setFechaFinal(Instant fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Habitaciones getHabitacionesReservaciones() {
        return this.habitacionesReservaciones;
    }

    public void setHabitacionesReservaciones(Habitaciones habitaciones) {
        this.habitacionesReservaciones = habitaciones;
        this.habitacionesReservacionesId = habitaciones != null ? habitaciones.getId() : null;
    }

    public Reservaciones habitacionesReservaciones(Habitaciones habitaciones) {
        this.setHabitacionesReservaciones(habitaciones);
        return this;
    }

    public Cliente getClienteReservaciones() {
        return this.clienteReservaciones;
    }

    public void setClienteReservaciones(Cliente cliente) {
        this.clienteReservaciones = cliente;
        this.clienteReservacionesId = cliente != null ? cliente.getId() : null;
    }

    public Reservaciones clienteReservaciones(Cliente cliente) {
        this.setClienteReservaciones(cliente);
        return this;
    }

    public Colaborador getColaboradorResrvaciones() {
        return this.colaboradorResrvaciones;
    }

    public void setColaboradorResrvaciones(Colaborador colaborador) {
        this.colaboradorResrvaciones = colaborador;
        this.colaboradorResrvacionesId = colaborador != null ? colaborador.getId() : null;
    }

    public Reservaciones colaboradorResrvaciones(Colaborador colaborador) {
        this.setColaboradorResrvaciones(colaborador);
        return this;
    }

    public Eventos getEventosReservaciones() {
        return this.eventosReservaciones;
    }

    public void setEventosReservaciones(Eventos eventos) {
        this.eventosReservaciones = eventos;
        this.eventosReservacionesId = eventos != null ? eventos.getId() : null;
    }

    public Reservaciones eventosReservaciones(Eventos eventos) {
        this.setEventosReservaciones(eventos);
        return this;
    }

    public Long getHabitacionesReservacionesId() {
        return this.habitacionesReservacionesId;
    }

    public void setHabitacionesReservacionesId(Long habitaciones) {
        this.habitacionesReservacionesId = habitaciones;
    }

    public Long getClienteReservacionesId() {
        return this.clienteReservacionesId;
    }

    public void setClienteReservacionesId(Long cliente) {
        this.clienteReservacionesId = cliente;
    }

    public Long getColaboradorResrvacionesId() {
        return this.colaboradorResrvacionesId;
    }

    public void setColaboradorResrvacionesId(Long colaborador) {
        this.colaboradorResrvacionesId = colaborador;
    }

    public Long getEventosReservacionesId() {
        return this.eventosReservacionesId;
    }

    public void setEventosReservacionesId(Long eventos) {
        this.eventosReservacionesId = eventos;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservaciones)) {
            return false;
        }
        return id != null && id.equals(((Reservaciones) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reservaciones{" +
            "id=" + getId() +
            ", fechaInicio='" + getFechaInicio() + "'" +
            ", fechaFinal='" + getFechaFinal() + "'" +
            "}";
    }
}
