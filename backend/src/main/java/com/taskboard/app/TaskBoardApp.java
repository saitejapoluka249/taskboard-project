package com.taskboard.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.time.LocalDate;

import com.taskboard.command.AddTaskCommand;
import com.taskboard.command.Command;
import com.taskboard.command.CommandHistory;
import com.taskboard.command.DeleteTaskCommand;
import com.taskboard.command.MoveTaskCommand;
import com.taskboard.factory.TaskFactory;
import com.taskboard.model.Priority;
import com.taskboard.observer.ConsoleBoardListener;
import com.taskboard.repository.BoardRepository;
import com.taskboard.repository.FileBoardRepository;
import com.taskboard.service.TaskService;
import com.taskboard.strategy.SortByDueDateStrategy;
import com.taskboard.strategy.SortByPriorityStrategy;
import com.taskboard.strategy.TaskSortStrategy;

/**
 * Console entry point for TaskBoard.
 */
public class TaskBoardApp {

    public static void main(String[] args) throws IOException {
        new TaskBoardApp().run();
    }

    private void run() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // DI / wiring
        BoardRepository repository = new FileBoardRepository(new File("tasks.json"));
        TaskSortStrategy sortStrategy = new SortByPriorityStrategy();
        TaskFactory factory = new TaskFactory();
        TaskService service = new TaskService(repository, sortStrategy, factory);
        service.addListener(new ConsoleBoardListener());
        CommandHistory history = new CommandHistory();

        System.out.println("=== TaskBoard - Simple Kanban Task Manager ===");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = reader.readLine();
            if (choice == null) break;
            choice = choice.trim();
            switch (choice) {
                case "1":
                    Command addCmd = createAddTaskCommand(reader);
                    addCmd.execute(service);
                    history.add(addCmd);
                    break;
                case "2":
                    Command moveCmd = createMoveTaskCommand(reader);
                    moveCmd.execute(service);
                    history.add(moveCmd);
                    break;
                case "3":
                    Command delCmd = createDeleteTaskCommand(reader);
                    delCmd.execute(service);
                    history.add(delCmd);
                    break;
                case "4":
                    chooseSortStrategy(reader, service);
                    break;
                case "5":
                    listTasksMenu(reader, service);
                    break;
                case "6":
                    service.saveBoard();
                    running = false;
                    System.out.println("Board saved. Goodbye!");
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1) Add task");
        System.out.println("2) Move task");
        System.out.println("3) Delete task");
        System.out.println("4) Change sort strategy");
        System.out.println("5) List tasks");
        System.out.println("6) Save & exit");
        System.out.print("Choose option: ");
    }

    private Command createAddTaskCommand(BufferedReader reader) throws IOException {
        System.out.print("Title: ");
        String title = reader.readLine();
        System.out.print("Description: ");
        String description = reader.readLine();
        System.out.print("Priority (LOW, MEDIUM, HIGH): ");
        String pr = reader.readLine();
        Priority priority;
        try {
            priority = Priority.valueOf(pr.trim().toUpperCase());
        } catch (Exception e) {
            priority = Priority.MEDIUM;
        }
        System.out.print("Due date (YYYY-MM-DD) or blank: ");
        String dueStr = reader.readLine();
        LocalDate due = null;
        if (dueStr != null && !dueStr.trim().isEmpty()) {
            try {
                due = LocalDate.parse(dueStr.trim());
            } catch (Exception e) {
                System.out.println("Invalid date, leaving blank.");
            }
        }
        return new AddTaskCommand(title, description, priority, due);
    }

    private Command createMoveTaskCommand(BufferedReader reader) throws IOException {
        System.out.print("Task ID to move: ");
        String idStr = reader.readLine();
        int id = Integer.parseInt(idStr.trim());
        System.out.print("Target column (To Do, In Progress, Done): ");
        String col = reader.readLine();
        return new MoveTaskCommand(id, col.trim());
    }

    private Command createDeleteTaskCommand(BufferedReader reader) throws IOException {
        System.out.print("Task ID to delete: ");
        String idStr = reader.readLine();
        int id = Integer.parseInt(idStr.trim());
        return new DeleteTaskCommand(id);
    }

    private void chooseSortStrategy(BufferedReader reader, TaskService service) throws IOException {
        System.out.println("Choose sort strategy:");
        System.out.println("1) By priority");
        System.out.println("2) By due date");
        System.out.print("Your choice: ");
        String c = reader.readLine();
        if ("2".equals(c)) {
            service.setSortStrategy(new SortByDueDateStrategy());
        } else {
            service.setSortStrategy(new SortByPriorityStrategy());
        }
    }

    private void listTasksMenu(BufferedReader reader, TaskService service) throws IOException {
        System.out.println("1) List single column");
        System.out.println("2) List all columns");
        System.out.print("Your choice: ");
        String c = reader.readLine();
        if ("1".equals(c)) {
            System.out.print("Column name (To Do, In Progress, Done): ");
            String col = reader.readLine();
            service.listTasks(col.trim());
        } else {
            service.listAllTasks();
        }
    }
}
