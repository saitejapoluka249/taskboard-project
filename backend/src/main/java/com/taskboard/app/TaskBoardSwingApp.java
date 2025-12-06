package com.taskboard.app;

import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.taskboard.factory.TaskFactory;
import com.taskboard.observer.ConsoleBoardListener;
import com.taskboard.repository.BoardRepository;
import com.taskboard.repository.FileBoardRepository;
import com.taskboard.service.TaskService;
import com.taskboard.strategy.SortByPriorityStrategy;
import com.taskboard.strategy.TaskSortStrategy;
import com.taskboard.ui.TaskBoardFrame;

/**
 * Entry point for the Swing UI version of TaskBoard.
 */
public class TaskBoardSwingApp {

    public static void main(String[] args) {
        // Use system look & feel for a cleaner native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            BoardRepository repository = new FileBoardRepository(new File("tasks.txt"));
            TaskSortStrategy sortStrategy = new SortByPriorityStrategy();
            TaskFactory factory = new TaskFactory();
            TaskService service = new TaskService(repository, sortStrategy, factory);
            service.addListener(new ConsoleBoardListener()); // optional logging
            TaskBoardFrame frame = new TaskBoardFrame(service);
            frame.setVisible(true);
        });
    }
}
