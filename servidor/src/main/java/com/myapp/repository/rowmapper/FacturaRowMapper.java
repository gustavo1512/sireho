package com.myapp.repository.rowmapper;

import com.myapp.domain.Factura;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Factura}, with proper type conversions.
 */
@Service
public class FacturaRowMapper implements BiFunction<Row, String, Factura> {

    private final ColumnConverter converter;

    public FacturaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Factura} stored in the database.
     */
    @Override
    public Factura apply(Row row, String prefix) {
        Factura entity = new Factura();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCantidadPaga(converter.fromRow(row, prefix + "_cantidad_paga", Long.class));
        entity.setFechaPago(converter.fromRow(row, prefix + "_fecha_pago", Instant.class));
        entity.setMetodoPgo(converter.fromRow(row, prefix + "_metodo_pgo", String.class));
        return entity;
    }
}
