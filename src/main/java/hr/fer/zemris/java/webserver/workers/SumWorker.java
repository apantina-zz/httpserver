package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Sums two numbers and prints the result. 
 * @author 0036502252
 *
 */
public class SumWorker implements IWebWorker {
	/**
	 * Default value of the first parameter: used in case the user's value
	 * cannot be interpreted as an integer.
	 */
	private static final int DEF_A = 1;
	/**
	 * Default value of the second parameter.
	 */
	private static final int DEF_B = 2;
	
	
	@Override
	public void processRequest(RequestContext context) throws Exception {
		String paramA = context.getParameter("a");
		String paramB = context.getParameter("b");
		
		int a = parse(paramA, DEF_A);
		int b = parse(paramB, DEF_B);
		
		context.setTemporaryParameter("zbroj", String.valueOf(a + b));
		context.setTemporaryParameter("a", String.valueOf(a));
		context.setTemporaryParameter("b", String.valueOf(b));

		context.getDispatcher().dispatchRequest("/private/calc.smscr");
	}

	/**
	 * Parses the string representation of the integer. If it cannot be parsed,
	 * returns the default value defined.
	 * @param param the parameter to be parsed
	 * @param defaultValue the default value to be returned
	 * @return the resulting integer, or <code>defaultValue</code>
	 * if <code>param</code> cannot be parsed.
	 */
	private int parse(String param, int defaultValue) {
		if(param == null) return defaultValue;
		int parsed;
		
		try {
			parsed = Integer.parseInt(param);
		} catch(NumberFormatException ex){
			parsed = defaultValue;
		}
		
		return parsed;
	}
}
