package com.taskboard.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.taskboard.factory.TaskFactory;
import com.taskboard.model.Board;
import com.taskboard.model.Column;
import com.taskboard.model.Priority;
import com.taskboard.model.Task;
import com.taskboard.observer.BoardListener;
import com.taskboard.repository.BoardRepository;
import com.taskboard.strategy.TaskSortStrategy;

public class TaskService {

    private final Board board;
    private final BoardRepository repository;
    private TaskSortStrategy sortStrategy;
    private final TaskFactory taskFactory;
    private final List<BoardListener> listeners = new ArrayList<>();

    public TaskService(BoardRepository repository, TaskSortStrategy sortStrategy, TaskFactory factory) {
        this.repository = repository;
        this.sortStrategy = sortStrategy;
        this.taskFactory = factory;
        this.board = repository.loadBoard();
        // ensure factory counter is ahead of existing tasks
        for (Column c : board.getColumns()) {
            for (Task t : c.getTasks()) {
                factory.updateCounterForExistingId(t.getId());
            }
        }
    }

    public void addListener(BoardListener listener) {
        listeners.add(listener);
    }

    public void setSortStrategy(TaskSortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }

    public void addTask(String title, String description, Priority priority, LocalDate dueDate) {
        Task task = taskFactory.createTask(title, description, priority, dueDate);
        Column todo = board.getColumnByName("To Do");
        if (todo == null) {
            // if columns are missing, create default columns
            Column c1 = new Column("To Do");
            Column c2 = new Column("In Progress");
            Column c3 = new Column("Done");
            board.addColumn(c1);
            board.addColumn(c2);
            board.addColumn(c3);
            todo = c1;
        }
        todo.addTask(task);
        for (BoardListener l : listeners) {
            l.onTaskAdded(task, todo);
        }
    }

    public void moveTask(int taskId, String targetColumnName) {
        Column from = board.findColumnContainingTask(taskId);
        Column to = board.getColumnByName(targetColumnName);
        if (from == null || to == null) {
            System.out.println("Invalid move. Check task ID and column name.");
            return;
        }
        Task task = from.findTaskById(taskId);
        if (task == null) {
            System.out.println("Task not found.");
            return;
        }
        if (from == to) {
            System.out.println("Task already in that column.");
            return;
        }
        from.removeTask(task);
        to.addTask(task);
        for (BoardListener l : listeners) {
            l.onTaskMoved(task, from, to);
        }
    }

    public void deleteTask(int taskId) {
        Column from = board.findColumnContainingTask(taskId);
        if (from == null) {
            System.out.println("Task not found.");
            return;
        }
        Task task = from.findTaskById(taskId);
        if (task == null) {
            System.out.println("Task not found.");
            return;
        }
        from.removeTask(task);
        for (BoardListener l : listeners) {
            l.onTaskDeleted(task, from);
        }
    }

    public void listTasks(String columnName) {
        Column column = board.getColumnByName(columnName);
        if (column == null) {
            System.out.println("Column not found: " + columnName);
            return;
        }
        List<Task> sorted = sortStrategy.sort(column.getTasks());
        System.out.println("=== " + column.getName() + " (sorted " + sortStrategy.getName() + ") ===");
        for (Task t : sorted) {
            System.out.println(t);
        }
    }

    public void listAllTasks() {
        for (Column col : board.getColumns()) {
            List<Task> sorted = sortStrategy.sort(col.getTasks());
            System.out.println("=== " + col.getName() + " (sorted " + sortStrategy.getName() + ") ===");
            for (Task t : sorted) {
                System.out.println(t);
            }
            System.out.println();
        }
    }

    public void saveBoard() {
        repository.saveBoard(board);
    }

    /**
     * Returns sorted tasks for a given column (for UI use).
     */
    public List<Task> getTasksForColumn(String columnName) {
        Column column = board.getColumnByName(columnName);
        if (column == null) {
            return new ArrayList<>();
        }
        return sortStrategy.sort(column.getTasks());
    }

    /**
     * Returns all columns on the board.
     */
    public List<Column> getColumns() {
        return board.getColumns();
    }
}
