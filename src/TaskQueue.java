import java.net.DatagramPacket;
import java.util.concurrent.Semaphore;

public class TaskQueue {
    Node first;
    Semaphore mutex =  new Semaphore(1);

    public TaskQueue() {

    }

    public void put(DatagramPacket s) {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Node newNode = new Node(s);

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

    public DatagramPacket get() {
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
        DatagramPacket content;
        Node(DatagramPacket t) {
            this.content = t;
            this.next = null;
        }

    }

    public static void main(DatagramPacket[] args) {
        /*Z_Fifo_q q = new Z_Fifo_q();
        q.put("Hi");
        q.put("Ahh");
        System.out.println(q.get());
        System.out.println(q.get());*/

    }
}