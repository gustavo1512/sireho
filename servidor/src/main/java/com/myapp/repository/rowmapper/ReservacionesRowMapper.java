package com.myapp.repository.rowmapper;

import com.myapp.domain.Reservaciones;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Reservaciones}, with proper type conversions.
 */
@Service
public class ReservacionesRowMapper implements BiFunction<Row, String, Reservaciones> {

    private final ColumnConverter converter;

    public ReservacionesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Reservaciones} stored in the database.
     */
    @Override
    public Reservaciones apply(Row row, String prefix) {
        Reservaciones entity = new Reservaciones();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFechaInicio(converter.fromRow(row, prefix + "_fecha_inicio", Instant.class));
        entity.setFechaFinal(converter.fromRow(row, prefix + "_fecha_final", Instant.class));
        entity.setHabitacionesReservacionesId(converter.fromRow(row, prefix + "_habitaciones_reservaciones_id", Long.class));
        entity.setClienteReservacionesId(converter.fromRow(row, prefix + "_cliente_reservaciones_id", Long.class));
        entity.setColaboradorResrvacionesId(converter.fromRow(row, prefix + "_colaborador_resrvaciones_id", Long.class));
        entity.setEventosReservacionesId(converter.fromRow(row, prefix + "_eventos_reservaciones_id", Long.class));
        return entity;
    }
}
