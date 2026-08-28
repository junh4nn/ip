package jimbo.ui;

import java.util.ArrayList;

import jimbo.exception.JimboException;
import jimbo.task.Task;
import jimbo.task.TaskList;

/**
 * Handles all interaction with the user: printing the welcome/goodbye
 * messages, the task list, confirmation messages for task operations, and
 * error messages. Keeping all {@code System.out} calls here means the rest
 * of the program can describe *what* happened without worrying about
 * *how* it's displayed.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    /**
     * Prints the horizontal divider line used to separate sections of
     * output.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Prints the startup banner and welcome message.
     */
    public void showWelcome() {
        String banner = "   ___   _____  ___  ___ ______   _____ \n"
                + "  |_  | |_   _| |  \\/  | | ___ \\ |  _  |\n"
                + "    | |   | |   | .  . | | |_/ / | | | |\n"
                + "    | |   | |   | |\\/| | | ___ \\ | | | |\n"
                + "/\\__/ /  _| |_  | |  | | | |_/ / \\ \\_/ /\n"
                + "\\____/   \\___/  \\_|  |_/ \\____/   \\___/ \n";

        showLine();
        System.out.println(banner);
        System.out.println("Hello! I'm Jimbo.");
        System.out.println("What can I do for you?");
        showLine();
    }

    /**
     * Prints the goodbye message shown when the user types "bye".
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Prints the numbered task list, e.g. in response to the "list" command.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        showLine();
    }

    /**
     * Prints the confirmation shown after a task is marked or unmarked as
     * done.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        if (isDone) {
            System.out.println("Nice! I've marked this task as done:");
        } else {
            System.out.println("OK, I've marked this task as not done yet:");
        }
        System.out.println("  " + task);
        showLine();
    }

    /**
     * Prints the confirmation shown after a task is added, including the
     * updated task count.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        showLine();
    }

    /**
     * Prints the confirmation shown after a task is deleted, including the
     * updated task count.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        showLine();
    }

    /**
     * Prints the numbered list of tasks matching a search keyword, e.g. in
     * response to the "find" command.
     */
    public void showMatchingTasks(ArrayList<Task> matches) {
        if (matches.isEmpty()) {
            System.out.println("I couldn't find any matching tasks in your list.");
        } else {
            System.out.println("Here are the matching tasks in your list:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.println((i + 1) + "." + matches.get(i));
            }
        }
        showLine();
    }

    /**
     * Prints an error message in response to a {@link JimboException}.
     */
    public void showError(String message) {
        showLine();
        System.out.println("Oopsie.. " + message);
        showLine();
    }
}
