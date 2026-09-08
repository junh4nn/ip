package jimbo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jimbo.exception.JimboException;

/**
 * Represents a task that starts at a specific date/time and ends at a
 * specific date/time, e.g. "team project meeting /from 2/10/2019 1400
 * /to 2/10/2019 1600". Note that {@code from} and {@code to} are each a
 * full date/time on their own; the date is not shared or inferred between
 * them, so it must be repeated even when both fall on the same day.
 */
public class Event extends Task {
    /**
     * Format accepted for the "from"/"to" date/time when typed by the user
     * in a command, e.g. "2/12/2019 1800".
     */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Format used to display the "from"/"to" date/time back to the user,
     * e.g. "Dec 02 2019, 6:00PM".
     */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * @throws JimboException if {@code from} or {@code to} does not match
     *                        the expected "d/M/yyyy HHmm" format, e.g.
     *                        "2/12/2019 1800".
     */
    public Event(String description, String from, String to) throws JimboException {
        super(description);
        try {
            this.from = LocalDateTime.parse(from, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new JimboException("Invalid event start date/time \"" + from
                    + "\". Please use the format d/M/yyyy HHmm, e.g. 2/12/2019 1800.");
        }
        try {
            this.to = LocalDateTime.parse(to, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new JimboException("Invalid event end date/time \"" + to
                    + "\". Please use the format d/M/yyyy HHmm, e.g. 2/12/2019 1800.");
        }
    }

    /**
     * Constructs an Event from already-parsed {@link LocalDateTime} values,
     * e.g. when reconstructing a task from the save file.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return "E | " + (status == DoneStatus.DONE ? 1 : 0) + " | " + description + " | "
                + from.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " | "
                + to.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
