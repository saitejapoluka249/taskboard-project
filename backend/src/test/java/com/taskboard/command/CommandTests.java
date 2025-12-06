package com.taskboard.command;

import com.taskboard.model.Priority;
import com.taskboard.service.TaskService;
import com.taskboard.repository.BoardRepository;
import com.taskboard.model.Column;
import com.taskboard.model.Board; // Added this import
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandTests {

    @Test
    void testAddTaskCommand() {
        StubTaskService service = new StubTaskService();
        AddTaskCommand command = new AddTaskCommand("Task Api Submit", "SWE Work", Priority.HIGH, LocalDate.now());
        command.execute(service);
        assertTrue(service.addCalled, "Service.addTask should have been called");
        assertEquals("Task Api Submit", service.lastTitle);
    }

    @Test
    void testMoveTaskCommand() {
        StubTaskService service = new StubTaskService();
        MoveTaskCommand command = new MoveTaskCommand(101, "Done");
        command.execute(service);
        assertTrue(service.moveCalled, "Service.moveTask should have been called");
        assertEquals(101, service.lastId);
        assertEquals("Done", service.lastColumn);
    }

    @Test
    void testDeleteTaskCommand() {
        StubTaskService service = new StubTaskService();
        DeleteTaskCommand command = new DeleteTaskCommand(55);
        command.execute(service);
        assertTrue(service.deleteCalled, "Service.deleteTask should have been called");
        assertEquals(55, service.lastId);
    }

    // --- INTERNAL HELPER CLASSES ---

    // 1. Fake Repository (Updated to use Board object)
    static class FakeRepository implements BoardRepository {
        @Override
        public Board loadBoard() {
            return new Board(); // Return a new Board object
        }

        @Override
        public void saveBoard(Board board) {
            // Do nothing
        }
    }

    // 2. Stub Service
    static class StubTaskService extends TaskService {
        public boolean addCalled = false;
        public boolean moveCalled = false;
        public boolean deleteCalled = false;
        public String lastTitle;
        public int lastId;
        public String lastColumn;

        public StubTaskService() {
            // Pass the FakeRepository
            super(new FakeRepository(), null, null);
        }

        @Override
        public void addTask(String title, String description, Priority priority, LocalDate dueDate) {
            this.addCalled = true;
            this.lastTitle = title;
        }

        @Override
        public void moveTask(int id, String targetColumn) {
            this.moveCalled = true;
            this.lastId = id;
            this.lastColumn = targetColumn;
        }

        @Override
        public void deleteTask(int id) {
            this.deleteCalled = true;
            this.lastId = id;
        }
    }
}