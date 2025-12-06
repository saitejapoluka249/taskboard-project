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
        // Use lowercase IDs exactly like Frontend
        board.addColumn(new Column("todo"));
        board.addColumn(new Column("in-progress"));
        board.addColumn(new Column("done"));
        return board;
    }
    private Board fromJson(BoardJson json) {
        Board board = new Board();

        Map<String, Column> colByName = new HashMap<>();

        // Handle empty file case
        if (json.columns == null || json.columns.isEmpty()) {
            return createDefaultBoard();
        }

        // Create columns (expecting "todo", "in-progress", "done" from JSON)
        for (String name : json.columns) {
            Column c = new Column(name);
            board.addColumn(c);
            colByName.put(name, c);
        }

        if (json.tasks != null) {
            for (TaskJson tj : json.tasks) {
                // Priority/Date parsing (same as before)
                Priority priority = Priority.MEDIUM;
                try { if(tj.priority != null) priority = Priority.valueOf(tj.priority); } catch(Exception ignored){}

                LocalDate dueDate = null;
                try { if(tj.dueDate != null) dueDate = LocalDate.parse(tj.dueDate); } catch(Exception ignored){}

                // 3. Simple State Switching (Exact match)
                TaskState state;
                String colName = tj.columnName != null ? tj.columnName : "todo";

                switch (colName) {
                    case "in-progress":
                        state = new InProgressState();
                        break;
                    case "done":
                        state = new DoneState();
                        break;
                    case "todo":
                    default:
                        state = new ToDoState();
                        break;
                }

                Task t = new Task(tj.id, tj.title, tj.description, priority, dueDate, state);

                Column col = colByName.get(colName);
                if (col == null) col = board.getColumns().get(0);
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