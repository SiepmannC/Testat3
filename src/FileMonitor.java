import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//Synchronisation nach dem Monitor Konzept
public class FileMonitor {

    // Diese Klasse beinhaltet 2 verschieden Lösungsvarienten
    // Die erste Lösung verwendet synchronized und wait/notify (Eigentliche Abgabe)
    // Die zweite dagegen ist mit ReentrantLock und Conditions implementiert worden (Zusatz)


    private int writerCount = 0; // Anzahl der Schreiber
    private int readerCount = 0; // Anzahl der Leser
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
        // Steuert die Ausgabe, keine Auswrikung auf Funktionalität
        start_read =this.mitAusgabe ?">>> START READ":"";
        end_read =this.mitAusgabe ?">>> END READ":"";
        start_write =this.mitAusgabe ?"*** START WRITE":"";
        end_write = this.mitAusgabe ? "*** END WRITE" : "";
    }

    // Die zweite Variante wurde auskommentiert

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
        writerCount--;
        isWriting = true;
        lock.unlock();
    }

    void endWrite() {
        lock.lock();
        System.out.println("*** END WRITE ***");
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

    public synchronized void startRead() { // Keyword synchronized ist wichtig für Monitore
        System.out.println(start_read);
        while (writerCount > 0 || writeRequests > 0) { // Reader warten, falls Writer schreiben bzw. schreiben wollen
            try {
                //System.out.println(">>> READ wartet");
                this.wait(); // Wartezustand
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readerCount++; // warten zu Ende, noch ein Reader fängt an zu lesen
    }

    public synchronized void endRead() {
        System.out.println(end_read);
        readerCount--; // Ein Reader weniger, welcher gerade liest
        this.notifyAll(); // Informiert andere, dass es das Lesen beendet hat
    }

    public synchronized void startWrite() {
        System.out.println(start_write);
        writeRequests++; // noch ein Schreiber möchte Lesen

        while (readerCount > 0 || writerCount > 0) { // Wartet bis alle Leser und Schreiber fertig sind
            try {
                //System.out.println("*** WRITE wartet");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writeRequests--; // Dieser Schreiber hat den Wartezustand verlassen
        writerCount++; // und kann mit dem Schreiben anfangen
    }

    public synchronized void endWrite() {
        System.out.println(end_write);
        writerCount--; // Schreiben wurde beendet
        notifyAll(); // informiert andere
    }
}
