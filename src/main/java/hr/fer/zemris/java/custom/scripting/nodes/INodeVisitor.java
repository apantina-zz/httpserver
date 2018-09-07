package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * An implementation of the Visitor design pattern with the 
 * {@link Node} class and its subclasses.
 * @author 0036502252
 *
 */
public interface INodeVisitor {
	/**
	 * Called when the visitor encounters a {@link TextNode}.
	 * @param node the encountered node
	 */
	public void visitTextNode(TextNode node);

	/**
	 * Called when the visitor encounters a {@link ForLoopNode}.
	 * @param node the encountered node
	 */
	public void visitForLoopNode(ForLoopNode node);

	/**
	 * Called when the visitor encounters a {@link EchoNode}.
	 * @param node the encountered node
	 */
	public void visitEchoNode(EchoNode node);

	/**
	 * Called when the visitor encounters a {@link DocumentNode}.
	 * @param node the encountered node
	 */
	public void visitDocumentNode(DocumentNode node);
}