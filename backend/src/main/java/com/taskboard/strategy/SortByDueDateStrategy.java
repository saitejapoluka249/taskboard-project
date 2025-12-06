package com.taskboard.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.taskboard.model.Task;

public class SortByDueDateStrategy implements TaskSortStrategy {

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> copy = new ArrayList<>(tasks);
        copy.sort(Comparator.comparing(Task::getDueDate, (d1, d2) -> {
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1; // nulls last
            if (d2 == null) return -1;
            return d1.compareTo(d2);
        }));
        return copy;
    }

    @Override
    public String getName() {
        return "By Due Date";
    }
}