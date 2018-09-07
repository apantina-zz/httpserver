package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Greets the lovely user, prints the current time, and the length 
 * of their name if it was provided as a parameter.
 * @author 0036502252
 *
 */
public class HelloWorker implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		
		context.setMimeType("text/html");
		String name = context.getParameter("name");
		
		try {
			context.write("<html><body>");
			context.write("<h1>Helloooooooo!!!</h1>");
			context.write(
					"<p>The current time is: " + sdf.format(now) + "</p>"
			);
			
			if(name == null || name.trim().isEmpty()) {
				context.write("<p>You did not send me your name!</p>");
			} else {
				context.write(
						"<p>Your name has " 
						+ name.trim().length() 
						+ " letters.</p>"
				);
			}
			
			context.write("</body></html>");
			
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
}
