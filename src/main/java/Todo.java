/**
 * Represents a task with no date or time attached to it,
 * e.g. "borrow book".
 */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }
}