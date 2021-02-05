import java.util.concurrent.Semaphore;

public class ThreadQueue {
    Node first;
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
        /*Z_Fifo_q q = new Z_Fifo_q();
        q.put("Hi");
        q.put("Ahh");
        System.out.println(q.get());
        System.out.println(q.get());*/

    }
}