package com.taskboard.state;

import com.taskboard.model.Task;

public interface TaskState {
    void start(Task task);
    void complete(Task task);
    String getName();
}
