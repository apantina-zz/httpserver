package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.lexer.LexerState;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexer;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexerException;
import hr.fer.zemris.java.custom.scripting.lexer.Token;
import hr.fer.zemris.java.custom.scripting.lexer.TokenType;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.zemris.java.custom.collections.ObjectStack;

/**
 * A script parser which uses a lexer to convert source code into tokens, which
 * are then converted to nodes. The parser can tell the difference between
 * various types of variables, text and functions inside of the text, with the
 * help of its nodes and their hierarchy. It contains a DocumentNode which
 * represents the head node of the node hierarchy, and it is used to reconstruct
 * the deconstructed source code back into its former state, if desired.
 * 
 * @author 0036502252
 *
 */
public class SmartScriptParser {
	/**
	 * Private collection used for utility storage when dealing with tags.
	 */
	private ArrayIndexedCollection array;
	/**
	 * The lexer used for deconstructing source code into tokens.
	 */
	private SmartScriptLexer lexer;
	/**
	 * Private data structure used for manipulating the node hierarchy.
	 */
	private ObjectStack stack;
	/**
	 * The head node of the node hierarchy.
	 */
	private DocumentNode documentNode;

	/**
	 * The minimum amount of variables in a for loop tag.
	 */
	private final int MIN_VARS_FOR_LOOP = 3;
	/**
	 * The maximum amount of variables in a for loop tag.
	 */
	private final int MAX_VARS_FOR_LOOP = 4;

	/**
	 * Constructs a new {@link SmartScriptParser} which deconstructs the given
	 * source code text into nodes.
	 * 
	 * @param text the text to be parsed
	 */
	public SmartScriptParser(String text) {
		this.lexer = new SmartScriptLexer(text);
		this.stack = new ObjectStack();
		this.documentNode = new DocumentNode();
		this.array = new ArrayIndexedCollection();

		this.stack.push(documentNode);

		try {
			parse();
		} catch (SmartScriptLexerException lexerExc) {
			throw new SmartScriptParserException(
					"Lexer encountered an error while tokenizing the source code.", lexerExc);
		}
	}

	/**
	 * Gets the document node used for source code reconstruction.
	 * 
	 * @return the document node
	 */
	public DocumentNode getDocumentNode() {
		return documentNode;
	}

	/**
	 * Parses the given text with help of a dedicated lexer and its tokens. 
	 * @throws SmartScriptParserException in various stages of parsing, through
	 * its submethods, in case of an invalid sequence of tags and/or text.  
	 */
	private void parse() {
		while (true) {
			lexer.getNextToken();
			TokenType type = lexer.getToken().getType();

			if (type == TokenType.EOF) {
				break; // end of file reached, document construction is now over
			} else if (type == TokenType.SOT) {
				lexer.setState(LexerState.TAG);
				tagProcedure();
			} else if (type == TokenType.TEXT) {
				lexer.setState(LexerState.TEXT);
				textProcedure();
			} 
			else {
				throw new SmartScriptParserException("Invalid token sequence!");
				
			}
		}
	}

	/**
	 * Used to parse textual(non-tag) parts of the source text. Converts TEXT
	 * tokens into TextNodes.
	 */
	private void textProcedure() {
		String val = lexer.getToken().getValue().toString();
		TextNode txtNode = new TextNode(val);

		Node topNode = (Node) stack.peek();
		topNode.addChildNode(txtNode);
	}

	/**
	 * Used to check if the parser has encountered an empty, or a non-empty tag.
	 * 
	 * @throws SmartScriptParserException if the tag sequence is invalid. At 
	 * this stage, a tag is sure to be invalid if it does not start with a 
	 * variable or symbol type token.
	 */
	private void tagProcedure() {
		Token token = lexer.getNextToken();

		// check what the first type of token is inside of the tag
		if (token.getType() == TokenType.VARIABLE) {
			nonEmptyTagProcedure();
		} else if (token.getType() == TokenType.SYMBOL
				&& (char) token.getValue() == '=') {
			emptyTagProcedure();
		} else {
			throw new SmartScriptParserException("Invalid tag sequence!");
		}
	}

	/**
	 * Used when the parser encounters an empty tag. An empty tag starts with an
	 * '=' sign.
	 * 
	 * @throws SmartScriptParserException
	 *             if the tag sequence is invalid inside of the empty tag.
	 */
	private void emptyTagProcedure() {
		array.clear();
		while (true) {
			lexer.getNextToken();
			if (lexer.getToken().getType() == TokenType.EOT) {
				lexer.setState(LexerState.TEXT);
				break;
			} else if (lexer.getToken().getType() == TokenType.EOF) {
				// an EOF happened before the tag was even closed
				throw new SmartScriptParserException("Invalid tag sequence!");
			} else {
				array.add(lexer.getToken());
			}
		}
		Element[] elements = new Element[array.size()];
		for (int i = 0; i < array.size(); i++) {
			Token currToken = (Token) array.get(i);
			elements[i] = createElement(currToken);
		}

		EchoNode echoNode = new EchoNode(elements);
		Node topNode = (Node) stack.peek();
		topNode.addChildNode(echoNode);
	}

	/**
	 * Used when the parser encounters a closed type tag. Calls other methods
	 * depending on whether it finds the start or the end of a closed tag
	 * sequence.
	 * 
	 * @throws SmartScriptParserException
	 *             if there is an inappropriate number of END tags, or if the
	 *             tag doesn't start with a supported tag name.
	 */
	private void nonEmptyTagProcedure() {
		/*
		 * check which is the first token in the tag, and call methods or
		 * exceptions accordingly
		 */
		String str = lexer.getToken().getValue().toString();
		if (str.toUpperCase().equals("FOR")) {
			parseForLoop();
		} else if (str.toUpperCase().equals("END")) {
			lexer.getNextToken(); //let the lexer continue to the EOT token
			lexer.setState(LexerState.TEXT);
			stack.pop();
			if (stack.isEmpty()) {
				throw new SmartScriptParserException(
						"There are more END tags than there are opened non-empty tags!");
			}

		} else {
			throw new SmartScriptParserException("Invalid tag start!");
		}
	}

	/**
	 * Used to parse a for-loop tag.
	 * 
	 * @throws SmartScriptParserException
	 *             if the tag sequence is invalid, or if there is an invalid
	 *             amount of arguments in the for loop.
	 */
	private void parseForLoop() {
		array.clear();
		while (true) {
			lexer.getNextToken();
			if (lexer.getToken().getType() == TokenType.EOT) {
				lexer.setState(LexerState.TEXT);
				break;
			} else if (lexer.getToken().getType() == TokenType.EOF) {
				// an EOF happened before the tag was even closed
				throw new SmartScriptParserException("Invalid tag sequence!");
			} else {
				array.add(lexer.getToken());
			}
		}
		ForLoopNode forNode = null;

		if (array.size() == MIN_VARS_FOR_LOOP) {
			forNode = initElements3Variables();
		} else if (array.size() == MAX_VARS_FOR_LOOP) {
			forNode = initElements4Variables();
		} else {
			throw new SmartScriptLexerException(
					"Invalid amount of arguments in a FOR loop!");
		}

		Node topNode = (Node) stack.peek();
		topNode.addChildNode(forNode);
		stack.push(forNode);
	}

	/**
	 * Used in the parseForLoop() method. Creates a new ForLoopNode using the
	 * tokens retrieved from the lexer.
	 * <p>
	 * NOTE: This method works with a for loop which contains 3 variables.
	 * 
	 * @return the newly created ForLoopNode
	 * @throws SmartScriptParserException
	 *             if the required order of variables in the for loop is not
	 *             respected.
	 */
	private ForLoopNode initElements3Variables() {
		Element start = null, end = null;
		ElementVariable variable = null;

		for (int i = 0; i < MIN_VARS_FOR_LOOP; i++) {
			Token currToken = (Token) array.get(i);
			TokenType currType = currToken.getType();
			switch (i) {
			case 0:
				if (currType != TokenType.VARIABLE) {
					throw new SmartScriptLexerException(
							"First argument of FOR must be a variable!");
				}
				variable = (ElementVariable) createElement(currToken);
				break;
			case 1:
				if (!isValidForParameter(currType)) {
					throw new SmartScriptLexerException(
							"Invalid FOR parameter!");
				}
				start = createElement(currToken);
				break;
			case 2:
				if (!isValidForParameter(currType)) {
					throw new SmartScriptLexerException(
							"Invalid FOR parameter!");
				}
				end = createElement(currToken);
				break;
			}
		}
		return new ForLoopNode(variable, start, end, null);
	}

	/**
	 * Utility method used in the parseForLoop() method. Creates a new
	 * ForLoopNode using the tokens retrieved from the lexer.
	 * <p>
	 * NOTE: This method works with a for loop which contains 4 variables.
	 * 
	 * @return the newly created ForLoopNode
	 * @throws SmartScriptParserException
	 *             if the required order of variables in the for loop is not
	 *             respected.
	 */
	private ForLoopNode initElements4Variables() {
		Element start = null, end = null, step = null;
		ElementVariable variable = null;

		for (int i = 0; i < MAX_VARS_FOR_LOOP; i++) {
			Token currToken = (Token) array.get(i);
			TokenType currType = currToken.getType();
			switch (i) {
			case 0:
				if (currType != TokenType.VARIABLE) {
					throw new SmartScriptLexerException(
							"First argument of FOR must be a variable!");
				}
				variable = (ElementVariable) createElement(currToken);
				break;
			case 1:
				if (!isValidForParameter(currType)) {
					throw new SmartScriptLexerException(
							"Invalid FOR parameter!");
				}
				start = createElement(currToken);
				break;
			case 2:
				if (!isValidForParameter(currType)) {
					throw new SmartScriptLexerException(
							"Invalid FOR parameter!");
				}
				end = createElement(currToken);
				break;

			case 3:
				if (!isValidForParameter(currType)) {
					throw new SmartScriptLexerException(
							"Invalid FOR parameter!");
				}
				step = createElement(currToken);
				break;
			}
		}
		return new ForLoopNode(variable, start, end, step);
	}

	/**
	 * Creates an element of a specified type using the given token.
	 * 
	 * @param token
	 *            the token used to create the element
	 * @return the new Element
	 */
	private Element createElement(Token token) {
		TokenType type = token.getType();
		switch (type) {
		case STRING:
			return new ElementString(token.getValue().toString());
		case VARIABLE:
			return new ElementVariable(token.getValue().toString());
		case FUNCTION:
			return new ElementFunction(token.getValue().toString());
		case INT:
			int value = (int) token.getValue();
			return new ElementConstantInteger(value);
		case DOUBLE:
			double doubleValue = (double) token.getValue();
			return new ElementConstantDouble(doubleValue);
		case SYMBOL:
			return new ElementOperator(token.getValue().toString());
		default:
			return null;
		}
	}

	/**
	 * Checks if the token can be used in a for loop, according to the token
	 * type.
	 * 
	 * @param type
	 *            the token type to check
	 * @return true if the token can be used as a for loop variable/element
	 */
	private boolean isValidForParameter(TokenType type) {
		return type == TokenType.INT || type == TokenType.DOUBLE
				|| type == TokenType.STRING || type == TokenType.VARIABLE;
	}
}
