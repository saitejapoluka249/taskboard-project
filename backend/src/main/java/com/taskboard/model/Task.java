package com.taskboard.model;

import java.time.LocalDate;

import com.taskboard.state.TaskState;
import com.taskboard.state.ToDoState;

public class Task {
    private final int id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private TaskState state;

    public Task(int id, String title, String description, Priority priority, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.state = new ToDoState();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public String getStatusName() {
        return state.getName();
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " (" + priority + ", due " + dueDate + ", " + state.getName() + ")";
    }
}
