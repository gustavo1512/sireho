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
 * A Colaborador.
 */
@Table("colaborador")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Colaborador implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nombre_colaborador")
    private String nombreColaborador;

    @Column("cargo")
    private String cargo;

    @Column("departamento")
    private String departamento;

    @Column("num_telefono")
    private Long numTelefono;

    @Column("correo")
    private String correo;

    @Transient
    @JsonIgnoreProperties(
        value = { "habitacionesReservaciones", "clienteReservaciones", "colaboradorResrvaciones", "eventosReservaciones" },
        allowSetters = true
    )
    private Set<Reservaciones> reservaciones = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "colaboradors" }, allowSetters = true)
    private TipoCargo tipoCargoColaborador;

    @Column("tipo_cargo_colaborador_id")
    private Long tipoCargoColaboradorId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Colaborador id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreColaborador() {
        return this.nombreColaborador;
    }

    public Colaborador nombreColaborador(String nombreColaborador) {
        this.setNombreColaborador(nombreColaborador);
        return this;
    }

    public void setNombreColaborador(String nombreColaborador) {
        this.nombreColaborador = nombreColaborador;
    }

    public String getCargo() {
        return this.cargo;
    }

    public Colaborador cargo(String cargo) {
        this.setCargo(cargo);
        return this;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return this.departamento;
    }

    public Colaborador departamento(String departamento) {
        this.setDepartamento(departamento);
        return this;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Long getNumTelefono() {
        return this.numTelefono;
    }

    public Colaborador numTelefono(Long numTelefono) {
        this.setNumTelefono(numTelefono);
        return this;
    }

    public void setNumTelefono(Long numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getCorreo() {
        return this.correo;
    }

    public Colaborador correo(String correo) {
        this.setCorreo(correo);
        return this;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Set<Reservaciones> getReservaciones() {
        return this.reservaciones;
    }

    public void setReservaciones(Set<Reservaciones> reservaciones) {
        if (this.reservaciones != null) {
            this.reservaciones.forEach(i -> i.setColaboradorResrvaciones(null));
        }
        if (reservaciones != null) {
            reservaciones.forEach(i -> i.setColaboradorResrvaciones(this));
        }
        this.reservaciones = reservaciones;
    }

    public Colaborador reservaciones(Set<Reservaciones> reservaciones) {
        this.setReservaciones(reservaciones);
        return this;
    }

    public Colaborador addReservaciones(Reservaciones reservaciones) {
        this.reservaciones.add(reservaciones);
        reservaciones.setColaboradorResrvaciones(this);
        return this;
    }

    public Colaborador removeReservaciones(Reservaciones reservaciones) {
        this.reservaciones.remove(reservaciones);
        reservaciones.setColaboradorResrvaciones(null);
        return this;
    }

    public TipoCargo getTipoCargoColaborador() {
        return this.tipoCargoColaborador;
    }

    public void setTipoCargoColaborador(TipoCargo tipoCargo) {
        this.tipoCargoColaborador = tipoCargo;
        this.tipoCargoColaboradorId = tipoCargo != null ? tipoCargo.getId() : null;
    }

    public Colaborador tipoCargoColaborador(TipoCargo tipoCargo) {
        this.setTipoCargoColaborador(tipoCargo);
        return this;
    }

    public Long getTipoCargoColaboradorId() {
        return this.tipoCargoColaboradorId;
    }

    public void setTipoCargoColaboradorId(Long tipoCargo) {
        this.tipoCargoColaboradorId = tipoCargo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Colaborador)) {
            return false;
        }
        return id != null && id.equals(((Colaborador) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Colaborador{" +
            "id=" + getId() +
            ", nombreColaborador='" + getNombreColaborador() + "'" +
            ", cargo='" + getCargo() + "'" +
            ", departamento='" + getDepartamento() + "'" +
            ", numTelefono=" + getNumTelefono() +
            ", correo='" + getCorreo() + "'" +
            "}";
    }
}
