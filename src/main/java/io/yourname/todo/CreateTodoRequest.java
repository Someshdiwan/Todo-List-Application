package io.yourname.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new Todo item.
 */
public class CreateTodoRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name cannot be longer than 200 characters")
    private String name;

    @NotBlank(message = "Deadline is required; use DD-MM-YYYY")
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Use DD-MM-YYYY")
    private String deadline;

    public CreateTodoRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline == null ? null : deadline.trim(); }
}