package hr.fer.zemris.java.custom.scripting.elems;


/**
 * Base element class. Classes inheriting this class represent basic elements of
 * a parsable source code. They are primarily used in the 
 * {@link hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser} class. 
 * @author 0036502252
 * 
 */
public class Element {
	/**
	 * No implementation offered in this class. Classes overriding this class
	 * should offer their own implemenation.
	 * @return an empty string.
	 */
	public String asText() {
		return "";
	}

	/**
	 * No implementation offered in this class. Classes overriding this class
	 * should offer their own implemenation.
	 * @return a null value
	 */
	public Object getValue() {
		return null;
	}
}
