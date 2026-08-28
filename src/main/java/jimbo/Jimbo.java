package jimbo;

import java.util.Scanner;

import jimbo.exception.JimboException;
import jimbo.parser.Parser;
import jimbo.storage.Storage;
import jimbo.task.DoneStatus;
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
    private static final String FILE_PATH = "./data/jimbo.txt";

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
        ui.showWelcome();

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
                    int index = parser.parseTaskIndex(tasks, indexArg, "mark");
                    setTaskDone(index, DoneStatus.DONE);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    String indexArg = command.length() > 6 ? command.substring(6) : "";
                    int index = parser.parseTaskIndex(tasks, indexArg, "unmark");
                    setTaskDone(index, DoneStatus.NOT_DONE);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String rest = command.length() > 4 ? command.substring(4).trim() : "";
                    addTask(parser.parseTodo(rest));
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String rest = command.length() > 8 ? command.substring(8).trim() : "";
                    addTask(parser.parseDeadline(rest));
                } else if (command.equals("event") || command.startsWith("event ")) {
                    String rest = command.length() > 5 ? command.substring(5).trim() : "";
                    addTask(parser.parseEvent(rest));
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    String indexArg = command.length() > 6 ? command.substring(6) : "";
                    int index = parser.parseTaskIndex(tasks, indexArg, "delete");
                    deleteTask(index);
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
     * Marks or unmarks the task at {@code index} and prints the standard
     * confirmation message.
     */
    private void setTaskDone(int index, DoneStatus status) {
        Task task = tasks.get(index);
        if (status == DoneStatus.DONE) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        ui.showTaskMarked(task, status == DoneStatus.DONE);
        storage.save(tasks.getTasks());
    }

    /**
     * Removes the task at {@code index} from the list and prints the
     * standard "Noted. I've removed this task" confirmation.
     */
    private void deleteTask(int index) {
        Task task = tasks.remove(index);
        ui.showTaskDeleted(task, tasks.size());
        storage.save(tasks.getTasks());
    }

    /**
     * Adds the given task to the list and prints the standard
     * "Got it. I've added this task" confirmation.
     */
    private void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks.getTasks());
    }

    /**
     * Starts Jimbo, loading and saving tasks at {@link #FILE_PATH}.
     */
    public static void main(String[] args) {
        new Jimbo(FILE_PATH).run();
    }
}
