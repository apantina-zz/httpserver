package hr.fer.zemris.java.custom.scripting.elems;

/**
 * An element representing a mathematical operator in a parsable source code.
 * @author 0036502252
 *
 */
public class ElementOperator extends Element{
	/**
	 * The value of the element.
	 */
	private String value;
	
	/**
	 * Creates a new ElementOperator with the given value.
	 * @param value the value to be set
	 */
	public ElementOperator(String value) {
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
	 * @return the value 
	 */
	@Override
	public String asText() {
		return value.toString();
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

}
