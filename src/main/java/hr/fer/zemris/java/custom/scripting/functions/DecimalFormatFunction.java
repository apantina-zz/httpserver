package hr.fer.zemris.java.custom.scripting.functions;

import java.text.DecimalFormat;
import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Formats the decimal format using the format parameter from the stack.
 * @author 0036502252
 *
 */
public class DecimalFormatFunction implements TokenFunction {

	@Override
	public void apply(RequestContext context, Stack<Object> stack) {

		Object format = stack.pop();
		Object value = stack.pop();
		
		DecimalFormat formatter = new DecimalFormat(format.toString());
		value = formatter
				.format(((Double) value))
				.replace("\"", "")
				.replace(',', '.');

		stack.push(value);
	}
}
