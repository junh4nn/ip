package jimbo;

import java.util.Scanner;

import jimbo.exception.JimboException;
import jimbo.parser.Parser;
import jimbo.storage.Storage;
import jimbo.task.Deadline;
import jimbo.task.DoneStatus;
import jimbo.task.Event;
import jimbo.task.Task;
import jimbo.task.TaskList;
import jimbo.ui.Ui;

/**
 * Entry point for the Jimbo task-list chatbot. Wires together the four
 * collaborators (see {@link Ui}, {@link Storage}, {@link Parser},
 * {@link TaskList}) and drives the command loop that reads user input,
 * interprets it, and applies it to the task list.
 */
public class Jimbo {
    public static final String FILE_PATH = "./data/jimbo.txt";

    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;

    /**
     * Creates a Jimbo instance whose task list is loaded from, and saved
     * to, the save file at {@code filePath}.
     */
    public Jimbo(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        tasks = new TaskList(storage.load());
    }

    /**
     * Shows the welcome message, then reads and handles commands from
     * standard input until the user types "bye".
     */
    public void run() {
        System.out.println(ui.showLine());
        System.out.println(ui.showWelcome());
        System.out.println(ui.showLine());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            String response = getResponse(command);
            System.out.println(ui.showLine());
            System.out.println(response);
            System.out.println(ui.showLine());
            if (command.equals("bye")) {
                break;
            }
        }
        scanner.close();
    }

    /**
     * Interprets a single line of user input and returns Jimbo's reply as a
     * plain string, applying whatever task-list/storage change the command
     * implies. This is the sole place command dispatch happens, so it can be
     * driven by a console loop (see {@link #run()}), a future GUI, or a unit
     * test, all without duplicating the parsing/response logic.
     */
    public String getResponse(String input) {
        try {
            String[] tokens = input.split(" ", 2);
            String commandWord = tokens[0];
            String args = tokens.length > 1 ? tokens[1].trim() : "";

            switch (commandWord) {
                case "bye" -> {
                    return ui.showGoodbye();
                }
                case "list" -> {
                    return ui.showTaskList(tasks);
                }
                case "mark" -> {
                    int index = parser.parseTaskIndex(tasks, args, "mark");
                    return setTaskDone(index, DoneStatus.DONE);
                }
                case "unmark" -> {
                    int index = parser.parseTaskIndex(tasks, args, "unmark");
                    return setTaskDone(index, DoneStatus.NOT_DONE);
                }
                case "todo" -> {
                    return addTask(parser.parseTodo(args));
                }
                case "deadline" -> {
                    return addTask(parser.parseDeadline(args));
                }
                case "event" -> {
                    return addTask(parser.parseEvent(args));
                }
                case "delete" -> {
                    int index = parser.parseTaskIndex(tasks, args, "delete");
                    return deleteTask(index);
                }
                case "update" -> {
                    return updateTask(args);
                }
                case "find" -> {
                    return ui.showMatchingTasks(tasks.find(parser.parseFind(args)));
                }
                default -> throw new JimboException("I'm sorry, but I don't know what that means :-(");
            }
        } catch (JimboException e) {
            return ui.showError(e.getMessage());
        }
    }

    /**
     * Marks or unmarks the task at {@code index}, persists the change, and
     * returns the standard confirmation message.
     */
    private String setTaskDone(int index, DoneStatus status) {
        Task task = tasks.get(index);
        if (status == DoneStatus.DONE) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks.getTasks());
        return ui.showTaskMarked(task, status == DoneStatus.DONE);
    }

    /**
     * Removes the task at {@code index} from the list, persists the change,
     * and returns the standard "Noted. I've removed this task" confirmation.
     */
    private String deleteTask(int index) {
        Task task = tasks.remove(index);
        storage.save(tasks.getTasks());
        return ui.showTaskDeleted(task, tasks.size());
    }

    /**
     * Updates a single field of the task named in {@code args}, which must
     * be of the form "{@code <index> /<field> <new value>}", e.g.
     * "{@code 2 /by 3/12/2019 1800}". Only one field can be updated per
     * command; {@code /desc} applies to any task type, while {@code /by}
     * (Deadline) and {@code /from}/{@code /to} (Event) are rejected if the
     * task at {@code index} is not of the matching type.
     *
     * @throws JimboException if the index is missing/invalid, the field
     *                        flag or new value is missing, the flag is not
     *                        recognised, the flag does not apply to the
     *                        task's type, or the new value is invalid for
     *                        that field (e.g. an unparsable date/time).
     */
    private String updateTask(String args) throws JimboException {
        String[] indexAndRest = args.split(" ", 2);
        int index = parser.parseTaskIndex(tasks, indexAndRest[0], "update");
        if (indexAndRest.length < 2 || indexAndRest[1].trim().isEmpty()) {
            throw new JimboException("Please tell me what to update, e.g. \"update 2 /by 3/12/2019 1800\".");
        }

        String[] flagAndValue = indexAndRest[1].trim().split(" ", 2);
        String flag = flagAndValue[0];
        String value = flagAndValue.length > 1 ? flagAndValue[1].trim() : "";
        if (value.isEmpty()) {
            throw new JimboException("Please provide a new value after \"" + flag + "\".");
        }

        Task task = tasks.get(index);
        switch (flag) {
            case "/desc" -> task.setDescription(value);
            case "/by" -> {
                if (!(task instanceof Deadline deadline)) {
                    throw new JimboException("Only a deadline has a \"/by\" time to update.");
                }
                deadline.setBy(value);
            }
            case "/from" -> {
                if (!(task instanceof Event event)) {
                    throw new JimboException("Only an event has a \"/from\" time to update.");
                }
                event.setFrom(value);
            }
            case "/to" -> {
                if (!(task instanceof Event event)) {
                    throw new JimboException("Only an event has a \"/to\" time to update.");
                }
                event.setTo(value);
            }
            default -> throw new JimboException("\"" + flag + "\" is not something I can update. "
                    + "Try \"/desc\", \"/by\", \"/from\", or \"/to\".");
        }

        storage.save(tasks.getTasks());
        return ui.showTaskUpdated(task);
    }

    /**
     * Adds the given task to the list, persists the change, and returns the
     * standard "Got it. I've added this task" confirmation.
     */
    private String addTask(Task task) {
        tasks.add(task);
        storage.save(tasks.getTasks());
        return ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Starts Jimbo, loading and saving tasks at {@link #FILE_PATH}.
     */
    public static void main(String[] args) {
        new Jimbo(FILE_PATH).run();
    }
}
