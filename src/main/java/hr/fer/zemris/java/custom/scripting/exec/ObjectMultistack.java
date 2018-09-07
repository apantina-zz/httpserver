package hr.fer.zemris.java.custom.scripting.exec;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Represents a custom data structure, which uses a map as its underlying data
 * structure. The map's keys are strings, but the values are each a separate
 * stack (implemented using linked list nodes).
 * 
 * @author 0036502252
 *
 */
public class ObjectMultistack {
	/**
	 * The underlying data structure which maps each string to a separate stack.
	 */
	private Map<String, MultistackEntry> map;

	/**
	 * Constructs a new {@link ObjectMultistack}.
	 */
	public ObjectMultistack() {
		this.map = new HashMap<>();
	}

	/**
	 * Pushes a value to the multistack of choice, determined by the
	 * <code>name</code> parameter.
	 * 
	 * @param name
	 *            the name of stack onto which the value will be pushed
	 * @param valueWrapper the value to be pushed
	 * @throws NullPointerException if the wrapper or name is a null reference
	 */
	public void push(String name, ValueWrapper valueWrapper) {
		Objects.requireNonNull(valueWrapper);
		Objects.requireNonNull(name); //throw NPE 
		
		if (map.get(name) == null) {
			map.put(name, new MultistackEntry(valueWrapper));
		} else {
			MultistackEntry oldHead = map.get(name);
			MultistackEntry head = new MultistackEntry(valueWrapper);
			head.next = oldHead;
			map.put(name, head);
		}
	}
	
	/**
	 * Pops a value from the multistack of choice, determined by the
	 * <code>name</code> parameter.
	 * 
	 * @param name
	 *            the name of stack from which the value will be popped
	 * @return the value from the top of the given stack
	 */
	public ValueWrapper pop(String name) {
		Objects.requireNonNull(name); //if name is null, throw NPE
		if (isEmpty(name)) { //otherwise throw NSEE if the stack is empty
			throw new NoSuchElementException("Can't pop from an empty stack!");
		}

		MultistackEntry head = map.get(name);
		ValueWrapper topValue = head.value;
		head = head.next; // GC, do your thing..
		map.put(name, head);

		return topValue;
	}
	
	/**
	 * Gets a value from the multistack of choice, determined by the
	 * <code>name</code> parameter.
	 * 
	 * @param name
	 *            the name of stack from which the value will be gotten
	 * @return the value from the top of the given stack
	 */
	public ValueWrapper peek(String name) {
		if (isEmpty(name)) {
			throw new NoSuchElementException("Can't peek an empty stack!");
		}
		return map.get(name).value;
	}

	/**
	 * Checks if the given stack is empty.
	 * @param name the name of the stack to be checked
	 * @return true if the stack has no values on it, false otherwise
	 */
	public boolean isEmpty(String name) {
		return map.get(name) == null;
	}

	/**
	 * A single multistack value, which is modelled as a linked list node.
	 * @author 0036502252
	 *
	 */
	static class MultistackEntry {
		/**
		 * The value of the entry.
		 */
		ValueWrapper value;
		/**
		 * The reference to the next {@link MultistackEntry}.
		 */
		MultistackEntry next;

		/**
		 * Constructs a new {@link MultistackEntry} with the given value.
		 * @param value the value to be set to the entry
		 */
		public MultistackEntry(ValueWrapper value) {
			this.value = value;
		}

	}
}
