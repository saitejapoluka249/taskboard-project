package com.taskboard.state;

import com.taskboard.model.Task;

public class ToDoState implements TaskState {

    @Override
    public void move(Task task, String targetColumn) {
        switch (targetColumn) {
            case "in-progress":
                task.setState(new InProgressState());
                break;
            case "done":
                task.setState(new DoneState());
                break;
            case "todo":
                System.out.println("Already in todo.");
                break;
            default:
                System.out.println("Unknown target column: " + targetColumn);
        }
    }

    @Override
    public String getName() {
        return "todo"; // MATCH FRONTEND ID
    }
}