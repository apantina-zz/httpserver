package hr.fer.zemris.java.custom.scripting.nodes;

import java.util.Objects;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

/**
 * Represents a for loop node in the source text. 
 * @author 0036502252
 *
 */
public class ForLoopNode extends Node {
	/**
	 * The variable in the for loop. Appears at the beginning of the loop.
	 */
	private ElementVariable variable;
	/**
	 * The starting expression in the for loop.
	 */
	private Element startExpression;
	/**
	 * The ending expression in the for loop.
	 */
	private Element endExpression;
	/**
	 * The step expression in the for loop. Can be a null value.
	 */
	private Element stepExpression;
	
	/**
	 * Constructs a {@link ForLoopNode} with the given variable and expressions.
	 * @param variable the variable to be incremented/used
	 * @param startExpression the starting condition/expression
	 * @param endExpression the ending condition/expression
	 * @param stepExpression the step expression. Can be a null value.
	 */
	public ForLoopNode(ElementVariable variable, Element startExpression,
			Element endExpression, Element stepExpression) {
		super();
		this.variable = Objects.requireNonNull(variable);
		this.startExpression = Objects.requireNonNull(startExpression);
		this.endExpression = Objects.requireNonNull(endExpression);
		this.stepExpression = stepExpression;
	}

	/**
	 * Gets this for loop's variable.
	 * @return the for loop's variable element
	 */
	public ElementVariable getVariable() {
		return variable;
	}
	
	/**
	 * Gets this for loop's start expression.
	 * @return the for loop's start expression element
	 */
	public Element getStartExpression() {
		return startExpression;
	}

	/**
	 * Gets this for loop's end expression.
	 * @return the for loop's end expression element
	 */
	public Element getEndExpression() {
		return endExpression;
	}

	/**
	 * Gets this for loop's step expression.
	 * @return the for loop's step expression element
	 */
	public Element getStepExpression() {
		return stepExpression;
	}
	
	@Override
	public String toString() {
		return this.variable.toString() + " " + this.startExpression.toString()
		 + " " + endExpression.toString() + " " + this.stepExpression.toString();
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitForLoopNode(this);
	
//		for(int i = numberOfChildren() - 1; i >= 0; i--) {
//			getChild(i).accept(visitor);
//		}
	}	
}
