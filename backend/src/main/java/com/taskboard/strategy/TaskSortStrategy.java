package com.taskboard.strategy;

import java.util.List;

import com.taskboard.model.Task;

public interface TaskSortStrategy {
    List<Task> sort(List<Task> tasks);
    String getName();
}
