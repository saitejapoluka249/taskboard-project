package com.taskboard.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.taskboard.model.Priority;
import com.taskboard.model.Task;

public class SortByPriorityStrategy implements TaskSortStrategy {

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> copy = new ArrayList<>(tasks);
        copy.sort(Comparator.comparing(Task::getPriority, (p1, p2) -> {
            // HIGH first, then MEDIUM, then LOW
            return Integer.compare(priorityValue(p2), priorityValue(p1));
        }));
        return copy;
    }

    private int priorityValue(Priority p) {
        switch (p) {
            case HIGH: return 3;
            case MEDIUM: return 2;
            case LOW: return 1;
            default: return 0;
        }
    }

    @Override
    public String getName() {
        return "By Priority";
    }
}
