package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Outputs to the user the parameters it obtained, formatted as an HTML table.
 * @author 0036502252
 *
 */
public class EchoParams implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) throws Exception {
		StringBuilder sb = new StringBuilder();
		context.setMimeType("text/html");
		
		sb.append("<html><body><h2>Parameters</h2>");
		sb.append("<table style = \"width:50%\"");
		
		sb.append("<tr><th align=\"left\">Key</th><th align=\"left\">Value</th></tr>");
		
		context.getParameterNames().forEach(name -> {
			sb.append("<tr><td>" + name + "</td><td>" + 
					context.getParameter(name) + "</td></tr>"
			);
		});
		
		sb.append("</table></body></html>");
		
		context.write(sb.toString());
	}
}
