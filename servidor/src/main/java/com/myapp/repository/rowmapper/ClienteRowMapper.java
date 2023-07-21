package com.myapp.repository.rowmapper;

import com.myapp.domain.Cliente;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Cliente}, with proper type conversions.
 */
@Service
public class ClienteRowMapper implements BiFunction<Row, String, Cliente> {

    private final ColumnConverter converter;

    public ClienteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Cliente} stored in the database.
     */
    @Override
    public Cliente apply(Row row, String prefix) {
        Cliente entity = new Cliente();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", String.class));
        entity.setApellido(converter.fromRow(row, prefix + "_apellido", String.class));
        entity.setDireccion(converter.fromRow(row, prefix + "_direccion", String.class));
        entity.setCorreo(converter.fromRow(row, prefix + "_correo", String.class));
        entity.setTelefono(converter.fromRow(row, prefix + "_telefono", String.class));
        return entity;
    }
}
