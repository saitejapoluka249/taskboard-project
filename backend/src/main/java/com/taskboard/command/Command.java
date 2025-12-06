package com.taskboard.command;

import com.taskboard.service.TaskService;

public interface Command {
    void execute(TaskService service);
    String description();
}
