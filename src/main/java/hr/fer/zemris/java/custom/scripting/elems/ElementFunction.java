package hr.fer.zemris.java.custom.scripting.elems;

/**
 * An element representing a function in a parsable source code.
 * @author 0036502252
 *
 */
public class ElementFunction extends Element{
	/**
	 * The value of the element.
	 */
	private String value;
	
	/**
	 * Creates a new ElementFunction with the given value.
	 * @param value the value to be set
	 */
	public ElementFunction(String value) {
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
	 * @return the raw name of the function, without the '@' prefix
	 */
	public String getName() {
		return value.substring(1); //without the @ sign
	}
	
	/**
	 * Returns the string representation of the element.
	 * @return the value 
	 */
	@Override
	public String asText() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
}
