import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class TodoListApplication {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private final List<String> taskNames = new ArrayList<>();
    private final List<String> taskDeadlines = new ArrayList<>();
    private final List<Boolean> taskCompleted = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public void addTask() {
        System.out.print("Enter task name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Task name cannot be empty.");
            return;
        }
        System.out.print("Enter deadline (DD-MM-YYYY): ");
        String deadline = scanner.nextLine().trim();
        if (!isValidDate(deadline)) {
            System.out.println("Invalid deadline. Use DD-MM-YYYY and a real date.");
            return;
        }
        taskNames.add(name);
        taskDeadlines.add(deadline);
        taskCompleted.add(false);
        System.out.println("Task added successfully.");
    }

    public void deleteTask() {
        if (taskNames.isEmpty()) {
            System.out.println("No tasks to delete.");
            return;
        }
        displayTasks();
        System.out.print("Enter the task number to delete: ");
        String input = scanner.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number.");
            return;
        }
        if (idx < 1 || idx > taskNames.size()) {
            System.out.println("Task number out of range.");
            return;
        }
        int removeIndex = idx - 1;
        String removed = taskNames.remove(removeIndex);
        taskDeadlines.remove(removeIndex);
        taskCompleted.remove(removeIndex);
        System.out.println("Removed task: " + removed);
    }

    public void editTask() {
        if (taskNames.isEmpty()) {
            System.out.println("No tasks to edit.");
            return;
        }
        displayTasks();
        System.out.print("Enter the task number to edit: ");
        String input = scanner.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number.");
            return;
        }
        if (idx < 1 || idx > taskNames.size()) {
            System.out.println("Task number out of range.");
            return;
        }
        int editIndex = idx - 1;
        System.out.println("Leave field empty to keep current value.");
        System.out.print("Current name: " + taskNames.get(editIndex) + " -> New name: ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            taskNames.set(editIndex, newName);
        }
        System.out.print("Current deadline: " + taskDeadlines.get(editIndex) + " -> New deadline (DD-MM-YYYY): ");
        String newDeadline = scanner.nextLine().trim();
        if (!newDeadline.isEmpty()) {
            if (!isValidDate(newDeadline)) {
                System.out.println("Invalid deadline. Edit aborted for deadline.");
            } else {
                taskDeadlines.set(editIndex, newDeadline);
            }
        }
        System.out.println("Task updated.");
    }

    public void markTaskCompleted() {
        if (taskNames.isEmpty()) {
            System.out.println("No tasks to mark.");
            return;
        }
        displayTasks();
        System.out.print("Enter the task number to toggle completed status: ");
        String input = scanner.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number.");
            return;
        }
        if (idx < 1 || idx > taskNames.size()) {
            System.out.println("Task number out of range.");
            return;
        }
        int index = idx - 1;
        boolean newVal = !taskCompleted.get(index);
        taskCompleted.set(index, newVal);
        System.out.println("Task \"" + taskNames.get(index) + "\" marked as " + (newVal ? "completed." : "not completed."));
    }

    public void displayTasks() {
        if (taskNames.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }
        System.out.println("Your Tasks:");
        for (int i = 0; i < taskNames.size(); i++) {
            String status = taskCompleted.get(i) ? "[x]" : "[ ]";
            System.out.printf("%d. %s %s (Deadline: %s)%n", i + 1, status, taskNames.get(i), taskDeadlines.get(i));
        }
    }

    public void sortTasksMenu() {
        if (taskNames.isEmpty()) {
            System.out.println("No tasks to sort.");
            return;
        }
        System.out.println("Sort by:");
        System.out.println("1. Deadline (earliest first)");
        System.out.println("2. Name (A → Z)");
        System.out.println("3. Name (Z → A)");
        System.out.println("4. Completed status (completed first)");
        System.out.print("Choose option (1-4): ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> sortByDeadline();
            case "2" -> sortByName(true);
            case "3" -> sortByName(false);
            case "4" -> sortByCompletedFirst();
            default -> System.out.println("Invalid sort option.");
        }
        System.out.println("Sort applied.");
    }

    private void sortByDeadline() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < taskNames.size(); i++) indices.add(i);
        indices.sort(Comparator.comparing(i -> LocalDate.parse(taskDeadlines.get(i), DATE_FORMAT)));
        reorderByIndices(indices);
    }

    private void sortByName(boolean asc) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < taskNames.size(); i++) indices.add(i);
        indices.sort((a, b) -> {
            int cmp = taskNames.get(a).compareToIgnoreCase(taskNames.get(b));
            return asc ? cmp : -cmp;
        });
        reorderByIndices(indices);
    }

    private void sortByCompletedFirst() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < taskNames.size(); i++) indices.add(i);
        indices.sort((a, b) -> {
            int ca = taskCompleted.get(a) ? 0 : 1;
            int cb = taskCompleted.get(b) ? 0 : 1;
            if (ca != cb) return Integer.compare(ca, cb);
            return taskNames.get(a).compareToIgnoreCase(taskNames.get(b));
        });
        reorderByIndices(indices);
    }

    private void reorderByIndices(List<Integer> indices) {
        List<String> namesCopy = new ArrayList<>(taskNames);
        List<String> deadlinesCopy = new ArrayList<>(taskDeadlines);
        List<Boolean> completedCopy = new ArrayList<>(taskCompleted);
        for (int i = 0; i < indices.size(); i++) {
            int src = indices.get(i);
            taskNames.set(i, namesCopy.get(src));
            taskDeadlines.set(i, deadlinesCopy.get(src));
            taskCompleted.set(i, completedCopy.get(src));
        }
    }

    public void showMenu() {
        System.out.println();
        System.out.println("=== Todo List Menu ===");
        System.out.println("1. Add Task");
        System.out.println("2. Delete Task");
        System.out.println("3. Display Tasks");
        System.out.println("4. Edit Task");
        System.out.println("5. Mark Task Completed / Toggle");
        System.out.println("6. Sort Tasks");
        System.out.println("7. Exit");
        System.out.print("Choose an option (1-7): ");
    }

    public void run() {
        boolean running = true;
        while (running) {
            showMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addTask();
                case "2" -> deleteTask();
                case "3" -> displayTasks();
                case "4" -> editTask();
                case "5" -> markTaskCompleted();
                case "6" -> sortTasksMenu();
                case "7" -> {
                    System.out.println("Exiting Todo List Application. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Enter a number between 1 and 7.");
            }
        }
    }

    public static void main(String[] args) {
        TodoListApplication app = new TodoListApplication();
        System.out.println("Welcome to the Todo List Application!");
        app.run();
    }
}
