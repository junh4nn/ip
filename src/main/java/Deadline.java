import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that needs to be done before a specific date/time,
 * e.g. "submit report by 11/10/2019 5pm".
 */
public class Deadline extends Task {
    /**
     * Format accepted for the "by" date/time when typed by the user in a
     * command, e.g. "2/12/2019 1800".
     */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Format used to display the "by" date/time back to the user,
     * e.g. "Dec 02 2019, 6:00PM".
     */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    protected LocalDateTime by;

    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, INPUT_FORMAT);
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return "D | " + (status == DoneStatus.DONE ? 1 : 0) + " | " + description + " | " + by;
    }
}