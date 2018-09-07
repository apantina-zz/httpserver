package hr.zemris.java.custom.collections;

import java.util.Arrays;
/**
 * An implementation of a resizable array-backed collection of objects.
 * @author 0036502252
 *
 */
public class ArrayIndexedCollection extends Collection{
	/**
	 * Size of the array indexed collection.
	 */
	private int size;
	/**
	 * Capacity of the array indexed collection.
	 */
	private int capacity;
	/**
	 * The elements of the array indexed collection.
	 */
	private Object[] elements;
	/**
	 * The default capacity of the collection, assigned when the default 
	 * constructor is called. 
	 */
	private static final int DEFAULT_CAPACITY = 16;
	/**
	 * The minimum allowed capacity of the collection. 
	 */
	private static final int MINIMUM_CAPACITY= 1;
	
	/**
	 * Default constructor. Initializes the capacity and the array of the 
	 * collection.
	 */
	public ArrayIndexedCollection() {
		this.capacity = DEFAULT_CAPACITY;
		elements = new Object[DEFAULT_CAPACITY];
	}

	/**
	 * Initializes the collection's array and capacity according to the given 
	 * initial capacity.
	 * @param initialCapacity the given initial capacity
	 * @throws IllegalArgumentException 
	 */
	public ArrayIndexedCollection(int initialCapacity) {
		if(initialCapacity < MINIMUM_CAPACITY) {
			throw new IllegalArgumentException("The initial capacity must be a"
				+ "number larger than " + MINIMUM_CAPACITY + "!");
		}
		this.capacity = initialCapacity;
		elements = new Object[initialCapacity];
	}
	
	/**
	 * Using this constructor, a copy of the given collection is made.
	 * @param other
	 * @throws NullPointerException if <code>other</code> is a null reference
	 */
	public ArrayIndexedCollection(Collection other) {
		this(); //call the default constructor
		if(other == null) {
			throw new NullPointerException("The given collection"
				+ "must not be null!");			
		}
		this.addAll(other);
	}

	/**
	 *
	 * Initializes the collection's array and capacity according to the given 
	 * initial capacity.
	 * Using this constructor, a copy of the given collection is made.
	 * @param other the collection to be copied
	 * @param initialCapacity the given initial capacity
	 * @throws NullPointerException if <code>other</code> is a null reference
	 */
	public ArrayIndexedCollection(Collection other, int initialCapacity) {
		this(initialCapacity);
		if(other == null) {
			throw new NullPointerException("The given collection"
				+ "must not be null!");			
		}
		if(this.capacity < other.size()) {
			this.capacity = other.size();
			this.elements = new Object[this.capacity];
		}
		for(Object element : other.toArray()) {
			this.add(element);
		}
	}
	/**
	 * Returns the size of the array indexed collection.
	 */
	@Override
	public int size() {
		return this.size;
	}
	
	/**
	 * Adds a value at the end of the array indexed collection.
	 * @throws NullPointerException if the user attempts to add a null value
	 * @param value the value to be added
	 */
	@Override
	public void add(Object value) {
		if(value == null) throw new NullPointerException("Can't add a null value!");
		ensureCapacity();
		elements[size] = value;
		size++;
	}
	
	/**
	 * Returns the object stored in the array at the position <code>index</code>. 
	 * @param index the index at which the object should be returned
	 * @return the object at the index
	 * @throws IndexOutOfBoundsException if the index is less than 0 
	 * or greater than size - 1
	 */
	public Object get(int index) {
		if(index < 0 || index > size-1) {
			throw new IndexOutOfBoundsException("Invalid index! Must be between"
					+ "0 and size-1!");
		}
		return elements[index];
	}
	
	/**
	 * Removes all elements from the array indexed collection.
	 */
	@Override
	public void clear() {
		Arrays.fill(elements, null);
		size = 0;
	}
	/**
	 * Inserts the value at the desired index in the array indexed collection.
	 * @param value the value to be inserted
	 * @param position the desired index
	 * @throws NullPointerException in case of an attempted insertion of a null 
	 * value
	 * @throws IllegalArgumentException in case of an invalid position 
	 */
	public void insert(Object value, int position) {
		if(value == null) {
			throw new NullPointerException("Can't insert a null value!");
		}
		if(position < 0 || position > size) {
			throw new IndexOutOfBoundsException("Invalid position for insertion!"
				+ "Must be between 0 and size!");
		}
		
		size++;
		ensureCapacity();
		
		//shift the elements, and add the new element at the desired position
		for(int i = position, n = size(); i < n; i++) {
			elements[i+1] = elements[i];
		}
		elements[position] = value;
	}
	/**
	 * Returns the index of the first occurence of the given <code>value</code>
	 * or -1 if the <code>value</code> is not found or is a null value.
	 * @param value the value to be found
	 * @return the index at which the value was first found, -1 otherwise
	 */
	public int indexOf(Object value) {
		if(value == null) return -1;
		for(int i = 0, n = size(); i < n; i++) {
			if(elements[i].equals(value)) return i;
		}
		 return -1;
	}
	
	/**
	 * Checks if the array indexed collection contains the given value.
	 * @param value the value to be checked
	 * @return true if the array indexed collection contains the value, false
	 * otherwise
	 */
	@Override
	public boolean contains(Object value) {
		if(value == null) return false;
		for(int i = 0, n = size; i < n; i++) {
			if(elements[i].equals(value)) return true;
		}
		
		return false;
	}
	
	/**
	 * Removes an element at the specified index from the location. 
	 * @param index the index at which the element will be removed.
	 * @throws IndexOutOfBoundsException if the invalid <code>index</code> 
	 * is given
	 */
	public void remove(int index) {
		if(index < 0 || index > size-1) {
			throw new IndexOutOfBoundsException("Index must be between 0 and size-1!");
		}
		for(int i = index, n = size(); i < n; i++) {
			elements[i] = elements[i+1];
		}
		size--;
		
	}
	
	/**
	 * Removes a value from the array indexed collection, if it is present.
	 * @param value the value to be removed
	 * @return true if the value was removed, false otherwise
	 */
	@Override
	public boolean remove(Object value) {
		int index = indexOf(value);
		if(index == -1) return false;
		else {
			remove(index);
			return true;
		}
	}
	
	/**
	 * Returns an array representation of the collection.
	 */
	@Override
	public Object[] toArray() {
		return Arrays.copyOf(elements, size);
	}
	
	/**
	 * Calls the Processor.process() method for each element of the collection.
	 * @param processor the processor whose <code>process</code> method will be
	 * called
	 */
	@Override
	public void forEach(Processor processor) {
		for(int i = 0; i < size; i++) {
			processor.process(elements[i]);
		}
	}
	
	/**
	 * Adds all the elements from another collection to this collection.
	 * @param other the collection from which the elements are extracted
	 */
	@Override
	public void addAll(Collection other) {
		class P extends Processor{
			public void process(Object value) {
				add(value);
			}
		}
		other.forEach(new P());
	}
	
	/**
	 * Utility method. Ensures the collection has a large enough capacity.
	 */
	private void ensureCapacity() {
		if(size == capacity) {
			elements = Arrays.copyOf(elements, capacity*2);
			capacity *= 2;
		}
	}
}
