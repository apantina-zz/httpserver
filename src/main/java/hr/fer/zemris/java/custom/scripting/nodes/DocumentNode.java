package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * The top most node in the node hierarchy when working with the {@link SmartScriptParser}.
 * @author 0036502252
 *
 */
public class DocumentNode extends Node {
	/**
	 * Default constructor.
	 */
	public DocumentNode() {
		super();
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitDocumentNode(this);
	}
}
