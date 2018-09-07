package hr.zemris.java.custom.collections;

/**
 * Represents a generic collection of objects.
 * @author 0036502252
 *
 */
public class Collection {
	
	/**
	 * Default protected constructor for the class.
	 */
	protected Collection() {
	}
	
	/**
	 * Checks whether the collection is empty.
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * Should return the size of the collection, however no implementation is 
	 * provided in this class.
	 * @return 0, always
	 */
	public int size() {
		return 0;
	}
	
	/**
	 * Adds a value to the collection. No implementation provided in this class.
	 * @param value the value to be added
	 */
	public void add(Object value) {
		
	}
	
	/**
	 * Checks if the collection contains the given value. No implementation 
	 * provided in this class.
	 * @param value the value to be checked
	 * @return false, always
	 */
	public boolean contains(Object value) {
		return false;
	}
	
	/**
	 * Removes the given value from the collection, if it is present in it. 
	 * @param value the value to be found and then removed from the collection
	 * @return false, always
	 */
	public boolean remove(Object value) {
		return false;
	}
	
	/**
	 * Guaranteed to throw an exception.
	 * @throws UnsupportedOperationException always 
	 * @return cannot return a value
	 */
	public Object[] toArray() {
		throw new UnsupportedOperationException("This operation is not"
				+ " supported for this class!");
	}
	
	/**
	 * Calls the Processor.process() method for each element of the collection.
	 * No implementation provided in this class.
	 * @param processor the processor whose <code>process</code> method will be
	 * called
	 */
	public void forEach(Processor processor) {

	}
	
	/**
	 * Adds all elements from the given collection into the current collection,
	 * without changing the other collection.
	 * @param other the collection from which the elements are added
	 */
	public void addAll(Collection other) {
		class LocalProcessor extends Processor{
			public void process(Object value) {
				add(value);
			}
		}
		LocalProcessor processor = new LocalProcessor();
		other.forEach(processor);

	}
	/**
	 * Clears the collection. No implementation provided in this class.
	 */
	public void clear() {
		
	}
}
