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

    private static final String ID_TODO = "todo";
    private static final String ID_IN_PROGRESS = "in-progress";
    private static final String ID_DONE = "done";
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
        syncFactoryCounter();
    }

    private void syncFactoryCounter() {
        for (Column col : board.getColumns()) {
            for (Task task : col.getTasks()) {
                taskFactory.updateCounterForExistingId(task.getId());
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
        TaskState initialState = determineInitialState(columnName);
        String targetColumnId = determineTargetColumnId(columnName);

        Task task = taskFactory.createTask(title, description, priority, dueDate, initialState);

        Column targetColumn = getOrCreateColumn(targetColumnId);

        if (targetColumn != null) {
            targetColumn.addTask(task);
            System.out.println("[TaskService] Added Task #" + task.getId() + " | State: " + task.getState().getName());
            notifyTaskAdded(task, targetColumn);
        }
    }

    private TaskState determineInitialState(String columnName) {
        if (ID_DONE.equals(columnName)) {
            return new DoneState();
        } else if (ID_IN_PROGRESS.equals(columnName)) {
            return new InProgressState();
        } else {
            return new ToDoState();
        }
    }

    private String determineTargetColumnId(String columnName) {
        if (ID_DONE.equals(columnName)) return ID_DONE;
        if (ID_IN_PROGRESS.equals(columnName)) return ID_IN_PROGRESS;
        return ID_TODO;
    }

    private Column getOrCreateColumn(String targetColumnName) {
        Column targetColumn = board.getColumnByName(targetColumnName);

        if (targetColumn == null) {
            // Re-create defaults with LOWERCASE names if missing
            board.addColumn(new Column(ID_TODO));
            board.addColumn(new Column(ID_IN_PROGRESS));
            board.addColumn(new Column(ID_DONE));
            targetColumn = board.getColumnByName(targetColumnName);
        }
        return targetColumn;
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

        notifyTaskMoved(task, from, to);
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
        notifyTaskDeleted(task, from);
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

    public List<Column> getColumns() {
        return board.getColumns();
    }

    private void notifyTaskAdded(Task task, Column col) {
        for (BoardListener l : listeners) {
            l.onTaskAdded(task, col);
        }
    }

    private void notifyTaskMoved(Task task, Column from, Column to) {
        for (BoardListener l : listeners) {
            l.onTaskMoved(task, from, to);
        }
    }

    private void notifyTaskDeleted(Task task, Column from) {
        for (BoardListener l : listeners) {
            l.onTaskDeleted(task, from);
        }
    }
}
