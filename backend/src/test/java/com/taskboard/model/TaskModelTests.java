package com.taskboard.model;

import com.taskboard.state.ToDoState; // Import the state
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TaskModelTests {

    @Test
    void testTaskCreationDefaults() {
        // Updated: Constructor now requires the 'State' object as 6th argument
        Task task = new Task(1, "Test Title", "Desc", Priority.HIGH, LocalDate.now(), new ToDoState());

        assertEquals(1, task.getId());
        assertEquals("Test Title", task.getTitle());

        // Updated: Check state name (lowercase "todo")
        assertEquals("todo", task.getState().getName());
    }
}