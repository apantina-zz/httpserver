package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Wraps an object which can then be used for performing mathematical operation.
 * 
 * @author 0036502252
 *
 */
public class ValueWrapper {
	/**
	 * Represents the type of the wrapper. If the wrapper value can be
	 * recognized as a certain type, it can then be used for mathematical
	 * operations. This enum holds the type of the first operand.
	 */
	private DataType firstType;
	/**
	 * Holds the type of the second operand.
	 */
	private DataType secondType;
	/**
	 * The value held by the wrapper.
	 */
	private Object value;

	/**
	 * Constructs a new {@link ValueWrapper} with the given value.
	 * 
	 * @param value
	 */
	public ValueWrapper(Object value) {
		this.value = value;
	}

	/**
	 * @return the object value of the wrapper
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to be set to the object
	 */
	public void setValue(Object value) {
		checkDataTypes(value, true);
		this.value = value;
	}

	/**
	 * Adds the value of <code>this</code> object to the given value.
	 * 
	 * @param incValue
	 *            the value to be added to this wrapper's value
	 * @throws ValueWrapperException
	 *             if the values are incompatible. Currently, the wrapper only
	 *             supports integers, doubles and strings which can be parsed
	 *             into doubles or integers. Null values are also supported, and
	 *             they represent an integer with the value of 0.
	 */
	public void add(Object incValue) {
		checkDataTypes(value, true);
		checkDataTypes(incValue, false);
		Double firstValue = parse(this.value, true);
		Double secondValue = parse(incValue, false);

		Double result = firstValue + secondValue;

		// if both data types are integers, the result must also be an integer
		if (bothIntegers()) {
			value = Integer.valueOf(result.intValue());
		} else {
			value = result;
		}
	}

	/**
	 * Subtracts the the given value from the value of <code>this</code>
	 * wrapper.
	 * 
	 * @param decValue
	 *            the value to be subtracted from this wrapper's value
	 * @throws ValueWrapperException
	 *             if the values are incompatible. Currently, the wrapper only
	 *             supports integers, doubles and strings which can be parsed
	 *             into doubles or integers. Null values are also supported, and
	 *             they represent an integer with the value of 0.
	 * 
	 */
	public void subtract(Object decValue) {
		checkDataTypes(value, true);
		checkDataTypes(decValue, false);

		Double firstValue = parse(this.value, true);
		Double secondValue = parse(decValue, false);

		Double result = firstValue - secondValue;

		// if both data types are integers, the result must also be an integer
		if (bothIntegers()) {
			value = Integer.valueOf(result.intValue());
		} else {
			value = result;
		}

	}

	/**
	 * Multiplies the value of <code>this</code> object with the given value.
	 * 
	 * @param mulValue
	 *            the value to be multiplied with this wrapper's value
	 * @throws ValueWrapperException
	 *             if the values are incompatible. Currently, the wrapper only
	 *             supports integers, doubles and strings which can be parsed
	 *             into doubles or integers. Null values are also supported, and
	 *             they represent an integer with the value of 0.
	 */
	public void multiply(Object mulValue) {
		checkDataTypes(value, true);
		checkDataTypes(mulValue, false);

		Double firstValue = parse(this.value, true);
		Double secondValue = parse(mulValue, false);

		Double result = firstValue * secondValue;

		// if both data types are integers, the result must also be an integer
		if (bothIntegers()) {
			value = Integer.valueOf(result.intValue());
		} else {
			value = result;
		}
	}

	/**
	 * Divides the value of <code>this</code> object by the given value.
	 * 
	 * @param divValue
	 *            with which this wrapper's value will be divided
	 * @throws ValueWrapperException
	 *             if the values are incompatible. Currently, the wrapper only
	 *             supports integers, doubles and strings which can be parsed
	 *             into doubles or integers. Null values are also supported, and
	 *             they represent an integer with the value of 0. Also thrown if
	 *             the user attempts to divide by zero. Saves lives.
	 */
	public void divide(Object divValue) {
		checkDataTypes(value, true);
		checkDataTypes(divValue, false);

		Double firstValue = parse(this.value, true);
		Double secondValue = parse(divValue, false);

		Double result = firstValue / secondValue;

		if (Math.abs(secondValue) < 1E-6) {
			throw new ValueWrapperException(
					"Can't divide by zero! Note: null is also considered as zero.");
		}

		// if both data types are integers, the result must also be an integer
		if (bothIntegers()) {
			value = Integer.valueOf(result.intValue());
		} else {
			value = result;
		}
	}

	/**
	 * Compares <code>this</code> wrapper's value with the given value.
	 * 
	 * @param withValue
	 *            the value with which <code>this</code> wrapper's value will be
	 *            compared
	 * @return 0 if both values are null, or if they are equal. Returns a
	 *         positive integer if <code>this</code> wrapper's value is greater,
	 *         or a negative integer if it is smaller.
	 * @throws ValueWrapperException
	 *             if the values are incompatible. Currently, the wrapper only
	 *             supports integers, doubles and strings which can be parsed
	 *             into doubles or integers. Null values are also supported, and
	 *             they represent an integer with the value of 0.
	 */
	public int numCompare(Object withValue) {
		checkDataTypes(value, true);
		checkDataTypes(withValue, false);

		Double firstValue = parse(this.value, true);
		Double secondValue = parse(withValue, false);

		return Double.compare(firstValue, secondValue);
	}

	// PRIVATE IMPLEMENTATION METHODS

	/**
	 * Private implementation method. Checks which data type corresponds to the
	 * given value.
	 * 
	 * @param value
	 *            the value to be checked
	 * @param isFromThisWrapper
	 *            this value is set to true, if the object to be checked belongs
	 *            to <code>this</code> wrapper, false otherwise. This is needed
	 *            to differentiate between values, because of the way types are
	 *            stored and later used for operations.
	 * @throws ValueWrapperException
	 *             if the values are incompatible
	 */
	private void checkDataTypes(Object value, boolean isFromThisWrapper) {
		DataType currentType;

		if (value == null) {
			currentType = DataType.NULL;
		} else if (value instanceof String) {
			String str = (String) value;
			currentType = str.contains(".") || str.contains("E")
					? DataType.STRING_DOUBLE
					: DataType.STRING_INTEGER;
		} else if (value instanceof Integer) {
			currentType = DataType.INTEGER;
		} else if (value instanceof Double) {
			currentType = DataType.DOUBLE;
		} else {
			throw new ValueWrapperException("When using arithmetic operations, "
					+ "both the wrapper types must be either an instance"
					+ " of Integer, String, Double or a null value!");

		}

		/*
		 * The aforementioned boolean value sent to the method is used here:
		 * since the operations are performed in a way where the first value is
		 * from the wrapper, and the second from the method call, we can set the
		 * data type to the according position. We later use this for addition,
		 * subtraction etc.
		 */

		if (isFromThisWrapper) {
			firstType = currentType;
		} else {
			secondType = currentType;
		}
	}

	/**
	 * Parses an object into a value which can be used for performing
	 * mathematical operations.
	 * 
	 * @param value
	 *            the object to be parsed
	 * @param isFromThisWrapper
	 *            this value is set to true, if the object to be checked belongs
	 *            to <code>this</code> wrapper, false otherwise. This is needed
	 *            to differentiate between values, because of the way types are
	 *            stored and later used for operations.
	 * @return the double value parsed from the object
	 */
	private Double parse(Object value, boolean isFromThisWrapper) {
		DataType type = isFromThisWrapper ? firstType : secondType;
		switch (type) {
		case DOUBLE:
			return (Double) value;
		case INTEGER:
			Integer i = Integer.valueOf((int) value);
			return i.doubleValue();
		case NULL:
			return Double.valueOf(0);
		case STRING_DOUBLE:
			try {
				return Double.parseDouble((String) value);
			} catch (NumberFormatException ex) {
				throw new ValueWrapperException(
						"Input \"" + (String) value + "\" is unparsable!", ex);
			}
		case STRING_INTEGER:
			try {
				String v = (String) value;
				return Double.parseDouble(v.replace("\"", ""));
			} catch (NumberFormatException ex) {
				throw new ValueWrapperException(
						"Input \"" + (String) value + "\" is unparsable!", ex);
			}
		}
		return null;
	}

	/**
	 * Utility method. Checks if both of the data types are integers, (in which
	 * case the result must also be an integer).
	 * 
	 * @return true if both data types are integers
	 */
	private boolean bothIntegers() {
		return (firstType == DataType.INTEGER
				|| firstType == DataType.STRING_INTEGER
				|| firstType == DataType.NULL)
				&& (secondType == DataType.INTEGER
						|| secondType == DataType.STRING_INTEGER
						|| secondType == DataType.NULL);
	}
}