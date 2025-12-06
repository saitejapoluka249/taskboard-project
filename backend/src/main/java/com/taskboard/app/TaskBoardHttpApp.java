package com.taskboard.app;

import com.google.gson.Gson;
import com.taskboard.command.AddTaskCommand;
import com.taskboard.command.Command;
import com.taskboard.command.DeleteTaskCommand;
import com.taskboard.command.MoveTaskCommand;
import com.taskboard.factory.TaskFactory;
import com.taskboard.model.Priority;
import com.taskboard.model.Column;
import com.taskboard.model.Task;
import com.taskboard.observer.ConsoleBoardListener;
import com.taskboard.repository.BoardRepository;
import com.taskboard.repository.FileBoardRepository;
import com.taskboard.service.TaskService;
import com.taskboard.strategy.SortByPriorityStrategy;
import com.taskboard.strategy.SortByDueDateStrategy;
import com.taskboard.strategy.TaskSortStrategy;

import spark.Request;
import spark.Response;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class TaskBoardHttpApp {
    private static final int PORT = 9090;
    private static final String DB_FILE = "tasks.json";
    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_BAD_REQUEST = 400;
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
        public String priority;
        public String dueDate;
        public String columnId;
    }

    public static void main(String[] args) {
        BoardRepository repository = new FileBoardRepository(new File(DB_FILE));
        TaskSortStrategy sortStrategy = new SortByPriorityStrategy();
        TaskFactory factory = new TaskFactory();
        TaskService service = new TaskService(repository, sortStrategy, factory);
        service.addListener(new ConsoleBoardListener());

        Gson gson = new Gson();

        // 2. Configure Port
        port(PORT);

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

        get("/", (req, res) -> "TaskBoard API is running!");

        // 🔹 POST: Create Task
        post("/api/tasks", (Request req, Response res) -> {
            System.out.println("Received POST /api/tasks: " + req.body());

            CreateTaskRequest body = gson.fromJson(req.body(), CreateTaskRequest.class);

            if (body == null || body.title == null || body.title.trim().isEmpty()) {
                res.status(HTTP_BAD_REQUEST);
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
            String columnId = body.columnId;
            Command addCmd = new AddTaskCommand(body.title.trim(), body.description, pr, due, columnId);
            addCmd.execute(service);
            service.saveBoard();

            res.status(HTTP_CREATED);
            res.type("application/json");
            return "{\"status\": \"success\"}";
        });

        // 🔹 PUT: Move Task
        put("/api/tasks/:id", (Request req, Response res) -> {
            String idStr = req.params(":id");
            try {
                int id = Integer.parseInt(idStr);
                UpdateTaskRequest body = gson.fromJson(req.body(), UpdateTaskRequest.class);

                if (body != null && body.columnId != null) {
                    // NO MAPPING NEEDED! Frontend sends "todo", Service expects "todo".
                    Command moveCmd = new MoveTaskCommand(id, body.columnId.trim());
                    moveCmd.execute(service);
                    service.saveBoard();
                }
                res.status(HTTP_OK);
            } catch (NumberFormatException e) {
                res.status(HTTP_BAD_REQUEST);
            }
            res.type("application/json");
            return "{\"status\": \"updated\"}";
        });

        // 🔹 GET: List all tasks
        get("/api/tasks", (req, res) -> {
            List<Column> columns = service.getColumns();
            List<TaskResponse> result = new ArrayList<>();

            if (columns != null) {
                for (Column col : columns) {
                    // NO MAPPING NEEDED! Column name is already "todo", "in-progress", etc.
                    String columnId = col.getName();
                    List<Task> sortedTasks = service.getTasksForColumn(col.getName());

                    for (Task t : sortedTasks) {
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
            return gson.toJson(result);
        });

        // DELETE: Delete Task
        delete("/api/tasks/:id", (Request req, Response res) -> {
            String idStr = req.params(":id");
            System.out.println("Received DELETE /api/tasks/" + idStr);

            try {
                int id = Integer.parseInt(idStr);

                Command deleteCmd = new DeleteTaskCommand(id);
                deleteCmd.execute(service);
                service.saveBoard();

                res.status(HTTP_OK);
                res.type("application/json");
                return "{\"status\": \"deleted\"}";
            } catch (NumberFormatException e) {
                res.status(HTTP_BAD_REQUEST);
                res.type("application/json");
                return "{\"error\": \"Invalid task id\"}";
            }
        });

        // 🔹 PUT: Switch Sort Strategy
        // Usage: PUT http://localhost:9090/api/sort/date
        put("/api/sort/:type", (req, res) -> {
            String type = req.params(":type");

            if ("date".equalsIgnoreCase(type)) {
                service.setSortStrategy(new SortByDueDateStrategy());
                System.out.println("Switched sorting to: Due Date");
            } else {
                service.setSortStrategy(new SortByPriorityStrategy());
                System.out.println("Switched sorting to: Priority");
            }

            res.status(HTTP_OK);
            res.type("application/json");
            return "{\"status\": \"strategy_changed\", \"current\": \"" + type + "\"}";
        });

        System.out.println("TaskBoard HTTP API running on http://localhost:9090");
    }
}