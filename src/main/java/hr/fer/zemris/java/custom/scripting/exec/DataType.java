package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Used to denote various data types that can be used in the
 * {@link ValueWrapper} class.
 * 
 * @author 0036502252
 *
 */
public enum DataType {
	/**
	 * Represents an integer.
	 */
	INTEGER,
	/**
	* Represents a double.
	*/
	DOUBLE,
	/**
	* Represnts a string which can be parsed into an integer.
	*/
	STRING_INTEGER,
	/**
	* Represnts a string which can be parsed into a double value.
	*/
	STRING_DOUBLE,
	/**
	 * Represents a <code>null</code> value.
	 */
	NULL;
}
