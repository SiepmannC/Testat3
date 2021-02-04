import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class FileServer {
    public static final int MAXSIZE = 65507;
    private static int port = 5999;
    private static String wdir = System.getProperty("user.home") + File.pathSeparator + "Desktop" + File.separator + "FileServer";

    public static void main(String[] args) throws Exception {
        String dpData = "";
        String answer = "***Error 900: unknown error";
        String filename = "";
        String newData = "";
        int lineNo = -1;

        MyFile f = null;
        String param[] = null;
        String param2[] = null;
        DatagramSocket ds = null;
        DatagramPacket dp = null;
        DatagramPacket dp2 = null;

        try {
            ds = new DatagramSocket(port);
            System.out.println("Server started on port: " + port);

            while (true) {
                try {
                    dp = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                    ds.receive(dp);
                    dpData = new String(dp.getData(), 0, dp.getLength()).trim();
                    if (dpData.startsWith("READ")) {
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
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    dp2 = new DatagramPacket(answer.getBytes(), answer.getBytes().length, dp.getAddress(), dp.getPort());
                    ds.send(dp2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
