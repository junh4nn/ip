package jimbo.ui;

import java.util.ArrayList;

import jimbo.exception.JimboException;
import jimbo.task.Task;
import jimbo.task.TaskList;

/**
 * Builds the user-facing text for every outcome Jimbo can produce: the
 * welcome/goodbye messages, the task list, confirmation messages for task
 * operations, and error messages. Methods return the message as a
 * {@code String} rather than printing it, so the same message text can be
 * shown in a console loop, a GUI, or asserted on directly in a test.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    /**
     * Returns the horizontal divider line used to separate sections of
     * console output.
     */
    public String showLine() {
        return LINE;
    }

    /**
     * Returns the startup banner and welcome message.
     */
    public String showWelcome() {
        String banner = "   ___   _____  ___  ___ ______   _____ \n"
                + "  |_  | |_   _| |  \\/  | | ___ \\ |  _  |\n"
                + "    | |   | |   | .  . | | |_/ / | | | |\n"
                + "    | |   | |   | |\\/| | | ___ \\ | | | |\n"
                + "/\\__/ /  _| |_  | |  | | | |_/ / \\ \\_/ /\n"
                + "\\____/   \\___/  \\_|  |_/ \\____/   \\___/ \n";

        return banner + "\n*chomp chomp* Hiii, I'm Jimbo! Got any tasks for me to nom on?";
    }

    /**
     * Returns the goodbye message shown when the user types "bye".
     */
    public String showGoodbye() {
        return "Nom nom nom... byeee! *burp* Come back with more tasks soon!";
    }

    /**
     * Returns the numbered task list, e.g. in response to the "list" command.
     */
    public String showTaskList(TaskList tasks) {
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append("\n").append(i + 1).append(") ").append(tasks.get(i));
        }
        return sb.toString();
    }

    /**
     * Returns the confirmation shown after a task is marked or unmarked as
     * done.
     */
    public String showTaskMarked(Task task, boolean isDone) {
        String heading = isDone
                ? "Yum! Gobbled this one right up:"
                : "Aww, spat this one back out — not done yet:";
        return heading + "\n  " + task;
    }

    /**
     * Returns the confirmation shown after a task is added, including the
     * updated task count.
     */
    public String showTaskAdded(Task task, int taskCount) {
        return "*chomp* Added this to my plate:\n  " + task
                + "\nI've got " + taskCount + " tasks to munch through now.";
    }

    /**
     * Returns the confirmation shown after a task is deleted, including the
     * updated task count.
     */
    public String showTaskDeleted(Task task, int taskCount) {
        return "Pfft, tossed this one out:\n  " + task
                + "\n" + taskCount + " tasks left on my plate.";
    }

    /**
     * Returns the confirmation shown after a task's details are updated.
     */
    public String showTaskUpdated(Task task) {
        return "Mmm, tastes different now:\n  " + task;
    }

    /**
     * Returns the numbered list of tasks matching a search keyword, e.g. in
     * response to the "find" command.
     */
    public String showMatchingTasks(ArrayList<Task> matches) {
        if (matches.isEmpty()) {
            return "Sniff sniff... nothing on my plate smells like that.";
        }
        StringBuilder sb = new StringBuilder("Ooh, I sniffed out these tasty matches:");
        for (int i = 0; i < matches.size(); i++) {
            sb.append("\n").append(i + 1).append(") ").append(matches.get(i));
        }
        return sb.toString();
    }

    /**
     * Returns an error message in response to a {@link JimboException}.
     */
    public String showError(String message) {
        return "Yuck, that's not candy! " + message;
    }
}
