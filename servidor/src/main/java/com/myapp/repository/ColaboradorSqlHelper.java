package com.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ColaboradorSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nombre_colaborador", table, columnPrefix + "_nombre_colaborador"));
        columns.add(Column.aliased("cargo", table, columnPrefix + "_cargo"));
        columns.add(Column.aliased("departamento", table, columnPrefix + "_departamento"));
        columns.add(Column.aliased("num_telefono", table, columnPrefix + "_num_telefono"));
        columns.add(Column.aliased("correo", table, columnPrefix + "_correo"));

        columns.add(Column.aliased("tipo_cargo_colaborador_id", table, columnPrefix + "_tipo_cargo_colaborador_id"));
        return columns;
    }
}
