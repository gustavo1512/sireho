package com.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FacturaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("cantidad_paga", table, columnPrefix + "_cantidad_paga"));
        columns.add(Column.aliased("fecha_pago", table, columnPrefix + "_fecha_pago"));
        columns.add(Column.aliased("metodo_pgo", table, columnPrefix + "_metodo_pgo"));

        return columns;
    }
}
