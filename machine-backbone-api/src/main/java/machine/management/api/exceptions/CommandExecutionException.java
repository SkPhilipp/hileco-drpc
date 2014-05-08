package machine.management.api.exceptions;

/**
 * An exception in executing the command from Java, does not indicate the command being executed has erred.
 */
public class CommandExecutionException extends Exception {

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
