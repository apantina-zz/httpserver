package hr.fer.zemris.java.custom.scripting.functions;

import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Replaces the order of the topmost two items on the stack. 
 * @author 0036502252
 *
 */
public class SwapFunction implements TokenFunction {

	@Override
	public void apply(RequestContext context, Stack<Object> stack) {
		Object a = stack.pop();
		Object b = stack.pop();

		stack.push(a);
		stack.push(b);
	}
}
