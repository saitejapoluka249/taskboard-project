package com.taskboard.command;

import com.taskboard.service.TaskService;

public class DeleteTaskCommand implements Command {

    private final int taskId;

    public DeleteTaskCommand(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void execute(TaskService service) {
        service.deleteTask(taskId);
    }
}
