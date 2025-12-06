package com.taskboard.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;

import com.taskboard.model.Task;

public class SortByDueDateStrategy implements TaskSortStrategy {

    private static final String STRATEGY_NAME = "By Due Date";

    private static final int EQUAL = 0;
    private static final int AFTER = 1;
    private static final int BEFORE = -1;

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> tasksCopy = new ArrayList<>(tasks);
        tasksCopy.sort((t1, t2) -> compareDates(t1.getDueDate(), t2.getDueDate()));

        return tasksCopy;
    }

    private int compareDates(LocalDate d1, LocalDate d2) {
        if (d1 == null && d2 == null) {
            return EQUAL;
        }
        if (d1 == null) {
            return AFTER;
        }
        if (d2 == null) {
            return BEFORE;
        }
        return d1.compareTo(d2);
    }

    @Override
    public String getName() {
        return STRATEGY_NAME;
    }
}