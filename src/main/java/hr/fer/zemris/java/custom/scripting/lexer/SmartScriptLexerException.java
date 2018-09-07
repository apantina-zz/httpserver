package hr.fer.zemris.java.custom.scripting.lexer;
/**
 * A runtime exception used in the {@link SmartScriptLexer} class.
 * @author 0036502252
 *
 */
public class SmartScriptLexerException extends RuntimeException {
    /**
     * Auto-generated serial ID for this exception.
	 */
	private static final long serialVersionUID = -6432817845190015329L;
	
    /** Constructs a new SmartScriptLexerException with {@code null} as its
     * detail message.
     */
    public SmartScriptLexerException() {
        super();
    }

    /** Constructs a new SmartScriptLexerException with the specified detail message.
     * @param   message the detail message.
     */
    public SmartScriptLexerException(String message) {
        super(message);
    }

    /**
     * Constructs a new SmartScriptLexerException with the specified detail message and cause.
     * @param  message the detail message 
     * @param  cause the cause
     */
    public SmartScriptLexerException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructs a new SmartScriptLexerException with the specified cause.
     * @param  cause the cause
     */
    public SmartScriptLexerException(Throwable cause) {
        super(cause);
    }


}
