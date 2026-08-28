import java.util.ArrayList;

/**
 * Represents the in-memory list of tasks. Wraps an {@link ArrayList} of
 * {@link Task}s and exposes the operations callers need (add, delete,
 * retrieve, count) so they don't have to work with the raw collection
 * directly.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the given tasks, e.g. ones loaded from
     * the save file.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 0-based index.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given 0-based index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks, e.g. for {@link Storage} to
     * persist to disk.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
