import java.net.DatagramPacket;

class TaskQueue {
    Node first;
    private FileMonitor fileMonitor = new FileMonitor(false);

    TaskQueue() {

    }

    void put(DatagramPacket s) {
        fileMonitor.startWrite();
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
        fileMonitor.endWrite();
    }

    DatagramPacket get() {
        fileMonitor.startRead();
        if (this.first != null) {
            Node current = this.first;
            this.first = this.first.next;
            fileMonitor.endRead();
            return current.content;
        }
        else {
            fileMonitor.endRead();
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
}