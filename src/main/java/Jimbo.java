import java.util.Scanner;

public class Jimbo {
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        TaskList tasks = new TaskList(Storage.load());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            try {
                if (command.equals("bye")) {
                    ui.showGoodbye();
                    break;
                } else if (command.equals("list")) {
                    ui.showTaskList(tasks);
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    String indexArg = command.length() > 4 ? command.substring(4) : "";
                    setTaskDone(ui, tasks, indexArg, DoneStatus.DONE);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    String indexArg = command.length() > 6 ? command.substring(6) : "";
                    setTaskDone(ui, tasks, indexArg, DoneStatus.NOT_DONE);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.length() > 4 ? command.substring(4).trim() : "";
                    if (description.isEmpty()) {
                        throw new JimboException("The description of a todo cannot be empty.");
                    }
                    addTask(ui, tasks, new Todo(description));
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    addDeadline(ui, tasks, command.length() > 8 ? command.substring(8).trim() : "");
                } else if (command.equals("event") || command.startsWith("event ")) {
                    addEvent(ui, tasks, command.length() > 5 ? command.substring(5).trim() : "");
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    String indexArg = command.length() > 6 ? command.substring(6) : "";
                    deleteTask(ui, tasks, indexArg);
                } else {
                    throw new JimboException("I'm sorry, but I don't know what that means :-(");
                }
            } catch (JimboException e) {
                ui.showError(e.getMessage());
            }
        }
        scanner.close();
    }

    /**
     * Parses {@code indexArg} (a 1-based task number, as typed by the user,
     * possibly with surrounding whitespace) into a valid 0-based index into
     * {@code tasks}. {@code commandName} is used to tailor the error message
     * shown when {@code indexArg} is blank, e.g. "mark" or "delete".
     *
     * @throws JimboException if the number is missing, not a valid integer,
     *                        or does not correspond to a task in the list.
     */
    private static int parseTaskIndex(TaskList tasks, String indexArg, String commandName)
            throws JimboException {
        String trimmed = indexArg.trim();
        if (trimmed.isEmpty()) {
            throw new JimboException("Please tell me which task number to " + commandName
                    + ", e.g. \"" + commandName + " 2\".");
        }
        int index;
        try {
            index = Integer.parseInt(trimmed) - 1;
        } catch (NumberFormatException e) {
            throw new JimboException("\"" + trimmed + "\" is not a valid task number.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new JimboException("Task number " + (index + 1) + " doesn't exist. "
                    + "You currently have " + tasks.size() + " task(s) in the list.");
        }
        return index;
    }

    /**
     * Marks or unmarks the task identified by {@code indexArg} and prints
     * the standard confirmation message.
     *
     * @throws JimboException if the number is missing, not a valid integer,
     *                        or does not correspond to a task in the list.
     */
    private static void setTaskDone(Ui ui, TaskList tasks, String indexArg, DoneStatus status)
            throws JimboException {
        int index = parseTaskIndex(tasks, indexArg, status == DoneStatus.DONE ? "mark" : "unmark");
        Task task = tasks.get(index);
        if (status == DoneStatus.DONE) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        ui.showTaskMarked(task, status == DoneStatus.DONE);
        Storage.save(tasks.getTasks());
    }

    /**
     * Parses {@code rest} (the text after the "deadline" keyword) into a
     * description and a "/by" time, and adds the resulting task.
     *
     * @throws JimboException if the description or the "/by" time is missing.
     */
    private static void addDeadline(Ui ui, TaskList tasks, String rest) throws JimboException {
        if (rest.isEmpty()) {
            throw new JimboException("The description of a deadline cannot be empty.");
        }
        String[] parts = rest.split(" /by ", 2);
        if (parts.length < 2) {
            throw new JimboException("A deadline needs a \"/by\" time, e.g. "
                    + "\"deadline return book /by Sunday\".");
        }
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new JimboException("The description of a deadline cannot be empty.");
        }

        addTask(ui, tasks, new Deadline(description, by));
    }

    /**
     * Parses {@code rest} (the text after the "event" keyword) into a
     * description, a "/from" time and a "/to" time, and adds the resulting
     * task.
     *
     * @throws JimboException if the description, the "/from" time or the
     *                        "/to" time is missing.
     */
    private static void addEvent(Ui ui, TaskList tasks, String rest) throws JimboException {
        if (rest.isEmpty()) {
            throw new JimboException("The description of an event cannot be empty.");
        }
        String[] parts = rest.split(" /from ", 2);
        if (parts.length < 2) {
            throw new JimboException("An event needs a \"/from\" and \"/to\" time, e.g. "
                    + "\"event project meeting /from Mon 2pm /to Mon 4pm\".");
        }
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new JimboException("The description of an event cannot be empty.");
        }
        String[] timeParts = parts[1].split(" /to ", 2);
        if (timeParts.length < 2) {
            throw new JimboException("An event needs a \"/to\" time, e.g. "
                    + "\"event project meeting /from Mon 2pm /to Mon 4pm\".");
        }
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (from.isEmpty()) {
            throw new JimboException("Please specify a start time for the event after \"/from\".");
        }
        if (to.isEmpty()) {
            throw new JimboException("Please specify an end time for the event after \"/to\".");
        }
        addTask(ui, tasks, new Event(description, from, to));
    }

    /**
     * Removes the task identified by {@code indexArg} from the list and
     * prints the standard "Noted. I've removed this task" confirmation.
     *
     * @throws JimboException if the number is missing, not a valid integer,
     *                        or does not correspond to a task in the list.
     */
    private static void deleteTask(Ui ui, TaskList tasks, String indexArg) throws JimboException {
        int index = parseTaskIndex(tasks, indexArg, "delete");
        Task task = tasks.remove(index);
        ui.showTaskDeleted(task, tasks.size());
        Storage.save(tasks.getTasks());
    }

    /**
     * Adds the given task to the list and prints the standard
     * "Got it. I've added this task" confirmation.
     */
    private static void addTask(Ui ui, TaskList tasks, Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        Storage.save(tasks.getTasks());
    }
}