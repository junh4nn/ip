import java.util.ArrayList;
import java.util.Scanner;

public class JimBO {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        String banner = "   ___   _____  ___  ___ ______   _____ \n"
                + "  |_  | |_   _| |  \\/  | | ___ \\ |  _  |\n"
                + "    | |   | |   | .  . | | |_/ / | | | |\n"
                + "    | |   | |   | |\\/| | | ___ \\ | | | |\n"
                + "/\\__/ /  _| |_  | |  | | | |_/ / \\ \\_/ /\n"
                + "\\____/   \\___/  \\_|  |_/ \\____/   \\___/ \n";

        System.out.println(LINE);
        System.out.println(banner);
        System.out.println("Hello! I'm JimBO.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        ArrayList<Task> tasks = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            try {
                if (command.equals("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;
                } else if (command.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                    System.out.println(LINE);
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    String indexArg = command.length() > 4 ? command.substring(4) : "";
                    setTaskDone(tasks, indexArg, true);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    String indexArg = command.length() > 6 ? command.substring(6) : "";
                    setTaskDone(tasks, indexArg, false);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.length() > 4 ? command.substring(4).trim() : "";
                    if (description.isEmpty()) {
                        throw new JimBOException("The description of a todo cannot be empty.");
                    }
                    addTask(tasks, new Todo(description));
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    addDeadline(tasks, command.length() > 8 ? command.substring(8).trim() : "");
                } else if (command.equals("event") || command.startsWith("event ")) {
                    addEvent(tasks, command.length() > 5 ? command.substring(5).trim() : "");
                } else {
                    throw new JimBOException("I'm sorry, but I don't know what that means :-(");
                }
            } catch (JimBOException e) {
                System.out.println(LINE);
                System.out.println("Oopsie.. " + e.getMessage());
                System.out.println(LINE);
            }
        }
        scanner.close();
    }

    /**
     * Marks or unmarks the task identified by {@code indexArg} (a 1-based
     * task number, as typed by the user, possibly with surrounding
     * whitespace) and prints the standard confirmation message.
     *
     * @throws JimBOException if the number is missing, not a valid integer,
     *                        or does not correspond to a task in the list.
     */
    private static void setTaskDone(ArrayList<Task> tasks, String indexArg, boolean done) throws JimBOException {
        String trimmed = indexArg.trim();
        if (trimmed.isEmpty()) {
            String command = done ? "mark" : "unmark";
            throw new JimBOException("Please tell me which task number to " + command
                    + ", e.g. \"" + command + " 2\".");
        }
        int index;
        try {
            index = Integer.parseInt(trimmed) - 1;
        } catch (NumberFormatException e) {
            throw new JimBOException("\"" + trimmed + "\" is not a valid task number.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new JimBOException("Task number " + (index + 1) + " doesn't exist. "
                    + "You currently have " + tasks.size() + " task(s) in the list.");
        }

        Task task = tasks.get(index);
        if (done) {
            task.markAsDone();
            System.out.println("Nice! I've marked this task as done:");
        } else {
            task.markAsNotDone();
            System.out.println("OK, I've marked this task as not done yet:");
        }
        System.out.println("  " + task);
        System.out.println(LINE);
    }

    /**
     * Parses {@code rest} (the text after the "deadline" keyword) into a
     * description and a "/by" time, and adds the resulting task.
     *
     * @throws JimBOException if the description or the "/by" time is missing.
     */
    private static void addDeadline(ArrayList<Task> tasks, String rest) throws JimBOException {
        if (rest.isEmpty()) {
            throw new JimBOException("The description of a deadline cannot be empty.");
        }
        String[] parts = rest.split(" /by ", 2);
        if (parts.length < 2) {
            throw new JimBOException("A deadline needs a \"/by\" time, e.g. "
                    + "\"deadline return book /by Sunday\".");
        }
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new JimBOException("The description of a deadline cannot be empty.");
        }

        addTask(tasks, new Deadline(description, by));
    }

    /**
     * Parses {@code rest} (the text after the "event" keyword) into a
     * description, a "/from" time and a "/to" time, and adds the resulting
     * task.
     *
     * @throws JimBOException if the description, the "/from" time or the
     *                        "/to" time is missing.
     */
    private static void addEvent(ArrayList<Task> tasks, String rest) throws JimBOException {
        if (rest.isEmpty()) {
            throw new JimBOException("The description of an event cannot be empty.");
        }
        String[] parts = rest.split(" /from ", 2);
        if (parts.length < 2) {
            throw new JimBOException("An event needs a \"/from\" and \"/to\" time, e.g. "
                    + "\"event project meeting /from Mon 2pm /to Mon 4pm\".");
        }
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new JimBOException("The description of an event cannot be empty.");
        }
        String[] timeParts = parts[1].split(" /to ", 2);
        if (timeParts.length < 2) {
            throw new JimBOException("An event needs a \"/to\" time, e.g. "
                    + "\"event project meeting /from Mon 2pm /to Mon 4pm\".");
        }
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (from.isEmpty()) {
            throw new JimBOException("Please specify a start time for the event after \"/from\".");
        }
        if (to.isEmpty()) {
            throw new JimBOException("Please specify an end time for the event after \"/to\".");
        }
        addTask(tasks, new Event(description, from, to));
    }

    /**
     * Adds the given task to the list and prints the standard
     * "Got it. I've added this task" confirmation.
     */
    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(LINE);
    }
}