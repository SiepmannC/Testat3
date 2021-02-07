import java.net.DatagramPacket;
import java.util.concurrent.*;

public class Worker implements Runnable {

    int id;
    DatagramPacket dp;
    ThreadFactory threadFactory;

    public Worker(int id) {
        this.id = id;
    }

    private void setDp(DatagramPacket dp) {
        this.dp = dp;
    }

    private DatagramPacket getDp() {
        return this.dp;
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket dp = FileServer.taskQueue.get();
            if (dp != null) {
                this.setDp(dp);
                String dpData = new String(dp.getData(), 0, dp.getLength()).trim();
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
                        FileServer.monitor.startRead();
                        Thread.sleep(8000);
                        param = dpData.split(" ", 2);
                        param2 = param[1].split(",", 2);
                        filename = param2[0].trim();
                        lineNo = Integer.parseInt(param2[1].trim());
                        f = new MyFile(FileServer.wdir + filename);
                        answer = f.read(lineNo);
                        FileServer.monitor.endRead();
                    } catch (Exception e) {
                        answer = "*** Error 901: bad READ COMMAND";
                        e.printStackTrace();
                        FileServer.monitor.endRead();
                    }
                } else if (dpData.startsWith("WRITE")) {
                    try {
                        FileServer.monitor.startWrite();
                        Thread.sleep(12000);
                        param = dpData.split(" ", 2);
                        param2 = param[1].split(",", 3);
                        filename = param2[0].trim();
                        lineNo = Integer.parseInt(param2[1].trim());
                        newData = param2[2];
                        f = new MyFile(FileServer.wdir + filename);
                        answer = f.write(lineNo, newData);
                        FileServer.monitor.endWrite();
                    } catch (Exception e) {
                        answer = "*** Error 901: bad WRITE COMMAND";
                        FileServer.monitor.endWrite();
                    }
                } else {
                    answer = "*** ERROR 902: unknown command";
                }

                try {
                    dp2 = new DatagramPacket(answer.getBytes(), answer.getBytes().length, this.getDp().getAddress(), this.getDp().getPort());
                    FileServer.ds.send(dp2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            FileServer.threadQueue.put(Thread.currentThread());
        }
    }
}
