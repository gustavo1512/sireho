package com.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ReservacionesSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("fecha_inicio", table, columnPrefix + "_fecha_inicio"));
        columns.add(Column.aliased("fecha_final", table, columnPrefix + "_fecha_final"));

        columns.add(Column.aliased("habitaciones_reservaciones_id", table, columnPrefix + "_habitaciones_reservaciones_id"));
        columns.add(Column.aliased("cliente_reservaciones_id", table, columnPrefix + "_cliente_reservaciones_id"));
        columns.add(Column.aliased("colaborador_resrvaciones_id", table, columnPrefix + "_colaborador_resrvaciones_id"));
        columns.add(Column.aliased("eventos_reservaciones_id", table, columnPrefix + "_eventos_reservaciones_id"));
        return columns;
    }
}
