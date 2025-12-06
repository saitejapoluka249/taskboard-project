package com.taskboard.observer;

import com.taskboard.model.Task;
import com.taskboard.model.Column;

public class ConsoleBoardListener implements BoardListener {

    @Override
    public void onTaskAdded(Task task, Column column) {
        System.out.println("Added task " + task.getId() + " to column " + column.getName());
    }

    @Override
    public void onTaskMoved(Task task, Column from, Column to) {
        System.out.println("Moved task " + task.getId() + " from " + from.getName() + " to " + to.getName());
    }

    @Override
    public void onTaskDeleted(Task task, Column from) {
        System.out.println("Deleted task " + task.getId() + " from " + from.getName());
    }
}
