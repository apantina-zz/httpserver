package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;

/**
 * Represents an echo statement in the node hierarchy. 
 * @author 0036502252
 *
 */
public class EchoNode extends Node {
	/**
	 * The elements of an echo statement.
	 */
	Element[] elements;
	
	/**
	 * Constructs a new EchoNode which can contain various elements. 
	 * @param elements the elements of the EchoNode
	 */
	public EchoNode(Element[] elements) {
		super();
		this.elements = elements;
	}
	
	/**
	 * Gets the elements used in the echo node.
	 * @return the element array
	 */
	public Element[] getElements() {
		return elements;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "{$=";
		for(int i = 0, n = elements.length; i < n; i++) {
			s += " ";
			s += elements[i].toString();
		}
		s += "$}";
		return s;
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitEchoNode(this);
	}
	
}
