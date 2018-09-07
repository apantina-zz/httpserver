package hr.fer.zemris.java.custom.scripting.functions;

import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Stores a <code>value</code> in the {@link RequestContext}'s 
 * <code>temporaryParameters</code> map.
 * @author 0036502252
 *
 */
public class TemporaryParameterSetFunction implements TokenFunction {
	@Override
	public void apply(RequestContext context, Stack<Object> stack) {
		String name = stack.pop().toString();
		String value = stack.pop().toString();
		context.setTemporaryParameter(name, value);
	}	
}
