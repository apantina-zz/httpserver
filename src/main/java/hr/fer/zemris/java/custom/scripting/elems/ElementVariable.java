package hr.fer.zemris.java.custom.scripting.elems;
/**
 * An element representing a variable in a parsable source code.
 * @author 0036502252
 *
 */
public class ElementVariable extends Element {
	/**
	 * The name of the element variable. Immutable.
	 */
	private String name;

	/**
	 * Constructs a new ElementVariable with the given name
	 * @param name the name of the element variable
	 */
	public ElementVariable(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name of the element .
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}


}
