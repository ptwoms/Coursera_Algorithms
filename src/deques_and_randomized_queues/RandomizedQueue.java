import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * RandomizedQueue.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 12/3/17.
 */

public class RandomizedQueue<Item> implements Iterable<Item> {
    private int capacity;
    private Item[] items;
    private int length;
    private static final int MIN_CAPACITY = 1;

    public RandomizedQueue()                 // construct an empty randomized queue
    {
        length = 0;
        resize(MIN_CAPACITY);
    }

    public boolean isEmpty()                 // is the queue empty?
    {
        return length == 0;
    }

    private void resize(int size){
        capacity = size;
        Item[] newItems = (Item[]) new Object[capacity];
        for (int i = 0; i < length; i++) {
            newItems[i] = items[i];
        }
        items = newItems;
    }

    private void resizeIfNecessary(){
        if (length == capacity){
            resize(capacity*2);
        }else if (capacity > MIN_CAPACITY && length <= capacity/4){
            resize(capacity/2);
        }
    }

    public int size()                        // return the number of items on the queue
    {
        return length;
    }

    public void enqueue(Item item)           // add the item
    {
        if (item == null){
            throw new NullPointerException("Item must not be null");
        }
        resizeIfNecessary();
        items[length++] = item;
    }

    public Item dequeue()                    // remove and return a random item
    {
        if (length == 0){
            throw new NoSuchElementException("No Item to remove");
        }
        int indexToRemove = StdRandom.uniform(length);
        Item itemToReturn = items[indexToRemove];
        items[indexToRemove] = items[length-1];
        items[length-1] = null;
        length--;
        resizeIfNecessary();
        return itemToReturn;
    }

    public Item sample() // return (but do not remove) a random item
    {
        if (length == 0){
            throw new NoSuchElementException("No Item to remove");
        }
        return items[StdRandom.uniform(length)];
    }

    private class RandomizedDequeIterator implements Iterator<Item>{
        private Item[] copiedItem;
        private int curLength;

        public RandomizedDequeIterator(Item[] curItems) {
            copiedItem = (Item[]) new Object[length];
            for (int i = 0; i < length; i++) {
                copiedItem[i] = curItems[i];
            }
            curLength = length;
        }

        @Override
        public boolean hasNext() {
            return curLength > 0;
        }

        @Override
        public Item next() {
            if (curLength == 0){
                throw new NoSuchElementException("No more items");
            }
            int indexToReturn = StdRandom.uniform(curLength);
            Item itemToReturn = copiedItem[indexToReturn];
            copiedItem[indexToReturn] = copiedItem[curLength-1];
            curLength--;
            return itemToReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }
    }

    public Iterator<Item> iterator()// return an independent iterator over items in random order
    {
        return new RandomizedDequeIterator(items);
    }

    private void printData(){
        for (int i = 0; i < capacity; i++) {
            System.out.print("" + items[i] + " ");
        }
    }

    public static void main(String[] args)   // unit testing (optional)
    {
        RandomizedQueue<Integer> randQueue = new RandomizedQueue<>();
        for (int i = 0; i < 64; i++) {
            randQueue.enqueue(i);
        }
        randQueue.printData();
        System.out.println();
        for (int i = 0; i < 63; i++) {
            randQueue.dequeue();
        }
        randQueue.printData();
        System.out.println();

        System.out.println();
        for (int curInt:randQueue) {
            System.out.println("" + curInt + " ");
            for (int inner: randQueue) {
                System.out.print("" + inner + " ");
            }
            System.out.println();
        }

    }
}

