package com.myapp.repository.rowmapper;

import com.myapp.domain.Habitaciones;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Habitaciones}, with proper type conversions.
 */
@Service
public class HabitacionesRowMapper implements BiFunction<Row, String, Habitaciones> {

    private final ColumnConverter converter;

    public HabitacionesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Habitaciones} stored in the database.
     */
    @Override
    public Habitaciones apply(Row row, String prefix) {
        Habitaciones entity = new Habitaciones();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTipo(converter.fromRow(row, prefix + "_tipo", String.class));
        entity.setPiso(converter.fromRow(row, prefix + "_piso", Long.class));
        entity.setDisponible(converter.fromRow(row, prefix + "_disponible", Boolean.class));
        entity.setFacturaHabitacionesId(converter.fromRow(row, prefix + "_factura_habitaciones_id", Long.class));
        return entity;
    }
}
