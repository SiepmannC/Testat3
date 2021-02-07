import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileMonitor {

    private int writeCount = 0;
    private int readerCount = 0;
    private boolean isWriting = false;
    private static Lock lock = new ReentrantLock();
    private static Condition reader = lock.newCondition();
    private static Condition writer = lock.newCondition();

    /*void startRead() {
        lock.lock();
        System.out.println("*** START READ ***");
        while (writeCount > 0 || isWriting) {
            try {
                reader.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reader.signalAll();
                //lock.unlock();
            }
        }
        /*else {
            try {
                reader.signal();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
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
            if (writeCount > 0) {
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
        writeCount++;

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
        } /* else {
            isWriting = true;
            lock.unlock();
        }
        //writer.signal();

        isWriting = true;
        lock.unlock();
    }

    void endWrite() {
        lock.lock();
        System.out.println("*** END WRITE ***");
        writeCount--;
        isWriting = false;
        try {
            if (writeCount > 0) {
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

    private int readers       = 0;
    private int writers       = 0;
    private int writeRequests = 0;

    public synchronized void startRead(){
        System.out.println(">>> START READ");
        while(writers > 0 || writeRequests > 0){
            try {
                System.out.println(">>> READ wartet");
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readers++;
    }

    public synchronized void endRead(){
        System.out.println(">>> END READ");
        readers--;
        this.notifyAll();
    }

    public synchronized void startWrite(){
        System.out.println("*** START WRITE");
        writeRequests++;

        while(readers > 0 || writers > 0){
            try {
                System.out.println("*** WRITE wartet");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writeRequests--;
        writers++;
    }

    public synchronized void endWrite(){
        System.out.println("*** END WRITE");
        writers--;
        notifyAll();
    }
}
