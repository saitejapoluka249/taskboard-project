package com.taskboard.state;

import com.taskboard.model.Task;

public class DoneState implements TaskState {

    @Override
    public void move(Task task, String targetColumn) {
        switch (targetColumn) {
            case "todo":
                task.setState(new ToDoState());
                break;
            case "in-progress":
                task.setState(new InProgressState());
                break;
            case "done":
                System.out.println("Already in done.");
                break;
            default:
                System.out.println("Unknown target column: " + targetColumn);
        }
    }

    @Override
    public String getName() {
        return "done"; // MATCH FRONTEND ID
    }
}