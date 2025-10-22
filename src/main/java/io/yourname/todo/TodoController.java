package io.yourname.todo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Single consolidated controller:
 * - Structured REST endpoints under /api/todos
 * - Console-style single-line command endpoint under /api/console (plain text)
 *
 * This version is written to avoid multi-catch duplication and Java syntax pitfalls.
 */
@RestController
@RequestMapping("/api")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    // Structured REST API
    @GetMapping("/todos")
    public List<TodoItem> list() {
        return service.list();
    }

    @PostMapping("/todos")
    public ResponseEntity<TodoItem> create(@Valid @RequestBody CreateTodoRequest req) {
        TodoItem created = service.add(req.getName(), req.getDeadline());
        return ResponseEntity.created(URI.create("/api/todos/" + created.getId())).body(created);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<TodoItem> get(@PathVariable int id) {
        TodoItem t = service.get(id);
        return t == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(t);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        TodoItem t = service.delete(id);
        return t == null ? ResponseEntity.notFound().build() : ResponseEntity.noContent().build();
    }

    @PatchMapping("/todos/{id}")
    public ResponseEntity<TodoItem> update(@PathVariable int id, @Valid @RequestBody UpdateTodoRequest req) {
        if (!req.hasAny()) {
            return ResponseEntity.badRequest().build();
        }
        TodoItem t = service.edit(id, req.getName(), req.getDeadline());
        return t == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(t);
    }

    @PostMapping("/todos/{id}/toggle")
    public ResponseEntity<TodoItem> toggle(@PathVariable int id) {
        TodoItem t = service.toggle(id);
        return t == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(t);
    }

    @PostMapping("/todos/sort")
    public ResponseEntity<Void> sort(@RequestParam int option) {
        service.sortByOption(option);
        return ResponseEntity.ok().build();
    }

    // Console-style command endpoint
    @PostMapping(value = "/console", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> execPlain(@RequestBody String body) {
        return exec(body);
    }

    @PostMapping(value = "/console/json", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> execJson(@RequestBody Map<String, String> payload) {
        String cmd = payload.getOrDefault("command", "");
        return exec(cmd);
    }

    // central dispatcher
    private ResponseEntity<String> exec(String raw) {
        Command parsed = parseCommand(raw);
        String cmd = parsed.cmd;
        Map<String, String> args = parsed.args;

        try {
            if ("add".equals(cmd)) {
                String name = args.get("name");
                String deadline = args.get("deadline");
                if (name == null || deadline == null) {
                    return bad("add needs name and deadline. Example: add|name=Buy milk|deadline=20-10-2025");
                }
                TodoItem item = service.add(name, deadline);
                return ok("Added: #" + item.getId() + " " + item.getName());

            } else if ("delete".equals(cmd)) {
                String idS = args.get("id");
                if (idS == null) return bad("delete needs id. Example: delete|id=1");
                int id = Integer.parseInt(idS);
                TodoItem t = service.delete(id);
                return t == null ? notFound("Task not found for id=" + id) : ok("Removed: " + t.getName());

            } else if ("edit".equals(cmd)) {
                String idS = args.get("id");
                if (idS == null) return bad("edit needs id. Example: edit|id=1|name=New name|deadline=20-11-2025");
                int id = Integer.parseInt(idS);
                String name = args.get("name");
                String deadline = args.get("deadline");
                TodoItem t = service.edit(id, name, deadline);
                return t == null ? notFound("Task not found for id=" + id) : ok("Updated: #" + id);

            } else if ("toggle".equals(cmd)) {
                String idS = args.get("id");
                if (idS == null) return bad("toggle needs id. Example: toggle|id=1");
                int id = Integer.parseInt(idS);
                TodoItem t = service.toggle(id);
                return t == null ? notFound("Task not found for id=" + id) : ok("Toggled: #" + id + " now " + (t.isCompleted() ? "completed" : "not completed"));

            } else if ("sort".equals(cmd)) {
                String optionS = args.get("option");
                if (optionS == null) return bad("sort needs option. Example: sort|option=1");
                int opt = Integer.parseInt(optionS);
                service.sortByOption(opt);
                return ok("Sort applied.");

            } else if ("list".equals(cmd)) {
                List<TodoItem> list = service.list();
                if (list.isEmpty()) return ok("No tasks available.");
                String out = list.stream()
                        .map(i -> String.format("%d. [%s] %s (Deadline: %s)",
                                i.getId(),
                                i.isCompleted() ? "x" : " ",
                                i.getName(),
                                i.getDeadline()))
                        .collect(Collectors.joining("\n"));
                return ok(out);

            } else if ("help".equals(cmd) || cmd.isEmpty()) {
                return ok(helpText());

            } else {
                return bad("Unknown command. Type help");
            }

        } catch (NumberFormatException ex) {
            // numeric parsing errors (bad id/option values)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error: Invalid number format in command arguments.");
        } catch (IllegalArgumentException | java.time.format.DateTimeParseException ex) {
            // validation / date parsing / other input errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error: " + (ex.getMessage() == null ? ex.toString() : ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error: Internal server error");
        }
    }

    // simple parser
    private static Command parseCommand(String line) {
        if (line == null) return new Command("", Collections.emptyMap());
        // inside parseCommand(String line)
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return new Command("", Collections.emptyMap());

        String[] parts = trimmed.split("\\|");
        String cmd = parts[0].trim().toLowerCase();
        Map<String, String> args = new HashMap<>();

        // fallback: if command doesn’t start with a keyword but looks like "taskname date"
        if (!List.of("add","delete","edit","toggle","sort","list","help").contains(cmd)) {
            String[] tokens = trimmed.split("\\s+");
            if (tokens.length >= 2 && tokens[tokens.length - 1].matches("\\d{2}-\\d{2}-\\d{4}")) {
                cmd = "add";
                args.put("name", String.join(" ", Arrays.copyOf(tokens, tokens.length - 1)));
                args.put("deadline", tokens[tokens.length - 1]);
                return new Command(cmd, args);
            }
        }

        for (int i = 1; i < parts.length; i++) {
            String p = parts[i];
            int eq = p.indexOf('=');
            if (eq >= 0) {
                String k = p.substring(0, eq).trim().toLowerCase();
                String v = p.substring(eq + 1).trim();
                args.put(k, v);
            }
        }
        return new Command(cmd, args);
    }

    private static class Command {
        final String cmd;
        final Map<String, String> args;
        Command(String cmd, Map<String, String> args) {
            this.cmd = cmd;
            this.args = args;
        }
    }

    // response helpers
    private ResponseEntity<String> ok(String s) {
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(s);
    }

    private ResponseEntity<String> bad(String s) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Usage:\n" + s);
    }

    private ResponseEntity<String> notFound(String s) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body(s);
    }

    private String helpText() {
        return String.join("\n",
                "Usage:",
                "add|name=<task name>|deadline=<DD-MM-YYYY>",
                "delete|id=<n>",
                "edit|id=<n>|name=<new name>|deadline=<DD-MM-YYYY>",
                "toggle|id=<n>",
                "sort|option=1..4 (1=deadline,2=nameA-Z,3=nameZ-A,4=completed first)",
                "list"
        );
    }
}