import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Deque.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 12/3/17.
 */
public class Deque<Item> implements Iterable<Item> {
    private int capacity;
    private Item[] items;
    private int firstIndex, lastIndex;
    private int length;
    private static final int MIN_CAPACITY = 1;

    public Deque()                           // construct an empty deque
    {
        resize(MIN_CAPACITY);
    }

    private void resize(int size){
        capacity = size;
        Item[] newItems = (Item[]) new Object[capacity];
        if (length == 0){
            firstIndex = lastIndex = -1;
        }else {
            int curIndex = firstIndex;
            firstIndex = (capacity-length)/2;
            for (int i = 0; i < length; i++) {
                newItems[firstIndex+i] = items[curIndex++];
                if (curIndex >= items.length){
                    curIndex = 0;
                }
            }
            lastIndex = firstIndex+length-1;
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

    public boolean isEmpty()                 // is the deque empty?
    {
        return length == 0;
    }

    public int size()                        // return the number of items on the deque
    {
        return length;
    }

    private void addItemAtIndex(int index, Item item){
        length++;
        items[index] = item;
    }

    private void performNullCheck(Item item) {
        if (item == null){
            throw new NullPointerException("Item must not be null");
        }
    }

    public void addFirst(Item item)          // add the item to the front
    {
        performNullCheck(item);
        resizeIfNecessary();
        if (firstIndex == -1) {
            firstIndex = lastIndex = (capacity - 1) / 2;
        }else if (firstIndex == 0){
            firstIndex = capacity-1;
        }else {
            firstIndex--;
        }
        addItemAtIndex(firstIndex, item);
    }

    public void addLast(Item item)           // add the item to the end
    {
        performNullCheck(item);
        resizeIfNecessary();
        if (lastIndex == -1){
            firstIndex = lastIndex = (capacity-1)/2;
        }
        else if (lastIndex >= capacity-1){
            lastIndex = 0;
        }else {
            lastIndex++;
        }
        addItemAtIndex(lastIndex, item);
    }

    private void checkIfThereAnyItem(){
        if (length == 0){
            throw new NoSuchElementException("No Item to remove");
        }
    }

    public Item removeFirst()                // remove and return the item from the front
    {
        checkIfThereAnyItem();
        length--;
        Item itemToReturn = items[firstIndex];
        items[firstIndex] = null;//reset value
        if (length == 0){
            firstIndex = lastIndex = -1;
        }else {
            firstIndex++;
            if (firstIndex >= capacity){
                firstIndex = 0;
            }
        }
        resizeIfNecessary();
        return itemToReturn;
    }

    public Item removeLast()                 // remove and return the item from the end
    {
        checkIfThereAnyItem();
        length--;
        Item itemToReturn = items[lastIndex];
        items[lastIndex] = null;
        if (length == 0){
            firstIndex = lastIndex = -1;
        }else {
            lastIndex--;
            if(lastIndex < 0){
                lastIndex = capacity - 1;
            }
        }
        resizeIfNecessary();
        return itemToReturn;
    }

    public Iterator<Item> iterator()         // return an iterator over items in order from front to end
    {
        return new DequeIterator(firstIndex);
    }

    private void printData(){
        for (int i = 0; i < capacity; i++) {
            System.out.print("" + items[i] + " ");
        }
    }

    private class DequeIterator implements Iterator<Item>{
        private int curIndex;
        private int curLength;

        public DequeIterator(int curIndex) {
            this.curIndex = curIndex;
            curLength = length;
        }

        @Override
        public boolean hasNext() {
            return  curLength > 0;
        }

        @Override
        public Item next() {
            if (curLength == 0){
                throw new NoSuchElementException("No more items");
            }
            curLength--;
            Item itemToReturn = items[curIndex++];
            if (curIndex >= capacity){
                curIndex = 0;
            }
            return itemToReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }
    }

    public static void main(String[] args)   // unit testing (optional)
    {
        Deque<Integer> deque = new Deque<>();
//        deque.addLast(10);
        for (int i = 0; i < 32; i++) {
            deque.addFirst(11+i);
            deque.addLast(11+i);
//            deque.removeLast();
//            deque.removeFirst();
        }
        deque.printData();
        System.out.println();
        for (int i = 0; i < 31; i++) {
            deque.removeLast();
            deque.removeFirst();
        }
        deque.removeLast();
//        deque.addLast(10);
        System.out.println("My Size: " + deque.size());
        deque.printData();
        System.out.println();
        for (int curInt:deque) {
            System.out.print(""+ curInt + " ");
        }
    }
}
