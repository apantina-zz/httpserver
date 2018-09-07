package hr.fer.zemris.java.custom.scripting.functions;

import java.util.Stack;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Represents a function which can be executed by the {@link SmartScriptEngine}.
 * @author 0036502252
 *
 */
public interface TokenFunction {
	/**
	 * Executes the token function.
	 * @param context the context at the moment of execution
	 * @param stack the stack used for parameter retireval and storage
	 */
	void apply(RequestContext context, Stack<Object> stack);
}