package com.taskboard.command;

import java.time.LocalDate;

import com.taskboard.model.Priority;
import com.taskboard.service.TaskService;

public class AddTaskCommand implements Command {

    private final String title;
    private final String description;
    private final Priority priority;
    private final LocalDate dueDate;
    private final String targetColumn;

    public AddTaskCommand(String title, String description, Priority priority, LocalDate dueDate, String targetColumn) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.targetColumn = targetColumn;
    }

    @Override
    public void execute(TaskService service) {
        service.addTask(title, description, priority, dueDate, targetColumn);
    }

    @Override
    public String description() {
        return "Add task: " + title;
    }
}
