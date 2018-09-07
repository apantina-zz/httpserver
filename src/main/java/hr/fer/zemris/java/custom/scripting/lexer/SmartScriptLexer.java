package hr.fer.zemris.java.custom.scripting.lexer;

import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexerException;

/**
 * A simple lexer used to deconstruct a source code into tokens. Implemented as
 * a 'lazy' lexer. It follows a predetermined set of rules to differentiate
 * various types of tokens. It can work in two different states, each with
 * separate rules.
 * 
 * @author 0036502252
 */
public class SmartScriptLexer {
	/**
	 * The input text, stored in a character array.
	 */
	private char[] data;
	/**
	 * The most recently created token.
	 */
	private Token currentToken;
	/**
	 * The currently used index in the character array.
	 */
	private int currentIndex;
	/**
	 * The lexer's state.
	 */
	private LexerState state;

	/**
	 * The initial index of the array index pointer is 0.
	 */
	private final int INITIAL_INDEX = 0;

	/**
	 * Constructs a new lexer with the inputted text.
	 * 
	 * @param text
	 *            the text used for deconstruction
	 */
	public SmartScriptLexer(String text) {
		if (text == null) {
			throw new IllegalArgumentException(
					"Can't initialize a lexer with null!");
		}
		data = text.toCharArray();
		currentIndex = INITIAL_INDEX;
		this.state = LexerState.TEXT;
	}

	/**
	 * Sets the lexer's state.
	 * 
	 * @param state
	 *            the state to be set
	 * @throws SmartScriptLexerException
	 *             if the state is invalid
	 */
	public void setState(LexerState state) {
		if (state == null)
			throw new SmartScriptLexerException("State can't be null!");
		this.state = state;
	}

	/**
	 * Gets the last token created. Does not create a new token.
	 * 
	 * @return the last token created
	 */
	public Token getToken() {
		return this.currentToken;
	}

	/**
	 * Returns the next token in the text given to the lexer by creating a new
	 * one.
	 * 
	 * @return the next token
	 */
	public Token getNextToken() {
		if (state == LexerState.TEXT) {
			textModeTokenGeneration();
		} else {
			tagModeTokenGeneration();
		}
		return currentToken;
	}

	// =========================================================================
	// TAG STATE METHODS
	// =========================================================================

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its tag state. Generates a new token.
	 */
	private void tagModeTokenGeneration() {
		if (currentToken != null && currentToken.getType() == TokenType.EOF) {
			throw new SmartScriptLexerException("No more tokens available.");
		}

		ignoreSpaces();

		if (currentIndex >= data.length) {
			currentToken = new Token(TokenType.EOF, null);
			return;
		} else if (isSymbol(data[currentIndex])) {
			symbolProcedure();
			return;
		} else if (Character.isDigit(data[currentIndex])) {
			numberProcedure();
			return;
		}

		// if it's not a symbol or the EOF, then it has to be a variable type
		// token
		StringBuilder sb = new StringBuilder();

		while (currentIndex < data.length && isVariable(data[currentIndex])) {
			sb.append(data[currentIndex++]);
		}
		currentToken = new Token(TokenType.VARIABLE, sb.toString());
	}

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its tag state. Runs a text processing procedure depending on the symbol
	 * given.
	 */
	private void symbolProcedure() {
		switch (data[currentIndex]) {
		case '@':
			functionProcedure();
			break;
		case '"':
			stringProcedure();
			break;
		case '$':
			EOTProcedure();
			break;
		case '-':
			negativeSignProcedure();
			break;
		default:
			otherSymbolProcedure();
			break;
		}
	}

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its tag state. SOT marks the "start of tag" token which is generated in
	 * order to tell the parser that a tag has been found.
	 */
	private void SOTProcedure() {
		if (isEndOfText()) {
			String s = String.copyValueOf(data).substring(currentIndex,
					currentIndex + 2);
			currentToken = new Token(TokenType.SOT, s);
			currentIndex += 2; // move the pointer after the "{$" sequence and
								// into the tag section
		} else {
			otherSymbolProcedure();
		}
	}

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its tag state. EOT marks the "end of tag" token which is generated in
	 * order to tell the parser that a tag has ended, and the state can be
	 * switched.
	 */
	private void EOTProcedure() {
		if (isEndOfTag()) {
			String s = String.copyValueOf(data).substring(currentIndex,
					currentIndex + 2);
			currentToken = new Token(TokenType.EOT, s);
			currentIndex += 2; // move the pointer after the "$}" sequence and
								// into the text section
		} else {
			otherSymbolProcedure();
		}
	}

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its tag state. It is called when the '@' character is detected, and it
	 * sends a function type token to the parser in order to let it know that a
	 * function needs to be parsed.
	 */
	private void functionProcedure() {
		StringBuilder sb = new StringBuilder();
		sb.append(data[currentIndex++]); // append the '@' sign to the string
		while (Character.isLetter(data[currentIndex])
				|| Character.isDigit(data[currentIndex])
				|| data[currentIndex] == '_') {
			sb.append(data[currentIndex++]);
		}
		currentToken = new Token(TokenType.FUNCTION, sb.toString());
	}

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its tag state. It converts a string into a string type token, while at
	 * the same time checking for proper (and improper) escape sequences.
	 * 
	 * @throws SmartScriptLexerException
	 *             if the escape sequence is invalid, or if the string itself is
	 *             not terminated in the tag.
	 */
	private void stringProcedure() {

		currentIndex++; // skip the first ' " ' character

		StringBuilder sb = new StringBuilder();

		// iterate until the string is terminated
		while (currentIndex < data.length && data[currentIndex] != '"') {
			// treat the ' \" ' sequence as ' " ', and the '\\' sequence as '\'
			// :
			if (data[currentIndex] == '\\') {
				currentIndex++;
				if (data[currentIndex] == '\\' || data[currentIndex] == '"') {
					sb.append(data[currentIndex]);
					currentIndex++;
					continue;
				} else if(data[currentIndex] == 'r'){
					sb.append("\r");
					currentIndex++;
					continue;
				}else if(data[currentIndex] == 'n'){
					sb.append("\n");
					currentIndex++;
					continue;
				}else {
					// the only allowed escape sequences are ' \\ ' and ' \" '
					throw new SmartScriptLexerException(
							"Invalid escape in string variable!");
				}
			}
			sb.append(data[currentIndex++]);
		}
		// check if the string has been properly terminated
		if (currentIndex >= data.length) {
			throw new SmartScriptLexerException("String is not terminated!");
		}

		currentIndex++; // skip the second '"' character
		currentToken = new Token(TokenType.STRING, sb.toString());
	}

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its tag state. When a negative sign character is detected, this method
	 * checks whether it is used as a sign for an integer/double, or simply as a
	 * symbol inside of the tag.
	 */
	private void negativeSignProcedure() {
		// check if the '-' is used as a negative sign for a number or just as a
		// symbol
		if (Character.isDigit(data[currentIndex + 1])) {
			numberProcedure();
		} else {
			otherSymbolProcedure();
		}
	}

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its tag state. When a single symbol is detected, it is sent as a token to
	 * the parser, with the SYMBOL token type.
	 */
	private void otherSymbolProcedure() {
		currentToken = new Token(TokenType.SYMBOL, data[currentIndex]);
		currentIndex++;
	}

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its tag state. When a number is detected, it is parsed as a double or as
	 * an integer, depending if the sequence contains a decimal dot.
	 * 
	 * @throws SmartScriptLexerException
	 *             if the number cannot be parsed
	 */
	private void numberProcedure() {
		StringBuilder sb = new StringBuilder();
		while (Character.isDigit(data[currentIndex])
				|| data[currentIndex] == '.') {
			sb.append(data[currentIndex++]);
		}
		if (sb.toString().contains(".")) {
			try {
				double d = Double.parseDouble(sb.toString());
				currentToken = new Token(TokenType.DOUBLE, d);
			} catch (NumberFormatException ex) {
				throw new SmartScriptLexerException("Can't parse the double!");
			}
		} else {
			try {
				int i = Integer.parseInt(sb.toString());
				currentToken = new Token(TokenType.INT, i);
			} catch (NumberFormatException ex) {
				throw new SmartScriptLexerException("Can't parse the integer!");
			}
		}
	}

	// =========================================================================
	// TEXT STATE METHODS
	// =========================================================================

	/**
	 * Private implementation method. It is used when the lexer is working in
	 * its text state. Generates a new token.
	 */
	private void textModeTokenGeneration() {
		if (currentToken != null && currentToken.getType() == TokenType.EOF) {
			throw new SmartScriptLexerException("No more tokens available.");
		}

		if (currentIndex >= data.length) {
			currentToken = new Token(TokenType.EOF, null);
			return;
		}

		/*
		 * This if-block is executed after the text token was made and sent to
		 * the parser. Now the lexer sends a SOT type token in order to notify
		 * the parser that a tag starts. This way the parser can tell the lexer
		 * to switch its state.
		 */
		if (isEndOfText()) {
			SOTProcedure();
			return;
		}

		StringBuilder sb = new StringBuilder();

		while (currentIndex < data.length) {
			if (isEndOfText()) {
				// reached end of text, make the token and send it to the parser
				makeTextToken(sb);
				return;
			} else if (data[currentIndex] == '\\') {
				escapeSequenceProcedure(sb);
			} else {
				sb.append(data[currentIndex]);
				currentIndex++;
			}
		}
		// this executes if the text is the final part of the code, and no more
		// tags are present
		makeTextToken(sb);
	}

	/**
	 * Utility method. Used for handling special escape sequence conditions
	 * defined by the language rules, while parsing a text section of a
	 * document.
	 * 
	 * @param sb
	 *            the StringBuilder used to append characters in order to create
	 *            a single string.
	 */
	private void escapeSequenceProcedure(StringBuilder sb) {
		while (currentIndex < data.length && data[currentIndex] == '\\') {
			currentIndex++;
			if (currentIndex < data.length && data[currentIndex] == '\\') {
				// outside of tags, '\\' is treated as '\'
				sb.append(data[currentIndex]);
				currentIndex++;
			} else if (currentIndex < data.length
					&& data[currentIndex] == '{') {
				// '{' after the '\' character is simply treated as a '{'
				sb.append(data[currentIndex]);
				currentIndex++;
			} else {
				/*
				 * this exception is thrown if the escape sequence is at the end
				 * of the text, or if a character other than '{' or '\' is found
				 * after a '\' character, which is considered an invalid escape
				 * sequence
				 */

				throw new SmartScriptLexerException(
						"Lexer detected invalid escape sequence!");
			}
		}
	}

	/**
	 * Utility method. Creates a new text token with the given string.
	 * 
	 * @param sb
	 *            the StringBuilder used to append characters in order to create
	 *            a single string
	 */
	private void makeTextToken(StringBuilder sb) {
		String s = sb.toString();
		currentToken = new Token(TokenType.TEXT, s);
	}

	/**
	 * Utility method. Used for skipping blanks in the input text by
	 * incrementing the array index pointer.
	 */
	private void ignoreSpaces() {
		while (currentIndex < data.length) {
			if (Character.isWhitespace(data[currentIndex])) {
				currentIndex++;
				continue;
			}
			break;
		}
	}

	/**
	 * Checks if the lexer has reached the end of the text, and the start of a
	 * tag.
	 * 
	 * @return true if the end of text has been reached
	 */
	private boolean isEndOfText() {
		String str = String.copyValueOf(data).substring(currentIndex);
		return str.startsWith("{$");
	}

	/**
	 * Checks if the lexer has reached the end of the tag, and potentially the
	 * start of a text sequence.
	 * 
	 * @return true if the end of the tag has been reached
	 */
	private boolean isEndOfTag() {
		String str = String.copyValueOf(data).substring(currentIndex);
		return str.startsWith("$}");
	}

	/**
	 * Utility method. Checks if the given character is a symbol, using the
	 * definition of a symbol given in the language definition of the problem
	 * set.
	 * 
	 * @param c
	 *            the character to be checked
	 * @return true if the character is a symbol
	 */
	private boolean isSymbol(char c) {
		return !Character.isLetter(c) && !Character.isDigit(c) && c != '\\'
				&& c != ' '
//				&& c != '\r' && c != '\n'
				;
	}

	/**
	 * Utility method. According to the semantic rules of the parser, if a char
	 * sequence is a sequence of a mix of letters, digits or an underscore, it
	 * is considered a variable
	 * 
	 * @param c
	 *            the character to be checked
	 * @return true if the character can be considered a part of a variable
	 *         sequence
	 */
	private boolean isVariable(char c) {
		return Character.isLetter(c) || Character.isDigit(c) || c == '_';
	}

}
