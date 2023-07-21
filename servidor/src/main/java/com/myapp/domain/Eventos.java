package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Eventos.
 */
@Table("eventos")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Eventos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nombre_evento")
    private String nombreEvento;

    @Column("fecha_hora")
    private Instant fechaHora;

    @Column("responsable")
    private String responsable;

    @Column("capacidad")
    private Long capacidad;

    @Column("participantes")
    private Long participantes;

    @Transient
    @JsonIgnoreProperties(
        value = { "habitacionesReservaciones", "clienteReservaciones", "colaboradorResrvaciones", "eventosReservaciones" },
        allowSetters = true
    )
    private Set<Reservaciones> reservaciones = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Eventos id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEvento() {
        return this.nombreEvento;
    }

    public Eventos nombreEvento(String nombreEvento) {
        this.setNombreEvento(nombreEvento);
        return this;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public Instant getFechaHora() {
        return this.fechaHora;
    }

    public Eventos fechaHora(Instant fechaHora) {
        this.setFechaHora(fechaHora);
        return this;
    }

    public void setFechaHora(Instant fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getResponsable() {
        return this.responsable;
    }

    public Eventos responsable(String responsable) {
        this.setResponsable(responsable);
        return this;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public Long getCapacidad() {
        return this.capacidad;
    }

    public Eventos capacidad(Long capacidad) {
        this.setCapacidad(capacidad);
        return this;
    }

    public void setCapacidad(Long capacidad) {
        this.capacidad = capacidad;
    }

    public Long getParticipantes() {
        return this.participantes;
    }

    public Eventos participantes(Long participantes) {
        this.setParticipantes(participantes);
        return this;
    }

    public void setParticipantes(Long participantes) {
        this.participantes = participantes;
    }

    public Set<Reservaciones> getReservaciones() {
        return this.reservaciones;
    }

    public void setReservaciones(Set<Reservaciones> reservaciones) {
        if (this.reservaciones != null) {
            this.reservaciones.forEach(i -> i.setEventosReservaciones(null));
        }
        if (reservaciones != null) {
            reservaciones.forEach(i -> i.setEventosReservaciones(this));
        }
        this.reservaciones = reservaciones;
    }

    public Eventos reservaciones(Set<Reservaciones> reservaciones) {
        this.setReservaciones(reservaciones);
        return this;
    }

    public Eventos addReservaciones(Reservaciones reservaciones) {
        this.reservaciones.add(reservaciones);
        reservaciones.setEventosReservaciones(this);
        return this;
    }

    public Eventos removeReservaciones(Reservaciones reservaciones) {
        this.reservaciones.remove(reservaciones);
        reservaciones.setEventosReservaciones(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Eventos)) {
            return false;
        }
        return id != null && id.equals(((Eventos) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Eventos{" +
            "id=" + getId() +
            ", nombreEvento='" + getNombreEvento() + "'" +
            ", fechaHora='" + getFechaHora() + "'" +
            ", responsable='" + getResponsable() + "'" +
            ", capacidad=" + getCapacidad() +
            ", participantes=" + getParticipantes() +
            "}";
    }
}
