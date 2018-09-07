package hr.fer.zemris.java.custom.scripting.parser;
/**
 * A runtime exception used in the {@link SmartScriptParser} class.
 * @author 0036502252
 *
 */
public class SmartScriptParserException extends RuntimeException {
    /**
     * Auto-generated serial ID for this exception.
	 */
	private static final long serialVersionUID = -6432817845190015329L;
	
    /** Constructs a new SmartScriptParserException with {@code null} as its
     * detail message.
     */
    public SmartScriptParserException() {
        super();
    }

    /** Constructs a new SmartScriptParserException with the specified detail message.
     * @param   message the detail message.
     */
    public SmartScriptParserException(String message) {
        super(message);
    }

    /**
     * Constructs a new SmartScriptParserException with the specified detail message and cause.
     * @param  message the detail message 
     * @param  cause the cause
     */
    public SmartScriptParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructs a new SmartScriptParserException with the specified cause.
     * @param  cause the cause
     */
    public SmartScriptParserException(Throwable cause) {
        super(cause);
    }


}
