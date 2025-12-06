package com.taskboard.command;

import com.taskboard.service.TaskService;

public class MoveTaskCommand implements Command {

    private final int taskId;
    private final String targetColumnName;

    public MoveTaskCommand(int taskId, String targetColumnName) {
        this.taskId = taskId;
        this.targetColumnName = targetColumnName;
    }

    @Override
    public void execute(TaskService service) {
        service.moveTask(taskId, targetColumnName);
    }

    @Override
    public String description() {
        return "Move task " + taskId + " to " + targetColumnName;
    }
}
