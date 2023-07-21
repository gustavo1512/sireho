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
 * A TipoCargo.
 */
@Table("tipo_cargo")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TipoCargo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nombre_cargo")
    private String nombreCargo;

    @Transient
    @JsonIgnoreProperties(value = { "reservaciones", "tipoCargoColaborador" }, allowSetters = true)
    private Set<Colaborador> colaboradors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TipoCargo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCargo() {
        return this.nombreCargo;
    }

    public TipoCargo nombreCargo(String nombreCargo) {
        this.setNombreCargo(nombreCargo);
        return this;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    public Set<Colaborador> getColaboradors() {
        return this.colaboradors;
    }

    public void setColaboradors(Set<Colaborador> colaboradors) {
        if (this.colaboradors != null) {
            this.colaboradors.forEach(i -> i.setTipoCargoColaborador(null));
        }
        if (colaboradors != null) {
            colaboradors.forEach(i -> i.setTipoCargoColaborador(this));
        }
        this.colaboradors = colaboradors;
    }

    public TipoCargo colaboradors(Set<Colaborador> colaboradors) {
        this.setColaboradors(colaboradors);
        return this;
    }

    public TipoCargo addColaborador(Colaborador colaborador) {
        this.colaboradors.add(colaborador);
        colaborador.setTipoCargoColaborador(this);
        return this;
    }

    public TipoCargo removeColaborador(Colaborador colaborador) {
        this.colaboradors.remove(colaborador);
        colaborador.setTipoCargoColaborador(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TipoCargo)) {
            return false;
        }
        return id != null && id.equals(((TipoCargo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TipoCargo{" +
            "id=" + getId() +
            ", nombreCargo='" + getNombreCargo() + "'" +
            "}";
    }
}
