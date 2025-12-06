package com.taskboard.state;

import com.taskboard.model.Task;

public class ToDoState implements TaskState {
    private static final String ID_TODO = "todo";
    private static final String ID_IN_PROGRESS = "in-progress";
    private static final String ID_DONE = "done";

    @Override
    public void move(Task task, String targetColumn) {
        if (targetColumn == null) {
            System.out.println("Unknown target column: null");
            return;
        }

        switch (targetColumn) {
            case ID_IN_PROGRESS:
                task.setState(new InProgressState());
                break;
            case ID_DONE:
                task.setState(new DoneState());
                break;
            case ID_TODO:
                System.out.println("Already in todo.");
                break;
            default:
                System.out.println("Unknown target column: " + targetColumn);
        }
    }

    @Override
    public String getName() {
        return ID_TODO;
    }
}