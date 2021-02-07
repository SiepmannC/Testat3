import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ThreadQueue {
    private List queue = new LinkedList<Thread>();
    private int limit = 10;

    ThreadQueue(int limit) {
        this.limit = limit;
    }


    synchronized void put(Thread thread) {
        while (this.queue.size() == this.limit) {
            try {
                wait(); // Bounded, daher muss gewartet werden
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
                wait(); // Leer, muss daher gewartet werden
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