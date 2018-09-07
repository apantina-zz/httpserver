package hr.fer.zemris.java.custom.scripting.functions;

import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Deletes a parameter mapped to the name from the {@link RequestContext}'s 
 * <code>temporaryParameters</code> map.
 * @author 0036502252
 *
 */
public class TemporaryParameterDeleteFunction implements TokenFunction {
	@Override
	public void apply(RequestContext context, Stack<Object> stack) {
		String name = stack.pop().toString();
		context.removeTemporaryParameter(name);
	}	
}
