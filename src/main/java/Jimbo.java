import java.util.Scanner;

public class Jimbo {
    private static final String FILE_PATH = "./data/jimbo.txt";

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage(FILE_PATH);
        TaskList tasks = new TaskList(storage.load());
        Parser parser = new Parser();

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
                    setTaskDone(ui, storage, tasks, index, DoneStatus.DONE);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    String indexArg = command.length() > 6 ? command.substring(6) : "";
                    int index = parser.parseTaskIndex(tasks, indexArg, "unmark");
                    setTaskDone(ui, storage, tasks, index, DoneStatus.NOT_DONE);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String rest = command.length() > 4 ? command.substring(4).trim() : "";
                    addTask(ui, storage, tasks, parser.parseTodo(rest));
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String rest = command.length() > 8 ? command.substring(8).trim() : "";
                    addTask(ui, storage, tasks, parser.parseDeadline(rest));
                } else if (command.equals("event") || command.startsWith("event ")) {
                    String rest = command.length() > 5 ? command.substring(5).trim() : "";
                    addTask(ui, storage, tasks, parser.parseEvent(rest));
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    String indexArg = command.length() > 6 ? command.substring(6) : "";
                    int index = parser.parseTaskIndex(tasks, indexArg, "delete");
                    deleteTask(ui, storage, tasks, index);
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
    private static void setTaskDone(Ui ui, Storage storage, TaskList tasks, int index, DoneStatus status) {
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
    private static void deleteTask(Ui ui, Storage storage, TaskList tasks, int index) {
        Task task = tasks.remove(index);
        ui.showTaskDeleted(task, tasks.size());
        storage.save(tasks.getTasks());
    }

    /**
     * Adds the given task to the list and prints the standard
     * "Got it. I've added this task" confirmation.
     */
    private static void addTask(Ui ui, Storage storage, TaskList tasks, Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.save(tasks.getTasks());
    }
}
