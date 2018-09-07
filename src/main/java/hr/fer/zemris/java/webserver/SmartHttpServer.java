package hr.fer.zemris.java.webserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * A custom implementation of a multithreaded HTTP/TCP server. 
 * Has added support for script execution. Fully supports cookies,
 * multiple concurrent users and can display HTML documents, pictures,
 * and has a landing page from which all functionalities are displayed.
 * @author 0036502252
 *
 */
public class SmartHttpServer {
	/**
	 * The address of the server.
	 */
	private String address;
	/**
	 * The server's domain name.
	 */
	private String domainName;
	/**
	 * The server's application port.
	 */
	private int port;
	/**
	 * The number of worker threads in the thread pool.
	 */
	private int workerThreads;
	/**
	 * Time during which cookies are stored. 
	 * After <code>sessionTimeout</code> seconds pass, cookies are deleted.
	 */
	private int sessionTimeout;
	/**
	 * Maps mime types to their names.
	 */
	private Map<String, String> mimeTypes;
	/**
	 * A server thread in the server's thread pool.
	 */
	private ServerThread serverThread;
	/**
	 * The server's thread pool.
	 */
	private ExecutorService threadPool;
	/**
	 * The server's document root from which all configuration and media 
	 * files are obtained.
	 */
	private Path documentRoot;
	/**
	 * The server's properties, parsed from configuration files.
	 */
	private Properties properties;
	/**
	 * Collection which maps {@link IWebWorker}s to their names.
	 */
	private Map<String, IWebWorker> workersMap;
	
	/**
	 * Collection which maps {@link SessionMapEntry} objects to their IDs.
	 */
	private Map<String, SessionMapEntry> sessions = new HashMap<>();
	/**
	 * Random number generator used for generating session IDs.
	 */
	private Random randomSession = new Random();
	/**
	 * Indicates whether the server is running.
	 */
	private boolean running;
	
	/**
	 * Number of miliseconds in 5 minutes. Used by the server's daemon 
	 * session cleaner thread.
	 */
	private static final int MILIS_IN_5_MINS = 300_000;
	/**
	 * Path of /ext/ worker scripts.
	 */
	private static final String EXT_PATH = "hr.fer.zemris.java.webserver.workers.";
	/**
	 * Default length of a randomly generated session ID.
	 */
	public static final int SID_LENGTH = 20;
	
	/**
	 * Constructs a new {@link SmartHttpServer}.
	 * @param configFilePath the path to the server's configuration file.
	 */
	public SmartHttpServer(String configFilePath) {
		serverThread = new ServerThread();
		properties = new Properties();
		try {
			Reader reader = Files.newBufferedReader(Paths.get(configFilePath));
			properties.load(reader);
			
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
		assignProperties();
		assignMimeTypes();
		startCleaner();
	}
	
	/**
	 * Initializes and starts the server's cleaner thread. This thread will 
	 * clear expired cookie objects every 5 minutes.
	 */
	private void startCleaner() {
		Thread cleanerThread = new Thread(()-> {
			while(true) {
				try {
					Thread.sleep(MILIS_IN_5_MINS);
				} catch (InterruptedException ignorable) {
				}
				
				synchronized(sessions) {
					sessions.entrySet().removeIf(entry->
						entry.getValue().validUntil < currentTimeSeconds()
					);
				}
			}
		});
		cleanerThread.setDaemon(true);
		cleanerThread.start();
	}

	/**
	 * @return the current time, in seconds.
	 * @see System#currentTimeMillis()
	 */
	private long currentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * Represents a single server session, which is used for implementing 
	 * cookie functionality to the server.
	 * @author 0036502252
	 *
	 */
	private static class SessionMapEntry{
		/**
		 * Unique session ID.
		 */
		String sid;
		/**
		 * The host name of this session.
		 */
		String host;
		/**
		 * Time until this session is valid, in seconds. This session's
		 * cookies will be erased after this time (once the cleaner thread 
		 * starts).
		 */
		long validUntil;
		/**
		 * Stores the session's parameters.
		 */
		Map<String, String> map;
		
		/**
		 * Constructs a new {@link SessionMapEntry}.
		 * @param sid the session ID
		 * @param host the host name
		 * @param validUntil time until this session will be valid (in seconds)
		 */
		public SessionMapEntry(String sid, String host, long validUntil) {
			this.sid = sid;
			this.host = host;
			this.validUntil = validUntil;
			this.map = new ConcurrentHashMap<>();
		}
	}
	
	/**
	 * Gets mime types from the server's mime.properties file, and 
	 * puts them in the server's internal map. 
	 */
	private void assignMimeTypes() {
		
		try {
			Reader reader = Files.newBufferedReader(
					Paths.get("./config/mime.properties")
			);
			
			Properties mimeProperties = new Properties();
			mimeProperties.load(reader);
			
			mimeTypes = new HashMap<>();
			for(String name : mimeProperties.stringPropertyNames()) {
				mimeTypes.put(name, mimeProperties.getProperty(name));
			}
			
		} catch(IOException ex) {
			System.out.println(
					"Error reading properties file: " + ex.getMessage()
			);
		}
	}
	
	/**
	 * Gets mime types from the server's server.properties file, and 
	 * puts them in the server's internal map. 
	 */
	private void assignProperties() {
		address = properties.getProperty("server.address");
		domainName = properties.getProperty("server.domainName");
		port = Integer.parseInt((String) properties.get("server.port"));
		workerThreads = Integer.parseInt(
				properties.getProperty("server.workerThreads")
		);		
		sessionTimeout = Integer.parseInt(
				properties.getProperty("session.timeout")
		);
		documentRoot = Paths.get(properties.getProperty("server.documentRoot"));
		parseWorkers(Paths.get(properties.getProperty("server.workers")));
	}

	/**
	 * Gets workers from the given config file, and maps their names
	 * to their paths using the server's internal map.
	 * @param path the path to the config file
	 */
	private void parseWorkers(Path path) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		workersMap = new HashMap<>();
		for(String line : lines) {
			if(line.startsWith("#") || line.isEmpty()) continue;
			String[] params = line.split("=");
			workersMap.put(params[0].trim(), getWorker(params[1].trim()));
		}
	}

	/**
	 * Instantiates a new {@link IWebWorker} using the path to the 
	 * class.
	 * @param path the path to the worker's class file
	 * @return the instantiated {@link IWebWorker}
	 */
	private IWebWorker getWorker(String path) {
		Class<?> referenceToClass;
		IWebWorker iww = null;
		try {
			referenceToClass = this.getClass().getClassLoader().loadClass(path);
			Object newObject = referenceToClass.getDeclaredConstructor().newInstance();
			iww = (IWebWorker) newObject;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		
		return iww;
	}

	/**
	 * Starts the server's thread pool.
	 */
	protected synchronized void start() {		
		if(!serverThread.isAlive()) {
			serverThread.start();
			running = true;
			threadPool = Executors.newFixedThreadPool(workerThreads);
		}
	}

	/**
	 * Stops the server's thread pool.
	 */
	protected synchronized void stop() {
		if(serverThread.isAlive()) {
			serverThread.interrupt();
			running = false;
			threadPool.shutdown();
		}
	}

	/**
	 * The server's main thread which binds the socket to the port, 
	 * and handles outside requests.
	 * @author 0036502252
	 *
	 */
	protected class ServerThread extends Thread {
		@Override
		public void run() {
			ServerSocket servSocket;
			try {
				servSocket = new ServerSocket();
				servSocket.bind(new InetSocketAddress((InetAddress)null, port));
				
				while(running) {
					Socket client = servSocket.accept();
					ClientWorker cw = new ClientWorker(client);
					threadPool.submit(cw);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Represents an individual request sent to the server which is to be
	 * executed.
	 * @author 0036502252
	 *
	 */
	private class ClientWorker implements Runnable, IDispatcher {
		/**
		 * One kilobyte. Used in byte buffers.
		 */
		private static final int ONE_KB = 1024;
		/**
		 * The client's socket.
		 */
		private Socket csocket;
		/**
		 * The client socket's input stream.
		 */
		private InputStream istream;
		/**
		 * The client socket's output stream.
		 */
		private OutputStream ostream;
		/**
		 * The HTTP version.
		 */
		private String version;
		/**
		 * The method of the client request.
		 */
		private String method;
		/**
		 * The host name.
		 */
		private String host;
		/**
		 * The parameters, used for script execution.
		 */
		private Map<String, String> params = new HashMap<String, String>();
		/**
		 * The temporary parameters, used for script execution.
		 */
		private Map<String, String> tempParams = new HashMap<>();
		/**
		 * Persistent parameters, used for cookie realization.
		 */
		private Map<String, String> permParams = new HashMap<>();
		/**
		 * The server's cookies.
		 */
		private List<RCCookie> outputCookies = new ArrayList<>();
		/**
		 * The session's ID.
		 */
		private String SID;
		/**
		 * The session request's context.
		 */
		private RequestContext context = null;
		
		/**
		 * Constructs a new {@link ClientWorker} using the request's socket.
		 * @param csocket the client request's socket.
		 */
		public ClientWorker(Socket csocket) {
			super();
			this.csocket = csocket;
		}
		
		@Override
		public void dispatchRequest(String urlPath) throws Exception {	
			internalDispatchRequest(urlPath, false);
		}

		@Override
		public void run() {
			try {
				istream = new PushbackInputStream(csocket.getInputStream());
				ostream = csocket.getOutputStream();
				
				//get request, store the lines in a list
				byte[] bytes = getBytesFromRequest(istream);
				
				String requestString = new String(
						bytes, 
						StandardCharsets.US_ASCII
				);
			
				List<String> request = readRequest(requestString);
				
				//check if the request is valid
				if(request.size() == 0) {
					sendError(ostream, 400, "Bad request");
					close();
					return;
				}
				
				String[] firstLineSplit = request.get(0).split("\\s+");

				if(firstLineSplit.length != 3) {
					sendError(ostream, 400, "Bad request");
					close();
					return;
				}

				method = firstLineSplit[0];
				String requestedPath = firstLineSplit[1];
				version = firstLineSplit[2];
				
				if(!method.toUpperCase().equals("GET")) {
					sendError(ostream, 400, "Bad request");
					close();
					return;
				}
				
				if(!version.toUpperCase().equals("HTTP/1.0") 
						&& !version.toUpperCase().equals("HTTP/1.1")){
					sendError(ostream, 400, "Bad request");
					close();
					return;
				}
												
				String[] requestedPathParams = requestedPath.split("\\?", 2);
				String paramString = "";
				address = requestedPathParams[0];
				
				//get the path parameters, if they exist
				if(requestedPathParams.length > 1) {
					paramString = requestedPathParams[1];
					parseParameters(paramString);
				}	
				
				setHost(request);
				checkSession(request);
				
				//dispatch the request further
				internalDispatchRequest(address, true);
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close();
			}
		}
		
		/**
		 * Generates a list of strings from the parsed request string. 
		 * @param requestString the request
		 * @return list of strings
		 * @throws IOException
		 */
		private List<String> readRequest(String requestString) throws IOException {
			List<String> lines = new ArrayList<>();
			String current = null;
			
			for(String str : requestString.split("\n")) {
				if(str.isEmpty()) break;
				char c = str.charAt(0);
				if(c == 9 || c == 32) {
					current += str;
				} else {
					if(current != null) {
						lines.add(current);
					}
					current = str;
				}
			}
			
			if(!current.isEmpty()) {
				lines.add(current);
			}
			
			return lines;
		}
		
		
		/**
		 * Checks the client request's session. Used for successful cookie
		 * realization. 
		 * @param request the client's request
		 */
		private void checkSession(List<String> request) {
			String sidCandidate = null;

			for(String line : request) {
				if(!line.trim().startsWith("Cookie:")) continue;
				
				String[] cookies = line.substring("Cookie: ".length()).split("; ");
				for(String cookie : cookies) {
					if(cookie.startsWith("sid")) {
						sidCandidate = cookie.trim().split("=")[1].replace("\"", "");
						break;
					}
				}
			}
			
			//the sessions map is not thread-safe, so we synchronize it
			synchronized(sessions) {
				SessionMapEntry entry;
				if(sidCandidate == null || !sessions.containsKey(sidCandidate)) {
					createNewEntry();
				} else {
					entry = sessions.get(sidCandidate);
					if(!entry.host.equals(host)) {
						createNewEntry();
					} else if(entry.validUntil < currentTimeSeconds()){
						sessions.remove(sidCandidate);
						createNewEntry();
					} else {
						entry.validUntil = sessionTimeout + currentTimeSeconds();
						permParams = entry.map;
					}
				}
			}
		}

		/**
		 * Creates a new session model.
		 */
		private void createNewEntry() {
			String sidCandidate = generateSID();

			SessionMapEntry newEntry = new SessionMapEntry(
					sidCandidate,
					host,
					currentTimeSeconds() + sessionTimeout
			);
			
			sessions.put(sidCandidate, newEntry);
			outputCookies.add(
					new RCCookie("sid", sidCandidate, null, host, "/")
			);
			permParams = newEntry.map;
			SID = sidCandidate;
		}


		/**
		 * Generates a new session ID.
		 * @return a new randomly-generated SID, which consists of 20 
		 * random uppercase letters
		 */
		private String generateSID() {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < SID_LENGTH; i++) {
				char randomChar = (char) (randomSession.nextInt('Z' - 'A' + 1) + 'A');
			 	sb.append(randomChar);
			}
			return sb.toString();
		}

		/**
		 * Gets the URL's parameters, parses them, and stores them in the 
		 * server's internal parameter map.
		 * @param paramString the URL's parameters
		 */
		private void parseParameters(String paramString) {
			String[] params = paramString.split("&");
			for(String str : params) {
				String[] toMap = str.split("=");
				if(toMap.length == 0) {
					throw new IllegalArgumentException("Invalid parameters!");
				}
				String key = toMap[0];
				String value;
				if(toMap.length == 1) {
					value = null;
				} else {
					value = toMap[1];
				}
				
				this.params.put(key, value);
			}
		}

		/**
		 * Sets the server's host by parsing the client's request
		 * @param request list of strings representing the client's request
		 */
		private void setHost(List<String> request) {
			String hostName = getHost(request);
			this.host = hostName == null ? 
					properties.getProperty("server.domainName") :
					hostName.trim();
		}

		/**
		 * Gets the server's host by parsing the client's request
		 * @param request list of strings representing the client's request
		 * @return the host name, or null if it isn't found in request
		 */
		private String getHost(List<String> request) {
			for(String line : request) {
				line = line.trim();
				if(line.toLowerCase().startsWith("host:")) {
					String hostName = line.substring(
							"host:".length()
					).trim();
					int pos = hostName.indexOf(':');
					if(pos != -1) {
						hostName = hostName.substring(0, pos).trim();
					}
					return hostName;
				}
			}
			return null;
		}

	
		/**
		 * Used for realizing the MVC(Model-View-Controller) design pattern. 
		 * Essentially, it is used to separate the processing part
		 * from the rendering part of the request. 
		 * 
		 * @param urlPath the request's URL
		 * @param directCall true if this method is directly called. Used to 
		 * prevent the user from directly calling the dispatch request for 
		 * files that are in the /private directory.
		 * @throws Exception
		 */
		public void internalDispatchRequest(String urlPath, boolean directCall)
				throws Exception {
			
			if(context == null) {
				context = new RequestContext(
						ostream, params, permParams, 
						outputCookies, this, new HashMap<>()
				);
			}
			
			if(directCall && (urlPath.startsWith("/private/") || urlPath.equals("/private"))) {
				sendError(ostream, 404, "Not found");
				close();
				return;
			}
			
			if(urlPath.contains("/ext/")) {
				String path = EXT_PATH + urlPath.substring("/ext/".length()).trim();
				getWorker(path).processRequest(context);
				close();
				return;
			}
			
			//workersMap is not inherently thread-safe, so it is synchronized
			synchronized (workersMap) {	
				if(workersMap.containsKey(urlPath.trim())) {
					workersMap.get(urlPath).processRequest(context);
					close();
					return;
				}
			}	
			
			Path resolvedPath = documentRoot.resolve(Paths.get(urlPath.substring(1)));
			
			if(!resolvedPath.startsWith(documentRoot.normalize())) {
				sendError(ostream, 403, "Forbidden");
				close();
				return;
			}
			
			if(!Files.isReadable(resolvedPath)
					|| !Files.isRegularFile(resolvedPath)) {
				sendError(ostream, 404, "Not found");
				close();
				return;
			}
			
			String fileName = resolvedPath.getFileName().toString();
			String extension = fileName.substring(fileName.indexOf(".") + 1);
			
			String mimeType = mimeTypes.get(extension);
			
			mimeType = mimeType == null ? "application/octet-stream" : mimeType;
			
			context.setMimeType(mimeType);
			context.setStatusCode(200);
			
			if(extension.equals("smscr")) {
				executeScript(resolvedPath);
			} else {
				context.setContentLength(Files.size(resolvedPath));
				writeToOutputStream(resolvedPath);
			}
			close();
		}
			
		
		/**
		 * Writes a file to the context's output stream.
		 * @param path the path from which the file is retrieved
		 * @throws IOException if an I/O error occurs
		 */
		private void writeToOutputStream(Path path) throws IOException {

			InputStream stream = new BufferedInputStream(
					Files.newInputStream(path)
			);
			
			byte[] buf = new byte[ONE_KB];
			while (true) {
				int r = stream.read(buf);
				if (r < 1) break;
				context.write(buf, 0, r);
			}			
		}

		/**
		 * Executes a script located at {@code path}. 
		 * @param path location of the .smscr smart script file
		 */
		private void executeScript(Path path) {
			String documentBody = "";
			try {
				documentBody = new String(Files.readAllBytes(path),
						StandardCharsets.UTF_8);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
						
			new SmartScriptEngine(
					new SmartScriptParser(documentBody).getDocumentNode(), 
					context
			).execute();
			
		}

		/**
		 * Closes the socket and flushes its output stream.
		 */
		private void close() {
			if(csocket.isClosed()) return;
			
			/* notice how the calls are wrapped each in their own try-catch:
			 * this is to prevent the possibility of the stream being 
			 * flushed, but not closed (which could happen if they are
			 * in the same try-catch block..)
			 */
			try {
				ostream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				csocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Gets bytes from an HTTP request header.
	 * @param is the input stream from which bytes are read
	 * @return the header as a byte array
	 * @throws IOException if I/O error occurs
	 */
	private static byte[] getBytesFromRequest(InputStream is) 
			throws IOException {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int state = 0;
	l:		while(true) {
				int b = is.read();
				if(b==-1) return null;
				if(b!=13) {
					bos.write(b);
				}
				switch(state) {
				case 0: 
					if(b==13) { state=1; } else if(b==10) state=4;
					break;
				case 1: 
					if(b==10) { state=2; } else state=0;
					break;
				case 2: 
					if(b==13) { state=3; } else state=0;
					break;
				case 3: 
					if(b==10) { break l; } else state=0;
					break;
				case 4: 
					if(b==10) { break l; } else state=0;
					break;
				}
			}
			return bos.toByteArray();
		}

	
	/**
	 * Utility method which sends an HTTP response without a body.
	 * @param ostream the output stream on which the response will be sent
	 * @param statusCode the status code of the response
	 * @param statusText the status text of the response
	 * @throws IOException if an I/O error occurs
	 */
	private static void sendError(OutputStream ostream, 
			int statusCode, String statusText) throws IOException {

			ostream.write(
				("HTTP/1.1 "+statusCode+" "+statusText+"\r\n"+
				"Server: SmartHTTPServer\r\n"+
				"Content-Type: text/plain;charset=UTF-8\r\n"+
				"Content-Length: 0\r\n"+
				"Connection: close\r\n"+
				"\r\n").getBytes(StandardCharsets.US_ASCII)
			);
			ostream.flush();
		}
	/**
	 * Main method. Instantiates and starts the server.
	 * @param args path to the server's config file
	 */
	public static void main(String[] args) {
		if(args.length != 1 || !Files.isRegularFile(Paths.get(args[0]))) {
			System.out.println("Expected 1 argument: path to config file.");
			return;
		}
		SmartHttpServer server = new SmartHttpServer(args[0]);
		server.start();
	}
}
