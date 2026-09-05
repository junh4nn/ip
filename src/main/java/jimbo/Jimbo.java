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
            if (input.equals("bye")) {
                return ui.showGoodbye();
            } else if (input.equals("list")) {
                return ui.showTaskList(tasks);
            } else if (input.equals("mark") || input.startsWith("mark ")) {
                String indexArg = input.length() > 4 ? input.substring(4) : "";
                int index = parser.parseTaskIndex(tasks, indexArg, "mark");
                return setTaskDone(index, DoneStatus.DONE);
            } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                String indexArg = input.length() > 6 ? input.substring(6) : "";
                int index = parser.parseTaskIndex(tasks, indexArg, "unmark");
                return setTaskDone(index, DoneStatus.NOT_DONE);
            } else if (input.equals("todo") || input.startsWith("todo ")) {
                String rest = input.length() > 4 ? input.substring(4).trim() : "";
                return addTask(parser.parseTodo(rest));
            } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                String rest = input.length() > 8 ? input.substring(8).trim() : "";
                return addTask(parser.parseDeadline(rest));
            } else if (input.equals("event") || input.startsWith("event ")) {
                String rest = input.length() > 5 ? input.substring(5).trim() : "";
                return addTask(parser.parseEvent(rest));
            } else if (input.equals("delete") || input.startsWith("delete ")) {
                String indexArg = input.length() > 6 ? input.substring(6) : "";
                int index = parser.parseTaskIndex(tasks, indexArg, "delete");
                return deleteTask(index);
            } else if (input.equals("find") || input.startsWith("find ")) {
                String rest = input.length() > 4 ? input.substring(4).trim() : "";
                String keyword = parser.parseFind(rest);
                return ui.showMatchingTasks(tasks.find(keyword));
            } else {
                throw new JimboException("I'm sorry, but I don't know what that means :-(");
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