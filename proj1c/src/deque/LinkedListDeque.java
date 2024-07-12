package deque;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T> {
    private int count;
    private ListNode<T> sentinel;
    public LinkedListDeque() {
        count = 0;
        sentinel = new ListNode<>();
        sentinel.setPreNode(sentinel);
        sentinel.setNextNode(sentinel);
    }

    @Override
    public void addFirst(T x) {
        ListNode<T> node = new ListNode<>(x,sentinel,sentinel.getNextNode());
        sentinel.getNextNode().setPreNode(node);
        sentinel.setNextNode(node);
        count++;
    }

    @Override
    public void addLast(T x) {
        ListNode<T> node = new ListNode<>(x,sentinel.getPreNode(),sentinel);
        sentinel.getPreNode().setNextNode(node);
        sentinel.setPreNode(node);
        count++;
    }

    @Override
    public List<T> toList() {
        List<T> returnList = new ArrayList<>();
        if (count > 0) {
            ListNode<T> temp = sentinel;
            for (int i = 0; i < count; i++) {
                temp = temp.getNextNode();
                returnList.add(temp.getItemData());
            }
        }
        return returnList;
    }

    @Override
    public boolean isEmpty() {
        return (count == 0);
    }

    @Override
    public int size() {
        if (count > 0) {
            ListNode<T> temp = sentinel;
            for (int i = 0; i < count; i++) {
                temp = temp.getNextNode();
                System.out.printf("%s ",temp.getItemData().toString());
            }
            System.out.println("");
        }
        System.out.println("");
        return count;
    }

    @Override
    public T removeFirst() {
        T ret = null;
        if (count > 0){
            ListNode<T> temp = sentinel.getNextNode();
            temp.getNextNode().setPreNode(sentinel);
            sentinel.setNextNode(temp.getNextNode());
            ret = temp.getItemData();
        }
        return ret;
    }

    @Override
    public T removeLast() {
        T ret = null;
        if (count > 0){
            ListNode<T> temp = sentinel.getPreNode();
            temp.getPreNode().setNextNode(sentinel);
            sentinel.setPreNode(temp.getPreNode());
            ret = temp.getItemData();
        }
        return ret;
    }

    @Override
    public T get(int index) {
        T ret = null;
        if ((count > 0) && (count > index)) {
            ListNode<T> temp = sentinel;
            for (int i = 0; i <= index; i++) {
                temp = temp.getNextNode();
            }
            ret = temp.getItemData();
        }
        return ret;
    }

    @Override
    public T getRecursive(int index) {
        return get(index);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LinkedListDeque<?>){
            LinkedListDeque<T> otherObj = (LinkedListDeque<T>) other;
            if (this.count != otherObj.count){
                return false;
            }
            for (int i = 0; i < this.count; i++){
                if (!this.get(i).equals(otherObj.get(i))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("[");
        if (count > 0) {
            ListNode<T> temp = sentinel;
            for (int i = 0; i < count; i++) {
                temp = temp.getNextNode();
                if (i > 0){
                    ret.append(", ");
                }
                ret.append(temp.getItemData().toString());
            }
        }
        ret.append("]");
        return ret.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new LLDequeIterator();
    }

    private class LLDequeIterator implements Iterator<T> {
        private ListNode<T> curr = sentinel.getNextNode();

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T ret = curr.getItemData();
            curr = curr.getNextNode();
            return ret;
        }

        public boolean hasNext() {
            return curr != sentinel;
        }
    }

    class ListNode<T> {
        private T itemDate;
        private ListNode<T> preNode;
        private ListNode<T> nextNode;
        ListNode(){
            itemDate = null;
            preNode  = null;
            nextNode = null;
        }
        ListNode(T obj){
            itemDate = obj;
            preNode  = null;
            nextNode = null;
        }
        ListNode(T obj, ListNode<T> pre, ListNode<T> next) {
            itemDate = obj;
            preNode = pre;
            nextNode = next;
        }
        public T getItemData() {
            return itemDate;
        }
        public void setItemDate(T data) {
            itemDate = data;
        }
        public ListNode<T> getPreNode() {
            return preNode;
        }
        public void setPreNode(ListNode<T> pre) {
            preNode = pre;
        }
        public ListNode<T> getNextNode() {
            return nextNode;
        }
        public void setNextNode(ListNode<T> next) {
            nextNode = next;
        }
    }
}

