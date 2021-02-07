import java.util.LinkedList;
import java.util.List;

public class ThreadQueue {
    /*Node first;
    Semaphore mutex =  new Semaphore(1);

    public ThreadQueue() {

    }

    public void put(Thread Thread) {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Node newNode = new Node(Thread);

        if(this.first == null) {
            this.first = newNode;
        }
        else {
            Node last = this.first;
            while(last.next!=null) {
                last = last.next;
            }
            last.next = newNode;
        }
        mutex.release();
    }

    public Thread get() {
        if (this.first != null) {
            Node current = this.first;
            this.first = this.first.next;
            return current.content;
        }
        else {
            return null;
        }
    }

    class Node {
        Node next;
        Thread content;
        Node(Thread t) {
            this.content = t;
            this.next = null;
        }

    }

    public static void main(Thread[] args) {


    }*/

    private List queue = new LinkedList<Thread>();
    private int limit = 10;

    ThreadQueue(int limit) {
        this.limit = limit;
    }


    synchronized void put(Thread thread) {
        while (this.queue.size() == this.limit) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.queue.add(thread);
        if (this.queue.size() == 1) {
            notifyAll();
        }
    }


     synchronized Thread get() {
        while (this.queue.size() == 0) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.queue.size() == this.limit) {
            notifyAll();
        }

        return (Thread) this.queue.remove(0);
    }
}