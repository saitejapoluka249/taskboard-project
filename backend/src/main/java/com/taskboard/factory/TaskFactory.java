package com.taskboard.factory;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import com.taskboard.model.Priority;
import com.taskboard.model.Task;
import com.taskboard.state.TaskState;

/**
 * Factory for creating Task instances with unique IDs.
 */
public class TaskFactory {

    private final AtomicInteger counter = new AtomicInteger(1);

    public Task createTask(String title, String description, Priority priority, LocalDate dueDate, TaskState state) {
        int id = counter.getAndIncrement();
        return new Task(id, title, description, priority, dueDate, state); 
    }

    public void updateCounterForExistingId(int id) {
        counter.updateAndGet(curr -> Math.max(curr, id + 1));
    }
}
