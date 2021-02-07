import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileMonitor {

    // Diese Klasse beinhaltet 2 verschieden Lösungsvarienten
    // Die erste Lösung ist mit ReentrantLock und Conditions implemntiert worden
    // Die zweite dagegen verwendet synchronized und wait/notify

    private int writerCount = 0;
    private int readerCount = 0;
    private boolean isWriting = false;
    private static Lock lock = new ReentrantLock();
    private static Condition reader = lock.newCondition();
    private static Condition writer = lock.newCondition();
    private boolean mitAusgabe;
    private String start_read;
    private String end_read;
    private String start_write;
    private String end_write;

    public FileMonitor(boolean mitAusgabe) {
        this.mitAusgabe = mitAusgabe;
        start_read =this.mitAusgabe ?">>> START READ":"";
        end_read =this.mitAusgabe ?">>> END READ":"";
        start_write =this.mitAusgabe ?"*** START WRITE":"";
        end_write = this.mitAusgabe ? "*** END WRITE" : "";
    }

    /*void startRead() {
        lock.lock();
        System.out.println("*** START READ ***");
        while (writerCount > 0 || isWriting) {
            try {
                reader.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reader.signalAll();
                //lock.unlock();
            }
        }

        writer.signalAll(); //gibt nicht bescheid
        reader.signalAll();
        lock.unlock();
    }

    void endRead() {
        lock.lock();
        System.out.println("*** END READ ***");
        try {
            if (writerCount > 0) {
                writer.signalAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void startWrite() {
        lock.lock();
        System.out.println("*** START WRITE***");
        writerCount++;

        while (isWriting) {
            try {
                writer.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //writer.notify();
                //isWriting = true;
                //lock.unlock();
            }
        }
        //writer.signal();

        isWriting = true;
        lock.unlock();
    }

    void endWrite() {
        lock.lock();
        System.out.println("*** END WRITE ***");
        writerCount--;
        isWriting = false;
        try {
            if (writerCount > 0) {
                writer.signalAll();
            } else {
                reader.signalAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }*/
    private int writeRequests = 0;

    public synchronized void startRead() {
        System.out.println(start_read);
        while (writerCount > 0 || writeRequests > 0) {
            try {
                //System.out.println(">>> READ wartet");
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readerCount++;
    }

    public synchronized void endRead() {
        System.out.println(end_read);
        readerCount--;
        this.notifyAll();
    }

    public synchronized void startWrite() {
        System.out.println(start_write);
        writeRequests++;

        while (readerCount > 0 || writerCount > 0) {
            try {
                //System.out.println("*** WRITE wartet");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writeRequests--;
        writerCount++;
    }

    public synchronized void endWrite() {
        System.out.println(end_write);
        writerCount--;
        notifyAll();
    }
}
