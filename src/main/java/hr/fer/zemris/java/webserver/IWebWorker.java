package hr.fer.zemris.java.webserver;

/**
 * This is an interface towards any object that can process the current request.
 * It gets the {@link RequestContext} as a parameter and it is expected
 * to create content for the client. 
 * @author 0036502252
 *
 */
public interface IWebWorker {
	/**
	 * Processes a request from the client.
	 * @param context the server's current context
	 * @throws Exception
	 */
	public void processRequest(RequestContext context) throws Exception;
}
