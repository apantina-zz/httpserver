package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * This enumeration is used to mark various token types that the lexer sends to 
 * the parser.
 * @author 0036502252
 */
public enum TokenType {
	
	/**
	 * Variable name found in tags. Must start with a letter, and followed by
	 * either another letter, a number, or an underscore ('_').
	 */
	VARIABLE,
	
	/**
	 * Function name. Starts with a '@' character.
	 */
	FUNCTION, 
	
	/**
	 * A symbol character.
	 */
	SYMBOL,
	
	/**
	 * A double type constant. 
	 */
	DOUBLE,
	
	/**
	 * An integer type constant.
	 */
	INT,
	
	/**
	 * Used to mark the end of a file.
	 */
	EOF,
	
	/**
	 * A string type constant, found in tags. Not to be confused with text.
	 */
	STRING,
	
	/**
	 * Indicates the start of a tag.
	 */
	SOT,
	
	/**
	 * Indicates the end of a tag.
	 */
	EOT,
	
	/**
	 * Text found outside of tags. Text tokens can only be generated when the 
	 * lexer is working in its text state.
	 */
	TEXT;
}
