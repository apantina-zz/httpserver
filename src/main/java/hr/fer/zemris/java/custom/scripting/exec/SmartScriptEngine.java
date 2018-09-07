package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.functions.DecimalFormatFunction;
import hr.fer.zemris.java.custom.scripting.functions.DuplicateFunction;
import hr.fer.zemris.java.custom.scripting.functions.ParameterGetFunction;
import hr.fer.zemris.java.custom.scripting.functions.PersistentParameterDeleteFunction;
import hr.fer.zemris.java.custom.scripting.functions.PersistentParameterGetFunction;
import hr.fer.zemris.java.custom.scripting.functions.PersistentParameterSetFunction;
import hr.fer.zemris.java.custom.scripting.functions.SetMimeTypeFunction;
import hr.fer.zemris.java.custom.scripting.functions.SineFunction;
import hr.fer.zemris.java.custom.scripting.functions.SwapFunction;
import hr.fer.zemris.java.custom.scripting.functions.TemporaryParameterGetFunction;
import hr.fer.zemris.java.custom.scripting.functions.TemporaryParameterSetFunction;
import hr.fer.zemris.java.custom.scripting.functions.TokenFunction;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Offers functionalities which can execute a smart script parsed into a 
 * {@link DocumentNode} tree using the {@link SmartScriptParser}. 
 * @author 0036502252
 *
 */
public class SmartScriptEngine {
	/**
	 * The document node which represents the root of the parsed node tree.
	 */
	private DocumentNode documentNode;
	/**
	 * The context representing the page from which the script will be 
	 * executed by this engine.
	 */
	private RequestContext requestContext;
	/**
	 * The underlying stack used for storing variables by their names
	 *  and using them for script command execution.
	 */
	private ObjectMultistack multistack = new ObjectMultistack();



	/**
	 * Maps all supported functions to their names.
	 */
	private Map<String, TokenFunction> functions;
	/**
	 * Temporary stack, used for executing functions.
	 */
	private Stack<Object> tempStack;

	/**
	 * Constructs a new {@link SmartScriptEngine}. 
	 * @param documentNode the parsed tree node which will be executed
	 * @param requestContext the context of the page from which the 
	 * script will be executed
	 */
	public SmartScriptEngine(DocumentNode documentNode,
			RequestContext requestContext) {
		this.documentNode = documentNode;
		this.requestContext = requestContext;
		
		functions = new HashMap<>();
		initFunctions();

	}

	/**
	 * Initializes the supported script functions; adds them to the internal
	 * map.
	 */
	private void initFunctions() {
		functions.put("sin", new SineFunction());
		functions.put("decfmt", new DecimalFormatFunction());
		functions.put("dup", new DuplicateFunction());
		functions.put("paramGet", new ParameterGetFunction());
		functions.put("swap", new SwapFunction());
		functions.put("setMimeType", new SetMimeTypeFunction());
		functions.put("pparamGet", new PersistentParameterGetFunction());
		functions.put("pparamSet", new PersistentParameterSetFunction());
		functions.put("pparamDel", new PersistentParameterDeleteFunction());
		functions.put("tparamGet", new TemporaryParameterGetFunction());
		functions.put("tparamSet", new TemporaryParameterSetFunction());
		functions.put("tparamDel", new TemporaryParameterGetFunction());

	}

	/**
	 * An implementation of the Visitor design pattern, where each node type
	 * is treated in a different way when visited: 
	 * 
	 * <p>DocumentNode - calls <code>accept</code> for all direct children.</p>
	 * 
	 * <p>TextNode - writes the node's text using 
	 * <code>RequestContext</code>'s <code>write</code> method.</p>
	 * 
	 * <p>ForLoopNode - pushes the node's variable on the stack, then 
	 * iterates over all of the child nodes</p>
	 * 
	 * <p>EchoNode - creates a temporary stack used for variable storage.
	 *  Then it performs the corresponding procedure for each specific 
	 *  token found in the <code>EchoNode.</code> </p>
	 * 
	 */
	private INodeVisitor visitor = new INodeVisitor() {

		@Override
		public void visitTextNode(TextNode node) {
			try {
				requestContext.write(node.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			String name = node.getVariable().toString();
			ValueWrapper varWrapper = new ValueWrapper(node.getStartExpression().getValue());

			String end = node.getEndExpression().toString();
			String step = node.getStepExpression().toString() == null ? 
					"0"
					: node.getStepExpression().toString();
			
			multistack.push(name, varWrapper);
			while (multistack.peek(name).numCompare(end) <= 0) {
				for (int i = 0, n = node.numberOfChildren(); i < n; i++) {
					Node child = node.getChild(i);
					child.accept(this);
				}
				multistack.peek(name).add(step);
			}
			multistack.pop(name);
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			tempStack = new Stack<>();
			for (Element element : node.getElements()) {
				
				if (element instanceof ElementConstantInteger
						|| element instanceof ElementConstantDouble
						|| element instanceof ElementString) {
					tempStack.push(element.getValue());
				} else if (element instanceof ElementVariable) {					
					Object value = multistack.peek(((ElementVariable) element)
									.getName()).getValue();
					
					tempStack.push(value);
				} else if (element instanceof ElementOperator) {
					operatorProcedure((ElementOperator) element);
				} else if (element instanceof ElementFunction) {
					functionProcedure((ElementFunction) element);
				}
				
			}
			List<Object> remaining = new ArrayList<>();
			while (!tempStack.isEmpty()) {
				remaining.add(tempStack.pop());
			}

			for (int i = remaining.size() - 1; i >= 0; i--) {
				try {
					requestContext
							.write(remaining.get(i).toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			for (int i = 0, n = node.numberOfChildren(); i < n; i++) {
				Node child = node.getChild(i);
				child.accept(this);
			}
		}
	};

	/**
	 * Called when the engine comes across an Operator token in the EchoNode.
	 * @param element the operator which will be performed
	 */
	private void operatorProcedure(ElementOperator element) {
		ValueWrapper a = new ValueWrapper(tempStack.pop());
		Object b = tempStack.pop();
		switch (element.getValue()) {
		case "*":
			a.multiply(b);
			break;
		case "/":
			a.divide(b);
			break;
		case "+":
			a.add(b);
			break;
		case "-":
			a.subtract(b);
			break;
		default: 
			throw new UnsupportedOperationException("Operator \"" + 
					element.getValue() + "\" is not supported!");
		}
		tempStack.push(a.getValue());
	}

	/**	
	 * Called when the engine comes across a Function token in the EchoNode.
	 * @param element the function which will be performed
	 */
	private void functionProcedure(ElementFunction element) {
		TokenFunction function = functions.get(element.getName());
		if(function != null) {
			function.apply(requestContext, tempStack);
		}
		
	}

	/**
	 * Executes the {@link SmartScriptEngine}'s script.
	 */
	public void execute() {
		documentNode.accept(visitor);
	}
}
