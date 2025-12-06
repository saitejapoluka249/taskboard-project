package com.taskboard.model;

import java.time.LocalDate;

import com.taskboard.state.TaskState;
import com.taskboard.state.ToDoState;

public class Task {
    private final int id;
    private final String title;
    private final String description;
    private final Priority priority;
    private final LocalDate dueDate;
    private TaskState state;

    public Task(int id, String title, String description, Priority priority, LocalDate dueDate, TaskState state) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " (" + priority + ", due " + dueDate + ", " + state.getName() + ")";
    }
}
