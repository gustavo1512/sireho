package com.myapp.repository.rowmapper;

import com.myapp.domain.TipoCargo;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TipoCargo}, with proper type conversions.
 */
@Service
public class TipoCargoRowMapper implements BiFunction<Row, String, TipoCargo> {

    private final ColumnConverter converter;

    public TipoCargoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TipoCargo} stored in the database.
     */
    @Override
    public TipoCargo apply(Row row, String prefix) {
        TipoCargo entity = new TipoCargo();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombreCargo(converter.fromRow(row, prefix + "_nombre_cargo", String.class));
        return entity;
    }
}
