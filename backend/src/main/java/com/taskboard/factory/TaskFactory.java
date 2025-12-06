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

    private static final int INITIAL_ID_VALUE = 1;
    private static final int NEXT_ID_OFFSET = 1;
    private final AtomicInteger counter = new AtomicInteger(INITIAL_ID_VALUE);

    public Task createTask(String title, String description, Priority priority, LocalDate dueDate, TaskState state) {
        int id = counter.getAndIncrement();
        return new Task(id, title, description, priority, dueDate, state);
    }

    public void updateCounterForExistingId(int id) {
        counter.updateAndGet(currentValue -> Math.max(currentValue, id + NEXT_ID_OFFSET));
    }
}
