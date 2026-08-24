/**
 * Signals that the user entered an invalid command or argument, e.g. a
 * missing task description or a task number that does not exist.
 * Thrown from the command-parsing logic and caught centrally in
 * {@link JimBO#main}, where the message is shown to the user.
 */
public class JimBOException extends Exception {
    public JimBOException(String message) {
        super(message);
    }
}