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
        for (Column c : columns) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public Task findTaskById(int id) {
        for (Column c : columns) {
            Task t = c.findTaskById(id);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    public Column findColumnContainingTask(int id) {
        for (Column c : columns) {
            Task t = c.findTaskById(id);
            if (t != null) {
                return c;
            }
        }
        return null;
    }
}
