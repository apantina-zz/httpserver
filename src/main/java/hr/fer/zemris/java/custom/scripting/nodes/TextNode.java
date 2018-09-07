package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * This node represents the text part of a source text file in the Node 
 * hierarchy.
 * @author 0036502252
 *
 */
public class TextNode extends Node {
	/**
	 * The node's text.
	 */
	private String text;
	
	/**
	 * Constructs a new TextNode with the given text.
	 * @param text the TextNode's text
	 */
	public TextNode(String text) {
		super();
		this.text = text;
	}
	/**
	 * Gets the text of this node.
	 * @return the node's text
	 */
	public String getText() {
		return text;
	}
	@Override
	public String toString() {
		String s = this.text.replace("\\", "\\\\");
		s = s.replace("\\r", "\r").replace("\\n", "\n");
		return s;
	}
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitTextNode(this);
	}
}
