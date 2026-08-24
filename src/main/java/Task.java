/**
 * Represents a task with a description and a done/not-done status.
 * This is an abstract base class: concrete task types (e.g. Todo, Deadline,
 * Event) extend it and specify their own type icon and any extra details.
 */
public abstract class Task {
    protected String description;
    protected DoneStatus status;

    public Task(String description) {
        this.description = description;
        this.status = DoneStatus.NOT_DONE;
    }

    public String getDescription() {
        return description;
    }

    public String getStatusIcon() {
        return (status == DoneStatus.DONE ? "X" : " "); // mark done task with X
    }

    /**
     * Returns the single-letter icon identifying this task's type,
     * e.g. "T" for Todo, "D" for Deadline, "E" for Event.
     */
    public abstract String getTypeIcon();

    public void markAsDone() {
        this.status = DoneStatus.DONE;
    }

    public void markAsNotDone() {
        this.status = DoneStatus.NOT_DONE;
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
