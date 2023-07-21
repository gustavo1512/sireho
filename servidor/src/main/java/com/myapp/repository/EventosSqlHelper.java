package com.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EventosSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nombre_evento", table, columnPrefix + "_nombre_evento"));
        columns.add(Column.aliased("fecha_hora", table, columnPrefix + "_fecha_hora"));
        columns.add(Column.aliased("responsable", table, columnPrefix + "_responsable"));
        columns.add(Column.aliased("capacidad", table, columnPrefix + "_capacidad"));
        columns.add(Column.aliased("participantes", table, columnPrefix + "_participantes"));

        return columns;
    }
}
