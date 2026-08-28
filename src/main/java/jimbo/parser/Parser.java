package jimbo.parser;

import jimbo.exception.JimboException;
import jimbo.task.Deadline;
import jimbo.task.Event;
import jimbo.task.Task;
import jimbo.task.TaskList;
import jimbo.task.Todo;

/**
 * Makes sense of raw user input: turns the text typed after a command word
 * (e.g. "todo", "deadline", "event") into a {@link Task}, and turns a task
 * number typed by the user into a valid 0-based index into a
 * {@link TaskList}. Jimbo's main loop still decides *which* command was
 * typed; Parser is responsible for interpreting the arguments that follow.
 */
public class Parser {
    /**
     * Parses {@code rest} (the text after the "todo" keyword) into a
     * {@link Todo}.
     *
     * @throws JimboException if the description is empty.
     */
    public Task parseTodo(String rest) throws JimboException {
        if (rest.isEmpty()) {
            throw new JimboException("The description of a todo cannot be empty.");
        }
        return new Todo(rest);
    }

    /**
     * Parses {@code rest} (the text after the "deadline" keyword) into a
     * {@link Deadline}.
     *
     * @throws JimboException if the description or the "/by" time is missing.
     */
    public Task parseDeadline(String rest) throws JimboException {
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
        return new Deadline(description, by);
    }

    /**
     * Parses {@code rest} (the text after the "event" keyword) into an
     * {@link Event}.
     *
     * @throws JimboException if the description, the "/from" time or the
     *                        "/to" time is missing.
     */
    public Task parseEvent(String rest) throws JimboException {
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
        return new Event(description, from, to);
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
    public int parseTaskIndex(TaskList tasks, String indexArg, String commandName) throws JimboException {
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
}
