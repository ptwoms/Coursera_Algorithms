/**
 * Trie26ST.java
 * 
 * @author Pyae Phyo Myint Soe
 * 
 */

//Clone of edu.princeton.cs.algs4.TrieST
public class Trie26ST<Value> {

	private Node root; 
	private int n;
	
	/**
	 * Initializes an empty string symbol table.
	 */
	public Trie26ST() {
	}

	public Node getRootNode() {
		return root;
	}

	/**
	 * Returns the value associated with the given key.
	 * @param key the key
	 * @return the value associated with the given key if the key is in the symbol table
	 *     and {@code null} if the key is not in the symbol table
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public Value get(String key) {
		Node x = get(root, key, 0);
		if (x == null) return null;
		return (Value) x.val;
	}

	public Node get(Node x, String key, int d) {
		int keyLength = key.length();
		char c;		
		while(x != null) {
			if (d == keyLength) return x;
			c = key.charAt(d);
			x = x.next[c - 'A'];
			d += 1;
		}
		return null;
	}

	/**
	 * Inserts the key-value pair into the symbol table, overwriting the old value
	 * with the new value if the key is already in the symbol table.
	 * If the value is {@code null}, this effectively deletes the key from the symbol table.
	 * @param key the key
	 * @param val the value
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public void put(String key, Value val) {
		root = put(root, key, val, 0);
	}

	public Node put(Node x, String key, Value val, int d) {
		if (x == null) x = new Node();
		if (d == key.length()) {
			if (x.val == null) n++;
			x.val = val;
			return x;
		}
		int index = key.charAt(d) - 'A';
		x.next[index] = put(x.next[index], key, val, d+1);
		return x;
	}

	/**
	 * Returns the number of key-value pairs in this symbol table.
	 * @return the number of key-value pairs in this symbol table
	 */
	public int size() {
		return n;
	}

	/**
	 * Is this symbol table empty?
	 * @return {@code true} if this symbol table is empty and {@code false} otherwise
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

}
