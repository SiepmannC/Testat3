import javax.management.monitor.Monitor;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class FileServer {
    public static final int MAXSIZE = 65507;
    private static int port = 5999;
    // Speichert Filme im Desktop unter dem Ordner FileServer, der im Voraus mit Dateien erstellt werden muss
    static String wdir = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "FileServer" + File.separator;
    static DatagramSocket ds;
    private static final int anzahlThreads = 3; // Anzahl der Max. Threads im Pool
    static ThreadQueue threadQueue = new ThreadQueue(anzahlThreads); // Thread Pool
    static TaskQueue taskQueue = new TaskQueue(); // Threadsicher, kann auch analog zu Thread Pool, bounded sein
    static FileMonitor monitor = new FileMonitor(true); // Monitor zur Synchronisation

    public static void main(String[] args) {
        DatagramPacket dp = null;

        // Init. der Threads
        for (int i = 0; i < anzahlThreads; i++) {
            Worker target = new Worker(i);
            Thread thread = new Thread(target);
            threadQueue.put(thread);
            System.out.println(target.id + "Thread in Queue");
        }

        System.out.println(threadQueue);

        try {
            ds = new DatagramSocket(port);
            System.out.println("Server started on port: " + port);

            while (true) {
                try {
                    dp = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                    ds.receive(dp);
                    taskQueue.put(dp);
                    // Starten der Threads
                    if (taskQueue.first != null) {
                        Thread t = threadQueue.get();
                        if (t != null) {
                            if (!t.isAlive() && !t.getState().equals(Thread.State.TERMINATED)) {
                                try {
                                    t.start();
                                } catch (Exception e) {
                                    System.out.println("Thread mit ID " + t.getId() + " kann nicht gestartet werden, State: "
                                            + t.getState());
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            System.out.println("Thread Queue leer");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
