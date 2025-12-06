package com.taskboard.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final List<Column> columns = new ArrayList<>();

    public void addColumn(Column column) {
        columns.add(column);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Column getColumnByName(String name) {
        for (Column col : columns) {
            if (col.getName().equalsIgnoreCase(name)) {
                return col;
            }
        }
        return null;
    }

    public Column findColumnContainingTask(int id) {
        for (Column col : columns) {
            Task t = col.findTaskById(id);
            if (t != null) {
                return col;
            }
        }
        return null;
    }
}
