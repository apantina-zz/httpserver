package hr.fer.zemris.java.custom.scripting.functions;

import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Gets a value from the {@link RequestContext}'s 
 * <code>parameters</code> map, and pushes it onto the stack. If no
 * value is available for the given <code>name</code>, the default value 
 * is pushed.
 * @author 0036502252
 *
 */
public class ParameterGetFunction implements TokenFunction {
	@Override
	public void apply(RequestContext context, Stack<Object> stack) {
		Object defaultValue = stack.pop();
		String name = stack.pop().toString();
		
		Object value = context.getParameter(name.replace("\"",  ""));
		
		stack.push(value == null ? defaultValue : value);
	}	
}
