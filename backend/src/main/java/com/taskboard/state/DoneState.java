package com.taskboard.state;

import com.taskboard.model.Task;

public class DoneState implements TaskState {

    @Override
    public void start(Task task) {
        System.out.println("Task already done. Cannot start again.");
    }

    @Override
    public void complete(Task task) {
        System.out.println("Task already done.");
    }

    @Override
    public String getName() {
        return "DONE";
    }
}
