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
 * A Factura.
 */
@Table("factura")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("cantidad_paga")
    private Long cantidadPaga;

    @Column("fecha_pago")
    private Instant fechaPago;

    @Column("metodo_pgo")
    private String metodoPgo;

    @Transient
    @JsonIgnoreProperties(value = { "facturaHabitaciones", "reservaciones" }, allowSetters = true)
    private Set<Habitaciones> habitaciones = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Factura id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCantidadPaga() {
        return this.cantidadPaga;
    }

    public Factura cantidadPaga(Long cantidadPaga) {
        this.setCantidadPaga(cantidadPaga);
        return this;
    }

    public void setCantidadPaga(Long cantidadPaga) {
        this.cantidadPaga = cantidadPaga;
    }

    public Instant getFechaPago() {
        return this.fechaPago;
    }

    public Factura fechaPago(Instant fechaPago) {
        this.setFechaPago(fechaPago);
        return this;
    }

    public void setFechaPago(Instant fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPgo() {
        return this.metodoPgo;
    }

    public Factura metodoPgo(String metodoPgo) {
        this.setMetodoPgo(metodoPgo);
        return this;
    }

    public void setMetodoPgo(String metodoPgo) {
        this.metodoPgo = metodoPgo;
    }

    public Set<Habitaciones> getHabitaciones() {
        return this.habitaciones;
    }

    public void setHabitaciones(Set<Habitaciones> habitaciones) {
        if (this.habitaciones != null) {
            this.habitaciones.forEach(i -> i.setFacturaHabitaciones(null));
        }
        if (habitaciones != null) {
            habitaciones.forEach(i -> i.setFacturaHabitaciones(this));
        }
        this.habitaciones = habitaciones;
    }

    public Factura habitaciones(Set<Habitaciones> habitaciones) {
        this.setHabitaciones(habitaciones);
        return this;
    }

    public Factura addHabitaciones(Habitaciones habitaciones) {
        this.habitaciones.add(habitaciones);
        habitaciones.setFacturaHabitaciones(this);
        return this;
    }

    public Factura removeHabitaciones(Habitaciones habitaciones) {
        this.habitaciones.remove(habitaciones);
        habitaciones.setFacturaHabitaciones(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factura)) {
            return false;
        }
        return id != null && id.equals(((Factura) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Factura{" +
            "id=" + getId() +
            ", cantidadPaga=" + getCantidadPaga() +
            ", fechaPago='" + getFechaPago() + "'" +
            ", metodoPgo='" + getMetodoPgo() + "'" +
            "}";
    }
}
