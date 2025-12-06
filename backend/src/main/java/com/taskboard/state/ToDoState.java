package com.taskboard.state;

import com.taskboard.model.Task;

public class ToDoState implements TaskState {

    @Override
    public void start(Task task) {
        task.setState(new InProgressState());
    }

    @Override
    public void complete(Task task) {
        // cannot complete directly from TODO
        System.out.println("Cannot mark task as done directly from TODO. Start it first.");
    }

    @Override
    public String getName() {
        return "TODO";
    }
}
