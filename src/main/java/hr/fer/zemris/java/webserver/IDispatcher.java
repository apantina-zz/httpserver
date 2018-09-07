package hr.fer.zemris.java.webserver;

/**
 * In our server, when a request is processed, it is dispatched further 
 * to other methods where it will be processed.
 * @author 0036502252
 *
 */
public interface IDispatcher {
	/**
	 * Dispatches the request which will be processed.
	 * @param urlPath the URL of the request
	 * @throws Exception
	 */
	void dispatchRequest(String urlPath) throws Exception;
}