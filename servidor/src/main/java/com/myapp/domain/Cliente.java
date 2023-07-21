package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Cliente.
 */
@Table("cliente")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nombre")
    private String nombre;

    @Column("apellido")
    private String apellido;

    @Column("direccion")
    private String direccion;

    @Column("correo")
    private String correo;

    @Column("telefono")
    private String telefono;

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

    public Cliente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Cliente nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public Cliente apellido(String apellido) {
        this.setApellido(apellido);
        return this;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public Cliente direccion(String direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return this.correo;
    }

    public Cliente correo(String correo) {
        this.setCorreo(correo);
        return this;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public Cliente telefono(String telefono) {
        this.setTelefono(telefono);
        return this;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Set<Reservaciones> getReservaciones() {
        return this.reservaciones;
    }

    public void setReservaciones(Set<Reservaciones> reservaciones) {
        if (this.reservaciones != null) {
            this.reservaciones.forEach(i -> i.setClienteReservaciones(null));
        }
        if (reservaciones != null) {
            reservaciones.forEach(i -> i.setClienteReservaciones(this));
        }
        this.reservaciones = reservaciones;
    }

    public Cliente reservaciones(Set<Reservaciones> reservaciones) {
        this.setReservaciones(reservaciones);
        return this;
    }

    public Cliente addReservaciones(Reservaciones reservaciones) {
        this.reservaciones.add(reservaciones);
        reservaciones.setClienteReservaciones(this);
        return this;
    }

    public Cliente removeReservaciones(Reservaciones reservaciones) {
        this.reservaciones.remove(reservaciones);
        reservaciones.setClienteReservaciones(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente)) {
            return false;
        }
        return id != null && id.equals(((Cliente) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cliente{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", apellido='" + getApellido() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", correo='" + getCorreo() + "'" +
            ", telefono='" + getTelefono() + "'" +
            "}";
    }
}
