package hr.fer.zemris.java.custom.scripting.elems;

/**
 * An element representing an integer constant in a parsable source code.
 * @author 0036502252
 *
 */
public class ElementConstantInteger extends Element {
	/**
	 * The value of the element. Immutable. 
	 */
	private int value;
	
	/**
	 * Creates a new ElementConstantInteger with the given value.
	 * @param value the value to be set
	 */
	public ElementConstantInteger(int value) {
		this.value = value;
	}
	
	/**
	 * Gets the value of the element.
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}

