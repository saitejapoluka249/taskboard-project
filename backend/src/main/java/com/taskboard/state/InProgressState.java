package com.taskboard.state;

import com.taskboard.model.Task;

public class InProgressState implements TaskState {

    @Override
    public void move(Task task, String targetColumn) {
        switch (targetColumn) {
            case "todo":
                task.setState(new ToDoState());
                break;
            case "done":
                task.setState(new DoneState());
                break;
            case "in-progress":
                System.out.println("Already in in-progress.");
                break;
            default:
                System.out.println("Unknown target column: " + targetColumn);
        }
    }

    @Override
    public String getName() {
        return "in-progress"; // MATCH FRONTEND ID
    }
}