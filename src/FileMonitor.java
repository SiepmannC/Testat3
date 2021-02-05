import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileMonitor {

    private int writeCount = 0;
    private boolean isWriting = false;
    private Lock lock = new ReentrantLock();
    private Condition reader = lock.newCondition();
    private Condition writer = lock.newCondition();


    void startRead() {
        lock.lock();
        System.out.println("Start read aufgerufen");
        if (writeCount > 0 || isWriting) {
            try {
                reader.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reader.signalAll();
                lock.unlock();
            }
        } else {
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
    }

    void endRead() {
        lock.lock();
        try {
            if (writeCount > 0) {
                writer.signal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void startWrite() {
        lock.lock();
        writeCount++;
        if (isWriting) {
            try {
                writer.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //writer.notify();
                isWriting = true;
                lock.unlock();
            }
        } else {
            isWriting = true;
            lock.unlock();
        }
    }

    void endWrite() {
        lock.lock();
        writeCount--;
        isWriting = false;
        if (writeCount > 0) writer.signal();
        else reader.signal();
        lock.unlock();
    }
}
