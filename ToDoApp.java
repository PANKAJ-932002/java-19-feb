import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

public class ToDoApp extends JFrame {
    private JTextField taskInput;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;

    public ToDoApp() {
        setTitle("To-Do List");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Load HTML Page
        loadHTMLUI();

        // Task Input Field
        taskInput = new JTextField();
        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(e -> addTask());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        // Task List
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        loadTasksFromFile();

        JScrollPane scrollPane = new JScrollPane(taskList);

        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(e -> deleteTask());

        // Layout
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addTask() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            taskListModel.addElement(task);
            saveTasksToFile();
            taskInput.setText("");
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            taskListModel.remove(selectedIndex);
            saveTasksToFile();
        }
    }

    private void loadTasksFromFile() {
        try {
            Files.lines(Paths.get("tasks.html"))
                .filter(line -> line.contains("<li>"))
                .map(line -> line.replace("<li>", "").replace("</li>", "").trim())
                .forEach(taskListModel::addElement);
        } catch (IOException e) {
            System.out.println("No saved tasks found.");
        }
    }

    private void saveTasksToFile() {
        try (PrintWriter writer = new PrintWriter("tasks.html")) {
            writer.println("<html><head><link rel='stylesheet' href='styles.css'></head><body>");
            writer.println("<h1>To-Do List</h1><ul>");
            for (int i = 0; i < taskListModel.getSize(); i++) {
                writer.println("<li>" + taskListModel.getElementAt(i) + "</li>");
            }
            writer.println("</ul></body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHTMLUI() {
        try {
            Desktop.getDesktop().browse(new File("tasks.html").toURI());
        } catch (IOException e) {
            System.out.println("Could not load HTML UI.");
        }
    }
}
