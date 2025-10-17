package io.yourname.todo;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Update DTO: fields are optional.
 * - If name is provided, it must be non-blank and reasonably sized.
 * - If deadline is provided, it must match DD-MM-YYYY (or be empty/null).
 */
public class UpdateTodoRequest {

    // Optional, but if present must be between 1 and 200 characters (no purely whitespace)
    @Size(min = 1, max = 200, message = "Name must be 1..200 characters when provided")
    private String name;

    // Allow empty or pattern
    @Pattern(regexp = "^$|\\d{2}-\\d{2}-\\d{4}", message = "Use DD-MM-YYYY or leave empty")
    private String deadline;

    public UpdateTodoRequest() {}

    public String getName() { return name; }
    public void setName(String name) {
        // normalize empty -> null so server logic can treat null as "not provided"
        if (name != null) {
            String trimmed = name.trim();
            this.name = trimmed.isEmpty() ? null : trimmed;
        } else {
            this.name = null;
        }
    }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) {
        if (deadline != null) {
            String trimmed = deadline.trim();
            this.deadline = trimmed.isEmpty() ? null : trimmed;
        } else {
            this.deadline = null;
        }
    }

    // helper: did client provide at least one field to update?
    public boolean hasAny() {
        return (name != null && !name.isEmpty()) || (deadline != null && !deadline.isEmpty());
    }
}