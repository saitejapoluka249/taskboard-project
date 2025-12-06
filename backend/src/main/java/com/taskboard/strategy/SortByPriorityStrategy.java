package com.taskboard.strategy;

import java.util.ArrayList;
import java.util.List;

import com.taskboard.model.Priority;
import com.taskboard.model.Task;

public class SortByPriorityStrategy implements TaskSortStrategy {

    private static final String STRATEGY_NAME = "By Priority";
    private static final int VALUE_HIGH = 3;
    private static final int VALUE_MEDIUM = 2;
    private static final int VALUE_LOW = 1;
    private static final int VALUE_DEFAULT = 0;

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> tasksCopy = new ArrayList<>(tasks);

        tasksCopy.sort((t1, t2) -> comparePriorities(t1.getPriority(), t2.getPriority()));

        return tasksCopy;
    }

    private int comparePriorities(Priority p1, Priority p2) {
        return Integer.compare(getPriorityValue(p2), getPriorityValue(p1));
    }

    private int getPriorityValue(Priority p) {
        if (p == null) return VALUE_DEFAULT;

        switch (p) {
            case HIGH:   return VALUE_HIGH;
            case MEDIUM: return VALUE_MEDIUM;
            case LOW:    return VALUE_LOW;
            default:     return VALUE_DEFAULT;
        }
    }

    @Override
    public String getName() {
        return STRATEGY_NAME;
    }
}