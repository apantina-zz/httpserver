package hr.fer.zemris.java.custom.scripting.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import hr.fer.zemris.java.webserver.RequestContext;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * Demonstration program. Showcases the usage of the {@link RequestContext} 
 * class by parsing various HTML header files using different charsets.
 * @author 0036502252
 *
 */
public class DemoRequestContext {
	/**
	 * Main method.
	 * @param args unused
	 * @throws IOException if writing to file fails
	 */
	public static void main(String[] args) throws IOException {
		demo1("primjer1.txt", "ISO-8859-2"); 
		demo1("primjer2.txt", "UTF-8"); //same demo, different encodings
		demo2("primjer3.txt", "UTF-8");
	}

	/**
	 * The first demonstration submethod. Outputs a header file to the desired 
	 * path using the specified encoding.
	 * @param filePath the path of the file where the header will be placed
	 * @param encoding the encoding used to generate the file 
	 * @throws IOException if writing to file fails
	 */
	private static void demo1(String filePath, String encoding)
			throws IOException {
		OutputStream os = Files.newOutputStream(Paths.get(filePath));
		RequestContext rc = new RequestContext(os,
				new HashMap<String, String>(), new HashMap<String, String>(),
				new ArrayList<RequestContext.RCCookie>());
		rc.setEncoding(encoding);
		rc.setMimeType("text/plain");
		rc.setStatusCode(205);
		rc.setStatusText("Idemo dalje");
		// Only at this point will header be created and written...
		rc.write("Čevapčići i Šiščevapčići.");
		os.close();
	}
	
	/**
	 * The second demonstration submethod. Outputs a header file to the desired 
	 * path using the specified encoding.
	 * @param filePath the path of the file where the header will be placed
	 * @param encoding the encoding used to generate the file 
	 * @throws IOException if writing to file fails
	 */
	private static void demo2(String filePath, String encoding)
			throws IOException {
		OutputStream os = Files.newOutputStream(Paths.get(filePath));
		RequestContext rc = new RequestContext(os,
				new HashMap<String, String>(), new HashMap<String, String>(),
				new ArrayList<RequestContext.RCCookie>());
		rc.setEncoding(encoding);
		rc.setMimeType("text/plain");
		rc.setStatusCode(205);
		rc.setStatusText("Idemo dalje");
		rc.addRCCookie(
				new RCCookie("korisnik", "perica", 3600, "127.0.0.1", "/"));
		rc.addRCCookie(new RCCookie("zgrada", "B4", null, null, "/"));
		// Only at this point will header be created and
		// written...
		rc.write("Čevapčići i Šiščevapčići.");
		os.close();
	}
	
}