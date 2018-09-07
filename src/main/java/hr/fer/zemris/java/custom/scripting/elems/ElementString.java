package hr.fer.zemris.java.custom.scripting.elems;

/**
 * An element representing a string 
 * in a parsable source code.
 * @author 0036502252
 *
 */
public class ElementString extends Element{
	/**
	 * The value of the element.
	 */
	private String value;
	
	/**
	 * Creates a new ElementString with the given value.
	 * @param value the value to be set
	 */
	public ElementString(String value) {
 		this.value = value;
	}
	
	/**
	 * Gets the value of the element.
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Returns the string representation of the element.
	 */
	@Override
	public String asText() {
		return "\"" + value + "\"";
	}
	
	@Override
	public String toString() {
		return asText();
	}
}
