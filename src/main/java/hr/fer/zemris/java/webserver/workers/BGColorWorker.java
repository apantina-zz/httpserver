package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Sets the background color of the homepage.
 * @author 0036502252
 *
 */
public class BGColorWorker implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) throws Exception {
		String param = context.getParameter("bgcolor");
		if(isValidParam(param)) {
			context.setPersistentParameter("bgcolor", param);
			context.write(
					"<html><body> Color is updated! "
					+ " <a href=\"/index2.html\">home</a> </body></html>"
			);

		} else {
			context.write("<html><body> Color is not updated! "
			+ "<a href=\"/index2.html\">home</a> </body></html>");
		}
	}
	/**
	 * Checks if the parameter is a valid hex string that can be used in
	 * a hex color encoding. Must be 6 characters long.
	 * @param param the parameter to be checked
	 * @return true if valid, false otherwise
	 */
	private boolean isValidParam(String param) {
		if(param.length() != 6) return false;
		
		for(char c : param.toLowerCase().toCharArray()) {
			if(!(c >= 'a' && c <= 'f') && !(c >= '0' && c <= '9')) return false;
		}
		return true;
	}
	
	
}


