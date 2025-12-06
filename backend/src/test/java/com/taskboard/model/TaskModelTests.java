package com.taskboard.model;

import com.taskboard.state.ToDoState;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TaskModelTests {

    @Test
    void testTaskCreationDefaults() {
        Task task = new Task(1, "Test Title", "Desc", Priority.HIGH, LocalDate.now(), new ToDoState());

        assertEquals(1, task.getId());
        assertEquals("Test Title", task.getTitle());

        assertEquals("todo", task.getState().getName());
    }
}