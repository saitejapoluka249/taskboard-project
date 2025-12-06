package com.taskboard.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taskboard.model.Board;
import com.taskboard.model.Column;
import com.taskboard.model.Priority;
import com.taskboard.model.Task;
import com.taskboard.state.DoneState;
import com.taskboard.state.InProgressState;
import com.taskboard.state.ToDoState;
import com.taskboard.state.TaskState;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

public class FileBoardRepository implements BoardRepository {

    private static final String ID_TODO = "todo";
    private static final String ID_IN_PROGRESS = "in-progress";
    private static final String ID_DONE = "done";

    private final File file;
    private final Gson gson;

    public FileBoardRepository(File file) {
        this.file = file;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        System.out.println("[FileBoardRepository] Using file: " + file.getAbsolutePath());
    }

    private static class TaskJson {
        int id;
        String title;
        String description;
        String priority;
        String dueDate;
        String columnName;
    }

    private static class BoardJson {
        List<String> columns;
        List<TaskJson> tasks;
    }

    @Override
    public Board loadBoard() {
        if (!file.exists()) {
            System.out.println("[FileBoardRepository] No file yet, creating default empty board");
            return createDefaultBoard();
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<BoardJson>() {}.getType();
            BoardJson json = gson.fromJson(reader, type);

            if (json == null) {
                return createDefaultBoard();
            }
            return fromJson(json);
        } catch (IOException e) {
            System.out.println("[FileBoardRepository] load() ERROR: " + e.getMessage());
            e.printStackTrace();
            return createDefaultBoard();
        }
    }

    @Override
    public void saveBoard(Board board) {
        System.out.println("[FileBoardRepository] save() started");
        BoardJson json = toJson(board);
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(json, writer);
        } catch (IOException e) {
            System.out.println("[FileBoardRepository] save() ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[FileBoardRepository] save() finished");
    }


    private Board createDefaultBoard() {
        Board board = new Board();
        // Use constants instead of magic strings
        board.addColumn(new Column(ID_TODO));
        board.addColumn(new Column(ID_IN_PROGRESS));
        board.addColumn(new Column(ID_DONE));
        return board;
    }

    private Board fromJson(BoardJson json) {
        Board board = new Board();
        Map<String, Column> colByName = setupColumns(board, json.columns);
        processTasks(board, json.tasks, colByName);

        return board;
    }

    private Map<String, Column> setupColumns(Board board, List<String> columnNames) {
        Map<String, Column> colByName = new HashMap<>();

        if (columnNames == null || columnNames.isEmpty()) {
            board.addColumn(new Column(ID_TODO));
            board.addColumn(new Column(ID_IN_PROGRESS));
            board.addColumn(new Column(ID_DONE));
            return colByName; // Note: map will be empty, tasks will fallback to default
        }

        for (String name : columnNames) {
            Column column = new Column(name);
            board.addColumn(column);
            colByName.put(name, column);
        }
        return colByName;
    }

    private void processTasks(Board board, List<TaskJson> taskJsons, Map<String, Column> colByName) {
        if (taskJsons == null) return;

        for (TaskJson taskJson : taskJsons) {
            Priority priority = parsePriority(taskJson.priority);
            LocalDate dueDate = parseDueDate(taskJson.dueDate);
            String colName = taskJson.columnName != null ? taskJson.columnName : ID_TODO;
            TaskState state = determineState(colName);

            Task task = new Task(
                    taskJson.id,
                    taskJson.title,
                    taskJson.description,
                    priority,
                    dueDate,
                    state
            );

            Column col = colByName.get(colName);
            if (col == null && !board.getColumns().isEmpty()) {
                col = board.getColumns().get(0);
            }

            if (col != null) {
                col.addTask(task);
            }
        }
    }

    private Priority parsePriority(String priorityStr) {
        if (priorityStr == null) return Priority.MEDIUM;
        try {
            return Priority.valueOf(priorityStr);
        } catch (IllegalArgumentException e) {
            return Priority.MEDIUM;
        }
    }

    private LocalDate parseDueDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private TaskState determineState(String colName) {
        switch (colName) {
            case ID_IN_PROGRESS:
                return new InProgressState();
            case ID_DONE:
                return new DoneState();
            case ID_TODO:
            default:
                return new ToDoState();
        }
    }

    private BoardJson toJson(Board board) {
        BoardJson json = new BoardJson();
        json.columns = new ArrayList<>();
        json.tasks = new ArrayList<>();

        for (Column column : board.getColumns()) {
            json.columns.add(column.getName());

            for (Task task : column.getTasks()) {
                TaskJson taskJson = new TaskJson();
                taskJson.id = task.getId();
                taskJson.title = task.getTitle();
                taskJson.description = task.getDescription();
                taskJson.priority = task.getPriority() != null ? task.getPriority().name() : null;
                taskJson.dueDate = task.getDueDate() != null ? task.getDueDate().toString() : null;
                taskJson.columnName = column.getName();

                json.tasks.add(taskJson);
            }
        }

        return json;
    }
}