import javax.management.monitor.Monitor;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class FileServer implements Runnable {
    public static final int MAXSIZE = 65507;
    private static int port = 5999;
    private static String wdir = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "FileServer" + File.separator;
    private static DatagramSocket ds;
    private static ThreadQueue threadQueue = new ThreadQueue();
    private static final int anzahlThreads = 1;
    private static TaskQueue taskQueue = new TaskQueue();

    private int id;
    private DatagramPacket dp;
    private FileMonitor monitor = new FileMonitor();

    private FileServer(int id) {
        this.id = id;
    }

    private void setDp(DatagramPacket dp) {
        this.dp = dp;
    }

    private DatagramPacket getDp() {
        return this.dp;
    }

    public static void main(String[] args) throws Exception {
        String dpData = "";
        String answer = "***Error 900: unknown error";
        String filename = "";
        String newData = "";
        int lineNo = -1;

        MyFile f = null;
        String[] param = null;
        String[] param2 = null;
        //DatagramSocket ds = null;
        DatagramPacket dp = null;
        DatagramPacket dp2 = null;

        for (int i = 0; i < anzahlThreads; i++) {
            FileServer fileServer = new FileServer(i);
            Thread t = new Thread(fileServer);
            threadQueue.put(t);
            //t.start();
        }

        System.out.println(threadQueue);

        try {
            ds = new DatagramSocket(port);
            System.out.println("Server started on port: " + port);

            while (true) {
                try {
                    dp = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                    ds.receive(dp);
                    //dpData = new String(dp.getData(), 0, dp.getLength()).trim();
                    taskQueue.put(dp);

                    if (taskQueue.first != null) {
                        Thread t = threadQueue.get();
                        if (t != null) {
                            //fileServer.setDp(dp);
                            if (!t.isAlive()) {
                                try {
                                    t.start();
                                } catch (Exception e) {
                                    System.out.println("Thread mit ID " + t.getId() + " kann nicht gestartet werde, State: "
                                            + t.getState());
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    /*if (dpData.startsWith("READ")) {
                        try {
                            param = dpData.split(" ", 2);
                            param2 = param[1].split(",", 2);
                            filename = param2[0].trim();
                            lineNo = Integer.parseInt(param2[1].trim());
                            f = new MyFile(wdir + filename);
                            answer = f.read(lineNo);
                        } catch (Exception e) {
                            answer = "*** Error 901: bad READ COMMAND";
                            throw new Exception(e);
                        }
                    } else if (dpData.startsWith("WRITE")) {
                        try {
                            param = dpData.split(" ", 2);
                            param2 = param[1].split(",", 3);
                            filename = param2[0].trim();
                            lineNo = Integer.parseInt(param2[1].trim());
                            newData = param2[2];
                            f = new MyFile(wdir + filename);
                            answer = f.write(lineNo, newData);
                        } catch (Exception e) {
                            answer = "*** Error 901: bad WRITE COMMAND";
                            throw new Exception(e);
                        }
                    } else {
                        answer = "*** ERROR 902: unknown command";
                        throw new Exception("Unknown Command");
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*try {
                    dp2 = new DatagramPacket(answer.getBytes(), answer.getBytes().length, dp.getAddress(), dp.getPort());
                    ds.send(dp2);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        DatagramPacket dp = taskQueue.get();
        this.setDp(dp);
        String dpData = new String(dp.getData(), 0, dp.getLength()).trim();
        //threadQueue.get();
        System.out.println(this.id + " bearbeitet Task " + dpData);
        String answer = "***Error 900: unknown error";
        String filename = "";
        String newData = "";
        int lineNo = -1;

        MyFile f = null;
        String[] param = null;
        String[] param2 = null;
        DatagramPacket dp2 = null;

        if (dpData.startsWith("READ")) {
            try {
                monitor.startRead();
                Thread.sleep(5000);
                param = dpData.split(" ", 2);
                param2 = param[1].split(",", 2);
                filename = param2[0].trim();
                lineNo = Integer.parseInt(param2[1].trim());
                f = new MyFile(wdir + filename);
                answer = f.read(lineNo);
                monitor.endRead();
            } catch (Exception e) {
                answer = "*** Error 901: bad READ COMMAND";
                e.printStackTrace();
                monitor.endRead();
                //throw new Exception(e);
            }
        } else if (dpData.startsWith("WRITE")) {
            try {
                monitor.startWrite();
                param = dpData.split(" ", 2);
                param2 = param[1].split(",", 3);
                filename = param2[0].trim();
                lineNo = Integer.parseInt(param2[1].trim());
                newData = param2[2];
                f = new MyFile(wdir + filename);
                answer = f.write(lineNo, newData);
                monitor.endWrite();
            } catch (Exception e) {
                answer = "*** Error 901: bad WRITE COMMAND";
                monitor.endWrite();
                //throw new Exception(e);
            }
        } else {
            answer = "*** ERROR 902: unknown command";
            //throw new Exception("Unknown Command");
        }

        try {
            System.out.println(this.getDp().getAddress());
            dp2 = new DatagramPacket(answer.getBytes(), answer.getBytes().length, this.getDp().getAddress(), this.getDp().getPort());
            ds.send(dp2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (threadQueue.first == null && taskQueue.first != null) {
                //dp = taskQueue.get();
                //this.setDp(dp);
                this.run();
            } else {
                threadQueue.put(Thread.currentThread());
            }
        }
    }
}
