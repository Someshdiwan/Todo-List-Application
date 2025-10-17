package io.yourname.todo;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TodoService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private final List<TodoItem> items = new ArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    // add
    public synchronized TodoItem add(String name, String deadline) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Task name cannot be empty.");
        if (!isValidDate(deadline))
            throw new IllegalArgumentException("Invalid deadline. Use DD-MM-YYYY and a real date.");
        int id = idGenerator.getAndIncrement();
        TodoItem t = new TodoItem(id, name.trim(), deadline.trim(), false);
        items.add(t);
        return t;
    }

    // list
    public synchronized List<TodoItem> list() {
        return new ArrayList<>(items);
    }

    // get single
    public synchronized TodoItem get(int id) {
        return items.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
    }

    // delete
    public synchronized TodoItem delete(int id) {
        int idx = indexOf(id);
        if (idx < 0) return null;
        return items.remove(idx);
    }

    // edit
    public synchronized TodoItem edit(int id, String newName, String newDeadline) {
        int idx = indexOf(id);
        if (idx < 0) return null;
        TodoItem t = items.get(idx);
        if (newName != null && !newName.trim().isEmpty()) t.setName(newName.trim());
        if (newDeadline != null && !newDeadline.trim().isEmpty()) {
            if (!isValidDate(newDeadline)) throw new IllegalArgumentException("Invalid deadline.");
            t.setDeadline(newDeadline.trim());
        }
        return t;
    }

    // toggle
    public synchronized TodoItem toggle(int id) {
        int idx = indexOf(id);
        if (idx < 0) return null;
        TodoItem t = items.get(idx);
        t.setCompleted(!t.isCompleted());
        return t;
    }

    // sort options (1..4)
    public synchronized void sortByOption(int option) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) indices.add(i);
        switch (option) {
            case 1 -> indices.sort(Comparator.comparing(i -> LocalDate.parse(items.get(i).getDeadline(), DATE_FORMAT)));
            case 2 -> indices.sort((a,b)-> items.get(a).getName().compareToIgnoreCase(items.get(b).getName()));
            case 3 -> indices.sort((a,b)-> items.get(b).getName().compareToIgnoreCase(items.get(a).getName()));
            case 4 -> indices.sort((a,b)-> {
                int ca = items.get(a).isCompleted() ? 0 : 1;
                int cb = items.get(b).isCompleted() ? 0 : 1;
                if (ca != cb) return Integer.compare(ca, cb);
                return items.get(a).getName().compareToIgnoreCase(items.get(b).getName());
            });
            default -> throw new IllegalArgumentException("Invalid sort option.");
        }
        reorderByIndices(indices);
    }

    private void reorderByIndices(List<Integer> indices) {
        List<TodoItem> copy = new ArrayList<>(items);
        for (int i = 0; i < indices.size(); i++) {
            items.set(i, copy.get(indices.get(i)));
        }
    }

    private int indexOf(int id) {
        for (int i = 0; i < items.size(); i++) if (items.get(i).getId() == id) return i;
        return -1;
    }
}