package hr.fer.zemris.java.custom.scripting.elems;

/**
 * An element representing a double constant in a parsable source code.
 * @author 0036502252
 *
 */
public class ElementConstantDouble extends Element {
	/**
	 * The value of the element. Immutable. 
	 */
	private double value;
	
	/**
	 * Creates a new ElementConstantDouble with the given value.
	 * @param value the value to be set
	 */
	public ElementConstantDouble(double value) {
		this.value = value;
	}
	
	/**
	 * Gets the value of the element.
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

}

