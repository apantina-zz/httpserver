package hr.fer.zemris.java.webserver;

import hr.zemris.java.custom.collections.Collection;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Models an HTTP request, with the corresponding header.
 * @author 0036502252
 *
 */
public class RequestContext {
	/**
	 * The output stream used for outputting content to the client. 
	 */
	private OutputStream outputStream;
	/**
	 * The charset used by the output stream.
	 */
	private Charset charset;

	/**
	 * The encoding used by the output stream.
	 */
	public String encoding = DEFAULT_ENCODING;
	/**
	 * The status code of the HTTP request.
	 */
	public int statusCode = DEFAULT_STATUS_CODE;
	/**
	 * The text for the corresponding status code of the request.
	 */
	public String statusText = DEFAULT_STATUS_TEXT;
	/**
	 * The mime type for the HTTP header.
	 */
	public String mimeType = DEFAULT_MIME_TYPE;

	/**
	 * Flag which indicates whether the header has been generated.
	 */
	private boolean headerGenerated;

	/**
	 * This context's dispatcher.
	 */
	private IDispatcher dispatcher;
	
	/**
	 * Maps each parameter by its name.
	 */
	private Map<String, String> parameters;
	/**
	 * Maps each persistent parameter by its name.
	 */
	private Map<String, String> persistentParameters;
	/**
	 * Maps each temporary parameter by its name.
	 */
	private Map<String, String> temporaryParameters;
	/**
	 * This request context's cookies.
	 */
	private List<RCCookie> outputCookies;

	/**
	 * Default encoding used.
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";
	/**
	 * Default status code used when everything is working according to the 
	 * specfications.
	 */
	private static final int DEFAULT_STATUS_CODE = 200;
	/**
	 * The default status text for the default status code.
	 */
	private static final String DEFAULT_STATUS_TEXT = "OK";
	/**
	 * The default mime type used.
	 */
	private static final String DEFAULT_MIME_TYPE = "text/html";
	
	/**
	 * Length of the content which is dispatched.
	 */
	public Long contentLength;
	
	/**
	 * @param contentLength the content length to be set
	 */
	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * @return the dispatcher for this context
	 */
	public IDispatcher getDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Constructs a new {@link RequestContext}.
	 * @param outputStream the underlying output stream 
	 * @param parameters the parameter map
	 * @param persistentParameters the persistent parameter map
	 * @param outputCookies the list of cookies
	 * @param dispatcher this context's dispatcher
	 * @param temporaryParameters the temporary parameter map
	 */
	public RequestContext(OutputStream outputStream,
			Map<String, String> parameters,
			Map<String, String> persistentParameters,
			List<RCCookie> outputCookies, IDispatcher dispatcher, 
			Map<String, String> temporaryParameters) {

		this.outputStream = Objects.requireNonNull(outputStream);

		this.outputCookies = outputCookies == null ?
				new ArrayList<>() :
				outputCookies;

		this.parameters = parameters == null ?
				Collections.emptyMap() :
				Collections.unmodifiableMap(parameters);

		this.persistentParameters = persistentParameters == null ?
				new HashMap<>() :
				persistentParameters;

		this.temporaryParameters = temporaryParameters;
		this.dispatcher = dispatcher;
	}

	
	/**
	 * Constructs a new {@link RequestContext}.
	 * @param outputStream the underlying output stream 
	 * @param parameters the parameter map
	 * @param persistentParameters the persistent parameter map
	 * @param outputCookies the list of cookies
	 */
	public RequestContext(OutputStream outputStream,
			Map<String, String> parameters,
			Map<String, String> persistentParameters,
			List<RCCookie> outputCookies) {
		this(outputStream, parameters, persistentParameters, outputCookies, 
				(IDispatcher) null, new HashMap<String, String>());
	
	}

	/**
	 * @param name the name of the required parameter
	 * @return the parameter mapped to given name
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * @return the set of all parameter names used by this context
	 */
	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(parameters.keySet());
	}

	/**
	 * @param name the name of the required persistent parameter
	 * @return the persistent parameter mapped to given name
	 */
	public String getPersistentParameter(String name) {
		return persistentParameters.get(name);
	}

	/**
	 * @return the set of all persistent parameter names used by this context
	 */
	public Set<String> getPersistentParameterNames() {
		return Collections.unmodifiableSet(persistentParameters.keySet());
	}

	/**
	 * Adds a new persistent parameter to this context's persistent parameter
	 * collection.
	 * @param name the name of the new persistent parameter
	 * @param value the value of the new persistent parameter
	 */
	public void setPersistentParameter(String name, String value) {
		persistentParameters.put(name, value);
	}

	/**
	 * Removes a persistent parameter from this context's persistent 
	 * parameter map.
	 * @param name the name of the persistent parameter which 
	 * will be removed
	 */
	public void removePersistentParameter(String name) {
		persistentParameters.remove(name);
	}
	/**
	 * @param name the name of the required temporary parameter
	 * @return the temporary parameter mapped to given name
	 */
	public String getTemporaryParameter(String name) {
		return temporaryParameters.get(name);
	}
	
	/**
	 * Adds a new temporary parameter to this context's temporary parameter
	 * collection.
	 * @param name the name of the new temporary parameter
	 * @param value the value of the new temporary parameter
	 */
	public void setTemporaryParameter(String name, String value) {
		temporaryParameters.put(name, value);
	}
	
	/**
	 * Removes a temporary parameter from this context's temporary
	 * parameter map.
	 * @param name the name of the persistent parameter which 
	 * will be removed
	 */
	public void removeTemporaryParameter(String name) {
		temporaryParameters.remove(name);
	}

	/**
	 * Writes given data to the context's output stream.
	 * @param data the data to be written
	 * @return this context
	 * @throws IOException if writing goes awry
	 */
	public RequestContext write(byte[] data) throws IOException {
		generateHeader();
		
		outputStream.write(data);
		return this;
	}
	
	/**
	 * Writes given string to the context's output stream.
	 * @param text the text string to be written
	 * @return this context
	 * @throws IOException if writing goes awry
	 */
	public RequestContext write(String text) throws IOException {
		generateHeader();

		outputStream.write(text.getBytes(charset));
		return this;
	}
	
	/**
	 * Writes given data to the context's output stream.
	 * @param data the data to be written
	 * @param offset the start offset in the data 
	 * @param len the number of bytes to write
	 * @return this context
	 * @throws IOException if writing goes awry
	 */
	 public RequestContext write(byte[] data, int offset, int len) throws IOException{
		 generateHeader();

		 outputStream.write(data, offset, len);
		 return this;
	 }

	/**
	 * Generates a HTTP header if none was generated prior.
	 */
	private void generateHeader() {
		if (headerGenerated) return;
		
		StringBuilder header = new StringBuilder();
		header.append(
				"HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
				+ "Content-Type: " + mimeType 
		);
		
		header.append(
				mimeType.startsWith("text/") ? 
						( "; charset=" + encoding ) + "\r\n" 
						: "\r\n"
		);
		
		if(contentLength != null) {
			header.append(
					"Content-Length: " + contentLength + "\r\n"
			);
		}
		
		if(!outputCookies.isEmpty()) {
			for(RCCookie cookie : outputCookies) {				
				header.append(
						"Set-Cookie: " + cookie.name + 
						"=\"" + cookie.value + "\""
				);
				if(cookie.domain != null) {
					header.append("; Domain=" + cookie.domain);	
				}
				if(cookie.path != null) {
					header.append("; Path=" + cookie.path);	
				}
				header.append(
						cookie.maxAge == null ? 
						"" 
						: ("; Max-Age=" + cookie.maxAge + "; HttpOnly") 
				);
				header.append("\r\n");
			}
		}
		
		header.append("\r\n");
		writeHeader(header.toString());
	}
	
	/**
	 * Writes a header to the underlying output stream.
	 * @param header the header to be written
	 */
	private void writeHeader(String header) {		
		
		byte[] data = header.getBytes(StandardCharsets.ISO_8859_1);
		
		try {
			outputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		headerGenerated = true;
		charset = Charset.forName(encoding);
	}

	/**
	 * Sets this context's encoding.
	 * @param encoding the encoding to be set
	 */
	public void setEncoding(String encoding) {
		if (headerGenerated) throw new RuntimeException("Header generated!");
		this.encoding = encoding;
	}

	/**
	 * Sets a status code to this context.
	 * @param statusCode the status code to be set
	 */
	public void setStatusCode(int statusCode) {
		if (headerGenerated) throw new RuntimeException("Header generated!");
		this.statusCode = statusCode;
	}

	/**
	 * Sets a status text to this context.
	 * @param statusText the statusText to be set
	 */
	public void setStatusText(String statusText) {
		if (headerGenerated) throw new RuntimeException("Header generated!");
		this.statusText = statusText;
	}

	/**
	 * Sets a mime type to this context.
	 * @param mimeType the mimeType to be set
	 */
	public void setMimeType(String mimeType) {
		if (headerGenerated) throw new RuntimeException("Header already generated!");
		this.mimeType = mimeType;
	}

	/**
	 * Adds a cookie to this context's internal collection of cookies.
	 * @param rcCookie the cookie to be added
	 */
	public void addRCCookie(RCCookie rcCookie) {
		if (headerGenerated) throw new RuntimeException("Header generated!");
		outputCookies.add(rcCookie);
	}


	/**
	 * Models an HTTP cookie.
	 * @author 0036502252
	 *
	 */
	public static class RCCookie {
		/**
		 * The name of this cookie.
		 */
		private final String name;
		/**
		 * The value of this cookie.
		 */
		private final String value;
		/**
		 * The origin domain of the cookie.
		 */
		private final String domain;
		/**
		 * The origin path of this cookie.
		 */
		private final String path;

		/**
		 * Determines how long this cookie will remain alive. (in seconds)
		 */
		private final Integer maxAge;

		/**
		 * Constructs a new {@link RCCookie}.
		 * @param name the name of the cookie
		 * @param value the value of the cookie
		 * @param maxAge the cookie's age, in seconds
		 * @param domain the cookie's origin domain
		 * @param path the cookie's origin path
		 */
		public RCCookie(String name, String value, Integer maxAge, String domain, 
				String path) {
			this.name = name;
			this.value = value;
			this.domain = domain;
			this.path = path;
			this.maxAge = maxAge;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @return the domain
		 */
		public String getDomain() {
			return domain;
		}

		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @return the maxAge
		 */
		public Integer getMaxAge() {
			return maxAge;
		}
	}
}
