package hr.fer.zemris.java.custom.scripting.exec;

/**
 * A runtime exception used in the {@link ValueWrapper} class.
 * @author 0036502252
 *
 */
public class ValueWrapperException extends RuntimeException {

    /**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = 3055707646385552386L;

	/** Constructs a new ValueWrapperException with {@code null} as its
     * detail message.
     */
    public ValueWrapperException() {
        super();
    }

    /** Constructs a new ValueWrapperException with the specified detail message.
     * @param   message the detail message.
     */
    public ValueWrapperException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValueWrapperException with the specified detail message and cause.
     * @param  message the detail message 
     * @param  cause the cause
     */
    public ValueWrapperException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructs a new ValueWrapperException with the specified cause.
     * @param  cause the cause
     */
    public ValueWrapperException(Throwable cause) {
        super(cause);
    }


}
