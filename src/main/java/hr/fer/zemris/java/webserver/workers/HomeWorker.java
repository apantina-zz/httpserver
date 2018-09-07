package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Displays the homepage, with menus where the user can check the 
 * functionalities of various smart scripts, and also change the background
 * color of the page.
 * @author 0036502252
 *
 */
public class HomeWorker implements IWebWorker {

	@Override
	public void processRequest(RequestContext context) throws Exception {
		String param = context.getPersistentParameter("bgcolor");
		
		if(param != null) {
			context.setTemporaryParameter("background", param);
		} else {
			context.setTemporaryParameter("background", "7F7F7F");
		}
		
		context.getDispatcher().dispatchRequest("/private/home.smscr");
	}

	public static void main(String[] args) {
		Path p = Paths.get("home/ardian/Desktop/odabrane-zadace-java/hw12-0036502252/scripts/brojPoziva.smscr");

		System.out.println(Files.exists(p));
	}
}
