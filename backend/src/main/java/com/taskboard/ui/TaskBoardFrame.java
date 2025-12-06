package com.taskboard.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.time.LocalDate;
import java.util.List;

import com.taskboard.model.Column;
import com.taskboard.model.Priority;
import com.taskboard.model.Task;
import com.taskboard.observer.BoardListener;
import com.taskboard.service.TaskService;

/**
 * Cleaner Swing UI for the TaskBoard.
 */
public class TaskBoardFrame extends JFrame implements BoardListener {

    private final TaskService service;

    private final DefaultListModel<String> todoModel = new DefaultListModel<>();
    private final DefaultListModel<String> inProgressModel = new DefaultListModel<>();
    private final DefaultListModel<String> doneModel = new DefaultListModel<>();

    private JList<String> todoList;
    private JList<String> inProgressList;
    private JList<String> doneList;

    public TaskBoardFrame(TaskService service) {
        this.service = service;
        this.service.addListener(this);

        setTitle("TaskBoard - Kanban");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initComponents();
        refreshLists();
    }

    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 246, 250));

        // Top header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 16, 10, 16));
        header.setBackground(new Color(245, 246, 250));

        JLabel title = new JLabel("TaskBoard", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Simple Kanban task manager (To Do · In Progress · Done)");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(120, 120, 120));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(header.getBackground());
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);

        getContentPane().add(header, BorderLayout.NORTH);

        // Center columns
        JPanel columnsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        columnsPanel.setBorder(new EmptyBorder(0, 16, 16, 16));
        columnsPanel.setBackground(new Color(245, 246, 250));

        todoList = createTaskList();
        inProgressList = createTaskList();
        doneList = createTaskList();

        columnsPanel.add(createColumnPanel("To Do", new Color(240, 248, 255), todoList));
        columnsPanel.add(createColumnPanel("In Progress", new Color(255, 250, 240), inProgressList));
        columnsPanel.add(createColumnPanel("Done", new Color(240, 255, 244), doneList));

        getContentPane().add(columnsPanel, BorderLayout.CENTER);

        // Bottom toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(new EmptyBorder(8, 16, 8, 16));
        toolbar.setBackground(new Color(245, 246, 250));

        JButton addButton = createToolbarButton("Add Task");
        JButton moveButton = createToolbarButton("Move Task");
        JButton deleteButton = createToolbarButton("Delete Task");
        JButton saveButton = createToolbarButton("Save Board");

        addButton.addActionListener(e -> onAdd());
        moveButton.addActionListener(e -> onMove());
        deleteButton.addActionListener(e -> onDelete());
        saveButton.addActionListener(e -> onSave());

        toolbar.add(addButton);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(moveButton);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(deleteButton);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(saveButton);

        getContentPane().add(toolbar, BorderLayout.SOUTH);
    }

    private JList<String> createTaskList() {
        JList<String> list = new JList<>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(26);
        list.setBorder(new EmptyBorder(4, 4, 4, 4));
        list.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return list;
    }

    private JButton createToolbarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(110, 28));
        return button;
    }

    private JPanel createColumnPanel(String name, Color headerColor, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel label = new JLabel(name, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setOpaque(true);
        label.setBackground(headerColor);
        label.setBorder(new EmptyBorder(6, 6, 6, 6));

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private void refreshLists() {
        SwingUtilities.invokeLater(() -> {
            todoModel.clear();
            inProgressModel.clear();
            doneModel.clear();

            fillModel("To Do", todoModel);
            fillModel("In Progress", inProgressModel);
            fillModel("Done", doneModel);

            todoList.setModel(todoModel);
            inProgressList.setModel(inProgressModel);
            doneList.setModel(doneModel);
        });
    }

    private void fillModel(String columnName, DefaultListModel<String> model) {
        List<Task> tasks = service.getTasksForColumn(columnName);
        for (Task t : tasks) {
            model.addElement(formatTask(t));
        }
    }

    private String formatTask(Task t) {
        return String.format("#%d  %s  [%s]  due: %s",
                t.getId(),
                t.getTitle(),
                t.getPriority(),
                t.getDueDate() != null ? t.getDueDate() : "none");
    }

    private void onAdd() {
        String title = JOptionPane.showInputDialog(this, "Title:");
        if (title == null || title.trim().isEmpty()) {
            return;
        }
        String description = JOptionPane.showInputDialog(this, "Description:");
        if (description == null) {
            description = "";
        }

        String[] options = { "HIGH", "MEDIUM", "LOW" };
        String pr = (String) JOptionPane.showInputDialog(
                this,
                "Priority:",
                "Priority",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                "MEDIUM"
        );
        Priority priority;
        try {
            priority = Priority.valueOf(pr);
        } catch (Exception e) {
            priority = Priority.MEDIUM;
        }

        String dueStr = JOptionPane.showInputDialog(this, "Due date (YYYY-MM-DD) or blank:");
        LocalDate due = null;
        if (dueStr != null && !dueStr.trim().isEmpty()) {
            try {
                due = LocalDate.parse(dueStr.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date. Leaving due date empty.");
            }
        }

        service.addTask(title, description, priority, due);
    }

    private void onMove() {
        String idStr = JOptionPane.showInputDialog(this, "Task ID to move:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        int id;
        try {
            id = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid task ID.");
            return;
        }

        String[] columns = { "To Do", "In Progress", "Done" };
        String target = (String) JOptionPane.showInputDialog(
                this,
                "Move to column:",
                "Move Task",
                JOptionPane.QUESTION_MESSAGE,
                null,
                columns,
                "In Progress"
        );
        if (target == null || target.trim().isEmpty()) return;

        service.moveTask(id, target.trim());
        refreshLists();
    }

    private void onDelete() {
        String idStr = JOptionPane.showInputDialog(this, "Task ID to delete:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        int id;
        try {
            id = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid task ID.");
            return;
        }
        service.deleteTask(id);
        refreshLists();
    }

    private void onSave() {
        service.saveBoard();
        JOptionPane.showMessageDialog(this, "Board saved.");
    }

    @Override
    public void onTaskAdded(Task task, Column column) {
        refreshLists();
    }

    @Override
    public void onTaskMoved(Task task, Column from, Column to) {
        refreshLists();
    }

    @Override
    public void onTaskDeleted(Task task, Column from) {
        refreshLists();
    }
}
