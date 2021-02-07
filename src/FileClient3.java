import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class FileClient3 {
	public final static int DEFAULT_PORT = 5999;
	public final static int MAX_PACKET_SIZE = 65507;
	
	public static void main (String[] args) {
		BufferedReader userIn = new BufferedReader (new InputStreamReader(System.in));
		String hostname = "localhost";
		int port = DEFAULT_PORT;
		String s;
		
		if(args.length > 1) {
			try {
				hostname = args[0];
				port = Integer.parseInt(args[1]);
				if( port < 1 || port > 65535) {
					throw new Exception();
				}
			}catch (Exception e){
				System.out.println("Usage: FileClient [hostname portno.]");
			}//try/catch
		}//if
		try {
			//create client datagramm socket
			DatagramSocket socket = new DatagramSocket();
			InetAddress host = InetAddress.getByName(hostname);
			System.out.println("Type command to send!");
			System.out.println("('READ file, lineNo'or' WRITE file,lineNo, data'or'x' to exit )");
			do {
				System.out.println("> ");
				s = userIn.readLine();
				if(s == null || s.equals("x")) break;
				byte[] data = s.getBytes();
				int len = data.length;
				if(len > MAX_PACKET_SIZE) {
					throw new Exception("Data too large to send");
				}
				DatagramPacket outPacket = new DatagramPacket (data, len, host, port);
				socket.send(outPacket);
				byte[] buffer = new byte[MAX_PACKET_SIZE];
				DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(inPacket);
				String answer = new String(inPacket.getData(), 0 , inPacket.getLength());
				System.out.println("Received Answer: "+answer);
			}while(true);
			
			socket.close();
				
				
			
				
			}//try
		catch (Exception e) {
			System.err.println(e);
		}
		
	}//main
}//class FileClient
