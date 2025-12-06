package com.taskboard.state;

import com.taskboard.model.Task;

public interface TaskState {
    void move(Task task, String targetColumn);

    String getName();
}