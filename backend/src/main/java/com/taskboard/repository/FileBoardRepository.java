package com.taskboard.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taskboard.model.Board;
import com.taskboard.model.Column;
import com.taskboard.model.Priority;
import com.taskboard.model.Task;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

/**
 * JSON-based repository: persists Board to a JSON file.
 */
public class FileBoardRepository implements BoardRepository {

    private final File file;
    private final Gson gson;

    public FileBoardRepository(File file) {
        this.file = file;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        System.out.println("[FileBoardRepository] Using file: " + file.getAbsolutePath());
    }

    // DTOs used only for JSON representation
    private static class TaskJson {
        int id;
        String title;
        String description;
        String priority;   // "LOW", "MEDIUM", "HIGH"
        String dueDate;    // "YYYY-MM-DD" or null
        String columnName; // "To Do", "In Progress", "Done"
    }

    private static class BoardJson {
        List<String> columns;    // ["To Do", "In Progress", "Done"]
        List<TaskJson> tasks;    // flat list of tasks
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

    // ---------- helpers ----------

    private Board createDefaultBoard() {
        Board board = new Board();
        Column todo = new Column("To Do");
        Column inProgress = new Column("In Progress");
        Column done = new Column("Done");
        board.addColumn(todo);
        board.addColumn(inProgress);
        board.addColumn(done);
        return board;
    }

    private Board fromJson(BoardJson json) {
        Board board = new Board();

        // Create columns from names
        Map<String, Column> colByName = new LinkedHashMap<>();
        if (json.columns != null && !json.columns.isEmpty()) {
            for (String name : json.columns) {
                Column c = new Column(name);
                board.addColumn(c);
                colByName.put(name, c);
            }
        } else {
            // Fallback to default if no columns listed
            return createDefaultBoard();
        }

        if (json.tasks != null) {
            for (TaskJson tj : json.tasks) {

                // safely parse priority
                Priority priority = Priority.MEDIUM;
                if (tj.priority != null) {
                    try {
                        priority = Priority.valueOf(tj.priority);
                    } catch (IllegalArgumentException ignored) {
                        // keep MEDIUM if invalid
                    }
                }

                // safely parse due date
                LocalDate dueDate = null;
                if (tj.dueDate != null && !tj.dueDate.trim().isEmpty()) {
                    try {
                        dueDate = LocalDate.parse(tj.dueDate.trim()); // expects YYYY-MM-DD
                    } catch (Exception ignored) {
                        // leave null if invalid
                    }
                }

                // ✅ use the 5-arg constructor, ID included
                Task t = new Task(
                        tj.id,           // id
                        tj.title,        // title
                        tj.description,  // description
                        priority,        // priority
                        dueDate          // due date (can be null)
                );

                Column col = colByName.getOrDefault(tj.columnName, colByName.get("To Do"));
                col.addTask(t);
            }
        }

        return board;
    }

    private BoardJson toJson(Board board) {
        BoardJson json = new BoardJson();
        json.columns = new ArrayList<>();
        json.tasks = new ArrayList<>();

        for (Column col : board.getColumns()) {
            json.columns.add(col.getName());
            for (Task t : col.getTasks()) {
                TaskJson tj = new TaskJson();
                tj.id = t.getId();
                tj.title = t.getTitle();
                tj.description = t.getDescription();
                tj.priority = t.getPriority() != null ? t.getPriority().name() : null;
                tj.dueDate = t.getDueDate() != null ? t.getDueDate().toString() : null;
                tj.columnName = col.getName();
                json.tasks.add(tj);
            }
        }

        return json;
    }
}