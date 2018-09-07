package hr.fer.zemris.java.custom.scripting.functions;

import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Calculates the sine of the given argument and stores the result on the stack.
 * @author 0036502252
 *
 */
public class SineFunction implements TokenFunction {

	@Override
	public void apply(RequestContext context, Stack<Object> stack) {
		
		Double value = Double.parseDouble(stack.pop().toString());
		stack.push(Double.valueOf(Math.sin(Math.toRadians(value))));
	}
}
