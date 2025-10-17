package io.yourname.todo;

public class TodoItem {
    private final int id;
    private String name;
    private String deadline; // dd-MM-uuuu
    private boolean completed;

    public TodoItem(int id, String name, String deadline, boolean completed) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.completed = completed;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}