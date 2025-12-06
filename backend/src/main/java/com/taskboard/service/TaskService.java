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
import com.taskboard.state.DoneState;
import com.taskboard.state.InProgressState;
import com.taskboard.state.TaskState;
import com.taskboard.state.ToDoState;
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
    public void addTask(String title, String description, Priority priority, LocalDate dueDate, String columnName) {
        TaskState initialState;
        String targetColumnName;

        if ("done".equals(columnName)) {
            initialState = new DoneState();
            targetColumnName = "done";
        } else if ("in-progress".equals(columnName)) {
            initialState = new InProgressState();
            targetColumnName = "in-progress";
        } else {
            initialState = new ToDoState();
            targetColumnName = "todo";
        }

        Task task = taskFactory.createTask(title, description, priority, dueDate, initialState);
        Column targetColumn = board.getColumnByName(targetColumnName);

        if (targetColumn == null) {
            // Re-create defaults with LOWERCASE names if missing
            board.addColumn(new Column("todo"));
            board.addColumn(new Column("in-progress"));
            board.addColumn(new Column("done"));
            targetColumn = board.getColumnByName(targetColumnName);
        }

        if (targetColumn != null) {
            targetColumn.addTask(task);
            System.out.println("[TaskService] Added Task #" + task.getId() + " | State: " + task.getState().getName());
            for (BoardListener l : listeners) {
                l.onTaskAdded(task, targetColumn);
            }
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

        task.getState().move(task, targetColumnName);

        // Physical Move
        from.removeTask(task);
        to.addTask(task);
        System.out.println("[TaskService] Moved Task #" + taskId + " New State: " + task.getState().getName());

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
