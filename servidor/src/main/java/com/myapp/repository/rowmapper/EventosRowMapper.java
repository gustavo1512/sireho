package com.myapp.repository.rowmapper;

import com.myapp.domain.Eventos;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Eventos}, with proper type conversions.
 */
@Service
public class EventosRowMapper implements BiFunction<Row, String, Eventos> {

    private final ColumnConverter converter;

    public EventosRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Eventos} stored in the database.
     */
    @Override
    public Eventos apply(Row row, String prefix) {
        Eventos entity = new Eventos();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombreEvento(converter.fromRow(row, prefix + "_nombre_evento", String.class));
        entity.setFechaHora(converter.fromRow(row, prefix + "_fecha_hora", Instant.class));
        entity.setResponsable(converter.fromRow(row, prefix + "_responsable", String.class));
        entity.setCapacidad(converter.fromRow(row, prefix + "_capacidad", Long.class));
        entity.setParticipantes(converter.fromRow(row, prefix + "_participantes", Long.class));
        return entity;
    }
}
