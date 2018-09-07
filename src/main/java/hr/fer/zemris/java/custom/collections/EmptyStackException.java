package hr.zemris.java.custom.collections;

/**
 * Thrown to indicate that a stack is empty.
 * @author 0036502252
 *
 */
public class EmptyStackException extends RuntimeException {
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code EmptyStackException} with no detail message.
	 */
	public EmptyStackException() {
		super();
	}
	
	/**
	 * Constructs a {@code EmptyStackException} with the specified 
	 * detail message.
	 * @param message the detail message
	 */
	public EmptyStackException(String message) {
		super(message);
	}
}
