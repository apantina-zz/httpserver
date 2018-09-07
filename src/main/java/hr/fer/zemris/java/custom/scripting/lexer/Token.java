package hr.fer.zemris.java.custom.scripting.lexer;


/**
 * Represents a token which is created when a lexer deconstructs a text input. 
 * A token can have multiple types, and each represents a certain type of value
 * held in the token. A {@link TokenType} enumeration is used for type distinction.
 * @author 0036502252
 *
 */
public class Token {
	/**
	 * The type of the token. 
	 */
	private TokenType type;
	/**
	 * The value of the token.
	 */
	private Object value;
	
	/**
	 * Creates a new token with the given type and value.
	 * @param type the type of the token
	 * @param value the token's value
	 */
	public Token(TokenType type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	/**
	 * Get the token's value.
	 * @return the value of the token
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Get the type of the token.
	 * @return the token type
	 */
	public TokenType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "(" + this.getType().toString() + ", " + 
			this.getValue().toString() + ")";
	}

}
