package hr.fer.zemris.java.custom.scripting.functions;

import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Duplicates the topmost value from the stack, by popping it and pushing it 
 * twice. Fun stuff.
 * @author 0036502252
 *
 */
public class DuplicateFunction implements TokenFunction {

	@Override
	public void apply(RequestContext context, Stack<Object> stack) {

		Object val = stack.pop();
		stack.push(val);
		stack.push(val);
	}
}
