package com.taskboard.app;

import com.google.gson.Gson;
import com.taskboard.factory.TaskFactory;
import com.taskboard.model.Priority;
import com.taskboard.model.Column;
import com.taskboard.model.Task;
import com.taskboard.repository.BoardRepository;
import com.taskboard.repository.FileBoardRepository;
import com.taskboard.service.TaskService;
import com.taskboard.strategy.SortByPriorityStrategy;
import com.taskboard.strategy.TaskSortStrategy;

import spark.Request;
import spark.Response;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class TaskBoardHttpApp {

    static class CreateTaskRequest {
        public String title;
        public String description;
        public String priority;
        public String dueDate;
        public String columnId;
    }

    static class UpdateTaskRequest {
        public String columnId;
    }

    static class TaskResponse {
        public int id;
        public String title;
        public String description;
        public String priority;  // "HIGH" | "MEDIUM" | "LOW"
        public String dueDate;   // "YYYY-MM-DD" or null
        public String columnId;  // "todo" | "in-progress" | "done"
    }

    public static void main(String[] args) {
        // 1. Setup Service
        BoardRepository repository = new FileBoardRepository(new File("tasks.json"));
        TaskSortStrategy sortStrategy = new SortByPriorityStrategy();
        TaskFactory factory = new TaskFactory();
        TaskService service = new TaskService(repository, sortStrategy, factory);

        Gson gson = new Gson();

        // 2. Configure Port
        port(9090);

        // 3. CORS
        options("/*", (Request req, Response res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((Request req, Response res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS");
            res.header("Access-Control-Allow-Headers",
                    "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        });

        // 4. Routes

        get("/", (req, res) -> "TaskBoard API is running!");

        // 🔹 POST: Create Task
        post("/api/tasks", (Request req, Response res) -> {
            System.out.println("Received POST /api/tasks: " + req.body());

            CreateTaskRequest body = gson.fromJson(req.body(), CreateTaskRequest.class);

            if (body == null || body.title == null || body.title.trim().isEmpty()) {
                res.status(400);
                res.type("application/json");
                return "{\"error\": \"Missing title\"}";
            }

            Priority pr = Priority.MEDIUM;
            if (body.priority != null) {
                try {
                    pr = Priority.valueOf(body.priority.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    // default to MEDIUM
                }
            }

            LocalDate due = null;
            if (body.dueDate != null && !body.dueDate.trim().isEmpty()) {
                try {
                    due = LocalDate.parse(body.dueDate.trim());
                } catch (Exception e) {
                    // leave null
                }
            }

            service.addTask(body.title.trim(), body.description, pr, due);
            service.saveBoard();

            res.status(201);
            res.type("application/json");
            return "{\"status\": \"success\"}";
        });

        // 🔹 PUT: Move Task
        put("/api/tasks/:id", (Request req, Response res) -> {
            String idStr = req.params(":id");
            System.out.println("Received PUT /api/tasks/" + idStr);

            try {
                int id = Integer.parseInt(idStr);
                UpdateTaskRequest body = gson.fromJson(req.body(), UpdateTaskRequest.class);

                if (body != null && body.columnId != null) {
                    String columnName;
                    switch (body.columnId.trim()) {
                        case "todo": columnName = "To Do"; break;
                        case "in-progress": columnName = "In Progress"; break;
                        case "done": columnName = "Done"; break;
                        default: columnName = body.columnId.trim(); break;
                    }
                    service.moveTask(id, columnName);
                    service.saveBoard();
                }
                res.status(200);
            } catch (NumberFormatException e) {
                res.status(400);
            }
            res.type("application/json");
            return "{\"status\": \"updated\"}";
        });

        // 🔹 GET: List all tasks as JSON (or [] if empty)
        get("/api/tasks", (req, res) -> {
            System.out.println("Handling GET /api/tasks");

            List<Column> columns = service.getColumns();
            List<TaskResponse> result = new ArrayList<>();

            if (columns != null) {
                for (Column col : columns) {
                    String columnId;
                    switch (col.getName()) {
                        case "To Do":
                            columnId = "todo";
                            break;
                        case "In Progress":
                            columnId = "in-progress";
                            break;
                        case "Done":
                            columnId = "done";
                            break;
                        default:
                            columnId = col.getName().toLowerCase().replace(" ", "-");
                            break;
                    }

                    for (Task t : col.getTasks()) {
                        TaskResponse dto = new TaskResponse();
                        dto.id = t.getId();
                        dto.title = t.getTitle();
                        dto.description = t.getDescription();
                        dto.priority = t.getPriority().name();
                        dto.dueDate = (t.getDueDate() != null) ? t.getDueDate().toString() : null;
                        dto.columnId = columnId;
                        result.add(dto);
                    }
                }
            }

            res.type("application/json");
            return gson.toJson(result); // [] if no tasks
        });

        // DELETE: Delete Task
        delete("/api/tasks/:id", (Request req, Response res) -> {
            String idStr = req.params(":id");
            System.out.println("Received DELETE /api/tasks/" + idStr);

            try {
                int id = Integer.parseInt(idStr);

                // uses your existing TaskService method
                service.deleteTask(id);
                service.saveBoard();

                res.status(200);
                res.type("application/json");
                return "{\"status\": \"deleted\"}";
            } catch (NumberFormatException e) {
                res.status(400);
                res.type("application/json");
                return "{\"error\": \"Invalid task id\"}";
            }
        });


        System.out.println("TaskBoard HTTP API running on http://localhost:9090");
    }
}