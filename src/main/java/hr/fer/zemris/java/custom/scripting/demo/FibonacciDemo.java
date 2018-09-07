package hr.fer.zemris.java.custom.scripting.demo;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;


/**
 * Demonstrates the use of the {@link SmartScriptEngine} by running a 
 * smart script which outputs several Fibonacci numbers.
 * @author 0036502252
 *
 */
public class FibonacciDemo {
	/**
	 * Main method.
	 * @param args unused
	 */
	public static void main(String[] args) {
		
		Path path = Paths.get("src/main/resources/engine_examples/fibonacci.smscr");
		String documentBody = "";
		try {
			documentBody = new String(Files.readAllBytes(path),
					StandardCharsets.UTF_8);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Map<String,String> parameters = new HashMap<String, String>();
		Map<String,String> persistentParameters = new HashMap<String, String>();
		List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
		// create engine and execute it
		new SmartScriptEngine(
		new SmartScriptParser(documentBody).getDocumentNode(),
		new RequestContext(System.out, parameters, persistentParameters, cookies)
		).execute();
	}
}
