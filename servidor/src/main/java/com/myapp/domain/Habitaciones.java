package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Habitaciones.
 */
@Table("habitaciones")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Habitaciones implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("tipo")
    private String tipo;

    @Column("piso")
    private Long piso;

    @Column("disponible")
    private Boolean disponible;

    @Transient
    @JsonIgnoreProperties(value = { "habitaciones" }, allowSetters = true)
    private Factura facturaHabitaciones;

    @Transient
    private Reservaciones reservaciones;

    @Column("factura_habitaciones_id")
    private Long facturaHabitacionesId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Habitaciones id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return this.tipo;
    }

    public Habitaciones tipo(String tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getPiso() {
        return this.piso;
    }

    public Habitaciones piso(Long piso) {
        this.setPiso(piso);
        return this;
    }

    public void setPiso(Long piso) {
        this.piso = piso;
    }

    public Boolean getDisponible() {
        return this.disponible;
    }

    public Habitaciones disponible(Boolean disponible) {
        this.setDisponible(disponible);
        return this;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Factura getFacturaHabitaciones() {
        return this.facturaHabitaciones;
    }

    public void setFacturaHabitaciones(Factura factura) {
        this.facturaHabitaciones = factura;
        this.facturaHabitacionesId = factura != null ? factura.getId() : null;
    }

    public Habitaciones facturaHabitaciones(Factura factura) {
        this.setFacturaHabitaciones(factura);
        return this;
    }

    public Reservaciones getReservaciones() {
        return this.reservaciones;
    }

    public void setReservaciones(Reservaciones reservaciones) {
        if (this.reservaciones != null) {
            this.reservaciones.setHabitacionesReservaciones(null);
        }
        if (reservaciones != null) {
            reservaciones.setHabitacionesReservaciones(this);
        }
        this.reservaciones = reservaciones;
    }

    public Habitaciones reservaciones(Reservaciones reservaciones) {
        this.setReservaciones(reservaciones);
        return this;
    }

    public Long getFacturaHabitacionesId() {
        return this.facturaHabitacionesId;
    }

    public void setFacturaHabitacionesId(Long factura) {
        this.facturaHabitacionesId = factura;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Habitaciones)) {
            return false;
        }
        return id != null && id.equals(((Habitaciones) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Habitaciones{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", piso=" + getPiso() +
            ", disponible='" + getDisponible() + "'" +
            "}";
    }
}
