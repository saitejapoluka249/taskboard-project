package com.taskboard.state;

import com.taskboard.model.Task;

public class InProgressState implements TaskState {

    @Override
    public void start(Task task) {
        System.out.println("Task already in progress.");
    }

    @Override
    public void complete(Task task) {
        task.setState(new DoneState());
    }

    @Override
    public String getName() {
        return "IN_PROGRESS";
    }
}
