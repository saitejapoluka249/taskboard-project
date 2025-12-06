package com.taskboard.observer;

import com.taskboard.model.Task;
import com.taskboard.model.Column;

public interface BoardListener {
    void onTaskAdded(Task task, Column column);
    void onTaskMoved(Task task, Column from, Column to);
    void onTaskDeleted(Task task, Column from);
}
