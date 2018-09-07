package hr.fer.zemris.java.custom.scripting.functions;

import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Sets the {@link RequestContext}'s mime type.
 * @author 0036502252
 *
 */
public class SetMimeTypeFunction implements TokenFunction {
	
	@Override
	public void apply(RequestContext context, Stack<Object> stack) {
		context.setMimeType(stack.pop().toString());
	}
}
