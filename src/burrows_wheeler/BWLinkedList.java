import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * BWLinkedList.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 19/12/17.
 */

public class BWLinkedList<Item> implements Iterable<Item> {
	private Node first, last;
	private int n;

	// helper linked list class
	private class Node {
		private Item item;
		private Node next;
	}

	public BWLinkedList() {
		first = new Node();//sentinel
		last = first;
		n = 0;
	}

	public boolean isEmpty() {
		return first == null;
	}

	public int size() {
		return n;
	}

	public void addLast(Item item) {
		Node oldLast = last;
		last = new Node();
		last.item = item;
		oldLast.next = last;
		n++;
	}
	
	public int getLocAndMoveFirst(Item item) {
		Node prevNode = first;
		Node curNode = first.next;
		int curIndex = 0;
		while(curNode != null) {
			if(curNode.item.equals(item)) {
				prevNode.next = curNode.next;
				curNode.next = first.next;
				first.next = curNode;
				return curIndex;
			}
			curIndex++;
			prevNode = curNode;
			curNode = curNode.next;
		}
		return 0;
	}
	
	public Item getValFromLocAndMoveFirst(int index) {
		Node prevNode = first;
		Node curNode = first.next;
		int curIndex = 0;
		while(curNode != null) {
			if(curIndex == index) {
				prevNode.next = curNode.next;
				curNode.next = first.next;
				first.next = curNode;
				return curNode.item;
			}
			curIndex++;
			prevNode = curNode;
			curNode = curNode.next;
		}
		return null;
	}

	public Iterator<Item> iterator()  {
		return new ListIterator();  
	}

	private class ListIterator implements Iterator<Item> {
		private Node current = first.next;

		public boolean hasNext()  { return current != null;                     }
		public void remove()      { throw new UnsupportedOperationException();  }

		public Item next() {
			if (!hasNext()) throw new NoSuchElementException();
			Item item = current.item;
			current = current.next; 
			return item;
		}
	}
}
