package hr.fer.zemris.java.custom.scripting.nodes;

import hr.zemris.java.custom.collections.ArrayIndexedCollection;

/**
 * The base class for all graph nodes.
 * @author 0036502252
 *
 */
public abstract class Node {
	/**
	 * Utility variable. Used to verify if the add method has already been called.
	 * This is to prevent the unnecessary creation of another collection if 
	 * the add method is never called. 
	 */
	private boolean addMethodAlreadyCalled; //defaults to false
	/**
	 * Collection used for storage of children nodes.
	 */
	ArrayIndexedCollection children;
	
	/**
	 * Adds a child node to this node.
	 * @param child the child node to add
	 */
	public void addChildNode(Node child) {
		//only create the collection when the addChildNode() method is called..
		if(!addMethodAlreadyCalled) {
			this.children = new ArrayIndexedCollection();
			addMethodAlreadyCalled = true;
		}
		
		children.add(child);
	}
	
	/**
	 * Gets the child node at the specified index.
	 * @param index the specified index
	 * @return the child node at the index
	 */
	public Node getChild(int index) {
		if(index < 0 || index > children.size() - 1) {
			throw new IndexOutOfBoundsException("Invalid index! Must be between"
					+ "0 and size-1!");
		}
		
		return (Node) children.get(index);
	}
	
	/**
	 * Gets the amount of children this node has.
	 * @return the number of children of the node
	 */
	public int numberOfChildren() {
		//if the node has no children, its children array has not even been
		//initialized(check the constructor), so it is a null reference by default
		return children == null ? 0 : children.size();
	}
	
	/**
	 * Used for implementing the Visitor design pattern. 
	 * @param visitor the visitor whose visitNode methods will be called
	 */
	public abstract void accept(INodeVisitor visitor);
}
