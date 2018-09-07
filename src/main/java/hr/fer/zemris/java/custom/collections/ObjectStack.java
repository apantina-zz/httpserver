package hr.zemris.java.custom.collections;

/**
 * An implementation of the stack data structure. Uses an array for data storage.
 * @author 0036502252
 */
public class ObjectStack {
	/**
	 * The array which holds the stack's data.
	 */
	private ArrayIndexedCollection data;
	
	/**
	 * Checks if the stack is empty.
	 * @return true if the stack is empty
	 */
	
	/**
	 * Default constructor. Constructs a new empty stack.
	 */
	public ObjectStack() {
		this.data = new ArrayIndexedCollection();
	}
	
	/**
	 * Checks if the stack is empty.
	 * @return true if the stack is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	/**
	 * Returns the amount of objects on the stack
	 * @return the stack's size
	 */
	public int size() {
		return data.size();
	}
	
	/**
	 * Pushes an object on the stack.
	 * @param value the value to be pushed
	 */
	public void push(Object value) {
		data.add(value);
	}
	
	/**
	 * Pops an object from the stack.
	 * @return the popped object
	 */
	public Object pop() {
		if(isEmpty()) throw new EmptyStackException(
				"Can't pop from an empty stack!");
		Object obj = data.get(data.size() - 1);
		data.remove(data.size() - 1);
		return obj;
	}
	
	/**
	 * Clears the entire stack.
	 */
	public void clear() {
		data.clear();
	}
	
	/**
	 * Returns an object from the top of the stack, similarly to the 
	 * {@code pop} method, but without removing the object.
	 * @return the object from the top of the stack
	 */
	public Object peek() {
		if(isEmpty()) throw new EmptyStackException(
				"Can't peek an empty stack!");
		return data.get(data.size() - 1);	
	}
}
