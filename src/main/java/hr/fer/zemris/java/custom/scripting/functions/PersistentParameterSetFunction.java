package hr.fer.zemris.java.custom.scripting.functions;

import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Stores a <code>value</code> in the {@link RequestContext}'s 
 * <code>persistentParameters</code> map.
 * @author 0036502252
 *
 */
public class PersistentParameterSetFunction implements TokenFunction {

	@Override
	public void apply(RequestContext context, Stack<Object> stack) {
		String name = stack.pop().toString();
		String value = stack.pop().toString();
		context.setPersistentParameter(name, value);
	}	
}
